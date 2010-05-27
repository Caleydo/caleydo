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

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

public class TextureAnimationController extends Controller {

	private static final long serialVersionUID = -3669101429356423878L;

	private Texture[] textures;
	
	private Vector3f translationDelta;
	private float rotationDelta;
	private Vector3f textureRotationAxis;
	private float xRepeat;
	private float yRepeat;
	private float zRepeat;
	
	private Vector3f currentTranslation;
	private float currentRotationAngle;
	private Quaternion currentRotation;
	
	public TextureAnimationController() {}
	
	public TextureAnimationController(TextureState ts) {
		textures = new Texture[ts.getNumberOfSetTextures()];
		for(int i = 0, count = 0; i < TextureState.getNumberOfTotalUnits(); i++) {
			Texture t = ts.getTexture(i);
			if(t != null) {
				textures[count++] = t;
			}
		}
		initializeValues();
	}
	
	public TextureAnimationController(Texture... t) {
		textures = t;
		
		initializeValues();
	}
	
	private void initializeValues() {
		currentTranslation = new Vector3f();
		currentRotation = new Quaternion();
		translationDelta = new Vector3f();
		textureRotationAxis = new Vector3f();
		textureRotationAxis.z = 1;
		xRepeat = 1;
		yRepeat = 1;
		zRepeat = 1;
	}
	
	@Override
	public void update(float time) {
		if(isActive()) {
			if(getRepeatType() == Controller.RT_WRAP) {
				if(translationDelta.x != 0 || translationDelta.y != 0 || translationDelta.z != 0) {
					currentTranslation.x += getSpeed() * time * translationDelta.x;
					currentTranslation.y += getSpeed() * time * translationDelta.y;
			        currentTranslation.z += getSpeed() * time * translationDelta.z;

			        if(currentTranslation.x > xRepeat || currentTranslation.x < (-1 * xRepeat)) {
			        	currentTranslation.x = 0;
			        } 
			        
			        if(currentTranslation.y > yRepeat || currentTranslation.y < (-1 * yRepeat)) {
			        	currentTranslation.y = 0;
			        }
			        
			        if(currentTranslation.z > zRepeat || currentTranslation.z < (-1 * zRepeat)) {
			        	currentTranslation.z = 0;
			        }
			        
				}
		        
				if(rotationDelta != 0) {
					currentRotationAngle += rotationDelta * getSpeed() * time;
					if(currentRotationAngle > FastMath.TWO_PI) {
						currentRotationAngle = 0;
					} else if(currentRotationAngle < 0) {
						currentRotationAngle = FastMath.TWO_PI;
					}
				    currentRotation.fromAngleNormalAxis(currentRotationAngle, 
				    		textureRotationAxis);
				}
			} else if(getRepeatType() == Controller.RT_CLAMP) {
				if(translationDelta.x != 0 || translationDelta.y != 0 || translationDelta.z != 0) {
					if(currentTranslation.x <= xRepeat && currentTranslation.x >= (-1 * xRepeat)) {
			        	currentTranslation.x += getSpeed() * time * translationDelta.x;
			        } 
			        
			        if(currentTranslation.y <= yRepeat && currentTranslation.y >= (-1 * yRepeat)) {
			        	currentTranslation.y += getSpeed() * time * translationDelta.y;
			        }
			        
			        if(currentTranslation.z <= zRepeat && currentTranslation.z >= (-1 * zRepeat)) {
			        	currentTranslation.z += getSpeed() * time * translationDelta.z;
			        }
			        
				}
		        
				if(rotationDelta != 0) {
					if(currentRotationAngle <= FastMath.TWO_PI) {
						currentRotationAngle += rotationDelta * getSpeed() * time;
						currentRotation.fromAngleNormalAxis(currentRotationAngle, 
				    		textureRotationAxis);
					}
				}
			} else if(getRepeatType() == Controller.RT_CYCLE) {
				if(translationDelta.x != 0 || translationDelta.y != 0 || translationDelta.z != 0) {
					if(currentTranslation.x > xRepeat || currentTranslation.x < (-1 * xRepeat)) {
						translationDelta.x *= -1;
			        } 
			        
			        if(currentTranslation.y > yRepeat || currentTranslation.y < (-1 * yRepeat)) {
			        	translationDelta.y *= -1;
			        }
			        
			        if(currentTranslation.z > zRepeat || currentTranslation.z < (-1 * zRepeat)) {
			        	translationDelta.z *= -1;
			        }
			        
			        currentTranslation.x += getSpeed() * time * translationDelta.x;
					currentTranslation.y += getSpeed() * time * translationDelta.y;
			        currentTranslation.z += getSpeed() * time * translationDelta.z;
			        
				}
		        
				if(rotationDelta != 0) {
					if(currentRotationAngle > FastMath.TWO_PI || currentRotationAngle < 0) {
						rotationDelta *= -1;
					}

					currentRotationAngle += rotationDelta * getSpeed() * time;
				    currentRotation.fromAngleNormalAxis(currentRotationAngle, 
				    		textureRotationAxis);
				}
			}

		    for(int i = 0; i < textures.length; i++) {
		    	textures[i].setTranslation(currentTranslation);
				textures[i].setRotation(currentRotation);
			}
		}
	}

	public float getRotationDelta() {
		return rotationDelta;
	}

	public void setRotationDelta(float rotationDelta) {
		this.rotationDelta = rotationDelta;
	}

	public Vector3f getTranslationDelta() {
		return translationDelta;
	}

	public void setTranslationDelta(Vector3f translationDelta) {
		this.translationDelta = translationDelta;
	}

	public float getXRepeat() {
		return xRepeat;
	}

	public void setXRepeat(float repeat) {
		xRepeat = repeat;
	}

	public float getYRepeat() {
		return yRepeat;
	}

	public void setYRepeat(float repeat) {
		yRepeat = repeat;
	}

	public float getZRepeat() {
		return zRepeat;
	}

	public void setZRepeat(float repeat) {
		zRepeat = repeat;
	}

	public Vector3f getTextureRotationAxis() {
		return textureRotationAxis;
	}

	public void setTextureRotationAxis(Vector3f textureRotationAxis) {
		this.textureRotationAxis = textureRotationAxis;
	}
	
	@Override
	public void write(JMEExporter e) throws IOException {
		super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(translationDelta, "translationDelta", null);
        capsule.write(rotationDelta, "rotationDelta", 0);
        capsule.write(textureRotationAxis, "textureRotationAxis", null);
        capsule.write(xRepeat, "xRepeat", 1);
        capsule.write(yRepeat, "yRepeat", 1);
        capsule.write(zRepeat, "zRepeat", 1);
        capsule.write(currentTranslation, "currentTranslation", null);
        capsule.write(currentRotation, "currentRotation", null);
        capsule.write(currentRotationAngle, "currentRotationAngle", 0);
        capsule.write(textures, "textures", null);
	}
	
	@Override
    public void read(JMEImporter e) throws IOException {
		super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        translationDelta = (Vector3f)capsule.readSavable("translationDelta", null);
        rotationDelta = capsule.readFloat("rotationDelta", 0);
        textureRotationAxis = (Vector3f)capsule.readSavable("textureRotationAxis", null);
        xRepeat = capsule.readFloat("xRepeat", 1);
        yRepeat = capsule.readFloat("yRepeat", 1);
        zRepeat = capsule.readFloat("zRepeat", 1);
        currentTranslation = (Vector3f)capsule.readSavable("currentTranslation", null);
        currentRotation = (Quaternion)capsule.readSavable("currentRotation", null);
        currentRotationAngle = capsule.readFloat("currentRotationAngle", 0);
        Savable[] savs = capsule.readSavableArray("textures", null);
        if(savs != null) {
        	textures = new Texture[savs.length];
        	for(int i = 0; i < savs.length; i++) {
        		textures[i] = (Texture)savs[i];
        	}
        }
    }
	
	@Override
    public Class getClassTag() {
        return this.getClass();
    }

}
