/*
 * Copyright (c) 2005-2006 jME Physics 2
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

package com.jmex.physics.impl.ode;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.export.JMEImporter;
import com.jmex.physics.*;
import com.jmex.physics.impl.ode.geometry.OdeGeometry;
import org.odejava.Body;
import org.odejava.GeomTransform;
import org.odejava.PlaceableGeom;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Irrisor
 */
public class DynamicPhysicsNodeImpl extends DynamicPhysicsNode implements OdePhysicsNode {

    private final Body body;

    public Body getBody() {
        return body;
    }

    /**
     * Temp variables to flatline memory usage.
     */
    private final static Vector3f odePos = new Vector3f();

    /**
     * Temp variables to flatline memory usage.
     */
    private final static Quaternion odeRot = new Quaternion();

    public DynamicPhysicsNodeImpl( OdePhysicsSpace space ) {
        this.space = space;
        body = space.createBody( getName() );
    }

    @Override
    public void setName( String name ) {
        super.setName( name );
        body.setName( name );
    }

    @Override
    public PhysicsSpace getSpace() {
        return space;
    }

    private final OdePhysicsSpace space;

    public OdeCollisionGroup getCollisionGroup() {
        return (OdeCollisionGroup) super.getCollisionGroup();
    }

    private void addGeom( PlaceableGeom geom ) {
        GeomTransform transform = new GeomTransform( geom.getName() );
        transform.setEncapsulatedGeom( geom );
        geom.setPhysicsObject( this );
        transform.setPhysicsObject( this );
        transform.setGeometry( geom.getGeometry() );
        body.addGeom( transform );
        if ( isActive() ) {
            ( (OdePhysicsSpace) getSpace() ).addGeom( transform, getCollisionGroup() );
        }
    }

    private void removeGeom( PlaceableGeom geom ) {
        for ( int i = body.getGeoms().size() - 1; i >= 0; i-- ) {
            GeomTransform transform = (GeomTransform) body.getGeoms().get( i );
            if ( transform.getEncapsulatedGeom() == geom ) {
                body.removeGeom( transform );
                if ( isActive() ) {
                    ( (OdePhysicsSpace) getSpace() ).removeGeom( transform, getCollisionGroup() );
                }
                break;
            }
        }
    }

    @Override
    public boolean setActive( boolean value ) {
        boolean changed = super.setActive( value );
        if ( changed ) {
            final OdePhysicsSpace odePhysicsSpace = (OdePhysicsSpace) getSpace();
            if ( value ) {
                unrest();
                for ( int i = body.getGeoms().size() - 1; i >= 0; i-- ) {
                    PlaceableGeom geom = body.getGeoms().get( i );
                    odePhysicsSpace.addGeom( geom, getCollisionGroup() );
                }
            } else {
                rest();
                for ( int i = body.getGeoms().size() - 1; i >= 0; i-- ) {
                    PlaceableGeom geom = body.getGeoms().get( i );
                    odePhysicsSpace.removeGeom( geom, getCollisionGroup() );
                }
            }
        }
        return changed;
    }

    public void setCollisionGroup( CollisionGroup value ) {
        boolean wasActive = isActive();
        setActive( false );
        super.setCollisionGroup( value );
        setActive( wasActive );
    }

    @Override
    public int attachChild( final Spatial child ) {
        Node oldParent = child != null ? child.getParent() : null;
        int index = super.attachChild( child );
        if ( child instanceof OdeGeometry ) {
            OdeGeometry odeGeometry = (OdeGeometry) child;
            if ( oldParent != this ) {
                if ( odeGeometry.getOdeGeom() instanceof PlaceableGeom )
                {
                    addGeom( (PlaceableGeom) odeGeometry.getOdeGeom() );
                }
                else
                {
                    getSpace().addToUpdateCallbacks( new PhysicsUpdateCallback() {
                        public void beforeStep( PhysicsSpace space, float time ) {
                            sceneFromOde();
                            updateWorldVectors();
                            child.updateWorldVectors();
                        }

                        public void afterStep( PhysicsSpace space, float time ) {

                        }
                    } );
                    Logger.getLogger( PhysicsSpace.LOGGER_NAME ).warning(
                            "This implementation does not support efficient computations" +
                                    "this kind of geometry in dynamic nodes: " + odeGeometry.getClass().getName()
                    );
                }
            }
        }
        return index;
    }

    @Override
    public int detachChild( Spatial child ) {
        Node oldParent = child != null ? child.getParent() : null;
        int index = super.detachChild( child );
        if ( child instanceof OdeGeometry ) {
            OdeGeometry odeGeometry = (OdeGeometry) child;
            if ( oldParent == this ) {
                if ( odeGeometry.getOdeGeom() instanceof PlaceableGeom ) {
                    removeGeom( (PlaceableGeom) odeGeometry.getOdeGeom() );
                } else {
                    //todo
                }
            }
        }
        return index;
    }

    private static final Vector3f tmpPosition = new Vector3f();

    public void updateTransforms( PlaceableGeom geom ) {
        PhysicsCollisionGeometry geometry = geom.getGeometry();
        geom.setPosition( tmpPosition.set( geometry.getLocalTranslation() )
                .subtractLocal( centerOfMass )
                .multLocal( getWorldScale() )
        );
        geom.setQuaternion( geometry.getLocalRotation() );
    }

    @Override
    public void updateWorldVectors() {
        super.updateWorldVectors();
        //TODO: only if necessary!
        sceneToOde();
    }

//    private boolean odeEnabled = true;

    private void sceneToOde() {
        if ( !checkODESceneSync() )
        {
            body.setEnabled( true );
            enabled = true;
            body.setPosition( localToWorld( centerOfMass, tmpPosition ) );
            body.setQuaternion( getWorldRotation() );
        }
        else
        {
            // maybe enabled state changed
            enabled = null;
        }
    }

    private boolean checkODESceneSync() {
        body.getPosition( odePos );
        body.getQuaternion( odeRot );
        //todo: is this too expensive to be worth it? (localToWorld vs. worldToLocal)
        boolean odeAndSceneInSync = getWorldRotation().equals( odeRot )
                && localToWorld( centerOfMass, tmpPosition ).equals( odePos );
        return odeAndSceneInSync;
    }

    public void sceneFromOde() {
        if ( !checkODESceneSync() )
        {
            if ( Float.isNaN( odeRot.x ) || Float.isNaN( odeRot.y ) || Float.isNaN( odeRot.z ) || Float.isNaN( odeRot.w )
                    || Float.isNaN( odePos.x ) || Float.isNaN( odePos.y ) || Float.isNaN( odePos.z ) )
            {
                Logger.getLogger( PhysicsSpace.LOGGER_NAME ).warning( "ODE transform result was NaN for node " + this
                        + " trying to recover..." );
                // with this position something went really wrong - move it a little bit
                System.err.println( getLocalTranslation() );
                System.err.println( getLocalRotation() );
                getLocalTranslation().x *= 1.0001f;
                getLocalTranslation().y *= 1.0001f;
                getLocalTranslation().z *= 1.0001f;
                // most likely velocities and/or forces are broken as well - reset them
                clearDynamics();
                return;
            }
            space.setLocalRotationFromWorldRotation( this, odeRot );
            // odePos is equivalent to the world position of the center of mass
            // thus the local translation still needs to be computed from that
            worldRotation.set( odeRot );
            worldTranslation.set( 0, 0, 0 );
            localToWorld( centerOfMass, tmpPosition );
            tmpPosition.multLocal( -1 ).addLocal( odePos );
            space.worldToLocal( this, tmpPosition, getLocalTranslation() );
        }
    }


    @Override
    public void addForce( Vector3f force, Vector3f at ) {
        body.setEnabled( true );
        if ( at != null ) {
            body.addForceAtRelPos( force.x, force.y, force.z, at.x, at.y, at.z );
        } else {
            body.addForce( force );
        }
    }

    @Override
    public void addTorque( Vector3f torque ) {
        body.setEnabled( true );
        body.addTorque( torque );
    }

    @Override
    public void clearForce() {
        body.setForce( 0, 0, 0 );
    }

    @Override
    public void clearTorque() {
        body.setTorque( 0, 0, 0 );
    }

    @Override
    public Vector3f getAngularVelocity( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return body.getAngularVel( store );
    }

    @Override
    public Vector3f getForce( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return body.getForce( store );
    }

    @Override
    public Vector3f getLinearVelocity( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return body.getLinearVel( store );
    }

    @Override
    public float getMass() {
        return body.getMass();
    }

    @Override
    public Vector3f getTorque( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return body.getTorque( store );
    }

    @Override
    public boolean isAffectedByGravity() {
        return body.getGravityMode() == 1;
    }

    @Override
    public void setAffectedByGravity( final boolean value ) {
        body.setGravityMode( value ? 1 : 0 );
    }

    @Override
    public void setAngularVelocity( Vector3f velocity ) {
        body.setAngularVel( velocity );
    }

    private final Vector3f centerOfMass = new Vector3f();

    @Override
    public Vector3f getCenterOfMass( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return store.set( centerOfMass );
    }

    @Override
    public void setCenterOfMass( final Vector3f value ) {
        float oldMass = getMass();
        if ( oldMass == 0 ) {
            throw new IllegalStateException( "Current mass is 0 - that's not a valid mass!" );
        }
        centerOfMass.set( value );
        updateBodyAndGeomPositions();
        setMass( oldMass );
    }

    @Override
    public void computeMass() {
        List geoms = body.getGeoms();
        if ( geoms.size() == 0 ) {
            throw new IllegalStateException( "no collision geometries have been added to this node yet - " +
                    "cannot compute mass!" );
        }
        updateGeometricState( 0, true ); // update geom positions
        centerOfMass.set( 0, 0, 0 );
        body.clearMass();
        float mass = 0;
        for ( int i1 = geoms.size() - 1; i1 >= 0; i1-- ) {
            PlaceableGeom geom = (PlaceableGeom) geoms.get( i1 );
            updateTransforms( geom );
            PhysicsCollisionGeometry geometry = geom.getGeometry();
            float density = geometry.getMaterial().getDensity();
            body.addDefaultMass( geom, density );
            mass += geometry.getVolume() * density;
        }
        body.getCenterOfMass( centerOfMass );
        if ( !centerOfMass.equals( Vector3f.ZERO ) )
        {
            updateBodyAndGeomPositions();
        }
        body.setCenterOfMass( 0, 0, 0 );
        body.applyMass();
        setMass( mass );
    }

    private void updateBodyAndGeomPositions() {
        sceneToOde();
        final List<PlaceableGeom> geoms = body.getGeoms();
        for ( int i1 = geoms.size() - 1; i1 >= 0; i1-- ) {
            PlaceableGeom geom = geoms.get( i1 );
            updateTransforms( geom );
        }
    }

    public void rest() {
        updateGeometricState( 0, true );
        body.setEnabled( false );
    }

    public void unrest() {
        body.setEnabled( true );
        updateGeometricState( 0, true );
    }

    private Boolean enabled;

    public boolean isResting() {
        if ( enabled == null )
        {
            enabled = body.isEnabled();
        }
        return !enabled;
    }

    @Override
    public void setLinearVelocity( Vector3f velocity ) {
        body.setLinearVel( velocity );
    }

    @Override
    public void setMass( float value ) {
        if ( value <= 0 ) {
            throw new IllegalArgumentException( "mass cannot be <= 0" );
        }
        body.adjustMass( value );
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        space.deleteBody( body );
    }

	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);

		float mass = getMass();
		computeMass();
		setMass(mass);
	}
}

/*
* $log$
*/
