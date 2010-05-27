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

import java.nio.FloatBuffer;
import java.nio.LongBuffer;

import com.jme.math.Vector3f;
import org.odejava.Geom;

/**
 * This class can be used for accessing collision data buffers. Usable only
 * with JavaCollision.
 * <p/>
 * Created 11.02.2004 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         <p/>
 *         see http://odejava.dev.java.net
 */
public class Contact {

    /**
     * Message for when the index is passed the maximum allowed
     */
    private static final String MAX_INDEX_MSG =
            "Index is past the end of the maximum number of held contacts";

    // intBuf chunk structure
    public static final int INTBUF_CHUNK_SIZE = 5;
    public static final int GEOM_ID1 = 0;
    public static final int GEOM_ID2 = 1;
    public static final int BODY_ID1 = 2;
    public static final int BODY_ID2 = 3;
    public static final int MODE = 4;

    // floatBuf chunk structure
    public static final int FLOATBUF_CHUNK_SIZE = 20;
    public static final int POSITION = 0;
    public static final int NORMAL = 3;
    public static final int DEPTH = 6;
    public static final int FDIR1 = 7;
    public static final int MU = 10;
    public static final int MU2 = 11;
    public static final int BOUNCE = 12;
    public static final int BOUNCE_VEL = 13;
    public static final int SOFT_ERP = 14;
    public static final int SOFT_CFM = 15;
    public static final int MOTION1 = 16;
    public static final int MOTION2 = 17;
    public static final int SLIP1 = 18;
    public static final int SLIP2 = 19;

    /**
     * Buffer holding the integer data for these sets of contacts
     */
    protected LongBuffer longBuf;

    /**
     * Buffer holding the float data for these sets of contacts
     */
    protected FloatBuffer floatBuf;

    /**
     * The current index that this class is working on, if used in that way
     */
    protected int index;

    /**
     * The maximum number of contacts processable
     */
    protected int maxContacts;

    /**
     * Create a new contact collection that sources it's data from the two NIO
     * buffers. The initial index is set to zero.
     *
     * @param longBuf   The integer data source
     * @param floatBuf The floating point data source
     */
    public Contact( LongBuffer longBuf, FloatBuffer floatBuf ) {
        this.longBuf = longBuf;
        this.floatBuf = floatBuf;
        index = 0;

        maxContacts = longBuf.capacity() / INTBUF_CHUNK_SIZE;
    }

    /**
     * Set the working index to the the given value. A check is made to make
     * sure that the value is not past the end of the valid list. If it is, an
     * array index exception is thrown.
     *
     * @param index The new index to use
     * @throws ArrayIndexOutOfBoundsException Passed the end of the array
     */
    public void setIndex( int index ) {
        if ( index >= maxContacts ) {
            throw new ArrayIndexOutOfBoundsException( MAX_INDEX_MSG );
        }

        this.index = index;
        clearCache();
    }

    private void clearCache() {
        geom1 = null;
        geom2 = null;
    }

    /**
     * Increment the internal working index by one to access the next contact.
     * If this will move the value passed the maximum number then an exception
     * is thrown.
     *
     * @throws ArrayIndexOutOfBoundsException Passed the end of the array
     */
    public void nextContact() {
        if ( index + 1 >= maxContacts ) {
            throw new ArrayIndexOutOfBoundsException( MAX_INDEX_MSG );
        }

        index++;
    }

    /**
     * Get the total maximum possible contacts that could be used from this
     * set. The maximum number is the total permissable, not the total that are
     * actually valid for this set. To check on the number of valid contacts,
     * use the {@link org.odejava.collision.JavaCollision#getContactCount()}  method.
     */
    public int getMaxContacts() {
        return maxContacts;
    }

    /**
     * Ignore contact so it does not affect to simulation. Note: if you wish to
     * ignore certain geom <->geom collisions then use categoryBits and
     * collideBits instead, that is a lot faster.
     */
    public void ignoreContact() {
        setGeomID1( 0 );
        setGeomID2( 0 );
    }

    /**
     * Returns the native address of the first Geom object involved in this contact.
     *
     * @return the native address of the first Geom object involved in this contact.
     * @see Geom#getNativeAddr()
     * @see Geom#getGeomFromNativeAddr
     */
    public long getGeomID1() {
        return longBuf.get( index * INTBUF_CHUNK_SIZE + GEOM_ID1 );
    }

    /**
     * Returns the native address of the first Geom object involved in this contact.
     *
     * @return the native address of the first Geom object involved in this contact.
     * @see Geom#getNativeAddr()
     * @see Geom#getGeomFromNativeAddr
     */
    public long getGeomID1( int idx ) {
        return longBuf.get( index * INTBUF_CHUNK_SIZE + GEOM_ID1 );
    }

    private Geom geom1;

    /**
     * Returns the first Geom object involved in this contact.
     *
     * @return the first Geom object involved in this contact
     */
    public Geom getGeom1() {
        if ( geom1 == null ) {
            geom1 = Geom.getGeomFromNativeAddr( getGeomID1() );
        }
        return geom1;
    }

    public void setGeomID1( long id ) {
        longBuf.put( index * INTBUF_CHUNK_SIZE + GEOM_ID1, id );
    }

    public void setGeomID1( long id, int idx ) {
        longBuf.put( idx * INTBUF_CHUNK_SIZE + GEOM_ID1, id );
    }

    /**
     * Returns the native address of the second Geom object involved in this contact.
     *
     * @return the native address of the second Geom object involved in this contact.
     * @see Geom#getNativeAddr()
     * @see Geom#getGeomFromNativeAddr
     */
    public long getGeomID2() {
        return longBuf.get( index * INTBUF_CHUNK_SIZE + GEOM_ID2 );
    }

    /**
     * Returns the native address of the second Geom object involved in this contact.
     *
     * @return the native address of the second Geom object involved in this contact.
     * @see Geom#getNativeAddr()
     * @see Geom#getGeomFromNativeAddr
     */
    public long getGeomID2( int idx ) {
        return longBuf.get( idx * INTBUF_CHUNK_SIZE + GEOM_ID2 );
    }

    private Geom geom2;

    /**
     * Returns the second Geom object involved in this contact.
     *
     * @return the second Geom object involved in this contact
     */
    public Geom getGeom2() {
        if ( geom2 == null ) {
            geom2 = Geom.getGeomFromNativeAddr( getGeomID2() );
        }
        return geom2;
    }

    /**
     * Tests the passed geom with the first Geom involved and returns true if
     * they are equal.  Compares native addresses which is faster than using
     * getGeomX and testing for identity.
     *
     * @param geom the geom to test
     * @return true if the passed geom is equal to the contact's first geom
     *         false if it isn't.
     */
    public boolean geom1EqualTo( Geom geom ) {
        return geom.getNativeAddr() == getGeomID1();
    }

    /**
     * Tests the passed geom with the second Geom involved and returns true if
     * they are equal.  Compares native addresses which is faster than using
     * getGeomX and testing for identity.
     *
     * @param geom the geom to test
     * @return true if the passed geom is equal to the contact's second geom
     *         false if it isn't.
     */
    public boolean geom2EqualTo( Geom geom ) {
        return geom.getNativeAddr() == getGeomID2();
    }

    public void setGeomID2( long id ) {
        longBuf.put( index * INTBUF_CHUNK_SIZE + GEOM_ID2, id );
    }

    public void setGeomID2( long id, int idx ) {
        longBuf.put( idx * INTBUF_CHUNK_SIZE + GEOM_ID2, id );
    }

    public long getBodyID1() {
        return longBuf.get( index * INTBUF_CHUNK_SIZE + BODY_ID1 );
    }

    public long getBodyID1( int idx ) {
        return longBuf.get( idx * INTBUF_CHUNK_SIZE + BODY_ID1 );
    }

    public void setBodyID1( long id ) {
        longBuf.put( index * INTBUF_CHUNK_SIZE + BODY_ID1, id );
    }

    public void setBodyID1( long id, int idx ) {
        longBuf.put( idx * INTBUF_CHUNK_SIZE + BODY_ID1, id );
    }

    public long getBodyID2() {
        return longBuf.get( index * INTBUF_CHUNK_SIZE + BODY_ID2 );
    }

    public long getBodyID2( int idx ) {
        return longBuf.get( idx * INTBUF_CHUNK_SIZE + BODY_ID2 );
    }

    public void setBodyID2( long id ) {
        longBuf.put( index * INTBUF_CHUNK_SIZE + BODY_ID2, id );
    }

    public void setBodyID2( long id, int idx ) {
        longBuf.put( idx * INTBUF_CHUNK_SIZE + BODY_ID2, id );
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @return mode of surface contact
     */
    public long getMode() {
        return longBuf.get( index * INTBUF_CHUNK_SIZE + MODE );
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @return mode of surface contact
     */
    public long getMode( int idx ) {
        return longBuf.get( idx * INTBUF_CHUNK_SIZE + MODE );
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @param mode of surface contact
     */
    public void setMode( int mode ) {
        longBuf.put( index * INTBUF_CHUNK_SIZE + MODE, mode );
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @param mode of surface contact
     */
    public void setMode( int mode, int idx ) {
        longBuf.put( idx * INTBUF_CHUNK_SIZE + MODE, mode );
    }

    //
    // floatBuffer methods
    //

    public Vector3f getPosition( Vector3f position ) {
        //floatBuf.get(position, index * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        position.x = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION );
        position.y = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION + 1 );
        position.z = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION + 2 );
        return position;
    }

    public void getPosition( Vector3f position, int idx ) {
        //floatBuf.get(position, idx * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        position.x = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION );
        position.y = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION + 1 );
        position.z = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION + 2 );
    }

    public void getPosition( float[] position ) {
        //floatBuf.get(position, index * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        position[0] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION );
        position[1] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION + 1 );
        position[2] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION + 2 );
    }

    public void getPosition( float[] position, int idx ) {
        //floatBuf.get(position, idx * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        position[0] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION );
        position[1] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION + 1 );
        position[2] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION + 2 );
    }

    public float[] getPosition() {
        float[] position = new float[3];
        //floatBuf.get(position, index * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        position[0] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION );
        position[1] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION + 1 );
        position[2] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + POSITION + 2 );
        return position;
    }

    public float[] getPosition( int idx ) {
        float[] position = new float[3];
        //floatBuf.get(position, idx * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        position[0] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION );
        position[1] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION + 1 );
        position[2] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + POSITION + 2 );
        return position;
    }

    public void setPosition( float[] position ) {
        //floatBuf.put(position, index * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + POSITION, position[0] );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + POSITION + 1, position[1] );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + POSITION + 2, position[2] );
    }

    public void setPosition( float[] position, int idx ) {
        //floatBuf.put(position, idx * FLOATBUF_CHUNK_SIZE + POSITION, 3);
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + POSITION, position[0] );
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + POSITION + 1, position[1] );
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + POSITION + 2, position[2] );
    }

    public Vector3f getNormal( Vector3f normal ) {
        //floatBuf.get(normal, index * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        normal.x = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL );
        normal.y = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL + 1 );
        normal.z = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL + 2 );
        return normal;
    }

    public void getNormal( Vector3f normal, int idx ) {
        //floatBuf.get(normal, idx * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        normal.x = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL );
        normal.y = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1 );
        normal.z = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2 );
    }

    public void getNormal( float[] normal ) {
        //floatBuf.get(normal, index * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        normal[0] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL );
        normal[1] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL + 1 );
        normal[2] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL + 2 );
    }

    public void getNormal( float[] normal, int idx ) {
        //floatBuf.get(normal, idx * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        normal[0] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL );
        normal[1] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1 );
        normal[2] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2 );
    }

    public float[] getNormal() {
        float[] normal = new float[3];
        //floatBuf.get(normal, index * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        normal[0] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL );
        normal[1] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL + 1 );
        normal[2] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + NORMAL + 2 );
        return normal;
    }

    public float[] getNormal( int idx ) {
        float[] normal = new float[3];
        //floatBuf.get(normal, idx * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        normal[0] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL );
        normal[1] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1 );
        normal[2] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2 );
        return normal;
    }

    public void setNormal( float[] normal ) {
        //floatBuf.put(normal, index * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + NORMAL, normal[0] );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + NORMAL + 1, normal[1] );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + NORMAL + 2, normal[2] );
    }

    public void setNormal( float[] normal, int idx ) {
        //floatBuf.put(normal, idx * FLOATBUF_CHUNK_SIZE + NORMAL, 3);
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + NORMAL, normal[0] );
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1, normal[1] );
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2, normal[2] );
    }

    public float getDepth() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + DEPTH );
    }

    public float getDepth( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + DEPTH );
    }

    public void setDepth( float depth ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + DEPTH, depth );
    }

    public void setDepth( float depth, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + DEPTH, depth );
    }

    public Vector3f getFdir1( Vector3f fdir1 ) {
        //floatBuf.get(fdir1, index * FLOATBUF_CHUNK_SIZE + FDIR1, 3);
        fdir1.x = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + FDIR1 );
        fdir1.y = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 1 );
        fdir1.z = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 2 );
        return fdir1;
    }

    public void getFdir1( float[] fdir1 ) {
        //floatBuf.get(fdir1, index * FLOATBUF_CHUNK_SIZE + FDIR1, 3);
        fdir1[0] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + FDIR1 );
        fdir1[1] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 1 );
        fdir1[2] = floatBuf.get( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 2 );
    }

    public void getFdir1( float[] fdir1, int idx ) {
        //floatBuf.get(fdir1, idx * FLOATBUF_CHUNK_SIZE + FDIR1, 3);
        fdir1[0] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + FDIR1 );
        fdir1[1] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 1 );
        fdir1[2] = floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 2 );
    }

    public void setFdir1( float[] fdir1 ) {
        //floatBuf.put(fdir1, index * FLOATBUF_CHUNK_SIZE + FDIR1, 3);
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + FDIR1, fdir1[0] );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 1, fdir1[1] );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 2, fdir1[2] );
    }

    public void setFdir1( Vector3f fdir1 ) {
        //floatBuf.put(fdir1, index * FLOATBUF_CHUNK_SIZE + FDIR1, 3);
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + FDIR1, fdir1.x );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 1, fdir1.y );
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + FDIR1 + 2, fdir1.z );
    }

    public void setFdir1( float[] fdir1, int idx ) {
        //floatBuf.put(fdir1, idx * FLOATBUF_CHUNK_SIZE + FDIR1, 3);
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + FDIR1, fdir1[0] );
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 1, fdir1[1] );
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 2, fdir1[2] );
    }

    /**
     * Coulomb friction coefficient. This must be in the range 0 to dInfinity.
     * 0 results in a frictionless contact, and dInfinity results in a contact that
     * never slips. Note that frictionless contacts are less time consuming to
     * compute than ones with friction, and infinite friction contacts can be
     * cheaper than contacts with finite friction. This must always be set.
     *
     * @return mu
     */
    public float getMu() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + MU );
    }

    public float getMu( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + MU );
    }

    public void setMu( float mu ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + MU, mu );
    }

    public void setMu( float mu, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + MU, mu );
    }

    /**
     * Optional Coulomb friction coefficient for friction direction 2 (0..dInfinity). This is only set if the
     * corresponding flag is set in mode.
     *
     * @return mu2
     */
    public float getMu2() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + MU2 );
    }

    public float getMu2( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + MU2 );
    }

    public void setMu2( float mu2 ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + MU2, mu2 );
    }

    public void setMu2( float mu2, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + MU2, mu2 );
    }

    /**
     * Restitution parameter (0..1). 0 means the surfaces are not bouncy at all, 1 is maximum bouncyness. This is only
     * set if the corresponding flag is set in mode.
     *
     * @return bounce
     */
    public float getBounce() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + BOUNCE );
    }

    public float getBounce( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + BOUNCE );
    }

    public void setBounce( float bounce ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + BOUNCE, bounce );
    }

    public void setBounce( float bounce, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + BOUNCE, bounce );
    }

    /**
     * The minimum incoming velocity necessary for bounce (in m/s). Incoming velocities below this will effectively have
     * a bounce parameter of 0. This is only set if the corresponding flag is set in mode.
     *
     * @return bounceVel
     */
    public float getBounceVel() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL );
    }

    public float getBounceVel( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL );
    }

    public void setBounceVel( float bounceVel ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL, bounceVel );
    }

    public void setBounceVel( float bounceVel, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL, bounceVel );
    }

    /**
     * Contact normal ``softness'' parameter. This is only set if the corresponding flag is set in mode.
     *
     * @return softErp
     */
    public float getSoftErp() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + SOFT_ERP );
    }

    public float getSoftErp( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + SOFT_ERP );
    }

    public void setSoftErp( float softErp ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + SOFT_ERP, softErp );
    }

    public void setSoftErp( float softErp, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + SOFT_ERP, softErp );
    }

    /**
     * Contact normal ``softness'' parameter. This is only set if the corresponding flag is set in mode.
     *
     * @return softCfm
     */
    public float getSoftCfm() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + SOFT_CFM );
    }

    public float getSoftCfm( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + SOFT_CFM );
    }

    public void setSoftCfm( float softCfm ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + SOFT_CFM, softCfm );
    }

    public void setSoftCfm( float softCfm, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + SOFT_CFM, softCfm );
    }

    /**
     * Surface velocity in friction direction 1 (in m/s). These are only set if the corresponding flags are set in mode.
     */
    public float getMotion1() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + MOTION1 );
    }

    public float getMotion1( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + MOTION1 );
    }

    public void setMotion1( float motion1 ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + MOTION1, motion1 );
    }

    public void setMotion1( float motion1, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + MOTION1, motion1 );
    }

    /**
     * Surface velocity in friction direction 2 (in m/s). These are only set if the corresponding flags are set in mode.
     */
    public float getMotion2() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + MOTION2 );
    }

    public float getMotion2( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + MOTION2 );
    }

    public void setMotion2( float motion2 ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + MOTION2, motion2 );
    }

    public void setMotion2( float motion2, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + MOTION2, motion2 );
    }

    /**
     * The coefficients of force-dependent-slip (FDS) for friction direction 1. These are only set if the
     * corresponding flags are set in mode.
     */
    public float getSlip1() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + SLIP1 );
    }

    public float getSlip1( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + SLIP1 );
    }

    public void setSlip1( float slip1 ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + SLIP1, slip1 );
    }

    public void setSlip1( float slip1, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + SLIP1, slip1 );
    }

    /**
     * The coefficients of force-dependent-slip (FDS) for friction direction 2. These are only set if the
     * corresponding flags are set in mode.
     */
    public float getSlip2() {
        return floatBuf.get( index * FLOATBUF_CHUNK_SIZE + SLIP2 );
    }

    public float getSlip2( int idx ) {
        return floatBuf.get( idx * FLOATBUF_CHUNK_SIZE + SLIP2 );
    }

    public void setSlip2( float slip2 ) {
        floatBuf.put( index * FLOATBUF_CHUNK_SIZE + SLIP2, slip2 );
    }

    public void setSlip2( float slip2, int idx ) {
        floatBuf.put( idx * FLOATBUF_CHUNK_SIZE + SLIP2, slip2 );
    }

}
