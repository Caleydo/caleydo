package com.jmex.physics.impl.jbullet.geometry;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsCylinder;
import com.jmex.physics.impl.jbullet.JBulletPhysicsNode;
import com.jmex.physics.impl.jbullet.geometry.proxies.CylinderProxy;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;
import com.jmex.physics.material.Material;

public class JBulletCylinder extends PhysicsCylinder implements JBulletGeometry {
    private CollisionShape shape;
    private boolean dirty;
    private com.jme.math.Vector3f previousScale = new com.jme.math.Vector3f();
    private Quaternion previousRotation = new Quaternion();
    private com.jme.math.Vector3f previousTranslation = new com.jme.math.Vector3f();

    public JBulletCylinder( PhysicsNode node ) {
        super( node );
		shape = new CylinderProxy(new Vector3f(1f,1f,0.5f),this);
    }

    public CollisionShape getJBulletShape() {
        return shape;
    }

    public boolean isDirty() {
        if ( dirty ) {
            return true;
        }
        return !( getLocalTranslation().equals( previousTranslation ) &&
                getLocalRotation().equals( previousRotation ) &&
                getLocalScale().equals( previousScale ) );
    }

    public void setDirty( boolean value ) {
        dirty = value;
        if ( !value ) {
            previousTranslation.set( getLocalTranslation() );
            previousRotation.set( getLocalRotation() );
            previousScale.set( getLocalScale() );
        }
    }

    @Override
    public void setMaterial( Material value ) {
        super.setMaterial( value );
        dirty = true;
    }

    @Override
    protected void setNode( PhysicsNode node ) {
        ( (JBulletPhysicsNode) getPhysicsNode() ).setDirty( true );
        super.setNode( node );
        if ( node != null ) {
            ( (JBulletPhysicsNode) node ).setDirty( true );
        }
        dirty = true;
    }

    @Override
    protected void setParent( Node parent ) {
        ( (JBulletPhysicsNode) getPhysicsNode() ).setDirty( true );
        super.setParent( parent );
        if ( parent != null ) {
            ( (JBulletPhysicsNode) parent ).setDirty( true );
        }
        dirty = true;
    }

    @Override
    public void lookAt( com.jme.math.Vector3f position, com.jme.math.Vector3f upVector ) {
        super.lookAt( position, upVector );
        dirty = true;
    }

    @Override
    public void rotateUpTo( com.jme.math.Vector3f newUp ) {
        super.rotateUpTo( newUp );
        dirty = true;
    }

    @Override
    public void setLocalRotation( Matrix3f rotation ) {
        super.setLocalRotation( rotation );
        dirty = true;
    }

    @Override
    public void setLocalRotation( Quaternion quaternion ) {
        super.setLocalRotation( quaternion );
        dirty = true;
    }

    @Override
    public void setLocalScale( float localScale ) {
        super.setLocalScale( localScale );
        dirty = true;
    }

    @Override
    public void setLocalScale( com.jme.math.Vector3f localScale ) {
        super.setLocalScale( localScale );
        dirty = true;
    }

    @Override
    public void setLocalTranslation( float x, float y, float z ) {
        super.setLocalTranslation( x, y, z );
        dirty = true;
    }

    @Override
    public void setLocalTranslation( com.jme.math.Vector3f localTranslation ) {
        super.setLocalTranslation( localTranslation );
        dirty = true;
    }

	public void buildShapeToScale() {
		com.jme.math.Vector3f baseSize = new com.jme.math.Vector3f(1f,1f,0.5f);
		baseSize.multLocal(getWorldScale());
		shape = new CylinderProxy(VecmathConverter.convert(baseSize),this);
	}
}
