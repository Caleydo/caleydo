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

import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Started Date: Jul 16, 2004<br><br>
 * Same as TransformMatrix, but stores rotations as quats, not Matrix3f.  This is faster for interpolation, but slower
 * than a matrix using Matrix3f for rotation when doing point translation.
 * 
 * @author Jack Lindamood
 * @author Joshua Slack
 */
public class TransformQuaternion implements Serializable, Savable, Cloneable {
    private static final long serialVersionUID = 1L;

    private Quaternion rot=new Quaternion();
    private Vector3f translation=new Vector3f();
    private Vector3f scale=new Vector3f(1,1,1);

    /**
     * Sets this rotation to the given Quaternion value by copying.
     * @param rot The new rotation for this matrix.
     */
    public void setRotationQuaternion(Quaternion rot) {
        this.rot.set(rot);
    }

    /**
     * Sets this translation to the given value by copying.
     * @param trans The new translation for this matrix.
     */
    public void setTranslation(Vector3f trans) {
        this.translation.set(trans);
    }

    /**
     * Return the translation vector in this matrix.
     * @return translation vector.
     */
    public Vector3f getTranslation() {
        return translation;
    }

    /**
     * Sets this scale to the given value by copying.
     * @param scale The new scale for this matrix.
     */
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    /**
     * Return the scale vector in this matrix.
     * @return scale vector.
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * Stores this translation value into the given vector3f.  If trans is null, a new vector3f is created to
     * hold the value.  The value, once stored, is returned.
     * @param trans The store location for this matrix's translation.
     * @return The value of this matrix's translation.
     */
    public Vector3f getTranslation(Vector3f trans) {
        if (trans==null) trans=new Vector3f();
        trans.set(this.translation);
        return trans;
    }

    /**
     * Stores this rotation value into the given Quaternion.  If quat is null, a new Quaternion is created to
     * hold the value.  The value, once stored, is returned.
     * @param quat The store location for this matrix's rotation.
     * @return The value of this matrix's rotation.
     */
    public Quaternion getRotation(Quaternion quat) {
        if (quat==null) quat=new Quaternion();
        quat.set(rot);
        return quat;
    }
    
    /**
     * Return the rotation quaternion in this matrix.
     * @return rotation quaternion.
     */
    public Quaternion getRotation() {
        return rot;
    } 
    
    /**
     * Stores this scale value into the given vector3f.  If scale is null, a new vector3f is created to
     * hold the value.  The value, once stored, is returned.
     * @param scale The store location for this matrix's scale.
     * @return The value of this matrix's scale.
     */
    public Vector3f getScale(Vector3f scale) {
        if (scale==null) scale=new Vector3f();
        scale.set(this.scale);
        return scale;
    }

    /**
     * Sets this matrix to the interpolation between the first matrix and the second by delta amount.
     * @param t1 The begining transform.
     * @param t2 The ending transform.
     * @param delta An amount between 0 and 1 representing how far to interpolate from t1 to t2.
     */
    public void interpolateTransforms(TransformQuaternion t1, TransformQuaternion t2, float delta) {
        this.rot.slerp(t1.rot,t2.rot,delta);
        this.translation.interpolate(t1.translation,t2.translation,delta);
        this.scale.interpolate(t1.scale,t2.scale,delta);
    }

    /**
     * Changes the values of this matrix acording to it's parent.  Very similar to the concept of Node/Spatial transforms.
     * @param parent The parent matrix.
     * @return This matrix, after combining.
     */
    public TransformQuaternion combineWithParent(TransformQuaternion parent) {
        scale.multLocal(parent.scale);
        rot.multLocal(parent.rot);
        parent
            .rot
            .multLocal(translation)
            .multLocal(parent.scale)
            .addLocal(parent.translation);
        return this;
    }

    /**
     * Applies the values of this matrix to the given Spatial.
     * @param spatial The spatial to be affected by this matrix.
     */
    public void applyToSpatial(Spatial spatial) {
        spatial.getLocalScale().set(scale);
        spatial.getLocalRotation().set(rot);
        spatial.getLocalTranslation().set(translation);
    }

    /**
     * Sets this matrix's translation to the given x,y,z values.
     * @param x This matrix's new x translation.
     * @param y This matrix's new y translation.
     * @param z This matrix's new z translation.
     */
    public void setTranslation(float x,float y, float z) {
        translation.set(x,y,z);
    }

    /**
     * Sets this matrix's scale to the given x,y,z values.
     * @param x This matrix's new x scale.
     * @param y This matrix's new y scale.
     * @param z This matrix's new z scale.
     */     public void setScale(float x, float y, float z) {
        scale.set(x,y,z);
    }

    /**
     * Loads the identity.  Equal to translation=1,1,1 scale=0,0,0 rot=0,0,0,1.
     */
    public void loadIdentity() {
        translation.set(0,0,0);
        scale.set(1,1,1);
        rot.set(0,0,0,1);
    }

    /**
     * Sets this matrix to be equal to the given matrix.
     * @param matrixQuat The matrix to be equal to.
     */
    public void set(TransformQuaternion matrixQuat) {
        this.translation.set(matrixQuat.translation);
        this.rot.set(matrixQuat.rot);
        this.scale.set(matrixQuat.scale);
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(rot, "rot", new Quaternion());
        capsule.write(translation, "translation", Vector3f.ZERO);
        capsule.write(scale, "scale", Vector3f.UNIT_XYZ);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        
        rot = (Quaternion)capsule.readSavable("rot", new Quaternion());
        translation = (Vector3f)capsule.readSavable("translation", Vector3f.ZERO.clone());
        scale = (Vector3f)capsule.readSavable("scale", Vector3f.UNIT_XYZ.clone());
    }
    
    public Class<? extends TransformQuaternion> getClassTag() {
        return this.getClass();
    }
    
    @Override
    public TransformQuaternion clone() {
        try {
            TransformQuaternion tq = (TransformQuaternion) super.clone();
            tq.rot = rot.clone();
            tq.scale = scale.clone();
            tq.translation = translation.clone();
            return tq;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int hashCode() {
        return rot.hashCode() * 2 + translation.hashCode() * 3
            + scale.hashCode() * 5;
    }

    @Override
    public boolean equals(Object oIn) {
        if (oIn == null) return false;
        if (oIn.getClass() != TransformQuaternion.class) return false;
        TransformQuaternion o = (TransformQuaternion) oIn;
        return rot.equals(o.rot) && translation.equals(o.translation)
                && scale.equals(o.scale);
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * It is simply a toString() call of the rotational matrix and the translational vector
     * @return the string representation of this object.
     */
    public String toString() {
        return TransformQuaternion.class.getName() + " [\n"+
                rot.toString() + ":" +
                translation.toString() + ":" +
                scale.toString() + "\n]";
    }
}
