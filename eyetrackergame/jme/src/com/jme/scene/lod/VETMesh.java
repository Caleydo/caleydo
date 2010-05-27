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

package com.jme.scene.lod;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 * <code>VETMesh</code> originally ported from David Eberly's c++,
 * modifications and enhancements made from there.<br>
 * <br>
 * This class is used as a base class for ClodCreator, and should have little
 * use outside of a base class for clod meshes.
 * 
 * @author Joshua Slack
 * @version $Id: VETMesh.java 4131 2009-03-19 20:15:28Z blaine.dev $
 * @see ClodCreator
 */

public class VETMesh {

	protected TreeMap<Integer, VertexAttribute> vertexMap; // Integer,VertexAttribute

	protected TreeMap<Edge, EdgeAttribute> edgeMap; // Edge,EdgeAttribute

	protected TreeMap<Triangle, TriangleAttribute> triangleMap;

	// construction
	public VETMesh() {
		vertexMap = new TreeMap<Integer, VertexAttribute>();
		edgeMap = new TreeMap<Edge, EdgeAttribute>();
		triangleMap = new TreeMap<Triangle, TriangleAttribute>();
	}

	// accessors for sizes
	public int getVertexQuantity() {
		return vertexMap.size();
	}

	public int getEdgeQuantity() {
		return edgeMap.size();
	}

	public int getTriangleQuantity() {
		return triangleMap.size();
	}

	// Used for operations that create new meshes from the current one.  This
	// allows derived class construction within the base class operations.
	public VETMesh create() {
		return new VETMesh();
	};

	// Callbacks that are executed when vertices, edges, or triangles are
	// inserted or removed during triangle insertion, triangle removal, or
	// edge collapsing.  The default behavior for the creation is to return
	// null pointers.  A derived class may override the creation and return
	// data to be attached to the specific mesh component.  The default
	// behavior for the destruction is to do nothing.  A derived class may
	// override the destruction and handle the data that was detached from
	// the specific mesh component before its destruction.
	public void onVertexInsert(Integer vert, boolean insert, VertexAttribute att) {
	}

	public void onVertexRemove(Integer vert, boolean remove, VertexAttribute att) {
	}

	public void onEdgeInsert(Edge edge, boolean insert, EdgeAttribute att) {
	}

	public void onEdgeRemove(Edge edge, boolean remove, EdgeAttribute att) {
	}

	public void onTriangleInsert(Triangle tri, boolean insert,
			TriangleAttribute att) {
	}

	public void onTriangleRemove(Triangle tri, boolean remove,
			TriangleAttribute att) {
	}

	public void insertTriangle(int ivert0, int ivert1, int ivert2) {
		boolean hadTri = false, hadV0 = false, hadV1 = false, hadV2 = false, hadE0 = false, hadE1 = false, hadE2 = false;
		Integer vert0 = new Integer(ivert0), vert1 = new Integer(ivert1), vert2 = new Integer(
				ivert2);
		Triangle tri = new Triangle(ivert0, ivert1, ivert2);
		Edge edge0 = new Edge(ivert0, ivert1), edge1 = new Edge(ivert1, ivert2), edge2 = new Edge(
				ivert2, ivert0);

		// insert triangle
		TriangleAttribute triAtt = new TriangleAttribute();
		hadTri = (triangleMap.get(tri) != null);
		triangleMap.put(tri, triAtt);

		// insert vertices
		VertexAttribute vert0att = vertexMap.get(vert0);
		if (vert0att == null)
			vert0att = new VertexAttribute();
		else
			hadV0 = true;
		vert0att.edgeSet.add(edge0);
		vert0att.edgeSet.add(edge2);
		vert0att.triangleSet.add(tri);
		vertexMap.put(vert0, vert0att);

		VertexAttribute vert1att = vertexMap.get(vert1);
		if (vert1att == null)
			vert1att = new VertexAttribute();
		else
			hadV1 = true;
		vert1att.edgeSet.add(edge0);
		vert1att.edgeSet.add(edge1);
		vert1att.triangleSet.add(tri);
		vertexMap.put(vert1, vert1att);

		VertexAttribute vert2att = vertexMap.get(vert2);
		if (vert2att == null)
			vert2att = new VertexAttribute();
		else
			hadV2 = true;
		vert2att.edgeSet.add(edge1);
		vert2att.edgeSet.add(edge2);
		vert2att.triangleSet.add(tri);
		vertexMap.put(vert2, vert2att);

		// insert edges
		EdgeAttribute edge0att = edgeMap.get(edge0);
		if (edge0att == null)
			edge0att = new EdgeAttribute();
		else
			hadE0 = true;
		edge0att.triangleSet.add(tri);
		edgeMap.put(edge0, edge0att);

		EdgeAttribute edge1att = edgeMap.get(edge1);
		if (edge1att == null)
			edge1att = new EdgeAttribute();
		else
			hadE1 = true;
		edge1att.triangleSet.add(tri);
		edgeMap.put(edge1, edge1att);

		EdgeAttribute edge2att = edgeMap.get(edge2);
		if (edge2att == null)
			edge2att = new EdgeAttribute();
		else
			hadE2 = true;
		edge2att.triangleSet.add(tri);
		edgeMap.put(edge2, edge2att);

		// Notify derived classes that mesh components have been inserted.  The
		// notification occurs here to make sure the derived classes have access
		// to the current state of the mesh after the triangle insertion.
		onVertexInsert(vert0, !hadV0, vert0att);
		onVertexInsert(vert1, !hadV1, vert1att);
		onVertexInsert(vert2, !hadV2, vert2att);
		onEdgeInsert(edge0, !hadE0, edge0att);
		onEdgeInsert(edge1, !hadE1, edge1att);
		onEdgeInsert(edge2, !hadE2, edge2att);
		onTriangleInsert(tri, !hadTri, triAtt);
	}

	public void insertTriangle(Triangle tri) {
		insertTriangle(tri.vert[0], tri.vert[1], tri.vert[2]);
	}

	public void removeTriangle(int ivert0, int ivert1, int ivert2) {
		// remove triangle
		Triangle kT = new Triangle(ivert0, ivert1, ivert2);
		TriangleAttribute pkTA = triangleMap.get(kT);
		if (pkTA == null) {
			// triangle does not exist, nothing to do
			return;
		}

		Integer vert0 = new Integer(ivert0), vert1 = new Integer(ivert1), vert2 = new Integer(
				ivert2);

		// update edges
		Edge kE0 = new Edge(ivert0, ivert1), kE1 = new Edge(ivert1, ivert2), kE2 = new Edge(
				ivert2, ivert0);

		EdgeAttribute pkE0 = edgeMap.get(kE0);
		pkE0.triangleSet.remove(kT);

		EdgeAttribute pkE1 = edgeMap.get(kE1);
		pkE1.triangleSet.remove(kT);

		EdgeAttribute pkE2 = edgeMap.get(kE2);
		pkE2.triangleSet.remove(kT);

		// update vertices
		VertexAttribute pkV0 = vertexMap.get(vert0);
		pkV0.triangleSet.remove(kT);

		VertexAttribute pkV1 = vertexMap.get(vert1);
		pkV1.triangleSet.remove(kT);

		VertexAttribute pkV2 = vertexMap.get(vert2);
		pkV2.triangleSet.remove(kT);

		if (pkE0.triangleSet.size() == 0) {
			pkV0.edgeSet.remove(kE0);
			pkV1.edgeSet.remove(kE0);
		}

		if (pkE1.triangleSet.size() == 0) {
			pkV1.edgeSet.remove(kE1);
			pkV2.edgeSet.remove(kE1);
		}

		if (pkE2.triangleSet.size() == 0) {
			pkV0.edgeSet.remove(kE2);
			pkV2.edgeSet.remove(kE2);
		}

		// Notify derived classes that mesh components are about to be destroyed.
		// The notification occurs here to make sure the derived classes have
		// access to the current state of the mesh before the triangle removal.

		boolean bDestroy = pkV0.edgeSet.size() == 0
				&& pkV0.triangleSet.size() == 0;
		onVertexRemove(vert0, bDestroy, pkV0);
		if (bDestroy)
			vertexMap.remove(vert0);

		bDestroy = pkV1.edgeSet.size() == 0 && pkV1.triangleSet.size() == 0;
		onVertexRemove(vert1, bDestroy, pkV1);
		if (bDestroy)
			vertexMap.remove(vert1);

		bDestroy = pkV2.edgeSet.size() == 0 && pkV2.triangleSet.size() == 0;
		onVertexRemove(vert2, bDestroy, pkV2);
		if (bDestroy)
			vertexMap.remove(vert2);

		bDestroy = pkE0.triangleSet.size() == 0;
		onEdgeRemove(kE0, bDestroy, pkE0);
		if (bDestroy)
			edgeMap.remove(kE0);

		bDestroy = pkE1.triangleSet.size() == 0;
		onEdgeRemove(kE1, bDestroy, pkE1);
		if (bDestroy)
			edgeMap.remove(kE1);

		bDestroy = pkE2.triangleSet.size() == 0;
		onEdgeRemove(kE2, bDestroy, pkE2);
		if (bDestroy)
			edgeMap.remove(kE2);

		onTriangleRemove(kT, true, pkTA);
		triangleMap.remove(kT);
	}

	public void removeTriangle(Triangle tri) {
		removeTriangle(tri.vert[0], tri.vert[1], tri.vert[2]);
	}

	// This should be called before Mesh destruction if a derived class has
	// allocated vertex, edge, or triangle data and attached it to the mesh
	// components.  Since the creation and destruction callbacks are virtual,
	// any insert/remove operations in the base Mesh destructor will only
	// call the base virtual callbacks, not any derived-class ones.  An
	// alternative to calling this is that the derived class maintain enough
	// information to know which data objects to destroy during its own
	// destructor call.

	public void removeAllTriangles() {
		Object[] tris = triangleMap.keySet().toArray();
		for (int x = 0; x < tris.length; x++) {
			Triangle tri = (Triangle) tris[x];
			int iV0 = tri.vert[0];
			int iV1 = tri.vert[1];
			int iV2 = tri.vert[2];
			removeTriangle(iV0, iV1, iV2);
		}
	}

	// vertex attributes
	public TreeMap getVertexMap() {
		return vertexMap;
	}

	// edge attributes
	public TreeMap<Edge, EdgeAttribute> getEdgeMap() {
		return edgeMap;
	}

	public ExVector getTriangles(int vert0, int vert1) { //<Triangle>
		EdgeAttribute edgeAtt = edgeMap.get(new Edge(vert0,
				vert1));
		return (edgeAtt != null ? edgeAtt.triangleSet : null);
	}

	// triangle attributes
	public TreeMap<Triangle, TriangleAttribute> getTriangleMap() {
		return triangleMap;
	}

	// The mesh is manifold if each edge has at most two adjacent triangles.
	// It is possible that the mesh has multiple connected components.
	public boolean isManifold() {
		Iterator<EdgeAttribute> it = edgeMap.values().iterator();
		while (it.hasNext()) {
			EdgeAttribute ea = it.next();
			if (ea.triangleSet.size() > 2)
				return false;
		}
		return true;
	}

	// The mesh is closed if each edge has exactly two adjacent triangles.
	// It is possible that the mesh has multiple connected components.
	public boolean isClosed() {
		Iterator<EdgeAttribute> it = edgeMap.values().iterator();
		while (it.hasNext()) {
			EdgeAttribute ea = it.next();
			if (ea.triangleSet.size() != 2)
				return false;
		}
		return true;
	}

	// The mesh is connected if each triangle can be reached from any other
	// triangle by a traversal.
	public boolean isConnected() {
		// Do a depth-first search of the mesh.  It is connected if and only if
		// all of the triangles are visited on a single search.

		int iTSize = triangleMap.size();
		if (iTSize == 0)
			return true;

		// for marking visited triangles during the traversal
		TreeMap<Triangle, Boolean> kVisitedMap = new TreeMap<Triangle, Boolean>();
		Iterator<Triangle> it = triangleMap.keySet().iterator();
		while (it.hasNext()) {
			kVisitedMap.put(it.next(), Boolean.FALSE);
		}

		// start the traversal at any triangle in the mesh
		Stack<Triangle> kStack = new Stack<Triangle>();
		kStack.push((Triangle) triangleMap.keySet().toArray()[0]);
		kVisitedMap.put(kStack.get(0), Boolean.TRUE);
		iTSize--;

		Iterator triIt;
		while (!kStack.empty()) {
			// start at the current triangle
			Triangle kT = kStack.pop();

			for (int i = 0; i < 3; i++) {
				// get an edge of the current triangle
				EdgeAttribute pkE = edgeMap.get(new Edge(
						kT.vert[i], kT.vert[(i + 1) % 3]));

				// visit each adjacent triangle
				ExVector rkTSet = (ExVector) pkE.triangleSet.clone(); // <Triangle>
				triIt = rkTSet.iterator();
				while (triIt.hasNext()) {
					Triangle rkTAdj = (Triangle) triIt.next();
					if (Boolean.FALSE.equals(kVisitedMap.get(rkTAdj))) {
						// this adjacent triangle not yet visited
						kStack.push(rkTAdj);
						kVisitedMap.put(rkTAdj, Boolean.TRUE);
						iTSize--;
					}
				}
			}
		}

		return iTSize == 0;
	}

	// Extract the connected components from the mesh.  For large data sets,
	// the array of VETMesh can use a lot of memory.  Instead use the
	// second form that just stores a sorted connectivity array.  Let N be
	// the number of components.  The value Index[i] indicates the starting
	// index for component i with 0 <= i < N, so it is always the case that
	// Index[0] = 0.  The value Index[N] is the total number of indices in
	// the raiConnect array.  The quantity of indices for component i is
	// Q(i) = Index[i+1]-Index[i] for 0 <= i < N.  The application is
	// responsible for deleting raiConnect.
	public void getComponents(Vector<VETMesh> store) {
		// Do a depth-first search of the mesh to find connected components.
		int iTSize = triangleMap.size();
		if (iTSize == 0)
			return;

		// for marking visited triangles during the traversal
		TreeMap<Triangle, Boolean> kVisitedMap = new TreeMap<Triangle, Boolean>();
		Iterator<Triangle> it = triangleMap.keySet().iterator();
		while (it.hasNext()) {
			kVisitedMap.put(it.next(), Boolean.FALSE);
		}

		while (iTSize > 0) {
			// find an unvisited triangle in the mesh
			Stack<Triangle> kStack = new Stack<Triangle>();
			Iterator<Triangle> visIt = kVisitedMap.keySet().iterator();
			while (visIt.hasNext()) {
				Triangle tri = visIt.next();
				if (Boolean.FALSE.equals(kVisitedMap.get(tri))) {
					// this triangle not yet visited
					kStack.push(tri);
					kVisitedMap.put(tri, Boolean.TRUE);
					iTSize--;
					break;
				}
			}

			// traverse the connected component of the starting triangle
			VETMesh pkComponent = create();
			Iterator triIt;
			while (!kStack.empty()) {
				// start at the current triangle
				Triangle kT = kStack.pop();
				pkComponent.insertTriangle(kT);

				for (int i = 0; i < 3; i++) {
					// get an edge of the current triangle
					Edge kE = new Edge(kT.vert[i], kT.vert[(i + 1) % 3]);
					EdgeAttribute pkE = edgeMap.get(kE);

					// visit each adjacent triangle
					ExVector rkTSet = (ExVector) pkE.triangleSet.clone(); // <Triangle>
					triIt = rkTSet.iterator();
					while (triIt.hasNext()) {
						Triangle rkTAdj = (Triangle) triIt.next();
						if (Boolean.FALSE.equals(kVisitedMap.get(rkTAdj))) {
							// this adjacent triangle not yet visited
							kStack.push(rkTAdj);
							kVisitedMap.put(rkTAdj, Boolean.TRUE);
							iTSize--;
						}
					}
				}
			}
			store.add(pkComponent);
		}
	}

	public void getComponents(Vector<Integer> rkIndex, int[] raiConnect) {
		rkIndex.clear();

		// Do a depth-first search of the mesh to find connected components.
		int iTSize = triangleMap.size();
		if (iTSize == 0) {
			raiConnect = null;
			return;
		}

		int iIQuantity = 3 * iTSize;
		int iIndex = 0;
		raiConnect = new int[iIQuantity];

		// for marking visited triangles during the traversal
		TreeMap<Triangle, Boolean> kVisitedMap = new TreeMap<Triangle, Boolean>(); 
		Iterator<Triangle> it = triangleMap.keySet().iterator();
		while (it.hasNext()) {
			kVisitedMap.put(it.next(), Boolean.FALSE);
		}

		while (iTSize > 0) {
			// find an unvisited triangle in the mesh
			Stack<Triangle> kStack = new Stack<Triangle>();
			Iterator<Triangle> visIt = kVisitedMap.keySet().iterator();
			while (visIt.hasNext()) {
				Triangle tri = visIt.next();
				if (Boolean.FALSE.equals(kVisitedMap.get(tri))) {
					// this triangle not yet visited
					kStack.push(tri);
					kVisitedMap.put(tri, Boolean.TRUE);
					iTSize--;
					break;
				}
			}

			// traverse the connected component of the starting triangle
			VETMesh pkComponent = create();
			Iterator triIt;
			while (!kStack.empty()) {
				// start at the current triangle
				Triangle kT = kStack.pop();
				pkComponent.insertTriangle(kT);

				for (int i = 0; i < 3; i++) {
					// get an edge of the current triangle
					Edge kE = new Edge(kT.vert[i], kT.vert[(i + 1) % 3]);
					EdgeAttribute pkE = edgeMap.get(kE);

					// visit each adjacent triangle
					ExVector rkTSet = (ExVector) pkE.triangleSet.clone(); // <Triangle>
					triIt = rkTSet.iterator();
					while (triIt.hasNext()) {
						Triangle rkTAdj = (Triangle) triIt.next();
						if (Boolean.FALSE.equals(kVisitedMap.get(rkTAdj))) {
							// this adjacent triangle not yet visited
							kStack.push(rkTAdj);
							kVisitedMap.put(rkTAdj, Boolean.TRUE);
							iTSize--;
						}
					}
				}
			}

			// store the connectivity information for this component
			TreeSet<Triangle> kTSet = new TreeSet<Triangle>();
			pkComponent.getTriangles(kTSet);
			pkComponent = null;

			rkIndex.add(new Integer(iIndex));
			Iterator<Triangle> tsetIter = kTSet.iterator();
			while (tsetIter.hasNext()) {
				Triangle rkT = tsetIter.next();
				raiConnect[iIndex++] = rkT.vert[0];
				raiConnect[iIndex++] = rkT.vert[1];
				raiConnect[iIndex++] = rkT.vert[2];
			}
		}

		rkIndex.add(new Integer(iIQuantity));
	}

	// Extract a connected component from the mesh and remove all the
	// triangles of the component from the mesh.  This is useful for computing
	// the components in a very large mesh that uses a lot of memory.  The
	// intention is that the function is called until all components are
	// found.  The typical code is
	//
	//     VETMesh kMesh = <some mesh>;
	//     int iITotalQuantity = 3*kMesh.GetTriangleQuantity();
	//     int* aiConnect = new int[iITotalQuantity];
	//     for (int iIQuantity = 0; iIQuantity < iITotalQuantity; /**/ )
	//     {
	//         int iCurrentIQuantity;
	//         int* aiCurrentConnect = aiConnect + iIQuantity;
	//         kMesh.RemoveComponent(iCurrentIQuantity,aiCurrentConnect);
	//         iIQuantity += iCurrentIQuantity;
	//     }

	public int removeComponent(int[] aiConnect) {
		// Do a depth-first search of the mesh to find connected components.  The
		// input array is assumed to be large enough to hold the component (see
		// the comments in WmlTriangleMesh.h for RemoveComponent).
		int riIQuantity = 0;

		int iTSize = triangleMap.size();
		if (iTSize == 0)
			return riIQuantity;

		// Find the connected component containing the first triangle in the mesh.
		// A set is used instead of a stack to avoid having a large-memory
		// 'visited' map.
		TreeSet<Triangle> kVisited = new TreeSet<Triangle>();
		kVisited.add((Triangle)triangleMap.keySet().toArray()[0]);

		// traverse the connected component
		Iterator triIt;
		while (!kVisited.isEmpty()) {
			// start at the current triangle
			Triangle kT = (Triangle) kVisited.toArray()[0];

			// add adjacent triangles to the set for recursive processing
			for (int i = 0; i < 3; i++) {
				// get an edge of the current triangle
				Edge kE = new Edge(kT.vert[i], kT.vert[(i + 1) % 3]);
				EdgeAttribute pkE = edgeMap.get(kE);

				// visit each adjacent triangle
				ExVector rkTSet = (ExVector) pkE.triangleSet.clone(); // <Triangle>
				triIt = rkTSet.iterator();
				while (triIt.hasNext()) {
					Triangle kTAdj = (Triangle) triIt.next();
					if (!kTAdj.equals(kT))
						kVisited.add(kTAdj);
				}
			}

			// add triangle to connectivity array
			aiConnect[riIQuantity++] = kT.vert[0];
			aiConnect[riIQuantity++] = kT.vert[1];
			aiConnect[riIQuantity++] = kT.vert[2];

			// remove the current triangle (visited, no longer needed)
			kVisited.remove(kT);
			removeTriangle(kT);
		}
		return riIQuantity;
	}

	// Extract the connected components from the mesh, but each component has
	// a consistent ordering across all triangles of that component.  The
	// mesh must be manifold.  The return value is 'true' if and only if the
	// mesh is manifold.  If the mesh has multiple components, each component
	// will have a consistent ordering.  However, the mesh knows nothing about
	// the mesh geometry, so it is possible that ordering across components is
	// not consistent.  For example, if the mesh has two disjoint closed
	// manifold components, one of them could have an ordering that implies
	// outward pointing normals and the other inward pointing normals.
	//
	// NOTE.  It is possible to create a nonorientable mesh such as a Moebius
	// strip.  In this case, GetConsistentComponents will return connected
	// components, but in fact the triangles will not (and can not) be
	// consistently ordered.
	public boolean getConsistentComponents(Vector<VETMesh> store) {
		if (!isManifold())
			return false;

		// Do a depth-first search of the mesh to find connected components.
		int iTSize = triangleMap.size();
		if (iTSize == 0)
			return true;

		// for marking visited triangles during the traversal
		TreeMap<Triangle, Boolean> kVisitedMap = new TreeMap<Triangle, Boolean>();
		Iterator<Triangle> it = triangleMap.keySet().iterator();
		while (it.hasNext()) {
			kVisitedMap.put(it.next(), Boolean.FALSE);
		}

		while (iTSize > 0) {
			// Find an unvisited triangle in the mesh.  Any triangle pushed onto
			// the stack is considered to have a consistent ordering.
			Stack<Triangle> kStack = new Stack<Triangle>();
			Iterator<Triangle> visIt = kVisitedMap.keySet().iterator();
			while (visIt.hasNext()) {
				Triangle tri = visIt.next();
				if (Boolean.FALSE.equals(kVisitedMap.get(tri))) {
					// this triangle not yet visited
					kStack.push(tri);
					kVisitedMap.put(tri, Boolean.TRUE);
					iTSize--;
					break;
				}
			}

			// traverse the connected component of the starting triangle
			VETMesh component = create();
			while (!kStack.empty()) {
				// start at the current triangle
				Triangle kT = kStack.pop();
				component.insertTriangle(kT);

				for (int i = 0; i < 3; i++) {
					// get an edge of the current triangle
					int iV0 = kT.vert[i], iV1 = kT.vert[(i + 1) % 3], iV2;
					Edge kE = new Edge(iV0, iV1);
					EdgeAttribute pkE = edgeMap.get(kE);

					int iSize = pkE.triangleSet.size();
					Triangle pkTAdj = (Triangle) pkE.triangleSet.toArray()[0];
					if (iSize == 2) {
						// get the adjacent triangle to the current one
						if (pkTAdj.equals(kT))
							pkTAdj = (Triangle) pkE.triangleSet.toArray()[1];

						if (Boolean.FALSE.equals(kVisitedMap.get(pkTAdj))) {
							// adjacent triangle not yet visited
							if ((pkTAdj.vert[0] == iV0 && pkTAdj.vert[1] == iV1)
									|| (pkTAdj.vert[1] == iV0 && pkTAdj.vert[2] == iV1)
									|| (pkTAdj.vert[2] == iV0 && pkTAdj.vert[0] == iV1)) {
								// adjacent triangle must be reordered
								iV0 = pkTAdj.vert[0];
								iV1 = pkTAdj.vert[1];
								iV2 = pkTAdj.vert[2];
								kVisitedMap.remove(pkTAdj);
								removeTriangle(iV0, iV1, iV2);
								insertTriangle(iV1, iV0, iV2);
								kVisitedMap.put(new Triangle(iV1, iV0, iV2),
										Boolean.FALSE);

								// refresh the iterators since maps changed
								pkE = edgeMap.get(kE);
								pkTAdj = (Triangle) pkE.triangleSet.toArray()[0];
								if (pkTAdj == kT)
									pkTAdj = (Triangle) pkE.triangleSet
											.toArray()[1];
							}

							kStack.push(pkTAdj);
							kVisitedMap.put(pkTAdj, Boolean.TRUE);
							iTSize--;
						}
					}
				}
			}
			store.add(component);
		}

		return true;
	}

	// Reverse the ordering of all triangles in the mesh.
	public VETMesh getReversedOrderMesh() {
		VETMesh reversed = create();

		Iterator<Triangle> it = triangleMap.keySet().iterator();
		while (it.hasNext()) {
			Triangle tri = it.next();
			reversed.insertTriangle(tri.vert[0], tri.vert[2], tri.vert[1]);
		}

		return reversed;
	}

	// statistics

	public void getVertices(Set<Integer> store) {
		store.clear();
		Iterator<Integer> it = vertexMap.keySet().iterator();
		while (it.hasNext())
			store.add(it.next());
	}

	public Object getData(int vert) {
		VertexAttribute pkV = vertexMap
				.get(new Integer(vert));
		return (pkV != null ? pkV.data : null);
	}

	public ExVector getEdges(int vert) {
		VertexAttribute pkV = vertexMap
				.get(new Integer(vert));
		return (pkV != null ? pkV.edgeSet : null);
	}

	public ExVector getTriangles(int vert) {
		VertexAttribute pkV = vertexMap
				.get(new Integer(vert));
		return (pkV != null ? pkV.triangleSet : null);
	}

	public void getEdges(Set<Edge> store) {
		store.clear();
		Iterator<Edge> it = edgeMap.keySet().iterator();
		while (it.hasNext()) {
			store.add(it.next());
		}
	}

	public Object getData(int vert0, int vert1) {
		EdgeAttribute pkE = edgeMap.get(new Edge(vert0, vert1));
		return (pkE != null ? pkE.data : null);
	}

	public Object getData(Edge edge) {
		return getData(edge.vert[0], edge.vert[1]);
	}

	public void getTriangles(Set<Triangle> store) {
		store.clear();
		Iterator<Triangle> it = triangleMap.keySet().iterator();
		while (it.hasNext()) {
			store.add(it.next());
		}
	}

	public Object getData(int vert0, int vert1, int vert2) {
		TriangleAttribute triAtt = triangleMap
				.get(new Triangle(vert0, vert1, vert2));
		return (triAtt != null ? triAtt.data : null);
	}

	public void setData(int vert0, int vert1, int vert2, Object data) {
		TriangleAttribute triAtt = triangleMap
				.get(new Triangle(vert0, vert1, vert2));
		if (triAtt != null)
			triAtt.data = data;
	}

	public Object getData(Triangle tri) {
		return getData(tri.vert[0], tri.vert[1], tri.vert[2]);
	}

	public void setData(Triangle tri, Object data) {
		setData(tri.vert[0], tri.vert[1], tri.vert[2], data);
	}

	// vertex is <v>
	// edge is <v0,v1> where v0 = min(v0,v1)
	// triangle is <v0,v1,v2> where v0 = min(v0,v1,v2)

	public class Edge implements Comparable {
		int vert[] = new int[2];

		public Edge(int iV0, int iV1) {
			if (iV0 < iV1) {
				// v0 is minimum
				vert[0] = iV0;
				vert[1] = iV1;
			} else {
				// v1 is minimum
				vert[0] = iV1;
				vert[1] = iV0;
			}
		}

		public boolean lessThan(Edge otherEdge) {
			if (vert[1] < otherEdge.vert[1])
				return true;

			if (vert[1] == otherEdge.vert[1])
				return vert[0] < otherEdge.vert[0];

			return false;
		}

		public boolean equals(Object obj) {
			Edge otherEdge = (Edge) obj;
			return (vert[0] == otherEdge.vert[0])
					&& (vert[1] == otherEdge.vert[1]);
		}

		public int compareTo(Object o) {
			Edge otherEdge = (Edge) o;
			if (lessThan(otherEdge))
				return -1;
			else if (equals(otherEdge))
				return 0;
			else
				return 1;
		}
	};

	public class Triangle implements Comparable {
		public int vert[] = new int[3];

		public Triangle(int vert0, int vert1, int vert2) {
			if (vert0 < vert1) {
				if (vert0 < vert2) {
					// vert0 is minimum
					vert[0] = vert0;
					vert[1] = vert1;
					vert[2] = vert2;
				} else {
					// vert2 is minimum
					vert[0] = vert2;
					vert[1] = vert0;
					vert[2] = vert1;
				}
			} else {
				if (vert1 < vert2) {
					// vert1 is minimum
					vert[0] = vert1;
					vert[1] = vert2;
					vert[2] = vert0;
				} else {
					// vert2 is minimum
					vert[0] = vert2;
					vert[1] = vert0;
					vert[2] = vert1;
				}
			}
		}

		public boolean lessThan(Triangle otherTri) {
			if (vert[2] < otherTri.vert[2])
				return true;

			if (vert[2] == otherTri.vert[2]) {
				if (vert[1] < otherTri.vert[1])
					return true;

				if (vert[1] == otherTri.vert[1])
					return vert[0] < otherTri.vert[0];
			}

			return false;
		}

		public boolean equals(Object obj) {
			Triangle otherTri = (Triangle) obj;
			return (vert[0] == otherTri.vert[0])
					&& ((vert[1] == otherTri.vert[1] && vert[2] == otherTri.vert[2]) || (vert[1] == otherTri.vert[2] && vert[2] == otherTri.vert[1]));
		}

		public int compareTo(Object o) {
			Triangle otherTri = (Triangle) o;
			if (lessThan(otherTri))
				return -1;
			else if (equals(otherTri))
				return 0;
			else
				return 1;
		}
	};

	public class VertexAttribute {
		public ExVector edgeSet; //<Edge>

		public ExVector triangleSet; //<Triangle>

		public Object data;

		public VertexAttribute() {
			edgeSet = new ExVector(8, 8);
			triangleSet = new ExVector(8, 8);
			data = null;
		}
	};

	public class EdgeAttribute {
		public ExVector triangleSet; //<Triangle>

		public Object data;

		public EdgeAttribute() {
			triangleSet = new ExVector(2, 2);
			data = null;
		}
	};

	public class TriangleAttribute {
		public Object data;

		public TriangleAttribute() {
			data = null;
		}
	};
}
