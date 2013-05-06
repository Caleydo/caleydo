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

import java.awt.Color;
import java.util.Locale;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * a simple basic widget for a slider
 *
 * @author Samuel Gratzl
 *
 */
public class GLSlider extends PickableGLElement {
	/**
	 * width of a gl slider
	 */
	private static final int BAR_WIDTH = 5;

	private ISelectionCallback callback = DUMMY_CALLBACK;

	/**
	 * left and minimal value
	 */
	private float min = 0;
	/**
	 * right and maximal value
	 */
	private float max = 1;
	/**
	 * current value
	 */
	private float value = 0.5f;

	private boolean hovered = false;
	private boolean dragged = false;

	/**
	 * horizontal or vertical rendering
	 */
	private boolean isHorizontal = true;

	/**
	 * show the value or not
	 */
	private boolean isShowText = true;

	public GLSlider() {

	}

	public GLSlider(float min, float max, float value) {
		this.min = min;
		this.max = max;
		this.value = value;
	}

	/**
	 * @return the isHorizontal, see {@link #isHorizontal}
	 */
	public boolean isHorizontal() {
		return isHorizontal;
	}

	/**
	 * @param isHorizontal
	 *            setter, see {@link isHorizontal}
	 */
	public GLSlider setHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
		return this;
	}

	/**
	 * @return the isShowText, see {@link #isShowText}
	 */
	public boolean isShowText() {
		return isShowText;
	}

	/**
	 * @param isShowText
	 *            setter, see {@link isShowText}
	 */
	public GLSlider setShowText(boolean isShowText) {
		this.isShowText = isShowText;
		return this;
	}

	/**
	 * @return the value, see {@link #value}
	 */
	public float getValue() {
		return value;
	}

	/**
	 * @param value
	 *            setter, see {@link value}
	 */
	public void setValue(float value) {
		value = Math.max(min, Math.min(max, value));
		if (this.value == value)
			return;
		this.value = value;
		repaintAll();
		fireCallback(value);
	}

	protected final void fireCallback(float value) {
		callback.onSelectionChanged(this, value);
	}

	/**
	 * @param callback
	 *            setter, see {@link callback}
	 */
	public final GLSlider setCallback(ISelectionCallback callback) {
		if (callback == null)
			callback = DUMMY_CALLBACK;
		if (this.callback == callback)
			return this;
		this.callback = callback;
		return this;
	}



	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (hovered || dragged)
			g.color(Color.GRAY);
		else
			g.color(Color.LIGHT_GRAY);
		if (isHorizontal) {
			float x = mapValue(w);
			g.fillRect(x, 0, Math.min(BAR_WIDTH, w - x), h);
			if (isShowText)
				g.drawText(String.format(Locale.ENGLISH, "%.2f", value), 2, 2, w - 4, h - 8, VAlign.CENTER);
		} else {
			float y = mapValue(h);
			g.fillRect(0, y, w, Math.min(BAR_WIDTH, h - y));
			if (isShowText) {
				g.save().gl.glRotatef(90, 0, 0, 1);
				g.drawText(String.format(Locale.ENGLISH, "%.2f", value), 2, 2 - w, h - 4, w - 8, VAlign.CENTER);
				g.restore();
			}
		}
		g.color(Color.BLACK).drawRect(0, 0, w, h);
	}

	private float mapValue(float total) {
		total -= BAR_WIDTH;
		float range = max - min;
		float factor = total / range;
		return value * factor;
	}

	private float unmapValue(float v) {
		float total = isHorizontal ? getSize().x() : getSize().y();
		total -= BAR_WIDTH;
		float range = max - min;
		float factor = total / range;
		return Math.max(min, Math.min(max, v / factor));
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (isHorizontal) {
			float x = mapValue(w);
			g.fillRect(x, 0, Math.min(BAR_WIDTH, w - x), h);
		} else {
			float y = mapValue(h);
			g.fillRect(0, y, w, Math.min(BAR_WIDTH, h - y));
		}
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (pick.isAnyDragging())
			return;
		hovered = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (!hovered)
			return;
		dragged = false;
		hovered = false;
		repaint();
	}

	@Override
	protected void onClicked(Pick pick) {
		if (pick.isAnyDragging())
			return;
		pick.setDoDragging(true);
		this.dragged = true;
		repaint();
	}

	@Override
	protected void onDragged(Pick pick) {
		if (!pick.isDoDragging())
			return;
		float v;
		if (isHorizontal) {
			v = mapValue(getSize().x()) + pick.getDx();
		} else {
			v = mapValue(getSize().y()) + pick.getDy();
		}
		setValue(unmapValue(v));
		repaintAll();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		this.dragged = false;
		repaint();
	}

	/**
	 * callback interface for selection changes
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface ISelectionCallback {
		void onSelectionChanged(GLSlider slider, float value);
	}

	private static final ISelectionCallback DUMMY_CALLBACK = new ISelectionCallback() {
		@Override
		public void onSelectionChanged(GLSlider slider, float value) {

		}
	};

	public static void main(String[] args) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(2));
		c.add(new GLElement());
		c.add(new GLSlider(0, 1, 0.2f).setHorizontal(false).setSize(32, -1));
		c.add(new GLElement());
		GLSandBox.main(args, c);
	}
}
