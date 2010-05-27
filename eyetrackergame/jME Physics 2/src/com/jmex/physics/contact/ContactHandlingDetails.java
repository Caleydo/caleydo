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

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.util.export.Savable;

/**
 * Interface for contact handling detail like friction, bouciness, etc.
 *
 * @author Irrisor
 */
public interface ContactHandlingDetails extends Savable {
    /**
     * @return true if the contact should be completely ignored (no collision, no events)
     * @see #isApplied()
     */
    boolean isIgnored();

    /**
     * @return true if this contact should be applied to the physics simulation (default), false to generate events only
     * @see #isIgnored()
     */
    boolean isApplied();

    /**
     * @return friction coefficient
     * @see MutableContactInfo#setMu(float)
     */
    float getMu();

    /**
     * @return othogonal friction coefficient
     * @see MutableContactInfo#setMuOrthogonal(float)
     */
    float getMuOrthogonal();

    /**
     * @return bounce parameter
     * @see MutableContactInfo#setBounce(float)
     */
    float getBounce();

    /**
     * @return minimum bounce velocity
     * @see MutableContactInfo#setMinimumBounceVelocity(float)
     */
    float getMinimumBounceVelocity();

    /**
     * @param store where to store the retrieved value (null to create a new vector)
     * @return surface motion
     * @see MutableContactInfo#setSurfaceMotion(com.jme.math.Vector2f)
     */
    Vector2f getSurfaceMotion( Vector2f store );

    /**
     * @param store where to store the retrieved value (null to create a new vector)
     * @return slip
     * @see MutableContactInfo#setSlip(com.jme.math.Vector2f)
     */
    Vector2f getSlip( Vector2f store );

    /**
     * Query the direction for the friction - TODO: remove this?
     *
     * @param store where to put the value, null to create a new Vector
     * @return store
     */
    public Vector3f getFrictionDirection( Vector3f store );

    /**
     * Query the direction for the secondary friction - TODO: remove this?
     *
     * @param store where to put the value, null to create a new Vector
     * @return store
     */
    public Vector3f getSecondaryFrictionDirection( Vector3f store );

    /**
     * If spring constant and damping coefficient are not NaN they make the contact 'softer' like a spring.
     * @see #getDampingCoefficient()
     * @return the spring constant used for this contact, NaN if not used
     */
    float getSpringConstant();

    /**
     * If spring constant and damping coefficient are not NaN they make the contact 'softer' like a spring.
     * @see #getSpringConstant()
     * @return the damping coefficient used for this contact, NaN if not used
     */
    float getDampingCoefficient();
}

/*
 * $log$
 */
