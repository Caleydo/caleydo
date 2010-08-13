package org.caleydo.datadomain.genetic;

import java.util.ArrayList;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.datadomain.ReplaceContentVAInUseCaseListener;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
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
public class GeneticDataDomain extends ASetBasedDataDomain {

	private static final String CLINICAL_DATADOMAIN_TYPE = "org.caleydo.datadomain.clinical";


	/**
	 * <code>TRUE</code>if only pathways can be displayed (no gene-expression
	 * data), <code>FALSE</code> otherwise
	 */
	private boolean pathwayViewerMode;

	private ReplaceContentVAInUseCaseListener clinicalReplaceContentVirtualArrayInUseCaseListener;
	private ForeignSelectionUpdateListener clinicalSelectionUpdateListener;
	private ForeignSelectionCommandListener clinicalSelectionCommandListener;

	private IIDMappingManager idMappingManager = GeneralManager.get()
			.getIDMappingManager();

	/**
	 * Constructor.
	 */
	public GeneticDataDomain() {

		super("org.caleydo.datadomain.genetic");

		icon = EIconTextures.DATA_DOMAIN_GENETIC;
		primaryContentMappingType = IDType.getIDType("DAVID");
		humanReadableContentIDType = IDType.getIDType("GENE_SHORT_NAME");
		humanReadableStorageIDType = IDType.getIDType("STORAGE");

		pathwayViewerMode = false;
		contentLabelSingular = "gene";
		contentLabelPlural = "genes";

	}

	@Override
	public void setSet(ISet set) {
		super.setSet(set);

	}

	/**
	 * Initializes a virtual array with all elements, according to the data
	 * filters, as defined in {@link EDataFilterLevel}.
	 */

	@Override
	protected void initFullVA() {

		// TODO preferences seem not to be initialized here either in XML case
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
		ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());

		for (int iCount = 0; iCount < set.depth(); iCount++) {
			if (dataFilterLevel != EDataFilterLevel.COMPLETE) {

				Integer iDavidID = null;
				// Here we get mapping data for all values
				// FIXME: Due to new mapping system, a mapping involving
				// expression index can return a Set of
				// values, depending on the IDType that has been specified when
				// loading expression data.
				// Possibly a different handling of the Set is required.
				Set<Integer> setDavidIDs = GeneralManager.get().getIDMappingManager()
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
					PathwayVertexGraphItem tmpPathwayVertexGraphItem = PathwayItemManager.get()
							.getPathwayVertexGraphItemByDavidId(iDavidID);

					if (tmpPathwayVertexGraphItem == null) {
						continue;
					}
				}
			}

			alTempList.add(iCount);
		}
		// ContentVirtualArray contentVA = new
		// ContentVirtualArray(ContentVAType.CONTENT,
		// alTempList);
		// // removeDuplicates(contentVA);
		// set.setContentVA(ContentVAType.CONTENT, contentVA);
	}

	// public ContentVirtualArray removeDuplicates(ContentVirtualArray
	// contentVirtualArray) {
	// Map<Object, Object> idMap =
	// GeneralManager.get().getIDMappingManager().getMap(EMappingType.REFSEQ_MRNA_INT_2_DAVID);
	// for (Object idObject : idMap.keySet()) {
	// Integer id = (Integer) idObject;
	// ArrayList<Integer> indices = contentVirtualArray.indicesOf(id);
	// if (indices.size() > 1) {
	// for (int count = 1; count < indices.size(); count++) {
	// contentVirtualArray.remove(indices.get(count));
	// }
	// }
	//
	// }
	// return contentVirtualArray;
	// }

	public boolean isPathwayViewerMode() {
		return pathwayViewerMode;
	}

	public void setPathwayViewerMode(boolean pathwayViewerMode) {
		this.pathwayViewerMode = pathwayViewerMode;
	}

	@Override
	public void handleContentVAUpdate(ContentVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != contentIDCategory)
			return;

		if (targetCategory == contentIDCategory && vaDelta.getIDType() != contentIDType)
			vaDelta = DeltaConverter.convertDelta(contentIDType, vaDelta);
		ContentVirtualArray va = set.getContentData(vaDelta.getVAType()).getContentVA();

		va.setDelta(vaDelta);
	}

	@Override
	public void handleStorageVAUpdate(StorageVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != storageIDCategory)
			return;

		StorageVirtualArray va = set.getStorageData(vaDelta.getVAType()).getStorageVA();

		va.setDelta(vaDelta);
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
			resendEvent.setDataDomainType(this.dataDomainType);

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
			ContentVAType vaType, ContentVirtualArray virtualArray) {

		if (dataDomainType.equals(CLINICAL_DATADOMAIN_TYPE)) {
			StorageVirtualArray newStorageVirtualArray = new StorageVirtualArray();

			for (Integer clinicalContentIndex : virtualArray) {
				Integer converteID = convertClinicalExperimentToGeneticExperiment(clinicalContentIndex);
				if (converteID != null)
					newStorageVirtualArray.append(converteID);

			}

			ReplaceStorageVAInUseCaseEvent event = new ReplaceStorageVAInUseCaseEvent();
			event.setDataDomainType(this.dataDomainType);
			event.setVaType(StorageVAType.STORAGE);
			event.setVirtualArray(newStorageVirtualArray);

			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}

	}

	private Integer convertClinicalExperimentToGeneticExperiment(
			Integer clinicalContentIndex) {

		// FIXME - this is a hack for one special dataset (asslaber)
		ISet clinicalSet = ((ISetBasedDataDomain) DataDomainManager.getInstance()
				.getDataDomain(CLINICAL_DATADOMAIN_TYPE)).getSet();
		int storageID = clinicalSet.getStorageData(StorageVAType.STORAGE).getStorageVA()
				.get(1);

		INominalStorage clinicalStorage = (INominalStorage<String>) clinicalSet
				.get(storageID);
		StorageVirtualArray origianlGeneticStorageVA = set.getStorageData(
				StorageVAType.STORAGE).getStorageVA();

		String label = (String) clinicalStorage.getRaw(clinicalContentIndex);

		label = label.replace("\"", "");
		// System.out.println(label);

		for (Integer storageIndex : origianlGeneticStorageVA) {
			if (label.equals(set.get(storageIndex).getLabel()))
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
			newCommandEvent.setDataDomainType(dataDomainType);
			eventPublisher.triggerEvent(newCommandEvent);
		}
	}

	@Override
	public String getContentLabel(IDType idType, Object id) {
		String geneSymbol = null;
		String refSeq = null;

		// FIXME: Due to new mapping system, a mapping involving
		// expression index can return a Set of
		// values, depending on the IDType that has been specified when
		// loading expression data.
		// Possibly a different handling of the Set is required.
		Set<String> setRefSeqIDs = idMappingManager.getIDAsSet(idType,
				IDType.getIDType("REFSEQ_MRNA"), id);

		if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
			refSeq = (String) setRefSeqIDs.toArray()[0];
		}

		// FIXME: Due to new mapping system, a mapping involving
		// expression index can return a Set of
		// values, depending on the IDType that has been specified when
		// loading expression data.
		// Possibly a different handling of the Set is required.
		Set<String> setGeneSymbols = idMappingManager.getIDAsSet(contentIDType,
				humanReadableContentIDType, id);

		if ((setGeneSymbols != null && !setGeneSymbols.isEmpty())) {
			geneSymbol = (String) setGeneSymbols.toArray()[0];
		}

		return refSeq + " | " + geneSymbol;
	}

	@Override
	public String getStorageLabel(IDType idType, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AItemContainer getContentItemContainer(IDType idType, int id) {

		GeneContextMenuItemContainer geneContainer = new GeneContextMenuItemContainer();
		geneContainer.setID(idType, id);
		return geneContainer;
	}
}
