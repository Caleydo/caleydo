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
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.util.TextureManager;

/**
 * <code>TestSharedMesh</code>
 * 
 * @author Mark Powell
 * @version $Id: TestSharedMesh.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestSharedMesh extends SimpleGame {
	private Quaternion rotQuat = new Quaternion();

	private float angle = 0;

	private Vector3f axis = new Vector3f(1, 1, 0);

	private Sphere s;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestSharedMesh app = new TestSharedMesh();
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

	/**
	 * builds the trimesh.
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		display.setTitle("jME - Sphere");

		s = new Sphere("Sphere", 20, 20, 25);
		s.setModelBound(new BoundingBox());
		s.updateModelBound();
		s.setVBOInfo(new VBOInfo(true));
        
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);
        
		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		ts.setTexture(TextureManager.loadTexture(
				TestSharedMesh.class.getClassLoader().getResource(
						"jmetest/data/images/Monkey.jpg"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
		
		TextureState ts2 = display.getRenderer().createTextureState();
		ts2.setEnabled(true);
		ts2.setTexture(TextureManager.loadTexture(
				TestSharedMesh.class.getClassLoader().getResource(
						"jmetest/data/texture/grass.jpg"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
		
		TextureState ts3 = display.getRenderer().createTextureState();
		ts3.setEnabled(true);
		ts3.setTexture(TextureManager.loadTexture(
				TestSharedMesh.class.getClassLoader().getResource(
						"jmetest/data/texture/clouds.png"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
		
		TextureState ts4 = display.getRenderer().createTextureState();
		ts4.setEnabled(true);
		ts4.setTexture(TextureManager.loadTexture(
				TestSharedMesh.class.getClassLoader().getResource(
						"jmetest/data/texture/water.png"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
		
		

		Node n1 = new Node("n1");
		Node n2 = new Node("n2");
		Node n3 = new Node("n3");
		Node n4 = new Node("n4");
		n1.setLocalTranslation(new Vector3f(750, 0, 0));
		n2.setLocalTranslation(new Vector3f(750, 0, 750));
		n3.setLocalTranslation(new Vector3f(-750, 0, 750));
		n4.setLocalTranslation(new Vector3f(-750, 0, -750));

//		n1.setRenderState(ts);
//		n2.setRenderState(ts2);
//		n3.setRenderState(ts3);
//		n4.setRenderState(ts4);
		
		rootNode.attachChild(n1);
		rootNode.attachChild(n2);
		rootNode.attachChild(n3);
		rootNode.attachChild(n4);
		
		WireframeState ws = display.getRenderer().createWireframeState();
		ws.setEnabled(true);

		for (int i = 0; i < 100; i++) {
			SharedMesh sm = new SharedMesh("Share" + i, s);
			sm.setLocalTranslation(new Vector3f(
					(float) Math.random() * 500 - 250,
					(float) Math.random() * 500 - 250,
					(float) Math.random() * 500 - 250));
			sm.setRenderState(ts);
			sm.setRenderState(ws);
			n1.attachChild(sm);
		}

		for (int i = 0; i < 100; i++) {
			SharedMesh sm = new SharedMesh("Share" + i, s);
			sm.setLocalTranslation(new Vector3f(
					(float) Math.random() * 500 - 250,
					(float) Math.random() * 500 - 250,
					(float) Math.random() * 500 - 250));
			sm.setRenderState(ts2);
			sm.setLightCombineMode(LightCombineMode.Off);
			n2.attachChild(sm);
		}

		for (int i = 0; i < 100; i++) {
			SharedMesh sm = new SharedMesh("Share" + i, s);
			sm.setLocalTranslation(new Vector3f(
					(float) Math.random() * 500 - 250,
					(float) Math.random() * 500 - 250,
					(float) Math.random() * 500 - 250));
			sm.setRenderState(ts3);
			
			n3.attachChild(sm);
		}

		for (int i = 0; i < 100; i++) {
			SharedMesh sm = new SharedMesh("Share" + i, s);
			sm.setLocalTranslation(new Vector3f(
					(float) Math.random() * 1000 - 500,
					(float) Math.random() * 1000 - 500,
					(float) Math.random() * 1000 - 500));
			sm.setRenderState(ts4);
			n4.attachChild(sm);
		}

	}
}
