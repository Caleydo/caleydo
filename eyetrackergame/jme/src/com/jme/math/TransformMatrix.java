/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.math;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import com.jme.scene.Spatial;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * TransformMatrix holds a rotation (Matrix3f)  and translation (Vector3f) for point manipulation
 *
 * @author Jack Lindamood
 * @author Joshua Slack
 */
public class TransformMatrix  implements Serializable, Savable, Cloneable {
    private static final Logger logger = Logger.getLogger(TransformMatrix.class
            .getName());
    
    // TODO: Clean up and standardize this class's functionality
    private static final long serialVersionUID = 1L;

    private Matrix3f rot=new Matrix3f();
    private Vector3f translation=new Vector3f();
    private Vector3f scale=new Vector3f(1,1,1);

    /**
     * Constructor instantiates a new <code>TransformMatrix</code> that is set to the
     * identity matrix by default.
     *
     */
    public TransformMatrix() {
    }

    /**
     * Constructor instantiates a new <code>TransformMatrix</code> that is set to the
     * provided matrix. This constructor copies a given matrix. If the
     * provided matrix is null, the constructor sets the matrix to the
     * identity.
     * @param mat the matrix to copy.
     */
    public TransformMatrix(TransformMatrix mat) {
        set(mat);
    }

    /**
     * Constructor instantiates a new <code>TransformMatrix</code> that has rotation
     * and translation defined by its parameters
     * @param myRot The given rotation, as a <code>Quaternion</code>
     * @param myPos The given translation, as a <code>Vector3f</code>
     */
    public TransformMatrix(Quaternion myRot, Vector3f myPos) {
        rot.set(myRot);
        translation.set(myPos);
    }

    /**
     * <code>set</code> copies the contents of a given matrix to this
     * matrix. If a null matrix is supplied, this matrix is set to the
     * identity matrix.
     * @param matrix the matrix to copy.
     */
    public void set(TransformMatrix matrix) {
        if (matrix == null) {
            loadIdentity();
        } else {
            rot.copy(matrix.rot);
            translation.set(matrix.translation);
            scale.set(matrix.scale);
        }
    }


    /**
     *
     * <code>set</code> defines the values of the matrix based on a supplied
     * <code>Quaternion</code> (which it does not modify).
     * It should be noted that all previous values will be overridden.
     * @param quaternion the quaternion to create a rotational matrix from.
     */
    public void set(Quaternion quaternion) {
        rot.set(quaternion);
        translation.zero();
        scale.set(1,1,1);
    }

    /**
     * <code>loadIdentity</code> sets this matrix to the identity matrix,
     * namely all zeros with ones along the diagonal.
     *
     */
    public void loadIdentity() {
        rot.loadIdentity();
        translation.zero();
        scale.set(1,1,1);
    }

    /**
     * Multiplies every value in the matrix by a scalar
     * @param scalar
     */
    public void mult(float scalar) {
        rot.multLocal(scalar);
        translation.mult(scalar);
        scale.multLocal(scalar);
    }

    /**
     * <code>multLocal</code> multiplies this matrix with another matrix and stores
     * the result back in this, returning this.  if null is passed, nothing happens
     * This function changes this matrix to what the child would look like if this were applied as it's parent
     *
     * @param child The matrix to multiply by
     * @param tempStore A temporary Vector3f object for this TransformMatrix to use during the calculation.
     * @return this matrix after multiplication
     */
    public TransformMatrix multLocal(TransformMatrix child,Vector3f tempStore){
        this.scale.multLocal(child.scale);
        this.translation.addLocal(rot.mult(child.translation,tempStore).multLocal(child.scale));
        this.rot.multLocal(child.rot);
        return this;
    }

    /**
     * Sets this transform to an interpolation between the start and end transforms.  Note that
     * this function isn't very efficient as it has to create 2 new Quaternions to do the
     * rotation interpolation
     * @param start Begining transform (delta=0)
     * @param end Ending transform (delta=1)
     * @param delta Value between 0.0 and 1.0 to show which side the transform leans towards
     */
    public void interpolateTransforms(TransformMatrix start,TransformMatrix end,float delta){
        this.translation.set(start.translation).interpolate(end.translation,delta);
        this.scale.set(start.scale).interpolate(end.scale,delta);
        Quaternion q1=new Quaternion(),q2=new Quaternion();
        start.getRotation(q1);
        end.getRotation(q2);
        q1.slerp(q2,delta);
        this.setRotationQuaternion(q1);
    }

    /**
     * Sets this transform to an interpolation between the start and end transforms.  Same as above but doesn't
     * create 2 new Quaternions
     * @param start Begining transform (delta=0)
     * @param end Ending transform (delta=1)
     * @param delta Value between 0.0 and 1.0 to show which side the transform leans towards
     * @param q1 A temporary Quaternion
     * @param q2 Another temporary Quaternion
     */
    public void interpolateTransforms(TransformMatrix start,TransformMatrix end,float delta,Quaternion q1,Quaternion q2){
        this.translation.set(start.translation).interpolate(end.translation,delta);
        this.scale.set(start.scale).interpolate(end.scale,delta);
        start.getRotation(q1);
        end.getRotation(q2);
        q1.slerp(q2,delta);
        this.setRotationQuaternion(q1);
    }


    /**
     * <code>mult</code> multiplies a normal about a transform matrix and
     * stores the result back in vec. The resulting vector is returned
     * with translational ignored.
     * @param vec the rotation normal.
     * @return The given Vector3f, after rotation
     */
    public Vector3f multNormal(Vector3f vec) {
        if (null == vec) {
            logger.warning("Source vector is null, null result returned.");
            return null;
        }
        return rot.multLocal(vec);
    }

    /**
     * <code>mult</code> multiplies a vector about a transform matrix. The
     * resulting vector is saved in vec and returned.
     * @param vec The point to rotate.
     * @return The rotated vector.
     */
    public Vector3f multPoint(Vector3f vec) {
        if (null == vec) {
            logger.warning("Source vector is null, null result returned.");
            return null;
        }
        return rot.multLocal(vec).multLocal(scale).addLocal(translation);
    }


    /**
     * Sets the rotation matrix to the given rotation matrix via a copy.  If null is supplied, the identity is set
     * @param rot The new rotation
     */
    public void setRotation(Matrix3f rot){
        this.rot.copy(rot);
    }

    /**
     * <code>setTranslation</code> will set the matrix's translation values.
     * @param transArray the new values for the translation.
     * @throws JmeException if translation is null or not size 3.
     */
    public void setTranslation(float[] transArray) {
        if (transArray == null || transArray.length != 3) {
            throw new JmeException("Translation size must be 3.");
        }
        translation.x = transArray[0];
        translation.y = transArray[1];
        translation.z = transArray[2];
    }

    /** <code>setTranslation</code> will copy the given Vector3f's values
     * into this Matrix's translational component
     *
     * @param trans
     */
    public void setTranslation(Vector3f trans){
        if (trans==null){
            throw new JmeException("Vector3f translation must be non-null");
        }
        translation.set(trans);
    }

    /**
     * Sets the Transform's Translational component
     * @param x New X translation
     * @param y New Y translation
     * @param z New Z translation
     */
    public void setTranslation(float x,float y,float z){
        translation.set(x,y,z);
    }

    /**
     * Sets the rotational component of this transform to the matrix represented
     * by an Euler rotation about x, y, then z.
     * @param x The X rotation, in radians
     * @param y The Y rotation, in radians
     * @param z The Z rotation, in radians
     */
    public void setEulerRot(float x,float y,float z){
        double A = Math.cos(x);
        double B = Math.sin(x);
        double C = Math.cos(y);
        double D = Math.sin(y);
        double E = Math.cos(z);
        double F = Math.sin(z);
        double AD =   A * D;
        double BD =   B * D;
        rot.m00 = (float) (C * E);
        rot.m01 = (float) (BD * E + -(A * F));
        rot.m02 = (float) (AD * E + B * F);
        rot.m10 = (float) (C * F);
        rot.m11 = (float) (BD * F + A * E);
        rot.m12 = (float) (AD * F + -(B * E));
        rot.m20 = (float) -D;
        rot.m21 = (float) (B * C);
        rot.m22 = (float) (A * C);
    }

    /**
     * <code>setRotationQuaternion</code> builds a rotation from a
     * <code>Quaternion</code>.
     * @param quat The quaternion to build the rotation from.
     * @throws JmeException if quat is null.
     */
    public void setRotationQuaternion(Quaternion quat) {
        if (null == quat) {
            throw new JmeException("Quat may not be null.");
        }
        rot.set(quat);
    }

    /**
     * <code>invertRotInPlace</code> inverts the rotational component of this Matrix
     * in place
     */
    private void invertRotInPlace() {
        float temp;
        temp=rot.m01;
        rot.m01=rot.m10;
        rot.m10=temp;
        temp=rot.m02;
        rot.m02=rot.m20;
        rot.m20=temp;
        temp=rot.m21;
        rot.m21=rot.m12;
        rot.m12=temp;

    }


    /**
     * Stores the rotational part of this matrix into the passed matrix.
     * Will create a new Matrix3f if given matrix is null.  Returns the
     * given matrix after it has been loaded with rotation values, to allow
     * chaining
     *
     * @param rotStore The matrix to store rotation values
     * @return The given matrix with updated values
     */
    public Matrix3f getRotation(Matrix3f rotStore){
        if (rotStore==null) rotStore=new Matrix3f();
        rotStore.copy(rot);
        return rotStore;
    }

    /**
     * Stores the translational part of this matrix into the passed matrix.
     * Will create a new Vector3f if given vector is null.  Returns the
     * given vector after it has been loaded with translation values, to allow
     * chaining
     *
     * @param tranStore The vector to store translation values
     * @return The given Vector with updated values
     */
    public Vector3f getTranslation(Vector3f tranStore){
        if (tranStore==null) tranStore=new Vector3f();
        tranStore.set(translation);
        return tranStore;
    }

    /**
     * Stores the rotational part of this matrix into the passed Quaternion.
     * Will create a new Quaternion if given quaternion is null.  Returns the
     * given Quaternion after it has been loaded with rotation values, to allow
     * chaining
     *
     * @param rotStore The Quat to store translation values
     * @return The given Vector with updated values
     */
    public Quaternion getRotation(Quaternion rotStore){
        if (rotStore==null) rotStore=new Quaternion();
        rotStore.fromRotationMatrix(rot);
        return rotStore;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * It is simply a toString() call of the rotational matrix and the translational vector
     * @return the string representation of this object.
     */
    public String toString() {
        return TransformMatrix.class.getName() + " [\n"+
                rot.toString() + ":" +
                translation.toString() + ":" +
                scale.toString() + "\n]";
    }

    /**
     * <code>inverse</code> turns this matrix into it's own inverse
     */
    public void inverse() {
        invertRotInPlace();
        rot.multLocal(translation);
        translation.multLocal(-1);
        scale.set(1/scale.x,1/scale.y,1/scale.z);
    }

    /**
     * <code>setEulerRot</code> is equivalent to
     * setEulerRot(eulerVec.x,eulverVec.y,eulverVec.z){
     * @param eulerVec A Vector3f representing the new rotation in Euler angles
     */
    public void setEulerRot(Vector3f eulerVec) {
        this.setEulerRot(eulerVec.x,eulerVec.y,eulerVec.z);
    }

    /**
     * <code>set</code> changes this matrix's rotational and translational components
     * to that represented by the given parameters, by copying.
     * @param rotation The new rotaiton
     * @param translation The new translation
     */
    public void set(Quaternion rotation, Vector3f translation) {
        this.set(rotation);
        this.setTranslation(translation);
    }

    /**
     * Sets this TransformMatrix's scale to the given scale (x,y,z), by copying.
     * @param scale The new scale
     */
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    /**
     * Sets this TransformMatrix's scale to the given x,y,z
     * @param x The x scale
     * @param y The y scale
     * @param z The z scale
     */
    public void setScale(float x, float y, float z) {
        scale.set(x,y,z);
    }

    /**
     * Returns this TransformMatrix's scale factor
     * @param storeS The place to store the current scale factor
     * @return The given scale factor
     */
    public Vector3f getScale(Vector3f storeS) {
        if (storeS==null) storeS=new Vector3f();
        return storeS.set(this.scale);
    }

    /**
     * Applies this TransformMatrix to the given spatial, by updating the
     * spatial's local translation, rotation, scale.
     * The spatial's transform values are replaced absolutely, not relatively.
     *
     * @param spatial The spatial to update
     */
    public void applyToSpatial(Spatial spatial) {
        spatial.setLocalTranslation(translation);
        spatial.setLocalRotation(rot);
        spatial.setLocalScale(scale);
    }

    /**
     * Combines this TransformMatrix with a parent TransformMatrix.
     * @param parent The parent matrix.
     * @return This matrix, after it has been updated by it's parent.
     */
    public TransformMatrix combineWithParent(TransformMatrix parent){
        this.scale.multLocal(parent.scale);
        this.rot.multLocal(parent.rot);
        parent.rot.multLocal(this.translation).multLocal(parent.scale).addLocal(parent.translation);
        return this;

    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(rot, "rot", new Matrix3f());
        capsule.write(translation, "translation", Vector3f.ZERO);
        capsule.write(scale, "scale", Vector3f.UNIT_XYZ);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        rot = (Matrix3f)capsule.readSavable("rot", new Matrix3f());
        translation = (Vector3f)capsule.readSavable("translation", Vector3f.ZERO.clone());
        scale = (Vector3f)capsule.readSavable("scale", Vector3f.UNIT_XYZ.clone());
    }
    
    public Class<? extends TransformMatrix> getClassTag() {
        return this.getClass();
    }

    @Override
    public int hashCode() {
        return rot.hashCode() * 2 + translation.hashCode() * 3
            + scale.hashCode() * 5;
    }

    @Override
    public boolean equals(Object oIn) {
        if (oIn.getClass() != TransformMatrix.class) return false;
        TransformMatrix o = (TransformMatrix) oIn;
        return rot.equals(o.rot) && translation.equals(o.translation)
                && scale.equals(o.scale);
    }

    @Override
    public TransformMatrix clone() {
        try {
            TransformMatrix tm = (TransformMatrix) super.clone();
            tm.rot = rot.clone();
            tm.scale = scale.clone();
            tm.translation = translation.clone();
            return tm;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
