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

/*
 * Created: Jun 12, 2006
 */
package jmetest.effects.transients;

import jmetest.effects.cloth.TestCloth;

import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jmex.effects.transients.Fader;

/**
 * @author Matthew D. Hicks
 */
public class TestFader extends TestCloth {
	private Fader fader;
	
	protected void simpleInitGame() {
		// We're stealing from TestCloth, so we just need to make sure it all gets initted first
		super.simpleInitGame();
		
		// Now we'll create our Fader and add
		float timeInSeconds = 5.0f;
		fader = new Fader("Fader", DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight(), new ColorRGBA(1.0f, 0.0f, 0.0f, 0.0f), timeInSeconds);
		fader.setAlpha(0.0f);
		fader.setMode(Fader.FadeMode.FadeOut);
		rootNode.attachChild(fader);
	}
	
	public void simpleUpdate() {
		super.simpleUpdate();
		
		// Lets make this more fun by constantly fading back and forth
		if ((fader.getFadeMode() == Fader.FadeMode.FadeOut) && (fader.getAlpha() == 1.0f)) {
			// If the fader gets to 1.0f we'll switch to fade back in
			fader.setMode(Fader.FadeMode.FadeIn);
		} else if ((fader.getFadeMode() == Fader.FadeMode.FadeIn) && (fader.getAlpha() == 0.0f)) {
			// If the fader gets to 0.0f we'll switch to fade back out
			fader.setMode(Fader.FadeMode.FadeOut);
		}
	}
	
	public static void main(String[] args) {
		TestFader app = new TestFader();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}
}