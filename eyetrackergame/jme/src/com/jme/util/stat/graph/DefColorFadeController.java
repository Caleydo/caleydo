/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util.stat.graph;

import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial.CullHint;

/**
 * <p>
 * A controller that changes over time the alpha value of the default color of a
 * given Geometry. When coupled with an appropriate BlendState, this can be used
 * to fade in and out unlit objects.
 * </p>
 * 
 * <p>
 * An example of an appropriate BlendState to use with this class:
 * </p>
 * 
 * <pre>
 * BlendState blend = DisplaySystem.getDisplaySystem().getRenderer()
 * 		.createBlendState();
 * blend.setBlendEnabled(true);
 * blend.setSourceFunction(SourceFunction.SourceAlpha);
 * blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
 * </pre>
 * 
 * @author Joshua Slack
 */
public class DefColorFadeController extends Controller {

	private static final long serialVersionUID = 1L;

	private Geometry target;
	private float targetAlpha;
	private float rate;
	private boolean dir;

	/**
	 * Sets up a new instance of the controller. The
	 * 
	 * @param target
	 *            the object whose default color we want to change the alpha on.
	 * @param targetAlpha
	 *            the alpha value we want to end up at.
	 * @param rate
	 *            the amount, per second, to change the alpha. This value will
	 *            be have its sign flipped if it is not the appropriate
	 *            direction given the current default color's alpha.
	 */
	public DefColorFadeController(Geometry target, float targetAlpha, float rate) {
		this.target = target;
		this.targetAlpha = targetAlpha;
		this.dir = target.getDefaultColor().a > targetAlpha;
		if ((dir && rate > 0) || (!dir && rate < 0)) {
			rate *= -1;
		}
		this.rate = rate;
	}

	@Override
	public void update(float time) {
		if (target == null) {
			return;
		}
		float alpha = target.getDefaultColor().a;

		alpha += rate * time;
		if (dir && alpha <= targetAlpha) {
			alpha = targetAlpha;
		} else if (!dir && alpha >= targetAlpha) {
			alpha = targetAlpha;
		}

		if (alpha != 0) {
			target.setCullHint(CullHint.Inherit);
		} else {
			target.setCullHint(CullHint.Always);
		}

		target.getDefaultColor().a = alpha;

		if (alpha == targetAlpha) {
			target.removeController(this);

			// enable gc
			target = null;
		}
	}

}
