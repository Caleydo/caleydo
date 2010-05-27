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

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

/**
 * <code>ShadowEdge</code>
 * Holds the indices of two points that form an edge in a ShadowTriangle
 * 
 * @author Mike Talbot (some code from a shadow implementation written Jan 2005)
 * @author Joshua Slack
 * @version $Id: ShadowEdge.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class ShadowEdge implements Savable {
    /**
     * <code>triangle</code> (int) the triangle number (in an occluder) to
     * which the edge is connected or INVALID_TRIANGLE if not connected.
     */
    public int triangle = ShadowTriangle.INVALID_TRIANGLE;
    
    /** The indices of the two points comprising this edge. */
    public int p0, p1;

    /**
     * @param p0 the first point
     * @param p1 the second point
     */
    public ShadowEdge(int p0, int p1) {
        this.p0 = p0;
        this.p1 = p1;
    }

	public void write(JMEExporter e) throws IOException {
		e.getCapsule(this).write(p0, "p0", 0);
		e.getCapsule(this).write(p1, "p1", 0);
		e.getCapsule(this).write(triangle, "triangle", ShadowTriangle.INVALID_TRIANGLE);
	}

	public void read(JMEImporter e) throws IOException {
		p0 = e.getCapsule(this).readInt("p0", 0);
		p1 = e.getCapsule(this).readInt("p1", 0);
		triangle = e.getCapsule(this).readInt("triangle", ShadowTriangle.INVALID_TRIANGLE);
	}
    
    public Class getClassTag() {
        return this.getClass();
    }
}
