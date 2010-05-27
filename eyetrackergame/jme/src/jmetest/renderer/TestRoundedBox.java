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
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.RoundedBox;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class TestRoundedBox extends SimpleGame {
	private Quaternion rotQuat = new Quaternion();
	private float angle = 0;
	private Vector3f axis = new Vector3f(1, 1, 0);
	private RoundedBox s;
	
	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestRoundedBox app = new TestRoundedBox();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();

	}

	protected void simpleUpdate() {
		if (tpf < 1) {
			angle = angle + (tpf * 1);
			if (angle > 360) {
				angle = 0;
			}
		}
		rotQuat.fromAngleAxis(angle, axis);
		s.setLocalRotation(rotQuat);
	}

	protected void simpleInitGame() {
		display.setTitle("jME - Sphere");

	    s = new RoundedBox("Sphere", new Vector3f(10,10,10), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0.5f, 0.5f));
	    s.setLocalTranslation(new Vector3f(0,0,-40));
	    s.setModelBound(new BoundingBox());
	    s.updateModelBound();
	    rootNode.attachChild(s);

	    TextureState ts = display.getRenderer().createTextureState();
	    ts.setEnabled(true);
	    ts.setTexture(
	        TextureManager.loadTexture(
	        TestSphere.class.getClassLoader().getResource(
	        "jmetest/data/images/Monkey.jpg"),
	        Texture.MinificationFilter.Trilinear,
	        Texture.MagnificationFilter.Bilinear));

	}
}
