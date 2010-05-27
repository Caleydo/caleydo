package com.jmex.physics.impl.jbullet.callback;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.impl.jbullet.JBulletPhysicsNode;
import com.jmex.physics.impl.jbullet.geometry.JBulletRay;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;

public class JBulletPendingContact extends PendingContact {

	ContactSet cs1;
	ContactSet cs2;
	ManifoldPoint mp;

	private boolean preDefined=false;
	
	JBulletPendingContact(ManifoldPoint point, ContactSet cs1, ContactSet cs2)
	{
		this.cs1 = cs1;
		this.cs2 = cs2;

		this.mp = point;
	}
	
	JBulletPendingContact(JBulletPhysicsNode node1, JBulletPhysicsNode node2, PhysicsCollisionGeometry geom1, PhysicsCollisionGeometry geom2)
	{
		this.cs1 = new ContactSet(geom1,node1);
		this.cs2 = new ContactSet(geom2,node2);
		
		this.preDefined = true;
	}
	
	public JBulletPendingContact(JBulletPhysicsNode node1, JBulletPhysicsNode node2, JBulletRay geom1, PhysicsCollisionGeometry geom2)
	{
		this.cs1 = new ContactSet(geom1,node1);
		this.cs2 = new ContactSet(geom2,node2);
		
		this.preDefined = true;
	}
	
	boolean isPreDefined()
	{
		return preDefined;
	}
	
	public void attachManifoldPoint(ManifoldPoint mp)
	{
		if(this.mp==mp)
			return;
		if(this.mp!=null && this.mp.userPersistentData!=mp.userPersistentData)
			throw new IllegalStateException("Attempting to define a new PendingContact for a contact that's alredy in use.");
		this.mp = mp;
	}
	
	public void detachManifoldPoint()
	{
		this.mp = null;
	}
	
	public Vector3f getContactNormal(Vector3f store) {
		if(store==null)
			store = new Vector3f();
		VecmathConverter.convert(mp.normalWorldOnB, store);
		return store;
	}

	public Vector3f getContactPosition(Vector3f store) {
		if(store==null)
			store = new Vector3f();
		VecmathConverter.convert(mp.positionWorldOnA,store);
		return store;
	}

	public void getDefaultFrictionDirections(Vector3f primaryStore,Vector3f secondaryStore) {
		VecmathConverter.convert(mp.lateralFrictionDir1,primaryStore);
		VecmathConverter.convert(mp.lateralFrictionDir2,secondaryStore);
	}

	public PhysicsCollisionGeometry getGeometry1() {
		return cs1.geom;
	}

	public PhysicsCollisionGeometry getGeometry2() {
		return cs2.geom;
	}

	public PhysicsNode getNode1() {
		return (PhysicsNode)cs1.node;
	}

	public PhysicsNode getNode2() {
		return (PhysicsNode)cs2.node;
	}

	public float getPenetrationDepth() {
		if(mp==null)
			return 0;
		return mp.distance1;
	}

	public float getTime() {
		if(mp==null)
			return 0;
		return mp.lifeTime; // not sure what this is, or what it's supposed to be, but that's all there is.
 	}

	public void applyContactAdjustments()
	{
		if(mp==null)
			return;
		
		if(isIgnored())
		{
			//mp.distance1=BulletGlobals.getContactBreakingThreshold()-1;
			mp.appliedImpulse=0;
			mp.appliedImpulseLateral1=0;
			mp.appliedImpulseLateral2=0;
		}
		
		if(!isApplied())
			return;
		
		Vector3f fDir = new Vector3f();
		Vector3f fDir2 = new Vector3f();
		Vector2f slip = new Vector2f();
		Vector2f surfaceMotion = new Vector2f();
		
		if(!Float.isNaN(getMinimumBounceVelocity()))
		{
			
			Vector3f velocity = new Vector3f();
			computeContactVelocity(this, velocity);
			if(velocity.length()<getMinimumBounceVelocity())
			{
				mp.combinedRestitution=0;
			}
			else if(!Float.isNaN(getBounce()))
			{
				mp.combinedRestitution=getBounce();
			}
		}		
		if(!Float.isNaN(getDampingCoefficient())){}
		if(!Float.isNaN(getMu()))
		{
			mp.combinedFriction=getMu();
		}
		if(!Float.isNaN(getMuOrthogonal())){}
		if(!Float.isNaN(getSpringConstant())){}
		if(!Float.isNaN(getFrictionDirection(fDir).x) && 
		   !Float.isNaN(fDir.y) &&
		   !Float.isNaN(fDir.z)){}
		if(!Float.isNaN(getSecondaryFrictionDirection(fDir2).x) && 
		   !Float.isNaN(fDir2.y) &&
		   !Float.isNaN(fDir2.z)){}
		if(!Float.isNaN(getSlip(slip).x) && 
		   !Float.isNaN(slip.y)){}
		if(!Float.isNaN(getSurfaceMotion(surfaceMotion).x) && 
		   !Float.isNaN(surfaceMotion.y)){}
	}
}
