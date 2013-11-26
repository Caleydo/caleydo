/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import java.util.Objects;

import org.apache.commons.lang.ObjectUtils;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Supplier;
import com.google.common.primitives.Ints;

/**
 * a simple basic widget for a spinner, e.g integer spinner
 *
 * @author Samuel Gratzl
 *
 */
public class GLSpinner<T> extends PickableGLElement {
	private static final float BUTTON_SIZE = 12;
	/**
	 * callback for value changes
	 */
	private IChangeCallback<? super T> callback = DUMMY_CALLBACK;

	/**
	 * renderer to render a specific model item
	 *
	 * the item can be retrieved by {@link GLElement#getLayoutDataAs(Class, Object)}
	 */
	private final IGLRenderer valueRenderer;

	private T value;

	/**
	 * model describing the way to increment descrement a value
	 */
	private final ISpinnerModel<T> model;

	private boolean isRenderingValue = false;

	private boolean hovered;
	private int armed = -1;

	/**
	 *
	 */
	public GLSpinner(T initialValue, ISpinnerModel<T> model, IGLRenderer valueRenderer) {
		this.value = initialValue;
		this.model = model;
		this.valueRenderer = valueRenderer;
		setRenderer(GLRenderers.fillRect(Color.WHITE));
	}

	private void inc() {
		if (!model.canInc(value))
			return;
		T next = model.inc(value);
		if (Objects.equals(next, value))
			return;
		this.value = next;
		repaint();
		fireCallback();
	}

	private void dec() {
		if (!model.canDec(value))
			return;
		T next = model.dec(value);
		if (Objects.equals(next, value))
			return;
		this.value = next;
		repaint();
		fireCallback();
	}

	protected final void fireCallback() {
		callback.onValueChanged(this, value);
	}

	/**
	 * @param callback
	 *            setter, see {@link callback}
	 */
	public final GLSpinner<T> setCallback(IChangeCallback<? super T> callback) {
		if (callback == null)
			callback = DUMMY_CALLBACK;
		if (this.callback == callback)
			return this;
		this.callback = callback;
		return this;
	}

	@Override
	protected void onMouseOver(Pick pick) {
		this.hovered = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		this.hovered = false;
		repaint();
	}

	@Override
	protected void onDoubleClicked(Pick pick) {
		if (!model.canSpecify())
			return;
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				new InputBox(canvas).open();
			}
		});
	}
	/**
	 * allow value changes via mouse wheel
	 *
	 * @param pick
	 */
	@Override
	protected void onMouseWheel(Pick pick) {
		int r = ((IMouseEvent) pick).getWheelRotation();
		if (r > 0)
			inc();
		else if (r < 0)
			dec();
	}

	@Override
	protected void onClicked(Pick pick) {
		Vec2f p = toRelative(pick.getPickedPoint());
		Vec2f size = getSize();
		if (p.x() < size.x() - BUTTON_SIZE)
			return;
		armed = p.y() <= size.y() * 0.5f ? 0 : 1;
		repaint();
	}

	@Override
	protected void onDragged(Pick pick) {
		Vec2f p = toRelative(pick.getPickedPoint());
		Vec2f size = getSize();
		if (p.x() < size.x() - BUTTON_SIZE) {
			if (armed >= 0)
				repaint();
			armed = -1;
			return;
		}
		int newArmed = p.y() <= size.y() * 0.5f ? 0 : 1;
		if (newArmed != armed) {
			armed = newArmed;
			repaint();
		}
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (armed < 0)
			return;
		if (armed == 0)
			inc();
		else
			dec();
		armed = -1;
		repaint();
	}

	/**
	 * @return the value, see {@link #value}
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value
	 *            setter, see {@link value}
	 */
	public GLSpinner<T> setValue(T value) {
		if (Objects.equals(this.value, value))
			return this;
		this.value = value;
		repaint();
		return this;
	}

	@Override
	public <U> U getLayoutDataAs(Class<U> clazz, Supplier<? extends U> default_) {
		if (isRenderingValue && clazz.isInstance(value))
			return clazz.cast(value);
		return super.getLayoutDataAs(clazz, default_);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		if (!hovered) {
			renderValue(g, w, h);
			return;
		}
		renderValue(g, w - BUTTON_SIZE, h);

		float hhalf = h * 0.5f;
		float offset = w - BUTTON_SIZE;
		// armed effects
		g.color(armed != 0 ? Color.LIGHT_GRAY : Color.GRAY).fillRect(offset, 0, BUTTON_SIZE, hhalf);
		g.color(armed != 1 ? Color.LIGHT_GRAY : Color.GRAY).fillRect(offset, hhalf, BUTTON_SIZE, hhalf);

		// borders and triangles
		g.color(Color.DARK_GRAY);
		g.drawRect(0, 0, w, h);
		g.move(offset, 0);
		g.fillPolygon(new Vec2f(1, hhalf - 2), new Vec2f(BUTTON_SIZE - 1, hhalf - 2), new Vec2f(BUTTON_SIZE * 0.5f, 2));
		g.fillPolygon(new Vec2f(1, hhalf + 2), new Vec2f(BUTTON_SIZE - 1, hhalf + 2), new Vec2f(BUTTON_SIZE * 0.5f,
				h - 2));

		g.drawLine(0, 0, 0, h);
		g.drawLine(0, hhalf, BUTTON_SIZE, hhalf);
		g.move(-offset, 0);

	}

	private void renderValue(GLGraphics g, float w, float h) {
		isRenderingValue = true;
		valueRenderer.render(g, w, h, this);
		isRenderingValue = false;
	}

	/**
	 * callback interface for value changes of a spinner
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IChangeCallback<TT> {
		void onValueChanged(GLSpinner<? extends TT> spinner, TT value);
	}

	private static final IChangeCallback<Object> DUMMY_CALLBACK = new IChangeCallback<Object>() {
		@Override
		public void onValueChanged(GLSpinner<?> spinner, Object value) {

		}
	};

	/**
	 * default value renderer, which renders the label it's a {@link ILabeled} otherwise the toString value
	 */
	public static final IGLRenderer DEFAULT = new IGLRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			Object r = parent.getLayoutDataAs(Object.class, "");
			if (r instanceof ILabeled)
				r = ((ILabeled) r).getLabel();
			g.drawText(Objects.toString(r), 2, 1, w - 2, h - 3);
		}

	};

	private class InputBox extends AInputBoxDialog {
		public InputBox(Composite canvas) {
			super(null, "Set Value", GLSpinner.this, canvas);
		}

		@Override
		protected void set(String value) {
			setValue(model.parse(value));
		}

		@Override
		protected String verify(String value) {
			return model.verify(value);
		}

		@Override
		protected String getInitialValue() {
			return model.format(getValue());
		}

	}

	/**
	 * model for a {@link GLSpinner} describing the way how to increment and decrement a value
	 *
	 * @author Samuel Gratzl
	 *
	 * @param <T>
	 */
	public interface ISpinnerModel<T> {
		/**
		 * increment the given value
		 *
		 * @param value
		 * @return
		 */
		T inc(T value);

		/**
		 * decrement the given value
		 *
		 * @param value
		 * @return
		 */
		T dec(T value);

		/**
		 * test whether the value can be incremented
		 *
		 * @param value
		 * @return
		 */
		boolean canInc(T value);

		/**
		 * test whether the value can be decremented
		 *
		 * @param value
		 * @return
		 */
		boolean canDec(T value);

		/**
		 * whether specifying a exact value by dialog is supported
		 *
		 * @return
		 */
		boolean canSpecify();

		T parse(String text);

		String verify(String text);

		String format(T value);
	}

	/**
	 * simple spinner factory for an integer spinner
	 *
	 * @param value
	 *            the initial value
	 * @param min
	 *            minimum value
	 * @param max
	 *            maximum value
	 * @param inc
	 *            the step width
	 * @return
	 */
	public static GLSpinner<Integer> createIntegerSpinner(int value, int min, int max, int inc) {
		return createIntegerSpinner(value, min, max, inc, DEFAULT);
	}

	public static GLSpinner<Integer> createIntegerSpinner(int value, int min, int max, int inc,
			IGLRenderer valueRenderer) {
		return new GLSpinner<Integer>(value, new IntSpinnerModel(min, max, inc), valueRenderer);
	}

	private static class IntSpinnerModel implements ISpinnerModel<Integer> {
		private final int min;
		private final int max;
		private final int inc;

		public IntSpinnerModel(int min, int max, int inc) {
			this.min = min;
			this.max = max;
			this.inc = inc;
		}
		@Override
		public Integer inc(Integer value) {
			return Math.min(value.intValue()+inc,max);
		}
		@Override
		public Integer dec(Integer value) {
			return Math.max(value.intValue()-inc,min);
		}
		@Override
		public boolean canInc(Integer value) {
			return value != null && value.intValue() < max;
		}
		@Override
		public boolean canDec(Integer value) {
			return value != null && value.intValue() > min;
		}

		@Override
		public boolean canSpecify() {
			return true;
		}

		@Override
		public Integer parse(String text) {
			return Integer.parseInt(text);
		}

		@Override
		public String verify(String text) {
			Integer v_o = Ints.tryParse(text);
			if (v_o == null)
				return "Invalid value: " + text + " can't be parsed to an Integer";
			int v = v_o.intValue();
			if (v < min)
				return "Too small, needs to be in the range: [" + min + "," + max + "]";
			if (v > max)
				return "Too large, needs to be in the range: [" + min + "," + max + "]";
			return null;
		}

		@Override
		public String format(Integer value) {
			return ObjectUtils.toString(value);
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, createIntegerSpinner(10, 0, 100, 1));
	}
}
