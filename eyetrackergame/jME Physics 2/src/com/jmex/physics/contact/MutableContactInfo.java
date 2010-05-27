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
package com.jmex.physics.contact;

import java.io.IOException;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * Helper class to specify contact details.
 *
 * @author Irrisor
 * @see ContactHandlingDetails
 */
public class MutableContactInfo implements ContactHandlingDetails {
    private boolean ignored;
    private float mu;
    private float muOrthogonal;
    private float bounce;
    private float minimumBounceVelocity;
    private Vector2f surfaceMotion;
    private Vector2f slip;
    private static Vector2f tmpVec2f = new Vector2f();
    private static Vector3f tmpVec3f = new Vector3f();
    private boolean applied = true;

    /**
     * Default ctor.
     */
    public MutableContactInfo() {
        clear();
    }

    /**
     * Copy ctor.
     *
     * @param toCopy details to copy
     */
    public MutableContactInfo( ContactHandlingDetails toCopy ) {
        this();
        copy( toCopy );
    }

    public boolean isIgnored() {
        return this.ignored;
    }

    public void setIgnored( final boolean value ) {
        this.ignored = value;
    }

    public boolean isApplied() {
        return this.applied && !this.ignored;
    }

    public void setApplied( final boolean value ) {
        this.applied = value;
    }

    public void clear() {
        mu = Float.NaN;
        bounce = Float.NaN;
        muOrthogonal = Float.NaN;
        minimumBounceVelocity = Float.NaN;
        frictionDirection.set( Float.NaN, Float.NaN, Float.NaN );
        if ( slip != null ) {
            slip.set( Float.NaN, Float.NaN );
        }
        if ( surfaceMotion != null ) {
            surfaceMotion.set( 0, 0 );
        }
    }

    /**
     * @return friction coefficient
     * @see #setMu(float)
     */
    public float getMu() {
        return mu;
    }

    /**
     * Coulomb friction coefficient. This must be in the range 0 to {@link Float#POSITIVE_INFINITY}.
     * 0 results in a frictionless contact, and infinity results in a contact that
     * never slips. Note that frictionless contacts are less time consuming to
     * compute than ones with friction, and infinite friction contacts can be
     * cheaper than contacts with finite friction. Friction must always be set.
     *
     * @param mu friction coefficient
     */
    public void setMu( float mu ) {
        this.mu = mu;
    }

    /**
     * @return othogonal friction coefficient
     * @see #setMuOrthogonal(float)
     */
    public float getMuOrthogonal() {
        return muOrthogonal;
    }

    /**
     * Optional Coulomb friction coefficient for orthogonal friction direction (0..Infinity). The direction for this
     * coefficient is orthogonal to the friction direction and the contact normal.
     *
     * @param muOrthogonal othogonal friction coefficient
     */
    public void setMuOrthogonal( float muOrthogonal ) {
        this.muOrthogonal = muOrthogonal;
    }

    /**
     * @return bounce parameter
     * @see #setBounce(float)
     */
    public float getBounce() {
        return bounce;
    }

    /**
     * Restitution parameter (0..1). 0 means the surfaces are not bouncy at all, 1 is maximum bouncyness.
     *
     * @param bounce bouncyness
     */
    public void setBounce( float bounce ) {
        this.bounce = bounce;
    }

    /**
     * @return minimum bounce velocity
     * @see #setMinimumBounceVelocity(float)
     */
    public float getMinimumBounceVelocity() {
        return minimumBounceVelocity;
    }

    /**
     * The minimum incoming velocity necessary for bounce (in m/s). Incoming velocities below this will effectively have
     * a bounce parameter of 0.
     *
     * @param minimumBounceVelocity new value
     */
    public void setMinimumBounceVelocity( float minimumBounceVelocity ) {
        this.minimumBounceVelocity = minimumBounceVelocity;
    }

    /**
     * Surface velocity - if set, the contact surface is assumed to be moving
     * independently of the motion of the nodes. This is kind of like a conveyor belt running over the surface.
     * The x component of the vector specifies motion in friction direction and the y component specifies motion
     * in direction orthogonal to friction direction and normal.
     *
     * @param motion new value, (0, 0) for no motion (not NaN)
     */
    public void setSurfaceMotion( Vector2f motion ) {
        if ( surfaceMotion == null ) {
            surfaceMotion = new Vector2f();
        }
        surfaceMotion.set( motion );
    }

    /**
     * @param store where to store the retrieved value (null to create a new vector)
     * @return surface motion
     * @see #setSurfaceMotion(com.jme.math.Vector2f)
     */
    public Vector2f getSurfaceMotion( Vector2f store ) {
        if ( store == null ) {
            store = new Vector2f();
        }
        if ( surfaceMotion != null ) {
            store.set( surfaceMotion );
        }
        else {
            store.set( 0, 0 );
        }
        return store;
    }

    /**
     * The coefficients of force-dependent-slip (FDS).
     * The x component of the vector specifies motion in friction direction and the y component specifies motion
     * in direction orthogonal to friction direction and normal.
     * @param slip slip vector
     */
    public void setSlip( Vector2f slip ) {
        if ( this.slip == null ) {
            this.slip = new Vector2f();
        }
        this.slip.set( slip );
    }

    /**
     * @param store where to store the retrieved value (null to create a new vector)
     * @return slip
     * @see #setSlip(com.jme.math.Vector2f)
     */
    public Vector2f getSlip( Vector2f store ) {
        if ( store == null ) {
            store = new Vector2f();
        }
        if ( slip != null ) {
            store.set( slip );
        }
        else {
            store.set( Float.NaN, Float.NaN );
        }
        return store;
    }

    /**
     * Copy contact details into this pending contact.
     *
     * @param details copy source
     */
    public void copy( ContactHandlingDetails details ) {
        setIgnored( details.isIgnored() );
        boolean applied = details.isApplied();
        setApplied( applied );
        if ( applied ) {
            setBounce( details.getBounce() );
            setMinimumBounceVelocity( details.getMinimumBounceVelocity() );
            setMu( details.getMu() );
            setMuOrthogonal( details.getMuOrthogonal() );
            setSlip( details.getSlip( tmpVec2f ) );
            setSurfaceMotion( details.getSurfaceMotion( tmpVec2f ) );
            setFrictionDirection( details.getFrictionDirection( tmpVec3f ) );
            setDampingCoefficient( details.getDampingCoefficient() );
            setSpringConstant( details.getSpringConstant() );
        }
    }

    private final Vector3f frictionDirection = new Vector3f();

    /*
     * @see ContactHandlingDetails#getFrictionDirection(com.jme.math.Vector3f)
     */
    public void setFrictionDirection( Vector3f direction ) {
        frictionDirection.set( direction );
    }

    public Vector3f getFrictionDirection( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return store.set( frictionDirection );
    }

    private final Vector3f secondaryFrictionDirection = new Vector3f();

    /*
     * @see ContactHandlingDetails#getSecondaryFrictionDirection(com.jme.math.Vector3f)
     */
    public void setSecondaryFrictionDirection( Vector3f direction ) {
        secondaryFrictionDirection.set( direction );
    }

    public Vector3f getSecondaryFrictionDirection( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return store.set( secondaryFrictionDirection );
    }

    private float springConstant = Float.NaN;

    public final void setSpringConstant( final float value ) {
        this.springConstant = value;
    }

    public final float getSpringConstant() {
        return this.springConstant;
    }

    private float dampingCoefficient = Float.NaN;

    public final void setDampingCoefficient( final float value ) {
        this.dampingCoefficient = value;
    }

    public final float getDampingCoefficient() {
        return this.dampingCoefficient;
    }

	public static final String IGNORED_PROPERTY = "ignored";
    public static final String MU_PROPERTY = "mu";
    public static final String MU_ORTHOGONAL_PROPERTY = "muOrthogonal";
    public static final String BOUNCE_PROPERTY = "bounce";
    public static final String MINIMUM_BOUNCE_VELOCITY_PROPERTY = "minimumBounceVelocity";
    public static final String SURFACE_MOTION_PROPERTY = "sufaceMotion";
    public static final String SLIP_PROPERTY = "slip";
    public static final String APPLIED_PROPERTY = "applied";
    public static final String FRICTION_DIRECTION_PROPERTY = "frictionDirection";
    public static final String SPRING_CONSTANT_PROPERTY = "springConstant";
    public static final String DAMPING_COEFFICIENT_PROPERTY = "dampingCoefficient";

	public Class getClassTag() {
		return MutableContactInfo.class;
	}

	public void read(JMEImporter im) throws IOException {

        InputCapsule capsule = im.getCapsule( this );
        
        setIgnored(capsule.readBoolean(IGNORED_PROPERTY, false));
        setMu(capsule.readFloat(MU_PROPERTY, Float.NaN));
        setMuOrthogonal(capsule.readFloat(MU_ORTHOGONAL_PROPERTY, Float.NaN));
        setBounce(capsule.readFloat(BOUNCE_PROPERTY, Float.NaN));
        setMinimumBounceVelocity(capsule.readFloat(MINIMUM_BOUNCE_VELOCITY_PROPERTY, Float.NaN));
        setSurfaceMotion((Vector2f) capsule.readSavable(SURFACE_MOTION_PROPERTY, null));
        setSlip((Vector2f) capsule.readSavable(SLIP_PROPERTY, null));
        setApplied(capsule.readBoolean(APPLIED_PROPERTY, true));

        Vector3f frictionDirection = (Vector3f)capsule.readSavable(FRICTION_DIRECTION_PROPERTY, Vector3f.ZERO);
        if (!Vector3f.ZERO.equals(frictionDirection)) 
        	setFrictionDirection(frictionDirection);
        
        setSpringConstant(capsule.readFloat(SPRING_CONSTANT_PROPERTY, Float.NaN));
        setDampingCoefficient(capsule.readFloat(DAMPING_COEFFICIENT_PROPERTY, Float.NaN));
	}

	public void write(JMEExporter ex) throws IOException {
		
		OutputCapsule capsule = ex.getCapsule( this );
		
		capsule.write(isIgnored(), IGNORED_PROPERTY, false);
		capsule.write(getMu(), MU_PROPERTY, Float.NaN);
		capsule.write(getMuOrthogonal(), MU_ORTHOGONAL_PROPERTY, Float.NaN);
		capsule.write(getBounce(), BOUNCE_PROPERTY, Float.NaN);
		capsule.write(getMinimumBounceVelocity(), MINIMUM_BOUNCE_VELOCITY_PROPERTY, Float.NaN);
        capsule.write(getSurfaceMotion( null ), SURFACE_MOTION_PROPERTY, null);
        capsule.write(getSlip( null ), SLIP_PROPERTY, null);
        capsule.write(isApplied(), APPLIED_PROPERTY, true);
        capsule.write(getFrictionDirection(null), FRICTION_DIRECTION_PROPERTY, Vector3f.ZERO);
        capsule.write(getSpringConstant(), SPRING_CONSTANT_PROPERTY, Float.NaN);
        capsule.write(getDampingCoefficient(), DAMPING_COEFFICIENT_PROPERTY, Float.NaN);
	}
}

/*
 * $log$
 */

