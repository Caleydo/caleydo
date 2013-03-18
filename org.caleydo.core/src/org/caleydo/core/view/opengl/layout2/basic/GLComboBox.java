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
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * a simple basic widget for a combo box
 *
 * @author Samuel Gratzl
 *
 */
public class GLComboBox<T> extends AGLButton {
	/**
	 * items of this combo box
	 */
	private final List<T> model;

	/**
	 * is the drop down currently visible
	 */
	private boolean isOpen = false;

	/**
	 * the current selected index
	 */
	private int selected = -1;
	/**
	 * currently hovered index
	 */
	private int hoveredIndex = -1;

	/**
	 * helper for transporting the model value to the {@link #valueRenderer}
	 */
	private int actRenderIndex = -1;

	/**
	 * renderer to render a specific model item
	 *
	 * the item can be retrieved by {@link GLElement#getLayoutDataAs(Class, Object)}
	 */
	private final IGLRenderer valueRenderer;

	/**
	 * renderer to render the background of the item list
	 */
	private final IGLRenderer listRenderer;

	/**
	 * z delta to use for rendering the drop down list
	 */
	private float zDeltaList = 0.25f;

	private ISelectionCallback<? super T> callback = DUMMY_CALLBACK;

	public GLComboBox(List<T> model, IGLRenderer valueRenderer, IGLRenderer listRenderer) {
		this.valueRenderer = valueRenderer;
		this.listRenderer = listRenderer;
		setPicker(null);
		this.model = model;
	}

	protected void onSelectionChanged(T newItem) {
		callback.onSelectionChanged(this, newItem);
	}

	/**
	 * @param zDeltaList
	 *            setter, see {@link zDeltaList}
	 */
	public void setzDeltaList(float zDeltaList) {
		if (this.zDeltaList == zDeltaList)
			return;
		this.zDeltaList = zDeltaList;
		if (isOpen)
			repaintAll();
	}

	/**
	 * @param callback
	 *            setter, see {@link callback}
	 */
	public final GLComboBox<T> setCallback(ISelectionCallback<? super T> callback) {
		if (callback == null)
			callback = DUMMY_CALLBACK;
		if (this.callback == callback)
			return this;
		this.callback = callback;
		return this;
	}

	public void setSelected(int index) {
		if (selected == index)
			return;
		this.selected = index;
		onSelectionChanged(getSelectedItem());
		repaint();
	}

	public void setSelectedItem(T item) {
		setSelected(model.indexOf(item));
	}

	/**
	 * currently selected item
	 *
	 * @return
	 */
	public T getSelectedItem() {
		if (selected < 0)
			return null;
		return model.get(selected);
	}

	@Override
	public <U> U getLayoutDataAs(Class<U> clazz, U default_) {
		// do we render currently a item of the model else return the currently selected item
		T item = actRenderIndex >= 0 ? model.get(actRenderIndex) : getSelectedItem();
		if (clazz.isInstance(item))
			return clazz.cast(item);
		return super.getLayoutDataAs(clazz, default_);
	}

	public int getSelected() {
		return selected;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (isOpen) { // render the drop down
			g.incZ(zDeltaList);
			g.move(0, h);
			listRenderer.render(g, w, h * model.size(), this);
			g.move(0, -h);
			g.color(Color.DARK_GRAY).drawLine(0, h, w, h).drawRect(0, 0, w, h * model.size() + h);
			// render all items
			for (int i = 0; i < model.size(); ++i) {
				actRenderIndex = i;
				g.move(0, h);
				valueRenderer.render(g, w, h, this);
			}
			g.move(0, -h * model.size());
			actRenderIndex = -1;
			g.incZ(-zDeltaList);
		} else {
			// render a small triangle as drop down indicator
			float hi = h * 0.5f;
			g.color(Color.LIGHT_GRAY).fillPolygon(new Vec2f(w - hi - 1, (h - hi) * 0.5f),
					new Vec2f(w - 1, (h - hi) * 0.5f), new Vec2f(w - hi * 0.5f - 1, hi + (h - hi) * 0.5f));
		}
		if (selected >= 0 && !isOpen) {
			// render the currently selected value
			valueRenderer.render(g, w, h, this);
		} else {
			super.renderImpl(g, w, h);
		}
		if (hoveredIndex >= 0) {
			g.incZ(zDeltaList);
			g.move(0, h * (hoveredIndex + 1));
		}
		if (hovered)
			hoverEffect.render(g, w, h, this);
		if (armed)
			armedEffect.render(g, w, h, this);
		if (hoveredIndex >= 0) {
			g.move(0, -h * (hoveredIndex + 1));
			g.incZ(-zDeltaList);
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (getVisibility() != EVisibility.PICKABLE) {
			return;
		}
		g.fillRect(0, 0, w, h);
		if (isOpen) {
			g.incZ(zDeltaList);
			g.fillRect(0, h, w, h * model.size());
			g.incZ(-zDeltaList);
		}
	}

	@Override
	protected void onMouseMoved(Pick pick) {
		if (this.hovered) {
			setHoveredIndex(toIndex(pick.getPickedPoint()));
		}
		super.onMouseMoved(pick);
	}

	/**
	 * @param index
	 */
	private void setHoveredIndex(int index) {
		if (this.hoveredIndex == index)
			return;
		this.hoveredIndex = index;
		repaint();
	}

	@Override
	protected void onClicked(Pick pick) {
		if (pick.isAnyDragging())
			return;
		armed = true;
		repaint();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (!armed)
			return;
		armed = false;
		if (!isOpen) {
			isOpen = true;
			hoveredIndex = -1;
			repaintAll();
		} else {
			setSelected(hoveredIndex);
			isOpen = false;
			hoveredIndex = -1;
			repaintAll();
		}
		super.onMouseReleased(pick);
	}

	/**
	 * converts the mouse position to the model index, where the mouse is currently over
	 *
	 * @param pickedPoint
	 * @return -1 if none else the index
	 */
	private int toIndex(Point pickedPoint) {
		if (!isOpen)
			return -1;
		float y = toRelative(pickedPoint).y();
		float hi = getSize().y();
		return (int) (Math.round(Math.floor(y / hi)) - 1);
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (isOpen) {
			isOpen = false;
			hoveredIndex = -1;
		}
		super.onMouseOut(pick);
	}


	/**
	 * default value renderer, which renders the label it's a {@link ILabelProvider} otherwise the toString value
	 */
	public static final IGLRenderer DEFAULT = new IGLRenderer() {

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			Object r = parent.getLayoutDataAs(Object.class, "");
			if (r instanceof ILabelProvider)
				r = ((ILabelProvider) r).getLabel();
			g.drawText(Objects.toString(r), 2, 1, w - 2, h - 3);
		}

	};

	/**
	 * callback interface for selection changes of a checkbox
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface ISelectionCallback<T> {
		void onSelectionChanged(GLComboBox<? extends T> widget, T item);
	}

	private static final ISelectionCallback<Object> DUMMY_CALLBACK = new ISelectionCallback<Object>() {
		@Override
		public void onSelectionChanged(GLComboBox<? extends Object> widget, Object item) {

		}
	};
}
