/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementAccessor;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.IGLElementVisitor;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * a container of {@link GLElementFactories} results
 *
 * to create a button bar use {@link #createButtonBar()}
 *
 * @author Samuel Gratzl
 *
 */
public class GLExtensionElement extends GLElement implements IGLElementParent, Iterable<GLElementSupplier> {
	private final List<GLElementSupplier> children;
	private final GLElement[] instances;
	private int active = 0;
	private final Collection<IActiveChangedCallback> callbacks = new ArrayList<>(1);
	private final ELazyiness lazy;

	/**
	 * @param children
	 * @param instances
	 * @param active
	 */
	public GLExtensionElement(List<GLElementSupplier> children, ELazyiness lazy) {
		this.children = children;
		this.instances = new GLElement[children.size()];
		this.lazy = lazy;
		if (lazy != ELazyiness.DESTROY) {
			for (int i = 0; i < instances.length; ++i) {
				instances[i] = children.get(i).get();
			}
		}
	}

	public GLElement createButtonBar() {
		return new ButtonBar();
	}

	/**
	 * @return the active, see {@link #active}
	 */
	public int getActive() {
		return active;
	}

	/**
	 * @param active
	 *            setter, see {@link active}
	 */
	public void setActive(int active) {
		if (this.active == active)
			return;
		int old = this.active;
		int new_ = active;
		GLElement oldv = getSelected();
		switch(lazy) {
		case NONE:
			break;
		case DESTROY:
			if (context != null && oldv != null)
				 GLElementAccessor.takeDown(oldv);
			instances[old] = null; // free element
			if (new_ >= 0) {
				instances[new_] = children.get(new_).get();
				if (context != null)
					setup(instances[new_]);
			}
			break;
		case UNINITIALIZE:
			if (context != null) {
				 if (oldv != null)
					 GLElementAccessor.takeDown(oldv);
				if (new_ >= 0)
					setup(instances[new_]);
			}
			break;
		}
		this.active = active;
		fireActiveChanged(active);
		relayout();
	}

	private void setup(GLElement child) {
		GLElementAccessor.setParent(child, this);
		if (context != null)
			GLElementAccessor.init(child, context);
	}

	/**
	 * @param active2
	 */
	private void fireActiveChanged(int a) {
		for (IActiveChangedCallback c : callbacks)
			c.onActiveChanged(a);
	}

	/**
	 * @return
	 */
	private GLElement getSelected() {
		return active < 0 ? null : instances[active];
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
		if (lazy == ELazyiness.NONE) {
			for (GLElement instance : instances) {
				instance.layout(deltaTimeMs);
			}
		} else {
			GLElement selected = getSelected();
			if (selected != null)
				selected.layout(deltaTimeMs);
		}
	}

	@Override
	protected final void layoutImpl() {
		super.layoutImpl();
		Vec2f size = getSize();
		if (lazy == ELazyiness.NONE) {
			for (GLElement instance : instances) {
				GLElementAccessor.asLayoutElement(instance).setBounds(0, 0, size.x(), size.y());
			}
		} else {
			GLElement selected = getSelected();
			if (selected != null)
				GLElementAccessor.asLayoutElement(selected).setBounds(0, 0, size.x(), size.y());
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		if (lazy == ELazyiness.NONE) {
			for (GLElement instance : instances) {
				GLElementAccessor.init(instance, context);
			}
		} else {
			GLElement selected = getSelected();
			if (selected != null)
				GLElementAccessor.init(selected, context);
		}
	}

	public final int size() {
		return children.size();
	}

	public final boolean isEmpty() {
		return children.isEmpty();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		GLElement selected = getSelected();
		if (selected != null)
			selected.render(g);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		GLElement selected = getSelected();
		if (selected != null)
			selected.renderPick(g);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		GLElement s = getSelected();
		if (s != null) {
			T v = s.getLayoutDataAs(clazz, null);
			if (v != null)
				return v;
		}
		return super.getLayoutDataAs(clazz, default_);
	}

	@Override
	protected boolean hasPickAbles() {
		return super.hasPickAbles() || !children.isEmpty(); // may have pickables
	}

	@Override
	public <P, R> R accept(IGLElementVisitor<P, R> visitor, P para) {
		return visitor.visit(this, para);
	}

	public void onActiveChanged(IActiveChangedCallback callback) {
		callbacks.add(callback);
	}

	public boolean removeOnActiveChanged(IActiveChangedCallback callback) {
		return callbacks.remove(callback);
	}

	public static interface IActiveChangedCallback {
		void onActiveChanged(int active);
	}

	/**
	 * how lazy should the children be
	 * <dl>
	 * <dt>NONE</dt>
	 * <dd>all elements will be initialized but only the selected will be rendered</dd>
	 * <dt>UNINITIALIZEE</dt>
	 * <dd>all elements will be created but only the selected will be initialized and rendered</dd>
	 * <dt>DESTROY</dt>
	 * <dd>just the selected element is created, initialized and rendered</dd>
	 * </dl>
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public static enum ELazyiness {
		NONE, UNINITIALIZE, DESTROY
	}

	private class ButtonBar extends GLElementContainer implements ISelectionCallback, IActiveChangedCallback,
			IGLRenderer {
		private final RadioController controller = new RadioController(this);

		public ButtonBar() {
			setSize(Float.NaN, 16);
			setLayout(GLLayouts.flowHorizontal(2));

			int i = 0;
			for (GLElementSupplier sup : GLExtensionElement.this) {
				GLButton b = new GLButton();
				b.setPickingObjectId(i++);
				b.setTooltip(sup.getLabel());
				b.setLayoutData(sup);
				b.setRenderer(this);
				controller.add(b);
				b.setSize(16, Float.NaN);
				this.add(b);
			}
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			GLElementSupplier sub = parent.getLayoutDataAs(GLElementSupplier.class, null);
			g.fillImage(g.getTexture(sub.getIcon()), 0, 0, w, h);
			if (((GLButton) parent).isSelected()) {
				g.gl.glEnable(GL.GL_BLEND);
				g.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				g.gl.glEnable(GL.GL_LINE_SMOOTH);
				g.color(1, 1, 1, 0.5f).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
				g.gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			}
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			GLExtensionElement.this.onActiveChanged(this);
			controller.setSelected(getActive());
		}

		@Override
		protected void takeDown() {
			GLExtensionElement.this.removeOnActiveChanged(this);
			super.takeDown();
		}

		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {
			GLExtensionElement.this.setActive(button.getPickingObjectId());
		}

		@Override
		public void onActiveChanged(int active) {
			controller.setSelected(active);
		}
	}

	@Override
	public Iterator<GLElementSupplier> iterator() {
		return children.iterator();
	}

	@Override
	public boolean moved(GLElement child) {
		return false;
	}

}
