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

package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;

/**
 * @author mpowell
 * 
 */
public class TestFontPanel extends SimpleGame {
	
	private int counter;
	Node fontPanel;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.app.SimpleGame#simpleInitGame()
	 */
	protected void simpleInitGame() {
		Vector2f center = new Vector2f(display.getWidth()>>1, display.getWidth()>>1);
		
		fontPanel = new Node("Font Panel");
		fontPanel.setLocalTranslation(new Vector3f(center.x, center.y, 0));
		
		Quad q1 = new Quad("Ortho Q1", 100, 100);
		q1.setZOrder(1);
		q1.setDefaultColor(ColorRGBA.blue.clone());
		q1.setLightCombineMode(LightCombineMode.Off);
		q1.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		rootNode.detachChild(statNode);
		fontPanel.attachChild(q1);
		fontPanel.attachChild(statNode);
		rootNode.attachChild(fontPanel);
		
		

	}
	
	protected void simpleUpdate() {
		counter++;
		if(counter > 1000) {
			counter = 0;
			int randy = (int)(Math.random() * 200 - 100);
			int randx = (int)(Math.random() * 200 - 100);
			Vector2f center = new Vector2f(display.getWidth()>>1, display.getWidth()>>1);
			fontPanel.setLocalTranslation(new Vector3f(center.x + randy, center.y + randx, 0));
		}
	}

	public static void main(String[] args) {
		TestFontPanel app = new TestFontPanel();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

}
