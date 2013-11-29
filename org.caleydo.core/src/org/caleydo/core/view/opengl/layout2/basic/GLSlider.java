/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import java.util.Locale;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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

	public enum EValueVisibility {
		NONE, VISIBLE, VISIBLE_HOVERED, VISIBLE_DRAGGED;

		boolean show(boolean dragged, boolean hovered) {
			return this == VISIBLE || (this == VISIBLE_HOVERED && hovered) || (this == VISIBLE_DRAGGED && dragged);
		}
	}

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

	/**
	 * if the user uses the mouse wheel to manipulate the value, how many DIPs are one mouse wheel rotation
	 */
	private float wheelInc = 1f;

	private boolean hovered = false;
	private boolean dragged = false;

	/**
	 * horizontal or vertical rendering
	 */
	private EDimension dim = EDimension.DIMENSION;

	/**
	 * show the value or not
	 */
	private EValueVisibility valueVisibility = EValueVisibility.VISIBLE;

	private EValueVisibility minMaxVisibility = EValueVisibility.NONE;

	/**
	 * the format string to use for rendering a value using {@link String#format(String, Object...)}
	 */
	private String valueFormat = "%.2f";

	public GLSlider() {

	}

	public GLSlider(float min, float max, float value) {
		this.min = min;
		this.max = max;
		this.value = clamp(value);
	}

	/**
	 * @param wheelInc
	 *            setter, see {@link wheelInc}
	 */
	public GLSlider setWheelInc(float wheelInc) {
		this.wheelInc = wheelInc;
		return this;
	}

	/**
	 * @return the dim, see {@link #dim}
	 */
	public EDimension getDim() {
		return dim;
	}

	/**
	 * @param dim
	 *            setter, see {@link dim}
	 */
	public GLSlider setDim(EDimension dim) {
		this.dim = dim;
		return this;
	}

	/**
	 * @param valueFormat
	 *            setter, see {@link valueFormat}
	 */
	public GLSlider setValueFormat(String valueFormat) {
		this.valueFormat = valueFormat;
		return this;
	}

	/**
	 * @param valueVisibility
	 *            setter, see {@link valueVisibility}
	 */
	public GLSlider setValueVisibility(EValueVisibility valueVisibility) {
		if (this.valueVisibility == valueVisibility)
			return this;
		this.valueVisibility = valueVisibility;
		repaint();
		return this;
	}

	/**
	 * @return the valueVisibility, see {@link #valueVisibility}
	 */
	public EValueVisibility getValueVisibility() {
		return valueVisibility;
	}

	/**
	 * @param minMaxVisibility
	 *            setter, see {@link minMaxVisibility}
	 */
	public GLSlider setMinMaxVisibility(EValueVisibility minMaxVisibility) {
		if (this.minMaxVisibility == minMaxVisibility)
			return this;
		this.minMaxVisibility = minMaxVisibility;
		repaint();
		return this;
	}

	/**
	 * @return the minMaxVisibility, see {@link #minMaxVisibility}
	 */
	public EValueVisibility getMinMaxVisibility() {
		return minMaxVisibility;
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
	public GLSlider setValue(float value) {
		value = clamp(value);
		if (this.value == value)
			return this;
		this.value = value;
		repaintAll();
		fireCallback(value);
		return this;
	}

	public GLSlider setMinMax(float min, float max) {
		if (this.min == min && this.max == max)
			return this;
		this.min = min;
		this.max = max;
		this.value = clamp(value);
		repaintAll();
		return this;
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public float getMin() {
		return min;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public float getMax() {
		return max;
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
		boolean showText = valueVisibility.show(dragged, hovered);
		boolean showMinMaxText = minMaxVisibility.show(dragged, hovered);

		if (dim.isHorizontal()) {
			float x = mapValue(w) + 1;
			g.fillRect(x, 0, Math.min(BAR_WIDTH, w - x), h);
			if (showMinMaxText) {
				g.textColor(Color.DARK_GRAY);
				g.drawText(format(min), 2, 4, w - 4, h - 11, VAlign.LEFT);
				g.drawText(format(max), 2, 4, w - 5, h - 11, VAlign.RIGHT);
				g.textColor(Color.BLACK);
			}
			if (showText)
				g.drawText(format(value), 2, 2, w - 4, h - 8, VAlign.CENTER);
		} else {
			float y = mapValue(h) + 1;
			g.fillRect(0, y, w, Math.min(BAR_WIDTH, h - y));
			if (showText || showMinMaxText)
				g.save().gl.glRotatef(90, 0, 0, 1);
			if (showMinMaxText) {
				g.textColor(Color.DARK_GRAY);
				g.drawText(format(min), 2, 4 - w, h - 4, w - 11, VAlign.LEFT);
				g.drawText(format(max), 2, 4 - w, h - 5, w - 11, VAlign.RIGHT);
				g.textColor(Color.BLACK);
			}
			if (showText)
				g.drawText(format(value), 2, 2 - w, h - 4, w - 8, VAlign.CENTER);
			if (showText || showMinMaxText)
				g.restore();
		}
		g.color(Color.BLACK).drawRect(0, 0, w, h);
	}

	protected String format(float v) {
		return String.format(Locale.ENGLISH, valueFormat, v);
	}

	private float mapValue(float total) {
		total -= BAR_WIDTH + 2;
		float range = max - min;
		float factor = total / range;
		return (value - min) * factor;
	}

	private float unmapValue(float v) {
		float total = dim.isHorizontal() ? getSize().x() : getSize().y();
		total -= BAR_WIDTH + 2;
		float range = max - min;
		float factor = total / range;
		return clamp(v / factor + min);
	}

	private float clamp(float v) {
		return Math.max(min, Math.min(max, v));
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (dim.isHorizontal()) {
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
	protected void onMouseWheel(Pick pick) {
		setValue(unmapValue(mapValue(getSize().x()) + ((IMouseEvent) (pick)).getWheelRotation() * wheelInc));
		repaintAll();
	}

	@Override
	protected void onDragDetected(Pick pick) {
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
		if (dim.isHorizontal()) {
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

	@Override
	protected void onDoubleClicked(Pick pick) {
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				new InputBox(canvas).open();
			}
		});
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

	private class InputBox extends AInputBoxDialog {
		public InputBox(Composite canvas) {
			super(null, "Set Value", GLSlider.this, canvas);
		}

		@Override
		protected void set(String value) {
			setValue(Float.parseFloat(value));
		}

		@Override
		protected String verify(String value) {
			try {
				float v = Float.parseFloat(value);
				if (v < min)
					return "Too small, needs to be in the range: [" + min + "," + max + "]";
				if (v > max)
					return "Too large, needs to be in the range: [" + min + "," + max + "]";
			} catch (NumberFormatException e) {
				return "The value: '" + value + "' can't be parsed to Float: " + e.getMessage();
			}
			return null;
		}

		@Override
		protected String getInitialValue() {
			return String.valueOf(getValue());
		}

	}

	public static void main(String[] args) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(2));
		c.add(new GLElement());
		c.add(new GLSlider(1, 20, 0.2f).setDim(EDimension.DIMENSION)
				.setMinMaxVisibility(EValueVisibility.VISIBLE_DRAGGED)
				.setSize(-1, 32));
		c.add(new GLElement());
		GLSandBox.main(args, c);
	}
}
