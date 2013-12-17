/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * A path of {@link PathwayVertexRep}s that consists of one or multiple {@link PathSegment}s.
 *
 * @author Christian
 *
 */
public class PathwayPath extends ArrayList<PathSegment> {

	/**
	 *
	 */
	private static final long serialVersionUID = -4401074951729509240L;

	/**
	 *
	 */
	public PathwayPath() {
		// TODO Auto-generated constructor stub
	}

	public PathwayPath(int capacity) {
		super(capacity);
	}

	public PathwayPath(Collection<? extends PathSegment> segments) {
		super(segments);
	}

	public List<PathSegment> getSegmentsOfPathway(PathwayGraph pathway) {
		List<PathSegment> segments = new ArrayList<>();
		for (PathSegment segment : this) {
			if (segment.getPathway() == pathway) {
				segments.add(segment);
			}
		}
		return segments;
	}

	public boolean hasPathway(PathwayGraph pathway) {
		for (PathSegment segment : this) {
			if (segment.getPathway() == pathway) {
				return true;
			}
		}
		return false;
	}

	public boolean checkIntegrity() {
		PathSegment prevSegment = null;
		for (PathSegment segment : this) {
			if (segment == null || segment.isEmpty() || !segment.checkIntegrity()) {
				return false;
			}
			if (prevSegment != null && (prevSegment.size() == 1 || (segment.size() == 1 && getLast() != segment))
					&& PathwayManager.get().areVerticesEquivalent(segment.getFirst(), prevSegment.getLast())) {
				return false;
			}

			if (prevSegment != null && prevSegment.getPathway() == segment.getPathway()) {
				DefaultEdge edge1 = segment.getPathway().getEdge(prevSegment.getLast(), segment.getFirst());
				DefaultEdge edge2 = segment.getPathway().getEdge(segment.getFirst(), prevSegment.getLast());
				if (edge1 != null || edge2 != null) {
					return false;
				}
			}

			prevSegment = segment;
		}
		return true;
	}

	public void ensurePathLevelIntegrity() {
		boolean pathChanged = false;

		do {
			pathChanged = false;

			PathSegment prevSegment = null;
			for (int i = 0; i < size(); i++) {
				PathSegment segment = get(i);

				if (segment == null || segment.isEmpty()) {
					remove(i);
					pathChanged = true;
					break;
				}

				if (prevSegment != null && prevSegment.size() == 1
						&& PathwayManager.get().areVerticesEquivalent(segment.getFirst(), prevSegment.getLast())) {
					remove(i - 1);
					pathChanged = true;
					break;
				}

				if (prevSegment != null && segment.size() == 1 && getLast() != segment
						&& PathwayManager.get().areVerticesEquivalent(segment.getFirst(), prevSegment.getLast())) {
					remove(i);
					pathChanged = true;
					break;
				}

				if (prevSegment != null && prevSegment.getPathway() == segment.getPathway()) {
					DefaultEdge edge1 = segment.getPathway().getEdge(prevSegment.getLast(), segment.getFirst());
					DefaultEdge edge2 = segment.getPathway().getEdge(segment.getFirst(), prevSegment.getLast());
					if (edge1 != null || edge2 != null) {
						prevSegment.addAll(segment);
						remove(i);
						pathChanged = true;
						break;
					}
				}

				prevSegment = segment;
			}
		} while (pathChanged);
	}

	/**
	 * @param segments
	 * @return One list of {@link PathwayVertexRep}s that contains all objects of the list of lists.
	 */
	public static List<PathwayVertexRep> flattenSegments(PathwayPath segments) {
		List<PathwayVertexRep> vertexReps = new ArrayList<>();
		for (PathSegment segment : segments) {
			vertexReps.addAll(segment);
		}
		return vertexReps;
	}

	/**
	 * Determines, whether the specified target path segments are shown by the source path segments. If the specified
	 * pathway is not null, only segments referring to this pathway are considered.
	 *
	 * @param segments
	 * @return
	 */
	public static boolean isPathShown(PathwayPath sourcePathSegments, PathwayPath targetPathSegments,
			PathwayGraph pathway) {
		List<PathwayVertexRep> sourceSegments = flattenSegments(sourcePathSegments);
		List<PathwayVertexRep> targetSegments = flattenSegments(targetPathSegments);
		int startIndex = 0;
		boolean equalityStarted = false;
		for (PathwayVertexRep vTarget : targetSegments) {
			// Ignore other pathway paths if this renderer only repersents a single pathway
			if (pathway != null && pathway != vTarget.getPathway())
				continue;
			if (startIndex >= sourceSegments.size())
				return false;
			for (int i = startIndex; i < sourceSegments.size(); i++) {
				PathwayVertexRep vSource = sourceSegments.get(i);
				startIndex = i + 1;
				// Ignore other pathway paths if this renderer only repersents a single pathway
				if (pathway != null && pathway != vSource.getPathway())
					continue;
				if (vTarget == vSource) {
					equalityStarted = true;
					break;
				} else if (equalityStarted) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Determines whether the specified path segments contain a vertex rep.
	 *
	 * @param pathSegments
	 * @param vertexRep
	 * @return
	 */
	public static boolean containsVertexRep(PathwayPath pathSegments, PathwayVertexRep vertexRep) {
		List<PathwayVertexRep> segments = flattenSegments(pathSegments);

		for (PathwayVertexRep vSource : segments) {
			if (vSource == vertexRep)
				return true;
		}
		return false;
	}

	/**
	 * Determines the number of equal vertices of the specified paths.
	 *
	 * @param segments
	 * @return
	 */
	public static int getNumEqualVertices(PathwayPath sourcePathSegments, PathwayPath targetPathSegments) {
		List<PathwayVertexRep> sourceSegments = flattenSegments(sourcePathSegments);
		List<PathwayVertexRep> targetSegments = flattenSegments(targetPathSegments);

		int numEqualVertices = 0;
		for (PathwayVertexRep vTarget : targetSegments) {
			for (PathwayVertexRep vSource : sourceSegments) {
				if (vSource == vTarget) {
					numEqualVertices++;
					break;
				}
			}
		}

		return numEqualVertices;
	}

	/**
	 * Determines the set of vertices the specified paths have in common.
	 *
	 * @param sourcePathSegments
	 * @param targetPathSegments
	 * @return
	 */
	public static Set<PathwayVertexRep> getCommonVertices(PathwayPath sourcePathSegments, PathwayPath targetPathSegments) {
		List<PathwayVertexRep> sourceSegments = flattenSegments(sourcePathSegments);
		List<PathwayVertexRep> targetSegments = flattenSegments(targetPathSegments);

		Set<PathwayVertexRep> commonVertices = new LinkedHashSet<>();
		for (PathwayVertexRep vTarget : targetSegments) {
			for (PathwayVertexRep vSource : sourceSegments) {
				if (vSource == vTarget) {
					commonVertices.add(vSource);
					break;
				}
			}
		}

		return commonVertices;
	}

	public PathSegment getFirst() {
		return isEmpty() ? null : get(0);
	}

	public PathSegment getLast() {
		return isEmpty() ? null : get(size() - 1);
	}

}
