package com.jmex.physics.impl.jbullet.callback;

import java.util.HashMap;
import java.util.Map;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.impl.jbullet.JBulletPhysicsNode;
import com.jmex.physics.impl.jbullet.JBulletRigidBody;
import com.jmex.physics.impl.jbullet.geometry.proxies.ShapeProxy;

public class JBulletPendingContactCache {

	private Map<ContactSet,Map<ContactSet,JBulletPendingContact>> cache = new HashMap<ContactSet,Map<ContactSet,JBulletPendingContact>>();
	
	public JBulletPendingContact acquirePendingContact(ManifoldPoint mp, JBulletRigidBody obj1, JBulletRigidBody obj2)
	{
		ShapeProxy sp1=(ShapeProxy)obj1.getLastTempProxyCollisionShape();
		ShapeProxy sp2=(ShapeProxy)obj2.getLastTempProxyCollisionShape();
		ContactSet cs1 = new ContactSet((sp1==null)?null:sp1.getJmeShape(),obj1.getParentNode());
		ContactSet cs2 = new ContactSet((sp2==null)?null:sp2.getJmeShape(),obj2.getParentNode());
		Map<ContactSet,JBulletPendingContact> cache2;
		JBulletPendingContact ret = new JBulletPendingContact(mp,cs1,cs2);
		ret.attachManifoldPoint(mp);
		JBulletPendingContact predef;
		
		cache2=cache.get(cs1);
		if(cache2==null)
		{
			cache2=new HashMap<ContactSet,JBulletPendingContact>();
			cache.put(cs1, new HashMap<ContactSet,JBulletPendingContact>());
		}
		predef = cache2.get(cs2);
		if(predef!=null)
		{
			ret.copy(predef);
		}
		return ret;
	}
	
	public void definePendingContact(PendingContact pc)
	{
		if(pc instanceof JBulletPendingContact)
			return;
		
		Map<ContactSet,JBulletPendingContact> cache2;

		JBulletPendingContact store = new JBulletPendingContact((JBulletPhysicsNode)pc.getNode1(), (JBulletPhysicsNode)pc.getNode2(), pc.getGeometry1(), pc.getGeometry2());
		store.copy(pc);
		
		cache2=cache.get(store.cs1);
		if(cache2==null)
		{
			cache2=new HashMap<ContactSet,JBulletPendingContact>();
			cache.put(store.cs1, new HashMap<ContactSet,JBulletPendingContact>());
		}
		cache2.put(store.cs2, store);
	}
	
	public void releasePendingContact(JBulletPendingContact pc)
	{
		return;
	}
}
