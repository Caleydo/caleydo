package org.caleydo.datadomain.genetic;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.datadomain.ReplaceContentVAInUseCaseListener;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.mapping.IDMappingLoader;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.genetic.contextmenu.container.GeneContentGroupMenuItemContainer;
import org.caleydo.datadomain.genetic.contextmenu.container.GeneContextMenuItemContainer;
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

	private ReplaceContentVAInUseCaseListener clinicalReplaceContentVirtualArrayInUseCaseListener;
	private ForeignSelectionUpdateListener clinicalSelectionUpdateListener;
	private ForeignSelectionCommandListener clinicalSelectionCommandListener;

	private IDMappingManager idMappingManager = GeneralManager.get()
			.getIDMappingManager();

	/**
	 * Constructor.
	 */
	public GeneticDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + ":" + extensionID++);
		
		icon = EIconTextures.DATA_DOMAIN_GENETIC;
		primaryContentMappingType = IDType.getIDType("DAVID");
		humanReadableContentIDType = IDType.getIDType("GENE_SYMBOL");
		humanReadableStorageIDType = IDType.getIDType("STORAGE");

		pathwayViewerMode = false;
		contentLabelSingular = "gene";
		contentLabelPlural = "genes";
	}

	@Override
	protected void initIDMappings() {
	
		// Load IDs needed in this datadomain
		IDMappingLoader.get().loadMappingFile(fileName);
	}

	@Override
	public void setSet(DataTable set) {
		super.setSet(set);

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
		ArrayList<Integer> alTempList = new ArrayList<Integer>(table.depth());

		for (int iCount = 0; iCount < table.depth(); iCount++) {
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
						.getIDAsSet(contentIDType, primaryContentMappingType, iCount);

				if ((setDavidIDs != null && !setDavidIDs.isEmpty())) {
					iDavidID = (Integer) setDavidIDs.toArray()[0];
				}
				// GeneticIDMappingHelper.get().getDavidIDFromStorageIndex(iCount);

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
		ContentVirtualArray contentVA = new ContentVirtualArray(DataTable.CONTENT, alTempList);
		// removeDuplicates(contentVA);
		// FIXME make this a filter?
		table.setContentVA(DataTable.CONTENT, contentVA);
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

		clinicalReplaceContentVirtualArrayInUseCaseListener = new ReplaceContentVAInUseCaseListener();
		clinicalReplaceContentVirtualArrayInUseCaseListener.setHandler(this);
		clinicalReplaceContentVirtualArrayInUseCaseListener
				.setExclusiveDataDomainType(CLINICAL_DATADOMAIN_TYPE);
		eventPublisher.addListener(ReplaceContentVAInUseCaseEvent.class,
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

		if (delta.getIDType() == storageIDType) {
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
	public void handleForeignContentVAUpdate(int setID, String dataDomainType,
			String vaType, ContentVirtualArray virtualArray) {

		if (dataDomainType.equals(CLINICAL_DATADOMAIN_TYPE)) {
			StorageVirtualArray newStorageVirtualArray = new StorageVirtualArray();

			for (Integer clinicalContentIndex : virtualArray) {
				Integer converteID = convertClinicalExperimentToGeneticExperiment(clinicalContentIndex);
				if (converteID != null)
					newStorageVirtualArray.append(converteID);

			}

			ReplaceStorageVAInUseCaseEvent event = new ReplaceStorageVAInUseCaseEvent();
			event.setDataDomainID(this.dataDomainID);
			event.setVAType(DataTable.STORAGE);
			event.setVirtualArray(newStorageVirtualArray);

			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}

	}

	private Integer convertClinicalExperimentToGeneticExperiment(
			Integer clinicalContentIndex) {

		// FIXME - this is a hack for one special dataset (asslaber)
		DataTable clinicalSet = ((ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(
				CLINICAL_DATADOMAIN_TYPE)).getSet();
		int storageID = clinicalSet.getStorageData(DataTable.STORAGE).getStorageVA().get(1);

		NominalStorage clinicalStorage = (NominalStorage<String>) clinicalSet
				.get(storageID);
		StorageVirtualArray origianlGeneticStorageVA = table.getStorageData(DataTable.STORAGE)
				.getStorageVA();

		String label = (String) clinicalStorage.getRaw(clinicalContentIndex);

		label = label.replace("\"", "");
		// System.out.println(label);

		for (Integer storageIndex : origianlGeneticStorageVA) {
			if (label.equals(table.get(storageIndex).getLabel()))
				return storageIndex;
		}

		return null;
	}

	@Override
	public void handleForeignSelectionCommand(String dataDomainType,
			IDCategory idCategory, SelectionCommand selectionCommand) {

		if (dataDomainType == CLINICAL_DATADOMAIN_TYPE && idCategory == storageIDCategory) {
			SelectionCommandEvent newCommandEvent = new SelectionCommandEvent();
			newCommandEvent.setSelectionCommand(selectionCommand);
			newCommandEvent.setIDCategory(idCategory);
			newCommandEvent.setDataDomainID(dataDomainType);
			eventPublisher.triggerEvent(newCommandEvent);
		}
	}

	@Override
	public String getContentLabel(IDType idType, Object id) {
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
				humanReadableContentIDType, id);

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
	public AItemContainer getContentItemContainer(IDType idType, int id) {

		GeneContextMenuItemContainer geneContainer = new GeneContextMenuItemContainer();
		geneContainer.setDataDomain(this);
		geneContainer.setID(idType, id);
		return geneContainer;
	}

	@Override
	public AItemContainer getContentGroupItemContainer(IDType idType,
			ArrayList<Integer> ids) {
		GeneContentGroupMenuItemContainer geneContentGroupContainer = new GeneContentGroupMenuItemContainer();
		geneContentGroupContainer.setDataDomain(this);
		geneContentGroupContainer.setGeneIDs(contentIDType, ids);
		return geneContentGroupContainer;
	}

	@Override
	protected void assignIDCategories() {
		contentIDCategory = IDCategory.getIDCategory("GENE");
		storageIDCategory = IDCategory.getIDCategory("EXPERIMENT");
	}
}
