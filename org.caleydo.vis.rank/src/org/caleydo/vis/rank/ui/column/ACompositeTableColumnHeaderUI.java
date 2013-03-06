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
package org.caleydo.vis.rank.ui.column;

import static org.caleydo.vis.rank.ui.RenderStyle.COLUMN_SPACE;
import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.SeparatorUI;
import org.caleydo.vis.rank.ui.SeparatorUI.IMoveHereChecker;
import org.caleydo.vis.rank.ui.StackedSeparatorUI;
/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACompositeTableColumnHeaderUI<T extends ACompositeRankColumnModel> extends GLElementContainer
		implements
		IGLLayout, IMoveHereChecker {
	private static final int SUMMARY = 0;
	private final static int FIRST_COLUMN = 1;
	protected T model;
	protected int numColumns = 0;

	private final PropertyChangeListener childrenChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onChildrenChanged((IndexedPropertyChangeEvent) evt);
		}
	};

	protected final boolean interactive;

	public ACompositeTableColumnHeaderUI(T model, boolean interactive) {
		this.model = model;
		this.interactive = interactive;
		setLayout(this);
		setLayoutData(model);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		for (ARankColumnModel col : model) {
			this.add(wrap(col));
			numColumns++;
		}
		if (interactive) {
			this.add(createSeparator(0)); // left
			for (int i = 0; i < numColumns; ++i)
				this.add(createSeparator(i + 1));
		}
	}

	protected SeparatorUI createSeparator(int index) {
		return new StackedSeparatorUI(this, 0);
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) {
			// moved
			int movedFrom = (Integer) evt.getOldValue();
			this.add(index + FIRST_COLUMN, get(movedFrom + FIRST_COLUMN));
		} else if (evt.getOldValue() == null) { // added
			Collection<GLElement> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			numColumns += news.size();
			asList().addAll(index + FIRST_COLUMN, news);
			if (interactive) {
				for (int i = 0; i < news.size(); ++i)
					add(createSeparator(0));
			}
		} else if (evt.getNewValue() == null) { //removed
			remove(index + FIRST_COLUMN);
			numColumns--;
			if (interactive)
				remove(this.size() - 1); // remove last separator
		} else { //replaced
			this.set(index + FIRST_COLUMN, wrap((ARankColumnModel) evt.getNewValue()));
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (model.isCollapsed()) { // just the summary
			g.incZ();
			get(SUMMARY).render(g);
			g.decZ();
		} else
			super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (model.isCollapsed()) { // just the summary
			g.incZ();
			get(SUMMARY).renderPick(g);
			g.decZ();
		} else
			super.renderPickImpl(g, w, h);
	}

	protected abstract GLElement wrap(ARankColumnModel model);

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		super.takeDown();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		float offset = LABEL_HEIGHT + HIST_HEIGHT;
		List<? extends IGLLayoutElement> columns = children.subList(1, numColumns+1);
		List<? extends IGLLayoutElement> separators = null;

		if (interactive) {
			separators = children.subList(numColumns + 2, children.size());
			assert separators.size() == columns.size();
			final IGLLayoutElement sep0 = children.get(numColumns + 1);
			sep0.setBounds(getLeftPadding() + 3, offset, COLUMN_SPACE, h - offset); // left separator
		}
		// align the columns normally
		float x = getLeftPadding();
		for (int i = 0; i < columns.size(); ++i) {
			IGLLayoutElement col = columns.get(i);
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			col.setBounds(x, offset, model.getPreferredWidth(), h - offset);
			x += model.getPreferredWidth() + COLUMN_SPACE;
			if (interactive && separators != null) {
				if (separators.size() <= i)
					System.err.println();
				IGLLayoutElement sep = separators.get(i);
				sep.setBounds(x - COLUMN_SPACE, offset, COLUMN_SPACE, h - offset);
				((SeparatorUI) sep.asElement()).setIndex(i + 1);
			}
		}
	}

	protected float getLeftPadding() {
		return RenderStyle.COLUMN_SPACE;
	}

	@Override
	public boolean canMoveHere(int index, ARankColumnModel model) {
		return this.model.isMoveAble(model, index);
	}

	@Override
	public void moveHere(int index, ARankColumnModel model) {
		assert canMoveHere(index, model);
		this.model.move(model, index);
	}
}

