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
import com.jmex.physics.RotationalJointAxis;
import org.odejava.Body;
import org.odejava.Joint;
import org.odejava.ode.OdeConstants;

/**
 * @author Irrisor
 */
public class HingeJointAxis2 extends RotationalJointAxis {
    private final Joint ode;

    public HingeJointAxis2( JointAxis toCopy, Joint hinge ) {
        this.ode = hinge;
        copy( toCopy );
    }

    @Override
    public void setDirection( Vector3f direction ) {
        float length = direction.length();
        if ( length == 0 ) {
            throw new IllegalArgumentException( "Axis direction may not be zero!" );
        }
        ode.setAxis2( direction.x, direction.y, direction.z );
        super.setDirection( direction );
    }

    @Override
    public float getVelocity() {
        return ode.getAngle2Rate();
    }

    @Override
    public float getPosition() {
        return ode.getAngle2();
    }

    @Override
    public void setAvailableAcceleration( float value ) {
        if ( Float.isNaN( value ) ) {
            value = 0;
        }
        ode.setParam( OdeConstants.dParamFMax2, value );
        enableBodies();
    }

    private void enableBodies() {
        final Body body1 = ode.getBody1();
        if ( body1 != null ) body1.setEnabled( true );
        final Body body2 = ode.getBody2();
        if ( body2 != null ) body2.setEnabled( true );
    }

    @Override
    public void setDesiredVelocity( float value ) {
        ode.setParam( OdeConstants.dParamVel2, value );
        enableBodies();
    }

    @Override
    public float getAvailableAcceleration() {
        return ode.getParam( OdeConstants.dParamFMax2 );
    }

    @Override
    public float getDesiredVelocity() {
        return ode.getParam( OdeConstants.dParamVel2 );
    }

    @Override
    public float getPositionMaximum() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float getPositionMinimum() {
        return Float.NEGATIVE_INFINITY;
    }

    @Override
    public void setPositionMaximum( float value ) {
        if ( !Float.isInfinite( value ) ) {
            throw new UnsupportedOperationException( "second axis cannot be restricted by this implementation" );
        }
    }

    @Override
    public void setPositionMinimum( float value ) {
        if ( !Float.isInfinite( value ) ) {
            throw new UnsupportedOperationException( "second axis cannot be restricted by this implementation" );
        }
    }
}

/*
 * $log$
 */

