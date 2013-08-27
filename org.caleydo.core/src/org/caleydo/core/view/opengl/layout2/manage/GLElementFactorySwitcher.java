/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementAccessor;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.IGLElementVisitor;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * a container of {@link GLElementFactories} results
 *
 * to create a button bar use {@link #createButtonBar()}
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementFactorySwitcher extends GLElement implements IGLElementParent, Iterable<GLElementSupplier> {
	private final List<GLElementSupplier> children;
	private final GLElement[] instances;
	private int active = -1;
	private final Collection<IActiveChangedCallback> callbacks = new ArrayList<>(1);
	private final ELazyiness lazy;

	private final IGLRenderer missingRenderer;

	/**
	 * @param children
	 * @param instances
	 * @param active
	 */
	public GLElementFactorySwitcher(List<GLElementSupplier> children, ELazyiness lazy) {
		this(children, lazy, null);
	}

	public GLElementFactorySwitcher(List<GLElementSupplier> children, ELazyiness lazy, IGLRenderer missingRenderer) {
		this.missingRenderer = Objects.firstNonNull(missingRenderer, GLRenderers.DUMMY);
		this.children = children;
		this.instances = new GLElement[children.size()];
		this.lazy = lazy;
		if (lazy != ELazyiness.DESTROY) {
			for (int i = 0; i < instances.length; ++i) {
				instances[i] = children.get(i).get();
				setup(instances[i]);
				instances[i].setLayoutData(children.get(i));
			}
		}
		setActive(instances.length == 0 ? -1 : 0);
	}

	/**
	 * create a new {@link ButtonBarBuilder} for this switcher
	 *
	 * @return
	 */
	public ButtonBarBuilder createButtonBarBuilder() {
		return new ButtonBarBuilder(this);
	}

	/**
	 * return the cached instance of the given extension by id, may return null depending on the lazyiness
	 *
	 * @param id
	 * @return
	 */
	public GLElement get(String id) {
		for (int i = 0; i < instances.length; ++i) {
			if (children.get(i).getId().equals(id))
				return instances[i];
		}
		return null;
	}

	/**
	 * @return the active, see {@link #active}
	 */
	public int getActive() {
		return active;
	}

	@Override
	public void repaint() {
		super.repaint();
		GLElement s = getActiveElement();
		if (s != null) {
			GLElementAccessor.repaintDown(s);
		}
	}

	@Override
	public void repaintPick() {
		super.repaintPick();
		GLElement s = getActiveElement();
		if (s != null)
			GLElementAccessor.repaintPickDown(s);
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
		GLElement oldv = getActiveElement();
		switch(lazy) {
		case NONE:
			break;
		case DESTROY:
			if (context != null && oldv != null)
				 GLElementAccessor.takeDown(oldv);
			if (old >= 0)
				instances[old] = null; // free element
			if (new_ >= 0) {
				instances[new_] = children.get(new_).get();
				instances[new_].setLayoutData(children.get(new_));
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
	public GLElement getActiveElement() {
		return active < 0 ? null : instances[active];
	}

	public String getActiveId() {
		return active < 0 ? null : children.get(active).getId();
	}

	public GLElementSupplier getActiveSupplier() {
		return active < 0 ? null : children.get(active);
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
		if (lazy == ELazyiness.NONE) {
			for (GLElement instance : instances) {
				instance.layout(deltaTimeMs);
			}
		} else {
			GLElement selected = getActiveElement();
			if (selected != null)
				selected.layout(deltaTimeMs);
		}
	}

	@Override
	protected final void layoutImpl(int deltaTimeMs) {
		super.layoutImpl(deltaTimeMs);
		Vec2f size = getSize();
		if (lazy == ELazyiness.NONE) {
			for (GLElement instance : instances) {
				GLElementAccessor.asLayoutElement(instance).setBounds(0, 0, size.x(), size.y());
			}
		} else {
			GLElement selected = getActiveElement();
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
			GLElement selected = getActiveElement();
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
		GLElement selected = getActiveElement();
		if (selected != null)
			selected.render(g);
		else
			missingRenderer.render(g, w, h, this);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		GLElement selected = getActiveElement();
		if (selected != null)
			selected.renderPick(g);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		GLElement s = getActiveElement();
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

	@Override
	public Iterator<GLElementSupplier> iterator() {
		return children.iterator();
	}

	public Iterable<GLElement> getInstances() {
		return Iterables.filter(Arrays.asList(instances), Predicates.notNull());
	}

	@Override
	public boolean moved(GLElement child) {
		return false;
	}

}
