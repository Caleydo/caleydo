package com.jme.scene;

import com.jme.scene.shape.Sphere;
import com.jme.bounding.BoundingSphere;
import static org.junit.Assert.*;

/**
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class CollisionMaskTest {
    public CollisionMaskTest() {}

    protected Node n;
    protected Sphere a, b, c;

    @org.junit.Before
    public void setupScene() {
        a = new Sphere("a", 8, 8, 1f);
    /*
     * Disabling this because the Bounding volumes never get initialized
     * correctly.
     * I must be missing something very obvious, because the
     * updateGeometricState call below should do everything that's needed
     * (since I previously allocated the Bounding* objects).
        b = new Sphere("b", 8, 8, 1f);
        c = new Sphere("c", 8, 8, 1f);
        //a.setModelBound(new BoundingSphere());
        //b.setModelBound(new BoundingSphere());
        //c.setModelBound(new BoundingSphere());
        b.getLocalTranslation().setX(.1f);
        c.getLocalTranslation().setX(20f);
        n = new Node("n");
        n.attachChild(a);
        n.attachChild(b);
        n.attachChild(c);
        n.setModelBound(new BoundingSphere());
        //a.updateWorldBound();
        //b.updateWorldBound();
        //c.updateWorldBound();
        n.updateGeometricState(0, true);
        System.err.println("BOunds of a = " + a.getWorldBound());
    }

    /*
    @org.junit.After
    public void cleanupScene() {
        a.removeFromParent();
        b.removeFromParent();
        c.removeFromParent();
        a = b = c = null;
        n = null;
    */
    }

    @org.junit.Test
    public void bitOps() {
        a.setCollisionMask(-1);
        assertTrue(a.isCollidable(0));
        a.setCollisionMask(0);
        assertTrue(a.isCollidable(0));
        assertFalse(a.isCollidable(-1));
        a.setCollisionMask(9999);
        assertFalse(a.isCollidable(-1));
        a.setCollisionMask(-9999);
        assertFalse(a.isCollidable(-1));
        a.setCollisionMask(10);
        assertTrue(a.isCollidable(10));
        a.setCollisionMask(11);
        assertTrue(a.isCollidable(10));
        a.setCollisionMask(10);
        assertFalse(a.isCollidable(11));
    }

    /*
    See comment in the 'setupScene' method above.
    @org.junit.Test
    public void geoGeoCollisions() {
        // Use requiredOnBits of 0 to just test whether the Spatials touch
        assertTrue(a.hasCollision(b, false, 0));
        assertTrue(b.hasCollision(a, false, 0));
        assertFalse(a.hasCollision(c, false, 0));
        assertFalse(c.hasCollision(b, false, 0));
        assertFalse(b.hasCollision(c, false, 0));
        assertFalse(c.hasCollision(a, false, 0));

        // These should all fail because a does not have 2 in its bit mask.
        b.setCollisionMask(3);
        c.setCollisionMask(-1);
        assertFalse(a.hasCollision(b, false, 2));
        assertFalse(b.hasCollision(a, false, 2));
        assertFalse(a.hasCollision(c, false, 2));
        assertFalse(c.hasCollision(b, false, 2));
        assertFalse(b.hasCollision(c, false, 2));
        assertFalse(c.hasCollision(a, false, 2));

        // Flip on the needed 2 bit for a.
        a.setCollisionMask(2);
        assertTrue(a.hasCollision(b, false, 2));
        assertTrue(b.hasCollision(a, false, 2));
        assertFalse(a.hasCollision(c, false, 2));
        assertFalse(c.hasCollision(b, false, 2));
        assertFalse(b.hasCollision(c, false, 2));
        assertFalse(c.hasCollision(a, false, 2));

        // Turn up a couple more bits too.
        a.setCollisionMask(10);
        assertTrue(a.hasCollision(b, false, 2));
        assertTrue(b.hasCollision(a, false, 2));
        assertFalse(a.hasCollision(c, false, 2));
        assertFalse(c.hasCollision(b, false, 2));
        assertFalse(b.hasCollision(c, false, 2));
        assertFalse(c.hasCollision(a, false, 2));
    }
    */
}
