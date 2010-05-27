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

package com.jmex.physics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.util.SyntheticButton;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.util.export.binary.BinaryClassLoader;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.geometry.PhysicsCapsule;
import com.jmex.physics.geometry.PhysicsCylinder;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.geometry.PhysicsRay;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.material.Material;
import com.jmex.physics.material.MaterialContactCallback;
import com.jmex.physics.util.binarymodules.BinaryDynamicPhysicsNodeModule;
import com.jmex.physics.util.binarymodules.BinaryJointModule;
import com.jmex.physics.util.binarymodules.BinaryPhysicsBoxModule;
import com.jmex.physics.util.binarymodules.BinaryPhysicsCapsuleModule;
import com.jmex.physics.util.binarymodules.BinaryPhysicsCylinderModule;
import com.jmex.physics.util.binarymodules.BinaryPhysicsMeshModule;
import com.jmex.physics.util.binarymodules.BinaryPhysicsSphereModule;
import com.jmex.physics.util.binarymodules.BinaryRotationalJointAxisModule;
import com.jmex.physics.util.binarymodules.BinaryStaticPhysicsNodeModule;
import com.jmex.physics.util.binarymodules.BinaryTranslationalJointAxisModule;

/**
 * PhysicsSpace is the central class of the jME Physics API. {@link PhysicsNode}s from the same space can interact
 * with each other. Multiple spaces can be used to model independent physics simulation parts. <br>
 * PhysicsSpace is used to create PhysicNodes via the {@link #createDynamicNode} and {@link #createStaticNode} methods.
 * <p/>
 * The application must frequently call {@link #update(float)} to let the simulation advance. While update is not called
 * the simulation is paused.
 *
 * @author Irrisor
 */
public abstract class PhysicsSpace {
    public static final String LOGGER_NAME = "com.jmex.physics";
    private static final Logger LOGGER = Logger.getLogger( PhysicsSpace.LOGGER_NAME );

    /**
     * This method is called by the application to process a simulation timestep. This method also calls the methods
     * from {@link PhysicsUpdateCallback} according to their contract.
     *
     * @param time time the simulation should advance
     */
    public abstract void update( float time );

    /**
     * Default constructor.
     */
    protected PhysicsSpace() {
        getContactCallbacks().add( MaterialContactCallback.get() );
    }

    /**
     * list of ContactCallbacks.
     */
    private final List<ContactCallback> contactCallbacks = new ArrayList<ContactCallback>();

    /**
     * Called by {@link #update(float)} to determine contact details. The default implementation iterates the
     * contactCallbacks (descending indexes) to determine the details.
     *
     * @param contact contact that needs details
     * @see PendingContact
     * @see #getContactCallbacks()
     * @see ContactCallback
     */
    protected void adjustContact( PendingContact contact ) {
        // set defaults
        contact.setMu( 1 );
        contact.setBounce( 0.4f );
        contact.setMinimumBounceVelocity( 1f );

        // iterate callbacks if registered
        for ( int i = contactCallbacks.size() - 1; i >= 0; i-- ) {
            ContactCallback callback = contactCallbacks.get( i );
            if ( callback.adjustContact( contact ) ) {
                return;
            }
        }
    }

    /**
     * Add/Remove/Query callbacks for specifying contact details.
     *
     * @see #adjustContact
     */
    public final List<ContactCallback> getContactCallbacks() {
        return contactCallbacks;
    }

    /**
     * Adds a <code>PhysicsNode</code> to this space. A newly created PhysicsNode is automatically added.
     *
     * @param node the PhysicsNode to add
     */
    protected void addNode( PhysicsNode node ) {
        if ( node.getSpace() != this ) {
            throw new IllegalArgumentException( "Nodes can only be added to the space they were created by." );
        }
        node.setActive( true );
    }

    /**
     * Adds a <code>Joint</code> to this space. A newly created Joint is automatically added.
     *
     * @param joint the Joint to add
     */
    protected void addJoint( Joint joint ) {
        if ( joint.getSpace() != this ) {
            throw new IllegalArgumentException( "Joints can only be added to the space they were created by." );
        }
        joint.setActive( true );
    }

    /**
     * Removes a <code>PhysicsNode</code> from this space. Use {@link #addNode(PhysicsNode)} to add it again.
     *
     * @param node the PhysicsNode to remove
     */
    protected void removeNode( PhysicsNode node ) {
        node.setActive( false );
    }

    /**
     * Removes a <code>Joint</code> from this space. Use {@link #addJoint(Joint)} to add it again.
     *
     * @param joint the PhysicsJoint to remove
     */
    protected void removeJoint( Joint joint ) {
        joint.setActive( false );
    }


    /**
     * The default material used for nodes without material set.
     *
     * @return current default material
     */
    public Material getDefaultMaterial() {
        if ( this.defaultMaterial == null ) {
            this.defaultMaterial = Material.DEFAULT;
        }
        return this.defaultMaterial;
    }

    /**
     * store the value for field defaultMaterial
     */
    private Material defaultMaterial;

    /**
     * Change default material for nodes without a material set.
     *
     * @param value new default material
     * @throws IllegalArgumentException if parameter is null
     */
    public void setDefaultMaterial( final Material value ) {
        if ( value != null ) {
            this.defaultMaterial = value;
        } else {
            throw new IllegalArgumentException( "null default material not allowed" );
        }
    }

    /**
     * @return the same list like the last call to {@link #update(float)}
     */
//    public abstract List<ContactInfo> getLastContactInfos();

    /**
     * @return an immutable list of physics nodes in this space
     */
    public abstract List<? extends PhysicsNode> getNodes();

    public abstract List<? extends Joint> getJoints();

    /**
     * Delete all physics for this space.
     */
    public abstract void delete();

    public abstract void pick( PhysicsSpatial spatial );

    public abstract boolean collide( PhysicsSpatial spatial1, PhysicsSpatial spatial2 );

    /**
     * This method sets the "rest" threshold, if the implementation supports ignoring dynamic physics nodes
     * that do not move.
     *
     * @param threshold if this threashold for linear or rotational velocity is not reaching during several simulation
     *                  steps the physics node rests until it collides, is moved or altered in another way
     * @see DynamicPhysicsNode#rest()
     * @see DynamicPhysicsNode#unrest()
     */
    public abstract void setAutoRestThreshold( float threshold );

    public void drawImplementationSpecificPhysics( Renderer renderer ) {

    }

    /**
     * Interface for subclasses to register a factory for them.
     */
    protected interface Factory {
        /**
         * Called by {@link PhysicsSpace#create()} to create an implementation specific instance.
         *
         * @return new instance of PhysicsSpace
         */
        public PhysicsSpace create();

        public String getImplementationName();

        public String getImplementationVersion();
    }


    /**
     * @return factory for creating PhysicsSpaces
     * @see Factory
     */
    protected static Factory getFactory() {
        return PhysicsSpace.factory;
    }

    /**
     * store the value for field factory.
     */
    private static Factory factory;

    /**
     * @param value factory for creating PhysicsSpaces
     * @see Factory
     */
    protected static void setFactory( final Factory value ) {
        final Factory oldValue = PhysicsSpace.factory;
        if ( oldValue != value ) {
            PhysicsSpace.factory = value;
        }
    }

    /**
     * List of known implementation factory class names. Used by {@link #create} to find a factory if no factory was set
     * yet. The specified class should have a parameterless constructor and implement {@link Factory}.
     */
    private static String[] knownImplementations = {
            "com.jmex.physics.impl.physx.PhysXSpace$PhysXFactory",
            "com.jmex.physics.impl.ode.OdePhysicsSpace$OdeFactory",
            "com.jmex.physics.impl.joode.JoodePhysicsSpace$JoodeFactory",
            "com.jmex.physics.impl.jbullet.JBulletPhysicsSpace$JBulletFactory"
    };

    private static Map<String, Factory> availableImplementations;

    /**
     * @return a set of available implementation names
     * @see #chooseImplementation(String)
     */
    public static Set<String> getAvailableImplementations() {
        scanAvailableImplementations();
        return Collections.unmodifiableSet( availableImplementations.keySet() );
    }

    /**
     * Choose an implementation by name to be used in the next call to {@link #create()}.
     *
     * @param implementationName implemntation name
     * @see #getAvailableImplementations()
     */
    public static void chooseImplementation( String implementationName ) {
        if ( implementationName == null ) {
            throw new IllegalArgumentException( "implementation name must not be null" );
        }
        scanAvailableImplementations();
        final Factory factory = availableImplementations.get( implementationName );
        if ( factory == null ) {
            throw new IllegalArgumentException( "Implementation '" + implementationName + "' not available!" );
        }
        setFactory( factory );
    }

    /**
     * Create an implementation specific instance of PhysicsSpace.
     *
     * @return new instance of this class
     */
    @SuppressWarnings({"unchecked"})
    public static PhysicsSpace create() {
        Factory factory = getFactory();
        if ( factory == null ) {
            factory = scanAvailableImplementations();
            setFactory( factory );
        }

        if ( factory != null ) {
            LOGGER.info( "Creating PhysicsSpace using physics implementation '" + factory.getImplementationName() + "'." );
            return factory.create();
        } else {
            throw new IllegalStateException( "No physics implementation was registered nor found!" );
        }
    }

    private static Factory scanAvailableImplementations() {
        Factory factory = null;

        if ( availableImplementations == null ) {
            availableImplementations = new TreeMap<String, Factory>();
            // no factory set yet - search for known implementation
            for ( String classname : knownImplementations ) {
                try {
                    Class<?> cls = Class.forName( classname );
                    if ( Factory.class.isAssignableFrom( cls ) ) {
                        //noinspection unchecked
                        Class<? extends Factory> factoryCls = (Class<? extends Factory>) cls;
                        Constructor<? extends Factory> constructor = factoryCls.getConstructor();
                        constructor.setAccessible( true );
                        Factory createdFactory = constructor.newInstance();
                        final String name = createdFactory.getImplementationName();
                        if ( name == null || name.length() == 0 ) {
                            throw new NullPointerException( "Implementation has no name" );
                        }
                        LOGGER.info( "Found physics implementation '" +
                                name + "' "
                                + createdFactory.getImplementationVersion() + "." );
                        availableImplementations.put( name, createdFactory );
                        if ( factory == null ) {
                            factory = createdFactory;
                        }
                    } else {
                        LOGGER.log( Level.WARNING, "Failed to use physics implementation '"
                                + classname + "' specified class is no Factory!" );
                    }
                } catch ( ClassNotFoundException e ) {
                    LOGGER.info( "Physics implementation '" + classname + "' not present." );
                    // ok implementation not present - continue searching
                } catch ( IllegalAccessException e ) {
                    LOGGER.log( Level.WARNING, "Failed to use physics implementation '"
                            + classname + "' due to IllegalAccessException: " + e.getMessage() );
                    e.printStackTrace();
                } catch ( InstantiationException e ) {
                    LOGGER.log( Level.WARNING, "Failed to use physics implementation '"
                            + classname + "' due to InstantiationException: " + e.getMessage(), e );
                } catch ( NoSuchMethodException e ) {
                    LOGGER.log( Level.WARNING, "Failed to use physics implementation '"
                            + classname + "': No parameterless contructor was found." );
                } catch ( InvocationTargetException e ) {
                    Throwable cause = e.getCause();
                    LOGGER.log( Level.WARNING, "Failed to use physics implementation '"
                            + classname + "' due to Exception while creating factory: " + cause, cause );
                } catch ( Throwable e ) {
                    LOGGER.log( Level.WARNING, "Failed to use physics implementation '"
                            + classname + "' due to Exception/Error: " + e, e );
                }
            }
        }
        return factory;
    }

    /**
     * @return a new dynamic physics node
     */
    public abstract DynamicPhysicsNode createDynamicNode();

    /**
     * @return a new static physics node
     */
    public abstract StaticPhysicsNode createStaticNode();

    /**
     * @return a new Joint
     */
    public abstract Joint createJoint();

    /**
     * Specify the overall directional gravity in this space.
     *
     * @param gravity new gravity in this space
     */
    public abstract void setDirectionalGravity( Vector3f gravity );

    /**
     * Query the gravity in this space. The
     * passed Vector3f will be populated with the values, and then returned.
     *
     * @param store where to store the gravity (null to create a new vector)
     * @return store
     */
    public abstract Vector3f getDirectionalGravity( Vector3f store );


    /**
     * Create a physics sphere. This is a convenience method for physics implementations which don't need different
     * geometry creation for different PhysicsNodes - is overridden in this case.
     *
     * @param name name of the Spatial
     * @param node physics node which becomes parent of the geometry, null allowed if implementation supports geoms without a parent
     * @return a new physics sphere
     * @see PhysicsCollisionGeometry
     * @see com.jmex.physics.geometry.PhysicsSphere
     */
    protected PhysicsSphere createSphere( String name, PhysicsNode node ) {
        throw errorNoGeometry();
    }

    private UnsupportedOperationException errorNoGeometry() {
        return new UnsupportedOperationException( "Neither PhysicsSpace nor PhysicsNode implementation does handle this geometry!" );
    }

    /**
     * Create a physics box. This is a convenience method for physics implementations which don't need different
     * geometry creation for different PhysicsNodes.
     *
     * @param name name of the Spatial
     * @param node physics node which becomes parent of the geometry, null allowed if implementation supports geoms without a parent
     * @return a new physics box
     * @see PhysicsCollisionGeometry
     * @see com.jmex.physics.geometry.PhysicsBox
     */
    protected PhysicsBox createBox( String name, PhysicsNode node ) {
        throw errorNoGeometry();
    }

    /**
     * Create a physics cylinder. This is a convenience method for physics implementations which don't need different
     * geometry creation for different PhysicsNodes.
     *
     * @param name name of the Spatial
     * @param node physics node which becomes parent of the geometry, null allowed if implementation supports geoms without a parent
     * @return a new physics cylinder
     * @see PhysicsCollisionGeometry
     * @see com.jmex.physics.geometry.PhysicsCylinder
     */
    protected PhysicsCylinder createCylinder( String name, PhysicsNode node ) {
        throw errorNoGeometry();
    }

    /**
     * Create a physics capsule. This is a convenience method for physics implementations which don't need different
     * geometry creation for different PhysicsNodes.
     *
     * @param name name of the Spatial
     * @param node physics node which becomes parent of the geometry, null allowed if implementation supports geoms without a parent
     * @return a new physics capsule
     * @see PhysicsCollisionGeometry
     * @see com.jmex.physics.geometry.PhysicsCapsule
     */
    protected PhysicsCapsule createCapsule( String name, PhysicsNode node ) {
        throw errorNoGeometry();
    }

    /**
     * Create a physics mesh. This is a convenience method for physics implementations which don't need different
     * geometry creation for different PhysicsNodes.
     *
     * @param name name of the Spatial
     * @param node physics node which becomes parent of the geometry, null allowed if implementation supports geoms without a parent
     * @return a new physics mesh
     * @see PhysicsCollisionGeometry
     * @see com.jmex.physics.geometry.PhysicsMesh
     */
    protected PhysicsMesh createMesh( String name, PhysicsNode node ) {
        throw errorNoGeometry();
    }

    /**
     * Create a physics ray. This is a convenience method for physics implementations which don't need different
     * geometry creation for different PhysicsNodes.
     *
     * @param name name of the Spatial
     * @param node physics node which becomes parent of the geometry, null allowed if implementation supports geoms without a parent
     * @return a new physics mesh
     * @see PhysicsCollisionGeometry
     * @see com.jmex.physics.geometry.PhysicsRay
     */
    protected PhysicsRay createRay( String name, PhysicsNode node ) {
        throw errorNoGeometry();
    }

    /**
     * list of PhysicsUpdateCallbacks.
     */
    private ArrayList<PhysicsUpdateCallback> updateCallbacks;

    /**
     * @see PhysicsUpdateCallback
     */
    public boolean addToUpdateCallbacks( PhysicsUpdateCallback value ) {
        boolean changed = false;
        if ( value != null ) {
            if ( this.updateCallbacks == null ) {
                this.updateCallbacks = new ArrayList<PhysicsUpdateCallback>();
            } else if ( this.updateCallbacks.contains( value ) ) {
                return false;
            }
            changed = this.updateCallbacks.add( value );
        }
        return changed;
    }

    /**
     * Get an element from the updateCallbacks association.
     *
     * @param index index of element to be retrieved
     * @return the element, null if index out of range
     * @see PhysicsUpdateCallback
     */
    protected PhysicsUpdateCallback getFromUpdateCallbacks( int index ) {
        if ( updateCallbacks != null && index >= 0 && index < updateCallbacks.size() ) {
            return updateCallbacks.get( index );
        } else {
            return null;
        }
    }

    /**
     * @see PhysicsUpdateCallback
     */
    public void removeAllFromUpdateCallbacks() {
        for ( int i = this.sizeOfUpdateCallbacks() - 1; i >= 0; i-- ) {
            this.removeFromUpdateCallbacks( i );
        }
    }

    /**
     * @see PhysicsUpdateCallback
     */
    public boolean removeFromUpdateCallbacks( PhysicsUpdateCallback value ) {
        boolean changed = false;
        if ( ( this.updateCallbacks != null ) && ( value != null ) ) {
            int index = this.updateCallbacks.indexOf( value );
            if ( index >= 0 ) {
                removeFromUpdateCallbacks( index );
            }
        }
        return changed;
    }

    private void removeFromUpdateCallbacks( int index ) {
        if ( this.updateCallbacks != null ) {
            this.updateCallbacks.remove( index );
        }
    }

    /**
     * @see PhysicsUpdateCallback
     */
    protected int sizeOfUpdateCallbacks() {
        return ( ( this.updateCallbacks == null )
                ? 0
                : this.updateCallbacks.size() );
    }

    private SyntheticButton generalCollisionEventHandler;

    /**
     * All phyics collisions are reported to this synthetic button to allow the application to react on collisions.
     * The trigger receives the ContactInfo as data element. Thus an action can cast getTriggerData() to ContactInfo
     * to obtain additional information on the collision.
     * <p/>
     * Note: if this event handler is obtained it <i>must</i> be used with an InputHandler which is updated regularly
     *
     * @return a synthetic button that is triggered on each collision event
     * @see PhysicsNode#getCollisionEventHandler()
     * @see PhysicsCollisionGeometry#getCollisionEventHandler()
     */
    public SyntheticButton getCollisionEventHandler() {
        if ( generalCollisionEventHandler == null ) {
            generalCollisionEventHandler = new SyntheticButton( "collision" );
        }
        return generalCollisionEventHandler;
    }

    /**
     * Called by subclasses upon each collision event that could be processed by the application.
     *
     * @param info info about the collision
     */
    protected void collisionEvent( ContactInfo info ) {
        if ( generalCollisionEventHandler != null ) {
            generalCollisionEventHandler.trigger( 0, '\0', 0, true, info );
        }
        PhysicsNode node1 = info.getNode1();
        if ( node1 != null && node1.collisionEventHandler != null ) {
            node1.collisionEventHandler.trigger( 0, '\0', 0, true, info );
        }
        PhysicsNode node2 = info.getNode2();
        if ( node2 != node1 &&
                node2 != null && node2.collisionEventHandler != null ) {
            node2.collisionEventHandler.trigger( 0, '\0', 0, true, info );
        }
        PhysicsCollisionGeometry geometry1 = info.getGeometry1();
        if ( geometry1 != null && geometry1.collisionEventHandler != null ) {
            geometry1.collisionEventHandler.trigger( 0, '\0', 0, true, info );
        }
        PhysicsCollisionGeometry geometry2 = info.getGeometry2();
        if ( geometry2 != geometry1 &&
                geometry2 != null && geometry2.collisionEventHandler != null ) {
            geometry2.collisionEventHandler.trigger( 0, '\0', 0, true, info );
        }
    }

    public void setupBinaryClassLoader( BinaryImporter importer ) {
        BinaryClassLoader.registerModule( new BinaryDynamicPhysicsNodeModule( this ) );
        BinaryClassLoader.registerModule( new BinaryStaticPhysicsNodeModule( this ) );
        BinaryClassLoader.registerModule( new BinaryJointModule( this ) );
        BinaryClassLoader.registerModule( new BinaryRotationalJointAxisModule() );
        BinaryClassLoader.registerModule( new BinaryTranslationalJointAxisModule() );
        BinaryClassLoader.registerModule( new BinaryPhysicsSphereModule() );
        BinaryClassLoader.registerModule( new BinaryPhysicsBoxModule() );
        BinaryClassLoader.registerModule( new BinaryPhysicsCylinderModule() );
        BinaryClassLoader.registerModule( new BinaryPhysicsCapsuleModule() );
        BinaryClassLoader.registerModule( new BinaryPhysicsMeshModule() );
    }

    /**
     * Change the accuracy of the underlying physics simulation. This value is implementation dependant! It should
     * match a step size (or similar) for iterative methods.
     *
     * @param value value > 0 to indicate accuracy, smaller values mean more accuracy
     */
    public abstract void setAccuracy( float value );

    private Vector3f worldMinBounds = new Vector3f(Float.MIN_VALUE,Float.MIN_VALUE,Float.MIN_VALUE);
    private Vector3f worldMaxBounds = new Vector3f(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
    
    /**
     * Change the declared boundaries of the physics world.  This value is used for optimization only and
     * will not actually limit the placement of objects.  However, tuning world boundaries MAY for some
     * implementations result in a performance improvement.  Default values at start are Float.MIN_VALUE and
     * Float.MAX_VALUE respectively for all axes.
     * 
     * @param min The minimum boundary of the physics space.
     * @param max The maximum boundary of the physics space.
     */
    public void setWorldBounds(Vector3f min, Vector3f max)
    {
    	if(min!=null)
    		worldMinBounds.set(min);
    	if(max!=null)
    		worldMaxBounds.set(max);
    }
    
    public Vector3f getWorldMinBound(Vector3f store)
    {
    	if(store==null)
    		store = new Vector3f();
    	store.set(worldMinBounds);
    	return store;
    }
    
    public Vector3f getWorldMaxBound(Vector3f store)
    {
    	if(store==null)
    		store = new Vector3f();
    	store.set(worldMaxBounds);
    	return store;
    }
    
    private int maxObjects = 10000; //This default can change, if need be.  Seems like a decent start tho.
    
    public int getMaxObjects() {
		return maxObjects;
	}

    /**
     * sets the maximum number of physics objects (i.e. physics nodes) in the world.  This number MAY
     * be a hard limit depending on the implementation, but can be invaluable for tuning and performance
     * improvement purposes.
     * 
     * @return
     */
	public void setMaxObjects(int maxObjects) {
		this.maxObjects = maxObjects;
	}

	private List<CollisionGroup> collisionGroups = new ArrayList<CollisionGroup>();
    private List<CollisionGroup> collisionGroupsReadOnly = Collections.unmodifiableList( collisionGroups );

    /**
     * add group
     * @param value group to add
     * @return index of group in list
     */
    protected int addCollisionGroup( CollisionGroup value ) {
        this.collisionGroups.add( value );
        return this.collisionGroups.size() - 1;
    }

    protected void removeCollisionGroup( CollisionGroup value ) {
        this.collisionGroups.remove( value );
    }

    /**
     * @return the list of {@link CollisionGroup}s used by this PhysicsSpace
     * @see CollisionGroup
     */
    public List<? extends CollisionGroup> getCollisionGroups()
    {
        return collisionGroupsReadOnly;
    }

    /**
     * Create a {@link CollisionGroup}.
     * @param name name of this group (descriptive only)
     * @return new collision group
     * @see CollisionGroup
     */
    public abstract CollisionGroup createCollisionGroup( String name );

    /**
     * @return the default collision group for newly created nodes
     * @see #getStaticCollisionGroup()
     */
    public abstract CollisionGroup getDefaultCollisionGroup();

    /**
     * Note: this method might return the same group like {@link #getDefaultCollisionGroup()}.
     * @return the default collision group for newly created static nodes
     * @see #getDefaultCollisionGroup()
     */
    public abstract CollisionGroup getStaticCollisionGroup();
}

/*
* $log$
*/
