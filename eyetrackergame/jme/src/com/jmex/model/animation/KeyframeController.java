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

package com.jmex.model.animation;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.scene.Controller;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * Started Date: Jun 12, 2004 <br>
 * <br>
 * 
 * Class can do linear interpolation of a TriMesh between units of time. Similar
 * to <code>VertexKeyframeController</code> but interpolates float units of
 * time instead of integer keyframes. <br>
 * <br>
 * Controller.setSpeed(float) sets a speed relative to the defined speed. For
 * example, the default is 1. A speed of 2 would run twice as fast and a speed
 * of .5 would run half as fast <br>
 * <br>
 * Controller.setMinTime(float) and Controller.setMaxTime(float) both define the
 * bounds that KeyframeController should follow. It is the programmer's
 * responsiblity to make sure that the MinTime and MaxTime are within the span
 * of the defined <code>setKeyframe</code><br>
 * <br>
 * Controller functions RepeatType and isActive are both defined in their
 * default way for KeyframeController <br>
 * <br>
 * When this controller is saved/loaded to XML format, it assumes that the mesh
 * it morphs is the TriMesh it belongs to, so it is recomended to only attach
 * this controller to the TriMesh it animates.
 * 
 * @author Jack Lindamood, kevglass (parts), hevee (blend time)
 * @version $Id: KeyframeController.java 4132 2009-03-19 20:34:56Z blaine.dev $
 */
public class KeyframeController extends Controller {
    private static final Logger logger = Logger.getLogger(KeyframeController.class.getName());

    private static final long serialVersionUID = 1L;

    /**
     * An array of <code>PointInTime</code> s that defines the animation
     */
    transient public ArrayList<PointInTime> keyframes;

    /**
     * A special array used with SmoothTransform to store temporary smooth
     * transforms
     */
    transient private ArrayList<PointInTime> prevKeyframes;

    /**
     * The mesh that is actually morphed
     */
    private TriMesh morphMesh;

    /**
     * The current time in the animation
     */
    transient private float curTime;

    /**
     * The current frame of the animation
     */
    transient private int curFrame;

    /**
     * The frame of animation we're heading towards
     */
    transient private int nextFrame;
    
    /**
     * The PointInTime before <code>curTime</code>
     */
    transient private PointInTime before;

    /**
     * The PointInTime after <code>curTime</code>
     */
    transient private PointInTime after;

    /**
     * If true, the animation is moving forward, if false the animation is
     * moving backwards
     */
    transient private boolean movingForward;

    /**
     * Used with SmoothTransform to signal it is doing a smooth transform
     */
    transient private boolean isSmooth;

    /**
     * Used with SmoothTransform to hold the new beinging and ending time once
     * the transform is complete
     */
    transient private float tempNewBeginTime;

    transient private float tempNewEndTime;

    /** If true, the model's bounding volume will update every frame. */
    private boolean updatePerFrame;

    /**
     * Default constructor. Speed is 1, MinTime is 0 MaxTime is 0. Both MinTime
     * and MaxTime are automatically adjusted by setKeyframe if the setKeyframe
     * time is less than MinTime or greater than MaxTime. Default RepeatType is
     * RT_WRAP.
     */
    public KeyframeController() {
        this.setSpeed(1);
        keyframes = new ArrayList<PointInTime>();
        curFrame = 0;
        this.setRepeatType(Controller.RT_WRAP);
        movingForward = true;
        this.setMinTime(0);
        this.setMaxTime(0);
        updatePerFrame = true;
    }
    
    public float getCurrentTime() {
        return curTime;
    }
    
    public int getCurrentFrame() {
        return curFrame;
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
     * Sets the Mesh that will be physically changed by this KeyframeController
     * 
     * @param morph
     *            The new mesh to morph
     */
    public void setMorphingMesh(TriMesh morph) {
        morphMesh = morph;
        keyframes.clear();
        keyframes.add(new PointInTime(0, null));
    }

    public void shallowSetMorphMesh(TriMesh morph) {
        morphMesh = morph;
    }

    /**
     * Tells the controller to change its morphMesh to <code>shape</code> at
     * <code>time</code> seconds. Time must be >=0 and shape must be non-null
     * and shape must have the same number of vertexes as the current shape. If
     * not, then nothing happens. It is also required that
     * <code>setMorphingMesh(TriMesh)</code> is called before
     * <code>setKeyframe</code>. It is assumed that shape.indices ==
     * morphMesh.indices, otherwise morphing may look funny
     * 
     * @param time
     *            The time for the change
     * @param shape
     *            The new shape at that time
     */
    public void setKeyframe(float time, TriMesh shape) {
        if (morphMesh == null || time < 0
                || shape.getVertexBuffer().capacity() != morphMesh.getVertexBuffer().capacity())
                return;
        for (int i = 0; i < keyframes.size(); i++) {
            PointInTime lookingTime = keyframes.get(i);
            if (lookingTime.time == time) {
                lookingTime.newShape = shape;
                return;
            }
            if (lookingTime.time > time) {
                keyframes.add(i, new PointInTime(time, shape));
                return;
            }
        }
        keyframes.add(new PointInTime(time, shape));
        if (time > this.getMaxTime()) this.setMaxTime(time);
        if (time < this.getMinTime()) this.setMinTime(time);
    }

    /**
     * This function will do a smooth translation between a keframe's current
     * look, to the look directly at newTimeToReach. It takes translationLen
     * time (in sec) to do that translation, and once translated will animate
     * like normal between newBeginTime and newEndTime <br>
     * <br>
     * This would be usefull for example when a figure stops running and tries
     * to raise an arm. Instead of "teleporting" to the raise-arm animation
     * begining, a smooth translation can occur.
     * 
     * @param newTimeToReach
     *            The time to reach.
     * @param translationLen
     *            How long it takes
     * @param newBeginTime
     *            The new cycle begining time
     * @param newEndTime
     *            The new cycle ending time.
     */
    public void setSmoothTranslation(float newTimeToReach,
            float translationLen, float newBeginTime, float newEndTime) {
        if (!isActive() || isSmooth) return;
        if (newBeginTime < 0
                || newBeginTime >  keyframes.get(keyframes.size() - 1).time) {
            logger.warning("Attempt to set invalid begintime:" + newBeginTime);
            return;
        }
        if (newEndTime < 0
                || newEndTime >  keyframes.get(keyframes.size() - 1).time) {
            logger.warning(
                    "Attempt to set invalid endtime:" + newEndTime);
            return;
        }
        TriMesh begin = null, end = null;
        if (prevKeyframes == null) {
            prevKeyframes = new ArrayList<PointInTime>();
            begin = new TriMesh();
            end = new TriMesh();
        } else {
            begin = prevKeyframes.get(0).newShape;
            end = prevKeyframes.get(1).newShape;
            prevKeyframes.clear();
        }

        getCurrent(begin);

        curTime = newTimeToReach;
        curFrame = 0;
        setMinTime(0);
        setMaxTime( keyframes.get(keyframes.size() - 1).time);
        update(0);
        getCurrent(end);

        swapKeyframeSets();
        curTime = 0;
        curFrame = 0;
        setMinTime(0);
        setMaxTime(translationLen);
        setKeyframe(0, begin);
        setKeyframe(translationLen, end);
        isSmooth = true;
        tempNewBeginTime = newBeginTime;
        tempNewEndTime = newEndTime;
    }

    /**
     * Swaps prevKeyframes and keyframes
     */
    private void swapKeyframeSets() {
        ArrayList<PointInTime> temp = keyframes;
        keyframes = prevKeyframes;
        prevKeyframes = temp;
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
        if (isSmooth) return;
        if (newBeginTime < 0
                || newBeginTime >  keyframes
                        .get(keyframes.size() - 1).time) {
            logger.warning(
                    "Attempt to set invalid begintime:" + newBeginTime);
            return;
        }
        if (newEndTime < 0
                || newEndTime >  keyframes
                        .get(keyframes.size() - 1).time) {
            logger.warning(
                    "Attempt to set invalid endtime:" + newEndTime);
            return;
        }
        setMinTime(newBeginTime);
        setMaxTime(newEndTime);
        setActive(true);
        if (newBeginTime <= newEndTime) { // Moving forward
            movingForward = true;
            curTime = newBeginTime;
            if (newBeginTime == newEndTime) {
                update(0);
                setActive(false);
            }
        } else { // Moving backwards
            movingForward = false;
            curTime = newEndTime;
        }
    }

    /**
     * Saves whatever the current morphMesh looks like into the dataCopy
     * 
     * @param dataCopy
     *            The copy to save the current mesh into
     */
    private void getCurrent(TriMesh dataCopy) {
        if (morphMesh.getColorBuffer() != null) {
            FloatBuffer dcColors = dataCopy.getColorBuffer();
            if (dcColors != null)
                dcColors.clear();
            FloatBuffer mmColors = morphMesh.getColorBuffer();
            mmColors.clear();
            if (dcColors == null || dcColors.capacity() != mmColors.capacity()) {
                dcColors = BufferUtils.createFloatBuffer(mmColors.capacity());
                dcColors.clear();
                dataCopy.setColorBuffer(dcColors);
            }
            
            dcColors.put(mmColors);
            dcColors.flip();
        }
        if (morphMesh.getVertexBuffer() != null) {
            FloatBuffer dcVerts = dataCopy.getVertexBuffer();
            if (dcVerts != null)
                dcVerts.clear();
            FloatBuffer mmVerts = morphMesh.getVertexBuffer();
            mmVerts.clear();
            if (dcVerts == null || dcVerts.capacity() != mmVerts.capacity()) {
                dcVerts = BufferUtils.createFloatBuffer(mmVerts.capacity());
                dcVerts.clear();
                dataCopy.setVertexBuffer(dcVerts);
            }
            
            dcVerts.put(mmVerts);
            dcVerts.flip();
        }
        if (morphMesh.getNormalBuffer() != null) {
            FloatBuffer dcNorms = dataCopy.getNormalBuffer();
            if (dcNorms != null)
                dcNorms.clear();
            FloatBuffer mmNorms = morphMesh.getNormalBuffer();
            mmNorms.clear();
            if (dcNorms == null || dcNorms.capacity() != mmNorms.capacity()) {
                dcNorms = BufferUtils.createFloatBuffer(mmNorms.capacity());
                dcNorms.clear();
                dataCopy.setNormalBuffer(dcNorms);
            }
            
            dcNorms.put(mmNorms);
            dcNorms.flip();
        }
        if (morphMesh.getIndexBuffer() != null) {
            IntBuffer dcInds = dataCopy.getIndexBuffer();
            if (dcInds != null)
                dcInds.clear();
            IntBuffer mmInds = morphMesh.getIndexBuffer();
            mmInds.clear();
            if (dcInds == null || dcInds.capacity() != mmInds.capacity()) {
                dcInds = BufferUtils.createIntBuffer(mmInds.capacity());
                dcInds.clear();
                dataCopy.setIndexBuffer(dcInds);
            }
            
            dcInds.put(mmInds);
            dcInds.flip();
        }
        if (morphMesh.getTextureCoords(0) != null) {
            FloatBuffer dcTexs = dataCopy.getTextureCoords(0).coords;
            if (dcTexs != null)
                dcTexs.clear();
            FloatBuffer mmTexs = morphMesh.getTextureCoords(0).coords;
            mmTexs.clear();
            if (dcTexs == null || dcTexs.capacity() != mmTexs.capacity()) {
                dcTexs = BufferUtils.createFloatBuffer(mmTexs.capacity());
                dcTexs.clear();
                dataCopy.setTextureCoords(new TexCoords(dcTexs), 0);
            }
            
            dcTexs.put(mmTexs);
            dcTexs.flip();
        }
    }

    /**
     * As defined in Controller
     * 
     * @param time
     *            as defined in Controller
     */
    public void update(float time) {
        if (easyQuit()) return;
        if (movingForward)
            curTime += time * this.getSpeed();
        else
            curTime -= time * this.getSpeed();
        
        findFrame();
        before =  keyframes.get(curFrame);
        // Change this bit so the next frame we're heading towards isn't always going
        // to be one frame ahead since now we coule be animating from the last to first
        // frames.
        //after =  keyframes.get(curFrame + 1));
        after =  keyframes.get(nextFrame);
        
        float delta = (curTime - before.time) / (after.time - before.time);
        
        // If we doing that wrapping bit then delta should be caculated based 
        // on the time before the start of the animation we are. 
        if (nextFrame < curFrame) {
        	delta = blendTime - (getMinTime()-curTime);
        }
        
        TriMesh oldShape = before.newShape;
        TriMesh newShape = after.newShape;
        
        FloatBuffer verts = morphMesh.getVertexBuffer();
        FloatBuffer norms = morphMesh.getNormalBuffer();
        FloatBuffer texts = morphMesh.getTextureCoords(0) != null ? morphMesh.getTextureCoords(0).coords : null;
        FloatBuffer colors = morphMesh.getColorBuffer();

        FloatBuffer oldverts = oldShape.getVertexBuffer();
        FloatBuffer oldnorms = oldShape.getNormalBuffer();
        FloatBuffer oldtexts = oldShape.getTextureCoords(0) != null ? oldShape.getTextureCoords(0).coords : null;
        FloatBuffer oldcolors = oldShape.getColorBuffer();

        FloatBuffer newverts = newShape.getVertexBuffer();
        FloatBuffer newnorms = newShape.getNormalBuffer();
        FloatBuffer newtexts = newShape.getTextureCoords(0) != null ? newShape.getTextureCoords(0).coords : null;
        FloatBuffer newcolors = newShape.getColorBuffer();
        int vertQuantity = verts.capacity() / 3;
        if (verts == null || oldverts == null || newverts == null) return;
        verts.rewind(); oldverts.rewind(); newverts.rewind();

        if (norms != null) norms.rewind(); // reset to start
        if (oldnorms != null) oldnorms.rewind(); // reset to start
        if (newnorms != null) newnorms.rewind(); // reset to start

        if (texts != null) texts.rewind(); // reset to start
        if (oldtexts != null) oldtexts.rewind(); // reset to start
        if (newtexts != null) newtexts.rewind(); // reset to start

        if (colors != null) colors.rewind(); // reset to start
        if (oldcolors != null) oldcolors.rewind(); // reset to start
        if (newcolors != null) newcolors.rewind(); // reset to start
                
        for (int i = 0; i < vertQuantity; i++) {
            for (int x = 0; x < 3; x++) // x, y, and z
                verts.put(i*3+x, (1f-delta)*oldverts.get(i*3 + x) + delta*newverts.get(i*3 + x));

            if (norms != null && oldnorms != null && newnorms != null)
                for (int x = 0; x < 3; x++) // x, y, and z
                    norms.put(i*3+x, (1f-delta)*oldnorms.get(i*3 + x) + delta*newnorms.get(i*3 + x));

            if (texts != null && oldtexts != null && newtexts != null)
                for (int x = 0; x < 2; x++) // x and y
                    texts.put(i*2+x,(1f-delta)*oldtexts.get(i*2 + x) + delta*newtexts.get(i*2 + x));

            if (colors != null && oldcolors != null && newcolors != null)
                for (int x = 0; x < 4; x++) // r, g, b, a
                    colors.put(i*4+x,(1f-delta)*oldcolors.get(i*4 + x) + delta*newcolors.get(i*4 + x));
        }

        if (updatePerFrame) morphMesh.updateModelBound();
    }

    /**
     * If both min and max time are equal and the model is already updated, then
     * it's an easy quit, or if it's on CLAMP and I've exceeded my time it's
     * also an easy quit.
     * 
     * @return true if update doesn't need to be called, false otherwise
     */
    private boolean easyQuit() {
        if (getMaxTime() == getMinTime() && curTime != getMinTime())
            return true;
        else if (getRepeatType() == RT_CLAMP
                && (curTime > getMaxTime() || curTime < getMinTime()))
            return true;
        else if (keyframes.size() < 2) return true;
        return false;
    }

    /**
     * If true, the model's bounding volume will be updated every frame. If
     * false, it will not.
     * 
     * @param update
     *            The new update model volume per frame value.
     */
    public void setModelUpdate(boolean update) {
        updatePerFrame = update;
    }

    /**
     * Returns true if the model's bounding volume is being updated every frame.
     * 
     * @return True if bounding volume is updating.
     */
    public boolean getModelUpdate() {
        return updatePerFrame;
    }

    private float blendTime = 0;
    /**
     * If repeat type <CODE>RT_WRAP</CODE> is set, after reaching the last frame of the currently set
     * animation maxTime (see <CODE>Controller.setMaxTime</CODE>), there will be an additional <CODE>blendTime</CODE>
     * seconds long phase inserted, morphing from the last frame to the first.
     * @param blendTime The blend time to set
     */
    public void setBlendTime(float blendTime){ this.blendTime = blendTime; }

    /**
     * Gets the currently set blending time for smooth animation transitions
     * @return The current blend time
     * @see #setBlendTime(float blendTime)
     */
    public float getBlendTime(){ return blendTime; }
    
    /**
     * This is used by update(float). It calculates PointInTime
     * <code>before</code> and <code>after</code> as well as makes
     * adjustments on what to do when <code>curTime</code> is beyond the
     * MinTime and MaxTime bounds
     */
    private void findFrame() {
    	// If we're in our special wrapping case then just ignore changing
    	// frames. Once we get back into the actual series we'll revert back
    	// to the normal process
    	if ((curTime < getMinTime()) && (nextFrame < curFrame)) {
    		return;
    	}
    	
    	// Update the rest to maintain our new nextFrame marker as one infront
    	// of the curFrame in all cases. The wrap case is where the real work 
    	// is done.
        if (curTime > this.getMaxTime()) {
            if (isSmooth) {
                swapKeyframeSets();
                isSmooth = false;
                curTime = tempNewBeginTime;
                curFrame = 0;
                nextFrame = 1;
                setNewAnimationTimes(tempNewBeginTime, tempNewEndTime);
                return;
            }
            if (this.getRepeatType() == Controller.RT_WRAP) {
            	float delta = blendTime;
                curTime = this.getMinTime() - delta;
                curFrame = Math.min(curFrame + 1, keyframes.size() - 1);
                
                for (nextFrame = 0; nextFrame < keyframes.size() - 1; nextFrame++) {
                    if (getMinTime() <=  keyframes.get(nextFrame).time)
                            break;
                }
                return;
            } else if (this.getRepeatType() == Controller.RT_CLAMP) {
                return;
            } else { // Then assume it's RT_CYCLE
                movingForward = false;
                curTime = this.getMaxTime();
            }
        } else if (curTime < this.getMinTime()) {
            if (this.getRepeatType() == Controller.RT_WRAP) {
                curTime = this.getMaxTime();
                curFrame = 0;
            } else if (this.getRepeatType() == Controller.RT_CLAMP) {
                return;
            } else { // Then assume it's RT_CYCLE
                movingForward = true;
                curTime = this.getMinTime();
            }
        }

    	nextFrame = curFrame+1;
    	
        if (curTime >  keyframes.get(curFrame).time) {
            if (curTime <  keyframes.get(curFrame + 1).time) {
            	nextFrame = curFrame+1;
                return;
            }
            
            for (; curFrame < keyframes.size() - 1; curFrame++) {
                if (curTime <=  keyframes.get(curFrame + 1).time) {
                		nextFrame = curFrame+1;
                        return;
                }
            }
            
            // This -should- be unreachable because of the above
            curTime = this.getMinTime();
            curFrame = 0;
            nextFrame = curFrame+1;
            return;            
        } 
            
        for (; curFrame >= 0; curFrame--) {
            if (curTime >=  keyframes.get(curFrame).time) {
            	nextFrame = curFrame+1;
            	return; 
            }
        }
        
        // This should be unreachable because curTime>=0 and
        // keyframes[0].time=0;
        curFrame = 0;
        nextFrame = curFrame+1;
    }

    /**
     * This class defines a point in time that states <code>morphShape</code>
     * should look like <code>newShape</code> at <code>time</code> seconds
     */
    public static class PointInTime implements Serializable, Savable {

        private static final long serialVersionUID = 1L;

        public TriMesh newShape;

        public float time;

        public PointInTime() {}
        
        public PointInTime(float time, TriMesh shape) {
            this.time = time;
            this.newShape = shape;
        }

		public void read(JMEImporter im) throws IOException {
			InputCapsule cap = im.getCapsule(this);
	    	time = cap.readFloat("time", 0);
	    	newShape = (TriMesh)cap.readSavable("newShape", null);
		}

		public void write(JMEExporter ex) throws IOException {
			OutputCapsule cap = ex.getCapsule(this);
	    	cap.write(time, "time", 0);
	    	cap.write(newShape, "newShape", null);
		}
        
        public Class getClassTag() {
            return this.getClass();
        }
    }

    @SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        keyframes = (ArrayList) in.readObject();
        movingForward = true;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (isSmooth)
            out.writeObject(prevKeyframes);
        else
            out.writeObject(keyframes);
    }

    public TriMesh getMorphMesh() {
        return morphMesh;
    }
    
    @Override
    public void write(JMEExporter ex) throws IOException {
    	super.write(ex);
    	OutputCapsule cap = ex.getCapsule(this);
    	cap.write(updatePerFrame, "updatePerFrame", true);
    	cap.write(morphMesh, "morphMesh", null);
    	cap.writeSavableArrayList(keyframes, "keyframes", new ArrayList<PointInTime>());
    }
    
	@Override
    @SuppressWarnings("unchecked")
    public void read(JMEImporter im) throws IOException {
    	super.read(im);
    	InputCapsule cap = im.getCapsule(this);
    	updatePerFrame = cap.readBoolean("updatePerFrame", true);
    	morphMesh = (TriMesh) cap.readSavable("morphMesh", null);
    	keyframes = cap.readSavableArrayList("keyframes", new ArrayList<PointInTime>());
    	movingForward = true;
    }
}
