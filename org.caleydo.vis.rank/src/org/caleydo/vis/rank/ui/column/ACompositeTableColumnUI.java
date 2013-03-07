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
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.ui.RenderStyle;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACompositeTableColumnUI<T extends ACompositeRankColumnModel> extends GLElementContainer implements
		IGLLayout, IColumModelLayout, ITableColumnUI {

	protected final T model;
	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case ACompositeRankColumnModel.PROP_CHILDREN:
				onChildrenChanged((IndexedPropertyChangeEvent) evt);
				break;
			}
		}
	};


	public ACompositeTableColumnUI(T model) {
		this.model = model;
		setLayout(this);
		setLayoutData(model);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, listener);
		for (ARankColumnModel col2 : model) {
			this.add(wrap(col2));
		}
	}

	@Override
	public GLElement asGLElement() {
		return this;
	}

	/**
	 * @return the model, see {@link #model}
	 */
	@Override
	public T getModel() {
		return model;
	}

	protected IColumModelLayout getColumnModelParent() {
		return (IColumModelLayout) getParent();
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, listener);
		super.takeDown();
	}

	@SuppressWarnings("unchecked")
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
		relayoutChildren();
		relayout();
		repaint();
	}

	protected void relayoutChildren() {
		for (GLElement c : this)
			c.relayout();
	}

	protected abstract GLElement wrap(ARankColumnModel model);

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		float x = getLeftPadding();
		for (IGLLayoutElement col : children) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			col.setBounds(x, 0, model.getPreferredWidth(), h);
			x += model.getPreferredWidth() + RenderStyle.COLUMN_SPACE;
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.decZ().decZ().decZ();
		g.color(model.getBgColor()).fillRect(0, 0, w, h);
		g.incZ().incZ().incZ();
		// float x = get(stacked.getAlignment()).getLocation().x();
		// g.color(Color.BLUE).drawLine(x, 0, x, h);
		if (!model.isCollapsed())
			super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (!model.isCollapsed())
			super.renderPickImpl(g, w, h);
	}

	protected float getLeftPadding() {
		return RenderStyle.COLUMN_SPACE;
	}

	@Override
	public GLElement setData(Iterable<IRow> data, IColumModelLayout parent) {
		for (GLElement col : this)
			((ColumnUI) col).setData(data, this);
		return this;
	}

	@Override
	public void update() {
		for (ITableColumnUI g : Iterables.filter(this, ITableColumnUI.class))
			g.update();
		relayout();
	}
}

