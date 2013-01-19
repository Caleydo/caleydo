/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org IContainer
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.DataRepresentation;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.base.AUniqueObject;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <h2>General Information</h2>
 * <p>
 * A set is the main container for tabular data in Caleydo. A set is made up of {@link IDimension}s, where each
 * dimension corresponds to a column in a tabular data table. Columns are therefore always referred to as
 * <b>Dimensions</b> and rows as <b>Record</b> The data should be accessed through {@link VirtualArray}s, which are
 * stored in {@link DimensionPerspective}s for Dimensions and {@link RecordPerspective}s for Record.
 * </p>
 * <h2>DataTable Creation</h2>
 * <p>
 * A data table relies heavily upon {@link DataTableUtils} for being created. Many creation related functions are
 * provided there, sometimes interfacing with package private methods in this class.
 * </p>
 *
 * @author Alexander Lex
 */
public class DataTable extends AUniqueObject {

	HashMap<Integer, AColumn> hashColumns;

	/** List of dimension IDs in the order as they have been added */
	private ArrayList<Integer> defaultColumnIDs;
	/**
	 * Container holding all the record perspectives registered. The perspectiveIDs are the keys
	 */
	private HashMap<String, RecordPerspective> hashRecordPerspectives;
	/** same as {@link #hashRecordPerspectives} for dimensions */
	private HashMap<String, DimensionPerspective> hashDimensionPerspectives;
	/**
	 * Default record perspective. Initially all the data is contained in this perspective. If not otherwise specified,
	 * filters, clusterings etc. are always applied to this perspective
	 */
	private RecordPerspective defaultRecordPerspective;
	/** Same as {@link #defaultRecordPerspective} for dimensions */
	private DimensionPerspective defaultDimensionPerspective;

	EDataTransformation dataTransformation;

	boolean isTableHomogeneous = false;

	DataTableDataType tableType = DataTableDataType.NUMERIC;

	private ATableBasedDataDomain dataDomain;


	/**
	 * all metaData for this DataTable is held in or accessible through this object
	 */
	private MetaData metaData;

	/**
	 * everything related to normalization of the data is held in or accessible through this object
	 */
	private Normalization normalization;

	boolean isColumnDimension = false;

	/**
	 * Constructor for the table. Creates and initializes members and registers the set whit the set manager. Also
	 * creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public DataTable(ATableBasedDataDomain dataDomain) {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.DATA_TABLE));
		this.dataDomain = dataDomain;
		isColumnDimension = !dataDomain.getDataSetDescription().isTransposeMatrix();
	}

	/**
	 * Initialization of member variables. Safe to be called by sub-classes.
	 */
	{
		hashColumns = new HashMap<Integer, AColumn>();
		hashRecordPerspectives = new HashMap<String, RecordPerspective>(6);
		hashDimensionPerspectives = new HashMap<String, DimensionPerspective>(3);
		defaultColumnIDs = new ArrayList<Integer>();
		metaData = new MetaData(this);
		normalization = new Normalization(this);
	}

	/**
	 * Returns an object which can be asked about different kinds of meta data of this set
	 *
	 * @return
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	public DataTableDataType getTableType() {
		return tableType;
	}

	/**
	 * Get the data domain that is responsible for the set
	 *
	 * @param dataDomain
	 */

	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public Float getNormalizedValue(Integer dimensionID, Integer recordID) {
		try {
			if (isColumnDimension) {
				return hashColumns.get(dimensionID).getNormalizedValue(recordID);
			} else {
				AColumn<?, ?> column = hashColumns.get(recordID);
				return column.getNormalizedValue(dimensionID);
			}
		} catch (NullPointerException npe) {
			Logger.log(new Status(IStatus.ERROR, "DataTable", "Data table does not contain a value for record: "
					+ recordID + " and dimension " + dimensionID));
			return null;
		}

	}


	public String getRawAsString(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		Integer rowID = recordID;
		if (!isColumnDimension) {
			columnID = recordID;
			rowID = dimensionID;
		}
		return hashColumns.get(columnID).getRawAsString(rowID);
	}

	public boolean containsDataRepresentation(DataRepresentation dataRepresentation, Integer dimensionID,
			Integer recordID) {
		Integer columnID = dimensionID;

		if (!isColumnDimension)
			columnID = recordID;

		return hashColumns.get(columnID).containsDataRepresentation(dataRepresentation);
	}

	public EDataType getRawDataType(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		if (!isColumnDimension)
			columnID = recordID;
		return hashColumns.get(columnID).getRawDataType();
	}

	@SuppressWarnings("unchecked")
	public <RawDataType> RawDataType getRaw(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		Integer rowID = recordID;
		if (!isColumnDimension) {
			columnID = recordID;
			rowID = dimensionID;
		}

		return (RawDataType) hashColumns.get(columnID).getRaw(rowID);
	}

	/**
	 * alias for {@link #getRaw(Integer, Integer)} with dedidacted hint what is first argument and what the second
	 *
	 * @param dimensionID
	 * @param recordID
	 * @return
	 */
	public <RawDataType> RawDataType getRawDxR(Integer dimensionID, Integer recordID) {
		return getRaw(dimensionID, recordID);
	}

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 *
	 * @param dNormalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(double dNormalized) {
		if (!isTableHomogeneous)
			throw new IllegalStateException(
					"Can not produce raw data on set level for inhomogenous sets. Access via dimensions");

		double result;

		if (dNormalized == 0)
			result = metaData.getMin();
		// if(getMin() > 0)
		result = metaData.getMin() + dNormalized * (metaData.getMax() - metaData.getMin());
		// return (dNormalized) * (getMax() + getMin());
		if (dataTransformation == EDataTransformation.NONE) {
			return result;
		} else if (dataTransformation == EDataTransformation.LOG2) {
			return Math.pow(2, result);
		} else if (dataTransformation == EDataTransformation.LOG10) {
			return Math.pow(10, result);
		}
		throw new IllegalStateException("Conversion raw to normalized not implemented for data rep"
				+ dataTransformation);
	}

	/**
	 * Calculates a normalized value based on min and max.
	 *
	 * @param dRaw
	 *            the raw value
	 * @return a value between 0 and 1
	 */
	public double getNormalizedForRaw(double dRaw) {
		if (!isTableHomogeneous)
			throw new IllegalStateException(
					"Can not produce normalized data on set level for inhomogenous sets. Access via dimensions");

		double result;

		if (dataTransformation == EDataTransformation.NONE) {
			result = dRaw;
		} else if (dataTransformation == EDataTransformation.LOG2) {
			result = Math.log(dRaw) / Math.log(2);
		} else if (dataTransformation == EDataTransformation.LOG10) {
			result = Math.log10(dRaw);
		} else {
			throw new IllegalStateException("Conversion raw to normalized not implemented for data rep"
					+ dataTransformation);
		}

		result = (result - metaData.getMin()) / (metaData.getMax() - metaData.getMin());

		return result;
	}

	/**
	 * @return Returns a clone of a list of all dimension ids in the order they were initialized.
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getColumnIDList() {
		return (List<Integer>) defaultColumnIDs.clone();
	}

	/**
	 * @return Returns a new list of all record IDs in the order they were initialized
	 */
	public List<Integer> getRowIDList() {
		ArrayList<Integer> list = new ArrayList<Integer>(metaData.nrColumns);
		for (int count = 0; count < metaData.getNrRows(); count++) {
			list.add(count);
		}
		return list;
	}

	/**
	 * Returns the current {@link EDataTransformation}, which tells which was the input source before normalization.
	 * E.g., if this tells you {@link EDataTransformation#LOG2} that means that the normalized data used for rendering
	 * is based on data that has been logarithmized by the base 2 before.
	 *
	 * @return
	 */
	public EDataTransformation getExternalDataRep() {
		return dataTransformation;
	}

	/**
	 * Returns true if the set contains homgeneous data (data of the same kind, with one global minimum and maximum),
	 * else false
	 *
	 * @return
	 */
	public boolean isDataHomogeneous() {
		return isTableHomogeneous;
	}

	/**
	 * @return the defaultRecordPerspective, see {@link #defaultRecordPerspective}
	 */
	public RecordPerspective getDefaultRecordPerspective() {
		return defaultRecordPerspective;
	}

	/**
	 * Returns a {@link RecordPerspective} object for the specified ID. The {@link RecordPerspective} provides access to
	 * all mutable data on how to access the DataTable, e.g., {@link VirtualArray}, {@link ClusterTree},
	 * {@link GroupList}, etc.
	 *
	 * @param recordPerspectiveID
	 * @return the associated {@link RecordPerspective} object, or null if no such object is registered.
	 */
	public RecordPerspective getRecordPerspective(String recordPerspectiveID) {
		if (recordPerspectiveID == null)
			throw new IllegalArgumentException("perspectiveID was null");
		RecordPerspective recordData = hashRecordPerspectives.get(recordPerspectiveID);
		if (recordData == null)
			throw new IllegalStateException("No RecordPerspective registered for " + recordPerspectiveID
					+ ", registered Perspectives: " + hashRecordPerspectives);
		return recordData;
	}

	/**
	 * @param recordPerspectiveID
	 * @return True, if a {@link RecordPerspective} with the specified ID is registered, false otherwise.
	 */
	public boolean containsRecordPerspective(String recordPerspectiveID) {
		return hashRecordPerspectives.containsKey(recordPerspectiveID);
	}

	/**
	 * @param dimensionPerspectiveID
	 * @return True, if a {@link DimensionPerspective} with the specified ID is registered, false otherwise.
	 */
	public boolean containsDimensionPerspective(String dimensionPerspectiveID) {
		return hashDimensionPerspectives.containsKey(dimensionPerspectiveID);
	}

	/**
	 * Register a new {@link RecordPerspective} with this DataTable and trigger datadomain update.
	 *
	 * @param recordPerspective
	 */
	public void registerRecordPerspective(RecordPerspective recordPerspective) {
		registerRecordPerspective(recordPerspective, true);
	}

	/**
	 * Register a new {@link RecordPerspective} with this DataTable
	 *
	 * @param recordPerspective
	 * @param flat
	 *            determines whether a datadomain update event is triggered
	 */
	public void registerRecordPerspective(RecordPerspective recordPerspective, boolean triggerUpdate) {
		if (recordPerspective.getPerspectiveID() == null)
			throw new IllegalStateException("Record perspective not correctly initiaklized: " + recordPerspective);
		if (!recordPerspective.getIdType().equals(dataDomain.getRecordIDType()))
			throw new IllegalStateException("Invalid reocrd id type for this datadomain: "
					+ recordPerspective.getIdType());
		hashRecordPerspectives.put(recordPerspective.getPerspectiveID(), recordPerspective);

		if (recordPerspective.isDefault()) {
			if (defaultRecordPerspective != null)
				throw new IllegalStateException(
						"The default record perspective is already set. It is not possible to have multiple defaults.");
			defaultRecordPerspective = recordPerspective;
		}

		if (triggerUpdate) {
			triggerUpdateEvent();
		}
	}

	/**
	 * @return the defaultDimensionPerspective, see {@link #defaultDimensionPerspective}
	 */
	public DimensionPerspective getDefaultDimensionPerspective() {
		return defaultDimensionPerspective;
	}

	/**
	 * Returns a {@link DimensionPerspective} object for the specified ID. The {@link DimensionPerspective} provides
	 * access to all mutable data on how to access the DataTable, e.g., {@link VirtualArray}, {@link ClusterTree},
	 * {@link GroupList}, etc.
	 *
	 * @param dimensionPerspectiveID
	 * @return the associated {@link DimensionPerspective} object, or null if no such object is registered.
	 */
	public DimensionPerspective getDimensionPerspective(String dimensionPerspectiveID) {
		if (dimensionPerspectiveID == null)
			throw new IllegalArgumentException("perspectiveID was null");
		DimensionPerspective dimensionPerspective = hashDimensionPerspectives.get(dimensionPerspectiveID);
		if (dimensionPerspective == null)
			throw new IllegalStateException("No DimensionPerspective registered for " + dimensionPerspectiveID
					+ ", registered Perspectives: " + hashDimensionPerspectives);
		return dimensionPerspective;
	}

	/**
	 * Register a new {@link DimensionPerspective} with this DataTable and trigger data domain update.
	 *
	 * @param dimensionPerspective
	 */
	public void registerDimensionPerspective(DimensionPerspective dimensionPerspective) {
		registerDimensionPerspective(dimensionPerspective, true);
	}

	/**
	 * Register a new {@link DimensionPerspective} with this DataTable
	 *
	 * @param dimensionPerspective
	 * @param flat
	 *            determines whether a datadomain update event is triggered
	 */
	public void registerDimensionPerspective(DimensionPerspective dimensionPerspective, boolean triggerUpdate) {
		if (dimensionPerspective.getPerspectiveID() == null)
			throw new IllegalStateException("Dimension perspective not correctly initiaklized: " + dimensionPerspective);
		hashDimensionPerspectives.put(dimensionPerspective.getPerspectiveID(), dimensionPerspective);

		if (dimensionPerspective.isDefault()) {
			if (defaultDimensionPerspective != null)
				throw new IllegalStateException(
						"The default dimension perspective is already set. It is not possible to have multiple defaults.");
			defaultDimensionPerspective = dimensionPerspective;
		}

		if (triggerUpdate) {
			triggerUpdateEvent();
		}
	}

	/**
	 * Return a list of content VA types that have registered {@link RecordPerspective}.
	 *
	 * @return
	 */
	public Set<String> getRecordPerspectiveIDs() {
		return hashRecordPerspectives.keySet();
	}

	/**
	 * Return a list of dimension VA types that have registered {@link DimensionPerspective}
	 *
	 * @return
	 */
	public Set<String> getDimensionPerspectiveIDs() {
		return hashDimensionPerspectives.keySet();
	}

	/**
	 * Removes all data related to the set (Dimensions, Virtual Arrays and Sets) from the managers so that the garbage
	 * collector can handle it.
	 */
	public void destroy() {

	}

	@Override
	public void finalize() {
		Logger.log(new Status(IStatus.INFO, this.toString(), "Data table  " + this + "destroyed"));
	}

	@Override
	public String toString() {
		return "Set for " + dataDomain + " with " + hashColumns.size() + " dimensions.";
	}


	/**
	 * Add a column by reference. The column has to be fully initialized with data
	 *
	 * @param column
	 *            the column
	 */
	public void addColumn(AColumn<?, ?> column) {
		// if (hashDimensions.isEmpty()) {
		if (column instanceof NumericalColumn) {
			if (tableType == null)
				tableType = DataTableDataType.NUMERIC;
			else if (tableType.equals(DataTableDataType.NOMINAL))
				tableType = DataTableDataType.HYBRID;
		} else {
			if (tableType == null)
				tableType = DataTableDataType.NOMINAL;
			else if (tableType.equals(DataTableDataType.NUMERIC))
				tableType = DataTableDataType.HYBRID;
		}

		hashColumns.put(column.getID(), column);
		defaultColumnIDs.add(column.getID());

	}

	// ----------------------------------------------------------------------------
	// END OF PUBLIC INTERFACE
	// ----------------------------------------------------------------------------

	// -------------------- set creation ------------------------------
	// Set creation is achieved by employing methods of SetUtils which utilizes
	// package private methods in the
	// table.

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 *
	 * @param dataTransformation
	 *            Determines how the data is visualized. For options see {@link EDataTransformation}
	 * @param isTableHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global maximum
	 *            and minimum, meaning that all dimensions in the set contain equal data. If false, each dimension is
	 *            treated separately, has it's own min and max etc. Sets that contain nominal data MUST be
	 *            inhomogeneous.
	 */
	void setDataTransformation(EDataTransformation dataTransformation, boolean isTableHomogeneous) {
		this.isTableHomogeneous = isTableHomogeneous;
		if (dataTransformation == this.dataTransformation)
			return;

		this.dataTransformation = dataTransformation;

		for (AColumn<?, ?> dimension : hashColumns.values()) {
			if (dimension instanceof NumericalColumn) {
				((NumericalColumn<?, ?>) dimension).setDataTransformation(dataTransformation);
			}
		}

		if (isTableHomogeneous) {
			switch (dataTransformation) {
			case NONE:
				normalization.normalizeGlobally();
				break;
			case LOG10:
				normalization.log10();
				normalization.normalizeGlobally();
				break;
			case LOG2:
				normalization.log2();
				normalization.normalizeGlobally();
				break;
			}
		} else {
			switch (dataTransformation) {
			case NONE:
				normalization.normalizeLocally();
				break;
			case LOG10:
				normalization.log10();
				normalization.normalizeLocally();
				break;
			case LOG2:
				normalization.log2();
				normalization.normalizeLocally();
				break;
			}
		}
	}

	// ---------------------- helper functions ------------------------------

	void createDefaultRecordPerspective() {
		defaultRecordPerspective = new RecordPerspective(dataDomain);
		defaultRecordPerspective.setDefault(true);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		Integer nrRecordsInSample = null;
		List<Integer> recordIDs;
		if (isColumnDimension) {
			if (dataDomain.getDataSetDescription().getDataProcessingDescription() != null) {
				nrRecordsInSample = dataDomain.getDataSetDescription().getDataProcessingDescription()
						.getNrRowsInSample();
			}
			recordIDs = getRowIDList();
		} else {
			if (dataDomain.getDataSetDescription().getDataProcessingDescription() != null) {
				nrRecordsInSample = dataDomain.getDataSetDescription().getDataProcessingDescription()
						.getNrColumnsInSample();
			}
			recordIDs = getColumnIDList();
		}

		recordIDs = Algorithms.sampleList(nrRecordsInSample, recordIDs);

		data.setData(recordIDs);
		defaultRecordPerspective.setLabel("Ungrouped", true);
		defaultRecordPerspective.init(data);
		hashRecordPerspectives.put(defaultRecordPerspective.getPerspectiveID(), defaultRecordPerspective);

		triggerUpdateEvent();
	}

	void createDefaultDimensionPerspective() {

		defaultDimensionPerspective = new DimensionPerspective(dataDomain);
		defaultDimensionPerspective.setDefault(true);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		List<Integer> dimensionIDs;
		Integer nrDimensionsInsample = null;
		if (isColumnDimension) {
			if (dataDomain.getDataSetDescription().getDataProcessingDescription() != null) {
				nrDimensionsInsample = dataDomain.getDataSetDescription().getDataProcessingDescription()
						.getNrColumnsInSample();
			}
			dimensionIDs = getColumnIDList();
		} else {
			if (dataDomain.getDataSetDescription().getDataProcessingDescription() != null) {
				nrDimensionsInsample = dataDomain.getDataSetDescription().getDataProcessingDescription()
						.getNrRowsInSample();
			}
			dimensionIDs = getRowIDList();
		}
		// here we sample the list of dimensions to avoid problems with the heat
		// map TODO: we should probably move this to some better place

		dimensionIDs = Algorithms.sampleList(nrDimensionsInsample, dimensionIDs);

		data.setData(dimensionIDs);
		defaultDimensionPerspective.init(data);

		defaultDimensionPerspective.setLabel("Ungrouped", true);

		hashDimensionPerspectives.put(defaultDimensionPerspective.getPerspectiveID(), defaultDimensionPerspective);

		triggerUpdateEvent();
	}

	private void triggerUpdateEvent() {
		DataDomainUpdateEvent event = new DataDomainUpdateEvent(dataDomain);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}
}
