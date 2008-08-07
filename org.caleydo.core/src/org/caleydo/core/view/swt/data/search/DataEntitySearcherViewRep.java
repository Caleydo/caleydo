package org.caleydo.core.view.swt.data.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.data.CmdDataCreateSelection;
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.data.selection.Selection;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewType;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Data entity searcher.
 * 
 * @author Marc Streit
 *
 */
public class DataEntitySearcherViewRep 
extends AView 
implements IMediatorSender{;
	
	private int iSearchSelectionSetId;
	
	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iParentContainerId
	 * @param sLabel
	 */
	public DataEntitySearcherViewRep(final int iViewID, 
			final int iParentContainerId, 
			final String sLabel) {

		super(iViewID, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_DATA_ENTITY_SEARCHER);
	}
	
	public void setAttributes(final ArrayList<Integer> iAlViewReceiverIDs) {

		//TODO: review when implemented ID management
		iSearchSelectionSetId = -1;//generalManager.getSetManager()
//			.createId(EManagerObjectType.SET);

		CmdDataCreateSelection selectedSetCmd = (CmdDataCreateSelection) generalManager.getCommandManager()
			.createCommandByType(CommandType.CREATE_SELECTION);
	
		selectedSetCmd.setAttributes(iSearchSelectionSetId);
		selectedSetCmd.doCommand();
	
		CmdEventCreateMediator tmpMediatorCmd = (CmdEventCreateMediator) generalManager.getCommandManager()
			.createCommandByType(CommandType.CREATE_EVENT_MEDIATOR);
		
		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		iAlSenderIDs.add(iUniqueID);
		
		//TODO: review when implemented ID management
//		tmpMediatorCmd.setAttributes(generalManager.getEventPublisher().createId(EManagerObjectType.EVENT_MEDIATOR_CREATE), 
//				iAlSenderIDs, iAlViewReceiverIDs, MediatorType.SELECTION_MEDIATOR);
		tmpMediatorCmd.doCommand();
	}
	
	public boolean searchForEntity(final String sEntity) {

		if (searchForPathway(sEntity)
				|| searchForNCBIGeneId(sEntity)
				|| searchForGeneShortName(sEntity)
				|| searchForRefSeq(sEntity))
			return true;
		
		return false;
	}
	
	private boolean searchForPathway(final String sEntity) {
		
		int iFoundPathwayId = 
			generalManager.getPathwayManager().searchPathwayIdByName(sEntity);
		
		if(iFoundPathwayId == -1)
			return false;

		ArrayList<Integer> iAlPathwayId = new ArrayList<Integer>(1);
		iAlPathwayId.add(iFoundPathwayId);
		
		triggerUpdate(null, null, iAlPathwayId);
		
		return true;
	}
	
	private boolean searchForRefSeq(final String sEntity) {
		
		int iDavidId = generalManager.getGenomeIdManager().getIdIntFromStringByMapping(sEntity, 
			EGenomeMappingType.REFSEQ_MRNA_2_DAVID);
		
		if (iDavidId == -1)
			return false;
		
		ArrayList<Integer> iAlSelectionId = new ArrayList<Integer>();
		ArrayList<Integer> iAlSelectionGroupId = new ArrayList<Integer>();
		
		iAlSelectionId.add(iDavidId);
		iAlSelectionGroupId.add(2);
		
		triggerUpdate(iAlSelectionId, iAlSelectionGroupId, null);
	
		return true;
	}
	
	// TODO: make case insensitive
	private boolean searchForNCBIGeneId(final String sNCBIGeneId) {
		
		int iNCBIGeneId = StringConversionTool.convertStringToInt(sNCBIGeneId, -1);
		
		if (iNCBIGeneId == -1)
			return false;
		
		int iDavidId = generalManager.getGenomeIdManager().getIdIntFromIntByMapping(
				iNCBIGeneId, EGenomeMappingType.ENTREZ_GENE_ID_2_DAVID);
		
		int iPathwayGraphItemId = generalManager.getPathwayItemManager()
			.getPathwayVertexGraphItemIdByDavidId(iDavidId);
	
		if (iPathwayGraphItemId == -1)
			return false;
		
		Iterator <IGraphItem> iterList = 
			((IGraphItem) generalManager.getPathwayItemManager().getItem(iPathwayGraphItemId))
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
		
//		int iNCBIGeneId = generalManager.getGenomeIdManager().getIdIntFromStringByMapping(sEntity, 
//				EGenomeMappingType.DA);
//		
//		if (iNCBIGeneId == -1)
//			return false;
//		
//		return searchForNCBIGeneId(generalManager.getGenomeIdManager()
//				.getIdStringFromIntByMapping(iNCBIGeneId, EGenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE));
		
		return false;
	}
	
	private void triggerUpdate(ArrayList<Integer> iAlSelectionId,
			ArrayList<Integer> iAlSelectionDepth,
			ArrayList<Integer> iAlOptional) {
		
		Selection tmpSelectionSet = 
			(Selection) generalManager.getSetManager().getItem(iSearchSelectionSetId);
		
		tmpSelectionSet.updateSelectionSet(iUniqueID, 
				iAlSelectionId,
				iAlSelectionDepth, 
				iAlOptional);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.AView#initViewSwtComposit(org.eclipse.swt.widgets.Composite)
	 */
	protected void initViewSwtComposit(Composite swtContainer) {

		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.IView#drawView()
	 */
	public void drawView() {

		// TODO Auto-generated method stub
		
	}
}
