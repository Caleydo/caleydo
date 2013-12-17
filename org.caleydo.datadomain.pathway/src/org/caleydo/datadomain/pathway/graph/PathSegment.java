/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

/**
 * Represents a segment of a {@link PathwayPath}. All {@link PathwayVertexRep} in a segment must be from the same
 * {@link PathwayGraph}, and subsequent vertex reps must be connected by a {@link DefaultEdge}. The direction of the
 * edges does not matter. Use {@link #checkIntegrity()} to find out whether a segment adheres to these rules.
 *
 * @author Christian
 *
 */
public class PathSegment extends ArrayList<PathwayVertexRep> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3548000841308810645L;

	/**
	 * The pathway of this segment
	 */
	protected PathwayGraph pathway;

	public PathSegment() {

	}

	public PathSegment(int capacity) {
		super(capacity);
	}

	public PathSegment(GraphPath<PathwayVertexRep, DefaultEdge> pathSegment) {
		addAll(Graphs.getPathVertexList(pathSegment));
	}

	public PathSegment(Collection<? extends PathwayVertexRep> vertexReps) {
		super();
		addAll(vertexReps);
	}

	@Override
	public void add(int index, PathwayVertexRep element) {
		super.add(index, element);
	}

	@Override
	public boolean add(PathwayVertexRep e) {
		if (pathway == null && e != null)
			pathway = e.getPathway();
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends PathwayVertexRep> c) {
		if (pathway == null && c != null && !c.isEmpty())
			pathway = c.iterator().next().getPathway();
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends PathwayVertexRep> c) {
		if (pathway == null && c != null && !c.isEmpty())
			pathway = c.iterator().next().getPathway();
		return super.addAll(index, c);
	}

	protected boolean checkIntegrity() {
		PathwayVertexRep prevVertex = null;

		for (PathwayVertexRep v : this) {
			if (v == null || pathway != v.getPathway()) {
				return false;
			}
			if (prevVertex != null) {
				DefaultEdge edge1 = pathway.getEdge(prevVertex, v);
				DefaultEdge edge2 = pathway.getEdge(prevVertex, v);
				if (edge1 == null && edge2 == null)
					return false;

			}
			prevVertex = v;
		}

		return true;
	}

	@Override
	public void clear() {
		super.clear();
		pathway = null;
	}

	@Override
	public PathwayVertexRep remove(int index) {
		PathwayVertexRep v = super.remove(index);
		if (isEmpty())
			pathway = null;
		return v;
	}

	@Override
	public boolean remove(Object o) {
		boolean b = super.remove(o);
		if (isEmpty())
			pathway = null;
		return b;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean b = super.removeAll(c);
		if (isEmpty())
			pathway = null;
		return b;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean b = super.retainAll(c);
		if (isEmpty())
			pathway = null;
		return b;
	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * Creates a {@link GraphPath} based on this segment. If two sequent vertices are connected by two edges in opposite
	 * direction the forward edge, i.e., the edge that originates from the vertex with the lower index in this segment,
	 * is used.
	 *
	 * @return
	 */
	public GraphPath<PathwayVertexRep, DefaultEdge> asGraphPath() {
		List<DefaultEdge> edges = new ArrayList<>();
		PathwayVertexRep prevVertex = null;
		for (PathwayVertexRep v : this) {
			if (prevVertex != null) {
				DefaultEdge edge = pathway.getEdge(prevVertex, v);
				if (edge == null)
					edge = pathway.getEdge(v, prevVertex);
				edges.add(edge);
			}
			prevVertex = v;
		}
		return new GraphPathImpl<PathwayVertexRep, DefaultEdge>(pathway, isEmpty() ? null : get(0), isEmpty() ? null
				: get(size() - 1), edges, 0);
	}

	public PathwayVertexRep getFirst() {
		return isEmpty() ? null : get(0);
	}

	public PathwayVertexRep getLast() {
		return isEmpty() ? null : get(size() - 1);
	}

}
