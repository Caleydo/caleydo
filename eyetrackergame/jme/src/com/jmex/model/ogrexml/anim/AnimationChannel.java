package com.jmex.model.ogrexml.anim;

import java.util.ArrayList;

import com.jme.math.Matrix4f;
import com.jme.scene.Controller;

public class AnimationChannel {
	
    /**
     * The controller that causes the animation.
     */
	private MeshAnimationController controller;
	
    /**
     * The subset of affected bone indexes affected by the animation.
     */
	private ArrayList<Integer> affectedBones;
	
    /**
     * The currently playing animation.
     */
    private Animation animation;

	private float time = 0f;  
    
    /**
     * Frameskip LOD option
     */
    private int framesToSkip = 0;
    private int curFrame = 0;
	
    /**
     * Animation to blend from, e.g the animation
     * that was set before setAnimation() was called.
     */
    private transient Animation blendFrom = null;
    
    /**
     * How much to blend the new animation,
     * a value of 0 indicates apply only the previous animation
     * while a value of 1 means apply only the current animation.
     */
    private transient float blendScale = 0f;

    /**
     * Multiply this by TPF to get an addition value for blendScale,
     * makes sure blendScale only becomes 1 when the blend time has been
     * reached.
     */
    private transient float blendMultiply = 0f;

    /**
     * Same as the <code>time</code> variable but for
     * the blendFrom animation.
     */
    private transient float blendFromTime = 0f;
	
	public ArrayList<Integer> getAffectedBones() {
		return affectedBones;
	}
	
    /**
     * Sets the currently active animation.
     */
    public void setAnimation(Animation animation, float blendTime) {   	
        if (blendTime > 0f){
            blendFrom = this.animation;
            blendMultiply = 1f / blendTime;
            blendScale = 0f;
        }
        
        this.animation = animation;
        
        time = 0;
    }
    
    public Animation getAnimation() {
		return animation;
	}
    
    /**
     * Enables frameskip LOD.
     * This technique is mostly only effective when software skinning is used.
     *
     * @param framesToSkip One frame will be played out of the framesToSkip number.
     */
    public void setFrameSkip(int framesToSkip){
        if (this.framesToSkip != framesToSkip)
            this.curFrame = 0;

        this.framesToSkip = framesToSkip;
    }
    
	public float getCurTime() {
		return time;
	}
	
	/**
	* Sets the time of the animation.
	* If it's greater than getAnimationLength(getActiveAnimation()),
	* the time will be appropriately clamped/wraped depending on the repeatMode.
	*/
	public void setCurTime(float time){
		this.time = time;
	}
        
	public AnimationChannel(MeshAnimationController controller) {
		this.controller = controller;
	}
	
	/**
	 * Add all the bones to the animation channel.
	 */
	public void addAllBones() {
		affectedBones = null;
	}
	
	/**
	 * Add a single Bone to the Channel, 
	 * and don't have multiple instances of the same in the list.
	 */
	public void addBone(String name) {
		addBone(controller.getSkeleton().getBone(name));
	}
	
	/**
	 * Add a single Bone to the Channel, 
	 * and don't have multiple instances of the same in the list.
	 */
	public void addBone(Bone bone) {
		int boneIndex = controller.getSkeleton().getBoneIndex(bone); 
		if(affectedBones == null) {
			affectedBones = new ArrayList<Integer>();
			affectedBones.add(boneIndex);
		}
		else if(!affectedBones.contains(boneIndex)) {
			affectedBones.add(boneIndex);
		}
	}
	
	/**
	 * Add Bones to the Channel going toward the root bone. (i.e. parents)
	 */
	public void addToRootBone(String name) {		
		addToRootBone(controller.getSkeleton().getBone(name));
	}
	
	/**
	 * Add Bones to the Channel going toward the root bone. (i.e. parents)
	 */
	public void addToRootBone(Bone bone) {
		addBone(bone);
		while (bone.parent != null) {
			bone = bone.parent;
			addBone(bone);
		}	
	}
		
	/**
	 * Add Bones to the Channel going away from the root bone. (i.e. children)
	 */
	public void addFromRootBone(String name) {
		addFromRootBone(controller.getSkeleton().getBone(name));
	}
	
	/**
	 * Add Bones to the Channel going away from the root bone. (i.e. children)
	 */
	public void addFromRootBone(Bone bone) {
		addBone(bone);
		if (bone.children == null)
			return;		
		for (Bone childBone : bone.children) {
			addBone(childBone);
			addFromRootBone(childBone);
		}
		
	}
	
	public void update(float tpf) {
    	
        if (animation == null)
            return;

        // do clamping/wrapping of time
        time = controller.clampWrapTime(time, animation.getLength());
        if (blendFrom != null){
            blendFromTime = controller.clampWrapTime(blendFromTime, blendFrom.getLength());
        }
        
        if (framesToSkip > 0){
            // check frame skipping
            curFrame++;

            if (curFrame != framesToSkip){
                time += tpf * controller.getSpeed();
                if (blendFrom != null)
                    blendFromTime += tpf * controller.getSpeed();

                return;
            }else{
                curFrame = 0;
            }
        }

        if (blendFrom == null){
            animation.setTime(time, controller.getTargets(), controller.getSkeleton(), 1f, affectedBones);
        } else {
            blendFrom.setTime(blendFromTime, controller.getTargets(), controller.getSkeleton(), 1f - blendScale, affectedBones);
            animation.setTime(time, controller.getTargets(), controller.getSkeleton(), blendScale, affectedBones);

            // here update the blending scale
            blendScale += tpf * blendMultiply;
            if (blendScale > 1f){
                blendFrom = null;
                blendScale = 0f;
                blendMultiply = 0f;
            }
        }
        

        time += tpf * controller.getSpeed();
        if (blendFrom != null)
            blendFromTime += tpf * controller.getSpeed();
	}
	

}
