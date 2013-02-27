/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute.path;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
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
	 * Determines whether the path will be selected by moving the mouse over nodes. This is only possible if
	 * {@link #isPathSelectable} is ture.
	 */
	protected boolean isPathSelectionMode = false;

	/**
	 * The segments of the currently selected path.
	 */
	protected List<List<PathwayVertexRep>> selectedPathSegments = new ArrayList<>();

	/**
	 * @param renderer
	 * @param pathwayPathEventSpace
	 */
	public FixedPathUpdateStrategy(APathwayPathRenderer renderer, String pathwayPathEventSpace) {
		super(renderer, pathwayPathEventSpace);
	}

	@Override
	public void onEnablePathSelection(EnablePathSelectionEvent event) {
		isPathSelectionMode = event.isPathSelectionMode();
	}

	@Override
	public void onSelectedPathChanged(PathwayPathSelectionEvent event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void triggerPathUpdate() {
		// TODO Auto-generated method stub

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

			if (isPathSelectionMode) {
				if (createNewPathSelection) {
					selectedPathSegments.clear();
					List<PathwayVertexRep> firstSegment = new ArrayList<>();
					firstSegment.add(node.getVertexReps().get(node.getVertexReps().size() - 1));
					selectedPathSegments.add(firstSegment);
					selectedPathStartNode = node;
					triggerPathUpdate();
				}
				createNewPathSelection = !createNewPathSelection;
			}
		}

		@Override
		protected void mouseOver(Pick pick) {
			if (isPathSelectionMode && !createNewPathSelection
					&& renderer.pathNodes.indexOf(node) > renderer.pathNodes.indexOf(selectedPathStartNode)) {
				Pair<Integer, Integer> fromIndexPair = renderer.determinePathSegmentAndIndexOfPathNode(
						selectedPathStartNode, selectedPathSegments.get(0).get(0));
				Pair<Integer, Integer> toIndexPair = renderer.determinePathSegmentAndIndexOfPathNode(node,
						node.getPrimaryPathwayVertexRep());

				List<List<PathwayVertexRep>> segments = renderer.pathSegments.subList(fromIndexPair.getFirst(),
						toIndexPair.getFirst() + 1);

				if (fromIndexPair.getFirst() == toIndexPair.getFirst()) {
					List<PathwayVertexRep> segment = segments.get(0).subList(fromIndexPair.getSecond(),
							toIndexPair.getSecond() + 1);
					segments.set(0, segment);
				} else {
					List<PathwayVertexRep> startSegment = segments.get(0).subList(0, fromIndexPair.getSecond() + 1);
					segments.set(0, startSegment);
					List<PathwayVertexRep> endSegment = segments.get(segments.size() - 1);
					endSegment = endSegment.subList(toIndexPair.getSecond(), endSegment.size());
					segments.set(segments.size() - 1, startSegment);
				}
				selectedPathSegments = segments;
				triggerPathUpdate();
			}
		}
	}

}
