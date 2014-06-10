/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import static org.caleydo.core.view.opengl.layout2.animation.InOutStrategies.MAX;
import static org.caleydo.core.view.opengl.layout2.animation.InOutStrategies.OTHER;
import static org.caleydo.core.view.opengl.layout2.animation.InOutStrategies.ZERO;
import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.InOutStrategies.IInOutStrategy;

/**
 * strategy what is the initial or last bounds of a element that is added or removed
 * 
 * @author Samuel Gratzl
 * 
 */
public class InOutInitializers {
	public interface IInOutInitializer {
		Vec4f get(Vec4f to_from, float w, float h);
	}

	public static final IInOutInitializer LEFT = new InOutInitializerBase(ZERO, OTHER, OTHER, OTHER);
	public static final IInOutInitializer RIGHT = new InOutInitializerBase(MAX, OTHER, OTHER, OTHER);
	public static final IInOutInitializer TOP = new InOutInitializerBase(OTHER, ZERO, OTHER, OTHER);
	public static final IInOutInitializer BOTTOM = new InOutInitializerBase(OTHER, MAX, OTHER, OTHER);

	public static final IInOutInitializer APPEAR = new InOutInitializerBase(OTHER, OTHER, OTHER, OTHER);

	public static class InOutInitializerBase implements IInOutInitializer {
		protected final IInOutStrategy x;
		protected final IInOutStrategy y;
		protected final IInOutStrategy w;
		protected final IInOutStrategy h;

		public InOutInitializerBase(IInOutStrategy x, IInOutStrategy y, IInOutStrategy w, IInOutStrategy h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		@Override
		public Vec4f get(Vec4f to_from, float w, float h) {
			Vec4f r = new Vec4f();
			r.setX(this.x.compute(to_from.x(), w));
			r.setY(this.y.compute(to_from.y(), h));
			r.setZ(this.w.compute(to_from.z(), w));
			r.setW(this.h.compute(to_from.w(), h));
			return r;
		}
	}
}

