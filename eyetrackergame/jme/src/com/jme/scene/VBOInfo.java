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
import java.io.Serializable;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>VBOInfo</code> provides a single class for dealing with the VBO
 * characteristics of a Geometry object(s)
 * 
 * @author Joshua Slack
 * @author Tijl Houtbeckers - Support for indices.
 * @version $Id: VBOInfo.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class VBOInfo implements Serializable, Savable {
    private static final long serialVersionUID = 1L;
    private boolean useVBOVertex = false;
	private boolean useVBOTexture = false;
	private boolean useVBOColor = false;
	private boolean useVBONormal = false;
	private boolean useVBOFogCoords = false;
	private boolean useVBOIndex = false;
	private int vboVertexID = -1;
	private int vboColorID = -1;
	private int vboNormalID = -1;
	private int vboFogCoordsID = -1;
	private int[] vboTextureIDs = null;
	private int vboIndexID = -1;

	public VBOInfo() {
	    this(false);
	}
	
	/**
	 * Create a VBOInfo object that sets VBO to enabled or disabled for all 
	 * types except Index, which is always disabled unless set with 
	 * setVBOIndexEnabled(true)
	 * 
	 * @param defaultVBO
	 * 				true for enabled, false for disabled.
	 */
	public VBOInfo(boolean defaultVBO) {
	    useVBOColor = defaultVBO;
	    useVBOTexture = defaultVBO;
	    useVBOVertex = defaultVBO;
	    useVBONormal = defaultVBO;
	    useVBOFogCoords = defaultVBO;
	    useVBOIndex = false;

		vboTextureIDs = new int[2];
	}

	/**
	 * Creates a copy of this VBOInfo. Does not copy any IDs.
	 *
	 * @return a copy of this VBOInfo instance
	 */
	public VBOInfo copy() {
	    VBOInfo copy = new VBOInfo();
	    copy.useVBOVertex = useVBOVertex;
	    copy.useVBOTexture = useVBOTexture;
	    copy.useVBOColor = useVBOColor;
	    copy.useVBONormal = useVBONormal;
	    copy.useVBOIndex = useVBOIndex;
	    copy.useVBOFogCoords = useVBOFogCoords;
	    return copy;
	}
	
    /**
     * <code>resizeTextureIds</code> forces the texid array to be the given
     * size, maintaining any old id values that can fit in the new sized array.
     * size of 0 is ignored.
     * 
     * @param size
     *            new size of texcoord id array
     */
    public void resizeTextureIds(int size) {
        if (vboTextureIDs.length == size || size == 0) return;
        
        int[] newIDArray = new int[size];
        for (int x = Math.min(size, vboTextureIDs.length); --x >= 0; )
            newIDArray[x] = vboTextureIDs[x];
        
        vboTextureIDs = newIDArray;
    }
    
	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for vertex information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for vertexes.
	 */
	public boolean isVBOVertexEnabled() {
		return useVBOVertex;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for fog coords
	 * information. This is used during rendering.
	 * 
	 * @return If VBO is enabled for fog coords.
	 */
	public boolean isVBOFogCoordsEnabled() {
		return useVBOFogCoords;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for indices information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for indices.
	 */
	public boolean isVBOIndexEnabled() {
		return useVBOIndex;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for texture information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for textures.
	 */
	public boolean isVBOTextureEnabled() {
		return useVBOTexture;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for normal information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for normals.
	 */
	public boolean isVBONormalEnabled() {
		return useVBONormal;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for color information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for colors.
	 */
	public boolean isVBOColorEnabled() {
		return useVBOColor;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for vertex information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for vertexes.
	 */
	public void setVBOVertexEnabled(boolean enabled) {
		useVBOVertex = enabled;
	}
	
	/**
	 * Enables or disables Vertex Buffer Objects for fog coords information.
	 * 
	 * @param enabled
	 *            If true, VBO enabled for fog coords.
	 */
	public void setVBOFogCoordsEnabled(boolean enabled) {
		this.useVBOFogCoords = enabled;
	}
	
	/**
	 * Enables or disables Vertex Buffer Objects for indices information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for indices.
	 */
	public void setVBOIndexEnabled(boolean enabled) {
		this.useVBOIndex = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for texture coordinate
	 * information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for texture coordinates.
	 */
	public void setVBOTextureEnabled(boolean enabled) {
		useVBOTexture = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for normal information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for normals
	 */
	public void setVBONormalEnabled(boolean enabled) {
		useVBONormal = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for color information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for colors
	 */
	public void setVBOColorEnabled(boolean enabled) {
		useVBOColor = enabled;
	}

	public int getVBOVertexID() {
		return vboVertexID;
	}

	public int getVBOTextureID(int index) {
        if (index >= vboTextureIDs.length) return -1;
		return vboTextureIDs[index];
	}

	public int getVBONormalID() {
		return vboNormalID;
	}

	public int getVBOFogCoordsID() {
		return vboFogCoordsID;
	}

	public int getVBOColorID() {
		return vboColorID;
	}

	public void setVBOVertexID(int id) {
		vboVertexID = id;
	}

	public void setVBOTextureID(int index, int id) {
		if (index >= vboTextureIDs.length) {
			resizeTextureIds(index+1);
		}
		vboTextureIDs[index] = id;
	}

	public void setVBONormalID(int id) {
		vboNormalID = id;
	}

	public void setVBOFogCoordsID(int id) {
		vboFogCoordsID = id;
	}

	public void setVBOColorID(int id) {
		vboColorID = id;
	}
	
	public int getVBOIndexID() {
		return vboIndexID;
	}

	public void setVBOIndexID(int id) {
		this.vboIndexID = id;
	}

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(useVBOVertex, "useVBOVertex", false);
        capsule.write(useVBOTexture, "useVBOTexture", false);
        capsule.write(useVBOColor, "useVBOColor", false);
        capsule.write(useVBONormal, "useVBONormal", false);
        capsule.write(useVBOFogCoords, "useVBOFogCoords", false);
        capsule.write(useVBOIndex, "useVBOIndex", false);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        useVBOVertex = capsule.readBoolean("useVBOVertex", false);
        useVBOTexture = capsule.readBoolean("useVBOTexture", false);
        useVBOColor = capsule.readBoolean("useVBOColor", false);
        useVBONormal = capsule.readBoolean("useVBONormal", false);
        useVBOFogCoords = capsule.readBoolean("useVBOFogCoords", false);
        useVBOIndex = capsule.readBoolean("useVBOIndex", false);
    }

    public Class<? extends VBOInfo> getClassTag() {
        return this.getClass();
    }
}
