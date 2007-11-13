package org.geneview.core.view.swt.data.search;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.data.CmdDataCreateSelectionSetMakro;
import org.geneview.core.command.event.CmdEventCreateMediator;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IEventPublisher.MediatorType;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.ViewType;

public class DataEntitySearcherViewRep 
extends AViewRep 
implements IMediatorSender{;
	
	private int iSearchSelectionSetId;
	
	public DataEntitySearcherViewRep(final IGeneralManager refGeneralManager, 
			final int iViewId, 
			final int iParentContainerId, 
			final String sLabel) {

		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_DATA_ENTITY_SEARCHER);
	}
	
	public void setAttributes(final ArrayList<Integer> iAlViewReceiverIDs) {

		iSearchSelectionSetId = refGeneralManager.getSingelton().getSetManager()
			.createId(ManagerObjectType.SET_LINEAR);

		CmdDataCreateSelectionSetMakro selectedSetCmd = (CmdDataCreateSelectionSetMakro) refGeneralManager.getSingelton().getCommandManager()
			.createCommandByType(CommandQueueSaxType.CREATE_SET_SELECTION_MAKRO);
	
		selectedSetCmd.setAttributes(iSearchSelectionSetId);
		selectedSetCmd.doCommand();
	
		CmdEventCreateMediator tmpMediatorCmd = (CmdEventCreateMediator) refGeneralManager.getSingelton().getCommandManager()
			.createCommandByType(CommandQueueSaxType.CREATE_EVENT_MEDIATOR);
		
		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		iAlSenderIDs.add(iUniqueId);
		tmpMediatorCmd.setAttributes(refGeneralManager.createId(ManagerObjectType.EVENT_MEDIATOR_CREATE), 
				iAlSenderIDs, iAlViewReceiverIDs, MediatorType.SELECTION_MEDIATOR);
		tmpMediatorCmd.doCommand();
	}
	
	public boolean searchForEntity(final String sEntity) {

		searchForPathway(sEntity);
		
		return false;
	}
	
	private boolean searchForPathway(final String sEntity) {
		
		int iFoundPathwayId = 
			refGeneralManager.getSingelton().getPathwayManager().searchPathwayIdByName(sEntity);
		
		if(iFoundPathwayId == -1)
			return false;

		int[] iArPathwayId = new int[1];
		iArPathwayId[0] = iFoundPathwayId;
		
		triggerUpdate(new int[0], new int[0], iArPathwayId);
		
		return true;
	}
	
	private void triggerUpdate(int[] iArSelectionId,
			int [] iArSelectionDepth,
			int [] iArOptional) {
		
		SetSelection tmpSelectionSet = 
			(SetSelection) refGeneralManager.getSingelton().getSetManager().getItemSet(iSearchSelectionSetId);
		
		tmpSelectionSet.updateSelectionSet(iUniqueId, 
				iArSelectionId,
				iArSelectionDepth, 
				iArOptional);
	}

	@Override
	protected void initViewSwtComposit(Composite swtContainer) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawView() {

		// TODO Auto-generated method stub
		
	}
}
