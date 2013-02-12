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
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class MouseLayer extends GLElementContainer implements IMouseLayer, IGLLayout {
	private static final IDragInfo DUMMY_DRAG_INFO = new IDragInfo() {
	};
	// marker for a tooltip element
	private static final Object TOOLTIP = new Object();

	public MouseLayer() {
		super();
		setLayout(this);
	}

	@Override
	public boolean doLayout(List<IGLLayoutElement> children, float w, float h) {
		for (IGLLayoutElement child : children) {
			child.setBounds(defaultValue(child.getSetX(), 0), defaultValue(child.getSetY(), 0),
					defaultValue(child.getSetWidth(), w), defaultValue(child.getSetHeight(), h));
		}
		return false;
	}

	@Override
	public void addDraggable(GLElement element) {
		this.addDraggable(element, DUMMY_DRAG_INFO);
	}

	@Override
	public void addDraggable(GLElement element, IDragInfo info) {
		this.add(element, info);
	}

	@Override
	public boolean hasDraggables() {
		return hasDraggable(IDragInfo.class);
	}

	@Override
	public boolean hasDraggable(Class<? extends IDragInfo> type) {
		return getFirstDragable(type) != null;
	}

	@Override
	public <T extends IDragInfo> Pair<GLElement, T> getFirstDragable(Class<T> type) {
		for (GLElement child : this)
			if (type.isInstance(child.getLayoutData()))
				return Pair.make(child, type.cast(child.getLayoutData()));
		return null;
	}

	@Override
	public <T extends IDragInfo> List<Pair<GLElement, T>> getDragables(Class<T> type) {
		List<Pair<GLElement, T>> r = new ArrayList<>(1);
		for (GLElement child : this)
			if (type.isInstance(child.getLayoutData()))
				r.add(Pair.make(child, type.cast(child.getLayoutData())));
		return r;
	}

	@Override
	public boolean removeDraggable(GLElement element) {
		return this.remove(element);
	}

	@Override
	public void setToolTip(GLElement element) {
		for (ListIterator<GLElement> it = asList().listIterator(); it.hasNext();) {
			if(it.next().getLayoutData() == TOOLTIP) {
				it.set(element);
				return;
			}
		}
		this.add(element, TOOLTIP);
	}

	@Override
	public void setToolTip(String text) {
		this.setToolTip(new TooltipElement(text));
	}

	@Override
	public boolean clearToolTip() {
		for (ListIterator<GLElement> it = asList().listIterator(); it.hasNext();) {
			if (it.next().getLayoutData() == TOOLTIP) {
				it.remove();
				return true;
			}
		}
		return false;
	}
}
