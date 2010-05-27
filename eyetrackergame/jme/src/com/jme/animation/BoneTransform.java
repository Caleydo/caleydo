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
import java.io.Serializable;
import java.util.logging.Logger;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * BoneTransform contains a Bone/Transform array pairing. This pairing defines
 * the bone that will be transformed (translate, rotate), and the
 * transformations for a given frame. The bone is updated during a call to the
 * update method that defines two keyframes and the interpolation value between
 * them.
 */
public class BoneTransform implements Serializable, Savable {
    private static final Logger logger = Logger.getLogger(BoneTransform.class
            .getName());

    private static final long serialVersionUID = -6037680427670917355L;

    private Quaternion[] rotations;
    private Vector3f[] translations;
    private Bone bone;
    private String boneId;

    private static Quaternion tempQuat1 = new Quaternion();
    private static Vector3f tempVec1 = new Vector3f();

    /**
     * Default constructor creates a new BoneTransform with no data set.
     */
    public BoneTransform() {

    }

    /**
     * Constructor defines the bone that will be transformed as well as how many
     * transform keyframes that exist. These keyframes are not set until setXXXX
     * is called.
     * 
     * @param bone
     *            the bone to transform.
     * @param frames
     *            the number of keyframes for this animation.
     */
    public BoneTransform(Bone bone, int frames) {
        this.bone = bone;
        rotations = new Quaternion[frames];
        translations = new Vector3f[frames];
    }

    /**
     * Constructor defines the bone and the list of transforms to use. This
     * constructor builds a complete BoneTransform ready for use.
     * 
     * @param bone
     *            the bone to transform.
     * @param transforms
     *            the transforms to use.
     */
    public BoneTransform(Bone bone, Matrix4f[] transforms) {
        this.bone = bone;
        setTransforms(transforms);
    }

    /**
     * setCurrentFrame will set the current frame from the bone. The frame
     * supplied will define how to transform the bone. It is the responsibility
     * of the caller to insure the frame supplied is valid.
     * 
     * @param frame
     *            the frame to set the bone's transform to.
     */
    public void setCurrentFrame(int frame) {
        setCurrentFrame(frame, NOBLEND, null, null, 0, null);
    }
    
    public void setCurrentFrame(int frame, float blend) {
        setCurrentFrame(frame, blend, null, null, 0, null);
    }

    /**
     * setCurrentFrame will set the current frame from the bone. The frame
     * supplied will define how to transform the bone. It is the responsibility
     * of the caller to insure the frame supplied is valid.
     * 
     * @param frame
     *            the frame to set the bone's transform to.
     */
    public void setCurrentFrame(int frame, float blend, Bone source, Spatial destination, float diffModifier, AnimationProperties props) {
        if (bone == null) return;
        if (props != null && bone == source && destination != null) {
            tempVec1.set(translations[frame]);
            if (frame != 0) {
                tempVec1.subtractLocal(translations[frame - 1]);
            }
            if (props.isLockX()) {
                tempVec1.x = 0;
            }

            if (props.isLockY()) {
                tempVec1.y = 0;
            }

            if (props.isLockZ()) {
                tempVec1.z = 0;
            }
        }
        if (blend == NOBLEND) {
            if (props != null && bone == source && destination != null) {
                destination.getLocalTranslation().addLocal(
                        tempVec1.divide(diffModifier));

                if (props.isLockX() || props.isLockY() || props.isLockZ()) {
                    bone.getLocalTranslation().set(translations[frame]);
                    
                    if(!props.isLockX()) {
                        bone.getLocalTranslation().x = 0;
                    }
                    
                    if(!props.isLockY()) {
                        bone.getLocalTranslation().y = 0;
                    }
                    
                    if(!props.isLockZ()) {
                        logger.info("LOCK Z");
                        bone.getLocalTranslation().z = 0;
                    }
                }
                
                bone.getLocalRotation().set(rotations[frame]);
                bone.propogateBoneChange(true);
            } else {
                bone.getLocalRotation().set(rotations[frame]);
                bone.getLocalTranslation().set(translations[frame]);
                bone.propogateBoneChange(true);
            }
        } else {
            if (props != null && bone == source && destination != null) {
                tempVec1.divideLocal(diffModifier);
                tempVec1.addLocal(destination.getLocalTranslation());
                destination.getLocalTranslation().interpolate(tempVec1, blend);
                
                bone.getLocalRotation().slerp(rotations[frame], blend);
                bone.propogateBoneChange(true);

            } else {
                bone.getLocalRotation().slerp(rotations[frame], blend);
                bone.getLocalTranslation().interpolate(translations[frame],
                        blend);
                bone.propogateBoneChange(true);
            }
        }
    }

    static public final float NOBLEND = -1f;

    /**
     * Convenience wrapper for the main update method, with no Blending.
     *
     * @see #update(int, int, int, float, float)
     */
    public void update(int prevFrame, int currentFrame, int interpType,
            float time) {
        update(prevFrame, currentFrame, interpType, time, NOBLEND);
    }

    /**
     * update sets the transform of the bone to a given interpolation between
     * two given frames.
     * 
     * @param prevFrame
     *            the initial frame.
     * @param currentFrame
     *            the goal frame.
     * @param interpType
     *            the type of interpolation
     * @param time
     *            the time between frames
     */
    public void update(int prevFrame, int currentFrame, int interpType,
            float time, float blend) {
        if (bone == null) return;
        interpolateRotation(rotations[prevFrame], rotations[currentFrame],
                interpType, time,
                (blend == NOBLEND) ? bone.getLocalRotation() : tempQuat1);
        interpolateTranslation(translations[prevFrame],
                translations[currentFrame], interpType, time,
                (blend == NOBLEND) ? bone.getLocalTranslation() : tempVec1);

        if (blend != NOBLEND) {
            bone.getLocalRotation().slerp(tempQuat1, blend);
            bone.getLocalTranslation().interpolate(tempVec1, blend);
        }
        bone.propogateBoneChange(true);
    }

    /**
     * setTransforms sets a transform for a given frame. It is the
     * responsibility of the caller to insure that the index is valid.
     * 
     * @param index
     *            the index of the transform to set.
     * @param transform
     *            the transform to set at the index.
     */
    public void setTransform(int index, Matrix4f transform) {
        if (rotations[index] == null)
            rotations[index] = new Quaternion();
        transform.toRotationQuat(rotations[index]);
        rotations[index].normalize();

        if (translations[index] == null)
            translations[index] = new Vector3f();
        transform.toTranslationVector(translations[index]);
    }

    /**
     * setRotation sets a rotation for a given frame. It is the responsibility
     * of the caller to insure that the index is valid.
     * 
     * @param index
     *            the index of the rotation to set.
     * @param rotation
     *            the rotation to set at the index.
     */
    public void setRotation(int index, Quaternion rotation) {
        rotations[index] = rotation;
    }

    /**
     * setTranslation sets a translation for a given frame. It is the
     * responsibility of the caller to insure that the index is valid.
     * 
     * @param index
     *            the index of the translation to set.
     * @param translation
     *            the translation to set at the index.
     */
    public void setTranslation(int index, Vector3f translation) {
        translations[index] = translation;
    }

    /**
     * sets the rotations array for the keyframes. This array should be the same
     * size as the times array and the types array. This is left to the user to
     * insure, if they are not the same, an ArrayIndexOutOfBounds exception will
     * be thrown during update.
     * 
     * @param rotations
     *            the rotations to set.
     */
    public void setRotations(Quaternion[] rotations) {
        this.rotations = rotations;
    }

    /**
     * sets the translations array for the keyframes. This array should be the
     * same size as the times array and the types array. This is left to the
     * user to insure, if they are not the same, an ArrayIndexOutOfBounds
     * exception will be thrown during update.
     * 
     * @param translations
     *            the translations to set.
     */
    public void setTranslations(Vector3f[] translations) {
        this.translations = translations;
    }

    /**
     * sets the transforms array for the keyframes. This array should be the
     * same size as the times array and the types array. This is left to the
     * user to insure, if they are not the same, an ArrayIndexOutOfBounds
     * exception will be thrown during update.
     * 
     * @param transforms
     *            the transforms to set.
     */
    public void setTransforms(Matrix4f[] transforms) {
        if (rotations == null || rotations.length != transforms.length)
            rotations = new Quaternion[transforms.length];
        if (translations == null || translations.length != transforms.length)
            translations = new Vector3f[transforms.length];

        for (int i = 0; i < transforms.length; i++) {
            setTransform(i, transforms[i]);
        }
    }

    /**
     * defines the bone that the controller will be affecting.
     * 
     * @param b
     *            the bone that will be controlled.
     */
    public void setBone(Bone b) {
        bone = b;
    }

    /**
     * interpolates two quaternions based on a given time.
     */
    private void interpolateRotation(Quaternion start, Quaternion end,
            int type, float time, Quaternion store) {
        // if interpolation type is not supported, do nothing
        if (type == BoneAnimation.LINEAR) {
            store.slerp(start, end, time);
        }
    }

    /**
     * interpolates two vectors based on a given time.
     */
    private void interpolateTranslation(Vector3f start, Vector3f end, int type,
            float time, Vector3f store) {
        // if interpolation type is not supported, do nothing
        if (type == BoneAnimation.LINEAR) {
            store.set(start).multLocal(1 - time).addLocal(end.x * time,
                    end.y * time, end.z * time);
        }
    }

    /**
     * returns the bone that this BoneTransform is responsible for updating.
     * 
     * @return the bone this BoneTransform is responsible for updating.
     */
    public Bone getBone() {
        return bone;
    }

    /**
     * @return the rotations array this BoneTransform is reponsible for applying
     *         to the bone.
     */
    public Quaternion[] getRotations() {
        return rotations;
    }

    /**
     * @return the translations array this BoneTransform is reponsible for
     *         applying to the bone.
     */
    public Vector3f[] getTranslations() {
        return translations;
    }

    public String getBoneId() {
        return boneId;
    }

    public void setBoneId(String boneId) {
        this.boneId = boneId;
    }

    public boolean findBone(Bone b) {
        if (boneId == null) {
            return false;
        }

        if (boneId.equals(b.getName())) {
            bone = b;
            return true;
        } else {
            for (int i = 0; i < b.getQuantity(); i++) {
                if (b.getChild(i) instanceof Bone) {
                    if (this.findBone((Bone) b.getChild(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule cap = e.getCapsule(this);
        cap.write(boneId, "name", null);
        cap.write(rotations, "rotations", null);
        cap.write(translations, "translations", null);
        cap.write(bone, "bone", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule cap = e.getCapsule(this);
        boneId = cap.readString("name", null);

        Savable[] savs = cap.readSavableArray("rotations", null);
        if (savs == null) {
            rotations = null;
        } else {
            rotations = new Quaternion[savs.length];
            for (int x = 0; x < savs.length; x++) {
                rotations[x] = (Quaternion) savs[x];
                // if we're the same as last one, just use it.
                if (x != 0 && rotations[x].equals(rotations[x-1])) {
                    rotations[x] = rotations[x-1];
                }
            }
        }

        savs = cap.readSavableArray("translations", null);
        if (savs == null) {
            translations = null;
        } else {
            translations = new Vector3f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                translations[x] = (Vector3f) savs[x];
                // if we're the same as last one, just use it.
                if (x != 0 && translations[x].equals(translations[x-1])) {
                    translations[x] = translations[x-1];
                }
            }
        }

        savs = cap.readSavableArray("transforms", null);
        if (savs != null) {
            Matrix4f[] transforms = new Matrix4f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                transforms[x] = (Matrix4f) savs[x];
            }
            setTransforms(transforms);
        }

        bone = (Bone) cap.readSavable("bone", null);
    }

    public Class getClassTag() {
        return this.getClass();
    }
}
