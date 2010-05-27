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
import java.util.Arrays;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.Quaternion;
import com.jme.math.TransformQuaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Started Date: Jul 9, 2004 <br>
 * <br>
 * 
 * This class animates spatials by interpolating between various
 * transformations. The user defines objects to be transformed and what
 * rotation/translation/scale to give each object at various points in time. The
 * user must call interpolateMissing() before using the controller in order to
 * interpolate unspecified translation/rotation/scale.
 * 
 * 
 * @author Jack Lindamood
 * @author Philip Wainwright (bugfixes)
 */
public class SpatialTransformer extends Controller {
    private static final Logger logger = Logger.getLogger(SpatialTransformer.class.getName());

    private static final long serialVersionUID = 1L;

    /** Number of objects this transformer changes. */
    private int numObjects;

    /** Refrences to the objects that will be changed. */
    public Spatial[] toChange;

    /** Used internally by update specifying how to change each object. */
    private TransformQuaternion[] pivots;

    /**
     * parentIndexes[i] states that toChange[i]'s parent is
     * toChange[parentIndex[i]].
     */
    public int[] parentIndexes;

    /** Interpolated array of keyframe states */
    public ArrayList<PointInTime> keyframes;

    /** Current time in the animation */
    private float curTime;

    /** Time previous to curTime */
    private transient PointInTime beginPointTime;

    /** Time after curTime */
    private transient PointInTime endPointTime;

    /** Used internally in update to flag that a pivot has been updated */
    private boolean[] haveChanged;

    private float delta;

    private final static Vector3f unSyncbeginPos = new Vector3f();

    private final static Vector3f unSyncendPos = new Vector3f();

    private final static Quaternion unSyncbeginRot = new Quaternion();

    private final static Quaternion unSyncendRot = new Quaternion();

    public SpatialTransformer() {}
    
    /**
     * Constructs a new SpatialTransformer that will operate on
     * <code>numObjects</code> Spatials
     * 
     * @param numObjects
     *            The number of spatials to change
     */
    public SpatialTransformer(int numObjects) {
        this.numObjects = numObjects;
        toChange = new Spatial[numObjects];
        pivots = new TransformQuaternion[numObjects];
        parentIndexes = new int[numObjects];
        haveChanged = new boolean[numObjects];
        Arrays.fill(parentIndexes, -1);
        for (int i = 0; i < numObjects; i++)
            pivots[i] = new TransformQuaternion();
        keyframes = new ArrayList<PointInTime>();
    }

    public void update(float time) {
        curTime += time * getSpeed();
        setBeginAndEnd();
        Arrays.fill(haveChanged, false);
        delta = endPointTime.time - beginPointTime.time;
        if (delta != 0f) delta = (curTime - beginPointTime.time) / delta;
        for (int i = 0; i < numObjects; i++) {
            updatePivot(i);
            pivots[i].applyToSpatial(toChange[i]);
        }
    }

    /**
     * Called by update, and itself recursivly. Will, when completed, change
     * toChange[objIndex] by pivots[objIndex]
     * 
     * @param objIndex
     *            The index to update.
     */
    private void updatePivot(int objIndex) {
        if (haveChanged[objIndex])
            return;

        haveChanged[objIndex] = true;
        int parentIndex = parentIndexes[objIndex];
        if (parentIndex != -1) {
            updatePivot(parentIndex);
        }
        pivots[objIndex].interpolateTransforms(beginPointTime.look[objIndex],
                endPointTime.look[objIndex], delta);
        if (parentIndex != -1)
                pivots[objIndex].combineWithParent(pivots[parentIndex]);
    }

    /**
     * overridden by SpatialTransformer to always set a time inside the first
     * and the last keyframe's time in the animation.
     * @author Kai Rabien (hevee)
     */
    public void setMinTime(float minTime) {
        if(keyframes != null &&  keyframes.size() > 0){
            float firstFrame = keyframes.get(0).time;
            float lastFrame = keyframes.get(keyframes.size() - 1).time;
            if(minTime < firstFrame) minTime = firstFrame;
            if(minTime > lastFrame) minTime = lastFrame;
        }

        curTime = minTime;
        super.setMinTime(minTime);
    }

    /**
     * overridden by SpatialTransformer to always set a time inside the first
     * and the last keyframe's time in the animation
     * @author Kai Rabien (hevee)
     */
    public void setMaxTime(float maxTime) {
        if(keyframes != null &&  keyframes.size() > 0){
            float firstFrame = keyframes.get(0).time;
            float lastFrame = keyframes.get(keyframes.size() - 1).time;
            if(maxTime < firstFrame) maxTime = firstFrame;
            if(maxTime > lastFrame) maxTime = lastFrame;
        }
        super.setMaxTime(maxTime);
    }

    /**
     * Sets the new animation boundaries for this controller. This will start at
     * newBeginTime and proceed in the direction of newEndTime (either forwards
     * or backwards). If both are the same, then the animation is set to their
     * time and turned off, otherwise the animation is turned on to start the
     * animation acording to the repeat type. If either BeginTime or EndTime are
     * invalid times (less than 0 or greater than the maximum set keyframe time)
     * then a warning is set and nothing happens. <br>
     * It is suggested that this function be called if new animation boundaries
     * need to be set, instead of setMinTime and setMaxTime directly.
     *
     * @param newBeginTime
     *            The starting time
     * @param newEndTime
     *            The ending time
     */
    public void setNewAnimationTimes(float newBeginTime, float newEndTime) {
        if (newBeginTime < 0
                || newBeginTime > keyframes
                        .get(keyframes.size() - 1).time) {
            logger.log(Level.WARNING, "Attempt to set invalid begintime:{0}", newBeginTime);
            return;
        }
        if (newEndTime < 0
                || newEndTime > keyframes
                        .get(keyframes.size() - 1).time) {
            logger.log(Level.WARNING, "Attempt to set invalid endtime:{0}", newEndTime);
            return;
        }
        setMinTime(newBeginTime);
        setMaxTime(newEndTime);
        setActive(true);
        if (newBeginTime <= newEndTime) { // Moving forward
            curTime = newBeginTime;
            if (newBeginTime == newEndTime) {
                update(0);
                setActive(false);
            }
        } else { // Moving backwards
            curTime = newEndTime;
        }
    }

    /**
     * Gets the current time in the animation
     */
    public float getCurTime(){return curTime;}

    /**
     * Sets the current time in the animation
     * @param time
     *            The time this Controller should continue at
     */
    public void setCurTime(float time){ curTime = time;}

    /**
     * Called in update for calculating the correct beginPointTime and
     * endPointTime, and changing curTime if neccessary.
     * @author Kai Rabien (hevee)
     */
    private void setBeginAndEnd() {
        float minTime = getMinTime();
        float maxTime = getMaxTime();

        if(getSpeed() > 0){
            if(curTime >= maxTime){
                if(getRepeatType() == RT_WRAP){
                    int[] is = findIndicesBeforeAfter(minTime);
                    int beginIndex = is[0];
                    int endIndex = is[1];
                    beginPointTime = keyframes.get(beginIndex);
                    endPointTime = keyframes.get(endIndex);
                    float overshoot = curTime - maxTime;
                    curTime = minTime + overshoot;
                } else if (getRepeatType() == RT_CLAMP){
                    int[] is = findIndicesBeforeAfter(maxTime);
                    int beginIndex = is[1];
                    beginPointTime = keyframes.get(beginIndex);
                    endPointTime = beginPointTime;
                    curTime = maxTime;
                } else if(getRepeatType() == RT_CYCLE){
                    int[] is = findIndicesBeforeAfter(maxTime);
                    int beginIndex = is[0];
                    int endIndex = is[1];
                    beginPointTime = keyframes.get(beginIndex);
                    endPointTime = keyframes.get(endIndex);
                    float overshoot = curTime - maxTime;
                    curTime = maxTime - overshoot;
                    setSpeed(- getSpeed());
                }
            } else if(curTime <= minTime){
                int[] is = findIndicesBeforeAfter(minTime);
                int beginIndex = is[0];
                int endIndex = is[1];
                beginPointTime = keyframes.get(beginIndex);
                endPointTime = keyframes.get(endIndex);
                curTime = minTime;
            } else{//curTime is inside minTime and maxTime
                int[] is = findIndicesBeforeAfter(curTime);
                int beginIndex = is[0];
                int endIndex = is[1];
                beginPointTime = keyframes.get(beginIndex);
                endPointTime = keyframes.get(endIndex);
            }
        } else if(getSpeed() < 0){
            if(curTime <= minTime){
                if(getRepeatType() == RT_WRAP){
                    int[] is = findIndicesBeforeAfter(maxTime);
                    int beginIndex = is[1];
                    int endIndex = is[0];
                    beginPointTime = keyframes.get(beginIndex);
                    endPointTime = keyframes.get(endIndex);
                    float overshoot = minTime - curTime;
                    curTime = maxTime - overshoot;
                } else if (getRepeatType() == RT_CLAMP){
                    int[] is = findIndicesBeforeAfter(minTime);
                    int beginIndex = is[1];
                    beginPointTime = keyframes.get(beginIndex);
                    endPointTime = beginPointTime;
                    curTime = minTime;
                } else if(getRepeatType() == RT_CYCLE){
                    int[] is = findIndicesBeforeAfter(minTime);
                    int beginIndex = is[1];
                    int endIndex = is[0];
                    beginPointTime = keyframes.get(beginIndex);
                    endPointTime = keyframes.get(endIndex);
                    float overshoot = minTime - curTime;
                    curTime = minTime + overshoot;
                    setSpeed(- getSpeed());
                }
            } else if(curTime >= maxTime){
                int[] is = findIndicesBeforeAfter(maxTime);
                int beginIndex = is[1];
                int endIndex = is[0];
                beginPointTime = keyframes.get(beginIndex);
                endPointTime = keyframes.get(endIndex);
                curTime = maxTime;
            } else{//curTime is inside minTime and maxTime
                int[] is = findIndicesBeforeAfter(curTime);
                int beginIndex = is[1];
                int endIndex = is[0];
                beginPointTime = keyframes.get(beginIndex);
                endPointTime = keyframes.get(endIndex);
            }
        } else {
        	beginPointTime = keyframes.get(0);
            endPointTime = keyframes.get(0);
        }
    }

    /**
     * Finds indices i in keyframes such that <code>
     * keyframes.get(i[0]).time < giventime <= keyframes.get(i[1]).time </code>
     * if no keyframe was found before or after <code>giventime</code>, the
     * corresponding value will clamp to <code>0</code> resp.
     * <code>keyframes.size() - 1</code>
     * @author Kai Rabien (hevee)
     */
    int[] findIndicesBeforeAfter(float giventime){
        int[] ret =  new int[]{0, keyframes.size() - 1};
        for (int i = 0; i < keyframes.size(); i++){
            float curFrameTime = keyframes.get(i).time;
            if (curFrameTime >= giventime) {
                ret[1] = i;
                return ret;
            }
            ret[0] = i;
        }
        return ret;
    }

    /**
     * Sets an object to animate. The object's index is <code>index</code> and
     * it's parent index is <code>parentIndex</code>. A parent index of -1
     * indicates it has no parent.
     *
     * @param objChange
     *            The spatial that will be updated by this SpatialTransformer.
     * @param index
     *            The index of that spatial in this transformer's array
     * @param parentIndex
     *            The parentIndex in this transformer's array for this Spatial
     */

    public void setObject(Spatial objChange, int index, int parentIndex) {
        toChange[index] = objChange;
        pivots[index].setTranslation(objChange.getLocalTranslation());
        pivots[index].setScale(objChange.getLocalScale());
        pivots[index].setRotationQuaternion(objChange.getLocalRotation());
        parentIndexes[index] = parentIndex;
    }

    /**
     * Returns the keyframe for <code>time</code>. If one doens't exist, a
     * new one is created, and <code>getMaxTime()</code> will be
     * set to <code>Math.max(time, getMaxTime())</code>.
     * @param time
     *            The time to look for.
     * @return The keyframe refrencing <code>time</code>.
     */
    private PointInTime findTime(float time) {
        for (int i = 0; i < keyframes.size(); i++) {
            if (keyframes.get(i).time == time){
                setMinTime(Math.min(time, getMinTime()));
                setMaxTime(Math.max(time, getMaxTime()));
                return keyframes.get(i);
            }
            if (keyframes.get(i).time > time) {
                PointInTime t = new PointInTime(time, numObjects);
                keyframes.add(i, t);
                setMinTime(Math.min(time, getMinTime()));
                setMaxTime(Math.max(time, getMaxTime()));
                return t;
            }
        }
        PointInTime t = new PointInTime(time, numObjects);
        keyframes.add(t);
        setMinTime(Math.min(time, getMinTime()));
        setMaxTime(Math.max(time, getMaxTime()));
        return t;
    }

    /**
     * Sets object with index <code>indexInST</code> to rotate by
     * <code>rot</code> at time <code>time</code>.
     *
     * @param indexInST
     *            The index of the spatial to change
     * @param time
     *            The time for the spatial to take this rotation
     * @param rot
     *            The rotation to take
     */
    public void setRotation(int indexInST, float time, Quaternion rot) {
        PointInTime toAdd = findTime(time);
        toAdd.setRotation(indexInST, rot);
    }

    /**
     * Sets object with index <code>indexInST</code> to translate by
     * <code>position</code> at time <code>time</code>.
     *
     * @param indexInST
     *            The index of the spatial to change
     * @param time
     *            The time for the spatial to take this translation
     * @param position
     *            The position to take
     */
    public void setPosition(int indexInST, float time, Vector3f position) {
        PointInTime toAdd = findTime(time);
        toAdd.setTranslation(indexInST, position);
    }

    /**
     * Sets object with index <code>indexInST</code> to scale by
     * <code>scale</code> at time <code>time</code>.
     *
     * @param indexInST
     *            The index of the spatial to change
     * @param time
     *            The time for the spatial to take this scale
     * @param scale
     *            The scale to take
     */
    public void setScale(int indexInST, float time, Vector3f scale) {
        PointInTime toAdd = findTime(time);
        toAdd.setScale(indexInST, scale);
    }

    /**
     * This must be called one time, once all translations/rotations/scales have
     * been set. It will interpolate unset values to make the animation look
     * correct. Tail and head values are assumed to be the identity.
     */
    public void interpolateMissing() {
        if (keyframes.size() != 1) {
            fillTrans();
            fillRots();
            fillScales();
        }
        for (int objIndex = 0; objIndex < numObjects; objIndex++)
            pivots[objIndex].applyToSpatial(toChange[objIndex]);
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing scale values.
     */
    private void fillScales() {
        for (int objIndex = 0; objIndex < numObjects; objIndex++) {
            // 1) Find first non-null scale of objIndex <code>objIndex</code>
            int start;
            for (start = 0; start < keyframes.size(); start++) {
                if (keyframes.get(start).usedScale
                        .get(objIndex)) break;
            }
            if (start == keyframes.size()) { // if they are all null then fill
                // with identity
                for ( PointInTime keyframe : keyframes ) {
                    pivots[objIndex].getScale( // pull original translation
                            keyframe.look[objIndex]
                                    .getScale() ); // ...into object translation.
                }
                continue; // we're done so lets break
            }

            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                keyframes.get(start).look[objIndex]
                        .getScale(unSyncbeginPos);
                for (int i = 0; i < start; i++)
                    keyframes.get(i).look[objIndex]
                            .setScale(unSyncbeginPos);
            }
            int lastgood = start;
            for (int i = start + 1; i < keyframes.size(); i++) {
                if (keyframes.get(i).usedScale.get(objIndex)) {
                    fillScale(objIndex, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            if (lastgood != keyframes.size() - 1) { // Make last ones equal to
                // last good
                keyframes.get(keyframes.size() - 1).look[objIndex]
                        .setScale(keyframes.get(lastgood).look[objIndex]
                                .getScale(null));
            }
            keyframes.get(lastgood).look[objIndex]
                    .getScale(unSyncbeginPos);

            for (int i = lastgood + 1; i < keyframes.size(); i++) {
                keyframes.get(i).look[objIndex]
                        .setScale(unSyncbeginPos);
            }
        }
    }

    /**
     * Interpolates unspecified scale values for objectIndex from start to end.
     *
     * @param objectIndex
     *            Index to interpolate.
     * @param startScaleIndex
     *            Starting scale index.
     * @param endScaleIndex
     *            Ending scale index.
     */
    private void fillScale(int objectIndex, int startScaleIndex,
            int endScaleIndex) {
        keyframes.get(startScaleIndex).look[objectIndex]
                .getScale(unSyncbeginPos);
        keyframes.get(endScaleIndex).look[objectIndex]
                .getScale(unSyncendPos);
        float startTime = keyframes.get(startScaleIndex).time;
        float endTime = keyframes.get(endScaleIndex).time;
        float delta = endTime - startTime;
        Vector3f tempVec = new Vector3f();

        for (int i = startScaleIndex + 1; i < endScaleIndex; i++) {
            float thisTime = keyframes.get(i).time;
            tempVec.interpolate(unSyncbeginPos, unSyncendPos,
                    (thisTime - startTime) / delta);
            keyframes.get(i).look[objectIndex]
                    .setScale(tempVec);
        }
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing rotation
     * values.
     */
    private void fillRots() {
        for (int joint = 0; joint < numObjects; joint++) {
            // 1) Find first non-null rotation of joint <code>joint</code>
            int start;
            for (start = 0; start < keyframes.size(); start++) {
                if (keyframes.get(start).usedRot.get(joint))
                        break;
            }
            if (start == keyframes.size()) { // if they are all null then fill
                // with identity
                for ( PointInTime keyframe : keyframes ) {
                    pivots[joint].getRotation( // pull original rotation
                            keyframe.look[joint]
                                    .getRotation() ); // ...into object rotation.
                }

                continue; // we're done so lets break
            }
            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null

                keyframes.get(start).look[joint]
                        .getRotation(unSyncbeginRot);
                for (int i = 0; i < start; i++)
                    keyframes.get(i).look[joint]
                            .setRotationQuaternion(unSyncbeginRot);
            }
            int lastgood = start;
            for (int i = start + 1; i < keyframes.size(); i++) {
                if (keyframes.get(i).usedRot.get(joint)) {
                    fillQuats(joint, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            //            fillQuats(joint,lastgood,keyframes.size()-1); // fills tail
            keyframes.get(lastgood).look[joint]
                    .getRotation(unSyncbeginRot);

            for (int i = lastgood + 1; i < keyframes.size(); i++) {
                keyframes.get(i).look[joint]
                        .setRotationQuaternion(unSyncbeginRot);
            }
        }
    }

    /**
     * Interpolates unspecified rot values for objectIndex from start to end.
     *
     * @param objectIndex
     *            Index to interpolate.
     * @param startRotIndex
     *            Starting rot index.
     * @param endRotIndex
     *            Ending rot index.
     */
    private void fillQuats(int objectIndex, int startRotIndex, int endRotIndex) {
        keyframes.get(startRotIndex).look[objectIndex]
                .getRotation(unSyncbeginRot);
        keyframes.get(endRotIndex).look[objectIndex]
                .getRotation(unSyncendRot);
        float startTime = keyframes.get(startRotIndex).time;
        float endTime = keyframes.get(endRotIndex).time;
        float delta = endTime - startTime;
        Quaternion tempQuat = new Quaternion();

        for (int i = startRotIndex + 1; i < endRotIndex; i++) {
            float thisTime = keyframes.get(i).time;
            tempQuat.slerp(unSyncbeginRot, unSyncendRot, (thisTime - startTime)
                    / delta);
            keyframes.get(i).look[objectIndex]
                    .setRotationQuaternion(tempQuat);
        }
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing translation
     * values.
     */
    private void fillTrans() {
        for (int objIndex = 0; objIndex < numObjects; objIndex++) {
            // 1) Find first non-null translation of objIndex
            // <code>objIndex</code>
            int start;
            for (start = 0; start < keyframes.size(); start++) {
                if (keyframes.get(start).usedTrans
                        .get(objIndex)) break;
            }
            if (start == keyframes.size()) { // if they are all null then fill
                // with identity
                for ( PointInTime keyframe : keyframes ) {
                    pivots[objIndex].getTranslation( // pull original translation
                            keyframe.look[objIndex]
                                    .getTranslation() ); // ...into object translation.
                }
                continue; // we're done so lets break
            }

            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                keyframes.get(start).look[objIndex]
                        .getTranslation(unSyncbeginPos);
                for (int i = 0; i < start; i++)
                    keyframes.get(i).look[objIndex]
                            .setTranslation(unSyncbeginPos);
            }
            int lastgood = start;
            for (int i = start + 1; i < keyframes.size(); i++) {
                if (keyframes.get(i).usedTrans.get(objIndex)) {
                    fillVecs(objIndex, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            if (lastgood != keyframes.size() - 1) { // Make last ones equal to
                // last good
                keyframes.get(keyframes.size() - 1).look[objIndex]
                        .setTranslation(keyframes.get(lastgood).look[objIndex]
                                .getTranslation(null));
            }
            keyframes.get(lastgood).look[objIndex]
                    .getTranslation(unSyncbeginPos);

            for (int i = lastgood + 1; i < keyframes.size(); i++) {
                keyframes.get(i).look[objIndex]
                        .setTranslation(unSyncbeginPos);
            }
        }
    }

    /**
     * Interpolates unspecified translation values for objectIndex from start to
     * end.
     *
     * @param objectIndex
     *            Index to interpolate.
     * @param startPosIndex
     *            Starting translation index.
     * @param endPosIndex
     *            Ending translation index.
     */
    private void fillVecs(int objectIndex, int startPosIndex, int endPosIndex) {
        keyframes.get(startPosIndex).look[objectIndex]
                .getTranslation(unSyncbeginPos);
        keyframes.get(endPosIndex).look[objectIndex]
                .getTranslation(unSyncendPos);
        float startTime = keyframes.get(startPosIndex).time;
        float endTime = keyframes.get(endPosIndex).time;
        float delta = endTime - startTime;
        Vector3f tempVec = new Vector3f();

        for (int i = startPosIndex + 1; i < endPosIndex; i++) {
            float thisTime = keyframes.get(i).time;
            tempVec.interpolate(unSyncbeginPos, unSyncendPos,
                    (thisTime - startTime) / delta);
            keyframes.get(i).look[objectIndex]
                    .setTranslation(tempVec);
        }
    }

    /**
     * Returns the number of Objects used by this SpatialTransformer
     * 
     * @return The number of objects.
     */
    public int getNumObjects() {
        return numObjects;
    }

    /**
     * Defines a point in time where at time <code>time</code>, ohject
     * <code>toChange[i]</code> will assume transformation
     * <code>look[i]</code>. BitSet's used* specify if the transformation
     * value was specified by the user, or interpolated
     */
    public static class PointInTime implements Savable {

        /** Bit i is true if look[i].rotation was user defined. */
        public BitSet usedRot;

        /** Bit i is true if look[i].translation was user defined. */
        public BitSet usedTrans;

        /** Bit i is true if look[i].scale was user defined. */
        public BitSet usedScale;

        /** The time of this TransformationMatrix. */
        public float time;

        /** toChange[i] looks like look[i] at time. */
        public TransformQuaternion[] look;

        public PointInTime() {}
        
        /**
         * Constructs a new PointInTime with the time <code>time</code>
         * 
         * @param time
         *            The the for this PointInTime.
         */
        public PointInTime(float time, int numObjects) {
            look = new TransformQuaternion[numObjects];
            usedRot = new BitSet(numObjects);
            usedTrans = new BitSet(numObjects);
            usedScale = new BitSet(numObjects);
            for (int i = 0; i < look.length; i++)
                look[i] = new TransformQuaternion();
            this.time = time;
        }

        /**
         * Sets the rotation for objIndex and sets usedRot to true for that
         * index
         * 
         * @param objIndex
         *            The object to take the rotation at this point in time.
         * @param rot
         *            The rotation to take.
         */
        void setRotation(int objIndex, Quaternion rot) {
            look[objIndex].setRotationQuaternion(rot);
            usedRot.set(objIndex);
        }

        /**
         * Sets the translation for objIndex and sets usedTrans to true for that
         * index
         * 
         * @param objIndex
         *            The object to take the translation at this point in time.
         * @param trans
         *            The translation to take.
         */
        void setTranslation(int objIndex, Vector3f trans) {
            look[objIndex].setTranslation(trans);
            usedTrans.set(objIndex);
        }

        /**
         * Sets the scale for objIndex and sets usedScale to true for that index
         * 
         * @param objIndex
         *            The object to take the scale at this point in time.
         * @param scale
         *            The scale to take.
         */
        void setScale(int objIndex, Vector3f scale) {
            look[objIndex].setScale(scale);
            usedScale.set(objIndex);
        }

        public void write(JMEExporter e) throws IOException {
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(usedRot, "usedRot", null);
            capsule.write(usedTrans, "usedTrans", null);
            capsule.write(usedScale, "usedScale", null);
            capsule.write(time, "time", 0);
            capsule.write(look, "look", null);
            
        }

        public void read(JMEImporter e) throws IOException {
            InputCapsule capsule = e.getCapsule(this);
            usedRot = capsule.readBitSet("usedRot", null);
            usedTrans = capsule.readBitSet("usedTrans", null);
            usedScale = capsule.readBitSet("usedScale", null);
            time = capsule.readFloat("time", 0);
            
            Savable[] savs = capsule.readSavableArray("look", null);
            if (savs == null) {
                look = null;
            } else {
                look = new TransformQuaternion[savs.length];
                for (int x = 0; x < savs.length; x++) {
                    look[x] = (TransformQuaternion)savs[x];
                }
            }
        }
        
        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        
        capsule.write(numObjects, "numObjects", 0);
        capsule.write(toChange, "toChange", new Spatial[0]);
        capsule.write(pivots, "pivots", new TransformQuaternion[0]);
        capsule.write(parentIndexes, "parentIndexes", new int[0]);
        capsule.writeSavableArrayList(keyframes, "keyframes", new ArrayList());
        capsule.write(haveChanged, "haveChanged", new boolean[0]);
        capsule.write(beginPointTime, "beginPointTime", null);
        capsule.write(endPointTime, "endPointTime", null);
    }

    @SuppressWarnings("unchecked")
	public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        
        numObjects = capsule.readInt("numObjects", 0);
        
        Savable[] savs = capsule.readSavableArray("toChange", new Spatial[0]);
        if (savs == null) {
            toChange = null;
        } else {
            toChange = new Spatial[savs.length];
            for (int x = 0; x < savs.length; x++) {
                toChange[x] = (Spatial)savs[x];
            }
        }
        
        savs = capsule.readSavableArray("pivots", new TransformQuaternion[0]);
        if (savs == null) {
            pivots = null;
        } else {
            pivots = new TransformQuaternion[savs.length];
            for (int x = 0; x < savs.length; x++) {
                pivots[x] = (TransformQuaternion)savs[x];
            }
        }
        
        parentIndexes = capsule.readIntArray("parentIndexes", new int[0]);
        keyframes = capsule.readSavableArrayList("keyframes", new ArrayList());
        haveChanged = capsule.readBooleanArray("haveChanged", new boolean[0]);
        
        beginPointTime = (PointInTime)capsule.readSavable("beginPointTime", null);
        endPointTime = (PointInTime)capsule.readSavable("endPointTime", null);
    }
}
