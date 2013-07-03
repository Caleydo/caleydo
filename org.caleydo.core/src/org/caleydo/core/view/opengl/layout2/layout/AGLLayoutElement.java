/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;


/**
 * basic implementation of a {@link IGLLayoutElement}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AGLLayoutElement implements IGLLayoutElement {
	@Override
	public final void setBounds(float x, float y, float w, float h) {
		setLocation(x, y);
		setSize(w, h);
	}

	@Override
	public final void setBounds(Vec4f xywh) {
		setBounds(xywh.x(), xywh.y(), xywh.z(), xywh.w());
	}

	@Override
	public final void hide() {
		setSize(0, 0);
	}

	@Override
	public final Vec2f getLocation() {
		Vec4f t = getBounds();
		return new Vec2f(t.x(), t.y());
	}

	public final Vec2f getSize() {
		Vec4f t = getBounds();
		return new Vec2f(t.z(), t.w());
	}

	@Override
	public final float getWidth() {
		return getBounds().z();
	}

	@Override
	public final float getHeight() {
		return getBounds().w();
	}

	@Override
	public final float getSetWidth() {
		return getSetBounds().z();
	}

	@Override
	public final float getSetHeight() {
		return getSetBounds().w();
	}

	@Override
	public final float getSetX() {
		return getSetBounds().x();
	}

	@Override
	public final float getSetY() {
		return getSetBounds().y();
	}

	@Override
	public final Vec2f getSetLocation() {
		Vec4f t = getSetBounds();
		return new Vec2f(t.x(), t.y());
	}

	@Override
	public final Vec2f getSetSize() {
		Vec4f t = getSetBounds();
		return new Vec2f(t.z(), t.w());
	}
}
