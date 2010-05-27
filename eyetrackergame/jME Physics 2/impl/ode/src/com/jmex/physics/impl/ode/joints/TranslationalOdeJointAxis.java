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
package com.jmex.physics.impl.ode.joints;

import com.jme.math.Vector3f;
import com.jmex.physics.JointAxis;
import com.jmex.physics.TranslationalJointAxis;

/**
 * @author Irrisor
 */
public class TranslationalOdeJointAxis extends TranslationalJointAxis implements OdeJointAxis {

    private JointAxis delegate;

    public void setDelegate( JointAxis delegate ) {
        this.delegate = delegate;
    }

    @Override
    public float getPosition() {
        if ( delegate != null ) {
            return delegate.getPosition();
        }
        else {
            return Float.NaN;
        }
    }

    @Override
    public float getVelocity() {
        if ( delegate != null ) {
            return delegate.getVelocity();
        }
        else {
            return Float.NaN;
        }
    }

    @Override
    public void setDirection( Vector3f direction ) {
        super.setDirection( direction );
        if ( delegate != null ) {
            delegate.setDirection( direction );
        }
    }

    private float availableAcceleration = 0;
    private float desiredVelocity = 0;

    @Override
    public void setAvailableAcceleration( float value ) {
        availableAcceleration = value;
        if ( delegate != null ) {
            delegate.setAvailableAcceleration( value );
        }
    }

    @Override
    public float getAvailableAcceleration() {
        return availableAcceleration;
    }

    @Override
    public void setDesiredVelocity( float value ) {
        desiredVelocity = value;
        if ( delegate != null ) {
            delegate.setDesiredVelocity( value );
        }
    }

    @Override
    public float getDesiredVelocity() {
        return desiredVelocity;
    }

    private float min = Float.NEGATIVE_INFINITY;
    private float max = Float.POSITIVE_INFINITY;

    @Override
    public float getPositionMaximum() {
        return max;
    }

    @Override
    public float getPositionMinimum() {
        return min;
    }

    @Override
    public void setPositionMaximum( float value ) {
        max = value;
        if ( delegate != null ) {
            delegate.setPositionMaximum( value );
        }
    }

    @Override
    public void setPositionMinimum( float value ) {
        min = value;
        if ( delegate != null ) {
            delegate.setPositionMinimum( value );
        }
    }
}

/*
 * $log$
 */

