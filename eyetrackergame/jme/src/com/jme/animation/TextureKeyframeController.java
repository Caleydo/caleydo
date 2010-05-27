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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.math.Matrix4f;
import com.jme.scene.Controller;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;


/**
 * 
 * TextureKeyframeController applies transform matrices to the texture matrix
 * based on keyframe times. Allow for highly controllable texture animation. 
 * Iterpolation can be used to allow flowing animation (lava flows) or turned 
 * off for a "flip book" animation (similar to gif animations).
 * 
 * @author Mark Powell
 *
 */
public class TextureKeyframeController extends Controller {
    private static final Logger logger = Logger.getLogger(TextureKeyframeController.class.getName());
	
	public static final int IT_STEP = 0;
	public static final int IT_LINEAR = 1;
	
	private static final long serialVersionUID = 1L;
	private float[] times;
	private int[] interp;
	private Matrix4f[] transforms;
	private Texture texture;
	
	private int index;
	private int maxIndex;
	private float currentTime;
	private boolean increment = true;

	private static Matrix4f workMat = new Matrix4f();
	private static Matrix4f workMat2 = new Matrix4f();
	
	
	public TextureKeyframeController() {}
	
	public TextureKeyframeController(Texture texture) {
		this.texture = texture;
	}
	
	public void addData(float[] times, Matrix4f[] transforms, int[] interp) {
		if(times.length != transforms.length) {
			logger.log(Level.WARNING, "Invalid texture keyframe information."
                    + " Times and transforms are not of same length."
                    + " [{0} != {1}", new Integer[] {times.length, transforms.length});
			return;
		}
		
		this.times = times;
		this.interp = interp;
		this.transforms = transforms;
		maxIndex = times.length - 1;
		currentTime = times[0];
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	@Override
	public void update(float time) {
		if(texture == null) {
			return;
		}
		if(increment) {
			//check next frame time to see if currentTime is >=
			//need to make sure that there *is* a next frame.
			if(index == maxIndex) {
				//Wrap check 0
				if(getRepeatType() == Controller.RT_WRAP) {
					currentTime = 0;
					index = 0;
					texture.setMatrix(transforms[index]);
				} else if(getRepeatType() == Controller.RT_CYCLE) {
					//Cycle set increment to false
					increment = false;		
					this.update(time);
				}
			} else {
				currentTime += time * getSpeed();

				if (currentTime >= times[index + 1]) {
					index++;
					texture.setMatrix(transforms[index]);
				} else {
					if (interp[index] == IT_LINEAR) {
						float change = (currentTime - times[index])
								/ (times[index + 1] - times[index]);
						workMat = transforms[index].mult(1 - change, workMat);
						workMat2 = transforms[index + 1].mult(change, workMat2);
						workMat.addLocal(workMat2);
						texture.setMatrix(workMat);
					}
				}
			}
		} else {
			if(index == 0) {
				if(getRepeatType() == Controller.RT_WRAP) {
					currentTime = times[maxIndex];
					index = maxIndex;
				} else if(getRepeatType() == Controller.RT_CYCLE) {
					increment = true;
				}
			} else {
				currentTime -= time * getSpeed();
				if(currentTime <= times[index-1]) {
					index--;
					texture.setMatrix(transforms[index]);
				} else if(interp[index] == IT_LINEAR) {
					float change = (currentTime-times[index])/(times[index-1] - times[index]);
					workMat = transforms[index].mult(1 - change, workMat);
					workMat2 = transforms[index-1].mult(change, workMat2);
					workMat.addLocal(workMat2);
					texture.setMatrix(workMat);
				}
			}
		}
	}
	
	@Override
	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(times, "times", null);
		capsule.write(interp, "interp", null);
		capsule.write(transforms, "transforms", null);
		capsule.write(texture, "texture", null);
		capsule.write(maxIndex, "maxIndex", 0);
	}
	
	@Override
    public void read(JMEImporter e) throws IOException {
		super.read(e);
		InputCapsule capsule = e.getCapsule(this);
		times = capsule.readFloatArray("times", null);
		interp = capsule.readIntArray("interp", null);
		
		Savable[] savs = capsule.readSavableArray("transforms", null);
		
		if (savs == null)
            transforms = null;
        else {
            transforms = new Matrix4f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                transforms[x] = (Matrix4f) savs[x];
            }
        }
		
		texture = (Texture)capsule.readSavable("texture", null);
		maxIndex = capsule.readInt("maxIndex", 0);
    }
	
	@Override
    public Class getClassTag() {
        return this.getClass();
    }
}
