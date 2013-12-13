/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;

/**
 * @author Christian
 *
 */
public class PathwayPathHandler implements IVertexRepSelectionListener {

	protected static final int MAX_ALTERNATIVE_PATHS = 8;

	protected boolean isPathSelectionMode = false;
	protected String eventSpace;
	protected PathwayVertexRep startVertexRep;
	protected PathwayPath selectedPath = new PathwayPath();
	protected IPathwayRepresentation pathwayRepresentation;
	protected Set<IPathUpdateListener> listeners = new LinkedHashSet<>();

	public PathwayPathHandler(IPathwayRepresentation pathwayRepresentation) {
		this.pathwayRepresentation = pathwayRepresentation;
		pathwayRepresentation.addVertexRepSelectionListener(this);
	}

	@ListenTo
	public void onPathSelectionModeChanged(EnablePathSelectionEvent event) {
		// if (event.getEventSpace() != null && event.getEventSpace().equals(eventSpace)) {
		isPathSelectionMode = event.isPathSelectionMode();
		// }
	}

	@Override
	public void onSelect(PathwayVertexRep vertexRep, Pick pick) {
		if (!isPathSelectionMode
				|| (vertexRep.getType() != EPathwayVertexType.gene
						&& vertexRep.getType() != EPathwayVertexType.compound && vertexRep.getType() != EPathwayVertexType.group))
			return;

		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			if (startVertexRep != null) {
				selectPath(startVertexRep, vertexRep);
			}
			break;
		case CLICKED:
			if (startVertexRep == null) {
				startVertexRep = vertexRep;
				selectedPath.clear();
				selectPath(startVertexRep, vertexRep);
			} else {
				selectPath(startVertexRep, vertexRep);
				startVertexRep = null;
			}

			break;
		default:
			break;
		}
	}

	protected void selectPath(PathwayVertexRep from, PathwayVertexRep to) {
		if (from == to) {
			addToPath(new PathSegment(Arrays.asList(from)));
		} else {
			KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
					pathwayRepresentation.getPathway(), from, MAX_ALTERNATIVE_PATHS);
			List<GraphPath<PathwayVertexRep, DefaultEdge>> paths = pathAlgo.getPaths(to);
			if (paths != null && !paths.isEmpty()) {
				addToPath(new PathSegment(paths.get(0)));
			}
		}
	}

	protected void addToPath(PathSegment segment) {
		if (selectedPath.isEmpty()) {
			selectedPath.add(segment);
		} else {
			PathSegment lastSegment = selectedPath.get(selectedPath.size() - 1);
			if (lastSegment.getPathway() == pathwayRepresentation.getPathway()) {
				PathwayVertexRep lastVertex = lastSegment.get(lastSegment.size() - 1);
				if (lastVertex == segment.get(0)) {
					lastSegment.remove(lastSegment.size() - 1);
					lastSegment.addAll(segment);
				}
			} else {
				selectedPath.add(segment);
			}
		}
		notifyListeners();
	}

	/**
	 * @return the selectedPath, see {@link #selectedPath}
	 */
	public PathwayPath getSelectedPath() {
		return selectedPath;
	}

	public void addPathUpdateListener(IPathUpdateListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	public void removePathUpdateListener(IPathUpdateListener listener) {
		listeners.remove(listener);
	}

	protected void notifyListeners() {
		for (IPathUpdateListener listener : listeners) {
			listener.onPathsChanged(this);
		}
	}

	public static interface IPathUpdateListener {
		public void onPathsChanged(PathwayPathHandler handler);
	}

}
