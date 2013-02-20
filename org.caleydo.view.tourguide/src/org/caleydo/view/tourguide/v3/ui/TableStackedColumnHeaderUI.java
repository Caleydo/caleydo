/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.v3.ui;

import static org.caleydo.view.tourguide.v3.ui.TableHeaderUI.COLUMN_SPACE;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.view.tourguide.v3.model.ACompositeRankColumnModel;
import org.caleydo.view.tourguide.v3.model.ARankColumnModel;
import org.caleydo.view.tourguide.v3.model.StackedRankColumnModel;
import org.caleydo.view.tourguide.v3.ui.SeparatorUI.IMoveHereChecker;
/**
 * @author Samuel Gratzl
 *
 */
public class TableStackedColumnHeaderUI extends GLElementContainer implements IGLLayout, IMoveHereChecker {
	private final static int FIRST_COLUMN = 1;
	private StackedRankColumnModel model;
	private int numColumns = 0;

	public final AlignmentDragInfo align = new AlignmentDragInfo();

	private final PropertyChangeListener childrenChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onChildrenChanged((IndexedPropertyChangeEvent) evt);
		}
	};

	public TableStackedColumnHeaderUI(StackedRankColumnModel model) {
		this.model = model;
		setLayout(this);
		setLayoutData(model);
		this.add(model.createSummary());
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		for (ARankColumnModel col : model) {
			this.add(wrap(col));
			numColumns++;
		}
		this.add(new StackedSeparatorUI(this, -1)); // left
		for (int i = 0; i < numColumns; ++i)
			this.add(new StackedSeparatorUI(this, i));
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) {
			// moved
			int movedFrom = (Integer) evt.getOldValue();
			this.add(index + FIRST_COLUMN, get(movedFrom + FIRST_COLUMN));
		} else if (evt.getOldValue() == null) { // added
			Collection<TableColumnHeaderUI> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			numColumns += news.size();
			asList().addAll(index + FIRST_COLUMN, news);
			for (int i = 0; i < news.size(); ++i)
				add(new StackedSeparatorUI(this));
		} else if (evt.getNewValue() == null) { //removed
			remove(index + FIRST_COLUMN);
			numColumns--;
			remove(this.size() - 1); // remove last separator
		} else { //replaced
			this.set(index + FIRST_COLUMN, new TableColumnHeaderUI((ARankColumnModel) evt.getNewValue(), true));
		}
	}

	/**
	 * @param newValue
	 * @return
	 */
	private TableColumnHeaderUI wrap(ARankColumnModel model) {
		return new TableColumnHeaderUI(model, true);
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		super.takeDown();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement summary = children.get(0);
		summary.setBounds(3, 0, w - 6, 40);

		List<? extends IGLLayoutElement> columns = children.subList(1, numColumns+1);
		List<? extends IGLLayoutElement> separators = children.subList(numColumns + 2, children.size());
		assert separators.size() == columns.size();

		final IGLLayoutElement sep0 = children.get(numColumns + 1);
		sep0.setBounds(3, 40, COLUMN_SPACE, h - 40); // left separator
		((StackedSeparatorUI) sep0.asElement()).setAlignment(this.model.getAlignment() == 0);
		// align the columns normally
		float x = COLUMN_SPACE + 3;
		for (int i = 0; i < columns.size(); ++i) {
			IGLLayoutElement col = columns.get(i);
			IGLLayoutElement sep = separators.get(i);
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			col.setBounds(x, 40, model.getPreferredWidth(), h - 40);
			x += model.getPreferredWidth() + COLUMN_SPACE;
			sep.setBounds(x - COLUMN_SPACE, 40, COLUMN_SPACE, h - 40);
			((SeparatorUI) sep.asElement()).setIndex(i);
			((StackedSeparatorUI) sep.asElement()).setAlignment(this.model.getAlignment() == (i + 1));
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(model.getBgColor()).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	@Override
	public boolean canMoveHere(int index, ARankColumnModel model) {
		return this.model.isMoveAble(model, index + 1);
	}

	@Override
	public void moveHere(int index, ARankColumnModel model) {
		assert canMoveHere(index, model);
		this.model.move(model, index + 1);
	}

	public static class AlignmentDragInfo implements IDragInfo {

	}

	public void setAlignment(int index) {
		model.setAlignment(index + 1);
		relayout();
	}

}

