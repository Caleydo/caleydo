package com.jmex.physics.impl.jbullet.callback;

import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.impl.jbullet.JBulletPhysicsNode;

class ContactSet
{
	PhysicsCollisionGeometry geom;
	JBulletPhysicsNode node;
	
	public ContactSet(PhysicsCollisionGeometry geom, JBulletPhysicsNode node)
	{
		this.geom = geom;
		this.node = node;
	}

	@Override
	public boolean equals(Object obj) {
		ContactSet comp = (ContactSet)obj;
		if(obj == null || !(obj instanceof ContactSet))
			return false;
		
		if(comp==this)
			return true;
		
		if(geom==comp.geom && node==comp.node)
			return true;
		
		if(geom==null && comp.geom!=null)
			return false;
		
		if(geom!=null && !geom.equals(comp.geom))
			return false;
		
		if(node==null && comp.node!=null)
			return false;
		
		if(node!=null && !node.equals(comp.node))
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		int ret = (geom==null)?0:geom.hashCode();
		ret += (node==null)?0:node.hashCode()*13;
		return ret;
	}

	@Override
	public String toString() {
		return "ContactSet[geom:"+((geom==null)?"null":geom.toString())+", node:"+((node==null)?"null":node.toString())+"]";
	}
}