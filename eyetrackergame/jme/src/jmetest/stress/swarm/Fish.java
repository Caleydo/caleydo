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

package jmetest.stress.swarm;

import java.util.HashMap;
import java.util.Map;

import com.jme.bounding.BoundingSphere;
import com.jme.intersection.BoundingCollisionResults;
import com.jme.intersection.CollisionResults;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

/**
 * A 'Fish' is a single dot in the {@link TestSwarm}. It has three bahaviour rules:
 * <ol>
 * <li> If other fish are too near try to keep distance </li>
 * <li> Move to average position of all fish in sight </li>
 * <li> Try to face in the average direction of all fish in sight </li>
 * </ol>
 *
 * @author Irrisor
 * @created 21.11.2004, 12:08:27
 */
public class Fish extends Node {
    private static final long serialVersionUID = 1L;
    /**
     * speed of this fish (distance it moves each frame).
     */
    private float speed;
    /**
     * current facing.
     */
    private Vector3f orientation;
    /**
     * Sight radius of all fish.
     */
    private static final float SIGHT_RADIUS = 0.1f;
    /**
     * Portion of direction that can be changes each frame.
     */
    private static final float STEERING_SPEED = 0.3f;
    /**
     * Minimum distance that a fish tries to keep.
     */
    private static final float MINIMUM_DISTANCE = 0.02f;
    /**
     * current sum of position of fish in sight.
     */
    private Vector3f relativePositionSumInSight = new Vector3f();
    /**
     * current sum of orientation of fish in sight.
     */
    private Vector3f orientationSumInSight = new Vector3f();
    /**
     * current sum of position of fish that are too near.
     */
    private Vector3f relativePositionSumTooNear = new Vector3f();
    /**
     * number of fish that are too near.
     */
    private int fishTooNear;
    /**
     * number of fish in range of sight.
     */
    private int fishInSight;
    /**
     * visual size of a fish.
     */
    private static final float SIZE = 0.01f;
    /**
     * temporary variable to compute relative position.
     */
    private Vector3f relativePosition = new Vector3f();
    /**
     * temporary variable to compute some positions.
     */
    private Vector3f tmp_pos = new Vector3f();
    /**
     * Node where all fish are contained (may have hierarchy).
     */
    private final Node rootNode;
    /**
     * Speed of fish that are shown in green. (lower bound, continuously mapped)
     */
    private static final float SPEED_GREEN = 0.001f;
    /**
     * Speed of fish that are shown in red. (upper bound, continuously mapped)
     */
    private static final float SPEED_RED = 0.01f;
    /**
     * flag to turn off the use of collision detection, setting this to false makes fish move independently
     */
    public static boolean useCollisionDetection = true;

    /**
     * Create a new Fish.
     *
     * @param x        x-coordinate of starting position
     * @param y        y-coordinate of starting position
     * @param z        z-coordinate of starting position
     * @param dirx     starting direction (x compartment)
     * @param diry     starting direction (y compartment)
     * @param dirz     starting direction (z compartment)
     * @param speed    speed of this Fish
     * @param rootNode Node where all fish are contained (may have hierarchy).
     */
    public Fish( float x, float y, float z, float dirx, float diry, float dirz, final float speed, Node rootNode ) {
        super( "fish" );
        this.rootNode = rootNode;
        this.speed = speed;
        this.orientation = new Vector3f( dirx, diry, dirz );
        this.orientation.normalize();

        setupAppearance( speed );

        getLocalTranslation().set( x, y, z );

        //init behaviour
        addController( new Controller() {
            private static final long serialVersionUID = 1L;
            public void update( float time ) {
                process();
            }
        } );
    }

    private static Map<Float, TriMesh> visuals = new HashMap<Float, TriMesh>();

    private void setupAppearance( final float speed ) {
        float redness = ( speed - SPEED_GREEN ) / SPEED_RED;
        if ( redness > 1 ) {
            redness = 1;
        }
        else if ( redness < 0 ) {
            redness = 0;
        }
        
        TriMesh mesh = visuals.get( redness );

        if ( mesh == null )
        {
            mesh = new Box( "fish", new Vector3f(), SIZE, SIZE, SIZE );

            final MaterialState material = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
            material.setDiffuse( new ColorRGBA() );

            material.setDiffuse( new ColorRGBA( redness, 1.0f - redness, 0, 1 ) );
            mesh.setRenderState( material );

            //setup view radius
            mesh.setModelBound( new BoundingSphere( SIGHT_RADIUS / 2, new Vector3f() ) );
            visuals.put( redness , mesh );
        }
        Spatial visual = new SharedMesh( "fish visual", mesh );
        attachChild( visual );
    }

    /**
     * @return current facing of this Fish
     */
    public Vector3f getOrientation() {
        return orientation;
    }

    /**
     * list of nearby fish.
     */
    protected static CollisionResults nearbyFish = new BoundingCollisionResults();

    /**
     * Compute the position and facing for the next frame. See class description for details.
     *
     * @see Fish
     */
    public void process() {
        fishInSight = 0;
        fishTooNear = 0;
        relativePositionSumInSight.set( 0, 0, 0 );
        orientationSumInSight.set( 0, 0, 0 );
        relativePositionSumTooNear.set( 0, 0, 0 );
        nearbyFish.clear();

        if ( useCollisionDetection )
        {
            //todo: it's important what is first here - add javadoc comment!
            //this.findCollisions( rootNode, nearbyFish );
            rootNode.findCollisions( this, nearbyFish );
            view( nearbyFish );
        }

        if ( fishInSight > 0 ) {
            relativePositionSumInSight.normalizeLocal();
            orientationSumInSight.normalizeLocal();
        }

        orientation.normalizeLocal();
        if ( fishTooNear > 0 ) {
            relativePositionSumTooNear.normalizeLocal();
            relativePositionSumTooNear.multLocal( -STEERING_SPEED * 2 / 3 );
            orientation.scaleAdd( 1.0f - STEERING_SPEED, orientation, relativePositionSumTooNear );
            relativePositionSumInSight.multLocal( -STEERING_SPEED / 3 );
            orientation.addLocal( relativePositionSumInSight );
        }
        else {
            if ( fishInSight > 0 ) {
                relativePositionSumInSight.multLocal( STEERING_SPEED / 2 );
                orientation.scaleAdd( 1.0f - STEERING_SPEED, orientation, relativePositionSumInSight );
                orientationSumInSight.multLocal( STEERING_SPEED / 2 );
                orientation.addLocal( orientationSumInSight );
            }
        }

        if ( getLocalTranslation().length() > 3 ) {
            tmp_pos.set( getLocalTranslation() );
            float mul = ( -1.0f / getLocalTranslation().length() ) * ( getLocalTranslation().length() - 3 ) * STEERING_SPEED;
            if ( mul > 1 ) {
                mul = 1;
            }
            tmp_pos.multLocal( mul );
            orientation.scaleAdd( 1 - mul, tmp_pos );
        }


        //move
        orientation.normalizeLocal();
        orientation.multLocal( speed );
        getLocalTranslation().addLocal( orientation );
    }

    /**
     * Compute sums of fish in a set.
     * @see #orientationSumInSight
     * @see #relativePositionSumInSight
     * @see #relativePositionSumTooNear
     * @see #fishInSight
     * @see #fishTooNear
     * @param fishSet set of regarded fish
     */
    private void view( CollisionResults fishSet ) {
        for ( int i = fishSet.getNumber() - 1; i >= 0; i-- ) {
            final Node node = fishSet.getCollisionData( i ).getSourceMesh().getParent();
            if ( node instanceof Fish ) {
                Fish fish = (Fish) node;
                if ( fish != this ) {
                    relativePosition.set( fish.getLocalTranslation() );
                    relativePosition.subtractLocal( getLocalTranslation() );
                    final float distance = relativePosition.length();
                    if ( distance < SIGHT_RADIUS ) {
                        ++fishInSight;
                        relativePositionSumInSight.addLocal( relativePosition );
                        orientationSumInSight.addLocal( fish.getOrientation() );

                        if ( distance < MINIMUM_DISTANCE ) {
                            ++fishTooNear;
                            relativePositionSumTooNear.addLocal( relativePosition );
                        }
                    }
                }
            }
        }
    }
}
