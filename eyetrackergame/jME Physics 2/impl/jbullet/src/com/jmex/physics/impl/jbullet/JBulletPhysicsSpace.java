package com.jmex.physics.impl.jbullet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.ContactDestroyedCallback;
import com.bulletphysics.ContactProcessedCallback;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.AxisSweep3_32;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.broadphase.SimpleBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.dispatch.CollisionWorld.RayResultCallback;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.geometry.PhysicsCapsule;
import com.jmex.physics.geometry.PhysicsCylinder;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.geometry.PhysicsRay;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.impl.jbullet.callback.JBulletPendingContact;
import com.jmex.physics.impl.jbullet.callback.JBulletPendingContactCache;
import com.jmex.physics.impl.jbullet.geometry.JBulletBox;
import com.jmex.physics.impl.jbullet.geometry.JBulletCapsule;
import com.jmex.physics.impl.jbullet.geometry.JBulletCylinder;
import com.jmex.physics.impl.jbullet.geometry.JBulletMesh;
import com.jmex.physics.impl.jbullet.geometry.JBulletRay;
import com.jmex.physics.impl.jbullet.geometry.JBulletSphere;
import com.jmex.physics.impl.jbullet.geometry.proxies.ShapeProxy;
import com.jmex.physics.impl.jbullet.joints.JBulletJoint;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;

public class JBulletPhysicsSpace extends PhysicsSpace {

    private List<PhysicsNode> physicsNodes = new ArrayList<PhysicsNode>();
    private List<Joint> joints = new ArrayList<Joint>();

    public DynamicsWorld dynamicsWorld = null;
    private BroadphaseInterface overlappingPairCache;
    private boolean broadphaseChanged=false;
    private CollisionDispatcher dispatcher;
    private ConstraintSolver solver;
    private DefaultCollisionConfiguration collisionConfiguration;
    
    private boolean forcingCollision;
    private boolean forcedCollisionSucceeded;
    private boolean isRayTest;
    
    private boolean updatedThisTick;
    
    private PhysicsCollisionGeometry forcedCollisionShape1;
    private PhysicsCollisionGeometry forcedCollisionShape2;

    JBulletCollisionGroup staticCollisionGroup;
    JBulletCollisionGroup defaultCollisionGroup;

    JBulletPendingContactCache contactInfoCache = new JBulletPendingContactCache();
    
    com.jme.math.Vector3f gravity = new com.jme.math.Vector3f();
    
    private float autoRestThreshold=0.02f;
    private float accuracy=1f/60f;
    
	public JBulletPhysicsSpace() {
        //TODO swap out a collision configuration that will proxy the CollisionAlgorithms in the end, to be able to capture collision events.
        collisionConfiguration = new DefaultCollisionConfiguration();

        //Gotta create the collisionGroups required for the JMEPhys engine.
        staticCollisionGroup = createCollisionGroup( "static" );
        defaultCollisionGroup = createCollisionGroup( "default" );
        staticCollisionGroup.collidesWith(defaultCollisionGroup, true);

        //Not used for multi-threading, but as JMEPhys doesn't multi-thread yet ...
        //TODO: Config option for multithreading.
        dispatcher = new CollisionDispatcher( collisionConfiguration );

        //Needs to use AxisSweep3, but the class is not in the JBullet
        //source
        //TODO: Convert to a more efficient Broadphase sweeping algorithm
        overlappingPairCache = new SimpleBroadphase();

        //Default solver.  Other multi-threaded options are available.
        //TODO: Config option for multithreading.
        solver = new SequentialImpulseConstraintSolver();

        //Initialization of the core JBullet physics engine.
        dynamicsWorld = new DiscreteDynamicsWorld( dispatcher, overlappingPairCache, solver, collisionConfiguration );

        dynamicsWorld.setGravity( new javax.vecmath.Vector3f( 0, -9.81f, 0 ) );
//		lwjgl = new LwjglGL();
//		lwjgl.init();
//		dynamicsWorld.setDebugDrawer(new GLDebugDrawer(lwjgl));
//		dynamicsWorld.getDebugDrawer().setDebugMode(DebugDrawModes.DRAW_AABB | DebugDrawModes.DRAW_WIREFRAME | DebugDrawModes.DRAW_FEATURES_TEXT | DebugDrawModes.DRAW_CONTACT_POINTS | DebugDrawModes.DRAW_TEXT);
        gravity = new com.jme.math.Vector3f( 0, -9.81f, 0 );
        BulletGlobals.setContactAddedCallback(new ContactAddedCallback()
        {
        	public boolean contactAdded(ManifoldPoint cp, CollisionObject colObj0,
        			int partId0, int index0, CollisionObject colObj1, int partId1,
        			int index1) 
        	{
        		JBulletPendingContact pc = contactInfoCache.acquirePendingContact(cp, (JBulletRigidBody)colObj0, (JBulletRigidBody)colObj1); 
        		if(forcingCollision)
        		{
        			if(isRayTest)
        				return true;
        			if(forcedCollisionShape1!=null && forcedCollisionShape1!=pc.getGeometry1() && forcedCollisionShape1!=pc.getGeometry2())
        				return false;
        			if(forcedCollisionShape2!=null && forcedCollisionShape2!=pc.getGeometry1() && forcedCollisionShape2!=pc.getGeometry2())
        				return false;
        			collisionEvent(pc);
        			forcedCollisionSucceeded=true;
        			return true;
        		}
        		cp.userPersistentData = pc;
        		adjustContact(pc);
        		pc.applyContactAdjustments();
        		return true;
        	}
        });

        BulletGlobals.setContactProcessedCallback(new ContactProcessedCallback(){	
        	public boolean contactProcessed(ManifoldPoint cp, Object body0, Object body1) 
        	{
        		if(cp.userPersistentData==null)
        			return true;
        		collisionEvent(((JBulletPendingContact)cp.userPersistentData));
        		return true;
        	}
    	});

        BulletGlobals.setContactDestroyedCallback(new ContactDestroyedCallback(){	
 			public boolean contactDestroyed(Object userPersistentData) {
 				contactInfoCache.releasePendingContact((JBulletPendingContact)userPersistentData);
 				return true;
			}
    	});
    }

    static class JBulletFactory implements Factory {
        public JBulletFactory() {
            setFactory( this );
        }

        public PhysicsSpace create() {
            JBulletPhysicsSpace space = new JBulletPhysicsSpace();
            return space;
        }

        public String getImplementationName() {
            return "JBullet";
        }

        public String getImplementationVersion() {
            return "2.70b1";
        }
    }

    @Override
    public void drawImplementationSpecificPhysics( Renderer renderer ) {
//    	dynamicsWorld.debugDrawWorld();
//    	for(PhysicsNode node : physicsNodes)
//    	{
//    		if(node instanceof JBulletPhysicsNode)
//    			drawCollisionObject(((JBulletPhysicsNode)node).getBody(),(node instanceof JBulletStaticPhysicsNode));
//    	}
    }

//    private void drawCollisionObject(CollisionObject colObj, boolean isStatic)
//    {
//    	if(colObj==null)
//    		return;
//    	Transform m = new Transform();
//    	javax.vecmath.Vector3f wireColor = new javax.vecmath.Vector3f();
//		m.set(colObj.getWorldTransform());
//		wireColor.set(1f, 1f, 0.5f); // wants deactivation
//
//		if (colObj.getActivationState() == 1) // active
//		{
//				wireColor.x -= (isStatic) ? 0.5f : 0.25f;
//		}
//		if (colObj.getActivationState() == 2) // ISLAND_SLEEPING
//		{
//				wireColor.y -= (isStatic) ? 0.5f : 0.25f;
//		}
//
//		GLShapeDrawer.drawOpenGL(lwjgl, m, colObj.getCollisionShape(), wireColor, dynamicsWorld.getDebugDrawer().getDebugMode());
//    }

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

    @Override
    public void addJoint( Joint joint ) {
        super.addJoint( joint );

        // log some stuff:
        Logger logger = Logger.getLogger( PhysicsSpace.LOGGER_NAME );
        if ( logger.isLoggable( Level.INFO ) ) {
            logger.log( Level.INFO,
                    "Joint (" + joint.getName() + ") has been added" );
        }

        // add it to the arraylist
        joints.add( joint );
    }

    protected void removeJoint( Joint joint ) {

        if ( !joints.remove( joint ) ) {
            return;
        }
        super.removeJoint( joint );

        // print out a statement
        Logger logger = Logger.getLogger( PhysicsSpace.LOGGER_NAME );
        if ( logger.isLoggable( Level.INFO ) ) {
            logger.log( Level.INFO,
                    "Joint ("
                            + joint.getName()
                            + ") has been removed from PhysicsWorld and will no longer take place in the simulation" );
        }
    }

    public JBulletCollisionGroup createCollisionGroup( String name ) {
        return new JBulletCollisionGroup( this, name );
    }

    public CollisionGroup getDefaultCollisionGroup() {
        return defaultCollisionGroup;
    }

    public CollisionGroup getStaticCollisionGroup() {
        return staticCollisionGroup;
    }

    @Override
    public DynamicPhysicsNode createDynamicNode() {
        JBulletDynamicPhysicsNode node = new JBulletDynamicPhysicsNode( this );
        node.setCollisionGroup( defaultCollisionGroup );
        node.setActive( true );
        return node;
    }

    @Override
    public Joint createJoint() {
        Joint ret = new JBulletJoint( this );
        addJoint( ret );
        return ret;
    }

    @Override
    public StaticPhysicsNode createStaticNode() {
        JBulletStaticPhysicsNode node = new JBulletStaticPhysicsNode( this );
        node.setCollisionGroup( staticCollisionGroup );
        node.setActive( true );
        return node;
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub

    }

    @Override
    public Vector3f getDirectionalGravity( Vector3f store ) {
        return store.set( gravity );
    }

    @Override
    public List<? extends Joint> getJoints() {
        return Collections.unmodifiableList( joints );
    }

    @Override
    public List<? extends PhysicsNode> getNodes() {
        return Collections.unmodifiableList( physicsNodes );
    }

    @Override
    public boolean collide( PhysicsSpatial spatial1, PhysicsSpatial spatial2 ) {
    	if(spatial1==null && spatial2==null)
    		throw new IllegalArgumentException("Really?  Can't wait for the physics space to generate collisions?");
    	
    	JBulletPhysicsNode node1=null;
    	JBulletPhysicsNode node2=null;
    	PhysicsCollisionGeometry geo1=null;
    	PhysicsCollisionGeometry geo2=null;
    	JBulletRigidBody b1=null;
    	JBulletRigidBody b2=null;
    	DispatcherInfo dInfo = new DispatcherInfo();
    	
    	forcingCollision=true;
    	
    	try
    	{
	    	if(!updatedThisTick)
	    	{
		    	dynamicsWorld.updateAabbs();
		    	dynamicsWorld.getBroadphase().calculateOverlappingPairs(dispatcher);
	    	}
	    	
	    	if(spatial1 instanceof JBulletPhysicsNode)
	    	{
	    		node1=(JBulletPhysicsNode)spatial1;
	    		b1=node1.getBody();	    		
	    	} else if(spatial1!=null && spatial1 instanceof JBulletRay)
	    	{
	    		node1=(JBulletPhysicsNode)((JBulletRay)spatial1).getPhysicsNode();
	    		isRayTest=true;
	    	} else if (spatial1 instanceof PhysicsCollisionGeometry)
	    	{
	    		geo1=(PhysicsCollisionGeometry)spatial1;
	    		node1=(geo1!=null)?(JBulletPhysicsNode)geo1.getPhysicsNode():null;
	    		b1=node1.getBody();
	    	}
	    	
	    	if(spatial2 instanceof JBulletPhysicsNode)
	    	{
	    		node2=(JBulletPhysicsNode)spatial2;
	    		b2=node2.getBody();
	    	} else if (spatial2 instanceof PhysicsCollisionGeometry)
	    	{
	    		geo2=(PhysicsCollisionGeometry)spatial2;
	    		node2=(geo2!=null)?(JBulletPhysicsNode)geo2.getPhysicsNode():null;
	    	}
	    	forcedCollisionShape1=geo1;
	    	forcedCollisionShape2=geo2;
	    	if(!isRayTest)
	    	{
	    		if(b1==null && b2==null)
	    			return false;
	    		if(b1!=null && !dynamicsWorld.getCollisionObjectArray().contains(b1))
	    			return false;
	    		if(b2!=null && !dynamicsWorld.getCollisionObjectArray().contains(b2))
	    			return false;
	    		for(BroadphasePair pair : dynamicsWorld.getBroadphase().getOverlappingPairCache().getOverlappingPairArray())
	    		{
	    			if(b1!=null && b1.getBroadphaseProxy() != pair.pProxy0 && b1.getBroadphaseProxy() != pair.pProxy1) continue;
	    			if(b2!=null && b2.getBroadphaseProxy() != pair.pProxy0 && b2.getBroadphaseProxy() != pair.pProxy1) continue;
	    			dispatcher.getNearCallback().handleCollision(pair, dispatcher, dInfo);
	    		}
	    		return forcedCollisionSucceeded;
	    	} else {
	    		javax.vecmath.Vector3f rayOrigin = VecmathConverter.convert(((JBulletRay)spatial1).getLocalTranslation());
	    		javax.vecmath.Vector3f rayDirection = VecmathConverter.convert(((JBulletRay)spatial1).getLocalScale());
	    		RayResultCallback cb = new ClosestRayResultCallback(rayOrigin,rayOrigin);
	    		dynamicsWorld.rayTest(rayOrigin, rayDirection, cb);
	    		if(cb.hasHit())
	    		{
	    			JBulletRigidBody hitBody=(JBulletRigidBody)cb.collisionObject;
	    			ShapeProxy ps = hitBody.getLastTempProxyCollisionShape();
	    			JBulletPendingContact pc = new JBulletPendingContact(node1,hitBody.getParentNode(),(JBulletRay)spatial1,(ps!=null)?ps.getJmeShape():null);
	    			collisionEvent(pc);
	    			return true;
	    		}
	    		return false;
	    	}
    	}
    	finally
    	{
    		isRayTest=false;
    		forcingCollision=false;
    		forcedCollisionSucceeded=false;
    		forcedCollisionShape1=null;
    		forcedCollisionShape2=null;
    	}
    }

    @Override
    public void pick( PhysicsSpatial spatial ) {
    	forcingCollision=true;
    	if(spatial instanceof JBulletRay)
    	{
    		collide(spatial,null);
    		return;
    	}
    	JBulletPhysicsNode node = null;
    	if(spatial instanceof JBulletPhysicsNode)
    		node=(JBulletPhysicsNode)spatial;
    	if(spatial instanceof PhysicsCollisionGeometry)
    	{
    		node = (JBulletPhysicsNode)((PhysicsCollisionGeometry)spatial).getPhysicsNode();
    	}
    	if(node==null)
    		return;
    	boolean active=((PhysicsNode)node).isActive();
    	if(!active && node.getBody()==null)
    		node.rebuildRigidBody();
    	((PhysicsNode)node).setActive(true);
    	collide(spatial,null);
    	((PhysicsNode)node).setActive(active);
    }

    @Override
    public void setAccuracy( float value ) {
    	accuracy = value;
    }

    @Override
    public void setAutoRestThreshold( float threshold ) {
    	this.autoRestThreshold=threshold;
    }

    float getAutoRestThreshold() {
		return autoRestThreshold;
	}

    @Override
    protected PhysicsBox createBox( String name, PhysicsNode node ) {
        JBulletBox ret = new JBulletBox( node );
        if ( node != null ) {
            node.attachChild( ret );
        }
        return ret;
    }

    @Override
    protected PhysicsMesh createMesh( String name, PhysicsNode node ) {
        JBulletMesh ret = new JBulletMesh( node );
        if ( node != null ) {
            node.attachChild( ret );
        }
        return ret;
    }

    @Override
    protected PhysicsSphere createSphere( String name, PhysicsNode node ) {
        JBulletSphere ret = new JBulletSphere( node );
        if ( node != null ) {
            node.attachChild( ret );
        }
        return ret;
    }
    
    @Override
	protected PhysicsRay createRay(String name, PhysicsNode node) {
    	JBulletRay ret = new JBulletRay( node );
    	if( node != null ) {
    		node.attachChild( ret );
    	}
    	return ret;
	}

	@Override
    protected PhysicsCylinder createCylinder( String name, PhysicsNode node ) {
        JBulletCylinder ret = new JBulletCylinder( node );
        if ( node != null ) {
            node.attachChild( ret );
        }
        return ret;
    }

    @Override
    protected PhysicsCapsule createCapsule( String name, PhysicsNode node ) {
        JBulletCapsule ret = new JBulletCapsule( node );
        if ( node != null ) {
            node.attachChild( ret );
        }
        return ret;
    }

    @Override
    public void setDirectionalGravity( Vector3f gravity ) {
        this.gravity = gravity;
        dynamicsWorld.setGravity( VecmathConverter.convert( gravity ) );
    }

    @Override
	protected void adjustContact(PendingContact contact) {
		this.contactInfoCache.definePendingContact(contact);
		super.adjustContact(contact);
    }

    @Override
    public void update( float time)
    {
    	update( time, 1 );
    }
    
    public void updateUnstableIgnoreAccuracy(float time)
    {
    	update( time , 0 );
    }
    
    public void update( float time , int maxSteps ) {
		if(broadphaseChanged)
		{
			List<CollisionObject> coList = new ArrayList<CollisionObject>();
			for(CollisionObject co : dynamicsWorld.getCollisionObjectArray())
				coList.add(co);
			for(CollisionObject co : coList)
				dynamicsWorld.removeCollisionObject(co);
			dynamicsWorld.setBroadphase(overlappingPairCache);
			for(CollisionObject co : coList)
				dynamicsWorld.addCollisionObject(co);
			for(Joint joint : joints)
				((JBulletJoint)joint).setDirty(true);
			
			broadphaseChanged=false;
		}
		
        for ( PhysicsNode node : physicsNodes ) {
            if ( !( node instanceof JBulletPhysicsNode ) ) {
                continue;
            }
            node.updateWorldVectors();
            if(!node.isStatic())
            {
            	((JBulletDynamicPhysicsNode)node).applyForces(time);
            	((JBulletDynamicPhysicsNode)node).applyVelocities();
            }
            if ( ( (JBulletPhysicsNode) node ).isDirty() ) {
                ( (JBulletPhysicsNode) node ).rebuildRigidBody();
            }
        }

		for(Joint joint : joints)
		{
			if(((JBulletJoint)joint).isDirty() && joint.isActive())
				((JBulletJoint)joint).buildConstraint();
		}

		//this may not be quite right, but I'll have to figure out how to correctly calculate
		// the step size from JBullet.  It'll be close enough for now.
		float actualStep = (time>accuracy)?accuracy:time;
        for ( int i = this.sizeOfUpdateCallbacks() - 1; i >= 0; i-- ) {
            PhysicsUpdateCallback updateCallback = this.getFromUpdateCallbacks( i );
            updateCallback.beforeStep( this, actualStep );
        }
        
		dynamicsWorld.stepSimulation( time, maxSteps, accuracy );
		
        for ( int i = this.sizeOfUpdateCallbacks() - 1; i >= 0; i-- ) {
            PhysicsUpdateCallback updateCallback = this.getFromUpdateCallbacks( i );
            updateCallback.afterStep( this, actualStep );
        }
        
        for ( PhysicsNode node : physicsNodes ) {
            if ( !( node instanceof JBulletDynamicPhysicsNode ) ) {
                continue;
            }
            ( (JBulletDynamicPhysicsNode) node ).applyPhysicsMovement();
        }
        
        updatedThisTick=false;
    }

	@Override
	public void setMaxObjects(int maxObjects) {
		super.setMaxObjects(maxObjects);
		updateBroadphaseInterface();
	}

	@Override
	public void setWorldBounds(Vector3f min, Vector3f max) {
		super.setWorldBounds(min, max);
		updateBroadphaseInterface();
	}
	
	private void updateBroadphaseInterface()
	{
		Vector3f worldMin = getWorldMinBound(null);
		Vector3f worldMax = getWorldMaxBound(null);
		if(worldMin.x==Float.MIN_VALUE &&
		   worldMin.y==Float.MIN_VALUE &&
		   worldMin.z==Float.MIN_VALUE &&
		   worldMax.x==Float.MAX_VALUE &&
		   worldMax.y==Float.MAX_VALUE &&
		   worldMax.z==Float.MAX_VALUE)
		{
			overlappingPairCache=new SimpleBroadphase();
		} else if(getMaxObjects() <= 16384){
			overlappingPairCache=new AxisSweep3(VecmathConverter.convert(worldMin),VecmathConverter.convert(worldMax),getMaxObjects());
		} else {
			overlappingPairCache=new AxisSweep3_32(VecmathConverter.convert(worldMin),VecmathConverter.convert(worldMax),getMaxObjects());
		}
		broadphaseChanged=true;
	}
}
