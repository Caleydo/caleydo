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

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.Durations.IDuration;
import org.caleydo.core.view.opengl.layout2.animation.StyleAnimations.IStyleAnimation;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * @author Samuel Gratzl
 *
 */
public class StyleAnimation extends AAnimation {
	private final IStyleAnimation anim;

	private float lastAlpha = -1;

	public StyleAnimation(int startIn, IDuration duration, IGLLayoutElement animated, IStyleAnimation anim) {
		super(startIn, duration, animated);
		this.anim = anim;
	}

	@Override
	protected void animate(float alpha, float w, float h) {
		lastAlpha = alpha;
	}

	@Override
	protected void firstTime(float w, float h) {
		lastAlpha = 0;
	}

	@Override
	protected void lastTime() {
		lastAlpha = 1;
	}

	public boolean isDone() {
		return lastAlpha >= 1;
	}

	public void render(GLGraphics g) {
		GLElement elem = getAnimatedElement();
		if (lastAlpha < 0)
			elem.render(g);
		else
			anim.render(elem, g, lastAlpha);
	}

	@Override
	public EAnimationType getType() {
		return EAnimationType.STYLE;
	}

}
