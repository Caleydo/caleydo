package org.caleydo.view.radial;

import java.util.Collection;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.IHierarchyData;

/**
 * The ExperimentClusterDataEventManager is responsible for handling and
 * triggering events specific to experiment cluster data when represented by the
 * radial hierarchy view.
 * 
 * @author Christian Partl
 */
public class ExperimentClusterDataEventManager extends ADataEventManager {

	public ExperimentClusterDataEventManager(GLRadialHierarchy radialHierarchy) {
		super(radialHierarchy);
	}

	@Override
	protected void registerDataSpecificEventListeners() {
	}

	@Override
	public void triggerDataSelectionEvents(SelectionType selectionType,
			PartialDisc pdSelected) {

		ClearSelectionsEvent clearSelectionsEvent = new ClearSelectionsEvent();
		clearSelectionsEvent.setSender(radialHierarchy);
		eventPublisher.triggerEvent(clearSelectionsEvent);

		if (!pdSelected.hasChildren()) {
			IHierarchyData<?> hierarchyData = pdSelected.getHierarchyData();
			ClusterNode clusterNode = null;
			
			if (hierarchyData instanceof ClusterNode) {
				clusterNode = (ClusterNode) hierarchyData;
				
				SelectionDelta delta = new SelectionDelta(
						EIDType.EXPERIMENT_INDEX);
				delta.addSelection(clusterNode.getLeaveID(), selectionType);
				SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
				selectionUpdateEvent.setSender(this);
				selectionUpdateEvent.setSelectionDelta(delta);
				selectionUpdateEvent.setInfo(radialHierarchy.getShortInfo());
				eventPublisher.triggerEvent(selectionUpdateEvent);
			}
		}

		SelectionManager selectionManager = radialHierarchy
				.getSelectionManager();

		ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
		event.setSender(this);
		event.setSelectionDelta(selectionManager.getDelta());
		// Specific elements for other RadialHierarchy Views
		event.setSelectedElementID(radialHierarchy.getCurrentSelectedElement()
				.getElementID());
		event.setSelectedElementStartAngle(radialHierarchy
				.getCurrentSelectedElement().getCurrentStartAngle());
		event.setRootElementID(radialHierarchy.getCurrentRootElement()
				.getElementID());
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

	@Override
	protected void unregisterDataSpecificEventListeners() {
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
			SelectionManager selectionManager = radialHierarchy
					.getSelectionManager();
			Collection<PartialDisc> partialDiscs = radialHierarchy.getPartialDiscs();

			selectionManager.clearSelections();
			Collection<SelectionDeltaItem> deltaItems = selectionDelta
					.getAllItems();

			//TODO: The performance of this approach is not good...
			for (SelectionDeltaItem item : deltaItems) {
				for(PartialDisc disc : partialDiscs) {
					IHierarchyData<?> hierarchyData = disc.getHierarchyData();
					ClusterNode clusterNode = null;
					
					if (hierarchyData instanceof ClusterNode) {
						clusterNode = (ClusterNode) hierarchyData;
						
						if(clusterNode.getLeaveID() == item.getPrimaryID()) {
							selectionManager.addToType(item.getSelectionType(), item
									.getPrimaryID());
						}
					}
				}
			}

			radialHierarchy.setNewSelection(true);
			radialHierarchy.setDisplayListDirty();
		}

	}

}
