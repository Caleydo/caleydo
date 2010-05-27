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

package com.jmex.physics.impl.ode;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jmex.physics.*;
import com.jmex.physics.Joint;
import com.jmex.physics.contact.CompleteContactInfo;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.geometry.*;
import com.jmex.physics.impl.ode.geometry.*;
import com.jmex.physics.impl.ode.joints.OdeJoint;
import org.odejava.*;
import org.odejava.collision.Contact;
import org.odejava.collision.JavaCollision;
import org.odejava.ode.Ode;
import org.odejava.ode.OdeConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Irrisor
 */
public class OdePhysicsSpace extends PhysicsSpace {
    private PendingContact contact = new PendingContact() {
        public Vector3f getContactNormal( Vector3f store ) {
            if ( store == null ) {
                store = new Vector3f();
            }
            return odeContact.getNormal( store );
        }            

        public Vector3f getContactPosition( Vector3f store ) {
            if ( store == null ) {
                store = new Vector3f();
            }
            return odeContact.getPosition( store );
        }

        public PhysicsCollisionGeometry getGeometry1() {
            return odeContact.getGeom1().getGeometry();
        }

        public PhysicsCollisionGeometry getGeometry2() {
            return odeContact.getGeom2().getGeometry();
        }

        public PhysicsNode getNode1() {
            return odeContact.getGeom1().getPhysicsObject();
        }

        public PhysicsNode getNode2() {
            return odeContact.getGeom2().getPhysicsObject();
        }

        public float getPenetrationDepth() {
            return odeContact.getDepth();
        }

        public void getDefaultFrictionDirections( Vector3f primaryStore, Vector3f secondaryStore ) {
            Vector3f n = getContactNormal( tmp2 );
            computeODEDefaultFrictionDirection( n, primaryStore, secondaryStore );
        }

        public float getTime() {
            return timeSinceStartOfUpdate;
        }
    };

    private static void computeODEDefaultFrictionDirection( Vector3f normal, Vector3f primary, Vector3f secondary ) {
        if ( FastMath.abs( normal.z ) > SQRT_2 ) {
            // choose p in y-z plane
            float a = normal.y * normal.y + normal.z * normal.z;
            float k = FastMath.invSqrt( a );
            primary.x = 0;
            primary.y = -normal.z * k;
            primary.z = normal.y * k;
            // set q = n x p
            if ( secondary != null ) {
                secondary.x = a * k;
                secondary.y = -normal.x * primary.z;
                secondary.z = normal.x * primary.y;
            }
        } else {
            // choose p in x-y plane
            float a = normal.x * normal.x + normal.y * normal.y;
            float k = FastMath.invSqrt( a );
            primary.x = -normal.y * k;
            primary.y = normal.x * k;
            primary.z = 0;
            // set q = n x p
            if ( secondary != null ) {
                secondary.x = -normal.z * primary.y;
                secondary.y = normal.z * primary.x;
                secondary.z = a * k;
            }
        }
    }

    private float timeSinceStartOfUpdate;
    public static final float SQRT_2 = FastMath.sqrt( 2 );

    public Body createBody( String name ) {
        return new Body( name, world );
    }

    public void deleteBody( Body body ) {
        body.delete();
    }

    public void addGeom( Geom geom, OdeCollisionGroup group ) {
        group.getOdeSpace().addGeom( geom );
        PhysicsCollisionGeometry geometry = geom.getGeometry();
        if ( geometry instanceof OdeMesh ) {
            OdeMesh mesh = (OdeMesh) geometry;
            triMeshes.add( mesh );
        }
    }

    public void removeGeom( Geom geom, OdeCollisionGroup group ) {
        group.getOdeSpace().remove( geom );
        PhysicsCollisionGeometry geometry = geom.getGeometry();
        if ( geometry instanceof OdeMesh ) {
            OdeMesh mesh = (OdeMesh) geometry;
            triMeshes.remove( mesh );
        }
    }

    static class OdeFactory implements Factory {
        public OdeFactory() {
            setFactory( this );
        }

        public PhysicsSpace create() {
            OdePhysicsSpace space = new OdePhysicsSpace();
            space.setUpdateRate( 100 );
            space.setStepSize( 1 / 100f );
            return space;
        }

        public String getImplementationName() {
            return "ODE";
        }

        public String getImplementationVersion() {
            Odejava.init();
            return "(ODEJava version " + OdeConstants.ODEJAVA_VERSION + ")";
        }
    }

    @Override
    public DynamicPhysicsNode createDynamicNode() {
        DynamicPhysicsNodeImpl dynamicPhysicsNode = new DynamicPhysicsNodeImpl( this );
        dynamicPhysicsNode.setCollisionGroup( defaultCollisionGroup );
        dynamicPhysicsNode.setActive( true );
        return dynamicPhysicsNode;
    }

    @Override
    public StaticPhysicsNode createStaticNode() {
        StaticPhysicsNodeImpl staticPhysicsNode = new StaticPhysicsNodeImpl( this );
        staticPhysicsNode.setCollisionGroup( staticCollisionGroup );
        staticPhysicsNode.setActive( true );
        return staticPhysicsNode;
    }

    private ArrayList<OdeJoint> joints = new ArrayList<OdeJoint>();
    private List<? extends Joint> jointsImmutable = Collections.unmodifiableList( joints );

    @Override
    public Joint createJoint() {
        OdeJoint joint = new OdeJoint( this );
        addJoint( joint );
        return joint;
    }

    @Override
    protected void addJoint( Joint joint ) {
        if ( !joint.isActive() ) {
            OdeJoint odeJoint = (OdeJoint) joint;
            super.addJoint( joint );
            joints.add( odeJoint );
        }
    }

    @Override
    protected void removeJoint( Joint joint ) {
        super.removeJoint( joint );
        //noinspection SuspiciousMethodCalls
        joints.remove( joint );
    }


    /**
     * This is an ODE-specific method and may be removed (or moved) if other dynamics implementations are used
     *
     * @return the ODEJava World object
     */
    public World getODEJavaWorld() {
        return world;
    }

    /**
     * The slowest but most accurate step function
     */
    public static final int SF_STEP_SIMULATION = 0;

    /**
     * A faster but lees accurate alternative
     */
    public static final int SF_STEP_FAST = 2;

    /**
     * The step function that has superseded SF_STEP_FAST
     */
    public static final int SF_STEP_QUICK = 1;

    // Signals which one of the above that is to be used.
    private int stepFunction;

    // The space to add bodys to.
    private World world;

    private OdeCollisionGroup defaultCollisionGroup;
    private OdeCollisionGroup staticCollisionGroup;

    // collision system
    private JavaCollision collision;

    // helper class to read/write collisional data
    private Contact odeContact;

    // Keeps track of all the PhysicsObjects in this world.
    private final ArrayList<PhysicsNode> physicsNodes;
    private final ArrayList<OdeMesh> triMeshes = new ArrayList<OdeMesh>();
    private final List<? extends PhysicsNode> physicsNodesImmutable;

    // Varables used to govern the frequency of update calls.
    private float updateRate;

    private float elapsedTime;

    private Vector3f tmp1 = new Vector3f();
    private Vector3f tmp2 = new Vector3f();
    private Vector3f tmpBounceVel = new Vector3f();

    /**
     * Initialises the world, space and gravity. Sets some default values: <br>
     * gravity = Earths <br>
     * step interactions = 15 <br>
     * step size = 0.02 <br>
     * update rate = 0 (update as fast as possible) <br>
     * step function = SF_STEP_QUICK
     */
    public OdePhysicsSpace() {
        // initialise ODE
        Odejava.init();

        // Create the ODE world which we'll add bodys to.
        world = new World();
        setAutoRestThreshold( Float.NaN );

        staticCollisionGroup = createCollisionGroup( "static" );

        defaultCollisionGroup = createCollisionGroup( "default" );
        defaultCollisionGroup.collidesWith( defaultCollisionGroup, true );

        defaultCollisionGroup.collidesWith( staticCollisionGroup, true );

        // Set the default gravity.
        world.setGravity( 0, -9.81f, 0 );

        // According to the odejava doc, setting this will remove
        // some sort of weirdness that would otherwise occur...
        world.setContactSurfaceThickness( 0.001f );

        // Set the default step interactions.
        world.setStepInteractions( 15 );

        // Set a default step size.
        world.setStepSize( 0.02f );

        // Set the default step function.
        stepFunction = SF_STEP_QUICK; //TODO: make this selectable in interface? 

        // use collisions
        collision = new JavaCollision( world );
        Odejava.setMaxContactGeomsPerNearcallback( 200 );

        // create the DirectBuffer
        odeContact = new Contact( collision.getContactLongBuffer(), collision.getContactFloatBuffer() );

        // create the results
//        contactInfos = new ArrayList<ContactInfo>();

        // create the collision pair list
        physicsNodes = new ArrayList<PhysicsNode>();
        physicsNodesImmutable = Collections.unmodifiableList( physicsNodes );

        // Default to no UPS restriction.
        updateRate = -1;
        elapsedTime = 0;
    }

    public void setAutoRestThreshold( float threshold ) {
        if ( !Float.isNaN( threshold ) )
        {
            world.setAutoDisableBodies( true );
            Ode.dWorldSetAutoDisableLinearThreshold( world.getId(), threshold );
            Ode.dWorldSetAutoDisableAngularThreshold( world.getId(), threshold );
            Ode.dWorldSetAutoDisableSteps( world.getId(), 20 );
            Ode.dWorldSetAutoDisableTime( world.getId(), 0.5f );
            for ( PhysicsNode physicsNode : physicsNodes ) {
                if ( physicsNode instanceof DynamicPhysicsNodeImpl )
                {
                    DynamicPhysicsNodeImpl dynamicPhysicsNode = (DynamicPhysicsNodeImpl) physicsNode;
                    final Body body = dynamicPhysicsNode.getBody();
                    body.setAutoDisable( true );
                }
            }
        } else
        {
            world.setAutoDisableBodies( false );
        }
    }

    @Override
    public void addNode( PhysicsNode node ) {
        super.addNode( node );

        // log some stuff:
        Logger logger = Logger.getLogger( PhysicsSpace.LOGGER_NAME );
        if ( logger.isLoggable( Level.INFO ) ) {
            logger.log( Level.INFO,
                    "PhysicsNode (" + node.getName() + ") has been added" );
        }

        // add it to the arraylist
        physicsNodes.add( node );
    }

    /**
     * Removes a <code>PhysicsObject</code> from the simulation.
     *
     * @param obj The object to remove.
     */
    protected void removeNode( PhysicsNode obj ) {

        if ( !physicsNodes.remove( obj ) ) {
            return;
        }
        super.removeNode( obj );

        // print out a statement
        Logger logger = Logger.getLogger( PhysicsSpace.LOGGER_NAME );
        if ( logger.isLoggable( Level.INFO ) ) {
            logger.log( Level.INFO,
                    "PhysicsObject ("
                            + obj.getName()
                            + ") has been removed from PhysicsWorld and will no longer take place in the simulation" );
        }
    }

    /**
     * Set how much to step by each time.
     *
     * @param stepSize The step size to set. Default is 0.02.
     */
    public void setStepSize( float stepSize ) {
        world.setStepSize( stepSize );
        Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.INFO,
                "Setting StepSize of PhysicsWorld to : " + stepSize );
    }

    /**
     * Obtains how far to step by each update call.
     *
     * @return The current step size. Default is 0.02.
     */
    public float getStepSize() {
        return world.getStepSize();
    }

    /**
     * Sets how many updates per second that is wanted. Default is running as
     * fast as possible (i.e. -1).
     *
     * @param ups number of steps per second Note: cannot be 0.
     */
    public void setUpdateRate( float ups ) {
        // updateRate = timer.getResolution() / ups;
        updateRate = 1.0f / ups;
        Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.INFO,
                "Changed the number of updates in a second of PhysicsWorld to: " + ups );
    }

    /**
     * Returns how many updates per second we run at. Default is running as fast
     * as possible (-1).
     *
     * @return The updaterate. Default is -1.
     */
    public int getUpdateRate() {
        // return (int) (updateRate * timer.getResolution());
        return (int) ( 1.0f / updateRate );
    }

    /**
     * Temp variables to flatline memory usage.
     */
    private final Quaternion inverseWorldRotation = new Quaternion();

    /**
     * Sets the world translation of a spatial according to a supplied body.
     * This is done by going "the other way around"; figuring
     * out what its local translation has to be in order for the world
     * translation to be the same as the one of the body.
     *
     * @param spat             The Spatial to synchronize.
     * @param worldTranslation world translation
     * @param store            vector that receives the result
     */
    void worldToLocal( Spatial spat, Vector3f worldTranslation, Vector3f store ) {
        if ( spat.getParent() != null ) {
            store.set( worldTranslation ).subtractLocal( spat.getParent().getWorldTranslation() );
            store.divideLocal( spat.getParent().getWorldScale() );
            inverseWorldRotation.set( spat.getParent().getWorldRotation() ).inverseLocal()
                    .multLocal( store );
        } else {
            store.set( worldTranslation );
        }
    }

    /**
     * Sets the world rotation of a spatial according to a supplied body.
     * This is done by going "the other way around"; figuring
     * out what its local rotation has to be in order for the world
     * rotation to be the same as the one of the body.
     *
     * @param spat          The Spatial to synchronize.
     * @param worldRotation new world rotation
     */
    void setLocalRotationFromWorldRotation( Spatial spat, Quaternion worldRotation ) {
        Quaternion tmpQuat = spat.getLocalRotation();
        if ( spat.getParent() != null ) {
            inverseWorldRotation.set( spat.getParent().getWorldRotation() ).inverseLocal().mult(
                    worldRotation, tmpQuat );
        } else {
            tmpQuat.set( worldRotation );
        }
    }

    @Override
    public List<? extends PhysicsNode> getNodes() {
        return physicsNodesImmutable;
    }

    @Override
    public List<? extends Joint> getJoints() {
        return jointsImmutable;
    }

    public void delete() {
        removeAllFromUpdateCallbacks();
        removeAllObjects();
        removeAllJoints();
    }

    public void pick( PhysicsSpatial spatial ) {
        if ( spatial instanceof PhysicsCollisionGeometry )
        {
            pick( ( PhysicsCollisionGeometry) spatial );
        }
        else
        {
            pick( ( PhysicsNode) spatial );
        }
    }

    private void removeAllJoints() {

        @SuppressWarnings("unchecked")
        List<Joint> list = (List<Joint>) joints.clone();
        for ( Joint joint : list ) {
            removeJoint( joint );
        }
    }

    /**
     * Steps the world forward. Perform all calculations and detect collisions.
     * Should be called every frame.
     * <p/>
     * Note, that if you have set an update rate, this method will check with
     * the timer if it should do any logic. If not, it escapes. If the time
     * elapsed is greater than the update interval it computes more than one
     * step.
     * </p>
     *
     * @param tpf time that has elapsed since the last call of update
     */
    @Override
    public void update( float tpf ) {

        if ( tpf < 0 ) {
            tpf = 0;
        }
        timeSinceStartOfUpdate = 0;
        elapsedTime += tpf;

//        contactInfos.clear();

        boolean updated = false;
        while ( elapsedTime >= updateRate ) {
            if ( !updated ) {
                reuseContactInfos(); //FIX ME: deactivate triggers using this infos? keep infos for active triggers?
                for ( OdeJoint joint : joints ) {
                    joint.updateJointType();
                }
            }

            // call update callbacks
            for ( int i = this.sizeOfUpdateCallbacks() - 1; i >= 0; i-- ) {
                PhysicsUpdateCallback updateCallback = this.getFromUpdateCallbacks( i );
//                if ( !updated ) {
//                    updateCallback.beforeUpdate( this );
//                }
                updateCallback.beforeStep( this, world.getStepSize() );
            }
            updated = true;

            computeTimeStep();

            if ( this.sizeOfUpdateCallbacks() > 0 ) {
                updateScene();
            }

            // call update callbacks
            for ( int i = this.sizeOfUpdateCallbacks() - 1; i >= 0; i-- ) {
                PhysicsUpdateCallback updateCallback = this.getFromUpdateCallbacks( i );
                updateCallback.afterStep( this, world.getStepSize() );
            }

            if ( updateRate > 0 ) {
                elapsedTime -= updateRate;
                timeSinceStartOfUpdate += updateRate;
            } else {
                elapsedTime = 0;
                break;
            }
        }

        if ( updated ) {
            if ( this.sizeOfUpdateCallbacks() == 0 ) {
                updateScene();
            } else {
                // call update callbacks
//                for ( int i = this.sizeOfUpdateCallbacks() - 1; i >= 0; i-- ) {
//                    PhysicsUpdateCallback updateCallback = this.getFromUpdateCallbacks( i );
//                    updateCallback.afterUpdate( this );
//                }
            }
//            lastContactInfos = contactInfos;
//            return contactInfos;
        }
    }

    public void pick( PhysicsCollisionGeometry geometry ) {
        OdeGeometry odeGeometry = (OdeGeometry) geometry;
        for ( int i = 0; i < getCollisionGroups().size(); i++ ) {
            OdeCollisionGroup group =  getCollisionGroups().get( i );
            collision.collide2( group.getOdeSpace(), odeGeometry.getOdeGeom() );
            iterateContacts( false );
        }

    }


    public boolean collide( PhysicsSpatial spatial1, PhysicsSpatial spatial2 ) {
        if ( spatial1 instanceof PhysicsCollisionGeometry )
        {
            if ( spatial2 instanceof PhysicsCollisionGeometry )
            {
                OdeGeometry odeGeometry1 = (OdeGeometry) spatial1;
                OdeGeometry odeGeometry2 = (OdeGeometry) spatial2;
                collision.collide2( odeGeometry1.getOdeGeom(), odeGeometry2.getOdeGeom() );
                return iterateContacts( false );
            }
            else
            {
                return collide( spatial2, spatial1 );
            }
        }
        else
        {
            boolean collides = false;
            PhysicsNode node = (PhysicsNode) spatial1;
            if ( node.isStatic() )
            {
                StaticPhysicsNodeImpl odeNode = (StaticPhysicsNodeImpl) node;
                for ( Geom geom : odeNode.getGeoms() ) {
                    //TODO: there is a more efficient way to check the geometries if the entity has got a space!
                    collides |= collide( geom.getGeometry(), spatial2 );
                }
            }
            else
            {
                DynamicPhysicsNodeImpl odeNode = (DynamicPhysicsNodeImpl) node;
                for ( Spatial spatial : odeNode.getChildren() ) {
                    if ( spatial instanceof OdeGeometry )
                    {
                        OdeGeometry geometry = (OdeGeometry) spatial;
                        final Geom geom = geometry.getOdeGeom();
                        if ( geom instanceof PlaceableGeom )
                        {
                            // apply world coordinates to geom
                            PlaceableGeom placeableGeom = (PlaceableGeom) geom;
                            placeableGeom.setPosition( spatial.getWorldTranslation() );
                            placeableGeom.setQuaternion( spatial.getWorldRotation() );
                        }
                        collides |= collide( (PhysicsCollisionGeometry) spatial, spatial2 );

                        if ( geom instanceof PlaceableGeom )
                        {
                            // set back to default coordinates
                            odeNode.updateTransforms( (PlaceableGeom) geom );
                        }
                    }
                }
            }
            return collides;
        }
    }

    public void pick( PhysicsNode node ) {
        if ( node.isStatic() ) {
            StaticPhysicsNodeImpl odeNode = (StaticPhysicsNodeImpl) node;
            for ( Geom geom : odeNode.getGeoms() ) {
                //TODO: there is a more efficient way to check the geometries if the entity has got a space!
                pick( geom.getGeometry() );
            }
        } else {
            DynamicPhysicsNodeImpl odeNode = (DynamicPhysicsNodeImpl) node;
            for ( Spatial spatial : odeNode.getChildren() ) {
                if ( spatial instanceof OdeGeometry )
                {
                    OdeGeometry geometry = (OdeGeometry) spatial;
                    final Geom geom = geometry.getOdeGeom();
                    if ( geom instanceof PlaceableGeom )
                    {
                        // apply world coordinates to geom
                        PlaceableGeom placeableGeom = (PlaceableGeom) geom;
                        placeableGeom.setPosition( spatial.getWorldTranslation() );
                        placeableGeom.setQuaternion( spatial.getWorldRotation() );
                    }

                    for ( int i = 0; i < getCollisionGroups().size(); i++ ) {
                        OdeCollisionGroup group =  getCollisionGroups().get( i );
                        collision.collide2( group.getOdeSpace(), geom );
                        iterateContacts( false );
                    }

                    if ( geom instanceof PlaceableGeom )
                    {
                        // set back to default coordinates
                        odeNode.updateTransforms( (PlaceableGeom) geom );
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<OdeCollisionGroup> getCollisionGroups() {
        return (List<OdeCollisionGroup>) super.getCollisionGroups();
    }

    protected int addCollisionGroup( CollisionGroup value ) {
        int index = super.addCollisionGroup( value );
        (( OdeCollisionGroup ) value ).setIndex( index );
        return index;

    }

    protected void removeCollisionGroup( CollisionGroup value ) {
        super.removeCollisionGroup( value );

        List<OdeCollisionGroup> collisionGroups = getCollisionGroups();
        for ( int i = 0; i < collisionGroups.size(); i++ ) {
            OdeCollisionGroup group = collisionGroups.get( i );
            group.setIndex( i );
        }
    }

    @SuppressWarnings({"ForLoopReplaceableByForEach"})
    private void computeTimeStep() {
        appliedContacts = 0;
        collision.emptyContactGroup();

        List<OdeCollisionGroup> collisionGroups = getCollisionGroups();
        for ( int i = 0; i < collisionGroups.size(); i++ ) {
            OdeCollisionGroup group = collisionGroups.get( i );
            List<CollisionGroup> collidesWith = group.getCollidesWith();
            for ( int j = 0; j < collidesWith.size(); j++ ) {
                OdeCollisionGroup otherGroup = (OdeCollisionGroup) collidesWith.get( j );
                boolean iterate;
                if ( otherGroup == group )
                {
                    collision.collide( group.getOdeSpace() );
                    iterate = collision.getContactCount() > 0;
                }
                else if ( otherGroup.getIndex() > group.getIndex() ) //avoid colliding A vs B _and_ B vs A
                {
                    collision.collide2( group.getOdeSpace(), otherGroup.getOdeSpace() );
                    iterate = collision.getContactCount() > 0;
                }
                else
                {
                    iterate = false;
                }
                if ( iterate )
                {
                    // read and modify contact information
                    iterateContacts( true );

                    // apply the contact information for the collisions
                    collision.applyContacts();
                }
            }
        }

        for ( int i = triMeshes.size() - 1; i >= 0; i-- ) {
            OdeMesh mesh = triMeshes.get( i );
            mesh.updateOdeLastTransformation();
        }

        try
        {
            // See what step function to use and then step through the world.
            if ( stepFunction == SF_STEP_QUICK ) {
                world.quickStep();
            } else if ( stepFunction == SF_STEP_FAST ) {
                world.stepFast();
            } else if ( stepFunction == SF_STEP_SIMULATION ) {
                world.step();
            }
        } catch ( StackOverflowError e )
        {
            throw new StackOverflowError( "The ODE step function could not allocate enough stack memory to compute " +
                    "the time step!\n" +
                    "This is usually caused by low stack size or by too many applied " +
                    "contacts respectively (number of contacts in this step was " + appliedContacts + ").\n" +
                    "Note, the stack size of threads other than the \"main\" thread can be raised with the VM paremeter -Xss." );
        }
    }

    private void updateScene() {
        for ( int i = physicsNodes.size() - 1; i >= 0; i-- ) {
            PhysicsNode node = physicsNodes.get( i );
            ( (OdePhysicsNode) node ).sceneFromOde();
            if ( node.getParent() == null ) {
                node.updateGeometricState( 0, true );
            }
        }
    }

    private final Vector2f tmpVec2 = new Vector2f();
    private int appliedContacts;

    /**
     * Loop through the collisions that have occured and set the contact
     * information. Eg. Bounce factor.
     *
     * @param applyContacts false to do event handling only
     * @return true if contacts were found
     */
    private boolean iterateContacts( boolean applyContacts ) {
        PhysicsNode previous1 = null;
        PhysicsNode previous2 = null;
        int appliedContacts = this.appliedContacts;
        for ( int i = 0; i < collision.getContactCount(); i++ ) {
            // set the index
            odeContact.setIndex( i );

            final Geom geom1 = odeContact.getGeom1();
            final Geom geom2 = odeContact.getGeom2();
            if ( geom1 == null || geom2 == null ) {
                odeContact.ignoreContact();
                continue;
            }
            PhysicsNode obj1 = geom1.getPhysicsObject();
            PhysicsNode obj2 = geom2.getPhysicsObject();

            {
                // don't let rays generate contact points in ode but generate events
                boolean applyThisContact = applyContacts && !(geom1 instanceof GeomRay) && !(geom2 instanceof GeomRay);
                contact.clear();

                if ( applyThisContact ) {
                    adjustContact( contact );
                }

                applyThisContact &= contact.isApplied();
                if ( contact.isIgnored() ) {
                    odeContact.ignoreContact();
                } else {
                    if ( !applyThisContact ) {
                        // don't apply contact points in but generate events 
                        odeContact.ignoreContact();
                    } else {
                        appliedContacts++;

                        if ( !Float.isNaN( contact.getMu() ) ) {
                            odeContact.setMu( contact.getMu() );
                        } else {
                            odeContact.setMu( 100 );
                        }

                        int mode = Ode.dContactApprox1;
                        if ( !Float.isNaN( contact.getBounce() ) ) {
                            mode |= Ode.dContactBounce;
                            odeContact.setMode( mode );
                            odeContact.setBounce( contact.getBounce() );
                        }
                        if ( !Float.isNaN( contact.getMinimumBounceVelocity() ) ) {
                            odeContact.setBounceVel( contact.getMinimumBounceVelocity() );
                        } else {
                            odeContact.setBounceVel( 1f );
                        }
                        Vector3f fdir = contact.getFrictionDirection( tmp2 );
                        if ( !Float.isNaN( fdir.x ) && !Float.isNaN( fdir.y ) && !Float.isNaN( fdir.z ) ) {
                            mode |= Ode.dContactFDir1;
                            odeContact.setFdir1( fdir );
                        }
                        if ( !Float.isNaN( contact.getMuOrthogonal() ) && (mode & Ode.dContactFDir1)!=0 ) {
                            mode |= Ode.dContactMu2;
                            odeContact.setMu2( contact.getMuOrthogonal() );
                        }
                        Vector2f surfaceMotion = contact.getSurfaceMotion( tmpVec2 );
                        if ( !Float.isNaN( surfaceMotion.x ) && surfaceMotion.x != 0 ) {
                            mode |= Ode.dContactMotion1;
                            odeContact.setMotion1( surfaceMotion.x );
                        }
                        if ( !Float.isNaN( surfaceMotion.y ) && surfaceMotion.y != 0 ) {
                            mode |= Ode.dContactMotion2;
                            odeContact.setMotion2( surfaceMotion.y );
                        }
                        Vector2f slip = contact.getSlip( tmpVec2 );
                        if ( !Float.isNaN( slip.x ) ) {
                            mode |= Ode.dContactSlip1;
                            odeContact.setSlip1( slip.x );
                        }
                        if ( !Float.isNaN( slip.y ) ) {
                            mode |= Ode.dContactSlip2;
                            odeContact.setSlip2( slip.y );
                        }
                        if ( !Float.isNaN( contact.getSpringConstant() ) && !Float.isNaN( contact.getDampingCoefficient() ) ) {
                            mode |= Ode.dContactSoftCFM | Ode.dContactSoftERP;
                            float r = contact.getDampingCoefficient();
                            float hk = getODEJavaWorld().getStepSize() * contact.getSpringConstant();

                            odeContact.setSoftErp( hk / ( hk + r ) );
                            odeContact.setSoftCfm( 1.0f / ( hk + r ) );
                        }
                        odeContact.setMode( mode );
//                        else {
//                            odeContact.setSoftErp( getODEJavaWorld().getErrorReductionParameter() );
//                            odeContact.setSoftCfm( getODEJavaWorld().getConstraintForceMix() );
//                        }
                    }

                    if ( ( obj1 != previous1 ) || ( obj2 != previous2 ) ) {
                        // compute collision velocity (must be done before
                        // returning contact)
                        // todo: this takes quite some time - could be skipped if not needed
                        Vector3f actualBounceVel = tmpBounceVel.set( 0, 0, 0 );
                        if ( obj1 != null && !obj1.isStatic() ) {
                            actualBounceVel.subtractLocal( ( (DynamicPhysicsNode) obj1 )
                                    .getLinearVelocity( tmp1 ) );
                        }
                        if ( obj2 != null && !obj2.isStatic() ) {
                            actualBounceVel.addLocal( ( (DynamicPhysicsNode) obj2 )
                                    .getLinearVelocity( tmp1 ) );
                        }
                        actualBounceVel.multLocal( odeContact.getNormal( tmp2 ) );
                        //TODO: allow to trash contacts if results are not needed
                        CompleteContactInfo info = reusableContactInfo( contact );
                        collisionEvent( info );
//                        contactInfos.add( info );
                    }
                }
            }
        }
        this.appliedContacts = appliedContacts;
        return collision.getContactCount() > 0;
    }

    private static class ReusableContactInfo extends MutableContactInfo implements CompleteContactInfo {
        ReusableContactInfo next;
        private final Vector3f normal = new Vector3f();
        private final Vector3f position = new Vector3f();

        public Vector3f getContactNormal( Vector3f store ) {
            if ( store == null ) {
                store = new Vector3f();
            }
            return store.set( normal );
        }

        public Vector3f getContactPosition( Vector3f store ) {
            if ( store == null ) {
                store = new Vector3f();
            }
            return store.set( position );
        }

        public Vector3f getContactVelocity( Vector3f store ) {
            if ( store == null ) {
                store = new Vector3f();
            }
            PendingContact.computeContactVelocity( this, store );
            return store;
        }

        private PhysicsCollisionGeometry geometry1;
        private PhysicsCollisionGeometry geometry2;
        private PhysicsNode node1;
        private PhysicsNode node2;

        public PhysicsCollisionGeometry getGeometry1() {
            return geometry1;
        }

        public PhysicsCollisionGeometry getGeometry2() {
            return geometry2;
        }

        public PhysicsNode getNode1() {
            return node1;
        }

        public PhysicsNode getNode2() {
            return node2;
        }

        private float depth;
        private float time;

        public float getPenetrationDepth() {
            return depth;
        }

        public void getDefaultFrictionDirections( Vector3f primaryStore, Vector3f secondaryStore ) {
            computeODEDefaultFrictionDirection( normal, primaryStore, secondaryStore );
        }

        public float getTime() {
            return time;
        }

        void set( CompleteContactInfo info ) {
            super.copy( info );
            info.getContactNormal( normal );
            info.getContactPosition( position );
            geometry1 = info.getGeometry1();
            geometry2 = info.getGeometry2();
            node1 = info.getNode1();
            node2 = info.getNode2();
            depth = info.getPenetrationDepth();
            time = info.getTime();
        }

        @Override
        public String toString() {
            return "ContactInfo: " + getGeometry1() + " <-> " + getGeometry2();
        }
    }

    private ReusableContactInfo usedContactInfo;
    private ReusableContactInfo lastUsedContactInfo;
    private ReusableContactInfo availableContactInfo;

    private ReusableContactInfo usedContactInfo2;
    private ReusableContactInfo lastUsedContactInfo2;
    private ReusableContactInfo availableContactInfo2;

    private void reuseContactInfos()
    {
        swapContactInfos();
        ReusableContactInfo lastUsed = lastUsedContactInfo;
        if ( lastUsed != null )
        {
            lastUsed.next = availableContactInfo;
            availableContactInfo = usedContactInfo;
            usedContactInfo = null;
            lastUsedContactInfo = null;
        }
    }

    /**
     * Use the contact infos every second update to prevent input handler problems.
     */
    private void swapContactInfos() {
        ReusableContactInfo usedContactInfo = this.usedContactInfo;
        ReusableContactInfo lastUsedContactInfo = this.lastUsedContactInfo;
        ReusableContactInfo availableContactInfo = this.availableContactInfo;
        this.usedContactInfo = this.usedContactInfo2;
        this.lastUsedContactInfo = this.lastUsedContactInfo2;
        this.availableContactInfo = this.availableContactInfo2;
        this.usedContactInfo2 = usedContactInfo;
        this.lastUsedContactInfo2 = lastUsedContactInfo;
        this.availableContactInfo2 = availableContactInfo;
    }

    private CompleteContactInfo reusableContactInfo( CompleteContactInfo info ) {
        ReusableContactInfo reusableContactInfo = availableContactInfo;
        if ( reusableContactInfo == null )
        {
            reusableContactInfo = new ReusableContactInfo();
        }
        else
        {
            ReusableContactInfo next = reusableContactInfo.next;
            availableContactInfo = next;
        }
        ReusableContactInfo nextUsed = usedContactInfo;
        reusableContactInfo.next = nextUsed;
        usedContactInfo = reusableContactInfo;
        if ( nextUsed == null )
        {
            lastUsedContactInfo = reusableContactInfo;
        }

        reusableContactInfo.set( info );
        return reusableContactInfo;
    }

    /**
     * Change the directional gravity. Earths gravity = (0, -9.81, 0)
     *
     * @param gravity The gravity vector.
     */
    @Override
    public void setDirectionalGravity( Vector3f gravity ) {
        world.setGravity( gravity.x, gravity.y, gravity.z );

        Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(
                Level.INFO,
                "Setting Gravity of PhysicsWorld to: (" + gravity.x + ", " + gravity.y + ", "
                        + gravity.z + ")" );
    }

    @Override
    public Vector3f getDirectionalGravity( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return world.getGravity( store );
    }

    /**
     * Number of interactions, higher gives better accuracy but lower speed in
     * case of many simultaneous collisions. Default is 15.
     *
     * @param stepInteractions The stepInteractions to set.
     */
    public void setStepInteractions( int stepInteractions ) {
        world.setStepInteractions( stepInteractions );
        Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.INFO,
                "Setting the number of interactions of PhysicsWorld to: " + stepInteractions );
    }

    /**
     * Gets the stepInteractions of this world.
     *
     * @return The stepInteractions of this world.
     */
    public int getStepInteractions() {
        return world.getStepInteractions();
    }

    /**
     * Sets the method which we use to update the world. It can be one of the
     * following: - SF_STEP_SIMULATION (The slowest but most accurate step
     * function) - SF_STEP_FAST (A faster but lees accurate alternative) -
     * SF_STEP_QUICK (The step function that has superseded SF_STEP_FAST)
     * Default is SF_STEP_QUICK.
     *
     * @param stepFunction The stepFunction to set.
     */
    public void setStepFunction( int stepFunction ) {
        this.stepFunction = stepFunction;

        if ( stepFunction == SF_STEP_FAST ) {
            Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.INFO, "Setting the Physics Solver to use StepFast" );
        } else if ( stepFunction == SF_STEP_QUICK ) {
            Logger.getLogger( PhysicsSpace.LOGGER_NAME )
                    .log( Level.INFO, "Setting the Physics Solver to use StepQuick" );
        } else {
            Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.INFO,
                    "Setting the Physics Solver to use Simulation" );
        }

    }

    /**
     * Gets the method which we use to update the world.
     *
     * @return The current step function being used.
     */
    public int getStepFunction() {
        return stepFunction;
    }

    /**
     * Removes all PhysicsObjects in the world. Could be useful for
     * level-switching and such.
     */
    public void removeAllObjects() {
        @SuppressWarnings("unchecked")
        List<PhysicsNode> list = (List<PhysicsNode>) physicsNodes.clone();
        for ( PhysicsNode node : list ) {
            removeNode( node );
        }
    }

    @Override
    protected PhysicsBox createBox( String name, PhysicsNode node ) {
        OdeBox geometry = new OdeBox( node );
        geometry.setName( name );
        if ( node != null ) {
            node.attachChild( geometry );
        }
        return geometry;
    }

    @Override
    protected PhysicsSphere createSphere( String name, PhysicsNode node ) {
        OdeSphere geometry = new OdeSphere( node );
        geometry.setName( name );
        if ( node != null ) {
            node.attachChild( geometry );
        }
        return geometry;
    }

    @Override
    public PhysicsCylinder createCylinder( String name, PhysicsNode node ) {
        OdeCylinder geometry = new OdeCylinder( node );
        geometry.setName( name );
        if ( node != null ) {
            node.attachChild( geometry );
        }
        return geometry;
    }

    @Override
    public PhysicsCapsule createCapsule( String name, PhysicsNode node ) {
        OdeCapsule geometry = new OdeCapsule( node );
        geometry.setName( name );
        if ( node != null ) {
            node.attachChild( geometry );
        }
        return geometry;
    }

    @Override
    public PhysicsMesh createMesh( String name, PhysicsNode node ) {
        OdeMesh geometry = new OdeMesh( node );
        geometry.setName( name );
        if ( node != null ) {
            node.attachChild( geometry );
        }
        return geometry;
    }

    @Override
    public PhysicsRay createRay( String name, PhysicsNode node ) {
        OdeRay geometry = new OdeRay( node );
        geometry.setName( name );
        if ( node != null ) {
            node.attachChild( geometry );
        }
        return geometry;
    }

    public void setAccuracy( float value ) {
        setStepSize( value );
        setUpdateRate( 1.f/value );
    }

    public OdeCollisionGroup createCollisionGroup( String name ) {
        return new OdeCollisionGroup( this, name );
    }

    public CollisionGroup getDefaultCollisionGroup() {
        return defaultCollisionGroup;
    }

    public CollisionGroup getStaticCollisionGroup() {
        return staticCollisionGroup;
    }

    public void drawImplementationSpecificPhysics( Renderer renderer ) {
        super.drawImplementationSpecificPhysics( renderer );

        ReusableContactInfo contactInfo = usedContactInfo;
        Vector3f at = new Vector3f();
        Vector3f to = new Vector3f();
        Vector2f surfaceMotion = new Vector2f();
        Vector3f dir1 = new Vector3f();
        Vector3f dir2 = new Vector3f();
        Vector3f normal = new Vector3f();
        while ( contactInfo != null ) {
            contactInfo.getContactPosition( at );

            contactInfo.getSurfaceMotion( surfaceMotion );
            if ( !Float.isNaN( surfaceMotion.x ) || !!Float.isNaN( surfaceMotion.y ) ) {
                to.set( at );
                contactInfo.getFrictionDirection( dir1 );
                contactInfo.getContactNormal( normal );
                dir1.cross( normal, dir2 );
                if ( !Float.isNaN( surfaceMotion.x ) ) {
                    to.scaleAdd( surfaceMotion.x, dir1, to );
                }
                if ( !Float.isNaN( surfaceMotion.y ) ) {
                    to.scaleAdd( surfaceMotion.y, dir2, to );
                }
                PhysicsDebugger.drawArrow( at, to, ColorRGBA.cyan, DisplaySystem.getDisplaySystem().getRenderer() );

                contactInfo = contactInfo.next;
            }

            PhysicsDebugger.drawCross( at, 0.1f, DisplaySystem.getDisplaySystem().getRenderer(),
                    ColorRGBA.cyan, 3 );
        }
    }
}

/*
* $log$
*/
