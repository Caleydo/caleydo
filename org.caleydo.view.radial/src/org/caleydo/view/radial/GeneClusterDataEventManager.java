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
import org.caleydo.core.view.opengl.canvas.listener.ClusterNodeSelectionListener;
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
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX) {
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
						
						if(clusterNode.getLeafID() == item.getPrimaryID()) {
							selectionManager.addToType(item.getSelectionType(), clusterNode.getClusterNr());
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

		if (selectionDelta.getIDType() == EIDType.CLUSTER_NUMBER) {
			if (event.isSenderRadialHierarchy()) {
				radialHierarchy.setupDisplay(event.getDrawingStateType(), event
						.getDefaultDrawingStrategyType(), event
						.isNewSelection(), event.getRootElementID(), event
						.getSelectedElementID(), event
						.getRootElementStartAngle(), event
						.getSelectedElementStartAngle(), event
						.getMaxDisplayedHierarchyDepth());
			}
			SelectionManager selectionManager = radialHierarchy
					.getSelectionManager();
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
			IHierarchyData<?> hierarchyData = pdSelected.getHierarchyData();
			ClusterNode clusterNode = null;
			
			if (hierarchyData instanceof ClusterNode) {
				clusterNode = (ClusterNode) hierarchyData;
				
				SelectionDelta delta = new SelectionDelta(
						EIDType.EXPRESSION_INDEX);
				delta.addSelection(clusterNode.getLeafID(), selectionType);
				SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
				selectionUpdateEvent.setSender(this);
				selectionUpdateEvent.setSelectionDelta(delta);
				selectionUpdateEvent.setInfo(radialHierarchy.getShortInfo());
				eventPublisher.triggerEvent(selectionUpdateEvent);
			}
		} else {
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

	}

}
