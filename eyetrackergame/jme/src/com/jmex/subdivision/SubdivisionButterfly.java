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
/*
 * Created on 2006-okt-30
 */
package com.jmex.subdivision;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * Subdivison according to the Butterfly scheme introduced by 
 * Dyn, Gregory and Levin in 
 * ['A butterfly subdivision scheme for surface interpolation with tension control', ACM Transactions on graphics 9, 2 (1990), pp. 160-169]
 * <br><br>
 * Other papers used during implementetion:<br>
 * 'SIGGRAPH 2000 Course Notes', Denis Zorin et al. (http://mrl.nyu.edu/~dzorin/sig00course/) mainly pp. 69-75 (Contains faulty coefficients on extraordinary crease rule)<br>
 * 'Interpolating Subdivision for Meshes with Arbitrary Topology', Denis Zorin, Peter Schröder, Wim Sweldens. Computer Graphics, Ann. Conf. Series, vol. 30, pp. 189-192, 1996.<br>
 * 'Sharpen&Bend: Recovering curved sharp edges in triangle meshes produced by feature-insensitive sampling', M. Attene, B. Falcidieno, J. Rossignac and M. Spagnuolo. (contains the corrected coefficients, which I had to correct again to get it right. Where it says 'K=degree(V)+1' it should be 'K=degree(V)-1', I think... it works for me)<br>
 * <br>
 * The SIGGRAPH 2000 Course Notes contains detailed descriptions of other subdivison schemes if anyone feels the urge to implement another one,<em>hint, hint</em>. Be sure to contact me if that's the case.<br>
 * Among others: Catmull-Clark, Loop<br>
 * <br>
 *
 * Usage:<br>
 * <code>
 * TriMesh mesh = {some trimesh};<br>
 * Subdivision subdivision = new SubdivisionButterfly(mesh.getBatch(0)); // prepare for subdivision<br>
 * subdivision.subdivide(); // subdivide<br>
 * subdivision.apply(); // Applies the new subdivided buffers to the batch<br>
 * subdivision.computeNormals(); // calculate new normals <br>
 * </code><br>
 * <br>
 * Or you can use it without giving it a batch:<br>
 * <br>
 * <code>
 * Subdivision subdivision = new SubdivisionButterfly();<br>
 * subdivision.setVertexBuffer(batch.getVertexBuffer());<br>
 * subdivision.setIndexBuffer(batch.getIndexBuffer());<br>
 * subdivision.addToBufferList(batch.getTextureBuffer(0), Subdivision.BufferType.TEXTUREBUFFER);<br>
 * subdivision.addToBufferList(batch.getTextureBuffer(1), Subdivision.BufferType.TEXTUREBUFFER);<br>
 * subdivision.addToBufferList(batch.getColorBuffer(), Subdivision.BufferType.COLORBUFFER);<br>
 * subdivision.subdivide(); // subdivide<br>
 * subdivision.apply(mesh.getBatch(0)); // Applies the new subdivided buffers to the batch<br> 
 * subdivision.computeNormals(mesh.getBatch(0)); // calculate new normals<br> 
 * </code>
 * <br>
 * <br>
 * Handles triangular faces only 
 * <br>
 * 
 * @author Tobias Andersson (tobbe.a removethisoryourclientgoesape gmail.com)
 */
public class SubdivisionButterfly extends Subdivision {
    private static final Logger logger = Logger
            .getLogger(SubdivisionButterfly.class.getName());
	
	protected ArrayList<Edge> edges;
	protected ArrayList<Triangle> triangles;
	protected ArrayList<Edge>[] vertexEdgeMap;
	
	
	/**
	 * Constructor
	 */
	public SubdivisionButterfly() {
		super();
	}
	
	/**
	 * @param vertexBuffer
	 * @param indexBuffer
	 */
	public SubdivisionButterfly(FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
		super(vertexBuffer, indexBuffer);
	}
	
	/**
	 * @param mesh The TriMesh that is to be subdivided
	 */
	public SubdivisionButterfly(TriMesh mesh) {
		super(mesh);
	}
	
	/* (non-Javadoc)
	 * @see com.tobbes.subdivision.Subdivision#prepare()
	 */
	/**
	 * Prepare the structures needed for subdividing
	 * 
	 * @return <code>true</code> always :)
	 */
	@Override
	public boolean prepare() {
		findEdgesAndTriangles();
		constructVertexEdgeMap();
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.tobbes.subdivision.Subdivision#doSubdivide()
	 */
	/**
	 * Do the actual subdivision.<br>
	 * 1. Set up the new buffers<br>
	 * 2. Split all edges and interpolate all buffers according to the Butterfly scheme
	 * 3. Construct new triangles
	 * 
	 * @return <code>true</code> always :)
	 */
	@Override
	protected boolean doSubdivide() {
		
		// **** Set up the new buffers ****
		newVertexBuffer = BufferUtils.createVector3Buffer(getVertexCount() + edges.size()); // every edge makes a new vertex
		getVertexBuffer().rewind();
		newVertexBuffer.put(getVertexBuffer()); // put the old vertex buffer in the beginning of the new. Butterfly is an interpolating scheme and all vertices remain.
		
		newIndexBuffer = BufferUtils.createIntBuffer(getTriangleCount() * 4 * 3); // Every old triangle makes four new
		
		newBuffers = new ArrayList<SubdivisionBuffer>();
		
		SubdivisionBuffer oldBuf;
		FloatBuffer newBuf;
		for (Iterator<SubdivisionBuffer> it = buffers.iterator(); it.hasNext(); ) {
			oldBuf = it.next();
			if (oldBuf != null) {
				oldBuf.buf.rewind();
				newBuf = BufferUtils.createFloatBuffer(oldBuf.buf.capacity() + (edges.size()*oldBuf.elemSize));
				newBuf.put(oldBuf.buf); // put the old buffer in the beginning of the new. Butterfly is an interpolating scheme and all vertices from earlier levels remain the same.
				newBuffers.add(new SubdivisionBuffer(newBuf, oldBuf.type));
			}
		}
		
		// **** Subdivide ****
		Edge edge;
		Vector newVector;
		Rule rule;
		SubdivisionBuffer curOldBuffer;
		SubdivisionBuffer curNewBuffer;
		int bufferNumber = 0;
		
		// For every edge in edgeList, make new vertex
		for (Iterator<Edge> it = edges.iterator() ; it.hasNext(); ) {
			edge = it.next();
			
			// determine which rule to use
			rule = VertexType.getRule(vertexValence[edge.vertexIndex[0]], vertexLocation[edge.vertexIndex[0]], vertexValence[edge.vertexIndex[1]], vertexLocation[edge.vertexIndex[1]]);
			
			// and split the edge according to that rule, once for every buffer
			// First: the vertex buffer
			newVector = rule.split(edge, vertexBuffer, 3, vertexEdgeMap, triangles, vertexValence, vertexLocation);
			edge.newVertexIndex = newVertexBuffer.position() / 3; // Store the index of the new vertex in the split edge
			newVertexBuffer.put(newVector.elem,0,3); // store the new vertex in the new vertex buffer
			
			// Second: the other buffers
			Iterator<SubdivisionBuffer> newBufIt = newBuffers.iterator();
			bufferNumber = 0;
			for (Iterator<SubdivisionBuffer> oldBufIt = buffers.iterator(); oldBufIt.hasNext(); bufferNumber++) {
				curNewBuffer = newBufIt.next();
				curOldBuffer = oldBufIt.next();
				
				if (curOldBuffer.linear) {
					// Those buffers marked as linear should be linearly interpolated, generally they are texture buffers
					newVector = new Vector(curOldBuffer.elemSize);
					newVector.interpolate(
							new Vector(curOldBuffer.elemSize).populateFromBuffer(curOldBuffer.buf, edge.vertexIndex[0]),
							new Vector(curOldBuffer.elemSize).populateFromBuffer(curOldBuffer.buf, edge.vertexIndex[1])
					);
				} else {
					// Otherwise we interpolate according to the Butterfly Scheme
					newVector = rule.split(edge, curOldBuffer.buf, curOldBuffer.elemSize, vertexEdgeMap, triangles, vertexValence, vertexLocation);
				}
				curNewBuffer.buf.put(newVector.elem,0,curNewBuffer.elemSize); // Put the new vector into the buffer
			}
		}
		// Flip all new buffers
		for (Iterator<SubdivisionBuffer> it=newBuffers.iterator(); it.hasNext();) {
			it.next().buf.flip();
		}
		
		
		// **** construct new triangles ***
		Triangle triangle;
		int[] vertices = new int[6];
		int i = 0;
		
		// For every triangle who's edges has a new vertex each, make four new triangles
		for (Iterator<Triangle> it = triangles.iterator() ; it.hasNext() ; ) {
			triangle = it.next();
			vertices[0] = triangle.vertexIndex[0];
			vertices[1] = triangle.vertexIndex[1];
			vertices[2] = triangle.vertexIndex[2];
			vertices[3] = triangle.findEdge(triangle.vertexIndex[0], triangle.vertexIndex[1]).newVertexIndex;
			vertices[4] = triangle.findEdge(triangle.vertexIndex[1], triangle.vertexIndex[2]).newVertexIndex;
			vertices[5] = triangle.findEdge(triangle.vertexIndex[2], triangle.vertexIndex[0]).newVertexIndex;
			
			newIndexBuffer.put(vertices[0]).put(vertices[3]).put(vertices[5]);
			newIndexBuffer.put(vertices[1]).put(vertices[4]).put(vertices[3]);
			newIndexBuffer.put(vertices[2]).put(vertices[5]).put(vertices[4]);
			newIndexBuffer.put(vertices[3]).put(vertices[4]).put(vertices[5]);
			
			i++;
		}
		newIndexBuffer.flip();
		return true;
	}
	
	/**
	 * Get the triangle count of the set index buffer (capacity / 3)
	 * 
	 * @return The triangle count
	 */
	private int getTriangleCount() {
		if (getIndexBuffer() == null) {
			logger.warning("No index buffer set, aborting.");
			return 0;
		}
		return indexBuffer.capacity() / 3;
	}
	
	/**
	 * Finds all edges and triangles in indexBuffer.
	 * 
	 * @see SubdivisionButterfly.Edge
	 * @see SubdivisionButterfly.Triangle
	 */
	private void findEdgesAndTriangles() {
		edges = new ArrayList<Edge>(); // only an approximation
		triangles = new ArrayList<Triangle>(this.getTriangleCount());
		IntBuffer ib = this.getIndexBuffer();
		Edge edge;
		Triangle triangle;
		int i1,i2,i3;		
		
		// go through all the triangles in the indexBuffer and 
		// add to edges and triangles
		ib.rewind();
		while (ib.remaining() >= 3) {
			i1 = ib.get();
			i2 = ib.get();
			i3 = ib.get();
			triangle = new Triangle(i1,i2,i3);
			
			edge = inList(edges, i1, i2);
			if (edge == null) {
				edge = new Edge(i1, i2);
				edges.add(edge);
				edge.triangles[0] = triangle;
			} else {
				edge.triangles[1] = triangle;
			}
			triangle.edges[0] = edge;
			
			edge = inList(edges, i2, i3);
			if (edge == null) {
				edge = new Edge(i2, i3);
				edges.add(edge);
				edge.triangles[0] = triangle;
			} else {
				edge.triangles[1] = triangle;
			}
			triangle.edges[1] = edge;
			
			edge = inList(edges, i3, i1);
			if (edge == null) {
				edge = new Edge(i3, i1);
				edges.add(edge);
				edge.triangles[0] = triangle;
			} else {
				edge.triangles[1] = triangle;
			}
			triangle.edges[2] = edge;
			
			triangles.add(triangle);
		}
	}
	
	
	private Edge inList(ArrayList<Edge> list, int i1, int i2) {
		Edge edge;
		for (int i = 0; i < list.size(); i++) {
			edge = list.get(i);
			if (edge.equals(i1, i2))
				return edge;
		}
		return null;
	}
	
	private int valence(int vertexIndex) {
		return vertexEdgeMap[vertexIndex].size();
	}
	
	/**
	 * Constructs the vertexEdgeMap which, for every vertex, stores that
	 * vertex's edges in a counter-clockwise order. Also, it calculates
	 * <code>Location</code> and <code>Valence</code> of each vertex
	 *
	 * @see SubdivisionButterfly.Location
	 * @see SubdivisionButterfly.Valence
	 */
	@SuppressWarnings ("unchecked")
	private void constructVertexEdgeMap() {
		Edge firstEdge, nextEdge, edge;
		vertexEdgeMap = new ArrayList[this.getVertexCount()];
		vertexLocation = new Location[this.getVertexCount()];
		vertexValence = new Valence[this.getVertexCount()];
		for (int i=0; i < this.getVertexCount() ; i++) {
			// Set the Location of this vertex to INTERIOR, will be changed if appropriate
			vertexLocation[i] = Location.INTERIOR;
			nextEdge = null;
			firstEdge = findOneEdge(i, edges);
			edge = firstEdge;
			vertexEdgeMap[i] = new ArrayList<Edge>();
			while ((!firstEdge.equals(nextEdge)) && edge != null) {
				vertexEdgeMap[i].add(edge);
				nextEdge = findNextCCWEdge(edge, i);
				edge = nextEdge;
			}
			if (edge == null) {
				// As we hit a boundary, we know that the vertex is a boundary vertex
				if (edge == null) vertexLocation[i] = Location.CREASE;
				// We are at a boundary vertex, and we might have missed 
				// some of its edges since we can't circle all around it.
				// TODO: Better solution
				// Temporary solution for now: rewind ClockWise until boundary 
				// is hit on the other side, then redo it CounterClockWise as usual
				edge = firstEdge;
				while (edge != null) {
					nextEdge = edge;
					edge = findNextCWEdge(edge, i);
				}
				firstEdge = nextEdge;
				
				// redo it from the start (the most clockwise edge)
				nextEdge = null;
				edge = firstEdge;
				vertexEdgeMap[i].clear();
				while ((!firstEdge.equals(nextEdge)) && edge != null) {
					vertexEdgeMap[i].add(edge);
					nextEdge = findNextCCWEdge(edge, i);
					edge = nextEdge;
				}
			}
			// Calculate Valence of the vertex
			vertexValence[i] = Valence.getValence(vertexLocation[i], valence(i));
		}
	}
	
	/**
	 * Finds any Edge that contains the vertex index
	 * 
	 *  @return the Edge
	 */
	private Edge findOneEdge(int vertexIndex, ArrayList<Edge> edges) {
		Edge result = null;
		boolean found = false;
		for (Iterator<Edge> it = edges.iterator() ; it.hasNext() && (!found) ; ) {
			result = it.next();
			if (result.hasVertex(vertexIndex)) found = true;			
		}
		return result;
	}
	
	/**
	 * One or two Triangles share an Edge, find the Triangle that is on the counter-clockwise
	 * side of the Edge when looking down the Edge from its vertex vertexIndex
	 *
	 * @see SubdivisionButterfly#findCWTriangle(SubdivisionButterfly.Edge, int)
	 * @param edge
	 * 			The edge whos triangles we are examining
	 * @param vertexIndex
	 * 			The vertex of the edge that we are looking down the edge from
	 * @return The counter-clockwise Triangle of the edge, or null if none found
	 */
	private Triangle findCCWTriangle(Edge edge, int vertexIndex) {
		Triangle result = null;
		Triangle[] triangles = edge.triangles;
		if (triangles[0] != null) {
			if (triangles[0].isCCW(edge, vertexIndex)) return triangles[0];
		}
		if (triangles[1] != null) {
			if (triangles[1].isCCW(edge, vertexIndex)) return triangles[1];
		}
		return result;
	}
	
	/**
	 * One or two Triangles share an Edge, find the Triangle that is on the clockwise
	 * side of the Edge when looking down the Edge from its vertex vertexIndex
	 *
	 * @see SubdivisionButterfly#findCCWTriangle(SubdivisionButterfly.Edge, int)
	 * @param edge
	 * 			The edge whos triangles we are examining
	 * @param vertexIndex
	 * 			The vertex of the edge that we are looking down the edge from
	 * @return The clockwise Triangle of the edge, or null if none found
	 */
	private Triangle findCWTriangle(Edge edge, int vertexIndex) {
		Triangle result = null;
		Triangle[] triangles = edge.triangles;
		if (triangles[0] != null) {
			if (!triangles[0].isCCW(edge, vertexIndex)) return triangles[0];
		}
		if (triangles[1] != null) {
			if (!triangles[1].isCCW(edge, vertexIndex)) return triangles[1];
		}
		return result;
	}
	
	/**
	 * Find the next counter-clockwise Edge of the vertex, using this method we can
	 * circle around the vertex and find all its Edges in a CCW order (if it's not a 
	 * boundary vertex)
	 * 
	 * @param edge The current edge
	 * @param vertexIndex The vertex who's edge we want to find
	 * @return The next Edge of the vertex, circling counter-clockwise around the vertex
	 */		
	private Edge findNextCCWEdge(Edge edge, int vertexIndex) {
		Triangle triangle = findCCWTriangle(edge, vertexIndex);
		if (triangle == null) return null;
		return triangle.findOtherSharedEdge(vertexIndex, edge);
	}
	
	/**
	 * Find the next clockwise Edge of the vertex, using this method we can
	 * circle around the vertex and find all its Edges in a CW order (if it's not a 
	 * boundary vertex)
	 * 
	 * @param edge The current edge
	 * @param vertexIndex The vertex who's edge we want to find
	 * @return The next Edge of the vertex, circling clockwise around the vertex
	 */		
	private Edge findNextCWEdge(Edge edge, int vertexIndex) {
		Triangle triangle = findCWTriangle(edge, vertexIndex);
		if (triangle == null) return null;
		return triangle.findOtherSharedEdge(vertexIndex, edge);
	}
	
	/**
	 * Inner helper class for SubdivisonBatch to keep track of the edges
	 * 
	 * @see SubdivisionButterfly#findEdgesAndTriangles()
	 * @author Tobias
	 */
		public class Edge {
		/** The two vertex indices of this Edge */
		public int[] vertexIndex;
		/** The two new Edges that were created when splitting this Edge */ 
		public Edge[] newEdges;
		/** The one or two triangle(s) sharing this edge */ 
		public Triangle[] triangles;
		/** The new vertex that was created when splitting this Edge */
		public int newVertexIndex = -1;
		
		/** Constructor */
		public Edge() {
			vertexIndex = new int[2];
			newEdges = new Edge[2];
			triangles = new Triangle[2];
		}
		
		/**
		 * Constructor
		 * 
		 * @param i1 One vertex index of this Edge
		 * @param i2 The other vertex index of this Edge
		 */
		public Edge(int i1, int i2) {
			this();
			if (i1 < i2) {
				vertexIndex[0] = i1;
				vertexIndex[1] = i2;
			} else {
				vertexIndex[0] = i2;
				vertexIndex[1] = i1;
			}
		}
		
		/**
		 * Does this Edge have the same vertex indices as the provided Edge. 
		 * The order of the vertex indices does not matter.
		 * 
		 * @param edge The Edge to compare this Edge to
		 * @return <code>true</code> if they are the same
		 */
		public boolean equals(Edge edge) {
			if (edge == null) return false;
			return (this.vertexIndex[0] == edge.vertexIndex[0] && this.vertexIndex[1] == edge.vertexIndex[1]) || (this.vertexIndex[1] == edge.vertexIndex[0] && this.vertexIndex[0] == edge.vertexIndex[1]);
		}
		
		/**
		 * Does this Edge have the same vertex indices as those provided. 
		 * The order of the vertex indices does not matter.
		 *
		 * @param i1 One vertex index
		 * @param i2 Another vertex index
		 * @return <code>true</code> if the vertex indices in this Edge are the same as those provided
		 */
		public boolean equals(int i1, int i2) {
			return (this.vertexIndex[0] == i1 && this.vertexIndex[1] == i2) || (this.vertexIndex[1] == i1 && this.vertexIndex[0] == i2);
		}
		
		/** 
		 * @param vertexIndex The index to look for
		 * @return <code>true</code> if this Edge contains the provided vertexIndex
		 */
		public boolean hasVertex(int vertexIndex) {
			if (this.vertexIndex[0] == vertexIndex) return true;
			if (this.vertexIndex[1] == vertexIndex) return true;
			return false;
		}
		
		/**
		 * @param vertexIndex The index that we already know of of this Edge
		 * @return The other vertex index of this Edge
		 */
		public int otherVertex(int vertexIndex) {
			if (this.vertexIndex[0] == vertexIndex) return this.vertexIndex[1];
			if (this.vertexIndex[1] == vertexIndex) return this.vertexIndex[0];
			return -1;
		}
		
		/**
		 * @return a String representing this Edge
		 */
		public String toString() {
			return "{" + vertexIndex[0] + "," + vertexIndex[1] + "}";
		}
	}
	
	
	/**
	 * Inner helper class for SubdivisonBatch to keep track of the triangles
	 * 
	 * @see SubdivisionButterfly#findEdgesAndTriangles()
	 * @author Tobias
	 */
	public class Triangle {
		/** The three vertex indices of this Triangle */
		public int[] vertexIndex;
		/** The three Edges of this Triangle */
		public Edge[] edges;
		
		/** Constructor */
		public Triangle() {
			vertexIndex = new int[3];
			edges = new Edge[3];
		}
		
		/**
		 * Constructor
		 * 
		 * @param i1 
		 * @param i2
		 * @param i3 The three vertex indices of this Triangle
		 */
		public Triangle(int i1, int i2, int i3) {
			this();
			vertexIndex[0] = i1;
			vertexIndex[1] = i2;
			vertexIndex[2] = i3;
		}
		
		/** 
		 * Does this Triangle contain the given Edge?
		 * 
		 * @param edge The edge to look for
		 * @return <code>true</code> if the edge is found
		 */
		public boolean hasEdge(Edge edge) {
			boolean result = false;
			for (int i=0 ; i < 3 ; i++) {
				if (edge.equals(edges[i])) result = true;
			}
			return result;
		}
		
		/**
		 * Returns true if the triangle is counter-clockwise from the edges point of view,
		 * looking down the edge starting at vertex vertexIndex
		 * 
		 * @param edge
		 * @param vertexIndex
		 * @return Whether the triangle is counter-clockwise from the edges point of view
		 */
		public boolean isCCW(Edge edge, int vertexIndex) {
			if (this.vertexIndex[0] == vertexIndex) {
				if (this.vertexIndex[1] == edge.otherVertex(vertexIndex)) {
					return true;
				}
			}
			if (this.vertexIndex[1] == vertexIndex) {
				if (this.vertexIndex[2] == edge.otherVertex(vertexIndex)) {
					return true;
				}
			}
			if (this.vertexIndex[2] == vertexIndex) {
				if (this.vertexIndex[0] == edge.otherVertex(vertexIndex)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Find the Edge of this Triangle with the provided vertex indices
		 * 
		 * @param vertexIndex1
		 * @param vertexIndex2
		 * @return The found Edge, or null if none was found in this Triangle
		 */
		public Edge findEdge(int vertexIndex1, int vertexIndex2) {
			Edge result = null;
			if (edges[0].equals(vertexIndex1, vertexIndex2)) result = edges[0];
			if (edges[1].equals(vertexIndex1, vertexIndex2)) result = edges[1];
			if (edges[2].equals(vertexIndex1, vertexIndex2)) result = edges[2];
			return result;
		}
		
		/**
		 * Given two vertex indices of this Triangle, find the third vertex index 
		 * 
		 * @param edge The edge that defines the other two vertex indices
		 * @return The third vertex index
		 */
		public int findThirdVertex(Edge edge) {
			int result = -1;
			if (edge.equals(vertexIndex[0], vertexIndex[1])) result = vertexIndex[2];
			if (edge.equals(vertexIndex[1], vertexIndex[2])) result = vertexIndex[0];
			if (edge.equals(vertexIndex[0], vertexIndex[2])) result = vertexIndex[1];
			return result;
		}
		
		/**
		 * Given two vertex indices of this Triangle, find the third vertex index 
		 * 
		 * @param vertexIndex1
		 * @param vertexIndex2
		 * @return The third vertex index
		 */
		public int findThirdVertex(int vertexIndex1, int vertexIndex2) {
			return findThirdVertex(new Edge(vertexIndex1, vertexIndex2));
		}
		
		/**
		 *  Finds the other edge in this triangle which has the vertex
		 *  i.e. if edge=a,b and vertex=a then otherEdge=a,c is returned 
		 *  
		 *  @param vertexIndex The vertex index who's other Edge we want to find
		 *  @param edge The edge we already know of
		 *  @return The other edge, or null if vertex index or Edge provided are not in the triangle
		 */
		public Edge findOtherSharedEdge(int vertexIndex, Edge edge) {
			if (edges[0].hasVertex(vertexIndex) && (!edges[0].equals(edge))) return edges[0];
			if (edges[1].hasVertex(vertexIndex) && (!edges[1].equals(edge))) return edges[1];
			if (edges[2].hasVertex(vertexIndex) && (!edges[2].equals(edge))) return edges[2];
			return null; // should never happen
		}
		
		/**
		 * @return A String representing this Triangle
		 */
		public String toString() {
			String ret = "{" + vertexIndex[0] + "," + vertexIndex[1] + "," + vertexIndex[2] + "}";
			return ret;
		}
	}
	/**
	 * Maps vertex indices to the enum <code>Location</code>
	 */
	Location[] vertexLocation;
	
	/**
	 * Maps vertex indices to the enum <code>Valence</code>
	 */
	Valence[] vertexValence;
	
	/**
	 * Which rule to use when splitting a certain <code>Edge</code><br>
	 * See Page 73-75 in SIGGRAPH 2000 Course Notes, Denis Zorin, et al.
	 * 
	 * @author Tobias
	 */
	public enum Rule {
		STANDARD { 
			Vector split(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap, ArrayList<Triangle> triangles, Valence[] vertexValence, Location[] vertexLocation) {
				Rule.standard++;
				newVector = regularButterfly(edge, edge.vertexIndex[0], vb, bufferElementSize, vertexEdgeMap[edge.vertexIndex[0]])
				.addLocal(regularButterfly(edge, edge.vertexIndex[1], vb, bufferElementSize, vertexEdgeMap[edge.vertexIndex[1]]));
				return newVector;
			} 
		}, 				
		REGULAR_INTERIOR_CREASE { 
			Vector split(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap, ArrayList<Triangle> triangles, Valence[] vertexValence, Location[] vertexLocation) {
				Rule.regularInteriorCrease++;
				int interiorVertex;
				if (vertexLocation[edge.vertexIndex[0]] == Location.INTERIOR)
					interiorVertex = 0;
				else
					interiorVertex = 1;
				newVector = regularInteriorCrease(edge, edge.vertexIndex[interiorVertex], vb, bufferElementSize, vertexEdgeMap[edge.vertexIndex[interiorVertex]], vertexEdgeMap[edge.vertexIndex[interiorVertex==0?1:0]]);
				return newVector;
			} 
		}, 
		REGULAR_CREASE_CREASE { 
			Vector split(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap, ArrayList<Triangle> triangles, Valence[] vertexValence, Location[] vertexLocation) {
				Rule.regularCreaseCrease++;
				newVector = regularCreaseCrease(edge, vb, bufferElementSize, vertexEdgeMap);
				return newVector;
			} 
		},
		EXTRAORDINARY_AVERAGE { 
			Vector split(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap, ArrayList<Triangle> triangles, Valence[] vertexValence, Location[] vertexLocation) {
				Rule.extraordinaryAverage++;
				newVector = extraordinaryInterior(edge, edge.vertexIndex[0], vb, bufferElementSize, vertexEdgeMap[edge.vertexIndex[0]], triangles)
				.addLocal(extraordinaryInterior(edge, edge.vertexIndex[1], vb, bufferElementSize, vertexEdgeMap[edge.vertexIndex[1]], triangles));
				newVector.multLocal(1f/2f);
				return newVector;
			} 
		}, 	
		EXTRAORDINARY_INTERIOR { 
			Vector split(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap, ArrayList<Triangle> triangles, Valence[] vertexValence, Location[] vertexLocation) {
				Rule.extraordinaryInterior++;
				int extraordinaryVertex;
				if (vertexValence[edge.vertexIndex[0]] == Valence.EXTRAORDINARY)
					extraordinaryVertex = 0;
				else
					extraordinaryVertex = 1;
				newVector = extraordinaryInterior(edge, edge.vertexIndex[extraordinaryVertex], vb, bufferElementSize, vertexEdgeMap[edge.vertexIndex[extraordinaryVertex]], triangles);
				return newVector;
			} 
		}, 	
		EXTRAORDINARY_CREASE { 
			Vector split(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap, ArrayList<Triangle> triangles, Valence[] vertexValence, Location[] vertexLocation) {
				Rule.extraordinaryCrease++;
				int extraordinaryVertex;
				if (vertexValence[edge.vertexIndex[0]] == Valence.EXTRAORDINARY)
					extraordinaryVertex = 0;
				else
					extraordinaryVertex = 1;
				newVector = extraordinaryCrease(edge, edge.vertexIndex[extraordinaryVertex], vb, bufferElementSize, vertexEdgeMap[edge.vertexIndex[extraordinaryVertex]]);
				return newVector;
//				return null;//newVertex;
			} 
		}; 
		
		private final static float WEIGHT = 0f;
		
		Vector newVector = null;
		
		abstract Vector split(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap, ArrayList<Triangle> triangles, Valence[] vertexValence, Location[] vertexLocation);
		
		private static Vector extraordinaryCrease(Edge edge, int vertexIndex, FloatBuffer vb, int bufferElementSize, ArrayList<Edge> edges) {
			Vector result = new Vector(bufferElementSize);
			int valence = edges.size();
			int k = valence - 1;// - 1;
			int v[] = new int[valence];
			int i=-1;
			double theta = Math.PI / (double)k; // (k-1)
			double c = 0f;
			
			Edge tempEdge;
			
			int j = valence-1;
			for (Iterator<Edge> it = edges.iterator() ; it.hasNext() ; ) {
				tempEdge = it.next();
				v[j] = tempEdge.otherVertex(vertexIndex);
				if (tempEdge.equals(edge)) i = j;
				j--;
			}
			
			Vector vert = new Vector(bufferElementSize); 
			vert.populateFromBuffer(vb, vertexIndex);
			//c = 1 - (1/((double)k-1))*Math.sin(theta)*Math.sin((double)i*theta) / (1d-Math.cos(theta)); 
			c = 1d - (Math.sin(theta)*Math.sin((double)i*theta))/((double)k*(1d-Math.cos(theta)));
			vert.multLocal((float)c);
			result.addLocal(vert);
			
			for (j=0; j < valence; j++) {
				
				vert.populateFromBuffer(vb, v[j]);
				
				if (j == 0 || j == (valence-1)) { 
					//c = 1d/4d * Math.cos((double)i*theta) - (1d/4d*(double)(k-1))*Math.sin(2d*theta)*Math.sin(2d*(double)i*theta) / (Math.cos(theta)-Math.cos(2d*theta));
					c = 1d/4d * Math.cos((double)i*theta) - ( Math.sin(2d*theta)*Math.sin(2d*(double)i*theta) )/(4d*(double)k*(Math.cos(theta) - Math.cos(2d*theta)));
					if (j == (valence-1)) c = -c;
				} else {
					//c = (1d/(double)k)*(Math.sin((double)i*theta)*Math.sin((double)j*theta) + (1d/2d) * Math.sin(2d*(double)i*theta)*Math.sin(2d*(double)j*theta));
					c = 1d/(double)k * (Math.sin((double)i*theta)*Math.sin((double)j*theta) + (1d/2d)*Math.sin(2d*(double)i*theta)*Math.sin(2d*(double)j*theta));
				}
				
				vert.multLocal((float)c);
				result.addLocal(vert);
			}
			return result;
		}
		
		private static Vector regularCreaseCrease(Edge edge, FloatBuffer vb, int bufferElementSize, ArrayList<Edge>[] vertexEdgeMap) {	
			Vector result = new Vector(bufferElementSize);
			Vector vert;
			
			boolean rule1 = false;
			
			ArrayList<Edge> edges0, edges1, tempEdges;
			edges0 = vertexEdgeMap[edge.vertexIndex[0]];
			edges1 = vertexEdgeMap[edge.vertexIndex[1]];
			
			// If we are the base of a triangle in a corner 
			// (i.e. if the two vertices are joined by a third crease vertex)
			// We do crease-crease rule 1, else we do 4-point crease rule (which also approximates crease-crease rule 2, for now)
			int vertex0 = -1, vertex1 = -1;
			if (edges0.get(0).otherVertex(edge.vertexIndex[0]) == edges1.get(3).otherVertex(edge.vertexIndex[1])) {
				// Rule 1
				rule1 = true;
				vertex0 = edge.vertexIndex[0]; // corner vertex is the most clockwise edge
				vertex1 = edge.vertexIndex[1]; // corner vertex is the most counter-clockwise edge
			} else if (edges0.get(3).otherVertex(edge.vertexIndex[0]) == edges1.get(0).otherVertex(edge.vertexIndex[1])) {
				// Rule 2
				rule1 = true;
				vertex0 = edge.vertexIndex[1]; // corner vertex is the most clockwise edge
				vertex1 = edge.vertexIndex[0]; // corner vertex is the most counter-clockwise edge
				// Flip also the edgelists
				tempEdges = edges1;	edges1 = edges0; edges0 = tempEdges; tempEdges = null;
			} else {
				// 4-point regular butterfly rule
				rule1 = false;
			}
			if (rule1) {
				// edges0.get(0) is the corner vertex, also the most clockwise edge
				// edges1.get(3) is the corner vertex, also the most counter-clockwise edge
				// Do the first vertex of the splitting edge
				vert = new Vector(bufferElementSize); 
				vert.populateFromBuffer(vb, vertex0);
				vert.multLocal(1f/2f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, edges0.get(2).otherVertex(vertex0));
				vert.multLocal(1f/4f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, edges0.get(3).otherVertex(vertex0));
				vert.multLocal(-1f/8f);
				result.addLocal(vert);
				
				// Do the second vertex of the splitting edge
				vert.populateFromBuffer(vb, vertex1);
				vert.multLocal(1f/2f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, edges1.get(0).otherVertex(vertex1));
				vert.multLocal(-1f/8f);
				result.addLocal(vert);
				
				// finished Crease-Crease Rule 1
				
			} else {
				// 4-point regular butterfly rule ( +---+-*-+---+ )
				if (edges0.get(0).equals(edge)) {
					vertex0 = edge.vertexIndex[1]; 
					vertex1 = edge.vertexIndex[0]; 
					// Flip also the edgelists
					tempEdges = edges1;	edges1 = edges0; edges0 = tempEdges; tempEdges = null;
				} else {
					vertex0 = edge.vertexIndex[0]; 
					vertex1 = edge.vertexIndex[1]; 
				}
				vert = new Vector(bufferElementSize); 
				// Do the first vertex of the splitting edge
				vert.populateFromBuffer(vb, vertex0);
				vert.multLocal(9f/16f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, edges0.get(0).otherVertex(vertex0));
				vert.multLocal(-1f/16f);
				result.addLocal(vert);
				
				// Do the second vertex of the splitting edge
				vert.populateFromBuffer(vb, vertex1);
				vert.multLocal(9f/16f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, edges1.get(3).otherVertex(vertex1));
				vert.multLocal(-1f/16f);
				result.addLocal(vert);
				
				// finished 4-point regular butterfly rule
			}
			return result;
		}
		
		private static Vector regularInteriorCrease(Edge edge, int vertexIndex, FloatBuffer vb, int bufferElementSize, ArrayList<Edge> interiorEdges, ArrayList<Edge> creaseEdges) {
			Vector result = new Vector(bufferElementSize);
			Vector vert;
			
			int v[] = new int[6];
			
			// First take care of the interior vertex
			// Fast forward the edgeMap to the edge we're starting at
			Iterator<Edge> it = interiorEdges.iterator();
			while (it.hasNext() && (!edge.equals(it.next())));		
			
			// circle around the interior vertex counter clock wise and find all vertices 
			for (int i=0 ; i<6 ; i++) {
				if (!it.hasNext()) 
					it = interiorEdges.iterator();
				v[i] = it.next().otherVertex(vertexIndex);
			}
			// v[0] is the first vertex counted CCW from the edge to split
			vert = new Vector(bufferElementSize); 
			vert.populateFromBuffer(vb, vertexIndex);
			vert.multLocal(5f/8f);
			result.addLocal(vert);
			vert.populateFromBuffer(vb, v[0]);
			vert.multLocal(3f/16f);
			result.addLocal(vert);
			vert.populateFromBuffer(vb, v[1]);
			vert.multLocal(-1f/8f);
			result.addLocal(vert);
			vert.populateFromBuffer(vb, v[3]);
			vert.multLocal(-1f/16f);
			result.addLocal(vert);
			vert.populateFromBuffer(vb, v[4]);
			vert.multLocal(1f/16f);
			result.addLocal(vert);
			vert.populateFromBuffer(vb, v[5]);
			vert.multLocal(3f/8f);
			result.addLocal(vert);
			
			// now the crease/boundary vertex
			// Since it's a crease-edge we know that the first index in the map is the edge 
			// that is the most clock-wise 
			vert.populateFromBuffer(vb, creaseEdges.get(0).otherVertex(edge.otherVertex(vertexIndex)));
			vert.multLocal(-1f/16f);
			result.addLocal(vert);			
			
			return result;
		}
		
		private static Vector extraordinaryInterior(Edge edge, int vertexIndex, FloatBuffer vb, int bufferElementSize, ArrayList<Edge> edges, ArrayList<Triangle> triangles) {
			Vector result = new Vector(bufferElementSize);
			int valence = edges.size();
			int v[] = new int[valence];
			Edge tempEdge = null;
			
			// spola fram i edgeMappen till edgen a1-a2
			Iterator<Edge> it = edges.iterator();
			while (it.hasNext() && (!edge.equals(tempEdge = it.next())));			
			
			// circle around the vertex counter clock-wise and find all vertices 
			for (int i=0 ; i<valence ; i++) {
				v[i] = tempEdge.otherVertex(vertexIndex);
				if (!it.hasNext()) 
					it = edges.iterator();
				tempEdge = it.next();
			}
			
			
			Vector vert = new Vector(bufferElementSize);
			if (valence == 3) {
				vert.populateFromBuffer(vb, vertexIndex);
				vert.multLocal(3f/4f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, v[0]);
				vert.multLocal(5f/12f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, v[1]);
				vert.multLocal(-(1f/12f));
				result.addLocal(vert);
				vert.populateFromBuffer(vb, v[2]);
				vert.multLocal(-(1f/12f));
				result.addLocal(vert);
				
			} else if (valence == 4) {
				vert.populateFromBuffer(vb, vertexIndex);
				vert.multLocal(3f/4f);
				result.addLocal(vert);
				vert.populateFromBuffer(vb, v[0]);
				vert.multLocal(3f/8f);
				result.addLocal(vert);
				//vert.populateFromBuffer(vb, v[1]);
				//vert.multLocal(0f);
				//result.addLocal(vert);
				vert.populateFromBuffer(vb, v[2]);
				vert.multLocal(-1f/8f);
				result.addLocal(vert);
				//vert.populateFromBuffer(vb, v[3]);
				//vert.multLocal(0f);
				//result.addLocal(vert);
			} else {
				vert.populateFromBuffer(vb, vertexIndex);
				vert.multLocal(3f/4f);
				result.addLocal(vert);
				int n;
				n = valence;
				for (int j=0; j<n; j++) {
					vert.populateFromBuffer(vb, v[j]);
					vert.multLocal((float)(	1f/4f + 
							Math.cos(2f*Math.PI*(float)j/(float)n) + 
							1f/2f * Math.cos(4f*Math.PI*(float)j/(float)n)));
					vert.multLocal( 1f/(float)n );
					result.addLocal(vert);
				}
			}
			return result;
		}
		
		
		private static Vector regularButterfly(Edge edge, int vertexIndex, FloatBuffer vb, int bufferElementSize, ArrayList<Edge> edges) {
			Vector result = new Vector(bufferElementSize);
			int v[] = new int[6]; // v[0] = b1, 1=c1, 2=d, 3=c2, 4=b2, 5=other_a
			
			// spola fram i edgeMappen till edgen a1-a2
			Iterator<Edge> it = edges.iterator();
			while (it.hasNext() && (!edge.equals(it.next())));		
			
			// circle around the vertex counter clock wise and find all vertices 
			for (int i=0 ; i<6 ; i++) {
				if (!it.hasNext()) 
					it = edges.iterator();
				v[i] = it.next().otherVertex(vertexIndex);
			}		
			
			Vector vert = new Vector(bufferElementSize);
			vert.populateFromBuffer(vb, vertexIndex);
			vert.multLocal(1f/2f - WEIGHT);
			result.addLocal(vert);
			
			vert.populateFromBuffer(vb, v[0]);
			vert.multLocal((1f/8f + 2*WEIGHT) / 2); // divided by 2 because it will be visited again
			result.addLocal(vert);
			
			vert.populateFromBuffer(vb, v[1]);
			vert.multLocal((-1f/16f) - WEIGHT);
			result.addLocal(vert);
			
			vert.populateFromBuffer(vb, v[2]);
			vert.multLocal(WEIGHT);
			result.addLocal(vert);
			
			vert.populateFromBuffer(vb, v[3]);
			vert.multLocal((-1f/16f) - WEIGHT);
			result.addLocal(vert);
			
			vert.populateFromBuffer(vb, v[4]);
			vert.multLocal((1f/8f + 2*WEIGHT) / 2); // divided by 2 because it will be visited again
			result.addLocal(vert);
			
			return result;
		}
		
		static int standard = 0;
		static int regularCreaseCrease = 0;
		static int regularInteriorCrease = 0;
		static int extraordinaryAverage = 0;
		static int extraordinaryInterior = 0;
		static int extraordinaryCrease = 0;
		
		/**
		 * @return A String showing some statistics on the number of types of vertices of the subdivision
		 */
		public static String stats() {
			return "standard = " + standard + ", regularCreaseCrease = " + regularCreaseCrease + ", regularInteriorCrease = " + regularInteriorCrease + ", extraordinaryAverage = " + extraordinaryAverage + ", extraordinaryInterior = " + extraordinaryInterior + ", extraordinaryCrease = " + extraordinaryCrease;
		}
		
	}
	
	/**
	 * Whether the vertex is interior or lies on a boundary
	 * 
	 * @author Tobias
	 */	  
	public enum Location {
		INTERIOR,
		CREASE
	}
	
	/**
	 * Regular: Valence==6 for interior vertices, Valence==4 for boundary/crease vertices
	 * Extraordinary: Everything else
	 * 
	 * @author Tobias
	 */
	public enum Valence {
		REGULAR,
		EXTRAORDINARY;
		
		/**
		 * Calculates whether a vertex is REGULAR or EXTRAORDINARY, given the Location and valence(degree) of the vertex
		 * @param location
		 * @param valence
		 * @return <code>Valence.REGULAR</code> or <code>Valence.EXTRAORDINARY</code>
		 */
		public static Valence getValence(Location location, int valence) {
			if (location == Location.INTERIOR) {
				if (valence == 6) return Valence.REGULAR;
			} else {
				if (valence == 4) return Valence.REGULAR;
			}
			return Valence.EXTRAORDINARY;
		}
	}
	
	/**
	 * Helper class to calculate which <code>Rule</code> to use
	 * when splitting an <code>Edge</code> whose vertices have
	 * <code>Location</code> and <code>Valence</code> 
	 * 
	 * Call: <code>VertexType.getRule(valence1, location1, valence2, location2);</code>
	 * 
	 * @author Tobias
	 */
	public enum VertexType {
		REGULAR_INTERIOR,
		REGULAR_CREASE,
		EXTRAORDINARY_INTERIOR,
		EXTRAORDINARY_CREASE;
		
		private static VertexType getVertexType(Valence valence, Location location) {
			if (location == Location.INTERIOR)
				if (valence == Valence.REGULAR) return REGULAR_INTERIOR; 	else return EXTRAORDINARY_INTERIOR;
			else if (valence == Valence.REGULAR) return REGULAR_CREASE; 	else return EXTRAORDINARY_CREASE;
		}
		
		/**
		 * Calculates which subdivision Rule to use on an edge whose vertices have the provided Locations and 
		 * Valances
		 * 
		 * @param valence1
		 * @param location1
		 * @param valence2
		 * @param location2
		 * @return The Rule to use on an edge with the provided Locations and Valances
		 */
		public static Rule getRule(Valence valence1, Location location1, Valence valence2, Location location2) {
			return getRule(getVertexType(valence1, location1), getVertexType(valence2, location2));
		}
		
		private static Rule getRule(VertexType type1, VertexType type2) {	 
			if (isSame(type1,type2,REGULAR_INTERIOR,REGULAR_INTERIOR))
				return Rule.STANDARD;
			if (isSame(type1,type2,REGULAR_INTERIOR,REGULAR_CREASE))
				return Rule.REGULAR_INTERIOR_CREASE;
			if (isSame(type1,type2,REGULAR_CREASE,REGULAR_CREASE))
				return Rule.REGULAR_CREASE_CREASE;
			if (isSame(type1,type2,EXTRAORDINARY_INTERIOR,EXTRAORDINARY_INTERIOR))
				return Rule.EXTRAORDINARY_AVERAGE;
			if (isSame(type1,type2,EXTRAORDINARY_INTERIOR,EXTRAORDINARY_CREASE))
				return Rule.EXTRAORDINARY_AVERAGE;
			if (isSame(type1,type2,EXTRAORDINARY_CREASE,EXTRAORDINARY_CREASE))
				return Rule.EXTRAORDINARY_AVERAGE;
			if (isSame(type1,type2,REGULAR_INTERIOR,EXTRAORDINARY_INTERIOR))
				return Rule.EXTRAORDINARY_INTERIOR;
			if (isSame(type1,type2,REGULAR_INTERIOR,EXTRAORDINARY_CREASE))
				return Rule.EXTRAORDINARY_CREASE;
			if (isSame(type1,type2,EXTRAORDINARY_INTERIOR,REGULAR_CREASE))
				return Rule.EXTRAORDINARY_INTERIOR;
			if (isSame(type1,type2,REGULAR_CREASE,EXTRAORDINARY_CREASE))
				return Rule.EXTRAORDINARY_CREASE;
			
			logger.info("Warning: unknown rule for " + type1 + " and " + type2);
			return Rule.EXTRAORDINARY_AVERAGE;
		}
		
		/**
		 * Compares the permutations of the types
		 * 
		 * @param type1
		 * @param type2
		 * @param comp1
		 * @param comp2
		 * @return <code>true</code> if they are equivalent
		 */
		public static boolean isSame(VertexType type1, VertexType type2, VertexType comp1, VertexType comp2) {
			return (((type1 == comp1) && (type2 == comp2)) || ((type1 == comp2) && (type2 == comp1)));
		}
	}
}
