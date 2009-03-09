package org.caleydo.core.view.swt.data.search;

import java.util.Set;
import java.util.logging.Level;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IDListEventContainer;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.eclipse.swt.widgets.Composite;

/**
 * Data entity searcher.
 * 
 * @author Marc Streit
 */
public class DataEntitySearcherViewRep
	extends ASWTView
	implements ISWTView, IMediatorSender {

	/**
	 * Constructor.
	 */
	public DataEntitySearcherViewRep(final int iParentContainerId, final String sLabel) {

		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER));

		GeneralManager.get().getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR, this);
	}

	public boolean searchForEntity(final String sEntity) {

		if (searchForPathway(sEntity) || searchForNCBIGeneId(sEntity) || searchForGeneShortName(sEntity)
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

		int iPathwayID = generalManager.getPathwayManager().searchPathwayIdByName(sEntity, ePathwayDatabaseType);

		if (iPathwayID == -1)
			return false;

		IDListEventContainer<Integer> idListEventContainer =
			new IDListEventContainer<Integer>(EEventType.LOAD_PATHWAY_BY_PATHWAY_ID, EIDType.PATHWAY);
		idListEventContainer.addID(iPathwayID);

		triggerEvent(EMediatorType.SELECTION_MEDIATOR, idListEventContainer);

		return true;
	}

	private boolean searchForRefSeq(final String sEntity) {
		Integer iRefSeqID =
			generalManager.getIDMappingManager().getID(EMappingType.REFSEQ_MRNA_2_REFSEQ_MRNA_INT,
				sEntity.toUpperCase());

		if (iRefSeqID == null || iRefSeqID == -1)
			return false;

		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.REFSEQ_MRNA_INT);
		selectionDelta.addSelection(iRefSeqID, ESelectionType.SELECTION);
		triggerEvent(EMediatorType.SELECTION_MEDIATOR, new DeltaEventContainer<ISelectionDelta>(selectionDelta));
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

		Integer iDavidID =
			generalManager.getIDMappingManager().getID(EMappingType.ENTREZ_GENE_ID_2_DAVID, iNCBIGeneID);

		if (iDavidID == null || iDavidID == -1)
			return false;

		triggerSearchResult(iDavidID);

		return true;
	}

	private boolean searchForGeneShortName(final String sEntity) {
		Integer iDavidID =
			generalManager.getIDMappingManager().getID(EMappingType.GENE_SYMBOL_2_DAVID, sEntity.toUpperCase());

		if (iDavidID == null || iDavidID == -1)
			return false;

		return triggerSearchResult(iDavidID);
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
	}

	@Override
	public void drawView() {
	}

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);
	}

	private boolean triggerSearchResult(int iDavidID) {
		Set<Integer> iSetRefSeq =
			generalManager.getIDMappingManager().getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA_INT, iDavidID);

		if (iSetRefSeq == null) {
			generalManager.getLogger().log(Level.SEVERE, "No RefSeq IDs found for David: " + iDavidID);
			return false;
		}

		ISelectionDelta selectionDelta = new SelectionDelta(EIDType.REFSEQ_MRNA_INT);
		for (Object iRefSeqID : iSetRefSeq) {
			selectionDelta.add(new SelectionDeltaItem((Integer) iRefSeqID, ESelectionType.SELECTION));
		}

		triggerEvent(EMediatorType.SELECTION_MEDIATOR, new SelectionCommandEventContainer(
			EIDType.EXPRESSION_INDEX, new SelectionCommand(ESelectionCommandType.CLEAR, ESelectionType.SELECTION)));
		triggerEvent(EMediatorType.SELECTION_MEDIATOR, new DeltaEventContainer<ISelectionDelta>(selectionDelta));

		return true;
	}
}