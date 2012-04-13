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
package org.caleydo.view.radial;

import java.util.Collection;
import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ClusterNodeSelectionListener;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;

/**
 * The GeneClusterDataEventManager is responsible for handling and triggering
 * events specific to gene cluster data when represented by the radial hierarchy
 * view.
 * 
 * @author Christian Partl
 */
public class GeneClusterDataEventManager extends ADataEventManager implements
		IClusterNodeEventReceiver {

	ClusterNodeSelectionListener clusterNodeSelectionListener;

	public GeneClusterDataEventManager(GLRadialHierarchy radialHierarchy) {
		super(radialHierarchy);
	}

	@Override
	protected void registerDataSpecificEventListeners() {

		clusterNodeSelectionListener = new ClusterNodeSelectionListener();
		clusterNodeSelectionListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeSelectionEvent.class,
				clusterNodeSelectionListener);
	}

	@Override
	protected void unregisterDataSpecificEventListeners() {

		if (clusterNodeSelectionListener != null) {
			eventPublisher.removeListener(clusterNodeSelectionListener);
			clusterNodeSelectionListener = null;
		}
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == radialHierarchy.getDataDomain()
				.getRecordIDType()) {
			SelectionManager selectionManager = radialHierarchy.getSelectionManager();
			Collection<PartialDisc> partialDiscs = radialHierarchy.getPartialDiscs();

			selectionManager.clearSelections();
			Collection<SelectionDeltaItem> deltaItems = selectionDelta.getAllItems();

			// TODO: The performance of this approach is not good...
			for (SelectionDeltaItem item : deltaItems) {
				for (PartialDisc disc : partialDiscs) {
					AHierarchyElement<?> hierarchyData = disc.getHierarchyData();
					ClusterNode clusterNode = null;

					if (hierarchyData instanceof ClusterNode) {
						clusterNode = (ClusterNode) hierarchyData;

						if (clusterNode.getLeafID() == item.getID()) {
							selectionManager.addToType(item.getSelectionType(),
									clusterNode.getID());
						}
					}
				}
			}

			radialHierarchy.setNewSelection(true);
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {
		SelectionDelta selectionDelta = event.getSelectionDelta();

		if (selectionDelta.getIDType() == radialHierarchy.getNodeIDType()) {
			if (event.isSenderRadialHierarchy()) {
				radialHierarchy.setupDisplay(event.getDrawingStateType(),
						event.getDefaultDrawingStrategyType(), event.isNewSelection(),
						event.getRootElementID(), event.getSelectedElementID(),
						event.getRootElementStartAngle(),
						event.getSelectedElementStartAngle(),
						event.getMaxDisplayedHierarchyDepth());
			}

			SelectionManager selectionManager = radialHierarchy.getSelectionManager();
			selectionManager.clearSelections();
			selectionManager.setDelta(selectionDelta);
			radialHierarchy.setNewSelection(true);
			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public void triggerDataSelectionEvents(SelectionType selectionType,
			PartialDisc pdSelected) {

		ClearSelectionsEvent clearSelectionsEvent = new ClearSelectionsEvent();
		// here we want to avoid circular events within the radial hierarchy,
		// therefore we need to set the sender to be the RH
		clearSelectionsEvent.setSender(radialHierarchy);
		eventPublisher.triggerEvent(clearSelectionsEvent);

		if (!pdSelected.hasChildren()) {
			AHierarchyElement<?> hierarchyData = pdSelected.getHierarchyData();
			ClusterNode clusterNode = null;

			if (hierarchyData instanceof ClusterNode) {
				clusterNode = (ClusterNode) hierarchyData;

				SelectionDelta delta = new SelectionDelta(radialHierarchy.getDataDomain()
						.getRecordIDType());
				delta.addSelection(clusterNode.getLeafID(), selectionType);
				SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
				selectionUpdateEvent.setSender(this);
				selectionUpdateEvent.setSelectionDelta(delta);
				eventPublisher.triggerEvent(selectionUpdateEvent);
			}
		} else {
			SelectionManager selectionManager = radialHierarchy.getSelectionManager();

			ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
			event.setSender(this);
			event.setSelectionDelta(selectionManager.getDelta());
			// Specific elements for other RadialHierarchy Views
			event.setSelectedElementID(radialHierarchy.getCurrentSelectedElement()
					.getElementID());
			event.setSelectedElementStartAngle(radialHierarchy
					.getCurrentSelectedElement().getCurrentStartAngle());
			event.setRootElementID(radialHierarchy.getCurrentRootElement().getElementID());
			event.setRootElementStartAngle(radialHierarchy.getCurrentRootElement()
					.getCurrentStartAngle());
			event.setDrawingStateType(radialHierarchy.getCurrentDrawingStateType());
			event.setMaxDisplayedHierarchyDepth(radialHierarchy
					.getMaxDisplayedHierarchyDepth());
			event.setDefaultDrawingStrategyType(radialHierarchy
					.getDrawingStrategyManager().getDefaultDrawingStrategy()
					.getDrawingStrategyType());
			event.setSenderRadialHierarchy(true);
			event.setNewSelection(selectionType == SelectionType.SELECTION);
			eventPublisher.triggerEvent(event);
		}

	}

}
