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

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.util.geom.BufferUtils;
import com.jmex.font3d.math.Triangulator.YMonotonePolygon.Triangle;

public class Triangulator extends DoublyConnectedEdgeList<TriangulationVertex, TriangulationEdge>
{
    private static final Logger logger = Logger.getLogger(Triangulator.class
            .getName());
    
	private IntBuffer complete_triangulation;
	private Vector<YMonotonePolygon> monotone_polygons = new Vector<YMonotonePolygon>();
	int polyids = 0;
	
	float getXAtY(TriangulationEdge edge, float y)
	{
		float dx = edge.getDX();
		float dy = edge.getDY();
		if(dy == 0)
		{
			if(edge.getOrigin().point.y == y)
				return edge.getOrigin().point.x;
			logger.warning("Degenerate case, dy == 0, no idea what will happen now....");
			return edge.getOrigin().point.x;
		}
		float t = (y - edge.getOrigin().point.y) / dy;
		return edge.getOrigin().point.x + dx * t;
	}
	
	public IntBuffer triangulate(boolean cleanrun)
	{
		if(cleanrun)
			complete_triangulation = null;
		return triangulate();
	}
	
	public IntBuffer triangulate()
	{
		// Have we done it before ?
		if(complete_triangulation == null)
		{
			// Make sure we have a valid planar closed polygon (maybe with holes)
			checkTriangulation();
			// Create the Y-monotone polygons
			generateMonotonePolygons();
			
			// Triangulate them all and count how many triangles we are going to need.
			int tricount = triangulateMonotonePolygons();
			complete_triangulation = BufferUtils.createIntBuffer(tricount * 3);
			complete_triangulation.rewind();
			
			// Copy all triangles to the buffer
			for(YMonotonePolygon poly : monotone_polygons)
			{
				for(Triangle t : poly.poly_tris)
				{
					complete_triangulation.put(t.p1);
					complete_triangulation.put(t.p2);
					complete_triangulation.put(t.p3);
				}
			}
		}
		return complete_triangulation;
	}
	
	/**
	 * This is the sweep-line algorithm outlined in section 3.2 of "Computational Geometry", ISBN: 3-540-65620-0.
	 */
	private void generateMonotonePolygons()
	{
		class SweepLineStatus extends TreeSet<TriangulationEdge>
		{
			private static final long serialVersionUID = 1L;
			SweepLineComparer sweep_comparer;
			public SweepLineStatus()
			{
				super(new SweepLineComparer());
				sweep_comparer = (SweepLineComparer) comparator();
			}
			
			@Override
			public boolean add(TriangulationEdge edge)
			{
				boolean result = super.add(edge);
				if(!result)
				{
					logger.severe("The insertion of edge "+edge+" had already been done....");
				}
				return result;
			}
			
			void printElements()
			{
				for(TriangulationEdge e : this)
				{
					logger.info("EDGE: "+e+":"+getXAtY(e, sweep_comparer.currentvertex.point.y));
				}
			}

			public boolean remove(TriangulationEdge edge)
			{
				boolean result = super.remove(edge);
				if(!result)
				{
					logger.severe("The removal of edge "+edge+" did not succeed");
				}
				return result;
			}
			
			public TriangulationEdge getLeftOf(TriangulationEdge edge)
			{
				SortedSet<TriangulationEdge> hset = headSet(edge);
				//logger.info("hset.size():"+hset.size());
				if(hset.size() == 0)
				{
					logger.warning("We could find no left of "+edge+": "+getXAtY(edge, sweep_comparer.currentvertex.point.y));
					//logger.info("Vertex: prev:"+((TriangulationVertex)edge.getOrigin()).prev_vert+","+edge.getOrigin()+",next:"+((TriangulationVertex)edge.getOrigin()).next_vert+")");
					// Print out the whole thing
					printElements();
				}
				return hset.last();
			}
			
			public void setCurrentVertex(TriangulationVertex v)
			{
				sweep_comparer.currentvertex = v;
			}
		}
		// Initialize type/ingoing/outgoing and such
		for(TriangulationVertex v : getVertices())
		{
			v.initializeType();
		}
		
		// Initialize structures
		SweepLineStatus sweep_line = new SweepLineStatus();
		PriorityQueue<TriangulationVertex> sweep_queue = new PriorityQueue<TriangulationVertex>(getVertices().size(), new SweepQueueComparator());
		sweep_queue.addAll(getVertices());
		
		class DiagnalEdge
		{
			int src,dst;
			public DiagnalEdge(int src, int dst)
			{
				this.src = src;
				this.dst = dst;
			}
			@Override
            public String toString()
			{
				return "("+src+"->"+dst+")";
			}
		}
		Vector<DiagnalEdge> postponed_diagonals = new Vector<DiagnalEdge>();
		
		// Empty the priority-queue
		TriangulationVertex v_i;
		while(!sweep_queue.isEmpty())
		{
			v_i = sweep_queue.poll();
			sweep_line.setCurrentVertex(v_i); // To make sure edges are ordered correctly
			//logger.info("NextVertex:"+v_i.toString()+"");

			switch(v_i.getType())
			{
			case START:
				sweep_line.add(v_i.getOutGoingEdge());
				v_i.getOutGoingEdge().helper = v_i;
				break;
			case END:
				if(v_i.getInGoingEdge().isHelperMergeVertex())
				{
					postponed_diagonals.add(new DiagnalEdge(v_i.getIndex(), v_i.getInGoingEdge().helper.getIndex()));
				}
				sweep_line.remove(v_i.getInGoingEdge());
				break;
			case SPLIT:
				{
					// Find edge directly left of v_i
					TriangulationEdge e_j = sweep_line.getLeftOf(v_i.getOutGoingEdge());
					{
						postponed_diagonals.add(new DiagnalEdge(v_i.getIndex(), e_j.helper.getIndex()));
					}
					e_j.helper = v_i;
					sweep_line.add(v_i.getOutGoingEdge());
					v_i.getOutGoingEdge().helper = v_i;
				}
				break;
			case MERGE:
				{
					if(v_i.getInGoingEdge().isHelperMergeVertex())
					{
						postponed_diagonals.add(new DiagnalEdge(v_i.getIndex(), v_i.getInGoingEdge().helper.getIndex()));
					}
					sweep_line.remove(v_i.getInGoingEdge());
					TriangulationEdge left_of = sweep_line.getLeftOf(v_i.getInGoingEdge());
					if(left_of.isHelperMergeVertex())
					{
						postponed_diagonals.add(new DiagnalEdge(v_i.getIndex(), left_of.helper.getIndex()));
					}
					left_of.helper = v_i;
				}
				break;
			case REGULAR_RIGHT: // The interior lies to the left of us
				{
					TriangulationEdge left_of = sweep_line.getLeftOf(v_i.getOutGoingEdge());
					if(left_of.isHelperMergeVertex())
					{
						postponed_diagonals.add(new DiagnalEdge(v_i.getIndex(), left_of.helper.getIndex()));
					}
					left_of.helper = v_i;
				}
				break;
			case REGULAR_LEFT: // The interior lies to the right of us
				{
					if(v_i.getInGoingEdge().isHelperMergeVertex())
					{
						postponed_diagonals.add(new DiagnalEdge(v_i.getIndex(), v_i.getInGoingEdge().helper.getIndex()));
					}
					sweep_line.remove(v_i.getInGoingEdge());
					sweep_line.add(v_i.getOutGoingEdge());
					v_i.getOutGoingEdge().helper = v_i;
				}
				break;
			case UNSET:
				logger.info("PANIX: the type of a vertex was: "+v_i.getType());
				break;
			}
			//logger.info("After:");
			//sweep_line.printElements();
			//logger.info("Diags: "+postponed_diagonals);
		}
		
		// Now add the diagonals
		//logger.info("\nDIAGONALS: ");
		for(DiagnalEdge de : postponed_diagonals)
		{
			//logger.info("Diagonal:"+de);
			addDiagonal(de.src, de.dst);
		}
		checkTriangulation();
		
		// Now extract all the monotone polygons
		monotone_polygons.clear();
		for(TriangulationEdge e : getEdges())
		{
			e.marked = false;
		}
		for(TriangulationEdge e : getEdges())
		{
			if(!e.marked && e.isRealEdge())
			{
				monotone_polygons.add(new YMonotonePolygon(e));
			}
		}
		
		checkTriangulation();
	}

	void addDiagonal(int src, int dst)
	{
		TriangulationEdge edge = addEdge(src, dst);
		edge.realedge = true;
		edge.getTwin().realedge = true;
	}

	private boolean checkTriangulation()
	{
		for(TriangulationVertex v : getVertices())
		{
			if(v.getFirstEdge() == null)
			{
				throw new GeometricException("We have a vertex with no edges: "+v);
			}
			if(!v.checkAllEdges())
				return false;
		}
		//logger.info("\n---- checkTriangulation() succeeded: v:"+getVertices().size()+",e:"+getEdges().size()+"\n");
		return true;
	}

	private int triangulateMonotonePolygons()
	{
		int tricount = 0;
		//logger.info("About to triangulate "+monotone_polygons.size()+" polygons");
		for(YMonotonePolygon poly : monotone_polygons)
		{
			tricount += poly.triangulate();
			checkTriangulation();
		}
		return tricount;
	}

	@Override
	public TriangulationEdge createEdge(TriangulationVertex origin, boolean real)
	{
		return new TriangulationEdge(origin, real);
	}

	@Override
	public TriangulationVertex createVertex(int index, Vector3f p)
	{
		return new TriangulationVertex(index, p);
	}

	/**
	 * This class represents a monoton polygon with respect to the y-coordinate.
	 * 
	 * @author emanuel
	 */
	class YMonotonePolygon
	{
		class Triangle
		{
			int p1,p2,p3;
			Triangle(int p1, int p2, int p3, boolean clockwise)
			{
				this.p1 = clockwise ? p1 : p2;
				this.p2 = clockwise ? p2 : p1;
				this.p3 = p3;
			}
		}
		ArrayList<TriangulationEdge> poly_edges = new ArrayList<TriangulationEdge>();
		ArrayList<Triangle>          poly_tris  = new ArrayList<Triangle>();
		private int polyid;

		public YMonotonePolygon(TriangulationEdge e)
		{
			polyid = polyids++;
			//logger.info("YMonoe, id:"+polyid);
			TriangulationEdge start_edge = e;
			TriangulationEdge next_edge  = start_edge;
			do
			{
				//logger.info("next_edge.getOrigin():"+next_edge.getOrigin());
				next_edge.marked = true;
				poly_edges.add(next_edge);
				next_edge = (TriangulationEdge) next_edge.getNext();
				if(!next_edge.isRealEdge())
				{
					throw new GeometricException("We cannot add a non-real edge to a polygon.");
				}
			}
			while(start_edge != next_edge);
		}

		/**
		 * This is the linear-time algorithm outlined in section 3.2 of "Computational Geometry", ISBN: 3-540-65620-0.
		 * @return
		 */
		public int triangulate()
		{
			int trianglecount = (poly_edges.size()-2);
			int triangle_index_count = trianglecount*3;
			//logger.info("TODO: triangulate this poly("+this.polyid+") ! ("+(tricount/3)+" triangles will be needed...)");
			if(trianglecount == 1)
			{
				poly_tris.add(new Triangle(poly_edges.get(0).getOrigin().getIndex(), poly_edges.get(1).getOrigin().getIndex(), poly_edges.get(2).getOrigin().getIndex(), false));
				return triangle_index_count; // Trivial, its one triangle.
			}
			
			
			// Create the sorted list by merging the two "paths" into one sorted list.
			ArrayList<TriangulationVertex> queue = createSortedVertexList();
			Stack<TriangulationVertex> stack = new Stack<TriangulationVertex>();
			// Push the first two onto the stack.
			stack.push(queue.get(0));
			stack.push(queue.get(1));
			for(int i = 2; i < queue.size()-1; i++)
			{
				TriangulationVertex u_j = queue.get(i);
				if(u_j.is_left_chain != stack.peek().is_left_chain)
				{
					//TriangulationVertex head = stack.peek();
					while(stack.size() > 1)
					{
						TriangulationVertex popped = stack.pop();
						poly_tris.add(new Triangle(u_j.getIndex(), popped.getIndex(), stack.peek().getIndex(), !u_j.is_left_chain));
					}
					stack.pop(); // Remove the last one.
					stack.push(queue.get(i-1));
					stack.push(u_j);
				}
				else
				{
					//logger.info("\nYEAAAAAHHHH(left:"+u_j.is_left_chain+")");
					TriangulationVertex lastpopped = stack.pop();
					while(!stack.isEmpty())
					{
						boolean is_left_of = isLeftOf(u_j.getPoint(), lastpopped.getPoint(), stack.peek().getPoint());
						if(u_j.is_left_chain == is_left_of)
						{
							poly_tris.add(new Triangle(u_j.getIndex(), lastpopped.getIndex(), stack.peek().getIndex(), u_j.is_left_chain));
							lastpopped = stack.pop();
							//addDiagonal(u_j.getIndex(), lastpopped.getIndex());
						}
						else
						{
							break;
						}
					}
					stack.push(lastpopped);
					stack.push(u_j);
				}
			}
			// Add diagonals to all verts on the stack (except first and last)
			TriangulationVertex lastpopped = null;
			if(stack.size() > 1)
				lastpopped = stack.pop();
			TriangulationVertex last = queue.get(queue.size()-1);
			while(stack.size() > 0)
			{
				//addDiagonal(last.getIndex(), popped.getIndex());
				poly_tris.add(new Triangle(last.getIndex(), lastpopped.getIndex(), stack.peek().getIndex(), lastpopped.is_left_chain));
				lastpopped = stack.pop();
			}
			
			/*
			int required_no_of_diagonals = (tricount/3) - 1;
			if(no_of_diagonals != required_no_of_diagonals)
			{
				int i = 0;
				for(TriangulationVertex v : queue)
				{
					logger.info("Queue["+(i++)+"]:"+v+",is_left:"+v.is_left_chain);
				}
				throw new RuntimeException("Subdivision of monoton polygon: "+polyid+" did not add the required number of diagonals: ("+no_of_diagonals+" != "+required_no_of_diagonals+")");
			}
			*/
			if(trianglecount != poly_tris.size())
			{
				throw new GeometricException("Subdivision of monoton polygon: "+polyid+" did not give as many triangles as planned:("+trianglecount+" != "+poly_tris.size()+")");
			}
			
			return triangle_index_count;
		}

		private ArrayList<TriangulationVertex> createSortedVertexList()
		{
			// Find the top and bottom node O(n) time, and set the outgoing to be the one in this polygon
			//SortedSet<TriangulationVertex> sortedset = new TreeSet<TriangulationVertex>(new SweepQueueComparator());
			TriangulationEdge top = poly_edges.get(0);
			TriangulationEdge bottom = top;
			for(TriangulationEdge edge : poly_edges)
			{
				//logger.info("Edge: "+edge);
				TriangulationVertex vert = ((TriangulationVertex) edge.getOrigin());
				if(((TriangulationVertex)top.getOrigin()).yLessThan(vert))
					top = edge;
				if(vert.yLessThan(bottom.getOrigin()))
					bottom = edge;
			}
			
			// DEBUG
			/*
			if(true)
			{
				logger.info("Top: "+top.getOrigin().getIndex());
				logger.info("Bottom: "+bottom.getOrigin().getIndex());
			}
			*/
			/*
			{
				Sphere tmp = new Sphere("Top:"+top, 5, 5, 0.01f);
				tmp.setLocalTranslation(new Vector3f(top.point));
				debugNode.attachChild(tmp);

				Box box = new Box("Bottom:"+bottom,bottom.point,0.01f,0.01f,0.01f);
				debugNode.attachChild(box);
			}
			*/
			// Go from top to bottom, setting them all to be leftsiders
			ArrayList<TriangulationVertex> arr = new ArrayList<TriangulationVertex>();
			int sanity = poly_edges.size()*2;
			arr.add((TriangulationVertex) top.getOrigin());
			TriangulationEdge tmp_left = (TriangulationEdge) top.getNext();
			TriangulationEdge tmp_right = (TriangulationEdge) top.getPrev();
			while(tmp_left != bottom || tmp_right != bottom)
			{
				// Ok, what should be inserted next
				TriangulationVertex left = (TriangulationVertex) tmp_left.getOrigin();
				TriangulationVertex right = (TriangulationVertex) tmp_right.getOrigin();
				left.is_left_chain = true;
				right.is_left_chain = false;
				if(left.yLessThan(right))
				{
					//logger.info("Added Right:"+right);
					arr.add(right);
					tmp_right = (TriangulationEdge) tmp_right.getPrev();
				}
				else
				{
					//logger.info("Added Left:"+left);
					arr.add(left);
					tmp_left = (TriangulationEdge) tmp_left.getNext();
				}
				if(sanity-- < 0)
					throw new RuntimeException("We could not get from top to bottom of the poly:"+this);
			}
			arr.add((TriangulationVertex) bottom.getOrigin());
			
			if(arr.size() != poly_edges.size())
			{
				logger.warning("The number of vertices does not match the number of edges: "
                                + arr.size() + " != " + poly_edges.size());
				throw new RuntimeException("The number of vertices does not match the number of edges: "+arr.size()+" != "+poly_edges.size());
			}
			return arr;
		}

		private boolean isLeftOf(Vector3f A, Vector3f B, Vector3f P)
		{
			//return 0> (v2.x - v1.x) * (v.y - v1.y) - (v.x - v1.x) * (v2.y - v1.y);
			return 0 > (B.x-A.x) * (P.y-A.y) - (P.x-A.x) * (B.y-A.y);
		}
	}

	/**
	 * Sort the edges according to their X-coordinate from the y coordinate of the sweepline.
	 * 
	 * @author emanuel
	 */
	class SweepLineComparer implements Comparator<TriangulationEdge>
	{
		TriangulationVertex currentvertex = null;
		public int compare(TriangulationEdge edge1, TriangulationEdge edge2)
		{
			if(edge1 == edge2)
				return 0;
			// Get the x coordinate from the y coordinate of the currentvertex
			float x1 = getXAtY(edge1, currentvertex.point.y);
			float x2 = getXAtY(edge2, currentvertex.point.y);
			//if(Math.abs(x1 - x2) < FastMath.FLT_EPSILON)
			if(x1 == x2)
			{
				// they share a vertex, and it is the currentvertex, 
				// then we use the dot-product of the lines (normalized) and the X-axis, since that will tell us who is most
				// to the right.
				logger.info("--------------------");
				logger.info("Edge1: "+edge1);
				logger.info("Edge2: "+edge2);
				logger.info("Edges share: "+currentvertex+", use other ends.");
				PlanarVertex x1_v = edge1.getOtherEnd(currentvertex);
				logger.info("Other end(1):"+x1_v);
				PlanarVertex x2_v = edge2.getOtherEnd(currentvertex);
				logger.info("Other end(2):"+x2_v);
				Vector3f x1_v_v = new Vector3f(x1_v.getPoint()).subtractLocal(currentvertex.getPoint()).normalizeLocal();
				Vector3f x2_v_v = new Vector3f(x2_v.getPoint()).subtractLocal(currentvertex.getPoint()).normalizeLocal();
				x1 = x1_v_v.dot(Vector3f.UNIT_X); // edge0.getOtherEnd(currentvertex).point.x;
				x2 = x2_v_v.dot(Vector3f.UNIT_X); // edge1.getOtherEnd(currentvertex).point.x;
				if(Math.abs(x1 - x2) < FastMath.FLT_EPSILON)
				{
					// Even worse they are also on the same level here...
					logger.info("Still the same, using Y-coordinates");
					x1 = x1_v.getPoint().y;
					x2 = x2_v.getPoint().y;
				}
			}
			if(x1 == x2)
				logger.warning("Equal vertices: "+x1+" == "+x2);
			return  (x1 < x2) ? -1 : 1;
		}
	}
	
	/**
	 * Simple y-sorting
	 * 
	 * @author emanuel
	 */
	class SweepQueueComparator implements Comparator<TriangulationVertex>
	{
		public int compare(TriangulationVertex v0, TriangulationVertex v1)
		{
			if(v0 == v1)
				return 0;
			return v0.yLessThan(v1) ? 1 : -1;
		}
		
	}

	public ArrayList<TriangulationEdge> getEdges()
	{
		return edges;
	}
}
