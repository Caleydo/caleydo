package com.jmex.physics.impl.jbullet;

import com.jmex.physics.CollisionGroup;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;

public class JBulletCollisionGroup extends CollisionGroup {

    public JBulletCollisionGroup( PhysicsSpace space, String name ) {
        super( space, name );
        this.collidesWith(this, true);
    }

    @Override
    protected void nodeAdded( PhysicsNode node ) {
    }

    @Override
    protected void nodeRemoved( PhysicsNode node ) {
    }

}
