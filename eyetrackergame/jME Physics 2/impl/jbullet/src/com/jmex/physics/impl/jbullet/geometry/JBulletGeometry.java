package com.jmex.physics.impl.jbullet.geometry;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.material.Material;

public interface JBulletGeometry {

    public CollisionShape getJBulletShape();

    public float getVolume();

    public Quaternion getLocalRotation();

    public Vector3f getLocalTranslation();

    public Vector3f getLocalScale();

    public void setLocalScale( Vector3f scale );

    public void setLocalScale( float scalar );

    public boolean isDirty();

    public void setDirty( boolean value );

    Vector3f getWorldScale();
    
    public Material getMaterial();
    
    public void setMaterial(Material material);
    
    public void buildShapeToScale();
}
