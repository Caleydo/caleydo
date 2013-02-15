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

