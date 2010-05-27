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

package com.jmex.effects;

import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>LensFlareFactory</code>
 *  A Factory useful for creating various types of LensFlares.
 * @author Joshua Slack
 * @version $Id: LensFlareFactory.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */

public class LensFlareFactory {
	private LensFlareFactory() {
	}

	/**
	 * Creates a basic LensFlare with 16 FlareQuad children.  4 textures are used,
	 * the first being used on the main flare point.
	 *
	 * @param name String
	 * @param tex TextureState[] Must be length 4, all non-null.
	 * @return LensFlare
	 */

	public static LensFlare createBasicLensFlare(String name, TextureState[] tex) {
		if (tex == null)
			throw new JmeException("Invalid (null) TextureStates array provided to LensFlareFactory.createBasicLensFlare(String, TextureState[])");
		else if (tex.length != 4)
			throw new JmeException("Wrong number of TextureStates provided to LensFlareFactory.createBasicLensFlare(String, TextureState[]).  Must be 4.");

		DisplaySystem display = DisplaySystem.getDisplaySystem();
		Vector2f midPoint = new Vector2f(display.getWidth() >> 1, display.getHeight() >> 1);

		LensFlare rVal = new LensFlare(name);

		// default texture for children.
		rVal.setRenderState(tex[2]);

		FlareQuad[] sFlare = new FlareQuad[16];
		sFlare[0] = new FlareQuad("sf0", midPoint.x * 1.25f, midPoint.x * 1.25f);
		sFlare[0].setOffset(.8f,.8f);
		sFlare[0].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f, 1f));
		sFlare[0].setRenderState(tex[3]);

		sFlare[1] = new FlareQuad("sf1", midPoint.x * .75f, midPoint.x * .75f);
		sFlare[1].setOffset(1.0f,1.0f);
		sFlare[1].setDefaultColor(new ColorRGBA(.8f, .8f, .8f, 1f));
		sFlare[1].setRenderState(tex[1]);

		sFlare[2] = new FlareQuad("sf2", midPoint.x * .15f, midPoint.x * .15f);
		sFlare[2].setOffset(.8f,.8f);
		sFlare[2].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f, 1f));

		sFlare[3] = new FlareQuad("sf3", midPoint.x * .08f, midPoint.x * .08f);
		sFlare[3].setOffset(2f,2f);
		sFlare[3].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
																					1f));

		sFlare[4] = new FlareQuad("sf4", midPoint.x * .40f, midPoint.x * .40f);
		sFlare[4].setOffset(2.2f,2.2f);
		sFlare[4].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
																					1f));

		sFlare[5] = new FlareQuad("sf5", midPoint.x * .1f, midPoint.x * .1f);
		sFlare[5].setOffset(2.4f,2.4f);
		sFlare[5].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
																					1f));

		sFlare[6] = new FlareQuad("sf6", midPoint.x * .25f, midPoint.x * .25f);
		sFlare[6].setOffset(3f,3f);
		sFlare[6].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
																					1f));

		sFlare[7] = new FlareQuad("sf7", midPoint.x * .01f, midPoint.x * .01f);
		sFlare[7].setOffset(5f,5f);
		sFlare[7].setDefaultColor(new ColorRGBA(.8f, .8f, .8f, 1f));

		sFlare[8] = new FlareQuad("sf8", midPoint.x * .02f, midPoint.x * .02f);
		sFlare[8].setOffset(-3f,-3f);
		sFlare[8].setDefaultColor(new ColorRGBA(.8f, .8f, .8f, 1f));

		sFlare[9] = new FlareQuad("sf9", midPoint.x * .1f, midPoint.x * .1f);
		sFlare[9].setOffset(-2f,-2f);
		sFlare[9].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f, 1f));

		sFlare[10] = new FlareQuad("sf10", midPoint.x * .06f, midPoint.x * .06f);
		sFlare[10].setOffset(-1.8f,-1.8f);
		sFlare[10].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
																					 1f));

		sFlare[11] = new FlareQuad("sf11", midPoint.x * .375f, midPoint.x * .375f);
		sFlare[11].setOffset(-1.5f,-1.5f);
		sFlare[11].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
																					 1f));

		sFlare[12] = new FlareQuad("sf12", midPoint.x * .1f, midPoint.x * .1f);
		sFlare[12].setOffset(-1.4f,-1.4f);
		sFlare[12].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
																					 1f));

		sFlare[13] = new FlareQuad("sf13", midPoint.x * .25f, midPoint.x * .25f);
		sFlare[13].setOffset(-1.1f,-1.1f);
		sFlare[13].setDefaultColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
																					 1f));

		sFlare[14] = new FlareQuad("sf14", midPoint.x * .75f, midPoint.x * .75f);
		sFlare[14].setOffset(-1.0f,-1.0f);
		sFlare[14].setDefaultColor(new ColorRGBA(.8f, .8f, .8f, 1f));
		sFlare[14].setRenderState(tex[1]);

		sFlare[15] = new FlareQuad("mainFlare", midPoint.x * .75f, midPoint.x * .75f);
		sFlare[15].setOffset(1.0f,1.0f);
		sFlare[15].setDefaultColor(new ColorRGBA(.95f, .95f, .95f, 1f));
		sFlare[15].setRenderState(tex[0]);

		for (int i = 0; i < sFlare.length; i++) {
			sFlare[i].setVBOInfo(new VBOInfo(true));
			rVal.attachChild(sFlare[i]);
		}
		return rVal;
	}

}
