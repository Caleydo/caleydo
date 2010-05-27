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
package org.odejava.collision;

import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_dJointGroupID;

/*
 * For colliding spaces or geoms with each other use classes NativeCollision,
 * JavaCollision or PureJavaCollision. Currently JavaCollision is preferred as
 * it allows accessing collision information in Java and it has been somewhat
 * tested.
 *
 * Created 11.02.2004 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *
 * see http://odejava.dev.java.net
 *
 */

public abstract class Collision {

    protected SWIGTYPE_p_dJointGroupID contactGroupId;

    /**
     * Flag indicating that this world has been requested to be deleted. After
     * this is set to true, none of the methods should allow further calls to
     * ODE as the values are invalid, and may well cause a crash of the library
     * or other strange error.
     */
    protected boolean deleted;

    // world is required for obtaining the contact joint group id where
    // contacts are to be stored.
    public Collision() {
        deleted = false;
        // Create contact joint group for collision routines
        contactGroupId = Ode.dJointGroupCreate( 0 );
//        Ode.setContactGroupID( contactGroupId );
    }

    public void delete() {
        if ( deleted ) {
            return;
        }

        // Delete contact joint group
        Ode.dJointGroupDestroy( contactGroupId );
        deleted = true;
    }

    /**
     * @return Returns the contactGroupId.
     */
    public SWIGTYPE_p_dJointGroupID getContactGroupId() {
        return contactGroupId;
    }

    public void emptyContactGroup() {
        if ( !deleted ) {
            Ode.dJointGroupEmpty( contactGroupId );
        }
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceBounce( float bounce ) {
        Ode.setSurfaceBounce( bounce );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceBounceVel( float bounceVel ) {
        Ode.setSurfaceBounceVel( bounceVel );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceMode( int mode ) {
        Ode.setSurfaceMode( mode );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceMotion1( float motion1 ) {
        Ode.setSurfaceMotion1( motion1 );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceMotion2( float motion2 ) {
        Ode.setSurfaceMotion2( motion2 );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceMu( float mu ) {
        Ode.setSurfaceMu( mu );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceMu2( float mu2 ) {
        Ode.setSurfaceMu2( mu2 );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceSlip1( float slip1 ) {
        Ode.setSurfaceSlip1( slip1 );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceSlip2( float slip2 ) {
        Ode.setSurfaceSlip2( slip2 );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceSoftCfm( float softCfm ) {
        Ode.setSurfaceSoftCfm( softCfm );
    }

    /**
     * Set default value for collision surface parameters.
     */
    public void setSurfaceSoftErp( float softErp ) {
        Ode.setSurfaceSoftErp( softErp );
    }
}
