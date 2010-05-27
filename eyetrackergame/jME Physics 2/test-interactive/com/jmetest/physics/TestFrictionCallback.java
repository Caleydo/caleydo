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
import com.jme.util.TextureManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.callback.FrictionCallback;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * @author Matthew D. Hicks
 *
 */
public class TestFrictionCallback {
	public static void main(String[] args) {
		StandardGame game = new StandardGame("Test FrictionCallback");
		game.getSettings().setVerticalSync(false);		// We want to see what FPS we're running at
		game.start();

		// Create a DebugGameState to give us the toys we want
		DebugGameState debug = new DebugGameState();
		debug.setActive(true);
		GameStateManager.getInstance().attachChild(debug);

		// Create our GameState
		PhysicsGameState physics = new PhysicsGameState("PhysicsState");
		physics.getPhysicsSpace().setAutoRestThreshold(0.5f);
		GameStateManager.getInstance().attachChild(physics);
		physics.getRootNode().setCullHint(CullHint.Never);
		physics.setActive(true);

		// Create our Boxes
		TextureState ts = game.getDisplay().getRenderer().createTextureState();
	    Texture t = TextureManager.loadTexture(TestStressPhysics.class.getClassLoader().getResource("com/jmetest/physics/resources/crate.png"), MinificationFilter.NearestNeighborLinearMipMap, MagnificationFilter.NearestNeighbor);
	    t.setWrap(WrapMode.Repeat);
	    ts.setTexture(t);
	    game.lock();
    	Box box1 = new Box("BoxFriction", new Vector3f(), 5.0f, 5.0f, 5.0f);
    	box1.setModelBound(new BoundingBox());
    	box1.updateModelBound();
    	box1.setRenderState(ts);
		DynamicPhysicsNode node1 = physics.getPhysicsSpace().createDynamicNode();
		node1.setAffectedByGravity(false);
		node1.getLocalTranslation().set(-20.0f, 20.0f, -150.0f);
		node1.attachChild(box1);
		node1.generatePhysicsGeometry();
		node1.updateGeometricState(0.0f, true);
		physics.getRootNode().attachChild(node1);
		
    	Box box2 = new Box("BoxFrictionless", new Vector3f(), 5.0f, 5.0f, 5.0f);
    	box2.setModelBound(new BoundingBox());
    	box2.updateModelBound();
    	box2.setRenderState(ts);
		DynamicPhysicsNode node2 = physics.getPhysicsSpace().createDynamicNode();
		node2.setAffectedByGravity(false);
		node2.getLocalTranslation().set(20.0f, 20.0f, -150.0f);
		node2.attachChild(box2);
		node2.generatePhysicsGeometry();
		node2.updateGeometricState(0.0f, true);
		physics.getRootNode().attachChild(node2);
		
    	Box box3 = new Box("BoxFriction2", new Vector3f(), 5.0f, 5.0f, 5.0f);
    	box3.setModelBound(new BoundingBox());
    	box3.updateModelBound();
    	box3.setRenderState(ts);
		DynamicPhysicsNode node3 = physics.getPhysicsSpace().createDynamicNode();
		node3.setAffectedByGravity(false);
		node3.getLocalTranslation().set(-20.0f, -20.0f, -40.0f);
		node3.attachChild(box3);
		node3.generatePhysicsGeometry();
		node3.updateGeometricState(0.0f, true);
		physics.getRootNode().attachChild(node3);
		
    	Box box4 = new Box("BoxFrictionless2", new Vector3f(), 5.0f, 5.0f, 5.0f);
    	box4.setModelBound(new BoundingBox());
    	box4.updateModelBound();
    	box4.setRenderState(ts);
		DynamicPhysicsNode node4 = physics.getPhysicsSpace().createDynamicNode();
		node4.setAffectedByGravity(false);
		node4.getLocalTranslation().set(20.0f, -20.0f, -40.0f);
		node4.attachChild(box4);
		node4.generatePhysicsGeometry();
		node4.updateGeometricState(0.0f, true);
		physics.getRootNode().attachChild(node4);
		
		physics.getRootNode().updateRenderState();
		
		// Create Friction on the first box
		FrictionCallback callback = new FrictionCallback();
		callback.add(node1, 0.5f, 0.0f);
		callback.add(node3, 0.0f, 0.5f);
		physics.getPhysicsSpace().addToUpdateCallbacks(callback);
		
		// Apply forces to both
		node1.addForce(new Vector3f(0.0f, 0.0f, 1000.0f));
		node2.addForce(new Vector3f(0.0f, 0.0f, 1000.0f));
		
		node3.setAngularVelocity(new Vector3f(0.0f, 5.0f, 0.0f));
		node4.setAngularVelocity(new Vector3f(0.0f, 5.0f, 0.0f));
		
		game.unlock();
	}
}
