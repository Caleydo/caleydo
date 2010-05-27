/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the odejava nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava;

import com.jme.math.Vector3f;
import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_float;
import org.odejava.ode.dJointFeedback;

/**
 * Provides a container for ODE class dJointFeedback
 * <p/>
 * Created 16.02.2003 (dd.mm.yyyy)
 *
 * @author: Matt Hall
 * see http://odejava.dev.java.net
 */
public class JointFeedback {
    dJointFeedback feedback = null;

    public JointFeedback( dJointFeedback feedback ) {
        this.feedback = feedback;
    }

    public dJointFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback( dJointFeedback feedback ) {
        this.feedback = feedback;
    }

    /**
     * @return force that joint applies to body 1
     */
    public Vector3f getForce1() {
        SWIGTYPE_p_float f1 = feedback.getF1();
        if ( f1 != null ) {
            Vector3f vf1 = convertSwigPFloat( f1 );
            return vf1;
        }
        return null;
    }

    /**
     * @return force that joint applies to body 2
     */
    public Vector3f getForce2() {
        SWIGTYPE_p_float f1 = feedback.getF2();
        if ( f1 != null ) {
            Vector3f vf1 = convertSwigPFloat( f1 );
            return vf1;
        }
        return null;
    }

    /**
     * @return torque that joint applies to body 1
     */
    public Vector3f getTorque1() {
        SWIGTYPE_p_float f1 = feedback.getT1();
        if ( f1 != null ) {
            Vector3f vf1 = convertSwigPFloat( f1 );
            return vf1;
        }
        return null;
    }

    /**
     * @return torque that joint applies to body 2
     */
    public Vector3f getTorque2() {
        SWIGTYPE_p_float f1 = feedback.getT2();
        if ( f1 != null ) {
            Vector3f vf1 = convertSwigPFloat( f1 );
            return vf1;
        }
        return null;
    }

    private Vector3f convertSwigPFloat( SWIGTYPE_p_float swigtype ) {
        if ( swigtype != null ) {
            Vector3f vf1 =
                    new Vector3f(
                            Ode.floatArray_getitem( swigtype, 0 ),
                            Ode.floatArray_getitem( swigtype, 1 ),
                            Ode.floatArray_getitem( swigtype, 2 ) );
            return vf1;
        }
        return null;
    }
}
