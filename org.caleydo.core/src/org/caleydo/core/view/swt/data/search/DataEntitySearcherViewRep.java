package org.caleydo.core.view.swt.data.search;

import java.util.ArrayList;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewType;
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

	
	/**
	 * Constructor.
	 * 
	 */
	public DataEntitySearcherViewRep(final int iParentContainerId, 
			final String sLabel) {

		super(iParentContainerId, 
				sLabel,
				ViewType.SWT_DATA_ENTITY_SEARCHER);
	}
	
	public void setAttributes(final ArrayList<Integer> iAlViewReceiverIDs) {

		CmdEventCreateMediator tmpMediatorCmd = (CmdEventCreateMediator) generalManager.getCommandManager()
			.createCommandByType(ECommandType.CREATE_EVENT_MEDIATOR);
		
		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		iAlSenderIDs.add(iUniqueID);
		
		tmpMediatorCmd.setAttributes(iAlSenderIDs, iAlViewReceiverIDs, MediatorType.SELECTION_MEDIATOR);
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
		
		int iPathwayID = 
			generalManager.getPathwayManager().searchPathwayIdByName(sEntity);
		
		if(iPathwayID == -1)
			return false;

		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.PATHWAY);
		selectionDelta.addSelection(iPathwayID, ESelectionType.SELECTION);
		triggerUpdate(selectionDelta);
		
		return true;
	}
	
	private boolean searchForRefSeq(final String sEntity) {
		
		int iDavidID = generalManager.getGenomeIdManager().getIdIntFromStringByMapping(sEntity, 
			EMappingType.REFSEQ_MRNA_2_DAVID);
		
		if (iDavidID == -1)
			return false;
		
		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.DAVID);
		selectionDelta.addSelection(iDavidID, ESelectionType.SELECTION);
		triggerUpdate(selectionDelta);
	
		return true;
	}
	
	// TODO: make case insensitive
	private boolean searchForNCBIGeneId(final String sNCBIGeneId) {
		
		int iNCBIGeneID = StringConversionTool.convertStringToInt(sNCBIGeneId, -1);
		
		if (iNCBIGeneID == -1)
			return false;
		
		int iDavidID = generalManager.getGenomeIdManager().getIdIntFromIntByMapping(
				iNCBIGeneID, EMappingType.ENTREZ_GENE_ID_2_DAVID);
		
		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.DAVID);
		selectionDelta.addSelection(iDavidID, ESelectionType.SELECTION);
		triggerUpdate(selectionDelta);
		
		return true;
	}
	
	private boolean searchForGeneShortName(final String sEntity) {
		
		int iDavidID = generalManager.getGenomeIdManager().getIdIntFromStringByMapping(sEntity, 
				EMappingType.GENE_SYMBOL_2_DAVID);
		
		if (iDavidID == -1)
			return false;
		
		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.DAVID);
		selectionDelta.addSelection(iDavidID, ESelectionType.SELECTION);
		triggerUpdate(selectionDelta);

		return true;
	}

	@Override
	protected void initViewSwtComposite(Composite swtContainer) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawView() {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggerUpdate()
	{
		generalManager.getEventPublisher().handleUpdate(this);
	}

	@Override
	public void triggerUpdate(ISelectionDelta selectionDelta)
	{
		generalManager.getEventPublisher().handleUpdate(this, selectionDelta);
	}
}