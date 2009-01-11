package org.caleydo.core.view.swt.data.search;

import java.util.Collection;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Data entity searcher.
 * 
 * @author Marc Streit
 *
 */
public class DataEntitySearcherViewRep 
extends ASWTView 
implements ISWTView, IMediatorSender {
	
	/**
	 * Constructor.
	 * 
	 */
	public DataEntitySearcherViewRep(final int iParentContainerId, 
			final String sLabel) {

		super(iParentContainerId, sLabel,
				GeneralManager.get().getIDManager().createID(
						EManagedObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER));
		
		GeneralManager.get().getEventPublisher().addSender(
				EMediatorType.SELECTION_MEDIATOR, this);
	}
	
	public boolean searchForEntity(final String sEntity) {

		if (searchForPathway(sEntity)
				|| searchForNCBIGeneId(sEntity)
				|| searchForGeneShortName(sEntity)
				|| searchForRefSeq(sEntity))
			return true;
		
		return false;
	}
	
	private boolean searchForPathway(String sEntity) {
		
		EPathwayDatabaseType ePathwayDatabaseType;
		
		if (sEntity.contains("KEGG"))
			ePathwayDatabaseType = EPathwayDatabaseType.KEGG;
		else if (sEntity.contains("BioCarta"))
			ePathwayDatabaseType = EPathwayDatabaseType.BIOCARTA;
		else
			return false;
		
		sEntity = sEntity.substring(0, sEntity.indexOf(" ("));
		
		int iPathwayID = 
			generalManager.getPathwayManager().searchPathwayIdByName(
					sEntity, ePathwayDatabaseType);
		
		if(iPathwayID == -1)
			return false;

		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.PATHWAY);
		selectionDelta.addSelection(iPathwayID, ESelectionType.SELECTION);
		triggerUpdate(EMediatorType.SELECTION_MEDIATOR, selectionDelta, null);
		
		return true;
	}
	
	private boolean searchForRefSeq(final String sEntity) {
		
		Integer iDavidID = generalManager.getIDMappingManager().getID(
				EMappingType.REFSEQ_MRNA_2_DAVID, sEntity.toUpperCase());
		
		if (iDavidID == null || iDavidID == -1)
			return false;
		
		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.DAVID);
		selectionDelta.addSelection(iDavidID, ESelectionType.SELECTION);
		triggerUpdate(EMediatorType.SELECTION_MEDIATOR, selectionDelta, null);
		return true;
	}
	
	private boolean searchForNCBIGeneId(final String sNCBIGeneId) {
		
		Integer iNCBIGeneID = 0;
		try {	
			iNCBIGeneID = Integer.valueOf(sNCBIGeneId);
		}
		catch (NumberFormatException nfe) {
			return false;
		}
		
		Integer iDavidID = generalManager.getIDMappingManager().getID(
				EMappingType.ENTREZ_GENE_ID_2_DAVID, iNCBIGeneID);
		
		if (iDavidID == null || iDavidID == -1)
			return false;
		
		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.DAVID);
		selectionDelta.addSelection(iDavidID, ESelectionType.SELECTION);
		triggerUpdate(EMediatorType.SELECTION_MEDIATOR, selectionDelta, null);
		
		return true;
	}
	
	private boolean searchForGeneShortName(final String sEntity) {
		
		Integer iDavidID = generalManager.getIDMappingManager().getID(
				EMappingType.GENE_SYMBOL_2_DAVID, sEntity.toUpperCase());
		
		if (iDavidID == null || iDavidID == -1)
			return false;
		
		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.DAVID);
		selectionDelta.addSelection(iDavidID, ESelectionType.SELECTION);
		triggerUpdate(EMediatorType.SELECTION_MEDIATOR, selectionDelta, null);

		return true;
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite)
	{
	}

	@Override
	public void drawView() 
	{
	}

	@Override
	public void triggerUpdate(EMediatorType eMediatorType, ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand)
	{
		generalManager.getEventPublisher().triggerUpdate(eMediatorType, this, selectionDelta, null);
	}
}