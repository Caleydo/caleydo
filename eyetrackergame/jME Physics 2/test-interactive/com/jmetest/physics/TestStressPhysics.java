/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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

package com.jmetest.physics;

import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * @author Matthew D. Hicks
 */
public class TestStressPhysics {
	public static void main(String[] args) throws Exception {
        System.setProperty("jme.stats", "set");
        long time = System.currentTimeMillis();

		StandardGame game = new StandardGame("Stress Physics");
        game.getSettings().setWidth( 1024 );
        game.getSettings().setHeight( 768 );
        game.getSettings().setVerticalSync(false);		// We want to see what FPS we're running at
		game.start();

		Thread.sleep(2000);

		// Move the camera
		game.getCamera().setLocation(new Vector3f(0.0f, 250.0f, -600.0f));
		game.getCamera().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));

		// Create a DebugGameState to give us the toys we want
//		DebugGameState debug = new DebugGameState();
//		debug.setActive(true);
//		GameStateManager.getInstance().attachChild(debug);

		// Create our GameState
//		PhysicsMultithreadedGameState physics = new PhysicsMultithreadedGameState("PhysicsState");
		PhysicsGameState physics = new PhysicsGameState("PhysicsState");
		physics.getPhysicsSpace().setAutoRestThreshold(0.05f);
		GameStateManager.getInstance().attachChild(physics);
		physics.getRootNode().setCullHint(CullHint.Never);
		physics.setActive(true);

//        physics.lock();
		// Create the floor
		Box visualFloor = new Box("Floor", new Vector3f(), 500.0f, 0.5f, 500.0f);
	    visualFloor.getLocalTranslation().set(0.0f, -25.0f, 0.0f);
	    TextureState wallTextureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        wallTextureState.setTexture(TextureManager.loadTexture(TestStressPhysics.class.getClassLoader().getResource("com/jmetest/physics/resources/floor.png"), MinificationFilter.Trilinear, MagnificationFilter.Bilinear));
        visualFloor.setRenderState(wallTextureState);
	    // Create the physics for the floor
        StaticPhysicsNode floor = physics.getPhysicsSpace().createStaticNode();
        floor.attachChild(visualFloor);
        floor.generatePhysicsGeometry();
        physics.getRootNode().attachChild(floor);
        physics.getRootNode().updateRenderState();
//        physics.unlock();

        // Create falling boxes
        TextureState ts = game.getDisplay().getRenderer().createTextureState();
	    Texture t = TextureManager.loadTexture(TestStressPhysics.class.getClassLoader().getResource("com/jmetest/physics/resources/crate.png"), MinificationFilter.Trilinear, MagnificationFilter.Bilinear);
	    t.setWrap( WrapMode.Repeat );
	    ts.setTexture(t);
        int boxes = 500;
        for (int i = 0; i < boxes; i++) {
        	game.lock();
        	Box box = new Box("Box" + i, new Vector3f(), 5.0f, 5.0f, 5.0f);
        	box.setModelBound(new BoundingBox());
        	box.updateModelBound();
        	box.setRenderState(ts);
    		DynamicPhysicsNode node = physics.getPhysicsSpace().createDynamicNode();
    		node.attachChild(box);
    		node.generatePhysicsGeometry();
    		node.setLocalTranslation((float)(Math.random() * 500.0f) - 250.0f, (float)(5.0f + (Math.random() * 100.0f)), (float)(Math.random() * 500.0f) - 250.0f);
//    		physics.lock();
    		node.updateGeometricState(0.0f, true);
    		physics.getRootNode().attachChild(node);
//    		physics.unlock();
    		physics.getRootNode().updateRenderState();
    		game.unlock();
//    		Thread.sleep(50);
        }

        Thread.sleep(50000);
        List<Spatial> list = new ArrayList<Spatial>();
        list.addAll(physics.getRootNode().getChildren());
        for (Spatial s : list) {
        	if (s instanceof DynamicPhysicsNode) {
        		game.lock();
        		((DynamicPhysicsNode)s).setActive(false);
        		s.removeFromParent();
        		game.unlock();
        	}
        }

        System.out.println("Entire process took: " + (System.currentTimeMillis() - time) + "ms");
	}
}