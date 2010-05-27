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

package com.jme.scene;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>DistanceSwitchModel</code> defines a <code>SwitchModel</code> for
 * selecting a child node based on the current distance from the containing node
 * to the camera. This can be used with the <code>DiscreteLodNode</code>
 * subclass of <code>SwitchNode</code> to all the detail level to decrease as
 * the camera travels further away from the object. The number of children to
 * switch between is provided and the distances are also set. So, each child
 * would have a minimum distance and a maximum distance. The child selected is
 * the one that the camera to model distance is between the a particular child's
 * min and max. If no values are valid, SN_INVALID_CHILD is returned.
 * 
 * @author Mark Powell
 * @version $Id: DistanceSwitchModel.java,v 1.2 2004/03/13 18:07:56 mojomonkey
 *          Exp $
 */
public class DistanceSwitchModel implements SwitchModel {

	private float[] modelMin;

	private float[] modelMax;

	private float[] worldMin;

	private float[] worldMax;

	private int numChildren;

	private float worldScaleSquared;

	private Vector3f diff;

    public DistanceSwitchModel() {}
    
	/**
	 * Constructor instantiates a new <code>DistanceSwitchModel</code> object
	 * with the number of children to select from.
	 * 
	 * @param numChildren
	 *            the number of children this model selects from.
	 */
	public DistanceSwitchModel(int numChildren) {
		this.numChildren = numChildren;
		modelMin = new float[numChildren];
		modelMax = new float[numChildren];
		worldMin = new float[numChildren];
		worldMax = new float[numChildren];
	}

	/**
	 * Returns the number of children that distances exist for.
	 * @return
	 */
	public	int		getNumChildren() {
		return numChildren;
	}
	
	/**
	 * Gets the minimum distance for this spatial index.
	 * @param index
	 * @return
	 */
	public	float		getModelMinDistance(int index) {
		return modelMin[index];
	}
	
	/**
	 * Gets the maximum distance for this spatial index.
	 * @param index
	 * @return
	 */
	public	float		getModelMaxDistance(int index) {
		return modelMax[index];
	}
	
	/**
	 * 
	 * <code>setModelMinDistance</code> sets the minimum distance that a
	 * particular child should be used.
	 * 
	 * @param index
	 *            the index of the child.
	 * @param minDist
	 *            the minimum of this child.
	 */
	public void setModelMinDistance(int index, float minDist) {
		if(index >= numChildren) {
			reallocateArrays(index + 1);
		}
		
		modelMin[index] = minDist;
	}

	/**
	 * Creates larger arrays for the max and mins while copying existing data.
	 * @param newLength
	 */
	private	void	reallocateArrays(int newLength) {
		// create the new arrays
		float	modelMinNew[] = new float[newLength];
		float	modelMaxNew[] = new float[newLength];
		float	worldMinNew[] = new float[newLength];
		float	worldMaxNew[] = new float[newLength];
		
		// copy in existing
		for(int i = 0; i < numChildren; i++) {
			modelMinNew[i] = modelMin[i];
			modelMaxNew[i] = modelMax[i];
			worldMinNew[i] = worldMin[i];
			worldMaxNew[i] = worldMax[i];
		}
		
		// reassign
		modelMin = modelMinNew;
		modelMax = modelMaxNew;
		worldMin = worldMinNew;
		worldMax = worldMaxNew;
		numChildren = newLength;
	}
	/**
	 * 
	 * <code>setModelMaxDistance</code> sets the maximum distance that a
	 * particular child should be used.
	 * 
	 * @param index
	 *            the index of the child.
	 * @param maxDist
	 *            the maximum of this child.
	 */
	public void setModelMaxDistance(int index, float maxDist) {
		if(index >= numChildren) {
			reallocateArrays(index + 1);
		}
		
		modelMax[index] = maxDist;
	}

	/**
	 * 
	 * <code>setModelDistance</code> sets the minimum and maximum distance
	 * that a particular child should be used.
	 * 
	 * @param index
	 *            the index of the child.
	 * @param minDist
	 *            the minimum of this child.
	 * @param maxDist
	 *            the maximum of this child.
	 */
	public void setModelDistance(int index, float minDist, float maxDist) {
		if(index >= numChildren) {
			reallocateArrays(index + 1);
		}
		
		modelMin[index] = minDist;
		modelMax[index] = maxDist;
	}

	/**
	 * <code>set</code> accepts Float and Vector3f objects to set the
	 * properties of the distance switch model. If the value passed is a Float
	 * object, this value is used to determine the world scale (squared) value,
	 * which allows the adjustment of the min and max distances for switching.
	 * If the value passed is a Vector3f, that is used to set the difference of
	 * the switch node and a comparison point which is typically the camera
	 * location.
	 * 
	 * @param value
	 *            either Float - the world scale squared value, or Vector3f -
	 *            the difference between the switch node and a location.
	 */
	public void set(Object value) {
		if (value instanceof Float) {

			worldScaleSquared = ((Float) value).floatValue();

			for (int i = 0; i < numChildren; i++) {
				worldMin[i] = worldScaleSquared * modelMin[i] * modelMin[i];
				worldMax[i] = worldScaleSquared * modelMax[i] * modelMax[i];
			}
		} else if (value instanceof Vector3f) {
			diff = (Vector3f) value;
		} else {
			throw new JmeException("Invalid value for set method.");
		}
	}

	/**
	 * <code>getSwitchChild</code> returns the index of the child that should
	 * be switched on. The current distance between the parent switch node and a
	 * supplied point is used to determine the valid child.
	 * 
	 * @return the index of the valid child.
	 */
	public int getSwitchChild() {
		// select the switch child
		if (numChildren > 0) {
			float sqrDistance = diff.lengthSquared();

			for (int i = 0; i < numChildren; i++) {
				if (worldMin[i] <= sqrDistance && sqrDistance < worldMax[i]) {
					return i;
				}
			}
		}

		return SwitchNode.SN_INVALID_CHILD;
	}
    
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        
        capsule.write(modelMin, "modelMin", new float[0]);
        capsule.write(modelMax, "modelMax", new float[0]);
        capsule.write(worldMin, "worldMin", new float[0]);
        capsule.write(worldMax, "worldMax", new float[0]);
        capsule.write(numChildren, "numChildren", 0);
        capsule.write(worldScaleSquared, "worldScaleSquared", 0);
        capsule.write(diff, "diff", Vector3f.ZERO);
    }
    
    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        
        modelMin = capsule.readFloatArray("modelMin", new float[0]);
        modelMax = capsule.readFloatArray("modelMax", new float[0]);
        worldMin = capsule.readFloatArray("worldMin", new float[0]);
        worldMax = capsule.readFloatArray("worldMax", new float[0]);
        numChildren = capsule.readInt("numChildren", 0);
        worldScaleSquared = capsule.readFloat("worldScaleSquared", 0);
        diff = (Vector3f)capsule.readSavable("diff", Vector3f.ZERO.clone());
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}