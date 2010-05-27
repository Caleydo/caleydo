package com.jmex.physics.impl.jbullet;

import java.util.ArrayList;
import java.util.List;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.impl.jbullet.geometry.JBulletGeometry;
import com.jmex.physics.impl.jbullet.geometry.proxies.CylinderProxy;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;

public class JBulletStaticPhysicsNode extends StaticPhysicsNode implements
        JBulletPhysicsNode {

    private JBulletPhysicsSpace space;
    private JBulletRigidBody body;
    private MotionState motionState = new DefaultMotionState();
    private boolean dirty;

    private Matrix3f tempRotMatrix = new Matrix3f();

    private List<JBulletGeometry> collisionShapes = new ArrayList<JBulletGeometry>();

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

        if ( body != null ) {
            space.dynamicsWorld.removeRigidBody( body );
        }

        CollisionShape mainShape;

        if ( collisionShapes.size() == 0 ) {
            body = new JBulletRigidBody( 0, motionState, null, new javax.vecmath.Vector3f(), this );
            body.setCollisionFlags( body.getCollisionFlags() | CollisionFlags.STATIC_OBJECT );
        } else if ( collisionShapes.size() == 1 && collisionShapes.get( 0 ).getLocalTranslation().equals( Vector3f.ZERO ) && collisionShapes.get( 0 ).getLocalRotation().equals( QUAT_ZERO_ROT ) ) {
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
            RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo( 0, motionState, mainShape );
            //Need to insert code here to deal with material types
            //ci.setSomePropertyToDealWithMaterials();
            body = new JBulletRigidBody( ci, this );
            body.setCollisionFlags(body.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
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
                VecmathConverter.convert( geom.getLocalTranslation(), t.origin );
                geom.getLocalRotation().toRotationMatrix( tempRotMatrix );
                VecmathConverter.convert( tempRotMatrix, t.basis );
                ( (CompoundShape) mainShape ).addChildShape( t, geom.getJBulletShape() );
                geom.setDirty( false );
            }
            mainShape.setLocalScaling( VecmathConverter.convert( getWorldScale() ) );
            RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo( 0, motionState, mainShape );
            
            body = new JBulletRigidBody( ci, this );
            body.setCollisionFlags(body.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
            dirty = false;
        }
        
        updateWorldVectors();

        if ( isActive() ) {
            ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.addRigidBody( body );
        }
    }

	public JBulletStaticPhysicsNode( JBulletPhysicsSpace space ) {
        this.space = space;
    }

    @Override
    public PhysicsSpace getSpace() {
        return this.space;
    }

    public JBulletRigidBody getBody() {
        return this.body;
    }

    public boolean isDirty() {
        if ( dirty ) {
            return dirty;
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

    @Override
    public boolean setActive( boolean value ) {
        if ( value != super.isActive() && body != null ) {
            if ( value ) {
                ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.addRigidBody( body );
                body.activate();
            } else {
                ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.removeRigidBody( body );
            }
        }
        return super.setActive( value );
    }

    @Override
    public void updateWorldVectors() {
        if ( getParent() != null ) {
            getParent().updateWorldVectors();
        }
        super.updateWorldVectors();
        if ( body != null ) {
            body.setWorldRotation( getWorldRotation() );
            body.setWorldTranslation( getWorldTranslation() );
            motionState.setWorldTransform( body.getWorldTransform(new Transform()) );
        }
    }
}
