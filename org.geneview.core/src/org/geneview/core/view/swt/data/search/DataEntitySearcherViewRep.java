package org.geneview.core.view.swt.data.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.data.CmdDataCreateSelectionSetMakro;
import org.geneview.core.command.event.CmdEventCreateMediator;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.mapping.GenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IEventPublisher.MediatorType;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.ViewType;
import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

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

		if (searchForPathway(sEntity)
				|| searchForAccession(sEntity)
				|| searchForNCBIGeneId(sEntity))
			return true;
		
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
	
	private boolean searchForAccession(final String sEntity) {
		
		int iFoundAccessionId = refGeneralManager.getSingelton().getGenomeIdManager().getIdIntFromStringByMapping(sEntity, 
			GenomeMappingType.ACCESSION_CODE_2_ACCESSION);
		
		if (iFoundAccessionId == -1)
			return false;
		
		int iNCBIGeneId = refGeneralManager.getSingelton().getGenomeIdManager().getIdIntFromIntByMapping(iFoundAccessionId, 
				GenomeMappingType.ACCESSION_2_NCBI_GENEID);

		if (iNCBIGeneId == -1)
			return false;
		
		return searchForNCBIGeneId(refGeneralManager.getSingelton().getGenomeIdManager()
				.getIdStringFromIntByMapping(iNCBIGeneId, GenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE));

	}
	
	private boolean searchForNCBIGeneId(String sNCBIGeneIdCode) {
		
		int iNCBIGeneIdCode = StringConversionTool.convertStringToInt(sNCBIGeneIdCode, -1);
		
		if (iNCBIGeneIdCode == -1)
			return false;
		
		int iPathwayGraphItemId = refGeneralManager.getSingelton().getPathwayItemManager()
			.getPathwayVertexGraphItemIdByNCBIGeneId(iNCBIGeneIdCode);
	
		if (iPathwayGraphItemId == -1)
			return false;
		
		Iterator <IGraphItem> iterList = 
			((IGraphItem) refGeneralManager.getSingelton().getPathwayItemManager().getItem(iPathwayGraphItemId))
				.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD).iterator();
	
		ArrayList<Integer> iAlSelectionId = new ArrayList<Integer>();
		ArrayList<Integer> iAlPathwayId = new ArrayList<Integer>();
		while (iterList.hasNext()) 
		{
			IGraphItem bufferItem = iterList.next();					
			iAlSelectionId.add( bufferItem.getId() );
			
			// get pathway id from graph
			List<IGraph> list = bufferItem.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT);
			PathwayGraph buffer = (PathwayGraph) list.get(0);
			iAlPathwayId.add( buffer.getKeggId() );
		}							
	
		int[] iArSelectionId = new int[iAlSelectionId.size()];
		int[] iArSelectionDepth = new int[iAlSelectionId.size()];
		int[] iArSelectionOptional = new int[iAlSelectionId.size()];
		
		for ( int i=0; i < iAlSelectionId.size(); i++) 
		{
			iArSelectionId[i] = iAlSelectionId.get(i).intValue();
			iArSelectionDepth[i] = 0;
			iArSelectionOptional[i] = iAlPathwayId.get(i).intValue();
		}
		
		triggerUpdate(iArSelectionId, iArSelectionDepth, iArSelectionOptional);
		
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
