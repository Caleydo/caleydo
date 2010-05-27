package com.jmex.physics.impl.jbullet.util;

public class VecmathConverter {

    private VecmathConverter() {
    }

    public static com.jme.math.Vector3f convert( javax.vecmath.Vector3f oldVec ) {
        com.jme.math.Vector3f newVec = new com.jme.math.Vector3f();
        convert( oldVec, newVec );
        return newVec;
    }

    public static void convert( javax.vecmath.Vector3f oldVec, com.jme.math.Vector3f newVec ) {
        newVec.x = oldVec.x;
        newVec.y = oldVec.y;
        newVec.z = oldVec.z;
    }

    public static javax.vecmath.Vector3f convert( com.jme.math.Vector3f oldVec ) {
        javax.vecmath.Vector3f newVec = new javax.vecmath.Vector3f();
        convert( oldVec, newVec );
        return newVec;
    }

    public static void convert( com.jme.math.Vector3f oldVec, javax.vecmath.Vector3f newVec ) {
        newVec.x = oldVec.x;
        newVec.y = oldVec.y;
        newVec.z = oldVec.z;
    }

    public static void convert( com.jme.math.Quaternion oldQuat, javax.vecmath.Quat4f newQuat ) {
        newQuat.w = oldQuat.w;
        newQuat.x = oldQuat.x;
        newQuat.y = oldQuat.y;
        newQuat.z = oldQuat.z;
    }

    public static javax.vecmath.Quat4f convert( com.jme.math.Quaternion oldQuat ) {
        javax.vecmath.Quat4f newQuat = new javax.vecmath.Quat4f();
        convert( oldQuat, newQuat );
        return newQuat;
    }

    public static void convert( javax.vecmath.Quat4f oldQuat, com.jme.math.Quaternion newQuat ) {
        newQuat.w = oldQuat.w;
        newQuat.x = oldQuat.x;
        newQuat.y = oldQuat.y;
        newQuat.z = oldQuat.z;
    }

    public static com.jme.math.Quaternion convert( javax.vecmath.Quat4f oldQuat ) {
        com.jme.math.Quaternion newQuat = new com.jme.math.Quaternion();
        convert( oldQuat, newQuat );
        return newQuat;
    }

    public static com.jme.math.Matrix3f convert( javax.vecmath.Matrix3f oldMatrix ) {
        com.jme.math.Matrix3f newMatrix = new com.jme.math.Matrix3f();
        convert( oldMatrix, newMatrix );
        return newMatrix;
    }

    public static void convert( javax.vecmath.Matrix3f oldMatrix, com.jme.math.Matrix3f newMatrix ) {
        newMatrix.m00 = oldMatrix.m00;
        newMatrix.m01 = oldMatrix.m01;
        newMatrix.m02 = oldMatrix.m02;
        newMatrix.m10 = oldMatrix.m10;
        newMatrix.m11 = oldMatrix.m11;
        newMatrix.m12 = oldMatrix.m12;
        newMatrix.m20 = oldMatrix.m20;
        newMatrix.m21 = oldMatrix.m21;
        newMatrix.m22 = oldMatrix.m22;
    }

    public static javax.vecmath.Matrix3f convert( com.jme.math.Matrix3f oldMatrix ) {
        javax.vecmath.Matrix3f newMatrix = new javax.vecmath.Matrix3f();
        convert( oldMatrix, newMatrix );
        return newMatrix;
    }

    public static void convert( com.jme.math.Matrix3f oldMatrix, javax.vecmath.Matrix3f newMatrix ) {
        newMatrix.m00 = oldMatrix.m00;
        newMatrix.m01 = oldMatrix.m01;
        newMatrix.m02 = oldMatrix.m02;
        newMatrix.m10 = oldMatrix.m10;
        newMatrix.m11 = oldMatrix.m11;
        newMatrix.m12 = oldMatrix.m12;
        newMatrix.m20 = oldMatrix.m20;
        newMatrix.m21 = oldMatrix.m21;
        newMatrix.m22 = oldMatrix.m22;
    }
}
