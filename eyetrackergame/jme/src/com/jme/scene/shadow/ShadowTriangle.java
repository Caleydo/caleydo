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

package com.jme.scene.shadow;

import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>ShadowTriangle</code> A class that holds the edge information of a
 * single face (triangle) of an occluder
 * 
 * @author Mike Talbot (some code from a shadow implementation written Jan 2005)
 * @author Joshua Slack
 * @version $Id: ShadowTriangle.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class ShadowTriangle implements Savable {

    /**
     * <code>INVALID_TRIANGLE</code> (int) indicates that an edge is not
     * connected
     */
    public final static int INVALID_TRIANGLE = -1;

    // The edges of the triangle
    public ShadowEdge edge1 = null;
    public ShadowEdge edge2 = null;
    public ShadowEdge edge3 = null;

    public ShadowTriangle() {
        edge1 = new ShadowEdge(0, 0);
        edge2 = new ShadowEdge(0, 0);
        edge3 = new ShadowEdge(0, 0);
    }

	public void write(JMEExporter e) throws IOException {
		OutputCapsule cap = e.getCapsule(this);
		cap.write(edge1, "edge1", new ShadowEdge(0, 0));
		cap.write(edge2, "edge2", new ShadowEdge(0, 0));
		cap.write(edge3, "edge3", new ShadowEdge(0, 0));
	}

	public void read(JMEImporter e) throws IOException {
		InputCapsule cap = e.getCapsule(this);
		edge1 = (ShadowEdge)cap.readSavable("edge1", new ShadowEdge(0, 0));
		edge2 = (ShadowEdge)cap.readSavable("edge2", new ShadowEdge(0, 0));
		edge3 = (ShadowEdge)cap.readSavable("edge3", new ShadowEdge(0, 0));
	}
    
    public Class getClassTag() {
        return this.getClass();
    }
}
