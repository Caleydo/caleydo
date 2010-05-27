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

package com.jme.animation;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Bone defines a scenegraph node that defines a single bone object within a
 * skeletal system. The bone defines a bind matrix which will transform the bone
 * into the pose position. This bine matrix is used to position the bone with
 * the skin, then move the skin to its world position.
 * 
 * @author Joshua Slack
 * @author Mark Powell
 * @version $Id: Bone.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class Bone extends Node implements Savable {
    private static final long serialVersionUID = -3167081457416773705L;
    
    protected Matrix4f bindMatrix = new Matrix4f();
    protected AnimationController animationController;

    private static boolean optimizeTransform = true;
    protected final Vector3f workVectA = new Vector3f();
    protected final Matrix4f transform = new Matrix4f();

    protected Vector3f oldScale = new Vector3f();
    protected Vector3f oldTran = new Vector3f();
    protected Quaternion oldRot = new Quaternion();

    protected transient boolean boneChanged = false;
    protected transient boolean skinRoot = false;

    private boolean isHardpoint = false;

    protected transient ArrayList<BoneChangeListener> changeListeners;

    public Bone() {
        super();
    }

    /**
     * Creates a new bone with the supplied name.
     * 
     * @param name
     *            the name of this bone.
     */
    public Bone(String name) {
        super(name);
    }

    /**
     * creates a new bone with a given name and a bind matrix.
     * 
     * @param name
     *            the name of the bone.
     * @param bindMatrix
     *            the bind matrix of the bone.
     */
    public Bone(String name, Matrix4f bindMatrix) {
        super(name);
        this.bindMatrix = bindMatrix;
    }

    /**
     * Switches the transform optimization on or off. The default is off when
     * this class is first loaded. The transform optimization works by
     * calculating this bone's world transform once before applying it to a
     * given skin, instead of calculating the transform once for every vertex
     * influenced by this bone.
     * 
     * @param enabled
     *            <code>true</code> switches the transform optimization on
     */
    public static void setOptimizeTransform(boolean enabled) {
        optimizeTransform = enabled;
    }

    /**
     * Updates this bone's useTransform and then calls its children recursively.
     * Called from SkinNode#updateSkin() so that this bone's transform is up to
     * date before the skin is recomputed. The method does nothing if the
     * transform optimization is disabled.
     */
    public void update() {
        if (!optimizeTransform) {
            return;
        }

        transform.setRotationQuaternion(worldRotation);
        transform.setTranslation(worldTranslation);
//        transform.scale(worldScale);
        if (this.children != null) {
            for (Spatial child : children) {
            	if (child instanceof Bone) {            		
                    ((Bone) child).update();
            	}
            }
        }
    }

    /**
     * copyBindings sets the bone's bone matrix of an incoming skeleton (or
     * subskeleton) to that of the matching bone of this skeleton.
     * 
     * @param newBone
     *            the bone to copy the bindings to.
     */
    public void copyBindings(Bone newBone) {
        Spatial copyTo = null;
        if (getName().equals(newBone.getName())) {
            copyTo = this;
        } else {
            copyTo = getChild(newBone.getName());
        }

        if (copyTo != null && copyTo instanceof Bone) {
            if (!newBone.getBindMatrix().isIdentity()) {
                ((Bone) copyTo).getBindMatrix().set(newBone.getBindMatrix());
            }
        }

        for (int x = 0, max = newBone.getQuantity(); x < max; x++) {
            if (newBone.getChild(x) instanceof Bone) {
                copyBindings((Bone) newBone.getChild(x));
            }
        }
    }

    /**
     * applyBone affects a given vertex by its current world position. This is
     * done by first placing the vertex into the pose position using the bind
     * matrix, then transforming it into world space using the bone's world
     * rotation and world translation.
     * 
     * @param inf
     *            the influence this bone affects the vertex. Including its
     *            offset and weight.
     * @param vstore
     *            the vertex to manipulate.
     * @param nstore
     *            the normal to manipulate.
     */
    public void applyBone(BoneInfluence inf, Vector3f vstore, Vector3f nstore) {    	
        if (!optimizeTransform) {
        	if (inf.vOffset != null) {
        		workVectA.set(inf.vOffset);
        		worldRotation.multLocal(workVectA);
        		workVectA.multLocal(worldScale);
        		workVectA.addLocal(worldTranslation);
        		workVectA.multLocal(inf.weight);
        		vstore.addLocal(workVectA);
        	}

        	if (inf.nOffset != null) {
        		workVectA.set(inf.nOffset);
        		worldRotation.multLocal(workVectA);
        		workVectA.multLocal(inf.weight);
        		nstore.addLocal(workVectA);
        	}
        } else {
            if (inf.vOffset != null) {
                workVectA.set(inf.vOffset);
                transform.rotateVect(workVectA);
                workVectA.multLocal(worldScale);
                transform.translateVect(workVectA);
                workVectA.multLocal(inf.weight);
                vstore.addLocal(workVectA);
            }

            if (inf.nOffset != null) {
                workVectA.set(inf.nOffset);
                transform.rotateVect(workVectA);
                workVectA.multLocal(inf.weight);
                nstore.addLocal(workVectA);
            }        	
        }
    }

    /**
     * retrieves the bind matrix of this bone.
     * 
     * @return the bind matrix of this bone.
     */
    public Matrix4f getBindMatrix() {
        return bindMatrix;
    }

    /**
     * sets the bind matrix of this bone.
     * 
     * @param bindMatrix
     *            the bind matrix of this bone.
     */
    public void setBindMatrix(Matrix4f bindMatrix) {
        this.bindMatrix = bindMatrix;
    }

    public void addController(Controller c) {
        super.addController(c);

        if (c instanceof AnimationController) {
            ((AnimationController) c).setSkeleton(this);
            animationController = (AnimationController) c;

        }
    }

    public void updateGeometricState(float time, boolean initiator) {
        if (parent == null || !(parent instanceof Bone)) {
            resetChangeValues();
        }
        super.updateGeometricState(time, initiator);

    }

    public void resetChangeValues() {
        boneChanged = false;
        for (int i = 0, maxSize = getQuantity(); i < maxSize; i++) {
            if (getChild(i) instanceof Bone) {
                ((Bone) getChild(i)).resetChangeValues();
            }
        }
    }

    @Override
    public void updateWorldVectors() {
        super.updateWorldVectors();

        if (skinRoot) {
            if (boneChanged || hasTransformChanged()) {
                fireBoneChange();
            }
        }
    }

    public AnimationController getAnimationController() {
        return animationController;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);
        cap.write(bindMatrix, "bindMatrix", new Matrix4f());
        cap.write(animationController, "animationController", null);
        cap.write(isHardpoint, "isHardpoint", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);
        bindMatrix = (Matrix4f) cap.readSavable("bindMatrix", new Matrix4f());
        animationController = (AnimationController) cap.readSavable(
                "animationController", null);
        isHardpoint = cap.readBoolean("isHardpoint", false);
    }

    public void revertToBind() {
        worldTranslation.set(bindMatrix.toTranslationVector());
        bindMatrix.toRotationQuat(worldRotation);
        worldRotation.normalize();

        Matrix3f rotMat = bindMatrix.toRotationMatrix();
        worldScale.set(rotMat.getRow(0).length(), rotMat.getRow(1).length(), rotMat.getRow(2).length());
        if (rotMat.determinant() < 0) worldScale.x = -worldScale.x;

        if (children != null) {
            for (Spatial child : children) {
                if (child instanceof Bone)
                    ((Bone) child).revertToBind();
            }
        }
        
        localTranslation.set(worldTranslation);
        localRotation.set(worldRotation);
        localScale.set(worldScale);
    }

    public Bone getRootSkeleton() {
        if (parent instanceof Bone)
            return ((Bone) parent).getRootSkeleton();
        else
            return this;
    }

    public void addBoneListener(BoneChangeListener listener) {
        if (changeListeners == null)
            changeListeners = new ArrayList<BoneChangeListener>();

        changeListeners.add(listener);
        propogateBoneChangeToChildren(true);
        skinRoot = true;
    }

    public void removeBoneListener(BoneChangeListener listener) {
        if (changeListeners == null) {
            return;
        }

        changeListeners.remove(listener);

        propogateBoneChangeToChildren(true);
        if (changeListeners.size() == 0) {
            skinRoot = false;
        }
    }

    protected int getListenerQuantity() {
        if (changeListeners == null)
            return 0;
        else
            return changeListeners.size();
    }

    protected void fireBoneChange() {
        if (changeListeners == null)
            return;
        BoneChangeEvent event = new BoneChangeEvent(this);
        for (int x = getListenerQuantity(); --x >= 0;) {
            changeListeners.get(x).boneChanged(event);
        }
    }

    public boolean isSkinRoot() {
        return skinRoot;
    }

    public void setSkinRoot(boolean skinRoot) {
        this.skinRoot = skinRoot;
    }

    public void propogateBoneChangeToParent(boolean initiator) {
        if (!boneChanged || initiator) {
            boneChanged = true;
            if (parent != null && parent instanceof Bone) {
                ((Bone) parent).propogateBoneChangeToParent(false);
            }
        }
    }

    public void propogateBoneChangeToChildren(boolean initiator) {
        if (!boneChanged || initiator) {
            boneChanged = true;
            Spatial child;
            for (int i = 0, max = getQuantity(); i < max; i++) {
                child = getChild(i);
                if (child instanceof Bone) {
                    ((Bone) child).propogateBoneChangeToChildren(false);
                }
            }
        }
    }

    protected boolean hasTransformChanged() {
        boolean rVal = false;
        if (!oldRot.equals(getWorldRotation())
                || !oldTran.equals(getWorldTranslation())
                || !oldScale.equals(getWorldScale())) {
            rVal = true;

            oldRot.set(getWorldRotation());
            oldTran.set(getWorldTranslation());
            oldScale.set(getWorldScale());
        }

        return rVal;
    }

    public void propogateBoneChange(boolean initiator) {
        propogateBoneChangeToParent(initiator);
        propogateBoneChangeToChildren(initiator);
    }

    public void setLocalRotation(Matrix3f rotation) {
        super.setLocalRotation(rotation);
        propogateBoneChange(true);
    }

    public void setLocalRotation(Quaternion quaternion) {
        super.setLocalRotation(quaternion);
        propogateBoneChange(true);
    }

    public void setLocalTranslation(Vector3f localTranslation) {
        super.setLocalTranslation(localTranslation);
        propogateBoneChange(true);
    }

    public void setLocalScale(Vector3f localScale) {
        super.setLocalScale(localScale);
        propogateBoneChange(true);
    }

    public void setLocalScale(float localScale) {
        super.setLocalScale(localScale);
        propogateBoneChange(true);
    }

    public boolean isHardpoint() {
        return isHardpoint;
    }

    public void setHardpoint(boolean isHardpoint) {
        this.isHardpoint = isHardpoint;
    }
}
