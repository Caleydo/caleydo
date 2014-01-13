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
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.GLMouseAdapter;
import org.caleydo.core.view.opengl.canvas.GLThreadListenerWrapper;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;

/**
 * Handles the selection of a {@link PathwayPath} in an {@link IPathwayRepresentation}, triggers, receives path related
 * events, and updates the selected path accordingly. Also handles alternative paths generated during path selection.
 *
 * @author Christian
 *
 */
public class PathwayPathHandler implements IVertexRepSelectionListener {

	protected static final int MAX_ALTERNATIVE_PATHS = 8;

	protected final IGLCanvas canvas;
	@DeepScan
	protected IGLKeyListener keyListener;
	@DeepScan
	protected IGLMouseListener mouseListener;

	protected boolean isAltDown = false;
	protected boolean isControlDown = false;
	protected boolean isShiftDown = false;
	protected boolean isUpDown = false;

	protected boolean isPathSelectionMode = false;
	protected String eventSpace;
	/**
	 * The vertex that is considered the start vertex when selecting a {@link PathSegment} in the
	 * {@link #pathwayRepresentation}.
	 */
	protected PathwayVertexRep startVertexRep;
	/**
	 * The currently selected path.
	 */
	protected PathwayPath selectedPath = new PathwayPath();
	/**
	 * The pathway representation that shall be used for path selection.
	 */
	protected IPathwayRepresentation pathwayRepresentation;
	/**
	 * Registered listeners interested in path updates.
	 */
	protected Set<IPathUpdateListener> listeners = new LinkedHashSet<>();

	/**
	 * The determined alternative {@link PathSegment}s that were calculated from the {@link #startVertexRep} to another
	 * vertex during path selection.
	 */
	protected List<PathSegment> alternativeSegments = new ArrayList<>();
	/**
	 * The index of the {@link PathSegment} from {@link #alternativeSegments} that is currently selected and part of the
	 * {@link #selectedPath}.
	 */
	protected int selectedAlternativeIndex = 0;

	/**
	 * Listener that gets notified about path changes of a {@link PathwayPathHandler}.
	 *
	 * @author Christian
	 *
	 */
	public static interface IPathUpdateListener {
		/**
		 * Called when paths in the specified handler changed.
		 *
		 * @param handler
		 */
		public void onPathsChanged(PathwayPathHandler handler);
	}

	public PathwayPathHandler(IPathwayRepresentation pathwayRepresentation, IGLCanvas canvas, String eventSpace) {
		this.pathwayRepresentation = pathwayRepresentation;
		this.canvas = canvas;
		this.eventSpace = eventSpace;
		pathwayRepresentation.addVertexRepSelectionListener(this);
		keyListener = GLThreadListenerWrapper.wrap(new IGLKeyListener() {

			@Override
			public void keyReleased(IKeyEvent e) {
				update(e);
			}

			@Override
			public void keyPressed(IKeyEvent e) {
				update(e);
				if (e.isUpDown()) {
					selectNextAlternative();
				}
				if (e.isDownDown()) {
					selectPreviousAlternative();
				}
			}

			private void update(IKeyEvent e) {
				isControlDown = e.isControlDown();
				isShiftDown = e.isShiftDown();
				isAltDown = e.isAltDown();
			}
		});
		canvas.addKeyListener(keyListener);

		mouseListener = GLThreadListenerWrapper.wrap(new GLMouseAdapter() {

			@Override
			public void mouseWheelMoved(IMouseEvent mouseEvent) {
				if (isAltDown) {
					if (mouseEvent.getWheelRotation() > 0) {
						selectNextAlternative();
					} else {
						selectPreviousAlternative();
					}
				}
			}
		});
		canvas.addMouseListener(mouseListener);
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onPathSelectionModeChanged(EnablePathSelectionEvent event) {
		// if (eventSpace == null || (event.getEventSpace() != null && event.getEventSpace().equals(eventSpace))) {
		isPathSelectionMode = event.isPathSelectionMode();
		// }
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onSelectedPathChanged(PathwayPathSelectionEvent event) {
		// if (event.getSender() != this
		// && (eventSpace == null || (event.getEventSpace() != null && event.getEventSpace().equals(eventSpace)))) {
		if (event.getSender() == this)
			return;
		selectedPath = event.getPath();
		startVertexRep = null;
		alternativeSegments.clear();
		notifyListeners();
		// }

	}

	@Override
	public void onSelect(PathwayVertexRep vertexRep, Pick pick) {
		if (!isPathSelectionMode
				|| isControlDown
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
				calcAlternatives(v, vertexRep);

				if (!alternativeSegments.isEmpty() && v != vertexRep) {
					startVertexRep = v;
					selectedAlternativeIndex = 0;
					addToPath(alternativeSegments.get(selectedAlternativeIndex));
				}
			}
			break;
		case CLICKED:
			// boolean isVertexEquivalentToLastVertexInPath = isVertexEquivalentToLastVertexOfPath(vertexRep);
			// if (isVertexEquivalentToLastVertexInPath && selectedPath.getLast().size() == 1) {
			// selectedPath.remove(selectedPath.size() - 1);
			// }

			if (isShiftDown) {

				handleShiftClick(vertexRep);
			} else {
				handleClick(vertexRep);
			}

			break;
		default:
			break;
		}
	}

	protected void handleClick(PathwayVertexRep vertexRep) {

		boolean isVertexEquivalentToLastVertexInPath = isVertexEquivalentToLastVertexOfPath(vertexRep);
		if (startVertexRep == null) {
			startVertexRep = vertexRep;
			selectedAlternativeIndex = 0;
			if (!isVertexEquivalentToLastVertexInPath)
				selectedPath.clear();
			selectPath(startVertexRep, vertexRep);
		} else {
			if (isVertexEquivalentToLastVertexInPath) {
				startVertexRep = vertexRep;
				selectedAlternativeIndex = 0;
			}

			selectPath(startVertexRep, vertexRep);
			if (!isVertexEquivalentToLastVertexInPath)
				startVertexRep = null;
		}
	}

	protected void handleShiftClick(PathwayVertexRep vertexRep) {

		if (startVertexRep == null) {
			startVertexRep = vertexRep;
			selectedAlternativeIndex = 0;
			selectPath(startVertexRep, vertexRep);
		} else {
			calcAlternatives(startVertexRep, vertexRep);
			boolean restartSelection = alternativeSegments.isEmpty();
			if (restartSelection) {
				startVertexRep = vertexRep;
				selectedAlternativeIndex = 0;
			}

			selectPath(startVertexRep, vertexRep);
			if (!restartSelection)
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

	/**
	 * Selects the next available {@link PathSegment} from {@link #alternativeSegments}.
	 */
	public void selectNextAlternative() {
		selectAlternativeByIndexOffset(1);
	}

	/**
	 * Selects the previous {@link PathSegment} from {@link #alternativeSegments}.
	 */
	public void selectPreviousAlternative() {
		selectAlternativeByIndexOffset(-1);
	}

	protected void selectAlternativeByIndexOffset(int offset) {
		if (alternativeSegments.isEmpty())
			return;
		int newIndex = selectedAlternativeIndex + offset;
		if (newIndex >= alternativeSegments.size())
			newIndex = 0;
		if (newIndex < 0)
			newIndex = alternativeSegments.size() - 1;
		selectAlternative(newIndex);
	}

	/**
	 * Selects a {@link PathSegment} alternative from {@link #alternativeSegments} using the specified index.
	 *
	 * @param alternativeIndex
	 */
	public void selectAlternative(int alternativeIndex) {
		if (alternativeIndex == selectedAlternativeIndex || alternativeIndex > alternativeSegments.size())
			return;

		PathSegment currentSegment = alternativeSegments.get(selectedAlternativeIndex);
		PathSegment lastPathSegment = selectedPath.getLast();
		for (int i = 1; i < currentSegment.size(); i++) {
			lastPathSegment.remove(lastPathSegment.size() - 1);
		}

		selectedAlternativeIndex = alternativeIndex;
		addToPath(alternativeSegments.get(alternativeIndex));

	}

	protected void calcAlternatives(PathwayVertexRep from, PathwayVertexRep to) {

		alternativeSegments.clear();
		if (from == to) {
			selectedAlternativeIndex = 0;
			alternativeSegments.add(new PathSegment(Arrays.asList(from)));
		} else {
			KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
					pathwayRepresentation.getPathway(), from, MAX_ALTERNATIVE_PATHS);
			List<GraphPath<PathwayVertexRep, DefaultEdge>> paths = pathAlgo.getPaths(to);

			if (paths != null && !paths.isEmpty()) {
				for (GraphPath<PathwayVertexRep, DefaultEdge> path : paths) {
					alternativeSegments.add(new PathSegment(path));
				}
			}
		}
	}

	protected void selectPath(PathwayVertexRep from, PathwayVertexRep to) {
		calcAlternatives(from, to);
		if (!alternativeSegments.isEmpty()) {
			if (selectedAlternativeIndex > alternativeSegments.size())
				selectedAlternativeIndex = 0;
			addToPath(alternativeSegments.get(selectedAlternativeIndex));
		}
	}

	protected void addToPath(PathSegment segment) {
		if (segment == null || segment.isEmpty())
			return;
		selectedPath.ensurePathLevelIntegrity();

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

		selectedPath.ensurePathLevelIntegrity();

		triggerPathUpdate();

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

	protected void triggerPathUpdate() {
		PathwayPathSelectionEvent event = new PathwayPathSelectionEvent();
		event.setPath(selectedPath);
		event.setEventSpace(eventSpace);
		event.setSender(this);
		EventPublisher.trigger(event);
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

	/**
	 * Adds a listener to be notified about path changes.
	 *
	 * @param listener
	 */
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

	/**
	 * Cleans up the resources of this handler. Shall be called when no longer needed.
	 */
	public void takeDown() {
		listeners.clear();
		canvas.removeKeyListener(keyListener);
		canvas.removeMouseListener(mouseListener);
	}

	/**
	 * @return The path segment that is currently selected.
	 */
	public PathSegment getSelectedAlternativeSegment() {
		return selectedAlternativeIndex >= alternativeSegments.size() ? null : alternativeSegments
				.get(selectedAlternativeIndex);
	}

	/**
	 * @return the alternativeSegments, see {@link #alternativeSegments}
	 */
	public List<PathSegment> getAlternativeSegments() {
		return alternativeSegments;
	}

	/**
	 * @param segment
	 * @return The index of the specified segment in {@link #alternativeSegments}.
	 */
	public int getAlternativeSegmentID(PathSegment segment) {
		return alternativeSegments.indexOf(segment);
	}

	/**
	 * @param eventSpace
	 *            setter, see {@link eventSpace}
	 */
	public void setEventSpace(String eventSpace) {
		this.eventSpace = eventSpace;
	}

}
