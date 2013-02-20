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

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.view.tourguide.v3.model.ACompositeRankColumnModel;
import org.caleydo.view.tourguide.v3.model.ARankColumnModel;
import org.caleydo.view.tourguide.v3.model.StackedRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class TableStackedColumnHeaderUI extends GLElementContainer implements IGLLayout{

	private StackedRankColumnModel model;

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
			this.add(new TableColumnHeaderUI(col, true));
		}
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) {
			// moved
			int movedFrom = (Integer) evt.getOldValue();
			this.add(index + 1, get(movedFrom));
		} else if (evt.getOldValue() == null) { // added
			Collection<TableColumnHeaderUI> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			asList().addAll(index + 1, news);
		} else if (evt.getNewValue() == null) { //removed
			remove(index + 1);
		} else { //replaced
			this.set(index + 1, new TableColumnHeaderUI((ARankColumnModel) evt.getNewValue(), true));
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
		summary.setBounds(0, 0, w, 40);
		// align the columns normally
		float x = TableHeaderUI.COLUMN_SPACE;
		for (IGLLayoutElement col : children.subList(1, children.size())) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			col.setBounds(x, 40, model.getPreferredWidth(), h - 40);
			x += model.getPreferredWidth() + TableHeaderUI.COLUMN_SPACE;
		}
	}
}

