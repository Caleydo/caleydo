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

import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;


/**
 * Used to do a triangulation of a complex polygon.
 * Please note that you should make sure all of these vertices are two-manifold, 
 * if they are not the triangulation will fail with nullpointers.
 * 
 * @author emanuel
 */
public class TriangulationVertex extends PlanarVertex
{
    private static final Logger logger = Logger
            .getLogger(TriangulationVertex.class.getName());
    
	// Easy access pointers
	//TriangulationVertex prev_vert,next_vert;
	//TriangulationEdge   ingoing_edge,outgoing_edge;
	
	public enum VertexType
	{
		START,
		END,
		SPLIT,
		MERGE,
		REGULAR_RIGHT,
		REGULAR_LEFT,
		UNSET
	}
	VertexType vert_type = VertexType.UNSET;
	public boolean is_left_chain = false;
	
	TriangulationVertex(int i, Vector3f p)
	{
		super(i, p);
	}
	
	boolean yLessThan(PlanarVertex vertex)
	{
		return (point.y == vertex.point.y ? point.x > vertex.point.x : point.y < vertex.point.y);
	}
	
	VertexType getType()
	{
		if(vert_type == VertexType.UNSET)
		{
			logger.info("VertexType not set!");
		}
		return vert_type;
	}
	
	@Override
	public String toString()
	{
		return "[indx:"+index+",("+point.x+","+point.y+"),type:"+vert_type+"]";
	}

	public void initializeType()
	{
		// Find the previous and next vertex
		TriangulationEdge outgoing_edge = getOutGoingEdge();
		TriangulationEdge ingoing_edge  = getInGoingEdge();
		TriangulationVertex prev_vert = (TriangulationVertex) ingoing_edge.getOrigin();
		TriangulationVertex next_vert = (TriangulationVertex) outgoing_edge.getTwin().getOrigin();
		
		if(prev_vert.yLessThan(this) && next_vert.yLessThan(this))
		{
			// Are we at the top but are we start/split
			Vector3f v1 = prev_vert.point;
			Vector3f v2 = point;
			Vector3f v  = next_vert.point;
			float turnang = (v2.x - v1.x) * (v.y - v1.y) - (v.x - v1.x) * (v2.y - v1.y);
			if(turnang > 0)
			{
				vert_type = VertexType.START;
			}
			else
			{
				vert_type = VertexType.SPLIT;
			}
		}
		else if(yLessThan(prev_vert) && yLessThan(next_vert))
		{
			// We are at the bottom, but are we end/merge ?
			Vector3f v1 = prev_vert.point;
			Vector3f v2 = point;
			Vector3f v  = next_vert.point;
			float turnang = (v2.x - v1.x) * (v.y - v1.y) - (v.x - v1.x) * (v2.y - v1.y);
			if(turnang > 0)
			{
				vert_type = VertexType.END;
			}
			else
			{
				vert_type = VertexType.MERGE;
			}
		}
		else if(prev_vert.yLessThan(this))
		{
			// Regular on the right side
			vert_type = VertexType.REGULAR_RIGHT;
		}
		else if(next_vert.yLessThan(this))
		{
			// Regular on the right side
			vert_type = VertexType.REGULAR_LEFT;
		}
		else
		{
			logger.info("PNIX: we are none of the above types !!!!");
			logger.info("GetType: (prev:"+prev_vert+",this:"+this+",next:"+next_vert);
		}
	}

	public boolean checkAllEdges()
	{
		// This one is used for sanity (HACK: hardcoded value)
		int sanity_check = 10000;
		int edgecount = 0;
		
		// Walk around our-selves with tmp = outgoing; tmp = tmp.prev.twin (clockwise)
		PlanarEdge tmp = getFirstEdge();
		float anglesum = 0;
		float anglimit = FastMath.TWO_PI+FastMath.FLT_EPSILON*2;
		edgecount = 0;
		if(tmp != null)
		{
			sanity_check = 10000;
			do
			{
				// Test that tmp has us as origin
				if(tmp.getOrigin() != this)
				{
					throw new GeometricException("edge "+tmp+" does not have a correct origin");
				}
				// Test surface links (clockwise)
				PlanarEdge tmp2 = tmp;
				//String debugString = "[";
				do
				{
					//debugString += " -> "+tmp2;
					if(tmp2.isRealEdge() != tmp2.getPrev().isRealEdge())
					{
						//logger.info("VERT: "+tmp2.getOrigin());
						logger.info("Edge1:"+tmp2);
						logger.info("Edge2:"+tmp2.getPrev());
						//logger.info("Tour: "+debugString+" -> "+tmp2.getPrev());
						throw new GeometricException("Bound two edges, one real one unreal, that is not possible in a closed polygon");
					}
					tmp2 = tmp2.getPrev();
					if(sanity_check-- <= 0)
						throw new GeometricException("Sanity check !");
				}
				while(tmp2 != tmp);
				anglesum += tmp.getTwin().getNext().angleCounterClockWise(tmp);
				edgecount++;
				if(anglesum > anglimit)
				{
					logger.info("HERE ARE MY EDGES");
					printEdges();
					throw new GeometricException("The sum of angles between edges exceeded 2 PI ("+anglesum+" > "+anglimit+") on this vert: "+this);
				}
				tmp = tmp.getTwin().getNext();
			}
			while(tmp != getFirstEdge());
			//logger.info("anglesum:"+anglesum+",edgecount:"+edgecount);
		}

		// Walk around our-selves with tmp = outgoing; tmp = tmp.twin.next(counter-clockwise)
		tmp = getFirstEdge();
		anglesum = 0;
		edgecount = 0;
		if(tmp != null)
		{
			sanity_check = 10000;
			edgecount = 0;
			do
			{
				// Test that tmp has us as origin
				if(tmp.getOrigin() != this)
				{
					throw new GeometricException("edge "+tmp+" does not have a correct origin");
				}
				// Test surface links (counter-clockwise)
				PlanarEdge tmp2 = tmp;
				do
				{
					if(tmp2.isRealEdge() != tmp2.getNext().isRealEdge())
					{
						logger.info("VERT: "+tmp2.getOrigin());
						logger.info("Edge1:"+tmp2);
						logger.info("Edge2:"+tmp2.getNext());
						throw new GeometricException("Bound two edges, one real one unreal, that is not possible in a closed polygon");
					}
					tmp2 = tmp2.getNext();
					if(sanity_check-- <= 0)
						throw new GeometricException("Sanity check !");
				}
				while(tmp2 != tmp);
				
				anglesum += tmp.angleCounterClockWise(tmp.getPrev().getTwin());
				edgecount++;
				if(anglesum > anglimit)
				{
					throw new GeometricException("The sum of angles between edges exceeded 2 PI ("+anglesum+" > "+anglimit+") on this vert: "+this);
				}
				tmp = tmp.getPrev().getTwin();
			}
			while(tmp != getFirstEdge());
			//logger.info("anglesum:"+anglesum+",edgecount:"+edgecount);
		}

		return true;
	}

	/**
	 * This method returns the first and best real edge going out of this vertex, there should be only one before the triangulation.
	 * 
	 * @return
	 */
	public TriangulationEdge getOutGoingEdge()
	{
		if(getFirstEdge() == null)
			return null;
		TriangulationEdge next = (TriangulationEdge) getFirstEdge(); 
		do
		{
			if(next.isRealEdge())
				return next;
			next = (TriangulationEdge) next.getTwin().getNext();
		}
		while(next != getFirstEdge()); // Only one round
		return null;
	}

	/**
	 * This method returns the first and best real edge going in to this vertex, there should be only one before the triangulation.
	 * 
	 * @return
	 */
	public TriangulationEdge getInGoingEdge()
	{
		if(getFirstEdge() == null)
			return null;
		TriangulationEdge next = (TriangulationEdge) getFirstEdge(); 
		do
		{
			if(next.getTwin().isRealEdge())
				return (TriangulationEdge) next.getTwin();
			next = (TriangulationEdge) next.getTwin().getNext();
		}
		while(next != getFirstEdge()); // Only one round
		return null;
	}
}
