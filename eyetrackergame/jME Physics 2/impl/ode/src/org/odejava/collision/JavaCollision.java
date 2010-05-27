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

import org.odejava.Geom;
import org.odejava.Odejava;
import org.odejava.Space;
import org.odejava.World;
import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_dGeomID;
import org.odejava.ode.SWIGTYPE_p_dSpaceID;
import org.odejava.ode.SWIGTYPE_p_dWorldID;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;

/**
 * JavaCollision is currently the preferred way of doing collision on Odejava.
 * Each Collide call updates buffers that contain collision data. Buffers are
 * createad at the native side and Java accessed them using NIO's
 * DirectBuffers.
 * <p/>
 * Created 11.02.2004 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         <p/>
 *         see http://odejava.dev.java.net
 */
public class JavaCollision extends Collision {

    /**
     * Error message for if they didn't provide a valid world
     */
    private static final String VALID_WORLD_REQ_ERR =
            "A valid world reference is required to construct an instance of " +
                    "this class.";

    private int contactCount;
    private ByteBuffer contactBuffer1;
    private LongBuffer contactLongBuffer;
    private ByteBuffer contactBuffer2;
    private FloatBuffer contactFloatBuffer;

    /**
     * Reference to our world we're working with
     */
    private SWIGTYPE_p_dWorldID worldId;

    /**
     * Create Java version of collision class. A valid world instance must be
     * provided for this collision group to work with.
     *
     * @param world The world instance that this collision should work with.
     * @throws NullPointerException The world reference is null
     */
    public JavaCollision( World world ) {
        super();

        if ( world == null ) {
            throw new NullPointerException( VALID_WORLD_REQ_ERR );
        }

        worldId = world.getId();
        contactBuffer1 = Odejava.getContactIntBuf();
        contactBuffer1.order( ByteOrder.nativeOrder() );

        contactLongBuffer = contactBuffer1.asLongBuffer();

        contactBuffer2 = Odejava.getContactFloatBuf();
        contactBuffer2.order( ByteOrder.nativeOrder() );
        contactFloatBuffer = contactBuffer2.asFloatBuffer();
    }

    /**
     * Sets contact data buffer to given size. Buffers need to be big enough to
     * contain contact data generated per single step. Default size is 4096.
     *
     * @param size
     */
    public void setMaxStepContactsPerNearcallback( int size ) {
        Odejava.setMaxStepContacts( size );
    }

    /**
     * Collide uses ODE's spaceCollide. Contact data is stored into buffers.
     * After each collide call you have an possibility to read/write contact
     * data using Contact class. Remember to add contacts to simulation by
     * calling applyContacts method.
     *
     * @param space
     */
    public void collide( Space space ) {
        if ( deleted ) {
            return;
        }

        contactLongBuffer.rewind();
        contactFloatBuffer.rewind();

        // Collide objects, generates all contacts
        contactCount = Ode.spaceCollide( space.getId() );
    }

    //FIX ME: this is quite a hack - it would be better to avoid using native pointers at all
    private static class MySWIGTYPE_p_dSpaceID extends SWIGTYPE_p_dSpaceID {
        private MySWIGTYPE_p_dSpaceID( long cPtr, boolean cMemoryOwn ) {
            super( cPtr, cMemoryOwn );
        }

        protected static long getCPtr( SWIGTYPE_p_dSpaceID obj ) {
            return SWIGTYPE_p_dSpaceID.getCPtr( obj );
        }
    }

    //FIX ME: this is quite a hack - it would be better to avoid using native pointers at all
    private static class MySWIGTYPE_p_dGeomID extends SWIGTYPE_p_dGeomID {
        private MySWIGTYPE_p_dGeomID( long cPtr, boolean cMemoryOwn ) {
            super( cPtr, cMemoryOwn );
        }

        protected static long getCPtr( SWIGTYPE_p_dGeomID obj ) {
            return SWIGTYPE_p_dGeomID.getCPtr( obj );
        }
    }

    /**
     * Collide2 uses ODE's spaceCollide2. Arguments can be spaces or geoms
     * addresses. Contact structures are stored to Collision.contactAddrs.
     * Contacts need to be added into contact jointgroup with method
     * applyContacts()
     *
     * @param o1
     * @param o2
     */
    private void collide2( long o1, long o2 ) {
        if ( deleted ) {
            return;
        }

        contactLongBuffer.rewind();
        contactFloatBuffer.rewind();

        // Collide objects, generates all contacts
        contactCount = Odejava.spaceCollide2( o1, o2 );
    }

    public void collide2( Space space1, Space space2 )
    {
        collide2( MySWIGTYPE_p_dSpaceID.getCPtr( space1.getId() ),
                MySWIGTYPE_p_dSpaceID.getCPtr( space2.getId() ) );
    }

    public void collide2( Space space, Geom geom )
    {
        collide2( MySWIGTYPE_p_dSpaceID.getCPtr( space.getId() ),
                MySWIGTYPE_p_dGeomID.getCPtr( geom.getId() ) );
    }

    public void collide2( Geom geom1, Geom geom2 )
    {
        collide2( MySWIGTYPE_p_dGeomID.getCPtr( geom1.getId() ),
                MySWIGTYPE_p_dGeomID.getCPtr( geom2.getId() ) );
    }


    /**
     * Apply contact data to simulation. Adds all contact joints to contact
     * jointgroup. Call this after spaceCollide call if you are not creating
     * contact joints manually at the Java side. Collision can be done with
     * other spaces before stepping the world.
     */
    public void applyContacts() {
        if ( deleted ) {
            return;
        }

        contactLongBuffer.rewind();
        contactFloatBuffer.rewind();

//        Ode.setContactGroupID( contactGroupId );
//        Ode.setWorldID( worldId );

        // Add all contacts to contact jointgroup in a single call
        if ( contactCount > 0 ) {
            Odejava.createContactJoints( worldId, contactGroupId );
        }
    }

    /**
     * Get the count of contacts (on buffers) generated by collide calls.
     *
     * @return count of contacts
     */
    public int getContactCount() {
        return contactCount;
    }

    public LongBuffer getContactLongBuffer() {
        return contactLongBuffer;
    }

    public FloatBuffer getContactFloatBuffer() {
        return contactFloatBuffer;
    }
}
