package org.caleydo.core.view.swt.data.search;

import java.util.Set;
import java.util.logging.Level;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;
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
	implements ISWTView {

	/**
	 * Constructor.
	 */
	public DataEntitySearcherViewRep(final int iParentContainerId, final String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_DATA_ENTITY_SEARCHER));
	}

	public boolean searchForEntity(final String sEntity) {

		if (searchForPathway(sEntity) || searchForNCBIGeneId(sEntity) || searchForGeneShortName(sEntity)
			|| searchForRefSeq(sEntity))
			return true;

		return false;
	}

	private boolean searchForPathway(String sEntity) {
		EPathwayDatabaseType ePathwayDatabaseType;

		if (sEntity.contains("KEGG")) {
			ePathwayDatabaseType = EPathwayDatabaseType.KEGG;
		}
		else if (sEntity.contains("BioCarta")) {
			ePathwayDatabaseType = EPathwayDatabaseType.BIOCARTA;
		}
		else
			return false;

		sEntity = sEntity.substring(0, sEntity.indexOf(" ("));

		PathwayGraph pathway =
			generalManager.getPathwayManager().searchPathwayByName(sEntity, ePathwayDatabaseType);

		if (pathway == null)
			return false;

		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathway.getID());
		eventPublisher.triggerEvent(event);

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
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);
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
			generalManager.getIDMappingManager().getID(EMappingType.GENE_SYMBOL_2_DAVID,
				sEntity.toUpperCase());

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

		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR, ESelectionType.SELECTION);
		sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);

		return true;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm; 
	}

}