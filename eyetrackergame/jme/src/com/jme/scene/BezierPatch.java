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
import com.jme.util.export.Savable;

/**
 * <code>BezierPatch</code> defines a 4x4 mesh of control points. The patch
 * will be enough to generate a single section of a <code>BezierMesh</code>.
 * The detail level of the patch determines the smoothness of the resultant
 * <code>BezierMesh</code>.
 * 
 * @author Mark Powell
 * @version $Id: BezierPatch.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class BezierPatch implements Savable {
	private Vector3f[][] anchors;

	private int detailLevel;

	/**
	 * Constructor instantiates a new <code>BezierPatch</code> with a default
	 * empty control point mesh and a detail level of zero.
	 *  
	 */
	public BezierPatch() {
		anchors = new Vector3f[4][4];
		detailLevel = 0;
	}

	/**
	 * Constructor instantiates a new <code>BezierPatch</code> with a given
	 * control point grid and a default detail level of zero.
	 * 
	 * @param anchors
	 *            the control points that make up the patch.
	 */
	public BezierPatch(Vector3f[][] anchors) {
		if (anchors.length != 4 || anchors[0].length != 4) {
			throw new JmeException("Bezier patch anchors must be 4x4.");
		}

		this.anchors = anchors;
		detailLevel = 0;
	}

	/**
	 * Constructor instantiates a new <code>BezierPatch</code> with a given
	 * control point grid and a given detail level.
	 * 
	 * @param anchors
	 *            the control points that make up the patch.
	 * @param detailLevel
	 *            the detail level of the patch.
	 */
	public BezierPatch(Vector3f[][] anchors, int detailLevel) {
		if (anchors.length != 4 || anchors[0].length != 4) {
			throw new JmeException("Bezier patch anchors must be 4x4.");
		}

		this.anchors = anchors;
		this.detailLevel = detailLevel;
	}

	/**
	 * 
	 * <code>setAnchors</code> sets the control anchors of this patch.
	 * 
	 * @param anchors
	 *            the control anchors of this patch.
	 */
	public void setAnchors(Vector3f[][] anchors) {
		if (anchors.length != 4 || anchors[0].length != 4) {
			throw new JmeException("Bezier patch anchors must be 4x4.");
		}

		this.anchors = anchors;
	}

	/**
	 * 
	 * <code>getAnchors</code> returns the control anchors that make up this
	 * patch.
	 * 
	 * @return the control anchors of this patch.
	 */
	public Vector3f[][] getAnchors() {
		return anchors;
	}

	/**
	 * 
	 * <code>setAnchor</code> sets a single anchor of the patch.
	 * 
	 * @param i
	 *            the i index (row).
	 * @param j
	 *            the j index (column).
	 * @param anchor
	 *            the control anchor for this point.
	 */
	public void setAnchor(int i, int j, Vector3f anchor) {
		if ((i < 0 || i > 4) || (j < 0 || j > 4)) {
			throw new JmeException("Bezier Patch anchor out of bounds.");
		}

		anchors[i][j] = anchor;
	}

	/**
	 * 
	 * <code>getAnchor</code> returns a single control anchor of a given (i,
	 * j) of the patch.
	 * 
	 * @param i
	 *            the i index (row).
	 * @param j
	 *            the j index (column).
	 * @return the control anchor of the given i,j.
	 */
	public Vector3f getAnchor(int i, int j) {
		if ((i < 0 || i > 4) || (j < 0 || j > 4)) {
			throw new JmeException("Bezier Patch anchor out of bounds.");
		}

		return anchors[i][j];
	}

	/**
	 * 
	 * <code>setDetailLevel</code> sets the detail level of this patch.
	 * 
	 * @param detailLevel
	 *            the detail level of this patch.
	 */
	public void setDetailLevel(int detailLevel) {
		this.detailLevel = detailLevel;
	}

	/**
	 * 
	 * <code>getDetailLevel</code> retrieves the detail level of this patch.
	 * 
	 * @return the detail level of this patch.
	 */
	public int getDetailLevel() {
		return detailLevel;
	}

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(anchors, "anchors", new Vector3f[4][4]);
        capsule.write(detailLevel, "detailLevel", 0);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        Savable[][] savs = capsule.readSavableArray2D("anchors", new Vector3f[4][4]);
        if(savs != null) {
            for(int i = 0; i < savs.length; i++) {
                for(int j = 0; j < savs[i].length; j++) {
                    anchors[i][j] = (Vector3f)savs[i][j];
                }
            }
        }
        
        detailLevel = capsule.readInt("detailLevel", 0);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}