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
package com.jmex.font3d.effects;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.geom.BufferUtils;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Glyph3D;
import com.jmex.font3d.Glyph3DMesh;

public class Font3DGradient implements Font3DEffect
{
	private Vector3f direction = new Vector3f();
	ColorRGBA start_color = new ColorRGBA();
	ColorRGBA end_color = new ColorRGBA();
	
	public Font3DGradient()
	{
		this(Vector3f.UNIT_Y.clone(), ColorRGBA.white.clone(), ColorRGBA.red.clone());
	}
	
	public Font3DGradient(Vector3f direction, ColorRGBA start_color, ColorRGBA end_color)
	{
		this.direction.set(direction);
		this.start_color.set(start_color);
		this.end_color.set(end_color);
	}

	public void applyEffect(Font3D font)
	{
		boolean mesh_locked = font.isMeshLocked();
		if(mesh_locked)
		{
    		font.unlockMesh();
		}
		
		// We must add a material-state to use lighting and vertex-colours at the same time
		font.enableDiffuseMaterial();
		// does any of these contain any alpha ?
		if(start_color.a != 1 || end_color.a != 1)
		{
			font.enableBlendState();
		}
		
		// Get the min and max
		for(Glyph3D g : font.getGlyphs())
		{
			if(g != null && g.getMesh() != null)
			{
				applyEffect(g.getMesh());
			}
		}
		
		
		// If it was locked, lock it again.
    	if(mesh_locked)
    	{
    		font.lockMesh();
    	}
	}

	private void applyEffect(Glyph3DMesh mesh)
	{
		// Calculate the max/min of the vertices in the mesh.
		Vector3f max = null,min = null;
		Vector3f[] verts = BufferUtils.getVector3Array(mesh.getVertexBuffer());
		for(Vector3f v : verts)
		{
			if(max == null || direction.dot(v) > direction.dot(max))
			{
				max = v;
			}
			if(min == null || direction.dot(v) < direction.dot(min))
			{
				min = v;
			}
		}
		float max_dot = direction.dot(max), min_dot = direction.dot(min);
		float dot_dist = max_dot - min_dot;
		
		// Create a color-array
		int color_pos = 0;
		ColorRGBA[] colors = new ColorRGBA[verts.length];
		
		// Iterate through all the vertices and create the colours
		for(Vector3f v : verts)
		{
			float dot_val = direction.dot(v);
			ColorRGBA c = colors[color_pos] = new ColorRGBA(start_color);
			c.interpolate(end_color, (dot_val-min_dot)/dot_dist);
			//logger.info("c:"+c);
			color_pos++;
		}
		
		// Apply the colors
		mesh.setColorBuffer(BufferUtils.createFloatBuffer(colors));
	}
}
