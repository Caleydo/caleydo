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
package com.jmex.font3d.math;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.math.Vector3f;



/**
 * This class represents as its name indicates a planar subdivision.
 * 
 * Its uses are many, but to name a few, its good for triangulation of complex polygons 
 * (those with holes in them....).
 * 
 * To make a triangulation it is assumed that some subset of the edges form a closed polygon around
 * the rest of the triangulation. These points must be connected in counter-clockwise order,
 * that is the interior of the polygon they form lies to the left of every edge in it.
 * The internal representation of the planar subdivision does allow though to traverse the edges backwards,
 * since we use DCEL representation.
 * 
 * NOTE:
 *  - At the moment the planar subdivision does not accept anything but 1 or two manifold vertices. If you
 *    add more edges than that, stuff will break :-)
 * 
 * @author emanuel
 *
 */
public abstract class DoublyConnectedEdgeList<Vertex extends PlanarVertex, Edge extends PlanarEdge>
{
    private static final Logger logger = Logger
            .getLogger(DoublyConnectedEdgeList.class.getName());
    
	// These are the points in the glyph
	ArrayList<Vertex> vertices  = new ArrayList<Vertex>();
	// These are the edges of the glyph
	ArrayList<Edge> edges       = new ArrayList<Edge>();

	public abstract Vertex createVertex(int index, Vector3f p);
	public abstract Edge   createEdge(Vertex origin, boolean real);

	public Vertex addVertex(Vector3f p)
	{
		Vertex point = createVertex(vertices.size(), p);
		vertices.add(point);
		return point;
	}
	
	public Edge addEdge(int src_i, int dst_i)
	{
		Vertex src = vertices.get(src_i);
		Vertex dst = vertices.get(dst_i);
		
		// Test that the edge does not already exist
		Edge src_e = (Edge) src.getEdge(dst);
		boolean new_src_e = false;
		if(src_e == null)
		{
			src_e = createEdge(src, true);
			new_src_e = true;
		}
		else
		{
			src_e.realedge = true;
			logger.info("Added an duplicate edge: ("+src_i+" -> "+dst_i+")");
			//throw new RuntimeException("POWER UP !!!");
		}
		Edge dst_e = (Edge) dst.getEdge(src);
		boolean new_dst_e = false;
		if(dst_e == null)
		{
			dst_e = createEdge(dst, false);
			new_dst_e = true;
		}
		else
		{
			logger.info("Added a duplicate edge (TWIN): ("+dst_i+" -> "+src_i+")");
		}
		
		// Bind the two half-edges
		src_e.setTwin(dst_e);
		// Bind that edge as the outgoing from where they are outgoing
		if(new_src_e)
			src.addOutgoingEdge(src_e);
		else if(new_dst_e)
			throw new RuntimeException("Damng, created a twin to an existing edge, that can never happen");
		//if(new_dst_e)
		//	dst.addOutgoingEdge(dst_e);
		
		//logger.info("Added edge: "+src_i+" -> "+dst_i);
		// Only add the "forward edge" to make sure we can get the original orientation.
		edges.add(src_e);
		edges.add(dst_e);
		
		return src_e;
	}
	
	public ArrayList<Vertex> getVertices()
	{
		return vertices;
	}
}
