package com.jmex.physics.impl.jbullet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.impl.jbullet.geometry.JBulletGeometry;
import com.jmex.physics.impl.jbullet.geometry.JBulletMesh;
import com.jmex.physics.impl.jbullet.geometry.proxies.CylinderProxy;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;
import com.jmex.physics.material.Material;

public class JBulletDynamicPhysicsNode extends DynamicPhysicsNode implements JBulletPhysicsNode {

    private boolean dirty = true;
    
    private boolean affectedByGravity = true;

    private Quaternion tempRotation = new Quaternion();
    private Vector3f tempPosition = new Vector3f();
    private Transform tempTransform = new Transform();
    private Vector3f centerOfMass = new Vector3f();

    private JBulletRigidBody body;
    private JBulletPhysicsSpace space;
    private Vector3f linearVelocity = new Vector3f( 0f, 0f, 0f );
    private Vector3f angularVelocity = new Vector3f( 0f, 0f, 0f );
    private Vector3f addedForce = new Vector3f( 0f, 0f, 0f );
    private Vector3f addedForceLocation = new Vector3f( 0f, 0f, 0f );
    private Vector3f addedTorque = new Vector3f( 0f, 0f, 0f );
    private float mass = 1f;

    private MotionState motionState = new DefaultMotionState();
    private Matrix3f tempRotMatrix = new Matrix3f();
    private Vector3f tempTranslation = new Vector3f();
    private Quaternion tempRot = new Quaternion();
    private javax.vecmath.Vector3f tempVec = new javax.vecmath.Vector3f();

    private List<JBulletGeometry> collisionShapes = new ArrayList<JBulletGeometry>();

    private Set<Joint> myJoints = new HashSet<Joint>();

    @Override
    public int attachChild( Spatial child ) {
        int index = super.attachChild( child );
        if ( child instanceof JBulletGeometry ) {
            dirty = true;
            collisionShapes.add( (JBulletGeometry) child );
        }
        return index;
    }

    @Override
    public int detachChild( Spatial child ) {
        int index = super.detachChild( child );
        if ( child instanceof JBulletGeometry ) {
            dirty = true;
            collisionShapes.remove( child );
        }
        return index;
    }

    public void rebuildRigidBody() {

        javax.vecmath.Vector3f localInertia = new javax.vecmath.Vector3f( 0, 0, 0 );

        if ( body != null ) {
            space.dynamicsWorld.removeRigidBody( body );
        }

        updateWorldVectors();

        CollisionShape mainShape;
        if ( collisionShapes.size() == 0 ) {
            body = new JBulletRigidBody( 0, motionState, null, localInertia, this );
            body.setCollisionFlags( body.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT );
            body.setSleepingThresholds(space.getAutoRestThreshold(),space.getAutoRestThreshold());
            dirty = false;
        } else if ( collisionShapes.size() == 1 && 
        		!(collisionShapes.get( 0 ) instanceof JBulletMesh) &&
        		collisionShapes.get( 0 ).getLocalTranslation().equals( Vector3f.ZERO ) && 
        		collisionShapes.get( 0 ).getLocalRotation().equals( QUAT_ZERO_ROT ) ) {
            mainShape = collisionShapes.get( 0 ).getJBulletShape();
            if(mainShape instanceof CylinderProxy)
            {
            	collisionShapes.get(0).buildShapeToScale();
            	mainShape = collisionShapes.get(0).getJBulletShape();
            }
            else
            {
            	mainShape.setLocalScaling( VecmathConverter.convert( getWorldScale().mult( collisionShapes.get( 0 ).getLocalScale() ) ) );
            }
            //mass = collisionShapes.get( 0 ).getVolume() * getMaterial().getDensity();
            mainShape.calculateLocalInertia( mass, localInertia );
            RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo( mass, motionState, mainShape, localInertia );
            //Need to insert code here to deal with material types
            //ci.setSomePropertyToDealWithMaterials();
            body = new JBulletRigidBody( ci, this );
            body.setCollisionFlags(body.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
            body.setSleepingThresholds(space.getAutoRestThreshold(),space.getAutoRestThreshold());
            dirty = false;
            collisionShapes.get( 0 ).setDirty( false );
        } else {
            mainShape = new CompoundShape();
            for ( JBulletGeometry geom : collisionShapes ) {
                Transform t = new Transform();
                t.setIdentity();
                CollisionShape cs = geom.getJBulletShape();
                if(mainShape instanceof CylinderProxy)
                {
                	geom.buildShapeToScale();
                	cs = geom.getJBulletShape();
                }
                else
                {
                	cs.setLocalScaling( VecmathConverter.convert( getWorldScale().mult( collisionShapes.get( 0 ).getLocalScale() ) ) );
                }
                VecmathConverter.convert( geom.getLocalTranslation().subtract(centerOfMass), t.origin );
                geom.getLocalRotation().toRotationMatrix( tempRotMatrix );
                VecmathConverter.convert( tempRotMatrix, t.basis );
                ( (CompoundShape) mainShape ).addChildShape( t, geom.getJBulletShape() );
                geom.setDirty( false );
            }
            
            getWorldRotation().mult(centerOfMass,tempPosition);
            tempPosition.addLocal(getWorldTranslation());
            tempRotation.set(getWorldRotation());
            
            tempTransform.setIdentity();
            VecmathConverter.convert(tempRotation.toRotationMatrix(),tempTransform.basis);
            VecmathConverter.convert(tempPosition,tempTransform.origin);
            
            Transform cm = new Transform();
            cm.setIdentity();
            VecmathConverter.convert(centerOfMass,cm.origin);
            cm.inverse();
            motionState = new DefaultMotionState(tempTransform,cm);
            
            mainShape.calculateLocalInertia( mass, localInertia );
            
            RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo( mass, motionState, mainShape, localInertia );
            //Need to insert code here to deal with material types
            //ci.setSomePropertyToDealWithMaterials();
            body = new JBulletRigidBody( ci, this );
            body.setCollisionFlags(body.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
            body.setSleepingThresholds(space.getAutoRestThreshold(),space.getAutoRestThreshold());
            
            dirty = false;
        }

        updateWorldVectors();
                
        body.setLinearVelocity( VecmathConverter.convert(linearVelocity) );
        body.setAngularVelocity( VecmathConverter.convert(angularVelocity) );
        
        if ( !isAffectedByGravity() ) {
            body.setGravity( new javax.vecmath.Vector3f() );
        } else {
            body.setGravity( VecmathConverter.convert( ( ( (JBulletPhysicsSpace) getSpace() ).gravity ) ) );
        }

        if ( isActive() ) {
            ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.addRigidBody( body );
        }
    }

    public JBulletDynamicPhysicsNode( JBulletPhysicsSpace space ) {
        this.space = space;
    }

    public void applyForces(float time)
    {
    	if(body==null)
    		return;
    	if(addedForce.length()>0)
    	{
	    	if(addedForceLocation.length()>0)
	    		body.applyImpulse(VecmathConverter.convert(addedForce.multLocal(time)), VecmathConverter.convert(addedForceLocation));
	    	else
	    		body.applyCentralImpulse(VecmathConverter.convert(addedForce.multLocal(time)));
    	}
    	
    	if(addedTorque.length()>0)
    		body.applyTorqueImpulse(VecmathConverter.convert(addedTorque.multLocal(time)));
    	
    	addedForce.zero();
    	addedForceLocation.zero();
    	addedTorque.zero();
    }
    
    public void applyVelocities()
    {
    	if(body==null)
    		return;
    	
    	VecmathConverter.convert(angularVelocity,tempVec);
    	body.setAngularVelocity(tempVec);
    	VecmathConverter.convert(linearVelocity,tempVec);
    	body.setLinearVelocity(tempVec);
    }
    
    @Override
    public void addForce( Vector3f force, Vector3f at ) {
    	addedForce.set(force);
    	addedForceLocation.set(at);
    }

    @Override
    public void addTorque( Vector3f torque ) {
    	addedTorque.set(torque);
    }

    @Override
    public void addForce( Vector3f force ) {
    	addedForce.set(force);
    }

    @Override
    public void clearForce() {
    	if(body!=null)
    		body.clearForces();
    }

    @Override
    public void clearTorque() {
    	if(body!=null)
    		body.clearForces();
    }

    @Override
    public void computeMass() {
        mass = 0f;
        float tempMass;
        centerOfMass=new Vector3f();
        Material mat;
        for ( JBulletGeometry geom : collisionShapes ) {
            Transform t = new Transform();
            t.setIdentity();
            CollisionShape cs = geom.getJBulletShape();
            cs.setLocalScaling( VecmathConverter.convert( getWorldScale().mult( geom.getLocalScale() ) ) );
            VecmathConverter.convert( geom.getLocalTranslation().divideLocal( geom.getLocalScale() ), t.origin );
        	mat = geom.getMaterial();
        	if(mat==null) mat=getMaterial();
            tempMass = geom.getVolume() * mat.getDensity();
            mass += tempMass;
            if(mass>0)
            	centerOfMass.interpolate(VecmathConverter.convert(t.origin), tempMass/mass);
        }
        if(mass==0)
        	mass=1f;
        dirty = true;
    }

    @Override
    public Vector3f getAngularVelocity( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        if ( body != null )
        {
        	VecmathConverter.convert( body.getAngularVelocity(), store );
        }
        else
        {
            store.zero();
        }
        return store;
    }

    @Override
    public Vector3f getCenterOfMass( Vector3f store ) {
    	return store.set( centerOfMass );
    }

    @Override
    public Vector3f getForce( Vector3f store ) {
        return store.set( Vector3f.ZERO );
    }

    @Override
    public Vector3f getLinearVelocity( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        if ( body != null )
        {
            VecmathConverter.convert( body.getLinearVelocity(), store );
        }
        else
        {
            store.zero();
        }
        return store;
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public Vector3f getTorque( Vector3f store ) {
        return store.set( Vector3f.ZERO );
    }

    @Override
    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    @Override
    public void rest() {
        if ( body != null ) {
            body.setActivationState( CollisionObject.WANTS_DEACTIVATION );
        }
    }

    @Override
	public void clearDynamics() {
		super.clearDynamics();
		unrest();
	}

	@Override
    public void setAffectedByGravity( boolean value ) {
        affectedByGravity = value;
        if ( body == null ) {
            return;
        }
        if ( value ) {
            body.setGravity( VecmathConverter.convert( getSpace().getDirectionalGravity( null ) ) );
        } else {
            body.setGravity( VecmathConverter.convert( Vector3f.ZERO ) );
        }
    }

    @Override
    public void setAngularVelocity( Vector3f velocity ) {
        angularVelocity.set( velocity );
        if ( body != null ) {
            body.setAngularVelocity( VecmathConverter.convert( velocity ) );
        }

    }

    @Override
    public void setCenterOfMass( Vector3f value ) {
        centerOfMass.set(value);
        dirty = true;
        if(body!=null)
        {
        	Transform t = new Transform();
        	VecmathConverter.convert(centerOfMass,t.origin);
        	body.setCenterOfMassTransform(t);
        }
    }

    @Override
    public void setLinearVelocity( Vector3f velocity ) {
        linearVelocity.set( velocity );
        if ( body != null ) {
            body.setLinearVelocity( VecmathConverter.convert( velocity ) );
        }
    }

    @Override
    public void setMass( float value ) {
        dirty = true;
        mass = value;
    }

    @Override
    public void unrest() {
        if ( body != null ) {
            body.activate();
        }
    }

    @Override
    public PhysicsSpace getSpace() {
        return this.space;
    }

    @Override
    public boolean isResting() {
        if ( body == null ) {
            return true;
        }
        return body.isActive();
    }

    public boolean isDirty() {
        if ( dirty ) {
            return true;
        }
        for ( JBulletGeometry geom : this.collisionShapes ) {
            if ( geom.isDirty() ) {
                return true;
            }
        }
        return false;
    }

    public void setDirty( boolean dirty ) {
        this.dirty = dirty;
    }

    public JBulletRigidBody getBody() {
        return body;
    }

    @Override
    public void updateWorldVectors() {
        if ( getParent() != null ) {
            getParent().updateWorldVectors();
        }
        super.updateWorldVectors();
        if ( body != null ) {
            getWorldRotation().mult(centerOfMass,tempPosition);
            tempPosition.addLocal(getWorldTranslation());
            tempRotation.set(getWorldRotation());
            
            tempTransform.setIdentity();
            VecmathConverter.convert(tempRotation.toRotationMatrix(),tempTransform.basis);
            VecmathConverter.convert(tempPosition,tempTransform.origin);
            
            body.setWorldTransform(tempTransform);
            motionState.setWorldTransform( tempTransform );
        }
    }

    public void applyPhysicsMovement() {
        VecmathConverter.convert( body.getWorldTransform(new Transform() ).basis, tempRotMatrix );
        VecmathConverter.convert( body.getWorldTransform(new Transform()).origin, tempTranslation );
        if ( getParent() != null ) {
	    	tempRot.fromRotationMatrix(tempRotMatrix);
	    	setLocalRotation(getParent().getWorldRotation().inverse().mult(tempRot));
	    	tempRot.mult(centerOfMass,tempPosition);
            tempTranslation.addLocal(tempPosition.mult(-1));
        	tempTranslation.subtractLocal(getParent().getWorldTranslation());
        	getParent().getWorldRotation().inverse().mult(tempTranslation, getLocalTranslation());
        } else {
            getLocalRotation().fromRotationMatrix( tempRotMatrix );
        	getWorldRotation().mult(centerOfMass,tempPosition);
            tempTranslation.addLocal(tempPosition.mult(-1));
        	getLocalTranslation().set( tempTranslation );
        }
        body.getLinearVelocity(tempVec);
        VecmathConverter.convert(tempVec,linearVelocity);
        body.getAngularVelocity(tempVec);
        VecmathConverter.convert(tempVec,angularVelocity);
    }

    @Override
    public boolean setActive( boolean value ) {
        if ( value != super.isActive() && body != null ) {
            if ( value ) {
                ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.addRigidBody( body );
            } else {
                ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.removeRigidBody( body );
            }
        }
        return super.setActive( value );
    }

    public void addParticipatingJoint( Joint joint ) {
        myJoints.add( joint );
    }

    public void removeParticipatingJoint( Joint joint ) {
        myJoints.remove( joint );
    }

    public Set<Joint> getMyJoints() {
        return Collections.unmodifiableSet( myJoints );
    }
}
