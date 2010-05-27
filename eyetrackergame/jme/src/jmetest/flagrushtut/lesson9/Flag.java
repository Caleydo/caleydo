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

import jmetest.effects.cloth.TestCloth;

import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.math.spring.SpringPoint;
import com.jme.math.spring.SpringPointForce;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.cloth.ClothPatch;
import com.jmex.effects.cloth.ClothUtils;
import com.jmex.terrain.TerrainBlock;

/**
 * Flag maintains the object that is the "goal" of the game. The
 * drivers are to try to grab the flags for points. The main job of 
 * the class is to build the flag geometry, and position itself randomly
 * within the level after a period of time.
 * @author Mark Powell
 *
 */
public class Flag extends Node{
	private static final long serialVersionUID = 1L;

    //10 second life time
    private static final int LIFE_TIME = 10;
    //start off with a full life time
    float countdown = LIFE_TIME;
    //reference to the level terrain for placement
    TerrainBlock tb;
    //the cloth that makes up the flag.
    private ClothPatch cloth;
    //parameters for the wind
    private float windStrength = 15f;
    private Vector3f windDirection = new Vector3f(0.8f, 0, 0.2f);
    private SpringPointForce gravity, wind;
    
    /**
     * Constructor builds the flag, taking the terrain as the parameter. This
     * is just the reference to the game's terrain object so that we can 
     * randomly place this flag on the level.
     * @param tb the terrain used to place the flag.
     */
    public Flag(TerrainBlock tb) {
        super("flag");
        this.tb = tb;
        //create a cloth patch that will handle the flag part of our flag.
        cloth = new ClothPatch("cloth", 25, 25, 1f, 10);
        // Add our custom flag wind force to the cloth
        wind = new RandomFlagWindForce(windStrength, windDirection);
        cloth.addForce(wind);
        // Add a simple gravitational force:
        gravity = ClothUtils.createBasicGravity();
        cloth.addForce(gravity);
        
        //Create the flag pole
        Cylinder c = new Cylinder("pole", 10, 10, 0.5f, 50 );
        this.attachChild(c);
        Quaternion q = new Quaternion();
        //rotate the cylinder to be vertical
        q.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
        c.setLocalRotation(q);
        c.setLocalTranslation(new Vector3f(-12.5f,-12.5f,0));

        //create a texture that the flag will display.
        //Let's promote jME! 
        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        ts.setTexture(
            TextureManager.loadTexture(
            TestCloth.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MinificationFilter.Trilinear,
            Texture.MagnificationFilter.Bilinear));
        
        //We'll use a LightNode to give more lighting to the flag, we use the node because
        //it will allow it to move with the flag as it hops around.
        //first create the light
        PointLight dr = new PointLight();
        dr.setEnabled( true );
        dr.setDiffuse( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
        dr.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        dr.setLocation( new Vector3f( 0.5f, -0.5f, 0 ) );
        //next the state
        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        lightState.setTwoSidedLighting( true );
        lightState.attach(dr);
        //last the node
        LightNode lightNode = new LightNode( "light" );
        lightNode.setLight( dr );
        lightNode.setLocalTranslation(new Vector3f(15,10,0));

        this.setRenderState(lightState);
        this.attachChild(lightNode);
        
        cloth.setRenderState(ts);
        //We want to see both sides of the flag, so we will turn back facing culling OFF.
        CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setCullFace(CullState.Face.None);
        cloth.setRenderState(cs);
        this.attachChild(cloth);
        
        //We need to attach a few points of the cloth to the poll. These points shouldn't
        //ever move. So, we'll attach five points at the top and 5 at the bottom. 
        //to make them not move the mass has to be high enough that no force can move it.
        //I also move the position of these points slightly to help bunch up the flag to
        //give it better realism.
        for (int i = 0; i < 5; i++) {
            cloth.getSystem().getNode(i*25).position.y *= .8f;
            cloth.getSystem().getNode(i*25).setMass(Float.POSITIVE_INFINITY);
            
        }
        
        for (int i = 24; i > 19; i--) {
            cloth.getSystem().getNode(i*25).position.y *= .8f;
            cloth.getSystem().getNode(i*25).setMass(Float.POSITIVE_INFINITY);
            
        }
        this.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        this.setLocalScale(0.25f);
        
    }
    
    /**
     * During the update, we decrement the time. When it reaches zero, we will
     * reset the flag.
     * @param time the time between frame.
     */
    public void update(float time) {
        countdown -= time;
        
        if(countdown <= 0) {
            reset();
        }
    }
    
    /**
     * reset sets the life time back to 10 seconds, and then randomly places the flag
     * on the terrain.
     *
     */
    public void reset() {
        countdown = LIFE_TIME;
        placeFlag();
    }
    
    /**
     * place flag picks a random point on the terrain and places the flag there. I
     * set the values to be between (45 and 175) which places it within the force field
     * level.
     *
     */
    public void placeFlag() {
        float x = 45 + FastMath.nextRandomFloat() * 130;
        float z = 45 + FastMath.nextRandomFloat() * 130;
        float y = tb.getHeight(x,z) + 7.5f;
        localTranslation.x = x;
        localTranslation.y = y;
        localTranslation.z = z;
        
    }
    
    /**
     * RandomFlagWindForce defines a SpringPointForce that will slighly adjust the
     * direction of the wind and the force of the wind. This will cause the flag
     * to flap in the wind and rotate about the flag pole slightly, giving it a
     * realistic movement.
     * @author Mark Powell
     *
     */
    private class RandomFlagWindForce extends SpringPointForce{
        
        private final float strength;
        private final Vector3f windDirection;

        /**
         * Creates a new force with a defined max strength and a starting direction.
         * @param strength the maximum strength of the wind.
         * @param direction the starting direction of the wind.
         */
        public RandomFlagWindForce(float strength, Vector3f direction) {
            this.strength = strength;
            this.windDirection = direction;
        }
        
        /**
         * called during the update of the cloth. Will adjust the direction slightly
         * and adjust the strength slightly.
         */
        public void apply(float dt, SpringPoint node) {
            windDirection.x += dt * (FastMath.nextRandomFloat() - 0.5f);
            windDirection.z += dt * (FastMath.nextRandomFloat() - 0.5f);
            windDirection.normalize();
            float tStr = FastMath.nextRandomFloat() * strength;
            node.acceleration.addLocal(windDirection.x * tStr, windDirection.y
                    * tStr, windDirection.z * tStr);
        }
    };

}
