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

package jmetest.effects.glsl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetest.renderer.loader.TestMaxJmeWrite;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.effects.glsl.SketchRenderPass;
import com.jmex.model.converters.MaxToJme;

/**
 * Sketch effect pass test
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class TestSketch extends SimplePassGame {
    private static final Logger logger = Logger.getLogger(TestSketch.class
            .getName());
    
	private SketchRenderPass sketchRenderPass;
	private int screenshotIndex = 0;

	public static void main(String[] args) {
		TestSketch app = new TestSketch();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	protected void cleanup() {
		super.cleanup();
		sketchRenderPass.cleanup();
	}

	protected void simpleInitGame() {
		//Setup camera
		cam.setFrustumPerspective(55.0f, (float) display.getWidth() / (float) display.getHeight(), 1, 1000);

		//Setup lights
		PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		light.setLocation(new Vector3f(0, 30, 0));
		light.setEnabled(true);
		lightState.attach(light);

		//Add dummy objects to rootNode
		rootNode.attachChild(createObjects());
		
		try {
            MaxToJme C1=new MaxToJme();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/char.3ds");
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
            Node r=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
            r.setLocalScale(.1f);
            if (r.getChild(0).getControllers().size()!=0)
                r.getChild(0).getController(0).setSpeed(20);
            Quaternion temp=new Quaternion();
            temp.fromAngleAxis(FastMath.PI/2,new Vector3f(-1,0,0));
            r.setLocalRotation(temp);
            r.setLocalTranslation(new Vector3f(0,3,0));
            rootNode.attachChild(r);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading max file", e);
        }

		//Setup renderpasses
		RenderPass rootPass = new RenderPass();
		rootPass.add(rootNode);
		pManager.add(rootPass);

		sketchRenderPass = new SketchRenderPass(cam, 2);
        if(!sketchRenderPass.isSupported()) {
            Text t = Text.createDefaultTextLabel("Text", "GLSL Not supported on this computer.");
            t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            t.setLightCombineMode(LightCombineMode.Off);
            t.setLocalTranslation(new Vector3f(0,20,0));
            statNode.attachChild(t);
        } else {
            sketchRenderPass.add(rootNode);
            pManager.add(sketchRenderPass);
        }
        
        if(!sketchRenderPass.isSupported()) {
               Text t = Text.createDefaultTextLabel("Text", "GLSL Not supported on this computer.");
               t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
               t.setLightCombineMode(LightCombineMode.Off);
               t.setLocalTranslation(new Vector3f(0,20,0));
               statNode.attachChild(t);
           }

		RenderPass statPass = new RenderPass();
		statPass.add(statNode);
		pManager.add(statPass);

		//Initialize keybindings
		KeyBindingManager.getKeyBindingManager().set("1", KeyInput.KEY_1);
		KeyBindingManager.getKeyBindingManager().set("2", KeyInput.KEY_2);
		KeyBindingManager.getKeyBindingManager().set("3", KeyInput.KEY_3);
		KeyBindingManager.getKeyBindingManager().set("4", KeyInput.KEY_4);
		KeyBindingManager.getKeyBindingManager().set("5", KeyInput.KEY_5);
		KeyBindingManager.getKeyBindingManager().set("6", KeyInput.KEY_6);
		KeyBindingManager.getKeyBindingManager().set("7", KeyInput.KEY_7);
		KeyBindingManager.getKeyBindingManager().set("8", KeyInput.KEY_8);
		KeyBindingManager.getKeyBindingManager().set("9", KeyInput.KEY_9);
		KeyBindingManager.getKeyBindingManager().set("0", KeyInput.KEY_0);

		KeyBindingManager.getKeyBindingManager().set("y", KeyInput.KEY_Y);
		KeyBindingManager.getKeyBindingManager().set("h", KeyInput.KEY_H);

		KeyBindingManager.getKeyBindingManager().set("shot", KeyInput.KEY_F4);
	}

	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("1", false)) {
			sketchRenderPass.setEnabled(!sketchRenderPass.isEnabled());
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("2", false)) {
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("3", false)) {
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("4", false)) {
			if(sketchRenderPass.getNormalMult() > 0.0f)
				sketchRenderPass.setNormalMult(sketchRenderPass.getNormalMult() - 0.1f);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("5", false)) {
			sketchRenderPass.setNormalMult(sketchRenderPass.getNormalMult() + 0.1f);
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("6", false)) {
			if(sketchRenderPass.getDepthMult() > 0.0f)
				sketchRenderPass.setDepthMult(sketchRenderPass.getDepthMult() - 1.0f);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("7", false)) {
			sketchRenderPass.setDepthMult(sketchRenderPass.getDepthMult() + 1.0f);
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("8", false)) {
			sketchRenderPass.setOff(sketchRenderPass.getOff() * 0.5f);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("9", false)) {
			sketchRenderPass.setOff(sketchRenderPass.getOff() * 2.0f);
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("y", false)) {
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("h", false)) {
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("0", false)) {
			sketchRenderPass.resetParameters();
		}


		if(KeyBindingManager.getKeyBindingManager().isValidCommand("shot", false)) {
			display.getRenderer().takeScreenShot("shot" + screenshotIndex++);
		}
	}

	private Node createObjects() {
		Node objects = new Node("objects");

		Torus torus = new Torus("Torus", 50, 50, 10, 20);
		torus.setLocalTranslation(new Vector3f(50, -5, 20));
		TextureState ts = display.getRenderer().createTextureState();
		Texture t0 = TextureManager.loadTexture(
				TestSketch.class.getClassLoader().getResource(
						"jmetest/data/images/Monkey.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		Texture t1 = TextureManager.loadTexture(
				TestSketch.class.getClassLoader().getResource(
						"jmetest/data/texture/north.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t1.setEnvironmentalMapMode(Texture.EnvironmentalMapMode.SphereMap);
		ts.setTexture(t0, 0);
		ts.setTexture(t1, 1);
		ts.setEnabled(true);
		torus.setRenderState(ts);
		objects.attachChild(torus);

		ts = display.getRenderer().createTextureState();
		t0 = TextureManager.loadTexture(
				TestSketch.class.getClassLoader().getResource(
						"jmetest/data/texture/wall.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		ts.setTexture(t0);

		Box box = new Box("box1", new Vector3f(-10, -10, -10), new Vector3f(10, 10, 10));
		box.setLocalTranslation(new Vector3f(0, -7, 0));
		box.setRenderState(ts);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		objects.attachChild(box);

		box = new Box("box2", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
		box.setLocalTranslation(new Vector3f(15, 10, 0));
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		box.setRenderState(ts);
		objects.attachChild(box);

		box = new Box("box4", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
		box.setLocalTranslation(new Vector3f(20, 0, 0));
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		box.setRenderState(ts);
		objects.attachChild(box);

		box = new Box("box5", new Vector3f(-50, -2, -50), new Vector3f(50, 2, 50));
		box.setLocalTranslation(new Vector3f(0, -15, 0));
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		box.setRenderState(ts);
		objects.attachChild(box);

		ts = display.getRenderer().createTextureState();
		t0 = TextureManager.loadTexture(
				TestSketch.class.getClassLoader().getResource(
						"jmetest/data/texture/cloud_land.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		ts.setTexture(t0);

		box = new Box("floor", new Vector3f(-1000, -10, -1000), new Vector3f(1000, 10, 1000));
		box.setLocalTranslation(new Vector3f(0, -100, 0));
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		box.setRenderState(ts);
		objects.attachChild(box);
		
		

		return objects;
	}
}
