/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.canvas.GLThreadListenerWrapper;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;

/**
 * @author Christian
 *
 */
public class PathwayPathHandler implements IVertexRepSelectionListener {

	protected static final int MAX_ALTERNATIVE_PATHS = 8;

	protected final IGLCanvas canvas;
	@DeepScan
	protected IGLKeyListener keyListener;

	protected boolean isAltDown = false;
	protected boolean isControlDown = false;
	protected boolean isShiftDown = false;

	protected boolean isPathSelectionMode = false;
	protected String eventSpace;
	protected PathwayVertexRep startVertexRep;
	protected PathwayPath selectedPath = new PathwayPath();
	protected IPathwayRepresentation pathwayRepresentation;
	protected Set<IPathUpdateListener> listeners = new LinkedHashSet<>();

	public PathwayPathHandler(IPathwayRepresentation pathwayRepresentation, IGLCanvas canvas) {
		this.pathwayRepresentation = pathwayRepresentation;
		this.canvas = canvas;
		pathwayRepresentation.addVertexRepSelectionListener(this);
		keyListener = GLThreadListenerWrapper.wrap(new IGLKeyListener() {

			@Override
			public void keyReleased(IKeyEvent e) {
				isControlDown = e.isControlDown();
				isShiftDown = e.isShiftDown();
				isAltDown = e.isAltDown();

			}

			@Override
			public void keyPressed(IKeyEvent e) {
				isControlDown = e.isControlDown();
				isShiftDown = e.isShiftDown();
				isAltDown = e.isAltDown();

			}
		});
		canvas.addKeyListener(keyListener);
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
		vertexRep = getSelectableVertexRepForPath(vertexRep);
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			if (startVertexRep != null) {

				selectPath(startVertexRep, vertexRep);
			} else if (isShiftDown && !selectedPath.isEmpty()) {
				PathwayVertexRep v = selectedPath.getLast().getLast();
				List<PathSegment> paths = calcPaths(v, vertexRep);

				if (!paths.isEmpty() && v != vertexRep) {
					startVertexRep = v;
					addToPath(paths.get(0));
				}
			}
			break;
		case CLICKED:
			boolean isVertexEquivalentToLastVertexInPath = isVertexEquivalentToLastVertexOfPath(vertexRep);
			if (isVertexEquivalentToLastVertexInPath && selectedPath.getLast().size() == 1) {
				selectedPath.remove(selectedPath.size() - 1);
			}

			if (isShiftDown) {
				handleShiftClick(vertexRep);
			} else {
				handleClick(vertexRep, isVertexEquivalentToLastVertexInPath);
			}

			break;
		default:
			break;
		}
	}

	protected void handleClick(PathwayVertexRep vertexRep, boolean isVertexEquivalentToLastVertexInPath) {

		if (startVertexRep == null) {
			startVertexRep = vertexRep;
			if (!isVertexEquivalentToLastVertexInPath)
				selectedPath.clear();
			selectPath(startVertexRep, vertexRep);
		} else {
			if (isVertexEquivalentToLastVertexInPath)
				startVertexRep = vertexRep;

			selectPath(startVertexRep, vertexRep);
			if (!isVertexEquivalentToLastVertexInPath)
				startVertexRep = null;
		}
	}

	protected void handleShiftClick(PathwayVertexRep vertexRep) {

		if (startVertexRep == null) {
			startVertexRep = vertexRep;
			selectPath(startVertexRep, vertexRep);
		} else {
			List<PathSegment> paths = calcPaths(startVertexRep, vertexRep);

			if (paths.isEmpty())
				startVertexRep = vertexRep;

			selectPath(startVertexRep, vertexRep);
			if (!paths.isEmpty())
				startVertexRep = null;
		}
	}

	protected boolean isVertexEquivalentToLastVertexOfPath(PathwayVertexRep vertexRep) {
		if (!selectedPath.isEmpty()) {
			PathwayVertexRep lastVertexRep = selectedPath.getLast().getLast();
			return lastVertexRep != vertexRep && PathwayManager.get().areVerticesEquivalent(lastVertexRep, vertexRep);
		}
		return false;
	}

	protected List<PathSegment> calcPaths(PathwayVertexRep from, PathwayVertexRep to) {

		if (from == to) {
			return Arrays.asList(new PathSegment(Arrays.asList(from)));
		} else {
			KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
					pathwayRepresentation.getPathway(), from, MAX_ALTERNATIVE_PATHS);
			List<GraphPath<PathwayVertexRep, DefaultEdge>> paths = pathAlgo.getPaths(to);
			List<PathSegment> segments = new ArrayList<>();
			if (paths != null && !paths.isEmpty()) {
				for (GraphPath<PathwayVertexRep, DefaultEdge> path : paths) {
					segments.add(new PathSegment(path));
				}
			}
			return segments;
		}
	}

	protected void selectPath(PathwayVertexRep from, PathwayVertexRep to) {
		List<PathSegment> paths = calcPaths(from, to);
		if (!paths.isEmpty()) {
			addToPath(paths.get(0));
		}
	}

	protected void addToPath(PathSegment segment) {
		if (segment == null || segment.isEmpty())
			return;
		if (selectedPath.isEmpty()) {
			selectedPath.add(segment);
		} else {
			PathSegment lastSegment = selectedPath.get(selectedPath.size() - 1);
			if (lastSegment.getPathway() == pathwayRepresentation.getPathway()) {
				int index = lastSegment.lastIndexOf(segment.get(0));
				if (index >= 0) {
					int numElementsToRemove = lastSegment.size() - index;
					for (int i = 1; i <= numElementsToRemove; i++) {
						lastSegment.remove(lastSegment.size() - 1);
					}
					lastSegment.addAll(segment);
				} else {
					selectedPath.add(segment);
				}

			} else {
				selectedPath.add(segment);
			}
		}
		StringBuilder b = new StringBuilder("Path (");
		for (PathSegment s : selectedPath) {
			b.append("[");
			for (PathwayVertexRep v : s) {
				b.append(v.getShortName() + ", ");
			}
			b.append("]");
		}
		b.append(")");
		System.out.println(b);
		notifyListeners();
	}

	/**
	 * Use parent node, if available and the current node has no edges -> selection of complex nodes
	 *
	 * @param vertexRep
	 * @return
	 */
	private PathwayVertexRep getSelectableVertexRepForPath(PathwayVertexRep vertexRep) {
		PathwayVertexRep parent = null;
		Set<DefaultEdge> edges = pathwayRepresentation.getPathway().edgesOf(vertexRep);
		while (edges.isEmpty()) {
			parent = vertexRep.getParent();
			if (parent == null)
				break;
			edges = pathwayRepresentation.getPathway().edgesOf(parent);
		}
		if (parent != null)
			vertexRep = parent;
		return vertexRep;
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

	public void takeDown() {
		listeners.clear();
		canvas.removeKeyListener(keyListener);
	}

	public static interface IPathUpdateListener {
		public void onPathsChanged(PathwayPathHandler handler);
	}

}
