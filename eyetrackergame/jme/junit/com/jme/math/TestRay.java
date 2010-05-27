package com.jme.math;

/**
 * JUnit Test for com.jme.math.Ray
 * @see com.jme.math.Ray
 * @author jeff_m_t
 */
public class TestRay extends junit.framework.TestCase {
    private Vector3f v0;
    private Vector3f v1;
    private Vector3f v2;

    public void setUp() throws Exception {
        v0 = new Vector3f(-1f,-1f,-1f);
        v1 = new Vector3f(+1f,-1f,-1f);
        v2 = new Vector3f(+1f,+1f,-1f);
    }

    public void testIntersectWhere() throws Exception {
        
        Vector3f intersectionPoint = new Vector3f();
        Ray pickRay;
        
        // inside triangle
        pickRay = new Ray(new Vector3f(0.5f,-0.5f,3f),new Vector3f(0f,0f,-1f));     
        assertTrue(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));

        // horizontal edge
        pickRay = new Ray(new Vector3f(0f,-1f,3f),new Vector3f(0f,0f,-1f));     
        assertTrue(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));
        
        // diagonal edge
        pickRay = new Ray(new Vector3f(0f,0f,3f),new Vector3f(0f,0f,-1f));      
        assertTrue(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));

        // vertical edge
        pickRay = new Ray(new Vector3f(+1f,0f,3f),new Vector3f(0f,0f,-1f));     
        assertTrue(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));

        // v0
        pickRay = new Ray(new Vector3f(-1f,-1f,3f),new Vector3f(0f,0f,-1f));        
        assertTrue(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));

        // v1
        pickRay = new Ray(new Vector3f(+1f,-1f,3f),new Vector3f(0f,0f,-1f));        
        assertTrue(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));

        // v2
        pickRay = new Ray(new Vector3f(1f,1f,3f),new Vector3f(0f,0f,-1f));      
        assertTrue(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));
        
        // outside horizontal edge
        pickRay = new Ray(new Vector3f(0f,-1.1f,3f),new Vector3f(0f,0f,-1f));       
        assertFalse(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));
        
        // outside diagonal edge
        pickRay = new Ray(new Vector3f(-0.1f,0.1f,3f),new Vector3f(0f,0f,-1f));     
        assertFalse(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));

        // outside vertical edge
        pickRay = new Ray(new Vector3f(+1.1f,0f,3f),new Vector3f(0f,0f,-1f));       
        assertFalse(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));

        // inside triangle but ray pointing other way
        pickRay = new Ray(new Vector3f(-0.5f,-0.5f,3f),new Vector3f(0f,0f,+1f));        
        assertFalse(pickRay.intersectWhere(v0, v1, v2, intersectionPoint));
    }
}