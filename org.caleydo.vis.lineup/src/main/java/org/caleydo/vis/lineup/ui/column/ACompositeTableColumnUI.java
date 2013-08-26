/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;


import gleem.linalg.Vec4f;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.ui.RenderStyle;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACompositeTableColumnUI<T extends ACompositeRankColumnModel> extends GLElementContainer implements
		IGLLayout, IColumModelLayout, ITableColumnUI {

	protected final T model;
	private final int firstColumn;
	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case ACompositeRankColumnModel.PROP_CHILDREN:
				onChildrenChanged((IndexedPropertyChangeEvent) evt);
				break;
			case ACompositeRankColumnModel.PROP_CHILDREN_ORDER:
				onChildrenOrderChanged();
				break;
			}
		}
	};


	public ACompositeTableColumnUI(T model, int firstColumn) {
		this.model = model;
		this.firstColumn = firstColumn;
		setLayout(this);
		setLayoutData(model);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, listener);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN_ORDER, listener);
		for (ARankColumnModel col2 : model) {
			this.add(wrap(col2));
		}
	}

	/**
	 * change order of children
	 */
	protected void onChildrenOrderChanged() {
		final List<GLElement> keep = new ArrayList<>(asList().subList(0, firstColumn));
		sortBy(new Comparator<GLElement>() {
			@Override
			public int compare(GLElement o1, GLElement o2) {
				int i1 = keep.indexOf(o1);
				int i2 = keep.indexOf(o2);
				if (i1 == -1 && i2 == -1) {
					// check model
					return model.indexOf(o1.getLayoutDataAs(ARankColumnModel.class, null))
							- model.indexOf(o2.getLayoutDataAs(ARankColumnModel.class, null));
				} else if (i1 == -1)
					return 1;
				else if (i2 == -1)
					return -1;
				else
					return i1 - i2;
			}
		});
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
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN_ORDER, listener);
		super.takeDown();
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) {
			// moved
			int movedFrom = (Integer) evt.getOldValue();
			this.add(firstColumn + index, get(firstColumn + movedFrom));
		} else if (evt.getOldValue() == null) { // added
			Collection<GLElement> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			asList().addAll(firstColumn + index, news);
		} else if (evt.getNewValue() == null) {// removed
			remove(firstColumn + index);
		} else { // replaced
			set(firstColumn + index, wrap((ARankColumnModel) evt.getNewValue()));
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
		int i = 0;
		for (IGLLayoutElement col : children.subList(firstColumn, children.size())) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			float wi = getChildWidth(i++, model);
			col.setBounds(x, 0, wi, h);
			x += wi + RenderStyle.COLUMN_SPACE;
		}
	}

	protected abstract float getChildWidth(int i, ARankColumnModel model);

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
	}

	protected float getLeftPadding() {
		return RenderStyle.COLUMN_SPACE;
	}

	@Override
	public void relayout() {
		for (ITableColumnUI g : Iterables.filter(this, ITableColumnUI.class))
			g.relayout();
		super.relayout();
	}

	public ITableColumnUI getLastChild() {
		for (int i = this.size() - 1; i >= 0; --i) {
			GLElement c = get(i);
			if (!GLElement.areValidBounds(c.getBounds()))
				continue;
			if (c instanceof ITableColumnUI)
				return (ITableColumnUI) get(i);
		}
		return null;
	}

	@Override
	public Vec4f getBounds(int rowIndex) {
		return null;
	}
}

