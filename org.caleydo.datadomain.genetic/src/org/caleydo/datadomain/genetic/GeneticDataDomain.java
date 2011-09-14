package org.caleydo.datadomain.genetic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.EDataFilterLevel;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingLoader;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.events.ReplaceRecordPerspectiveEvent;
import org.caleydo.core.data.virtualarray.events.ReplaceRecordPerspectiveListener;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

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

	private ReplaceRecordPerspectiveListener clinicalReplaceContentVirtualArrayListener;
	private ForeignSelectionUpdateListener clinicalSelectionUpdateListener;
	private ForeignSelectionCommandListener clinicalSelectionCommandListener;

	/**
	 * Constructor. Do not create a {@link GeneticDataDomain} yourself, use
	 * {@link DataDomainManager#createDataDomain(String)} instead.
	 */
	public GeneticDataDomain() {
		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE
				+ DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER + extensionID++);

	}

	@Override
	public void init() {
		super.init();
		icon = EIconTextures.DATA_DOMAIN_GENETIC;
		if (isColumnDimension) {
			primaryRecordMappingType = IDType.getIDType("DAVID");
			humanReadableRecordIDType = IDType.getIDType("GENE_SYMBOL");

			primaryDimensionMappingType = IDType.getIDType("DIMENSION");
			humanReadableDimensionIDType = IDType.getIDType("DIMENSION");

			recordDenominationSingular = "gene";
			recordDenominationPlural = "genes";

			dimensionDenominationSingular = "sample";
			dimensionDenominationPlural = "samples";
		} else {
			primaryRecordMappingType = IDType.getIDType("DIMENSION");
			humanReadableRecordIDType = IDType.getIDType("DIMENSION");

			primaryDimensionMappingType = IDType.getIDType("DAVID");
			humanReadableDimensionIDType = IDType.getIDType("GENE_SYMBOL");

			recordDenominationSingular = "sample";
			recordDenominationPlural = "samples";

			dimensionDenominationSingular = "gene";
			dimensionDenominationPlural = "genes";
		}
		pathwayViewerMode = false;

	}

	@Override
	protected void initIDMappings() {
		IDMappingLoader.get().loadMappingFile("data/bootstrap/bootstrap.xml");
	}

	@Override
	protected void assignIDCategories() {
		if (isColumnDimension) {
			recordIDCategory = IDCategory.getIDCategory("GENE");
			dimensionIDCategory = IDCategory.getIDCategory("EXPERIMENT");
		} else {
			recordIDCategory = IDCategory.getIDCategory("EXPERIMENT");
			dimensionIDCategory = IDCategory.getIDCategory("GENE");
		}
	}

	@Override
	public void setTable(DataTable set) {
		super.setTable(set);

	}

	/**
	 * Initializes a virtual array with all elements, according to the data
	 * filters, as defined in {@link EDataFilterLevel}.
	 */

	// TODO: Re-write this as a filter
	// protected void initFullVA() {
	//
	// String sLevel = GeneralManager.get().getPreferenceStore()
	// .getString(PreferenceConstants.DATA_FILTER_LEVEL);
	// if (sLevel.equals("complete")) {
	// dataFilterLevel = EDataFilterLevel.COMPLETE;
	// } else if (sLevel.equals("only_mapping")) {
	// dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
	// } else if (sLevel.equals("only_context")) {
	// // Only apply only_context when pathways are loaded
	// // TODO we need to wait for the pathways to be loaded here!
	// if (PathwayManager.get().size() > 100) {
	// dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
	// } else {
	// dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
	// }
	// } else
	// dataFilterLevel = EDataFilterLevel.COMPLETE;
	//
	// // initialize virtual array that contains all (filtered) information
	// ArrayList<Integer> alTempList = new
	// ArrayList<Integer>(table.getMetaData()
	// .depth());
	//
	// for (int iCount = 0; iCount < table.getMetaData().depth(); iCount++) {
	// if (dataFilterLevel != EDataFilterLevel.COMPLETE) {
	//
	// Integer iDavidID = null;
	// // Here we get mapping data for all values
	// // FIXME: Due to new mapping system, a mapping involving
	// // expression index can return a Set of
	// // values, depending on the IDType that has been specified when
	// // loading expression data.
	// // Possibly a different handling of the Set is required.
	// java.util.Set<Integer> setDavidIDs = GeneralManager.get()
	// .getIDMappingManager()
	// .getIDAsSet(recordIDType, primaryRecordMappingType, iCount);
	//
	// if ((setDavidIDs != null && !setDavidIDs.isEmpty())) {
	// iDavidID = (Integer) setDavidIDs.toArray()[0];
	// }
	// // GeneticIDMappingHelper.get().getDavidIDFromDimensionIndex(iCount);
	//
	// if (iDavidID == null) {
	// // generalManager.getLogger().log(new Status(Status.WARNING,
	// // GeneralManager.PLUGIN_ID,
	// // "Cannot resolve gene to DAVID ID!"));
	// continue;
	// }
	//
	// if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
	// // Here all values are contained within pathways as well
	// PathwayVertexGraphItem tmpPathwayVertexGraphItem = PathwayItemManager
	// .get().getPathwayVertexGraphItemByDavidId(iDavidID);
	//
	// if (tmpPathwayVertexGraphItem == null) {
	// continue;
	// }
	// }
	// }
	//
	// alTempList.add(iCount);
	// }
	// RecordVirtualArray recordVA = new RecordVirtualArray(DataTable.RECORD,
	// alTempList);
	// // removeDuplicates(recordVA);
	// // FIXME make this a filter?
	// table.setRecordVA(DataTable.RECORD, recordVA);
	// }

	public boolean isPathwayViewerMode() {
		return pathwayViewerMode;
	}

	public void setPathwayViewerMode(boolean pathwayViewerMode) {
		this.pathwayViewerMode = pathwayViewerMode;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		if (DataDomainManager.get().getDataDomainByType(CLINICAL_DATADOMAIN_TYPE) == null)
			return;

		String clinicalDataDomainID = DataDomainManager.get()
				.getDataDomainByType(CLINICAL_DATADOMAIN_TYPE).getDataDomainID();

		clinicalReplaceContentVirtualArrayListener = new ReplaceRecordPerspectiveListener();
		clinicalReplaceContentVirtualArrayListener.setHandler(this);
		clinicalReplaceContentVirtualArrayListener
				.setExclusiveDataDomainID(clinicalDataDomainID);
		eventPublisher.addListener(ReplaceRecordPerspectiveEvent.class,
				clinicalReplaceContentVirtualArrayListener);

		clinicalSelectionUpdateListener = new ForeignSelectionUpdateListener();
		clinicalSelectionUpdateListener.setHandler(this);
		clinicalSelectionUpdateListener.setExclusiveDataDomainID(clinicalDataDomainID);
		eventPublisher.addListener(SelectionUpdateEvent.class,
				clinicalSelectionUpdateListener);

		clinicalSelectionCommandListener = new ForeignSelectionCommandListener();
		clinicalSelectionCommandListener.setHandler(this);
		clinicalSelectionCommandListener.setDataDomainID(clinicalDataDomainID);
		eventPublisher.addListener(SelectionCommandEvent.class,
				clinicalSelectionCommandListener);
	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (clinicalReplaceContentVirtualArrayListener != null) {
			eventPublisher.removeListener(clinicalReplaceContentVirtualArrayListener);
			clinicalReplaceContentVirtualArrayListener = null;
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
	public void handleForeignSelectionUpdate(String dataDomainType, SelectionDelta delta,
			boolean scrollToSelection, String info) {
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
						.getID());
				if (converteID == null)
					continue;

				convertedItem.setID(converteID);
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
	public void handleForeignRecordVAUpdate(String dataDomainType, String vaType,
			PerspectiveInitializationData data) {

		// FIXME its not clear which dimension va should be updated here
		// if (dataDomainType.equals(CLINICAL_DATADOMAIN_TYPE)) {
		// DimensionVirtualArray newDimensionVirtualArray = new
		// DimensionVirtualArray();
		//
		// for (Integer clinicalContentIndex : virtualArray) {
		// Integer converteID =
		// convertClinicalExperimentToGeneticExperiment(clinicalContentIndex);
		// if (converteID != null)
		// newDimensionVirtualArray.append(converteID);
		//
		// }

		// replaceDimensionVA(tableID, dataDomainType, DataTable.DIMENSION,
		// newDimensionVirtualArray);
		// }

	}

	// FIXME its not clear which dimension va should be updated here
	private Integer convertClinicalExperimentToGeneticExperiment(
			Integer clinicalContentIndex) {
		return null;
	}

	//
	// // FIXME - this is a hack for one special dataset (asslaber)
	// DataTable clinicalSet = ((ATableBasedDataDomain) DataDomainManager.get()
	// .getDataDomainByType(CLINICAL_DATADOMAIN_TYPE)).getTable();
	// int dimensionID = clinicalSet.getDimensionData(DataTable.DIMENSION)
	// .getDimensionVA().get(1);
	//
	// NominalDimension clinicalDimension = (NominalDimension<String>)
	// clinicalSet
	// .get(dimensionID);
	// DimensionVirtualArray origianlGeneticDimensionVA =
	// table.getDimensionData(
	// DataTable.DIMENSION).getDimensionVA();
	//
	// String label = (String) clinicalDimension.getRaw(clinicalContentIndex);
	//
	// label = label.replace("\"", "");
	// // System.out.println(label);
	//
	// for (Integer dimensionIndex : origianlGeneticDimensionVA) {
	// if (label.equals(table.get(dimensionIndex).getLabel()))
	// return dimensionIndex;
	// }
	//
	// return null;
	// }

	@Override
	public void handleForeignSelectionCommand(String dataDomainType,
			IDCategory idCategory, SelectionCommand selectionCommand) {

		if (dataDomainType == CLINICAL_DATADOMAIN_TYPE
				&& idCategory == dimensionIDCategory) {
			SelectionCommandEvent newCommandEvent = new SelectionCommandEvent();
			newCommandEvent.setSelectionCommand(selectionCommand);
			newCommandEvent.tableIDCategory(idCategory);
			newCommandEvent.setDataDomainID(dataDomainType);
			eventPublisher.triggerEvent(newCommandEvent);
		}
	}

	// @Override
	// public String getRecordLabel(IDType idType, Object id) {
	// return super.getRecordLabel(idType, id);
	// // String geneSymbol = null;
	// //
	// // Set<String> setGeneSymbols =
	// // getGeneIDMappingManager().getIDAsSet(idType,
	// // humanReadableRecordIDType, id);
	// //
	// // if ((setGeneSymbols != null && !setGeneSymbols.isEmpty())) {
	// // geneSymbol = (String) setGeneSymbols.toArray()[0];
	// // }
	// //
	// // if (geneSymbol != null)
	// // return geneSymbol;// + " | " + refSeq;
	// // // else if (refSeq != null)
	// // // return refSeq;
	// // else
	// // return "No mapping";
	//
	// }

	public IDMappingManager getGeneIDMappingManager() {
		if (isColumnDimension)
			return recordIDMappingManager;
		else
			return dimensionIDMappingManager;
	}

	public IDMappingManager getSampleIDMappingManager() {
		if (isColumnDimension)
			return dimensionIDMappingManager;
		else
			return recordIDMappingManager;
	}

	// FIXME CONTEXT MENU
	// @Override
	// public AItemContainer getRecordItemContainer(IDType idType, int id) {
	//
	// GeneContextMenuItemContainer geneContainer = new
	// GeneContextMenuItemContainer();
	// geneContainer.setDataDomain(this);
	// geneContainer.tableID(idType, id);
	// return geneContainer;
	// }

	// FIXME CONTEXT MENU
	// @Override
	// public AItemContainer getRecordGroupItemContainer(IDType idType,
	// ArrayList<Integer> ids) {
	// GeneRecordGroupMenuItemContainer geneContentGroupContainer = new
	// GeneRecordGroupMenuItemContainer();
	// geneContentGroupContainer.setDataDomain(this);
	// geneContentGroupContainer.setGeneIDs(recordIDType, ids);
	// return geneContentGroupContainer;
	// }

}
