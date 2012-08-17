/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.genetic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.EDataFilterLevel;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ForeignSelectionCommandListener;
import org.caleydo.core.data.selection.events.ForeignSelectionUpdateListener;
import org.caleydo.core.data.virtualarray.events.ReplaceRecordPerspectiveEvent;
import org.caleydo.core.data.virtualarray.events.ReplaceRecordPerspectiveListener;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * DataDomain for genetic data.
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
		icon = EIconTextures.DATA_DOMAIN_GENETIC;
		super.init();
	}

	// @Override
	// public void createDefaultConfiguration() {
	//
	// configuration = new DataDomainConfiguration();
	// configuration.setDefaultConfiguration(true);
	//
	// configuration.setRecordIDCategory("GENE");
	// configuration.setDimensionIDCategory("SAMPLE");
	//
	//
	// }
	//
	// @Override
	// public void createDefaultConfigurationWithColumnsAsRecords() {
	//
	// configuration = new DataDomainConfiguration();
	// configuration.setDefaultConfiguration(true);
	//
	// configuration.setRecordIDCategory("SAMPLE");
	// configuration.setDimensionIDCategory("GENE");
	// }

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
	public void handleForeignSelectionUpdate(String dataDomainType, SelectionDelta delta) {
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
		if (isColumnDimension())
			return recordIDMappingManager;
		else
			return dimensionIDMappingManager;
	}

	public IDMappingManager getSampleIDMappingManager() {
		if (isColumnDimension())
			return dimensionIDMappingManager;
		else
			return recordIDMappingManager;
	}

	/**
	 * Returns the idType for the content in the data table, which is either the
	 * recordIDType or the dimensionIDType depending on the result of
	 * {@link #isColumnDimension()}
	 * 
	 * @return
	 */
	public IDType getGeneIDType() {
		if (isColumnDimension())
			return getRecordIDType();
		else
			return getDimensionIDType();
	}

	public IDType getSampleIDType() {
		if (isColumnDimension())
			return getDimensionIDType();
		else
			return getRecordIDType();
	}

	public IDType getHumanReadableGeneIDType() {
		if (isColumnDimension())
			return getHumanReadableRecordIDType();
		else
			return getHumanReadableDimensionIDType();
	}

	public IDType getGeneGroupIDType() {
		if (isColumnDimension())
			return getRecordGroupIDType();
		else
			return getDimensionGroupIDType();
	}

	public IDType getSampleGroupIDType() {
		if (isColumnDimension())
			return getDimensionGroupIDType();
		else
			return getRecordGroupIDType();

	}

	/**
	 * Returns the value of the type specified in the dataRepresentation from
	 * the table based on the ID of the gene and the experiment. Resolves
	 * dimension/record association for you in doing so.
	 */
	public float getGeneValue(DataRepresentation dataRepresentation, Integer geneID,
			Integer experimentID) {
		Integer recordID;
		Integer dimensionID;
		if (isGeneRecord()) {
			recordID = geneID;
			dimensionID = experimentID;
		} else {
			recordID = experimentID;
			dimensionID = geneID;
		}
		return table.getFloat(dataRepresentation, recordID, dimensionID);
	}

	public boolean isGeneRecord() {
		return (recordIDCategory == IDCategory.getIDCategory("GENE"));
	}

}
