/*
 * Copyright (c) 2004, William Denniss. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of William Denniss nor the names of
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 *
 */
package org.odejava;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_float;

/**
 * A Geom that is placeable as defined by ODE.  These geom's can be
 * moved and rotated.
 *
 * @author William Denniss
 */
public abstract class PlaceableGeom extends Geom implements Placeable {

    // null if static object
    private Body body;

    // References to position, quaternion and rotation objects
    private SWIGTYPE_p_float posArray, rotArray;
    private SWIGTYPE_p_float quatArray = Ode.new_floatArray( 4 );

    private boolean fixed = true;
    private SWIGTYPE_p_float float4Array;

    /**
     * Calls the super constructor
     *
     * @param name the name of this geom
     */
    protected PlaceableGeom( String name ) {
        super( name );
    }

    /**
     * Sets the position of this transformable
     *
     * @param position to set
     */
    public void setPosition( Vector3f position ) {

        //Odejava.odeLog.debug("dGeomSetPosition(geomId: " + geomId + ", x, z y)");
        Ode.dGeomSetPosition( geomId, position.x, position.y, position.z );
    }

    @Override
    protected void finalize() throws Throwable {
        if ( float4Array != null ) {
            Ode.delete_floatArray( float4Array );
        }
        super.finalize();
    }

    /**
     * Sets the rotation of the geom using a quaternion
     *
     * @param quaternion the rotation quaternion to use
     */
    public void setQuaternion( Quaternion quaternion ) {
        if ( float4Array == null ) {
            float4Array = Ode.new_floatArray( 4 );
        }
        Ode.floatArray_setitem( float4Array, 0, quaternion.w );
        Ode.floatArray_setitem( float4Array, 1, quaternion.x );
        Ode.floatArray_setitem( float4Array, 2, quaternion.y );
        Ode.floatArray_setitem( float4Array, 3, quaternion.z );

        //Odejava.odeLog.debug("dGeomSetQuaternion(geomId: " + geomId);
        Ode.dGeomSetQuaternion( geomId, float4Array );
    }

    /**
     * Sets the rotation of this geom using a 3x3 rotation matrix
     *
     * @param r rotation matrix to use
     */
    public void setRotation( Matrix3f r ) {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 12 );
        Ode.floatArray_setitem( tmpArray, 0, r.get( 0, 0 ) );
        Ode.floatArray_setitem( tmpArray, 1, r.get( 0, 1 ) );
        Ode.floatArray_setitem( tmpArray, 2, r.get( 0, 2 ) );
        Ode.floatArray_setitem( tmpArray, 4, r.get( 1, 0 ) );
        Ode.floatArray_setitem( tmpArray, 5, r.get( 1, 1 ) );
        Ode.floatArray_setitem( tmpArray, 6, r.get( 1, 2 ) );
        Ode.floatArray_setitem( tmpArray, 8, r.get( 2, 0 ) );
        Ode.floatArray_setitem( tmpArray, 9, r.get( 2, 1 ) );
        Ode.floatArray_setitem( tmpArray, 10, r.get( 2, 2 ) );

        //Odejava.odeLog.debug("Ode.dGeomSetRotation(geomID: " + geomId + ", tmpArray: " + tmpArray + ")");
        Ode.dGeomSetRotation( geomId, tmpArray );
        Ode.delete_floatArray( tmpArray );
    }

    /**
     * Sets the rotation using the given axis and angle.
     *
     * @param ax    Axis X component
     * @param ay    Axis Y component
     * @param az    Axis Z component
     * @param angle angle component
     */
    public void setAxisAndAngle( float ax, float ay, float az, float angle ) {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );

        //Odejava.odeLog.debug("Ode.dQFromAxisAndAngle");
        Ode.dQFromAxisAndAngle( tmpArray, ax, ay, az, angle );

        //Odejava.odeLog.debug("Ode.dGeomSetQuaternion(geomID: " + geomId + ", tmpArray: " + tmpArray + ")");
        Ode.dGeomSetQuaternion( geomId, tmpArray );
        Ode.delete_floatArray( tmpArray );
    }

    /**
     * Returns the current position.
     *
     * @return the current position.
     */
    public Vector3f getPosition() {
        return getPosition( (Vector3f) null );
    }

    /**
     * Returns the current position using the provided Vector3f
     *
     * @param result The result Vector3f
     * @return the current position
     */
    public Vector3f getPosition( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        //Odejava.odeLog.debug("Ode.dGeomGetPosition(geomId: " + geomId + ")");
        posArray = Ode.dGeomGetPosition( geomId );

        result.x = Ode.floatArray_getitem( posArray, 0 );
        result.y = Ode.floatArray_getitem( posArray, 1 );
        result.z = Ode.floatArray_getitem( posArray, 2 );

        return result;
    }

    /**
     * Returns the current position using the provided float array.
     *
     * @param result The result array at least length 3
     */
    public void getPosition( float[] result ) {

        posArray = Ode.dGeomGetPosition( geomId );

        result[0] = Ode.floatArray_getitem( posArray, 0 );
        result[1] = Ode.floatArray_getitem( posArray, 1 );
        result[2] = Ode.floatArray_getitem( posArray, 2 );
    }

    /**
     * Sets the position of this transformable
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public void setPosition( float x, float y, float z ) {
        Ode.dGeomSetPosition( geomId, x, y, z );
    }

    /**
     * Returns the quaternion.
     *
     * @return the quaternion.
     */
    public Quaternion getQuaternion() {
        return getQuaternion( null );
    }

    /**
     * Returns the quaternion using the provided Quad4f
     *
     * @param result the result Quad4f.
     * @return the quaternion.
     */
    public Quaternion getQuaternion( Quaternion result ) {

        //Odejava.odeLog.debug("Ode.dGeomGetQuaternion(geomId: " + geomId + ", quatArray)");
        Ode.dGeomGetQuaternion( geomId, quatArray );
        if ( result == null ) {
            result = new Quaternion();
        }

        result.x = Ode.floatArray_getitem( quatArray, 1 );
        result.y = Ode.floatArray_getitem( quatArray, 2 );
        result.z = Ode.floatArray_getitem( quatArray, 3 );
        result.w = Ode.floatArray_getitem( quatArray, 0 );

        return result;
    }

    /**
     * Gets the rotation matrix of the geom
     *
     * @return the rotation matrix
     */
    public Matrix3f getRotation() {

        //Odejava.odeLog.debug("Ode.dGeomGetRotation(geomId: " + geomId + ")");
        rotArray = Ode.dGeomGetRotation( geomId );

        Matrix3f result = new Matrix3f();
        result.set(
                new float[]{
                        Ode.floatArray_getitem( rotArray, 0 ),
                        Ode.floatArray_getitem( rotArray, 1 ),
                        Ode.floatArray_getitem( rotArray, 2 ),
                        Ode.floatArray_getitem( rotArray, 4 ),
                        Ode.floatArray_getitem( rotArray, 5 ),
                        Ode.floatArray_getitem( rotArray, 6 ),
                        Ode.floatArray_getitem( rotArray, 8 ),
                        Ode.floatArray_getitem( rotArray, 9 ),
                        Ode.floatArray_getitem( rotArray, 10 )} );
        return result;
    }


    /**
     * Returns the <code>Body</code> this <code>Geom</code> is attached to.
     *
     * @return the <code>Body</code> this <code>Geom</code> is attached to.
     */
    public Body getBody() {
        return body;
    }

    /**
     * <p>Sets this Geom's body and calls the ODE method void dGeomSetBody (dGeomID, dBodyID).
     * The position and rotation components of the this Geom and the
     * passed Body are combined so that setting one will also set the other.</p>
     * <p/>
     * <p>If <code>null</code> is passed, the Geom will be set to have no
     * Body.  If it was previously attached to a Body then it's
     * translation and rotation will be that of the Body when this
     * method was called.  From then on it has its own translation and
     * rotation.</p>
     * <p/>
     * <p><b>This method is automatically called by Body.addGeom and Body.removeGeom.</b>
     * It should not be used outside those two methods, and such use may result in errors or
     * ambiguous cases (such as two Bodies thinking they both have this Geom as their child
     * when in actual fact, a Body can only have one parent).  In some (but not all) cases -
     * such attempts will cause an AssertionException to be thrown.
     * </p>
     *
     * @param body The body to set.
     */
    protected void setBody( final Body body ) {

        boolean legalOperation = true;

        // If this Geom's body isn't null - and the passed body
        // is also not null, we may have a problem as a Geom
        // can only have one parent, and to set a new parent,
        // the old one must be removed first
        if ( this.body != null && body != null ) {

            // unless the two bodies are equal, we do have a problem
            if ( this.body != body ) {
                legalOperation = false;
            }
        }

        // cause exception if the operation isn't legal
        if ( !legalOperation ) {
            throw new IllegalOdejavaOperation(
                    "Attempt to create a second Body parent for this Geom.  As a Geoms " +
                            "can only have one parent, it's current parent must first " +
                            "be removed (by calling its removeGeom method) before a new one can be set."
            );
        }

        if ( isEncapsulated ) {
            System.out.println( "Odejava: Warning - an Encapsulated geom was added to a body.  You probably don't want to use the parent GeomTransform instead." );
        }


        this.body = body;

        if ( body != null ) {

//            assert body.getGeoms().contains(this) :
//                "This method should only be called by Body.addGeom.  " +
//                "The passed Body does not have this Geom listed as one of its Geoms.  " +
//                "If this action were allowed, it would be possible for several" +
//                " Bodies to think they had this Geom as a child, when in actual fact it can only belong to one.";

            //Odejava.odeLog.debug  ("dGeomSetBody(" + this.getId() + ", " + body.getId() + ")");
            Ode.dGeomSetBody( this.getId(), body.getId() );

        }
        else {
            //Odejava.odeLog.debug  ("dGeomSetBody(" + this.getId() + ", BODYID_ZERO)");
            Ode.dGeomSetBody( this.getId(), Ode.getBODYID_ZERO() );
        }


        fixed = body == null;

    }

    public boolean fixed() {
        return fixed;
    }

    @Override
    public void delete() {
        if ( body != null ) {
            body.removeGeom( this );
        }
        super.delete();
    }
}
