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

package jmetest.flagrushtut.lesson9;

import jmetest.flagrushtut.Lesson2;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * ForceFieldFence creates a new Node that contains all the objects that make up the 
 * fence object in the game.
 * @author Mark Powell
 *
 */
public class ForceFieldFence extends Node {
	private static final long serialVersionUID = 1L;

    //The texture that makes up the "force field", we will keep a reference to it
    // here to allow us to animate it.
    private Texture t;
    
    /**
     * create the fence, passing the name to the parent.
     * @param name the name of the fence
     */
    public ForceFieldFence(String name) {
        super(name);
        buildFence();
    }
    
    /**
     * we add an update method to allow the texture to animate. This will
     * be called from the main games update method.
     * @param interpolation the time between frames.
     */
    public void update(float interpolation) {
        //We will use the interpolation value to keep the speed
        //of the forcefield consistent between computers.
        //we update the Y have of the texture matrix to give
        //the appearance the forcefield is moving.
        t.getTranslation().y += 0.3f * interpolation;
        //if the translation is over 1, it's wrapped, so go ahead
        //and check for this (to keep the vector's y value from getting
        //too large.)
        if(t.getTranslation().y > 1) {
            t.getTranslation().y = 0;
        }
    }
    
    /**
     * create all the fence geometry.
     *
     */
    private void buildFence() {
        //This cylinder will act as the four main posts at each corner
        Cylinder postGeometry = new Cylinder("post", 10, 10, 1, 10);
        Quaternion q = new Quaternion();
        //rotate the cylinder to be vertical
        q.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
        postGeometry.setLocalRotation(q);
        postGeometry.setModelBound(new BoundingBox());
        postGeometry.updateModelBound();
        
        //We will share the post 4 times (one for each post)
        //It is *not* a good idea to add the original geometry 
        //as the sharedmeshes will alter its local values.
        //We then translate the posts into position. 
        //Magic numbers are bad, but help illustrate the point.:)
        SharedMesh post1 = new SharedMesh("post1", postGeometry);
        post1.setLocalTranslation(new Vector3f(0,0.5f,0));
        SharedMesh post2 = new SharedMesh("post2", postGeometry);
        post2.setLocalTranslation(new Vector3f(32,0.5f,0));
        SharedMesh post3 = new SharedMesh("post3", postGeometry);
        post3.setLocalTranslation(new Vector3f(0,0.5f,32));
        SharedMesh post4 = new SharedMesh("post4", postGeometry);
        post4.setLocalTranslation(new Vector3f(32,0.5f,32));
        
        //This cylinder will be the horizontal struts that hold
        //the field in place.
        Cylinder strutGeometry = new Cylinder("strut", 10,10, 0.125f, 32);
        strutGeometry.setModelBound(new BoundingBox());
        strutGeometry.updateModelBound();
        
        //again, we'll share this mesh.
        //Some we need to rotate to connect various posts.
        SharedMesh strut1 = new SharedMesh("strut1", strutGeometry);
        Quaternion rotate90 = new Quaternion();
        rotate90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));
        strut1.setLocalRotation(rotate90);
        strut1.setLocalTranslation(new Vector3f(16,3f,0));
        SharedMesh strut2 = new SharedMesh("strut2", strutGeometry);
        strut2.setLocalTranslation(new Vector3f(0,3f,16));
        SharedMesh strut3 = new SharedMesh("strut3", strutGeometry);
        strut3.setLocalTranslation(new Vector3f(32,3f,16));
        SharedMesh strut4 = new SharedMesh("strut4", strutGeometry);
        strut4.setLocalRotation(rotate90);
        strut4.setLocalTranslation(new Vector3f(16,3f,32));
        
        //Create the actual forcefield 
        //The first box handles the X-axis, the second handles the z-axis.
        //We don't rotate the box as a demonstration on how boxes can be 
        //created differently.
        Box forceFieldX = new Box("forceFieldX", new Vector3f(-16, -3f, -0.1f), new Vector3f(16f, 3f, 0.1f));
        forceFieldX.setModelBound(new BoundingBox());
        forceFieldX.updateModelBound();
        //We are going to share these boxes as well
        SharedMesh forceFieldX1 = new SharedMesh("forceFieldX1",forceFieldX);
        forceFieldX1.setLocalTranslation(new Vector3f(16,0,0));
        SharedMesh forceFieldX2 = new SharedMesh("forceFieldX2",forceFieldX);
        forceFieldX2.setLocalTranslation(new Vector3f(16,0,32));
        
        //The other box for the Z axis
        Box forceFieldZ = new Box("forceFieldZ", new Vector3f(-0.1f, -3f, -16), new Vector3f(0.1f, 3f, 16));
        forceFieldZ.setModelBound(new BoundingBox());
        forceFieldZ.updateModelBound();
        //and again we will share it
        SharedMesh forceFieldZ1 = new SharedMesh("forceFieldZ1",forceFieldZ);
        forceFieldZ1.setLocalTranslation(new Vector3f(0,0,16));
        SharedMesh forceFieldZ2 = new SharedMesh("forceFieldZ2",forceFieldZ);
        forceFieldZ2.setLocalTranslation(new Vector3f(32,0,16));
        
        //add all the force fields to a single node and make this node part of
        //the transparent queue.
        Node forceFieldNode = new Node("forceFieldNode");
        forceFieldNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        forceFieldNode.attachChild(forceFieldX1);
        forceFieldNode.attachChild(forceFieldX2);
        forceFieldNode.attachChild(forceFieldZ1);
        forceFieldNode.attachChild(forceFieldZ2);
        
        //Add the alpha values for the transparent node
        BlendState as1 = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        as1.setEnabled(true);
        
        forceFieldNode.setRenderState(as1);
        
        //load a texture for the force field elements
        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        t = TextureManager.loadTexture(Lesson2.class.getClassLoader()
                  .getResource("jmetest/data/texture/reflector.jpg"),
                  Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        
        t.setWrap(Texture.WrapMode.Repeat);
        t.setTranslation(new Vector3f());
        ts.setTexture(t);
        
        forceFieldNode.setRenderState(ts);
        
       
        //put all the posts into a tower node
        Node towerNode = new Node("tower");
        towerNode.attachChild(post1);
        towerNode.attachChild(post2);
        towerNode.attachChild(post3);
        towerNode.attachChild(post4);
        
        //add the tower to the opaque queue (we don't want to be able to see through them)
        //and we do want to see them through the forcefield.
        towerNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        //load a texture for the towers
        TextureState ts2 = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        Texture t2 = TextureManager.loadTexture(Lesson2.class.getClassLoader()
                  .getResource("jmetest/data/texture/post.jpg"),
                  Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        
        ts2.setTexture(t2);
        
        towerNode.setRenderState(ts2);
        
        //put all the struts into a single node.
        Node strutNode = new Node("strutNode");
        strutNode.attachChild(strut1);
        strutNode.attachChild(strut2);
        strutNode.attachChild(strut3);
        strutNode.attachChild(strut4);
        //this too is in the opaque queue.
        strutNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        //load a texture for the struts
        TextureState ts3 = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        Texture t3 = TextureManager.loadTexture(Lesson2.class.getClassLoader()
                  .getResource("jmetest/data/texture/rust.jpg"),
                  Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        
        ts3.setTexture(t3);
        
        strutNode.setRenderState(ts3);
        
        //Attach all the pieces to the main fence node
        this.attachChild(forceFieldNode);
        this.attachChild(towerNode);
        this.attachChild(strutNode);
    }
}
