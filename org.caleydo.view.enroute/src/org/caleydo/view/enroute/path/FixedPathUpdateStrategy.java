/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.MinSizeUpdateEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnableFreePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.enroute.path.node.ALinearizableNode;

/**
 * @author Christian
 *
 */
public class FixedPathUpdateStrategy extends APathUpdateStrategy {

	/**
	 * The node that is the first node of the selected path that appears this path renderer.
	 */
	protected ALinearizableNode selectedPathStartNode;

	/**
	 * Determines whether clicking a path node will create a new selected path or finish path selection.
	 */
	protected boolean createNewPathSelection = true;

	/**
	 * Determines whether the selected path is a continuation from other pathways.
	 */
	protected boolean isSelectedPathContinuation = false;

	/**
	 * Determines whether the path will be selected by moving the mouse over nodes.
	 */
	protected boolean isPathSelectionMode = false;

	/**
	 * Determines whether a free path can be selected by clicking on the nodes of the path.
	 */
	protected boolean isFreePathSelectionMode = false;

	/**
	 * The segments of the currently selected path.
	 */
	protected List<List<PathwayVertexRep>> selectedPathSegments = new ArrayList<>();

	protected ContextualPathsRenderer contextualPathsRenderer;

	/**
	 * @param renderer
	 * @param pathwayPathEventSpace
	 */
	public FixedPathUpdateStrategy(APathwayPathRenderer renderer, String pathwayPathEventSpace,
			boolean isPathSelectionMode, boolean isFreePathSelectionMode,
			ContextualPathsRenderer contextualPathsRenderer,
			List<List<PathwayVertexRep>> selectedPathSegments) {
		super(renderer, pathwayPathEventSpace);
		this.isPathSelectionMode = isPathSelectionMode;
		this.isFreePathSelectionMode = isFreePathSelectionMode;
		this.contextualPathsRenderer = contextualPathsRenderer;
		setSelectedPathSegments(selectedPathSegments);
	}

	public void setSelectedPathSegments(List<List<PathwayVertexRep>> segments) {
		selectedPathSegments = new ArrayList<>();
		for (List<PathwayVertexRep> segment : segments) {
			selectedPathSegments.add(new ArrayList<>(segment));
		}
	}

	public List<List<PathwayVertexRep>> getSelectedPathSegments() {
		return selectedPathSegments;
	}

	@Override
	public void onEnablePathSelection(EnablePathSelectionEvent event) {
		isPathSelectionMode = event.isPathSelectionMode();
		if (isPathSelectionMode)
			isFreePathSelectionMode = false;
	}

	@Override
	public void onEnableFreePathSelection(EnableFreePathSelectionEvent event) {
		isFreePathSelectionMode = event.isEnabled();
		if (isFreePathSelectionMode)
			isPathSelectionMode = false;

	}

	@Override
	public void onSelectedPathChanged(PathwayPathSelectionEvent event) {
		selectedPathSegments = event.getPathSegmentsAsVertexList();
	}

	@Override
	public void triggerPathUpdate() {
		contextualPathsRenderer.contextPathsChanged();
		MinSizeUpdateEvent event = new MinSizeUpdateEvent(renderer, renderer.minHeightPixels, renderer.minWidthPixels);
		event.setEventSpace(pathwayPathEventSpace);
		EventPublisher.INSTANCE.triggerEvent(event);
	}

	@Override
	public void nodesCreated() {
		for (ALinearizableNode node : renderer.pathNodes) {
			node.addPickingListener(new PathSelectionPickingListener(node));
		}
	}

	protected class PathSelectionPickingListener extends APickingListener {

		private final ALinearizableNode node;

		public PathSelectionPickingListener(ALinearizableNode node) {
			this.node = node;
		}

		@Override
		protected void clicked(Pick pick) {
			if (contextualPathsRenderer.isControlKeyPressed())
				return;

			if (isPathSelectionMode) {
				isSelectedPathContinuation = false;
				if (!selectedPathSegments.isEmpty()) {
					List<PathwayVertexRep> lastSegment = selectedPathSegments.get(selectedPathSegments.size() - 1);
					if (lastSegment.size() > 1) {
						for (PathwayVertexRep v : node.getVertexReps()) {
							if (v != lastSegment.get(lastSegment.size() - 1)
									&& PathwayManager.get().areVerticesEquivalent(v,
											lastSegment.get(lastSegment.size() - 1))) {
								isSelectedPathContinuation = true;
								createNewPathSelection = true;
								break;
							}
						}
					}
				}
				if (createNewPathSelection) {
					if (!isSelectedPathContinuation) {
						selectedPathSegments.clear();
					}
					List<PathwayVertexRep> firstSegment = new ArrayList<>();
					firstSegment.add(node.getVertexReps().get(node.getVertexReps().size() - 1));
					selectedPathSegments.add(firstSegment);
					selectedPathStartNode = node;
					triggerPathUpdate(selectedPathSegments);
				}
				createNewPathSelection = !createNewPathSelection;
			} else if (isFreePathSelectionMode) {
				PathwayVertexRep vertexRep = node.getVertexReps().get(node.getVertexReps().size() - 1);

				if (selectedPathSegments.size() > 0) {
					List<PathwayVertexRep> lastSegment = selectedPathSegments.get(selectedPathSegments.size() - 1);
					// Do not add the same node after each other
					if (lastSegment.get(lastSegment.size() - 1) == vertexRep)
						return;
				}
				List<PathwayVertexRep> newSegment = new ArrayList<>(1);
				newSegment.add(vertexRep);
				selectedPathSegments.add(newSegment);
				triggerPathUpdate(selectedPathSegments);
			}
		}

		@Override
		protected void mouseOver(Pick pick) {
			if (contextualPathsRenderer.isControlKeyPressed())
				return;

			if (isPathSelectionMode && !createNewPathSelection
					&& renderer.pathNodes.indexOf(node) > renderer.pathNodes.indexOf(selectedPathStartNode)) {
				Pair<Integer, Integer> fromIndexPair = renderer.determinePathSegmentAndIndexOfPathNode(
						selectedPathStartNode, selectedPathSegments.get(selectedPathSegments.size() - 1).get(0));
				Pair<Integer, Integer> toIndexPair = renderer.determinePathSegmentAndIndexOfPathNode(node,
						node.getPrimaryPathwayVertexRep());
				if (fromIndexPair == null || toIndexPair == null)
					return;

				List<List<PathwayVertexRep>> segments = new ArrayList<>(renderer.pathSegments.subList(
						fromIndexPair.getFirst(), toIndexPair.getFirst() + 1));

				if (fromIndexPair.getFirst() == toIndexPair.getFirst()) {
					List<PathwayVertexRep> segment = new ArrayList<>(segments.get(0).subList(fromIndexPair.getSecond(),
							toIndexPair.getSecond() + 1));
					segments.set(0, segment);
				} else {
					List<PathwayVertexRep> startSegment = new ArrayList<>(segments.get(0).subList(0,
							fromIndexPair.getSecond() + 1));
					segments.set(0, startSegment);
					List<PathwayVertexRep> endSegment = segments.get(segments.size() - 1);
					endSegment = new ArrayList<>(endSegment.subList(toIndexPair.getSecond(), endSegment.size()));
					segments.set(segments.size() - 1, startSegment);
				}
				selectedPathSegments.remove(selectedPathSegments.size() - 1);
				selectedPathSegments.addAll(segments);
				triggerPathUpdate(selectedPathSegments);
			}
		}
	}

	@Override
	public boolean isPathChangePermitted(List<List<PathwayVertexRep>> newPath) {
		if (renderer != contextualPathsRenderer.getSelectedPathRenderer())
			return true;

		if (PathUtil.isPathShown(newPath, selectedPathSegments, renderer.pathway))
			return true;
		return false;
	}

}
