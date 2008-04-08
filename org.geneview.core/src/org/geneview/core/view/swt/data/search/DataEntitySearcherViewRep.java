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
import org.geneview.core.data.mapping.EGenomeMappingType;
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

/**
 * 
 * DataEntitySearcherViewRep
 * 
 * @author Marc Streit
 *
 */
public class DataEntitySearcherViewRep 
extends AViewRep 
implements IMediatorSender{;
	
	private int iSearchSelectionSetId;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param sLabel
	 */
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

		iSearchSelectionSetId = generalManager.getSingelton().getSetManager()
			.createId(ManagerObjectType.SET_LINEAR);

		CmdDataCreateSelectionSetMakro selectedSetCmd = (CmdDataCreateSelectionSetMakro) generalManager.getSingelton().getCommandManager()
			.createCommandByType(CommandQueueSaxType.CREATE_SET_SELECTION_MAKRO);
	
		selectedSetCmd.setAttributes(iSearchSelectionSetId);
		selectedSetCmd.doCommand();
	
		CmdEventCreateMediator tmpMediatorCmd = (CmdEventCreateMediator) generalManager.getSingelton().getCommandManager()
			.createCommandByType(CommandQueueSaxType.CREATE_EVENT_MEDIATOR);
		
		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		iAlSenderIDs.add(iUniqueId);
		tmpMediatorCmd.setAttributes(generalManager.createId(ManagerObjectType.EVENT_MEDIATOR_CREATE), 
				iAlSenderIDs, iAlViewReceiverIDs, MediatorType.SELECTION_MEDIATOR);
		tmpMediatorCmd.doCommand();
	}
	
	public boolean searchForEntity(final String sEntity) {

		if (searchForPathway(sEntity)
				|| searchForNCBIGeneId(sEntity)
				|| searchForGeneShortName(sEntity)
				|| searchForAccession(sEntity))
			return true;
		
		return false;
	}
	
	private boolean searchForPathway(final String sEntity) {
		
		int iFoundPathwayId = 
			generalManager.getSingelton().getPathwayManager().searchPathwayIdByName(sEntity);
		
		if(iFoundPathwayId == -1)
			return false;

		ArrayList<Integer> iAlPathwayId = new ArrayList<Integer>(1);
		iAlPathwayId.add(iFoundPathwayId);
		
		triggerUpdate(null, null, iAlPathwayId);
		
		return true;
	}
	
	private boolean searchForAccession(final String sEntity) {
		
		int iFoundAccessionId = generalManager.getSingelton().getGenomeIdManager().getIdIntFromStringByMapping(sEntity, 
			EGenomeMappingType.ACCESSION_CODE_2_ACCESSION);
		
		if (iFoundAccessionId == -1)
			return false;
		
		ArrayList<Integer> iAlSelectionId = new ArrayList<Integer>();
		ArrayList<Integer> iAlSelectionGroupId = new ArrayList<Integer>();
		
		iAlSelectionId.add(iFoundAccessionId);
		iAlSelectionGroupId.add(2);
		
		triggerUpdate(iAlSelectionId, iAlSelectionGroupId, null);

//		iAlSelectionId.clear();
//		iAlSelectionGroupId.clear();
//		iAlSelectionId.add(iFoundAccessionId);
//		iAlSelectionGroupId.add(1);
//		
//		triggerUpdate(iAlSelectionId, iAlSelectionGroupId, null);
		
		return true;
		
//		int iNCBIGeneId = generalManager.getSingelton().getGenomeIdManager().getIdIntFromIntByMapping(iFoundAccessionId, 
//				EGenomeMappingType.ACCESSION_2_NCBI_GENEID);
//
//		if (iNCBIGeneId == -1)
//			return false;
//		
//		return searchForNCBIGeneId(generalManager.getSingelton().getGenomeIdManager()
//				.getIdStringFromIntByMapping(iNCBIGeneId, EGenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE));

	}
	
	// TODO: make case insensitive
	private boolean searchForNCBIGeneId(final String sNCBIGeneIdCode) {
		
		int iNCBIGeneIdCode = StringConversionTool.convertStringToInt(sNCBIGeneIdCode, -1);
		
		if (iNCBIGeneIdCode == -1)
			return false;
		
		int iPathwayGraphItemId = generalManager.getSingelton().getPathwayItemManager()
			.getPathwayVertexGraphItemIdByNCBIGeneId(iNCBIGeneIdCode);
	
		if (iPathwayGraphItemId == -1)
			return false;
		
		Iterator <IGraphItem> iterList = 
			((IGraphItem) generalManager.getSingelton().getPathwayItemManager().getItem(iPathwayGraphItemId))
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
	
		
		ArrayList<Integer> iAlSelectionDepth = new ArrayList<Integer>(0);
		
		for ( int i=0; i < iAlSelectionId.size(); i++) 
		{
			iAlSelectionDepth.add(2);
		}
		
		triggerUpdate(iAlSelectionId, iAlSelectionDepth, iAlPathwayId);
		
		return true;
	}
	
	private boolean searchForGeneShortName(final String sEntity) {
		
		int iNCBIGeneId = generalManager.getSingelton().getGenomeIdManager().getIdIntFromStringByMapping(sEntity, 
				EGenomeMappingType.GENE_SHORT_NAME_2_NCBI_GENEID);
		
		if (iNCBIGeneId == -1)
			return false;
		
		return searchForNCBIGeneId(generalManager.getSingelton().getGenomeIdManager()
				.getIdStringFromIntByMapping(iNCBIGeneId, EGenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE));
	}
	
	private void triggerUpdate(ArrayList<Integer> iAlSelectionId,
			ArrayList<Integer> iAlSelectionDepth,
			ArrayList<Integer> iAlOptional) {
		
		SetSelection tmpSelectionSet = 
			(SetSelection) generalManager.getSingelton().getSetManager().getItemSet(iSearchSelectionSetId);
		
		tmpSelectionSet.getWriteToken();
		tmpSelectionSet.updateSelectionSet(iUniqueId, 
				iAlSelectionId,
				iAlSelectionDepth, 
				iAlOptional);
		tmpSelectionSet.returnWriteToken();
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
