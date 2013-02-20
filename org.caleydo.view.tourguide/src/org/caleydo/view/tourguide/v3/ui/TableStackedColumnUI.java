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
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.view.tourguide.v3.model.ACompositeRankColumnModel;
import org.caleydo.view.tourguide.v3.model.ARankColumnModel;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.StackedRankColumnModel;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin.MultiFloat;

/**
 * @author Samuel Gratzl
 *
 */
public class TableStackedColumnUI extends GLElementContainer implements IGLLayout, IColumModelLayout {

	private StackedRankColumnModel stacked;

	private final PropertyChangeListener childrenChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onChildrenChanged((IndexedPropertyChangeEvent) evt);
		}
	};

	public TableStackedColumnUI(StackedRankColumnModel model) {
		this.stacked = model;
		setLayout(this);
		setLayoutData(model);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		for (ARankColumnModel col2 : model) {
			TableColumnUI ui = new TableColumnUI(col2);
			ui.setData(model.getTable().getData());
			this.add(ui);
		}
	}

	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) {
			// moved
			int movedFrom = (Integer) evt.getOldValue();
			this.add(index, get(movedFrom));
		} else if (evt.getOldValue() == null) { // added
			Collection<GLElement> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			asList().addAll(index, news);
		} else if (evt.getNewValue() == null) {// removed
			remove(index);
		} else { // replaced
			set(index, wrap((ARankColumnModel) evt.getNewValue()));
		}
	}

	private GLElement wrap(ARankColumnModel model) {
		TableColumnUI ui = new TableColumnUI(model);
		ui.setData(model.getTable().getData());
		return ui;
	}

	@Override
	protected void takeDown() {
		stacked.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		g.color(stacked.getBgColor()).fillRect(0, 0, w, h);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		float x = TableHeaderUI.COLUMN_SPACE;
		for (IGLLayoutElement col : children) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			col.setBounds(x, 0, model.getPreferredWidth(), h);
			x += model.getPreferredWidth() + TableHeaderUI.COLUMN_SPACE;
		}
	}

	@Override
	public void layoutRows(ARankColumnModel model, List<? extends IGLLayoutElement> children, float w, float h) {
		int combinedAlign = stacked.getAlignment();
		int combinedIndex = stacked.indexOf(model);
		if (combinedIndex != combinedAlign) {
			// moving around
			int[] ranks = stacked.getTable().getOrder();
			float[] rowPositions = ((TableBodyUI) getParent()).getRowPositions();
			float[] weights = new float[stacked.size()];
			for (int i = 0; i < weights.length; ++i)
				weights[i] = stacked.get(i).getWeight();

			float y = 0;
			BitSet used = new BitSet(children.size());
			used.set(0, children.size());
			int ri = 0;
			for (float hr : rowPositions) {
				int r = ranks[ri++];
				IGLLayoutElement row = children.get(r);
				used.clear(r);
				IRow data = row.getLayoutDataAs(IRow.class, null);
				float x = 0;
				MultiFloat vs = stacked.getSplittedValue(data);
				if (combinedIndex < combinedAlign) {
					for (int i = 0; i <= combinedIndex; ++i)
						x -= vs.values[i] + weights[i];
				} else {
					for (int i = combinedIndex; i < vs.size(); ++i)
						x += vs.values[i] - weights[i];
				}
				row.setBounds(x, y, w, hr - y);
				y = hr;
			}
			for (int unused = used.nextSetBit(0); unused >= 0; unused = used.nextSetBit(unused + 1)) {
				children.get(unused).setBounds(0, h, w, 0);
			}
		} else {
			// simple
			((TableBodyUI) getParent()).layoutRows(model, children, w, h);
		}
	}

	/**
	 * @param data
	 */
	public void setData(Collection<IRow> data) {
		for (GLElement col : this)
			((TableColumnUI) col).setData(data);
	}

	/**
	 *
	 */
	public void update() {
		for (GLElement g : this)
			g.relayout();
	}
}

