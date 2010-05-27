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
import java.util.Set;
import java.util.logging.Logger;
import com.jme.math.FastMath;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * AnimationController provides a method for managing multiple BoneAnimations.
 * AnimationController maintains a list of available animations to play, and
 * a reference to the currently active animation. The currently active animation
 * can be set via index into the animation set, the name of the animation or
 * a reference to the animation itself. If blending is used, the active animation
 * is not immediately switched, but instead morphs with an incoming animation via
 * crossfading for a specified period of time. When the blend is complete, the
 * active animation is set to the incoming animation.
 *
 * @see BoneAnimation
 * @author mpowell
 */
public class AnimationController extends Controller implements Savable {
    private static final Logger logger =
            Logger.getLogger(AnimationController.class .getName());
    private static final long serialVersionUID = 1L;

    private ArrayList<BoneAnimation> animationSets;

    private Bone skeleton;

    private Spatial modelNode;

    private BoneAnimation activeAnimation;

    /**
     * Resets the active animation to the start, then disables the
     * AnimationController so the animation will no longer animate.
     *
     * <P>
     * This method does not clear the active animation (there is a method to
     * do that).
     * </P>
     */
    public void reset() {
        if (activeAnimation != null) activeAnimation.reset();
        setActive(false);
    }

//    private class ModifierData {
//        public BoneAnimation animation;
//        public float blendTime;
//    }
//    private ArrayList<ModifierData> activeAnimationsList = new ArrayList<ModifierData>();

    private BoneAnimation blendAnimation;

    private float currentBlendTime;

    private float endBlendTime;

    private boolean needsSync;

    private int previousTest = -1;

    private AnimationProperties props;

    public AnimationController() {
    }

    public void addAnimation(BoneAnimation bac) {
        if (bac == null) {
            return;
        }

        if (animationSets == null) {
            animationSets = new ArrayList<BoneAnimation>();
        }
        animationSets.add(bac);

        if (skeleton != null) {
            bac.assignSkeleton(skeleton);
        }
    }

    public boolean hasAnimation(String name) {
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                if (animationSets.get(i).getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    public BoneAnimation getAnimation(String name) {
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                if (animationSets.get(i).getName().equalsIgnoreCase(name)) {
                    return animationSets.get(i);
                }
            }
        }

        return null;
    }

    public void removeAnimation(BoneAnimation bac) {
        if (animationSets != null) {
            animationSets.remove(bac);

            if (animationSets.size() == 0) {
                activeAnimation = null;
            } else if (bac == activeAnimation) {
                activeAnimation = animationSets.get(0);
            }
        }
    }

    public void removeAnimation(int index) {
        if (animationSets == null || index < 0 || index >= animationSets.size()) {
            return;
        }

        BoneAnimation bac = animationSets.get(index);
        animationSets.remove(index);
        if (animationSets.size() == 0) {
            activeAnimation = null;
        } else if (bac == activeAnimation) {
            activeAnimation = animationSets.get(0);
        }
    }

    public BoneAnimation getActiveAnimation() {
        return activeAnimation;
    }

    public void setCurrentFrame(int frame) {
        if (activeAnimation != null) {
            activeAnimation.setCurrentFrame(frame);
        }
    }

    public void clearAnimations() {
        if (animationSets != null) {
            animationSets.clear();
        }
    }

    public BoneAnimation getAnimation(int i) {
        if (animationSets != null) {
            return animationSets.get(i);
        } else {
            return null;
        }
    }

    public ArrayList<BoneAnimation> getAnimations() {
        return animationSets;
    }

    public void clearActiveAnimation() {
        activeAnimation = null;
        blendAnimation = null;
    }

    public void setBlendAnimation(BoneAnimation blendAnimation,
            float blendTime, boolean sync) {
    	// don't blend between the same animation
        if (blendAnimation == this.blendAnimation) {
            return;
        }

        if(blendAnimation == this.activeAnimation /*&& this.blendAnimation == null*/) {
            needsSync = false;
            this.blendAnimation = blendAnimation;
            this.blendAnimation.setCurrentFrame(this.activeAnimation.getCurrentFrame());
            currentBlendTime = 1;
        } else {
//            if (this.blendAnimation != null) {
//                ModifierData modifierData = new ModifierData();
//                modifierData.animation = this.blendAnimation;
//                modifierData.blendTime = this.currentBlendTime;
//                activeAnimationsList.add(modifierData);
//            }

            blendAnimation.reset();

            this.blendAnimation = blendAnimation;

            if (sync) {
                needsSync = true;
                calculateSyncFrame();
            } else {
                needsSync = false;
            }

            currentBlendTime = 0f;
            endBlendTime = blendTime;
        }
    }

    private void calculateSyncFrame() {
        if (activeAnimation != null && blendAnimation != null
                && previousTest != activeAnimation.getCurrentFrame()) {
            previousTest = activeAnimation.getCurrentFrame();

            // Make sure current animation has sync tags.
            Set<String> activeTags = activeAnimation.getAllSyncTags();
            if (activeTags == null || activeTags.size() == 0) {
                needsSync = false;
                return;
            }

            // Make sure blend animation has sync tags.
            Set<String> blendTags = blendAnimation.getAllSyncTags();
            if (blendTags == null || blendTags.size() == 0) {
                needsSync = false;
                return;
            }

            // Make sure there is at least one matching sync tag to use
            boolean match = false;
            for (String name : activeTags) {
                if (blendTags.contains(name)) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                needsSync = false;
                return;
            }

            // We can sync, so let's try.
            ArrayList<String> currentSyncTags = activeAnimation
                    .getSyncNames(previousTest);

            // We can't sync yet, wait a bit.
            if (currentSyncTags.size() == 0) {
                return;
            }

            for (String tag : currentSyncTags) {
                if (blendTags.contains(tag)) {
                    // we can sync here
                    int[] frames = blendAnimation.getSyncFrames(tag);
                    // find the closest match to our frame.
                    int old = (int) FastMath.abs(frames[0] - previousTest);
                    int diff = 0;
                    int i = 1;
                    for (i = 1; i < frames.length; i++) {
                        diff = (int) FastMath.abs(frames[i] - previousTest);
                        if (diff > old) {
                            // old is the sync frame
                            break;
                        }
                    }
                    blendAnimation.setInitialFrame(frames[i - 1]);
                    needsSync = false;
                    return;
                }
            }

            // TODO: If we made it this far, we need to wait a few more frames
            // before
            // syncing. This can be precalculated so that we don't do this each
            // frame.
        }
    }

    public void updateProps() {
        if(props.isAllowTranslation() && skeleton != null && modelNode != null) {
            if(blendAnimation != null) {
                blendAnimation.setSourceBone(skeleton);
                blendAnimation.setDestSpatial(modelNode);
                blendAnimation.setAnimationProperties(props);
            }

            if(activeAnimation != null) {
                activeAnimation.setSourceBone(skeleton);
                activeAnimation.setDestSpatial(modelNode);
                activeAnimation.setAnimationProperties(props);
            }
        } else {
            if(blendAnimation != null) {
                blendAnimation.setSourceBone(null);
                blendAnimation.setDestSpatial(null);
            }

            if(activeAnimation != null) {
                activeAnimation.setSourceBone(null);
                activeAnimation.setDestSpatial(null);
            }
        }
    }

    public void setActiveAnimation(String name) {
        setActiveAnimation(name, false, 0, null);
    }

    public void setActiveAnimation(String name, boolean blend, float time,
            AnimationProperties props) {
        this.props = props;
        if (blend) {
            if (animationSets != null) {
                for (int i = 0; i < animationSets.size(); i++) {
                    if (animationSets.get(i).getName().equalsIgnoreCase(name)) {
                        setBlendAnimation(animationSets.get(i), time, !props.isOneOff());
                        return;
                    }
                }
            }
        } else {
            BoneAnimation old = activeAnimation;
            if (animationSets != null) {
                for (int i = 0; i < animationSets.size(); i++) {
                    if (animationSets.get(i).getName().equalsIgnoreCase(name)) {
                        activeAnimation = animationSets.get(i);
                        activeAnimation.reset();
                        if (old != activeAnimation) {
                            this.blendAnimation = null;
                        }
                        return;
                    }
                }
            }

            // Invalid animation, set active to null
            clearActiveAnimation();
            // Terrible behavior to just silently fail to do what was requested.
            // We are making it difficult to detect and troubleshoot problems.
        }
    }

    public void setActiveAnimation(BoneAnimation bac) {
        setActiveAnimation(bac, false, 0, false);
    }

    public void setActiveAnimation(BoneAnimation bac, boolean blend,
            float time, boolean sync) {
        if (blend) {
            setBlendAnimation(bac, time, sync);
            return;
        } else {
            BoneAnimation old = activeAnimation;
            if (animationSets != null) {
                for (int i = 0; i < animationSets.size(); i++) {
                    if (animationSets.get(i) == bac) {
                        activeAnimation = animationSets.get(i);
                        activeAnimation.reset();
                        if (old != activeAnimation) {
                            this.blendAnimation = null;
                        }
                        return;
                    }
                }
            }
            // Invalid animation, set active to null
            clearActiveAnimation();
        }
    }

    public void setActiveAnimation(int index) {
        setActiveAnimation(index, false, 0, false);
    }

    public void setActiveAnimation(int index, boolean blend, float time,
            boolean sync) {
        if (blend) {
            if (animationSets != null && animationSets.size() > index) {
                setBlendAnimation(animationSets.get(index), time, sync);
                return;
            }
        } else {
            BoneAnimation old = activeAnimation;
            if (animationSets != null && animationSets.size() > index) {
                activeAnimation = animationSets.get(index);
                activeAnimation.reset();
                if (old != activeAnimation) {
                    this.blendAnimation = null;
                }
                return;
            }
            // Invalid animation, set active to null
            clearActiveAnimation();
        }
    }

    public void setSkeleton(Bone b) {
        this.skeleton = b;
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                animationSets.get(i).assignSkeleton(skeleton);
            }
        }
    }

    @Override
    public void update(float time) {
    	//We are blending into nothing, so just set this as active.
    	if(blendAnimation != null && activeAnimation == null) {
    		activeAnimation = blendAnimation;
    		blendAnimation = null;
    	}

        if (blendAnimation != null && !needsSync) {
            currentBlendTime += time / endBlendTime;
            if (currentBlendTime >= 1.0f) {
//                activeAnimationsList.clear();

                activeAnimation = blendAnimation;
                blendAnimation = null;
                updateProps();
            }
        }

        if (activeAnimation != null) {
            activeAnimation.update(time, getRepeatType(), getSpeed());
//            for (ModifierData modifierData : activeAnimationsList) {
//                modifierData.animation.update(time, getRepeatType(), getSpeed(),
//                        modifierData.blendTime);
//            }

            if (blendAnimation != null) {
                if (!needsSync) {
                    blendAnimation.update(time, getRepeatType(), getSpeed(),
                            currentBlendTime);
                } else {
                    calculateSyncFrame();
                }
            }
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);
        cap.writeSavableArrayList(animationSets, "animationSets", null);
        cap.write(skeleton, "skeleton", null);
        cap.write(activeAnimation, "activeAnimation", null);
        if (restPoseAnimation != null)
            cap.write(restPoseAnimation.getName(), "restPoseAnimName", null);
        if (restPoseFrame > -1) cap.write(restPoseFrame, "restPoseFrame", -1);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);
        animationSets = cap.readSavableArrayList("animationSets", null);
        skeleton = (Bone) cap.readSavable("skeleton", null);
        activeAnimation = (BoneAnimation) cap.readSavable("activeAnimation",
                null);
        setRestPoseFrame(cap.readInt("restPoseFrame", -1));
        String restPoseAnimName = cap.readString("restPoseAnimName", null);
        if (restPoseAnimName != null) setRestPoseAnimName(restPoseAnimName);
    }

    protected int restPoseFrame = -1;
    protected BoneAnimation restPoseAnimation;

    /**
     * This method does not activate or deactive the Controller itself, nor
     * does it refresh any skins.  It just sets the current Animation and
     * the frame of that Animation.
     *
     * @throws IllegalStateException if either the rest pose animation or
     *                               frame have not been set.
     */
    public void rest() {
        if (restPoseAnimation == null || restPoseFrame < 0)
            throw new IllegalStateException(
                    "Can not rest the Skeleton because the rest pose frame "
                    + "has not been set up");
        setActiveAnimation(restPoseAnimation);
        activeAnimation.setCurrentFrame(restPoseFrame);
    }

    public void setRestPoseAnimName(String restPoseAnimName) {
        restPoseAnimation = getAnimation(restPoseAnimName);
    }

    public String getRestPoseAnimName() {
        return (restPoseAnimation == null) ? null : restPoseAnimation.getName();
    }

    public int getRestPoseFrame() {
        return restPoseFrame;
    }

    public void setRestPoseFrame(int restPoseFrame) {
        this.restPoseFrame = restPoseFrame;
    }

    public BoneAnimation getBlendAnimation() {
        return blendAnimation;
    }

    public Spatial getModelNode() {
        return modelNode;
    }

    public void setModelNode(Spatial modelNode) {
        this.modelNode = modelNode;
    }

    public AnimationProperties getProps() {
        return props;
    }

    public void setProps(AnimationProperties props) {
        this.props = props;
    }

    /**
     * Has no effect, and that is misleading.
     * Should just throw.  Does not in order to not break apps that now call it.
     */
    public void setMaxTime(float minTime) {
        /* TODO:  Implement.
         * This should set the keyFrameTime of the end fame of the current
         * BoneAnimation, but needs to verify that it is higher than
         * keyFrameTime[endFrame-1].
         */
        logger.warning(AnimationController.class.getName()
                + ".setMaxTime() does nothing.  Needs to be implemented.");
        super.setMaxTime(minTime);
        // for the unlikely case that somebody runtime-overrides
        // Controller.setMaxTime().
    }

    /**
     * Has no effect, and that is misleading.
     * Should just throw.  Does not in order to not break apps that now call it.
     */
    public void setMinTime(float minTime) {
        /* TODO:  Implement.
         * This should set the keyFrameTime of the start fame of the current
         * BoneAnimation, but needs to verify that it is lower than
         * keyFrameTime[startFrame+1].
         */
        logger.warning(AnimationController.class.getName()
                + ".setMinTime() does nothing.  Needs to be implemented.");
        // for the unlikely case that somebody runtime-overrides
        // Controller.setMinTime().
    }

    public float getMinTime() {
        if (activeAnimation == null)
            throw new IllegalStateException("No animation is active");
        return activeAnimation.getKeyFrameTimes()[
                activeAnimation.getStartFrame()];
    }

    public float getMaxTime() {
        if (activeAnimation == null)
            throw new IllegalStateException("No animation is active");
        return activeAnimation.getKeyFrameTimes()[
                activeAnimation.getEndFrame()];
    }

    public float getCurTime() {
        if (activeAnimation == null)
            throw new IllegalStateException("No animation is active");
        return activeAnimation.getCurrentTime();
    }
}
