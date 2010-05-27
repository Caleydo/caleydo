package com.jmex.physics.impl.jbullet;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.impl.jbullet.geometry.proxies.ShapeProxy;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;

public class JBulletRigidBody extends RigidBody {

	private ShapeProxy lastTempProxyCollisionShape=null;
	
	private com.jme.math.Matrix3f tempRotMatrix = new com.jme.math.Matrix3f();
    private JBulletPhysicsNode parentNode=null;

    public JBulletPhysicsNode getParentNode() {
		return parentNode;
	}

	public JBulletRigidBody( RigidBodyConstructionInfo constructionInfo, JBulletPhysicsNode parent ) {
        super( constructionInfo );
        parentNode = parent;
    }

    public JBulletRigidBody( float mass, MotionState motionState, CollisionShape collisionShape, JBulletPhysicsNode parent  ) {
        this( mass, motionState, collisionShape, new Vector3f(), parent );
    }

    public JBulletRigidBody( float mass, MotionState motionState, CollisionShape collisionShape, Vector3f localInertia, JBulletPhysicsNode parent  ) {
        super( mass, motionState, collisionShape, localInertia );
        parentNode = parent;
    }

    public void setWorldTranslation( com.jme.math.Vector3f worldTranslation ) {
        final Transform transform = getWorldTransform(new Transform());
        VecmathConverter.convert( worldTranslation, transform.origin );
        setWorldTransform( transform );
    }

    public void setWorldRotation( com.jme.math.Quaternion worldRotation ) {
        worldRotation.toRotationMatrix( tempRotMatrix );
        final Transform transform = getWorldTransform(new Transform());
        VecmathConverter.convert( tempRotMatrix, transform.basis );
        setWorldTransform( transform );
    }

    public Vector3f getLinearVelocity() {
        return getLinearVelocity( new Vector3f() );
    }

    public Vector3f getAngularVelocity() {
        return getAngularVelocity( new Vector3f() );
    }

	@Override
	public boolean checkCollideWith(CollisionObject co) {
		if(co instanceof JBulletRigidBody)
		{
			CollisionGroup cg1 = ((PhysicsNode)getParentNode()).getCollisionGroup();
			CollisionGroup cg2 = ((PhysicsNode)((JBulletRigidBody)co).getParentNode()).getCollisionGroup();
			if(cg1.getCollidesWith().contains(cg2) || cg2.getCollidesWith().contains(cg1))
				return super.checkCollideWith(co);
			return false;
		}
		return super.checkCollideWith(co);
	}
	
	@Override
	public void internalSetTemporaryCollisionShape(CollisionShape collisionShape) {
		if(collisionShape instanceof ShapeProxy)
			lastTempProxyCollisionShape=(ShapeProxy)collisionShape;
		else if(this.collisionShape==this.rootCollisionShape)
			lastTempProxyCollisionShape=null;
		super.internalSetTemporaryCollisionShape(collisionShape);
	}

    public ShapeProxy getLastTempProxyCollisionShape() {
    	if(lastTempProxyCollisionShape==null && (collisionShape instanceof ShapeProxy))
    		return (ShapeProxy)collisionShape;
		return lastTempProxyCollisionShape;
	}

	@Override
	public void setGravity(Vector3f acceleration) {
		if(((JBulletDynamicPhysicsNode)getParentNode()).isAffectedByGravity())
			super.setGravity(acceleration);
	}
}
