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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsSpace;
import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_dBodyID;
import org.odejava.ode.SWIGTYPE_p_dJointID;
import org.odejava.ode.SWIGTYPE_p_dMass;
import org.odejava.ode.SWIGTYPE_p_dWorldID;
import org.odejava.ode.SWIGTYPE_p_float;
import org.odejava.ode.dMass;

/**
 * Representation of a body that can be used in a rigid body simulation. A rigid
 * body has various properties from the point of view of the simulation. Some
 * properties change over time:
 * <p/>
 * Position vector (x,y,z) of the body's point of reference. Currently the point
 * of reference must correspond to the body's center of mass. Linear velocity of
 * the point of reference, a vector (vx,vy,vz). Orientation of a body,
 * represented by a quaternion (qs,qx,qy,qz) or a 3x3 rotation matrix. Angular
 * velocity vector (wx,wy,wz) which describes how the orientation changes over
 * time.
 * <p/>
 * Other body properties are usually constant over time:
 * <p/>
 * Mass of the body. Position of the center of mass with respect to the point of
 * reference. In the current implementation the center of mass and the point of
 * reference must coincide. Inertia matrix. This is a 3x3 matrix that describes
 * how the body's mass is distributed around the center of mass.
 * <p/>
 * Conceptually each body has an x-y-z coordinate frame embedded in it, that
 * moves and rotates with the body:
 * <p/>
 * The origin of this coordinate frame is the body's point of reference. Some
 * values in ODE (vectors, matrices etc) are relative to the body coordinate
 * frame, and others are relative to the global coordinate frame.
 * <p/>
 * Note that the shape of a rigid body is not a dynamic property (except insofar
 * as it influences the various mass properties). It is only collision detection
 * that cares about the detailed shape of the body.
 * <p/>
 * <b>Unimplemented</b>
 * <p/>
 * <ul>
 * <li>Auto disable of all forms. Getter methods return default values</li>
 * <li>Contact surface thickness</li>
 * <li>Correction velocity</li>
 * </ul>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 * @author William Denniss
 *         see http://odejava.dev.java.net
 */
public class Body implements Placeable {
    // Unique name
    private String name;
    private final SWIGTYPE_p_dBodyID bodyId;
    private SWIGTYPE_p_dWorldID worldId;
    private dMass mass;
    private SWIGTYPE_p_dMass massId;
    private dMass mass2;
    private SWIGTYPE_p_dMass massId2;

    /**
     * Flag indicating that this body has been requested to be deleted. After
     * this is set to true, none of the methods should allow further calls to
     * ODE as the values are invalid, and may well cause a crash of the library
     * or other strange error.
     */
    private boolean deleted;

    /**
     * Allows the user to attach arbituary data
     */
    private Object userData;

    /**
     * The reference to the world that this body is a member of
     */
    private World world;

    /**
     * @deprecated This is no longer needed and will be deleted shortly. Zero
     *             IDs are automatically derived from null references to objects
     */
    public static final Body BODYID_ZERO = null;

    // use an arraylist to list geoms without iterator (add and remove happens
    // less frequently)
    List<PlaceableGeom> geoms = new ArrayList<PlaceableGeom>();

    // References to position, quaternion and rotation objects
    SWIGTYPE_p_float posArray, quatArray, rotArray, angularVelArray, forceArray, linearVelArray,
            torqueArray;

    private SWIGTYPE_p_float tmpArray;

    /**
     * Create a named body that belongs to the given world, and does not contain
     * any geometry. The world reference must be non-null and the name string is
     * optional.
     *
     * @param world The parent world reference
     * @param name  A name string to associate with this body
     * @throws NullPointerException The world reference was not null
     */
    public Body( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Create a named ODE body with an initial geometry. The body's default mass
     * is based on geometry. Unnamed geoms are named after their body's name.
     *
     * @param world The parent world reference
     * @param name  A name string to associate with this body
     * @param geom  An initial geometry to associate with the body
     * @throws NullPointerException The world reference was not null
     */
    public Body( String name, World world, PlaceableGeom geom ) {
        this.worldId = world.getId();
        this.name = name;
        this.world = world;

        deleted = false;

        // Create body
        bodyId = Ode.dBodyCreate( worldId );

        // if (geom != null) {
        // Set geom for this body
        // setGeom(geom);
        // Name unnamed geoms by body
        // if ((this.name != null) && (geom.getName() == null)) {
        // geom.setName(name);
        // space.getGeomMap().put(geom.getName(), geom);
        // }
        // space.addGeom(geom);
        // }

        // Create mass
        mass = new dMass();
        massId = mass.getCPtr();
        Ode.dMassSetBox( massId, 1, 1, 1, 1 );
        // Create second mass
        mass2 = new dMass();
        massId2 = mass2.getCPtr();

        if ( geom != null ) {
            addGeom( geom );
        }

        // Get references
        posArray = Ode.dBodyGetPosition( bodyId );
        quatArray = Ode.dBodyGetQuaternion( bodyId );
        rotArray = Ode.dBodyGetRotation( bodyId );
        angularVelArray = Ode.dBodyGetAngularVel( bodyId );
        forceArray = Ode.dBodyGetForce( bodyId );
        linearVelArray = Ode.dBodyGetLinearVel( bodyId );
        torqueArray = Ode.dBodyGetTorque( bodyId );
    }

    /**
     * Adds a geom to this body. An exception will be thrown if the geom is
     * already in this body.
     * @param geom what to add
     */
    public void addGeom( PlaceableGeom geom ) {

        // assert !geoms.contains(geom) : "Geom already exists in the Body!";

        // adds to geom list
        geoms.add( geom );

        // sets the geom to be this body (by calling Ode.dGeomSetBody)
        geom.setBody( this );

        if ( ( this.name != null ) && ( geom.getName() == null ) ) {
            geom.setName( name );
        }
    }

    /**
     * Removes a geom from this body. An exception will be thrown if the geom
     * isn't in the body to begin with.
     * @param geom what to remove
     */
    public void removeGeom( PlaceableGeom geom ) {

        // assert geoms.contains(geom) : "Geom doesn't exist in the Body!";

        // removes from the geom list
        geoms.remove( geom );

        // sets Body to null on Geom
        geom.setBody( null );
    }

    /**
     * Returns the last added geom
     *
     * @return the last added geom
     */
    public Geom getGeom() {
        return geoms.get( geoms.size() - 1 );
    }

    /**
     * @return a list of geoms
     */
    public List<PlaceableGeom> getGeoms() {
        return geoms;
    }

    public void clearMass() {
        Ode.dMassSetZero( massId );
    }

    /**
     * adds the default mass distribution for the specified geom but does not apply the mass to the body
     * @param density desity of the geom
     * @param geom default shape to calculate added mass for
     * @see #applyMass()
     */
    public void addDefaultMass( PlaceableGeom geom, float density ) {
        PlaceableGeom encapsulatedGeom;
        if ( geom instanceof GeomTransform ) {
            GeomTransform transform = (GeomTransform) geom;
            encapsulatedGeom = transform.getEncapsulatedGeom();
        }
        else {
            encapsulatedGeom = null;
        }
        if ( geom instanceof GeomBox ) {
            float size[] = ( (GeomBox) geom ).getLengths();
            Ode.dMassSetBox( massId2, density, size[0], size[1], size[2] );
        }
        else if ( encapsulatedGeom instanceof GeomBox ) {
            float size[] = ( (GeomBox) encapsulatedGeom ).getLengths();
            Ode.dMassSetBox( massId2, density, size[0], size[1], size[2] );
        }
        else if ( geom instanceof GeomSphere ) {
            float radius = ( (GeomSphere) geom ).getRadius();
            Ode.dMassSetSphere( massId2, density, radius );
        }
        else if ( encapsulatedGeom instanceof GeomSphere ) {
            float radius = ( (GeomSphere) encapsulatedGeom ).getRadius();
            Ode.dMassSetSphere( massId2, density, radius );
        }/* else if ( geom instanceof GeomTriMesh ) {
            Ode.dMassSetTrimesh( massId2, density, geom.getId() );
        } else if ( encapsulatedGeom instanceof GeomTriMesh ) {
            Ode.dMassSetTrimesh( massId2, density, encapsulatedGeom.getId() );
        }*/
        else {
            Ode.dMassSetParameters( massId2, density, 0.0f, 0.0f, 0.0f,
                    density, density, density, 0.0f, 0.0f,
                    0.0f );
        }
        if ( encapsulatedGeom != null ) {
            Ode.dMassRotate( massId2, Ode.dGeomGetRotation( encapsulatedGeom.getId() ) );
            Vector3f pos = encapsulatedGeom.getPosition( tmp );
            Ode.dMassTranslate( massId2, pos.x, pos.y, pos.z );
        }
        Ode.dMassAdd( massId, massId2 );
    }

    public void applyMass() {
        float x = Ode.floatArray_getitem( mass.getC(), 0 );
        float y = Ode.floatArray_getitem( mass.getC(), 1 );
        float z = Ode.floatArray_getitem( mass.getC(), 2 );
        if ( x != 0 || y != 0 || z != 0 ) {
            if ( Math.abs( x ) > FastMath.FLT_EPSILON
                    || Math.abs( y ) > FastMath.FLT_EPSILON
                    || Math.abs( z ) > FastMath.FLT_EPSILON ) {
                Logger.getLogger( PhysicsSpace.LOGGER_NAME ).warning( "ODE cannot handle a center of mass different " +
                        "from (0;0;0) but it was (" + x + ";" + y + ";" + z + ")!" );
            }
            Ode.floatArray_setitem( mass.getC(), 0, 0 );
            Ode.floatArray_setitem( mass.getC(), 1, 0 );
            Ode.floatArray_setitem( mass.getC(), 2, 0 );
        }
        Ode.dBodySetMass( bodyId, massId );
    }

    private static final Vector3f tmp = new Vector3f();

    /**
     * Adjusts and applied the total mass.
     * @param mass new mass
     */
    public void adjustMass( float mass ) {
        if ( geoms.size() == 0 ) {
            Logger.getLogger( World.LOGGER_NAME ).log( Level.WARNING,
                    "geoms should be added before the mass is adjusted" );
        }

        Ode.dMassAdjust( massId, mass );
        applyMass();
    }

    public void setCenterOfMass( float nx, float ny, float nz ) {

        // get current center of mass
        SWIGTYPE_p_float c = mass.getC();
        float x = Ode.floatArray_getitem( c, 0 );
        float y = Ode.floatArray_getitem( c, 1 );
        float z = Ode.floatArray_getitem( c, 2 );

        Ode.dMassTranslate( massId, nx - x, ny - y, nz - z );
        applyMass();
    }

    public float getMass() {
        return mass.getMass();
    }

    public void resetDynamics() {
        setAngularVel( 0f, 0f, 0f );
        setLinearVel( 0f, 0f, 0f );
        setForce( 0f, 0f, 0f );
        setTorque( 0f, 0f, 0f );
    }

    /**
     * Get the location in world space of the body. A new Vector3f instance will
     * be created for each request. This is identical to calling
     * <code>getPosition(null)</code>.
     *
     * @return A new vector object containing the position values
     */
    public Vector3f getPosition() {
        return getPosition( (Vector3f) null );
    }

    /**
     * Get the position of the body and place it in the user-provided data
     * structure. If the user-provided data structure is null, then a new
     * instance is created and returned, otherwise the user provided structure
     * is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getPosition( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        result.x = Ode.floatArray_getitem( posArray, 0 );
        result.y = Ode.floatArray_getitem( posArray, 1 );
        result.z = Ode.floatArray_getitem( posArray, 2 );

        return result;
    }

    /**
     * Get the position of the body and place it in the user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getPosition( float[] result ) {
        result[0] = Ode.floatArray_getitem( posArray, 0 );
        result[1] = Ode.floatArray_getitem( posArray, 1 );
        result[2] = Ode.floatArray_getitem( posArray, 2 );
    }

    /**
     * Set the position to a new value, using a vector.
     *
     * @param position A vector holding the position
     */
    public void setPosition( Vector3f position ) {
        Ode.dBodySetPosition( bodyId, position.x, position.y, position.z );
    }

    /**
     * Set the force to a new value, using individual values.
     *
     * @param x The x component of the the position
     * @param y The y component of the the position
     * @param z The z component of the the position
     */
    public void setPosition( float x, float y, float z ) {
        Ode.dBodySetPosition( bodyId, x, y, z );
    }

    public void setAxisAndAngle( float ax, float ay, float az, float angle ) {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );
        Ode.dQFromAxisAndAngle( tmpArray, ax, ay, az, angle );
        Ode.dBodySetQuaternion( bodyId, tmpArray );
        Ode.delete_floatArray( tmpArray );
    }

    /**
     * Get the rotation expressed as a quaternion for the body. A new Quaternion
     * instance will be created for each request. This is identical to calling
     * <code>getQuaternion(null)</code>.
     *
     * @return A new quaternion object containing the axis values
     */
    public Quaternion getQuaternion() {
        return getQuaternion( (Quaternion) null );
    }

    /**
     * Get the rotation expressed as a quaternion for the body and place it in
     * the user-provided data structure. If the user-provided data structure is
     * null, then a new instance is created and returned, otherwise the user
     * provided structure is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Quaternion getQuaternion( Quaternion result ) {
        if ( result == null ) {
            result = new Quaternion();
        }

        result.x = Ode.floatArray_getitem( quatArray, 1 );
        result.y = Ode.floatArray_getitem( quatArray, 2 );
        result.z = Ode.floatArray_getitem( quatArray, 3 );
        result.w = Ode.floatArray_getitem( quatArray, 0 );

        return result;
    }

    public void setQuaternion( Quaternion quaternion ) {
        if ( tmpArray == null ) {
            tmpArray = Ode.new_floatArray( 4 );
        }
        Ode.floatArray_setitem( tmpArray, 0, quaternion.w );
        Ode.floatArray_setitem( tmpArray, 1, quaternion.x );
        Ode.floatArray_setitem( tmpArray, 2, quaternion.y );
        Ode.floatArray_setitem( tmpArray, 3, quaternion.z );
        Ode.dBodySetQuaternion( bodyId, tmpArray );
        // Ode.delete_floatArray(tmpArray);
    }

    /**
     * Get the rotation of the body expressed as a quaternion and place it in
     * the user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getQuaternion( float[] result ) {
        result[0] = Ode.floatArray_getitem( quatArray, 1 );
        result[1] = Ode.floatArray_getitem( quatArray, 2 );
        result[2] = Ode.floatArray_getitem( quatArray, 3 );
        result[3] = Ode.floatArray_getitem( quatArray, 0 );
    }

    /**
     * Get the rotation matrix for the body. A new matrix instance will be
     * created for each request. This is identical to calling
     * <code>getMatrix(null)</code>.
     *
     * @return A new matrix object containing the axis values
     */
    public Matrix3f getRotation() {
        return getRotation( (Matrix3f) null );
    }

    /**
     * Get the rotation matrix for the body and place it in the user-provided
     * data structure. If the user-provided data structure is null, then a new
     * instance is created and returned, otherwise the user provided structure
     * is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Matrix3f getRotation( Matrix3f result ) {
        if ( result == null ) {
            result = new Matrix3f();
        }

        // Note that the ODE representation is a 4x3 matrix and we only need a
        // 3x3 for return.
        result.m00 = Ode.floatArray_getitem( rotArray, 0 );
        result.m01 = Ode.floatArray_getitem( rotArray, 1 );
        result.m02 = Ode.floatArray_getitem( rotArray, 2 );
        result.m10 = Ode.floatArray_getitem( rotArray, 4 );
        result.m11 = Ode.floatArray_getitem( rotArray, 5 );
        result.m12 = Ode.floatArray_getitem( rotArray, 6 );
        result.m20 = Ode.floatArray_getitem( rotArray, 8 );
        result.m21 = Ode.floatArray_getitem( rotArray, 9 );
        result.m22 = Ode.floatArray_getitem( rotArray, 10 );
        return result;
    }

    /**
     * Get the rotation of the body expressed as a 3x3 matrix and place it in
     * the user-provided array. The array must be of length 9 and is row-major.
     *
     * @param result An object to place the values into
     */
    public void getRotation( float[] result ) {
        // Note that the ODE representation is a 4x3 matrix and we only need a
        // 3x3 for return.
        result[0] = Ode.floatArray_getitem( rotArray, 0 );
        result[1] = Ode.floatArray_getitem( rotArray, 1 );
        result[2] = Ode.floatArray_getitem( rotArray, 2 );
        result[3] = Ode.floatArray_getitem( rotArray, 4 );
        result[4] = Ode.floatArray_getitem( rotArray, 5 );
        result[5] = Ode.floatArray_getitem( rotArray, 6 );
        result[6] = Ode.floatArray_getitem( rotArray, 8 );
        result[7] = Ode.floatArray_getitem( rotArray, 9 );
        result[8] = Ode.floatArray_getitem( rotArray, 10 );
    }

    /**
     * Use of setQuaternion is preferred instead of setRotation as this method
     * might have some problems in some cases.
     *
     * @param r new rotation
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
        Ode.dBodySetRotation( bodyId, tmpArray );
        Ode.delete_floatArray( tmpArray );
    }

    /**
     * Get the current mode describing how global gravity will effect this body.
     * A value of 1 indicates gravity will effect it, a value of zero indicates
     * it will not be effected by gravity.
     *
     * @return A value of 0 or 1
     */
    public int getGravityMode() {
        return Ode.dBodyGetGravityMode( bodyId );
    }

    /**
     * Change the mode of how gravity effects this geometry. A value of 1 will
     * tell the body to obey the world's local gravity, a value of 0 will ignore
     * it.
     *
     * @param mode A value of 1 for global gravity, 0 for no gravity
     */
    public void setGravityMode( int mode ) {
        Ode.dBodySetGravityMode( bodyId, mode );
    }

    public void setEnabled( boolean enabled ) {
        if ( enabled ) {
            Ode.dBodyEnable( bodyId );
        }
        else {
            Ode.dBodyDisable( bodyId );
        }
    }

    public boolean isEnabled() {
        return Ode.dBodyIsEnabled( bodyId ) != 0;
    }

    public void addForce( float x, float y, float z ) {
        Ode.dBodyAddForce( bodyId, x, y, z );
    }

    /**
     * Convienience method, just calls addForce(x,y,z)
     *
     * @param force force to be added
     */
    public void addForce( Vector3f force ) {
        addForce( force.x, force.y, force.z );
    }

    public void addForceAtPos( float fx, float fy, float fz, float px, float py, float pz ) {
        Ode.dBodyAddForceAtPos( bodyId, fx, fy, fz, px, py, pz );
    }

    public void addForceAtRelPos( float fx, float fy, float fz, float px, float py, float pz ) {
        Ode.dBodyAddForceAtRelPos( bodyId, fx, fy, fz, px, py, pz );
    }

    public void addRelForce( Vector3f vec ) {
        Ode.dBodyAddRelForce( bodyId, vec.x, vec.y, vec.z );
    }

    public void addRelForceAtPos( float fx, float fy, float fz, float px, float py, float pz ) {
        Ode.dBodyAddRelForceAtPos( bodyId, fx, fy, fz, px, py, pz );
    }

    public void addRelForceAtRelPos( float fx, float fy, float fz, float px, float py, float pz ) {
        Ode.dBodyAddRelForceAtRelPos( bodyId, fx, fy, fz, px, py, pz );
    }

    public void addTorque( float x, float y, float z ) {
        Ode.dBodyAddTorque( bodyId, x, y, z );
    }

    /**
     * Convenience method, just calls addTorque(x,y,z)
     *
     * @param torque tourque to be added
     */
    public void addTorque( Vector3f torque ) {
        addTorque( torque.x, torque.y, torque.z );
    }

    public void addRelTorque( float x, float y, float z ) {
        Ode.dBodyAddRelTorque( bodyId, x, y, z );
    }

    /**
     * Convenience method, just calls addRelTorque(x,y,z)
     *
     * @param torque tourque to be added
     */
    public void addRelTorque( Vector3f torque ) {
        addRelTorque( torque.x, torque.y, torque.z );
    }

    /**
     * Return number of joints attached to this body.
     *
     * @return number of joints attached to this body.
     */
    public int getNumOfJoints() {
        return Ode.dBodyGetNumJoints( bodyId );
    }

    /**
     * Return a list of joints attached to this body, or null if none. A new
     * list is created each time this method is called.
     *
     * @return A new list of the current joints
     */
    public List<SWIGTYPE_p_dJointID> getJoints() {
        return getJoints( null );
    }

    /**
     * Return a list of joints attached to this body, copying them into the
     * given list. This method will first clear the given list, even if there
     * are no joints current. If the passed list is null, a new list instance
     * will be created.
     *
     * @param result A list to put the joints into
     * @return A list of the current joints or null if no joints
     */
    public List<SWIGTYPE_p_dJointID> getJoints( List<SWIGTYPE_p_dJointID> result ) {

        if ( getNumOfJoints() == 0 ) {
            return null;
        }

        if ( result == null ) {
            result = new ArrayList<SWIGTYPE_p_dJointID>();
        }
        else {
            result.clear();
        }

        if ( getNumOfJoints() > 0 ) {
            for ( int i = 0; i < getNumOfJoints(); i++ ) {
                result.add( Ode.dBodyGetJoint( bodyId, i ) );
            }
        }

        return result;
    }

    /**
     * @return Returns the bodyId.
     */
    public SWIGTYPE_p_dBodyID getId() {
        return bodyId;
    }

    /**
     * Fet the currently associated name string. If none is set, return null.
     *
     * @return The current name string or null.
     */
    public String getName() {
        return name;
    }

    /**
     * Set a new name string to associate with this body.
     *
     * @param name The new name string to set.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Deletes the body. This is performed automatically by World.deletebody and
     * should not be called directly.
     */
    public void delete() {
        if ( deleted ) {
            Logger.getLogger( World.LOGGER_NAME ).log( Level.WARNING, "Body " + name + " already deleted" );
            return;
        }

        for ( PlaceableGeom placeableGeom : new LinkedList<PlaceableGeom>( geoms ) ) {
            placeableGeom.delete();
        }

        Ode.dBodyDestroy( bodyId );

        deleted = true;

        world = null;
        geoms = null;
        userData = null;
    }

    /**
     * Set the force to a new value, using a vector.
     *
     * @param force A vector holding the force
     */
    public void setForce( Vector3f force ) {
        setForce( force.x, force.y, force.z );
    }

    /**
     * Set the force to a new value, using individual values.
     *
     * @param x The x component of the the force
     * @param y The y component of the the force
     * @param z The z component of the the force
     */
    public void setForce( float x, float y, float z ) {
        Ode.dBodySetForce( bodyId, x, y, z );
    }

    /**
     * Get the force of the body. A new Vector3f instance will be created for
     * each request. This is identical to calling <code>getForce(null)</code>.
     *
     * @return A new vector object containing the velocity values
     */
    public Vector3f getForce() {
        return getForce( (Vector3f) null );
    }

    /**
     * Get the force for the body and place it in the user-provided data
     * structure. If the user-provided data structure is null, then a new
     * instance is created and returned, otherwise the user provided structure
     * is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getForce( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        result.x = Ode.floatArray_getitem( forceArray, 0 );
        result.y = Ode.floatArray_getitem( forceArray, 1 );
        result.z = Ode.floatArray_getitem( forceArray, 2 );

        return result;
    }

    /**
     * Get the force of the body and place it in the user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getForce( float[] result ) {
        result[0] = Ode.floatArray_getitem( forceArray, 0 );
        result[1] = Ode.floatArray_getitem( forceArray, 1 );
        result[2] = Ode.floatArray_getitem( forceArray, 2 );
    }

    /**
     * Set the angular velocity to a new value, using a vector.
     *
     * @param angularVel A vector holding the angular velocity
     */
    public void setAngularVel( Vector3f angularVel ) {
        setAngularVel( angularVel.x, angularVel.y, angularVel.z );
    }

    /**
     * Set the angular velocity to a new value, using individual values.
     *
     * @param x The x component of the the angular velocity
     * @param y The y component of the the angular velocity
     * @param z The z component of the the angular velocity
     */
    public void setAngularVel( float x, float y, float z ) {
        Ode.dBodySetAngularVel( bodyId, x, y, z );
    }

    /**
     * Get the angular velocity of the body. A new Vector3f instance will be
     * created for each request. This is identical to calling
     * <code>getAngularVel(null)</code>.
     *
     * @return A new vector object containing the velocity values
     */
    public Vector3f getAngularVel() {
        return getAngularVel( (Vector3f) null );
    }

    /**
     * Get the angular velocity for the body and place it in the user-provided
     * data structure. If the user-provided data structure is null, then a new
     * instance is created and returned, otherwise the user provided structure
     * is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getAngularVel( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        result.x = Ode.floatArray_getitem( angularVelArray, 0 );
        result.y = Ode.floatArray_getitem( angularVelArray, 1 );
        result.z = Ode.floatArray_getitem( angularVelArray, 2 );

        return result;
    }

    /**
     * Get the angular velocity of the body and place it in the user-provided
     * array.
     *
     * @param result An object to place the values into
     */
    public void getAngularVel( float[] result ) {
        result[0] = Ode.floatArray_getitem( angularVelArray, 0 );
        result[1] = Ode.floatArray_getitem( angularVelArray, 1 );
        result[2] = Ode.floatArray_getitem( angularVelArray, 2 );
    }

    /**
     * Set the linear velocity to a new value, using a vector.
     *
     * @param linearVel A vector holding the linear velocity
     */
    public void setLinearVel( Vector3f linearVel ) {
        setLinearVel( linearVel.x, linearVel.y, linearVel.z );
    }

    /**
     * Set the linear velocity to a new value, using individual values.
     *
     * @param x The x component of the the linear velocity
     * @param y The y component of the the linear velocity
     * @param z The z component of the the linear velocity
     */
    public void setLinearVel( float x, float y, float z ) {
        Ode.dBodySetLinearVel( bodyId, x, y, z );
    }

    /**
     * Get the linear velocity of the body. A new Vector3f instance will be
     * created for each request. This is identical to calling
     * <code>getLinearVel(null)</code>.
     *
     * @return A new vector object containing the velocity values
     */
    public Vector3f getLinearVel() {
        return getLinearVel( (Vector3f) null );
    }

    /**
     * Get the linear velocity for the body and place it in the user-provided
     * data structure. If the user-provided data structure is null, then a new
     * instance is created and returned, otherwise the user provided structure
     * is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getLinearVel( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        result.x = Ode.floatArray_getitem( linearVelArray, 0 );
        result.y = Ode.floatArray_getitem( linearVelArray, 1 );
        result.z = Ode.floatArray_getitem( linearVelArray, 2 );

        return result;
    }

    /**
     * Get the linear velocity of the body and place it in the user-provided
     * array.
     *
     * @param result An object to place the values into
     */
    public void getLinearVel( float[] result ) {
        result[0] = Ode.floatArray_getitem( linearVelArray, 0 );
        result[1] = Ode.floatArray_getitem( linearVelArray, 1 );
        result[2] = Ode.floatArray_getitem( linearVelArray, 2 );
    }

    /**
     * Set the torque to a new value, using a vector.
     *
     * @param torque A vector holding the torque
     */
    public void setTorque( Vector3f torque ) {
        setTorque( torque.x, torque.y, torque.z );
    }

    /**
     * Set the torque to a new value, using individual values.
     *
     * @param x The x component of the the torque
     * @param y The y component of the the torque
     * @param z The z component of the the torque
     */
    public void setTorque( float x, float y, float z ) {
        Ode.dBodySetTorque( bodyId, x, y, z );
    }

    /**
     * Get the torque of the body. A new Vector3f instance will be created for
     * each request. This is identical to calling <code>getTorque(null)</code>.
     *
     * @return A new vector object containing the velocity values
     */
    public Vector3f getTorque() {
        return getTorque( (Vector3f) null );
    }

    /**
     * Get the torque for the body and place it in the user-provided data
     * structure. If the user-provided data structure is null, then a new
     * instance is created and returned, otherwise the user provided structure
     * is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getTorque( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        result.x = Ode.floatArray_getitem( torqueArray, 0 );
        result.y = Ode.floatArray_getitem( torqueArray, 1 );
        result.z = Ode.floatArray_getitem( torqueArray, 2 );

        return result;
    }

    /**
     * Get the torque of the body and place it in the user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getTorque( float[] result ) {
        result[0] = Ode.floatArray_getitem( torqueArray, 0 );
        result[1] = Ode.floatArray_getitem( torqueArray, 1 );
        result[2] = Ode.floatArray_getitem( torqueArray, 2 );
    }

    /**
     * Set the finiteRotationAxis to a new value, using a vector.
     *
     * @param finiteRotationAxis A vector holding the finiteRotationAxis
     */
    public void setFiniteRotationAxis( Vector3f finiteRotationAxis ) {
        setFiniteRotationAxis( finiteRotationAxis.x, finiteRotationAxis.y, finiteRotationAxis.z );
    }

    /**
     * Set the finiteRotationAxis to a new value, using individual values.
     *
     * @param x The x component of the the rotation axis
     * @param y The y component of the the rotation axis
     * @param z The z component of the the rotation axis
     */
    public void setFiniteRotationAxis( float x, float y, float z ) {
        Ode.dBodySetFiniteRotationAxis( bodyId, x, y, z );
    }

    /**
     * Get the finiteRotationAxis of the body. A new Vector3f instance will be
     * created for each request. This is identical to calling
     * <code>getFiniteRotationAxis(null)</code>.
     *
     * @return A new vector object containing the velocity values
     */
    public Vector3f getFiniteRotationAxis() {
        return getFiniteRotationAxis( (Vector3f) null );
    }

    /**
     * Get the finiteRotationAxis for the body and place it in the user-provided
     * data structure. If the user-provided data structure is null, then a new
     * instance is created and returned, otherwise the user provided structure
     * is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getFiniteRotationAxis( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );
        Ode.dBodyGetFiniteRotationAxis( bodyId, tmpArray );

        result.x = Ode.floatArray_getitem( tmpArray, 0 );
        result.y = Ode.floatArray_getitem( tmpArray, 1 );
        result.z = Ode.floatArray_getitem( tmpArray, 2 );
        Ode.delete_floatArray( tmpArray );

        return result;
    }

    /**
     * Get the finiteRotationAxis of the body and place it in the user-provided
     * array.
     *
     * @param result An object to place the values into
     */
    public void getFiniteRotationAxis( float[] result ) {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );
        Ode.dBodyGetFiniteRotationAxis( bodyId, tmpArray );

        result[0] = Ode.floatArray_getitem( tmpArray, 0 );
        result[1] = Ode.floatArray_getitem( tmpArray, 1 );
        result[2] = Ode.floatArray_getitem( tmpArray, 2 );
        Ode.delete_floatArray( tmpArray );
    }

    public void setFiniteRotationMode( int mode ) {
        Ode.dBodySetFiniteRotationMode( bodyId, mode );
    }

    public int getFiniteRotationMode() {
        return Ode.dBodyGetFiniteRotationMode( bodyId );
    }

    /**
     * @return The SWIG representation of this Body identifier
     */
    public SWIGTYPE_p_dBodyID getBodyId() {
        return bodyId;
    }

    /**
     * Set the mass parameters to the given values. themass is the mass of the
     * body. (cx,cy,cz) is the center of gravity position in the body frame. The
     * Ixx values are the elements of the inertia matrix:
     * <p>[ I11 I12 I13 ]<br />[ I12 I22 I23 ]<br />[ I13 I23 I33 ]<br />
     * </p>
     *
     * @param themass absolute mass
     * @param cgx x
     * @param cgy y
     * @param cgz z
     * @param I11 matrix component
     * @param I22 matrix component
     * @param I33 matrix component
     * @param I12 matrix component
     * @param I13 matrix component
     * @param I23 matrix component
     */
    public void setMassParameters( float themass, float cgx, float cgy, float cgz, float I11,
                                   float I22, float I33, float I12, float I13, float I23 ) {
        Ode.dMassSetParameters( massId, themass, cgx, cgy, cgz, I11, I22, I33, I12, I13, I23 );
        applyMass();
    }

    public boolean fixed() {
        return false;
    }

    /**
     * @return Returns the userData.
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * @param userData The userData to set.
     */
    public void setUserData( Object userData ) {
        this.userData = userData;
    }

    /**
     * Control whether the world should allow auto-disable of this body. A
     * value of true will enable the auto disable ability. Whether this body
     * is disabled or not depends on the settings of the individual
     * thresholds (which can be set by other methods).
     *
     * @param state True to enable auto disabling, false to disable
     */
    public void setAutoDisable( boolean state ) {
        Ode.dBodySetAutoDisableFlag( bodyId, state ? 1 : 0 );
    }

    /**
     * Check to see the current state of the auto disable functionality.
     *
     * @return true if the auto-disable mode is on, false otherwise
     */
    public boolean isAutoDisabling() {
        return Ode.dBodyGetAutoDisableFlag( bodyId ) == 1;
    }

    /**
     * Set the threshold for the linear velocity that will cause a body to be
     * disabled. Once the velocity falls below this value, the body will be
     * subject to being disabled. The threshold is only used if the auto disable
     * capability is enabled.
     *
     * @param vel The speed below which the body is disabled
     */
    public void setLinearVelocityDisableThreshold( float vel ) {
         Ode.dBodySetAutoDisableLinearThreshold(bodyId, vel);
    }

    /**
     * Get the threshold for linear velocity at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public float getLinearVelocityDisableThreshold() {
//        return 0.01f;
         return Ode.dBodyGetAutoDisableLinearThreshold(bodyId);
    }

    /**
     * Set the threshold for the angular velocity that will cause a body to be
     * disabled. Once the velocity falls below this value, the body will be
     * subject to being disabled. The threshold is only used if the auto disable
     * capability is enabled.
     *
     * @param vel The speed below which the body is disabled
     */
    public void setAngularVelocityDisableThreshold( float vel ) {
         Ode.dBodySetAutoDisableAngularThreshold(bodyId, vel);
    }

    /**
     * Get the threshold for angular velocity at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public float getAngularVelocityDisableThreshold() {
//        return 0.01f;
         return Ode.dBodyGetAutoDisableAngularThreshold(bodyId);
    }

    /**
     * Set the number of evaluation steps before an umoving body is disabled. If
     * the body has not moved in this number of steps, it is automatically
     * disabled. This setting is only used if the auto disable capabilities is
     * enabled. If the number of steps is negative or zero, bodies cannot be
     * disabled using this way.
     *
     * @param steps The number of evaluation steps to use or negative to disable
     */
    public void setStepDisableThreshold( int steps ) {
         Ode.dBodySetAutoDisableSteps(bodyId, steps);
    }

    /**
     * Get the threshold for the number of steps at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public int getStepDisableThreshold() {
//        return 10;
         return Ode.dBodyGetAutoDisableSteps(bodyId);
    }

    /**
     * Set the total amount of evaluation time an umoving body is disabled. If
     * the body has not moved in this time, it is automatically disabled. This
     * setting is only used if the auto disable capabilities is enabled. If the
     * time is negative or zero, bodies cannot be disabled using this way.
     *
     * @param time The amount of time in seconds or negative to disable
     */
    public void setTimeDisableThreshold( float time ) {
         Ode.dBodySetAutoDisableTime(bodyId, time);
    }

    /**
     * Get the threshold for the evaluation time at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public float getTimeDisableThreshold() {
        return 0;
        // return Ode.dBodyGetAutoDisableTime(bodyId);
    }

    public void getCenterOfMass( Vector3f store ) {
        SWIGTYPE_p_float c = mass.getC();
        store.x = Ode.floatArray_getitem( c, 0 );
        store.y = Ode.floatArray_getitem( c, 1 );
        store.z = Ode.floatArray_getitem( c, 2 );
    }
}
