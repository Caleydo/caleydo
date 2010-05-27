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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.export.StringIntMap;

/**
 * BoneAnimation defines a component that manipulates the position of a
 * skeletal system based on a collection of keyframes.
 * In the simplest case, BoneAnimation directly affects a single
 * bone, and the skeletal system is a tree (skeleton) of these BoneAnimations.
 * The BoneAnimation is defined with an array of keyframe times and a
 * BoneTransform for each bone directly controlled.
 * The animations can have a heirarchical composition, and at any level the
 * animation may not control a bone, but simply control sub-animations.
 *
 * In a typical application, the skeletal tree described above is collapsed
 * down so that only one BoneAnimation object is used at display-time.
 *
 * Though it implements no interface, the intention is that an update method
 * of this class should be called by a Controller.
 *
 * This class takes per-frame data specifying the interpolation type, but
 * nobody has ever undertaken the effort to make this class work that way.
 *
 * @see #optimize(boolean)
 * @see BoneTransform
 * @see Controller
 * @see #update(float, int, float, float)
 * @author mpowell
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class BoneAnimation implements Serializable, Savable {
    private static final Logger logger =
            Logger.getLogger(BoneAnimation.class.getName());

    private static final float DEFAULT_RATE = 1f / 20f;

    public static final int LINEAR = 0;

    public static final int BEZIER = 1;

    private static final long serialVersionUID = 1L;

    // values defining how the controller will interact with the bone
    private String name;

    private float[] keyframeTime = null;

    private int[] interpolationType = null;

    private ArrayList<BoneTransform> boneTransforms;

    // values defining start and end frames, and where we currently are
    // in the animation.
    private float currentTime;

    private int currentFrame = 1;

    private int prevFrame = 0;
    // prevFrame is not the previous frame we visited, but the FROM frame
    // of TO-FROM interpolations.

    private int endFrame;

    private int startFrame;

    private int lastEventFrame;

    private float interpolationRate = DEFAULT_RATE;

    private float lastTime;

    volatile private int cycleMode = 1;

    // children animations of this animation
    private ArrayList<BoneAnimation> children;

    private HashMap<String, int[]> syncTags;

    // used if this animation allows the root bone to update the translation of
    // the model node.
    private Bone sourceBone;
    private Spatial destSpatial;
    private AnimationProperties props;


    public BoneAnimation() {
    }

    /**
     * Contructor creates a new animation. The name is the name of the
     * animation.
     *
     * @param name
     *            the name of the animation.
     */
    public BoneAnimation(String name) {
        this.name = name;
    }

    /**
     * Creates a new, linear-interpolating BoneAnimation with a name,
     * the bone it will control and the number of keyframes it will have.
     *
     * @param name
     *            the name of the animation
     * @param bone
     *            the bone the animation affects
     * @param numKeyframes
     *            the number of keyframes the animation has.
     */
    public BoneAnimation(String name, Bone bone, int numKeyframes) {
        this.name = name;
        keyframeTime = new float[numKeyframes];
        setInterpolate(true);
    }

    /**
     * addBoneAnimation adds a child animation to this animation. This
     * child's update will be called with the parent's.
     *
     * @param ba
     *            the child animation to add to this animation.
     */
    public void addBoneAnimation(BoneAnimation ba) {
        if (children == null) {
            children = new ArrayList<BoneAnimation>();
        }
        children.add(ba);
    }

    /**
     * adds an AnimationEvent to this animation for a specified frame.
     *
     * @param frame
     *            the frame number to trigger the event.
     * @param event
     *            the event to trigger.
     */
    public void addEvent(int frame, AnimationEvent event) {
        AnimationEventManager.getInstance().addAnimationEvent(this, frame,
                event);
    }

    /**
     * add a sync tag to this animation.
     *
     * @param name
     *            the name of the sync tag.
     * @param frames
     *            the frames that are tied to this sync tag.
     */
    public void addSync(String name, int[] frames) {
        if (syncTags == null) {
            syncTags = new HashMap<String, int[]>();
        }

        syncTags.put(name, frames);
    }

    public int[] getSyncFrames(String name) {
        if (syncTags != null) {
            return syncTags.get(name);
        }

        return null;
    }

    public boolean containsSyncTags() {
        if (syncTags == null) {
            return false;
        }
        return (syncTags.keySet().size() > 0);
    }

    public Set<String> getAllSyncTags() {
        if (syncTags == null) {
            return null;
        }
        return syncTags.keySet();
    }

    public ArrayList<String> getSyncNames(int frame) {
        if (syncTags != null) {
            Set<String> names = syncTags.keySet();
            int[] frames = null;
            ArrayList<String> nameList = new ArrayList<String>();
            for (String name : names) {
                frames = syncTags.get(name);
                if (frames != null) {
                    if (Arrays.binarySearch(frames, frame) > 0) {
                        nameList.add(name);
                    }
                }
            }

            return nameList;
        }

        return null;
    }

    /**
     * This is only used for Blend animations.
     */
    public void setInitialFrame(int frame) {
        if (frame >= endFrame) {
            currentFrame = frame;
            prevFrame = currentFrame - 1;
        } else {
            prevFrame = frame;
            currentFrame = prevFrame + 1;
        }

        currentTime = keyframeTime[currentFrame];

        // call the children of this animation if any
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setInitialFrame(frame);
            }
        }
    }

    protected StringIntMap frameNames;

    public Set<String> getFrameNames() {
        return (frameNames == null) ? null : frameNames.keySet();
    }

    public void setFrameNames(StringIntMap frameNames) {
        this.frameNames = frameNames;
    }

    public void setCurrentFrame(String frameName) {
        if (frameNames == null)
            throw new IllegalStateException(
                    "Can not use frame names, since they have not been "
                    + "defined for this animation");
        Integer frameNum = frameNames.get(frameName);
        if (frameNum == null)
            throw new IllegalArgumentException(
                    "No frame with name '" + frameName
                    + "' defined for Animation '" + name + "'.");
        setCurrentFrame(frameNames.get(frameName));
    }

    /**
     * setCurrentFrame will set the current position of the animation to the
     * supplied frame.
     * <P>
     * This method is used internally only for non-interpolated animation.
     * For internal management, The frames fields are primarily manipulated by
     * other methods.
     * </P> <P>
     * This method is to be used if you know you really want the frame changed.
     * Instead of checking whether the frame has changed, it assumes you are
     * smart enough to invoke only when needed.
     * This avoids the possibility that this method may mistakenly not update.
     * </P> <P>
     * This method works for posing plain Bones (with the Controller inactive),
     * but for posing skin-and-bones you need to use SkinNode.pose().
     * </P>
     *
     * @param frame the frame to set the current animation frame to.
     * @see #SkinNode.pose(String, int)
     */
    public void setCurrentFrame(int frame) {
        /*
         * We do not set 'prevFrame' as it needs to be set for motion
         * interpolation.  That's ok because we do no interpolation here, and
         * prevFrame is always set by the update() call before it is used.
         */
        logger.log(Level.FINE,
                "Current frame for ''{0}'' => {1}", new Object[] {name, frame});
        if (keyframeTime != null) {
            if (frame > endFrame  || frame < startFrame) {
                logger.log(Level.SEVERE, "Invalid frame index {0}.  "
                        + "Note between start and end key frames {1} "
                        + "and {2} inclusive",
                         new Object[] {frame, startFrame, endFrame});
                return;
            }
            currentFrame = frame;
            currentTime = keyframeTime[currentFrame];
            prevFrame = -1;

            // set the bone to the current frame
            if (boneTransforms != null) {
                for (int i = 0; i < boneTransforms.size(); i++) {
                    boneTransforms.get(i).setCurrentFrame(currentFrame);
                }
            }
        }
        changeFrame(currentFrame);

        // call the children of this animation if any
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setCurrentFrame(currentFrame);
            }
        }
    }

    /**
     * change frame determines if a new frame is selected and performs any
     * triggers that might be assigned to this frame.
     *
     * @param frame
     *            the current frame.
     */
    private void changeFrame(int frame) {
        if (frame != lastEventFrame) {
            ArrayList<AnimationEvent> eventList = AnimationEventManager
                    .getInstance().getAnimationEventList(this, frame);
            if (eventList != null) {
                for (int i = 0, max = eventList.size(); i < max; i++) {
                    eventList.get(i).performAction();
                }
            }

            lastEventFrame = frame;
        }
    }

    /**
     * addBoneTransform adds a bone transform array pair that this bone
     * animation uses to update. This BoneTransform is added to the
     * list of bone transforms currently in place.
     *
     * @param bt
     *            the BoneTransform to add to this list.
     */
    public void addBoneTransforms(BoneTransform bt) {
        if (boneTransforms == null) {
            boneTransforms = new ArrayList<BoneTransform>();
        }
        this.boneTransforms.add(bt);
    }

    private float estimateCallsPerFrame(float time) {
        return (keyframeTime[currentFrame] - keyframeTime[prevFrame]) / time;
    }

    /**
     * A wrapper class for non-blending updates.
     *
     * @see #update(float, int, float, float)
     */
    public void update(float time, int repeat, float speed) {
        update(time, repeat, speed, BoneTransform.NOBLEND);
    }

    /**
     * update is called during the update phase of the game cycle.
     * If this animation is not active, this method immediately
     * returns. The update of the bone is dependent on the repeat type
     * (see 'repeat' param below).
     * If blendRate is set to NOBLEND, no blending is performed.
     *
     * @param time
     * The time
     * supplied is the time between frames (normally) and this is used to define
     * what frame of animation we should be at and how to interpolate between
     * frames.
     * @param repeat
     *      <code>Controller.RT_CLAMP</code>
     * will cause the bones to animate through a single cycle and stop.
     *      <code>Controller.RT_CYCLE</code>
     * will cause the animation to reverse when it reaches one of the ends
     * of the cycle.
     *      <code>Controller.RT_WRAP</code>
     * will start the animation over from the beginning.
     *
     * @see update(float, int, float, float)
     * @see Controller
     */
    public void update(float time, int repeat, float speed, float blendRate) {
        if (startFrame >= endFrame)
            throw new IllegalStateException(
                    "Start frame " + startFrame
                    + " is not lower than end frame " + endFrame);
        if (boneTransforms != null && keyframeTime != null) {
            if (endFrame >= keyframeTime.length) {
                endFrame = keyframeTime.length - 1;
            }
            int oldFrame =
                    (blendRate == BoneTransform.NOBLEND) ? currentFrame : -1;
            // The -1 ensures we will never skip transform update due to
            // frame not changing.
            if (updateCurrentTime(time, repeat, speed)) {
                if (interpolationType != null) {
                    lastTime += time;
                    if (lastTime >= interpolationRate) {
                        if (interpolationRate > 0) {
                            lastTime = lastTime % interpolationRate;
                        } else {
                        	lastTime = 0.0f;
                        }
                        float result = (currentTime - keyframeTime[prevFrame])
                                / (keyframeTime[currentFrame] - keyframeTime[prevFrame]);
                        for (int i = 0; i < boneTransforms.size(); i++) {
                            boneTransforms.get(i).update(prevFrame,
                                    currentFrame, interpolationType[prevFrame],
                                    result, blendRate);
                        }
                        checkForClampFinish(repeat);
                    }
                } else if (oldFrame != currentFrame) {
                    int savedCurrentFrame = currentFrame;
                    // Hack because we treat 'prevFrame' as if it is the
                    // currentFrame in this non-interp block.
                    // We must restore the real current frame when done.
                    if(props == null || !props.isAllowTranslation()) {
                        for (int i = 0; i < boneTransforms.size(); i++) {
                            boneTransforms.get(i).setCurrentFrame(
                                    prevFrame, blendRate);
                        }
                    } else {
                        for (int i = 0; i < boneTransforms.size(); i++) {
                            boneTransforms.get(i).setCurrentFrame(prevFrame,
                                    blendRate, sourceBone, destSpatial,
                                    estimateCallsPerFrame(time), props);
                        }
                    }
                    currentFrame = savedCurrentFrame;
                    checkForClampFinish(repeat);
                }
            }
        }

        if (currentFrame <= endFrame) changeFrame(currentFrame);
        // Don't call if sitting at end of clamped animation

        // update the children!
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).update(time, repeat, speed, blendRate);
            }
        }
    }

    private void checkForClampFinish(int repeatMode) {
        if (repeatMode != Controller.RT_CLAMP
                && keyframeTime[endFrame] == currentTime)
            currentFrame = endFrame + 1;
    }

    private boolean updateCurrentTime(float time, int repeat, float speed) {
        if (repeat == Controller.RT_CLAMP && currentFrame > endFrame)
            return false;
            // For RT_CLAMP only, currentFrame > endFrame signifies anim DONE
            // We need a way to signify to not waste processing for this
            // "now static" position.
        if (repeat != Controller.RT_CYCLE) cycleMode = 1;
        currentTime += time * speed * cycleMode;

        float halfCycleDuration =
                keyframeTime[endFrame] - keyframeTime[startFrame];
        // This is put here for easy development.
        // For performance, should be set upon change of start or end frames.

        switch (repeat) {
            case Controller.RT_CLAMP:
                if (currentTime > keyframeTime[endFrame])
                    currentTime = keyframeTime[endFrame];
                    // It is safe to call with this situation repeatedly.
                    // We'll keep setting the time back to the endFrame time.
                    // To prevent the unnecessary work here, at some point
                    // the caller should detect that we have set time to
                    // keyframeTime[endFrame] and then set currentFrame to
                    // > endFrame.
                break;
            case Controller.RT_CYCLE:
                boolean invertOffset = false;
                while (currentTime > keyframeTime[endFrame]) {
                    cycleMode *= -1;
                    currentTime -= halfCycleDuration;
                    invertOffset = cycleMode < 0;
                }
                while (currentTime < keyframeTime[startFrame]) {
                    cycleMode *= -1;
                    currentTime += halfCycleDuration;
                    invertOffset = cycleMode > 0;
                }
                if (invertOffset) currentTime = keyframeTime[startFrame]
                            + keyframeTime[endFrame] - currentTime;
                break;
            case Controller.RT_WRAP:
                while (currentTime > keyframeTime[endFrame])
                    currentTime -= halfCycleDuration;
                break;
        }
        updateFrames();

        return true;
    }

    /**
     * Update prevFrame and currentFrame according to currentTime and
     * cycleMode.
     *
     * <P>
     * Assumes that currentTime has already been clamped to inside of our
     * keyframeTime array values.
     * </P>
     * Assumes that currentTime has already been clamped to inside of our
     */
    protected void updateFrames() {
        currentFrame = -1;
        if (cycleMode > 0) {
            for (int i = startFrame + 1; i <= endFrame; i++)
                if (currentTime <= keyframeTime[i]) {
                    currentFrame = i;
                    break;
                }
            prevFrame = currentFrame - 1;
        } else {
            for (int i = endFrame - 1; i >= startFrame; i--)
                if (currentTime >= keyframeTime[i]) {
                    currentFrame = i;
                    break;
                }
            prevFrame = currentFrame + 1;
        }
        if (currentFrame < 0)
            throw new IllegalStateException(
                    "Internal error.  Current time not within start/end time "
                    + "bounds of animation.  " + currentTime + " vs. "
                    + keyframeTime[startFrame] + " to "
                    + keyframeTime[endFrame]);
        // Following statement is so verbose that even with finest level, we
        // don't wanto to log this unless user specifically requests it.
        if (System.getProperty("com.jme.animation.BoneAnimation.TRACE")
                != null)
            logger.log(Level.FINEST, "Frames: direction {0}, {1} => {2}",
                    new Integer[] { cycleMode, prevFrame, currentFrame} );
    }

    /**
     * @deprecated It looks like the return value is very inaccurate.
     * @return true if this animation is valid (i.e. contains valid information)
     */
    public boolean isValid() {
        // TODO:  Check *.length == *.length for the per-frame array fields.
        if (keyframeTime != null) return false;
        if (boneTransforms != null) return false;
        if (interpolationType == null) return true;
        // TODO:  Consider iterating through all boneTransofrms and checking
        //        length so the rotation and translation arrays of each.
        //        These should match our keyframeTime.length
        return interpolationType.length == keyframeTime.length;
    }

    /**
     * returns the number of children animations that are attached to this
     * animation.
     *
     * @return the number of children animations this bone animation
     *         is responsible for.
     */
    public int subanimationCount() {
        if (children != null) {
            return children.size();
        } else {
            return 0;
        }
    }

    /**
     * returns a child animation from a given index. If the index is
     * invalid, null is returned.
     *
     * @param i the index to obtain the animation.
     * @return the animation at a given index, null if the index is invalid.
     */
    public BoneAnimation getSubanimation(int i) {
        if (children == null) {
            return null;
        }

        if (i >= children.size() || i < 0) {
            return null;
        }

        return children.get(i);
    }

    /**
     * Sets the times array for the keyframes. This array should be the same
     * size as the transforms array and the types array. This is left to the
     * user to insure, if they are not the same, an ArrayIndexOutOfBounds
     * exception will be thrown during update.
     *
     * @param times
     *            the times to set.
     */
    public void setTimes(float[] times) {
        this.keyframeTime = times;
    }

    /**
     * Sets the interpolation types array for the keyframes. This array should
     * be the same size as the transforms array and the types array. This is
     * left to the user to insure, if they are not the same, an
     * ArrayIndexOutOfBounds exception will be thrown during update.
     * <P>
     * If the interpolation type array is null, interpolation work will be
     * skipped (performance benefit if interpolation not required).
     * </P>
     *
     * @param types
     *        the interpolation types to set, or null for no interpolation.
     */
    public void setInterpolationTypes(int[] types) {
        interpolationType = types;
        if (interpolationType == null || interpolationType.length < 2)
            return;
        for (int i = 1; i < interpolationType.length; i++)
            if (interpolationType[i] != interpolationType[0])
                throw new IllegalArgumentException(
                        "Sorry, but at this time only one interpolation type "
                        + "may be used in a single animation sequence");
    }

    public int[] getInterpolationTypes() { return this.interpolationType; }

    /**
     * returns the name of this animation.
     *
     * @return the name of this animation
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of this animation.
     *
     * @param name
     *            the name of this animation.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the current frame that animation is currently working towards or
     * set to.
     *
     * @return the current frame of this animation.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * returns the current time of the animation.
     *
     * @return the current time of this animation.
     */
    public float getCurrentTime() {
        return currentTime;
    }

    /**
     * retrieves the end frame of the animation. The end frame defines where the
     * animation will "stop".
     *
     * @return the end frame of the animation.
     */
    public int getEndFrame() {
        return endFrame;
    }

    /**
     * sets the end frame of the animation. The end frame defines where the
     * animation will "stop".
     *
     * @param endFrame
     *            the end frame of the animation.
     */
    public void setEndFrame(int endFrame) {
        if (endFrame >= keyframeTime.length || endFrame < 0) {
            logger.log(Level.SEVERE, "Invalid endframe index {0}"
                    + ". Intialized to only have: {1} keyframes.",
                    new Integer[]{endFrame,keyframeTime.length});
            return;
        }
        this.endFrame = endFrame;
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setEndFrame(endFrame);
            }
        }
    }

    /**
     * retrieves the start frame of the animation. The start frame defines where
     * the animation will "start".
     *
     * @return the start frame of the animation.
     */
    public int getStartFrame() {
        return startFrame;
    }

    /**
     * sets the start frame of the animation. The start frame defines where the
     * animation will "start".
     *
     * @param startFrame
     *            the start frame of the animation.
     */
    public void setStartFrame(int startFrame) {
        if (startFrame >= keyframeTime.length || startFrame < 0) {
            logger.log(Level.SEVERE, "Invalid endframe index {0}"
                    + ". Intialized to only " + "have: {1} keyframes.",
                    new Integer[]{startFrame, keyframeTime.length});
            return;
        }
        this.startFrame = startFrame;
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setStartFrame(startFrame);
            }
        }
    }

    /**
     * returns true if this animation should interpolate between keyframes,
     * false otherwise.
     *
     * @return true if we will interpolation between frames.
     */
    public boolean isInterpolate() {
        return interpolationType != null;
    }

    /**
     * sets whether this animation should interpolate between frames. It also
     * sets the children of this animation to the interpolation value. True will
     * interpolate between frames, false will not.
     *
     * @param interpolate true to interpolate, false otherwise.
     */
    public void setInterpolate(boolean interpolate) {
        if (interpolate == true && keyframeTime == null) {
            throw new IllegalStateException(
                "Can't call setInterpolate() before the keyframeTimes are set");
        }
        setInterpolationTypes(
                interpolate ? new int[keyframeTime.length] : null);
        // Due to the fact that the array instantiator instantiates to 0's,
        // and our constant for linear type == 0, this sets our instance to
        // interpolate linearly.
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setInterpolate(interpolate);
            }
        }
    }

    /**
     * retrieves the rate at which interpolation occurs, this is in unit
     * seconds. Therefore, 0.25 would be a quater of a second.
     *
     * @return the interpolation rate.
     */
    public float getInterpolationRate() {
        return interpolationRate;
    }

    /**
     * sets the rate at which interpolation occurs, this is in unit seconds.
     * Therefore, 0.25 would be a quater of a second.
     *
     * @param interpolationRate
     *            the interpolation rate.
     */
    public void setInterpolationRate(float interpolationRate) {
        this.interpolationRate = interpolationRate;
    }

    /**
     * hasChildren returns true if this BoneAnimation has child
     * BoneAnimations, false otherwise.
     *
     * @return true if this animation has child animations, false otherwise.
     */
    public boolean hasChildren() {
        return (children != null);
    }

    /**
     * returns the list of keyframe times for this animation.
     *
     * @return the list of keyframe times for this animation.
     */
    public float[] getKeyFrameTimes() {
        return this.keyframeTime;
    }

    /**
     * returns the list of BoneTransforms for this animation.
     *
     * @return the list of BoneTransforms for this animation.
     */
    public ArrayList<BoneTransform> getBoneTransforms() {
        return boneTransforms;
    }

    /**
     * optimize will attempt to condense the BoneAnimation into as few
     * children as possible. This allows the proper sharing of keyframe times
     * and calculation of current time and current frame. If a child animation
     * has no children of its own, and its keyframes are equal to this
     * animation, the BoneTransforms are assimilated into this animation and
     * the child is deleted.
     */
    public void optimize(boolean removeChildren) {
        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            // check if the child has children, if so, optimize this child
            if (children.get(i).hasChildren()) {
                children.get(i).optimize(removeChildren);
            } else {
                // make sure the keyframes are equal, if we don't have keyframes
                // set it to the first one.
                if (this.keyframeTime == null) {
                    if (boneTransforms == null) {
                        boneTransforms = new ArrayList<BoneTransform>();
                    }
                    this.keyframeTime = children.get(i).getKeyFrameTimes();
                    this.interpolationType = children.get(i)
                            .getInterpolationType();
                    this.startFrame = children.get(i).getStartFrame();
                    this.endFrame = children.get(i).getEndFrame();
                    if (children.get(i).getBoneTransforms() != null) {
                        for (int j = 0; j < children.get(i).getBoneTransforms()
                                .size(); j++) {
                            BoneTransform bt = children.get(i)
                                    .getBoneTransforms().get(j);
                            if (bt != null && bt.getRotations() != null
                                    && bt.getRotations().length > 0) {
                                boneTransforms.add(children.get(i)
                                        .getBoneTransforms().get(j));
                            }
                        }
                    }
                    // we've copied this child's data, get rid of it, and adjust
                    // the count
                    // accordingly.
                    children.remove(i);
                    i--;
                } else {
                    boolean same = true;
                    if (this.keyframeTime.length == children.get(i)
                            .getKeyFrameTimes().length) {
                        for (int j = 0; j < keyframeTime.length; j++) {
                            if (keyframeTime[j] != children.get(i)
                                    .getKeyFrameTimes()[j]) {
                                same = false;
                                break;
                            }
                        }
                        if (same) {
                            if (children.get(i).getBoneTransforms() != null) {
                                for (int j = 0; j < children.get(i)
                                        .getBoneTransforms().size(); j++) {
                                    BoneTransform bt = children.get(i)
                                            .getBoneTransforms().get(j);
                                    if (bt.getRotations() != null
                                            && bt.getRotations().length > 0) {
                                        boneTransforms.add(children.get(i)
                                                .getBoneTransforms().get(j));
                                    }
                                }
                            }
                            // we've copied this child's data, get rid of it,
                            // and adjust the count
                            // accordingly.
                            children.remove(i);
                            i--;
                        }
                    }
                }
            }
        }

        if (removeChildren) {
            children.clear();
            children = null;
        }
    }

    /**
     * return the list of interpolation types assigned to this animation,
     * or null if this animation does not interpolate.
     *
     * @return the list of interpolation types assigned to this animation.
     */
    private int[] getInterpolationType() {
        return interpolationType;
    }

    /**
     * returns the string representation of this animation.
     */
    public String toString() {
        return name;
    }

    /**
     * Assigns this animation to a provided skeleton. The skeleton bones are
     * examined to assign transforms as needed. If no bones are properly
     * assigned to a transform, false is returned. If the assignment is
     * successful, true is returned.
     *
     * @param b
     *            the skeleton to assign.
     * @return true if this was successful, false otherwise.
     */
    public boolean assignSkeleton(Bone b) {
        boolean ok = true;
        if (boneTransforms != null) {
            for (int i = 0; i < boneTransforms.size(); i++) {
                if (!boneTransforms.get(i).findBone(b)) {
                    ok = false;
                }
            }
        }

        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                if (!children.get(i).assignSkeleton(b)) {
                    ok = false;
                }
            }
        }

        return ok;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule cap = e.getCapsule(this);
        cap.write(name, "name", null);
        cap.write(keyframeTime, "keyframeTime", null);
        cap.write(interpolationType, "interpolationType", null);
        cap.writeSavableArrayList(boneTransforms, "boneTransforms", null);
        cap.write(currentTime, "currentTime", 0);
        cap.write(currentFrame, "currentFrame", 1);
        cap.write(endFrame, "endFrame", 0);
        cap.write(startFrame, "startFrame", 0);
        if (frameNames != null) cap.write(frameNames, "frameNames", null);
        cap.write(interpolationRate, "interpolationRate", DEFAULT_RATE);
        cap.write(lastTime, "lastTime", 0);
        cap.write(cycleMode, "cycleMode", 1);
        // cap.write(interpolate, "interpolate", true);
        // N.b. the interpolationType above stores everthing we need.
        cap.writeSavableArrayList(children, "children", null);

        Integer[] frames = AnimationEventManager.getInstance().getFrames(this);
        if (frames != null) {
            int[] saveFrames = new int[frames.length];
            for (int i = 0; i < frames.length; i++) {
                saveFrames[i] = frames[i];
            }

            cap.write(saveFrames, "eventFrames", null);
            for (int i = 0; i < frames.length; i++) {
                cap.writeSavableArrayList(AnimationEventManager.getInstance()
                        .getEvents(this, frames[i]), "event" + frames[i], null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        InputCapsule cap = e.getCapsule(this);
        name = cap.readString("name", null);
        keyframeTime = cap.readFloatArray("keyframeTime", null);
        interpolationType = cap.readIntArray("interpolationType", null);
        boneTransforms = cap.readSavableArrayList("boneTransforms", null);
        currentTime = cap.readFloat("currentTime", 0);
        currentFrame = cap.readInt("currentFrame", 1);
        endFrame = cap.readInt("endFrame", 0);
        startFrame = cap.readInt("startFrame", 0);
        interpolationRate = cap.readFloat("interpolationRate", DEFAULT_RATE);
        lastTime = cap.readFloat("lastTime", 0);
        cycleMode = cap.readInt("cycleMode", 1);
        frameNames = (StringIntMap) cap.readSavable("frameNames", null);
        // interpolate = cap.readBoolean("interpolate", true);
        // We ignore the "interpolate" setting, since it is entirely
        // superfluous to the interpolationType field.
        // Leave this comment in place, because old versions of JME persisted
        // this setting, and people may look here and wonder why it is not
        // being read.
        children = cap.readSavableArrayList("children", null);

        int[] frames = cap.readIntArray("eventFrames", null);

        if (frames != null) {
            for (int i = 0; i < frames.length; i++) {
                ArrayList<Savable> events = cap.readSavableArrayList("event"
                        + frames[i], null);
                for (int j = 0; j < events.size(); j++) {
                    AnimationEventManager.getInstance().addAnimationEvent(this,
                            frames[i], (AnimationEvent) events.get(i));
                }
            }
        }

        // TODO:  Update this check to permit BEZIER if that really works.
        if (interpolationType != null) {
            // This is only a warning check.  No effect to behavior.
            int iType = BoneAnimation.LINEAR;
            for (int i = 0; i < keyframeTime.length; i++) {
                if (interpolationType[i] != BoneAnimation.LINEAR) {
                    iType = interpolationType[i];
                    break;
                }
            }
            if (iType != BoneAnimation.LINEAR) {
                logger.log(Level.WARNING,
                        "Unsupported interpolation type specified for "
                        + "at least one frame: {0}. Continuing with specified "
                        + "type.", iType);
            }
        }
    }

    public Class getClassTag() {
        return this.getClass();
    }

    public void resetCurrentTime() {
        currentTime = 0;
        lastTime = 0;
    }

    public void reset() {
        setCurrentFrame(startFrame);
    }

    /**
     * Ensures that an animation can continue running if you start updating
     * it again.
     *
     * @deprecated This method is entirely unnecessary after the frame number
     * refactor.
     */
    public void reactivate(int repeatType) {
    }

    public Spatial getDestSpatial() {
        return destSpatial;
    }

    public void setDestSpatial(Spatial destSpatial) {
        this.destSpatial = destSpatial;
    }

    public Bone getSourceBone() {
        return sourceBone;
    }

    public void setSourceBone(Bone sourceBone) {
        this.sourceBone = sourceBone;
    }

    public AnimationProperties getAnimationProperties() {
        return props;
    }

    public void setAnimationProperties(AnimationProperties props) {
        this.props = props;
    }
}
