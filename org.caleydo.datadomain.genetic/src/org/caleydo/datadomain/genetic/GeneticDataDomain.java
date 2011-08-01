package org.caleydo.datadomain.genetic;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.dimension.NominalDimension;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingLoader;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.datadomain.ReplaceRecordVAInUseCaseListener;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceRecordVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.genetic.contextmenu.container.GeneContextMenuItemContainer;
import org.caleydo.datadomain.genetic.contextmenu.container.GeneRecordGroupMenuItemContainer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Use case specialized to genetic data.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class GeneticDataDomain extends ATableBasedDataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.genetic";

	private static final String CLINICAL_DATADOMAIN_TYPE = "org.caleydo.datadomain.clinical";

	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static int extensionID = 0;
	
	/**
	 * <code>TRUE</code>if only pathways can be displayed (no gene-expression
	 * data), <code>FALSE</code> otherwise
	 */
	private boolean pathwayViewerMode;

	private ReplaceRecordVAInUseCaseListener clinicalReplaceContentVirtualArrayInUseCaseListener;
	private ForeignSelectionUpdateListener clinicalSelectionUpdateListener;
	private ForeignSelectionCommandListener clinicalSelectionCommandListener;

	private IDMappingManager idMappingManager = GeneralManager.get()
			.getIDMappingManager();

	/**
	 * Constructor.
	 */
	public GeneticDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER + extensionID++);
		
		icon = EIconTextures.DATA_DOMAIN_GENETIC;
		primaryRecordMappingType = IDType.getIDType("DAVID");
		humanReadableRecordIDType = IDType.getIDType("GENE_SYMBOL");
		humanReadableDimensionIDType = IDType.getIDType("STORAGE");

		pathwayViewerMode = false;
		recordLabelSingular = "gene";
		recordLabelPlural = "genes";
	}

	@Override
	protected void initIDMappings() {
	
		// Load IDs needed in this datadomain
		IDMappingLoader.get().loadMappingFile(fileName);
	}

	@Override
	public void setTable(DataTable set) {
		super.setTable(set);

	}

	/**
	 * Initializes a virtual array with all elements, according to the data
	 * filters, as defined in {@link EDataFilterLevel}.
	 */

	@Override
	protected void initFullVA() {

		String sLevel = GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.DATA_FILTER_LEVEL);
		if (sLevel.equals("complete")) {
			dataFilterLevel = EDataFilterLevel.COMPLETE;
		} else if (sLevel.equals("only_mapping")) {
			dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
		} else if (sLevel.equals("only_context")) {
			// Only apply only_context when pathways are loaded
			// TODO we need to wait for the pathways to be loaded here!
			if (PathwayManager.get().size() > 100) {
				dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
			} else {
				dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
			}
		} else
			dataFilterLevel = EDataFilterLevel.COMPLETE;

		// initialize virtual array that contains all (filtered) information
		ArrayList<Integer> alTempList = new ArrayList<Integer>(table.getMetaData().depth());

		for (int iCount = 0; iCount < table.getMetaData().depth(); iCount++) {
			if (dataFilterLevel != EDataFilterLevel.COMPLETE) {

				Integer iDavidID = null;
				// Here we get mapping data for all values
				// FIXME: Due to new mapping system, a mapping involving
				// expression index can return a Set of
				// values, depending on the IDType that has been specified when
				// loading expression data.
				// Possibly a different handling of the Set is required.
				java.util.Set<Integer> setDavidIDs = GeneralManager.get()
						.getIDMappingManager()
						.getIDAsSet(recordIDType, primaryRecordMappingType, iCount);

				if ((setDavidIDs != null && !setDavidIDs.isEmpty())) {
					iDavidID = (Integer) setDavidIDs.toArray()[0];
				}
				// GeneticIDMappingHelper.get().getDavidIDFromDimensionIndex(iCount);

				if (iDavidID == null) {
					// generalManager.getLogger().log(new Status(Status.WARNING,
					// GeneralManager.PLUGIN_ID,
					// "Cannot resolve gene to DAVID ID!"));
					continue;
				}

				if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
					// Here all values are contained within pathways as well
					PathwayVertexGraphItem tmpPathwayVertexGraphItem = PathwayItemManager
							.get().getPathwayVertexGraphItemByDavidId(iDavidID);

					if (tmpPathwayVertexGraphItem == null) {
						continue;
					}
				}
			}

			alTempList.add(iCount);
		}
		RecordVirtualArray recordVA = new RecordVirtualArray(DataTable.RECORD, alTempList);
		// removeDuplicates(recordVA);
		// FIXME make this a filter?
		table.setRecordVA(DataTable.RECORD, recordVA);
	}

	public boolean isPathwayViewerMode() {
		return pathwayViewerMode;
	}

	public void setPathwayViewerMode(boolean pathwayViewerMode) {
		this.pathwayViewerMode = pathwayViewerMode;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		clinicalReplaceContentVirtualArrayInUseCaseListener = new ReplaceRecordVAInUseCaseListener();
		clinicalReplaceContentVirtualArrayInUseCaseListener.setHandler(this);
		clinicalReplaceContentVirtualArrayInUseCaseListener
				.setExclusiveDataDomainType(CLINICAL_DATADOMAIN_TYPE);
		eventPublisher.addListener(ReplaceRecordVAInUseCaseEvent.class,
				clinicalReplaceContentVirtualArrayInUseCaseListener);

		clinicalSelectionUpdateListener = new ForeignSelectionUpdateListener();
		clinicalSelectionUpdateListener.setHandler(this);
		clinicalSelectionUpdateListener
				.setExclusiveDataDomainType(CLINICAL_DATADOMAIN_TYPE);
		eventPublisher.addListener(SelectionUpdateEvent.class,
				clinicalSelectionUpdateListener);

		clinicalSelectionCommandListener = new ForeignSelectionCommandListener();
		clinicalSelectionCommandListener.setHandler(this);
		clinicalSelectionCommandListener.setDataDomainType(CLINICAL_DATADOMAIN_TYPE);
		eventPublisher.addListener(SelectionCommandEvent.class,
				clinicalSelectionCommandListener);
	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (clinicalReplaceContentVirtualArrayInUseCaseListener != null) {
			eventPublisher
					.removeListener(clinicalReplaceContentVirtualArrayInUseCaseListener);
			clinicalReplaceContentVirtualArrayInUseCaseListener = null;
		}

		if (clinicalSelectionUpdateListener != null) {
			eventPublisher.removeListener(clinicalSelectionUpdateListener);
			clinicalSelectionUpdateListener = null;
		}

		if (clinicalSelectionCommandListener != null) {
			eventPublisher.removeListener(clinicalSelectionCommandListener);
			clinicalSelectionCommandListener = null;
		}
	}

	@Override
	public void handleForeignSelectionUpdate(String dataDomainType,
			ISelectionDelta delta, boolean scrollToSelection, String info) {
		// if (dataDomainType == CLINICAL_DATADOMAIN_TYPE)
		// System.out
		// .println("TODO Convert and re-send selection from clinical to genetic");

		if (delta.getIDType() == dimensionIDType) {
			// for(ISeldelta)
			SelectionUpdateEvent resendEvent = new SelectionUpdateEvent();
			resendEvent.setDataDomainID(this.dataDomainID);

			SelectionDelta convertedDelta = new SelectionDelta(delta.getIDType());
			for (SelectionDeltaItem item : delta) {
				SelectionDeltaItem convertedItem = new SelectionDeltaItem();
				convertedItem.setSelectionType(item.getSelectionType());
				Integer converteID = convertClinicalExperimentToGeneticExperiment(item
						.getPrimaryID());
				if (converteID == null)
					continue;

				convertedItem.setPrimaryID(converteID);
				convertedItem.setConnectionIDs(item.getConnectionIDs());
				convertedItem.setRemove(item.isRemove());
				convertedDelta.add(convertedItem);
			}
			resendEvent.setSelectionDelta((SelectionDelta) convertedDelta);

			eventPublisher.triggerEvent(resendEvent);
		} else
			return;
	}

	@Override
	public void handleForeignRecordVAUpdate(int tableID, String dataDomainType,
			String vaType, RecordVirtualArray virtualArray) {

		if (dataDomainType.equals(CLINICAL_DATADOMAIN_TYPE)) {
			DimensionVirtualArray newDimensionVirtualArray = new DimensionVirtualArray();

			for (Integer clinicalContentIndex : virtualArray) {
				Integer converteID = convertClinicalExperimentToGeneticExperiment(clinicalContentIndex);
				if (converteID != null)
					newDimensionVirtualArray.append(converteID);

			}

			ReplaceDimensionVAInUseCaseEvent event = new ReplaceDimensionVAInUseCaseEvent();
			event.setDataDomainID(this.dataDomainID);
			event.setVAType(DataTable.DIMENSION);
			event.setVirtualArray(newDimensionVirtualArray);

			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}

	}

	private Integer convertClinicalExperimentToGeneticExperiment(
			Integer clinicalContentIndex) {

		// FIXME - this is a hack for one special dataset (asslaber)
		DataTable clinicalSet = ((ATableBasedDataDomain) DataDomainManager.get().getDataDomainByType(
				CLINICAL_DATADOMAIN_TYPE)).getTable();
		int dimensionID = clinicalSet.getDimensionData(DataTable.DIMENSION).getDimensionVA().get(1);

		NominalDimension clinicalDimension = (NominalDimension<String>) clinicalSet
				.get(dimensionID);
		DimensionVirtualArray origianlGeneticDimensionVA = table.getDimensionData(DataTable.DIMENSION)
				.getDimensionVA();

		String label = (String) clinicalDimension.getRaw(clinicalContentIndex);

		label = label.replace("\"", "");
		// System.out.println(label);

		for (Integer dimensionIndex : origianlGeneticDimensionVA) {
			if (label.equals(table.get(dimensionIndex).getLabel()))
				return dimensionIndex;
		}

		return null;
	}

	@Override
	public void handleForeignSelectionCommand(String dataDomainType,
			IDCategory idCategory, SelectionCommand selectionCommand) {

		if (dataDomainType == CLINICAL_DATADOMAIN_TYPE && idCategory == dimensionIDCategory) {
			SelectionCommandEvent newCommandEvent = new SelectionCommandEvent();
			newCommandEvent.setSelectionCommand(selectionCommand);
			newCommandEvent.tableIDCategory(idCategory);
			newCommandEvent.setDataDomainID(dataDomainType);
			eventPublisher.triggerEvent(newCommandEvent);
		}
	}

	@Override
	public String getRecordLabel(IDType idType, Object id) {
		String geneSymbol = null;
		String refSeq = null;

		java.util.Set<String> setRefSeqIDs = idMappingManager.getIDAsSet(idType,
				IDType.getIDType("REFSEQ_MRNA"), id);

		if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
			refSeq = (String) setRefSeqIDs.toArray()[0];
		}

		// FIXME: Due to new mapping system, a mapping involving
		// expression index can return a Set of
		// values, depending on the IDType that has been specified when
		// loading expression data.
		// Possibly a different handling of the Set is required.
		java.util.Set<String> setGeneSymbols = idMappingManager.getIDAsSet(idType,
				humanReadableRecordIDType, id);

		if ((setGeneSymbols != null && !setGeneSymbols.isEmpty())) {
			geneSymbol = (String) setGeneSymbols.toArray()[0];
		}

		if (geneSymbol != null)
			return geneSymbol + " | " + refSeq;
		else if (refSeq != null)
			return refSeq;
		else
			return "Unknown";

	}

	@Override
	public AItemContainer getRecordItemContainer(IDType idType, int id) {

		GeneContextMenuItemContainer geneContainer = new GeneContextMenuItemContainer();
		geneContainer.setDataDomain(this);
		geneContainer.tableID(idType, id);
		return geneContainer;
	}

	@Override
	public AItemContainer getRecordGroupItemContainer(IDType idType,
			ArrayList<Integer> ids) {
		GeneRecordGroupMenuItemContainer geneContentGroupContainer = new GeneRecordGroupMenuItemContainer();
		geneContentGroupContainer.setDataDomain(this);
		geneContentGroupContainer.setGeneIDs(recordIDType, ids);
		return geneContentGroupContainer;
	}

	@Override
	protected void assignIDCategories() {
		recordIDCategory = IDCategory.getIDCategory("GENE");
		dimensionIDCategory = IDCategory.getIDCategory("EXPERIMENT");
	}
}
