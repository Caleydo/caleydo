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

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;

/**
 * Instances of this class are used to query contact details with the {@link ContactCallback} interface.
 * See all setter methods for details.
 *
 * @author Irrisor
 * @see ContactCallback
 */
public abstract class PendingContact extends MutableContactInfo implements CompleteContactInfo {

    private static final Vector3f tmpVelocity = new Vector3f();

    public Vector3f getContactVelocity( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        computeContactVelocity( this, store );
        return store;
    }

    public static void computeContactVelocity( ContactInfo pendingContact, Vector3f store ) {
        Vector3f actualBounceVel = store.set( 0, 0, 0 );
        if ( pendingContact.getNode1() instanceof DynamicPhysicsNode ) {
            actualBounceVel.subtractLocal( ( (DynamicPhysicsNode) pendingContact.getNode1() )
                    .getLinearVelocity( tmpVelocity ) );
        }
        if ( pendingContact.getNode2() instanceof DynamicPhysicsNode ) {
            actualBounceVel.addLocal( ( (DynamicPhysicsNode) pendingContact.getNode2() )
                    .getLinearVelocity( tmpVelocity ) );
        }
        pendingContact.getContactNormal( tmpVelocity );
        float lengthSqr = actualBounceVel.dot( tmpVelocity );
        final float length = FastMath.sqrt( Math.abs(lengthSqr) ) * Math.signum( lengthSqr );
        actualBounceVel.set( tmpVelocity ).multLocal( length );
    }

    protected PendingContact() {
        clear();
    }


}

/*
 * $log$
 */

