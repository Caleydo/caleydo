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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_dGeomID;
import org.odejava.ode.SWIGTYPE_p_dSpaceID;

/**
 * Geometry objects (or geoms for short) are the fundamental objects in the
 * collision system. A geom can represents a single rigid shape (such as a
 * sphere or box), or it can represents a group of other geoms - this is a
 * special kind of geom called a space.
 * <p/>
 * Any geom can be collided against any other geom to yield zero or more contact
 * points. Spaces have the extra capability of being able to collide their
 * contained geoms together to yield internal contact points.
 * <p/>
 * The rotation matrix, just like a rigid body, that can be changed during the
 * simulation. A non-placeable geom does not have this capability - for example,
 * it may represent some static feature of the environment that can not be
 * moved. Spaces are non-placeable geoms, because each contained geom may have
 * its own position and orientation but it does not make sense for the space
 * itself to have a position and orientation.
 * <p/>
 * To use the collision engine in a rigid body simulation, placeable geoms are
 * associated with rigid body objects. This allows the collision engine to get
 * the position and orientation of the geoms from the bodies. Note that geoms
 * are distinct from rigid bodies in that a geom has geometrical properties
 * (size, shape, position and orientation) but no dynamical properties (such as
 * velocity or mass). A body and a geom together represent all the properties of
 * the simulated object.
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public abstract class Geom {
    // Unique name
    private String name;

    protected SWIGTYPE_p_dSpaceID spaceId;

    protected SWIGTYPE_p_dGeomID geomId;

    private Space space = null;

    // Native address can be used to compare against contact information values
    // which are also native addresses.
    private long nativeAddr;

    /**
     * Flag indicating that this geometry has been requested to be deleted.
     * After this is set to true, none of the methods should allow further calls
     * to ODE as the values are invalid, and may well cause a crash of the
     * library or other strange error.
     */
    private boolean deleted;

    // TODO: Add a reference to parent Space - possibly replace spaceId (can be
    // attained from Space)

    protected boolean isEncapsulated = false;

    /**
     * Geom Properties that one can extend and add to this Geom
     */
    private Object userObject;

    /**
     * the physicsObject that owns this Geom.
     */
    private PhysicsNode physicsObject;

    /**
     * the spatial that is represented by this geom
     */
    private PhysicsCollisionGeometry geometry;

    /**
     * Lookup map of Geoms to their native addresses.
     */
    private static Map<MutableLong, WeakReference<Geom>> geomNativeAddr =
            new HashMap<MutableLong, WeakReference<Geom>>();

    private static MutableLong tmp_addess = new MutableLong();

    private static class MutableLong {
        public long value;

        public MutableLong() {
        }

        public MutableLong( long value ) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return (int) value;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( obj instanceof MutableLong ) {
                return value == ( (MutableLong) obj ).value;
            }
            return false;
        }

        @Override
        public String toString() {
            return Long.toHexString( value );
        }
    }

    /**
     * Create a generic items of geometry with an associated name. The name
     * reference may be null.
     *
     * @param name A name to associate with this geometry
     */
    protected Geom( String name ) {
        this.name = name;
        deleted = false;
    }

    public SWIGTYPE_p_dGeomID getId() {
        return geomId;
    }

    public void setCategoryBits( long bits ) {
        Ode.dGeomSetCategoryBits( geomId, bits );
    }

    public void setCollideBits( long bits ) {
        Ode.dGeomSetCollideBits( geomId, bits );
    }

    /**
     * Returns the name of the Geom.
     *
     * @return Returns the name of the Geom.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Geom
     *
     * @param name The name of the Geom.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Destroys the Geom, removing it from ODE. Dereferences user data.
     */
    public void delete() {
        if ( deleted ) {
            Logger.getLogger( World.LOGGER_NAME ).log( Level.WARNING, "Geom " + name + " already deleted" );
            return;
        }

        if ( space != null ) {
            space.remove( this );
        }

        // TODO: remove from Space
        tmp_addess.value = nativeAddr;
        geomNativeAddr.remove( tmp_addess );
        // Odejava.odeLog.debug("dGeomDestory(geomId: " + geomId + ")");
        Ode.dGeomDestroy( geomId );
        space = null;

        deleted = true;
    }

    /**
     * Calls the delete() method to clean up native resources if it hasn't
     * already been called on this Geom.
     */
    @Override
    protected void finalize() throws Throwable {
        if ( !deleted ) {
            delete();
            Logger.getLogger( World.LOGGER_NAME ).log( Level.WARNING,
                    "Geom " + name + " deleted on finalisation" );
        }
        super.finalize();
    }

    /**
     * Diables or Enables this geom.
     *
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled ) {
            // Odejava.odeLog.debug("dGeomEnable(geomId: " + geomId + ")");
            Ode.dGeomEnable( geomId );
        }
        else {
            // Odejava.odeLog.debug("debug(geomId: " + geomId + ")");
            Ode.dGeomDisable( geomId );
        }
    }

    //FIX ME: this is quite a hack - it would be better to avoid using native pointers at all
    private static class MySWIGTYPE_p_dGeomID extends SWIGTYPE_p_dGeomID{
        private MySWIGTYPE_p_dGeomID( long cPtr, boolean cMemoryOwn ) {
            super( cPtr, cMemoryOwn );
        }

        public static long getCPtr(SWIGTYPE_p_dGeomID obj) {
            return SWIGTYPE_p_dGeomID.getCPtr( obj );
        }
    }

    protected void retrieveNativeAddr() {
        if ( nativeAddr != 0 ) {
            throw new IllegalStateException( "The native address of a geom may not change!" );
        }

        // Get native address based on Swig C pointer
        // This can be used with dContact.getGeom() structure that
        // contains native address instead of Swig C pointer values
        nativeAddr = Odejava.getNativeAddr( MySWIGTYPE_p_dGeomID.getCPtr( geomId ) );

        geomNativeAddr.put( new MutableLong( nativeAddr ), new WeakReference<Geom>( this ) );
    }

    /**
     * Performs lookup of a WeakHashMap and returns the Geom with the native
     * address equal to the one passed or null if none exist.
     *
     * @param nativeAddress the native address to lookup
     * @return the Geom with the native address equal to the one passed or null
     *         if none can be found.
     */
    public static Geom getGeomFromNativeAddr( long nativeAddress ) {
        tmp_addess.value = nativeAddress;
        final WeakReference<Geom> ref = geomNativeAddr.get( tmp_addess );
        Geom geom = ref != null ? ref.get() : null;
        if ( geom == null && nativeAddress != 0 ) {
            throw new IllegalStateException( "Geom with native address 0x" + Long.toHexString( nativeAddress ) + " not found!" );
        }
        return geom;
    }

    /**
     * Native address can be used to compare against contact information values
     * which are also native addresses.
     *
     * <p><b>UNSUPPORTED</b> - use of this method is better avoided.  No guarantees
     * are made that this method won't change or even exist in future versions.
     * </p>
     *
     * @return Returns the native address.
     */
    public long getNativeAddr() {
        return nativeAddr;
    }

    /**
     * Removes this Geom from its space and adds it to the empty space. This
     * method is automatically called by Space.remove and should not be called
     * directly
     *
     * @see Space#remove
     */
    protected void removeFromSpace() {
        // assert spaceId == null: "This geom is already not in any Space";

        Ode.dSpaceRemove( spaceId, geomId );
        spaceId = Ode.getPARENTSPACEID_ZERO();
        this.space = null;

    }

    /**
     * Removes this Geom from the ZERO space and adds it to the given one. This
     * methods is automatically called from Space.addGeom and should not be
     * called directly. An assert will cause an exception to be thrown if this
     * Geom already belongs to a space.
     *
     * @see Space#addGeom
     */
    protected void addToSpace( Space space ) {
        // assert spaceId != null: "Can't add a Geom to a space if it already
        // belongs to one";

        if ( isEncapsulated ) {
            System.out
                    .println( "Odejava: Warning - an Encapsulated geom was added to a space.  You probably don't want to use the parent GeomTransform instead." );
        }

        spaceId = space.getId();
        this.space = space;
        Ode.dSpaceAdd( spaceId, geomId );

    }

    /**
     * Obtain the physics object that owns this geom
     *
     * @return physics for this geom
     */
    public PhysicsNode getPhysicsObject() {
        return physicsObject;
    }

    /**
     * Set the physics object that owns this geom
     *
     * @param value new owner
     */
    public void setPhysicsObject( PhysicsNode value ) {
        physicsObject = value;
    }

    /**
     * Get the graphical object that owns this geom which can be the whole or
     * part of the spatial that the physics object represents.
     *
     * @return the graphical representation
     */
    public PhysicsCollisionGeometry getGeometry() {
        return geometry;
    }

    /**
     * Set the graphical object that owns this geom which can be the whole or
     * part of the spatial that the physics object represents.
     *
     * @param geometry new graphics representation
     */
    public void setGeometry( PhysicsCollisionGeometry geometry ) {
        this.geometry = geometry;
    }

    public void setUserObject( Object props ) {
        this.userObject = props;
    }

    public Object getUserObject() {
        return userObject;
    }
}
