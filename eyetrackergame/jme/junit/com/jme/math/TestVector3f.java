package com.jme.math;

public class TestVector3f extends junit.framework.TestCase {
    private Vector3f vector1;
    private Vector3f vector2;

    protected void setUp() throws Exception {
        vector1 = new Vector3f(1.0f, 2.0f, 3.0f);
        vector2 = new Vector3f(4.0f, 5.0f, 6.0f);
    }

    public void testCreation() {
        Vector3f vec1 = new Vector3f();
        testVectorEquals("Init zero", vec1, 0.0f, 0.0f, 0.0f);

        Vector3f vec2 = new Vector3f(1.0f, 2.0f, 3.0f);
        testVectorEquals("Init something", vec2, 1.0f, 2.0f, 3.0f);

        Vector3f vec3 = new Vector3f(vec2);
        testVectorEquals("Init clone", vec3, 1.0f, 2.0f, 3.0f);
    }

    public void testSet() {
        Vector3f vec1 = new Vector3f();
        vec1.set(1.0f, 2.0f, 3.0f);
        testVectorEquals("Set something", vec1, 1.0f, 2.0f, 3.0f);

        vec1.set(1, 5.0f);
        testVectorEquals("Set index", vec1, 1.0f, 5.0f, 3.0f);
    }

    public void testEquals() {
        Vector3f vec1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f vec2 = new Vector3f(1.0f, 2.0f, 3.0f);
        assertTrue("Equal vectors", vec1.equals(vec2));

        Vector3f vec3 = new Vector3f(4.0f, 5.0f, 6.0f);
        assertFalse("Not equal vectors", vec2.equals(vec3));
    }

    public void testAdd() {
        Vector3f result = vector1.add(vector2);
        testVectorEquals("Add", result, 5.0f, 7.0f, 9.0f);

        result = result.add(-1.0f, 1.0f, 1.0f);
        testVectorEquals("Add", result, 4.0f, 8.0f, 10.0f);

        result.add(vector2, result);
        testVectorEquals("Add", result, 8.0f, 13.0f, 16.0f);
    }

    public void testAddLocal() {
        Vector3f result = new Vector3f(vector1);

        result.addLocal(vector2);
        testVectorEquals("Addlocal", result, 5.0f, 7.0f, 9.0f);

        result.addLocal(-1.0f, 1.0f, 1.0f);
        testVectorEquals("Addlocal", result, 4.0f, 8.0f, 10.0f);
    }

    public void testSubtract() {
        Vector3f result = vector1.subtract(vector2);
        testVectorEquals("Subtract", result, -3.0f, -3.0f, -3.0f);

        result = result.subtract(-1.0f, 1.0f, 1.0f);
        testVectorEquals("Subtract", result, -2.0f, -4.0f, -4.0f);

        result.subtract(vector2, result);
        testVectorEquals("Subtract", result, -6.0f, -9.0f, -10.0f);
    }

    public void testSubtractLocal() {
        Vector3f result = new Vector3f(vector1);

        result.subtractLocal(vector2);
        testVectorEquals("Subtractlocal", result, -3.0f, -3.0f, -3.0f);

        result.subtractLocal(-1.0f, 1.0f, 1.0f);
        testVectorEquals("Subtractlocal", result, -2.0f, -4.0f, -4.0f);
    }

    public void testMult() {
        Vector3f result = vector1.mult(vector2);
        testVectorEquals("Mult", result, 4.0f, 10.0f, 18.0f);

        result = result.mult(2.0f);
        testVectorEquals("Mult", result, 8.0f, 20.0f, 36.0f);

        result.mult(vector2, result);
        testVectorEquals("Mult", result, 32.0f, 100.0f, 216.0f);
    }

    public void testMultLocal() {
        Vector3f result = new Vector3f(vector1);

        result.multLocal(vector2);
        testVectorEquals("Multlocal", result, 4.0f, 10.0f, 18.0f);

        result.multLocal(2.0f);
        testVectorEquals("Multlocal", result, 8.0f, 20.0f, 36.0f);
    }

    public void testCross() {
        Vector3f result = vector1.cross(vector2);
        testVectorEquals("Cross", result, -3.0f, 6.0f, -3.0f);
    }

    public void testLength() {
        float result = vector1.lengthSquared();
        assertEquals("Length", 14.0f, result, FastMath.FLT_EPSILON);

        result = vector1.length();
        assertEquals("Length", 3.7416575f, result, FastMath.FLT_EPSILON);
    }

    private void testVectorEquals(String test, Vector3f v, float x, float y,
            float z) {
        boolean success = true;
        if (Float.compare(x, v.x) != 0)
            success = false;
        if (Float.compare(y, v.y) != 0)
            success = false;
        if (Float.compare(z, v.z) != 0)
            success = false;
        if (!success) {
            fail(test + " Excpected [" + x + ", " + y + ", " + z
                    + "] but was [" + v.x + ", " + v.y + ", " + v.z + "]");
        }
    }

    private void testVectorEquals(String test, Vector3f v1, Vector3f v2) {
        boolean success = true;
        if (Float.compare(v1.x, v2.x) != 0)
            success = false;
        if (Float.compare(v1.y, v2.y) != 0)
            success = false;
        if (Float.compare(v1.z, v2.z) != 0)
            success = false;
        if (!success) {
            fail(test + " Excpected [" + v1.x + ", " + v1.y + ", " + v1.z
                    + "] but was [" + v2.x + ", " + v2.y + ", " + v2.z + "]");
        }
    }
}
