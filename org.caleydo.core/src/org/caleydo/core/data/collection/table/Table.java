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

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * A table is the main container for matrix data in Caleydo. It is made up of {@link AColumn}s, where each column
 * corresponds to a column in an input file.
 * </p>
 * <p>
 * The table does not provide direct access to columns and rows, but abstracts it through <b>dimensions</b> and
 * <b>records</b> to allow a de-facto transposition of the input data.
 * <p>
 * Every column in the table has to have the same length. Data is immutable.
 * </p>
 * <p>
 * The base implementation of Table does not make any assumptions on the relationship of data between individual
 * columns. Specific implementations for homogeneous numerical ({@link NumericalTable}) and for homogeneous categorical
 * ({@link CategoricalTable}) exist.
 * </p>
 * <p>
 * The data should be accessed through {@link VirtualArray}s, which are stored in {@link Perspective}s. The table
 * creates default instances for both records and dimensions, but modification to these are common.
 * </p>
 * <h2>Table Creation</h2>
 * <p>
 * A data table is created using the {@link TableUtils} implementation.
 * </p>
 *
 * @author Alexander Lex
 */
public class Table {

	public class Transformation {
		/** Untransformed data */
		public static final String NONE = "NONE";
	}

	/** The data domain holding this table */
	private ATableBasedDataDomain dataDomain;

	/**
	 * The transformation delivered when calling the {@link #getNormalizedValue(Integer, Integer)} method without and
	 * explicit transformation
	 */
	private String defaultDataTransformation = Table.Transformation.NONE;

	/** The columns of the table hashed by their column ID */
	protected List<AColumn<?, ?>> columns;

	/** List of column IDs in the order as they have been added */
	private ArrayList<Integer> defaultColumnIDs;

	/** Container holding all the record perspectives registered. The perspectiveIDs are the keys */
	private HashMap<String, Perspective> hashRecordPerspectives;
	/** same as {@link #hashRecordPerspectives} for dimensions */
	private HashMap<String, Perspective> hashDimensionPerspectives;

	/**
	 * Default record perspective. Initially all the data is contained in this perspective. If not otherwise specified,
	 * filters, clusterings etc. are always applied to this perspective
	 */
	private Perspective defaultRecordPerspective;

	/** Same as {@link #defaultRecordPerspective} for dimensions */
	private Perspective defaultDimensionPerspective;

	/**
	 * Flag telling whether the columns correspond to dimensions (false) or whether the columns correspond to records
	 * (true)
	 */
	protected boolean isColumnDimension = false;

	/** The number of records in the table */
	protected int depth = 0;

	/**
	 * Constructor for the table. Creates and initializes members and registers the set whit the set manager. Also
	 * creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public Table(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		isColumnDimension = !dataDomain.getDataSetDescription().isTransposeMatrix();
		columns = new ArrayList<>();
		hashRecordPerspectives = new HashMap<String, Perspective>(6);
		hashDimensionPerspectives = new HashMap<String, Perspective>(3);
		defaultColumnIDs = new ArrayList<Integer>();
	}

	/**
	 * Returns a boolean specifying whether the data is homogenous (i.e. all columns have the same data type and value
	 * ranges. False for base class. Implementing classes may override this
	 */
	public boolean isDataHomogeneous() {
		return false;
	}

	/**
	 * Get the number of dimensions in a set
	 *
	 * @return
	 */
	public int size() {
		if (isColumnDimension)
			return getNrColumns();
		else
			return getNrRows();
	}

	/**
	 * Get the depth of the set, which is the number of records, the length of the dimensions
	 *
	 * @return the number of elements in the dimensions contained in the list
	 */
	public int depth() {
		if (isColumnDimension)
			return getNrRows();
		else
			return getNrColumns();
	}

	/**
	 * Get parent data domain
	 *
	 * @param dataDomain
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * Returns the normalized value using the dataTransformation set in {@link #defaultDataTransformation}
	 *
	 * @param dimensionID
	 * @param recordID
	 * @return
	 */
	public Float getNormalizedValue(Integer dimensionID, Integer recordID) {
		try {
			if (isColumnDimension) {
				return columns.get(dimensionID).getNormalizedValue(defaultDataTransformation, recordID);
			} else {
				AColumn<?, ?> column = columns.get(recordID);
				return column.getNormalizedValue(defaultDataTransformation, dimensionID);
			}
		} catch (NullPointerException npe) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Data table does not contain a value for record: "
					+ recordID + " and dimension " + dimensionID, npe));
			return null;
		}

	}

	public Float getNormalizedValue(String dataTransformation, Integer dimensionID, Integer recordID) {
		try {
			int colID;
			int rowID;
			if (isColumnDimension) {
				colID = dimensionID;
				rowID = recordID;
			} else {
				rowID = dimensionID;
				colID = recordID;
			}

			AColumn<?, ?> col = columns.get(colID);
			return col.getNormalizedValue(dataTransformation, rowID);
		} catch (NullPointerException npe) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Data table does not contain a value for record: "
					+ recordID + " and dimension " + dimensionID, npe));
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
		return columns.get(columnID).getRawAsString(rowID);
	}

	// public boolean containsDataRepresentation(DataRepresentation dataRepresentation, Integer dimensionID,
	// Integer recordID) {
	// Integer columnID = dimensionID;
	//
	// if (!isColumnDimension)
	// columnID = recordID;
	//
	// return columns.get(columnID).containsDataRepresentation(dataRepresentation);
	// }

	public EDataType getRawDataType(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		if (!isColumnDimension)
			columnID = recordID;
		return columns.get(columnID).getRawDataType();
	}

	/** Returns the {@link EDataClass} associated with the value specified through dimension and record ID */
	public EDataClass getDataClass(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		if (!isColumnDimension)
			columnID = recordID;
		return columns.get(columnID).getDataClass();

	}

	@SuppressWarnings("unchecked")
	public <RAW_DATA_TYPE> RAW_DATA_TYPE getRaw(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		Integer rowID = recordID;
		if (!isColumnDimension) {
			columnID = recordID;
			rowID = dimensionID;
		}

		return (RAW_DATA_TYPE) columns.get(columnID).getRaw(rowID);
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
		int rowCount = getNrRows();
		ArrayList<Integer> list = new ArrayList<Integer>(rowCount);
		for (int i = 0; i < rowCount; i++) {
			list.add(i);
		}
		return list;
	}

	/**
	 * @return the defaultRecordPerspective, see {@link #defaultRecordPerspective}
	 */
	public Perspective getDefaultRecordPerspective() {
		return defaultRecordPerspective;
	}

	/**
	 * Returns a {@link Perspective} object for the specified ID. The {@link Perspective} provides access to all mutable
	 * data on how to access the Table, e.g., {@link VirtualArray}, {@link ClusterTree}, {@link GroupList}, etc.
	 *
	 * @param recordPerspectiveID
	 * @return the associated {@link Perspective} object, or null if no such object is registered.
	 */
	public Perspective getRecordPerspective(String recordPerspectiveID) {
		if (recordPerspectiveID == null)
			throw new IllegalArgumentException("perspectiveID was null");
		Perspective recordData = hashRecordPerspectives.get(recordPerspectiveID);
		if (recordData == null)
			throw new IllegalStateException("No Perspective registered for " + recordPerspectiveID
					+ ", registered Perspectives: " + hashRecordPerspectives);
		return recordData;
	}

	/**
	 * @param recordPerspectiveID
	 * @return True, if a {@link Perspective} with the specified ID is registered, false otherwise.
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
	 * Register a new {@link Perspective} with this Table and trigger datadomain update.
	 *
	 * @param recordPerspective
	 */
	public void registerRecordPerspective(Perspective recordPerspective) {
		registerRecordPerspective(recordPerspective, true);
	}

	/**
	 * Register a new {@link Perspective} with this Table
	 *
	 * @param recordPerspective
	 * @param flat
	 *            determines whether a datadomain update event is triggered
	 */
	public void registerRecordPerspective(Perspective recordPerspective, boolean triggerUpdate) {
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
	public Perspective getDefaultDimensionPerspective() {
		return defaultDimensionPerspective;
	}

	/**
	 * Returns a {@link DimensionPerspective} object for the specified ID. The {@link DimensionPerspective} provides
	 * access to all mutable data on how to access the Table, e.g., {@link VirtualArray}, {@link ClusterTree},
	 * {@link GroupList}, etc.
	 *
	 * @param dimensionPerspectiveID
	 * @return the associated {@link DimensionPerspective} object, or null if no such object is registered.
	 */
	public Perspective getDimensionPerspective(String dimensionPerspectiveID) {
		if (dimensionPerspectiveID == null)
			throw new IllegalArgumentException("perspectiveID was null");
		Perspective dimensionPerspective = hashDimensionPerspectives.get(dimensionPerspectiveID);
		if (dimensionPerspective == null)
			throw new IllegalStateException("No DimensionPerspective registered for " + dimensionPerspectiveID
					+ ", registered Perspectives: " + hashDimensionPerspectives);
		return dimensionPerspective;
	}

	/**
	 * Register a new {@link DimensionPerspective} with this Table and trigger data domain update.
	 *
	 * @param dimensionPerspective
	 */
	public void registerDimensionPerspective(Perspective dimensionPerspective) {
		registerDimensionPerspective(dimensionPerspective, true);
	}

	/**
	 * Register a new {@link DimensionPerspective} with this Table
	 *
	 * @param dimensionPerspective
	 * @param flat
	 *            determines whether a datadomain update event is triggered
	 */
	public void registerDimensionPerspective(Perspective dimensionPerspective, boolean triggerUpdate) {
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
	 * Return a list of content VA types that have registered {@link Perspective}.
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
		String message = "Inhomogeneous ";
		if (this instanceof NumericalTable)
			message = "Numerical ";
		else if (this instanceof CategoricalTable)
			message = "Categorical ";

		return message + "table for " + dataDomain.getDataSetDescription().getDataSetName() + "(" + size() + ","
				+ depth() + ")";
	}

	/**
	 * Add a column to the table. An id is assigned to the column.
	 *
	 * @param column
	 *            the column
	 * @return the ID assigned to the column
	 */
	public int addColumn(AColumn<?, ?> column) {
		int id = columns.size();
		column.setID(id);
		columns.add(column);
		defaultColumnIDs.add(column.getID());
		return id;
	}

	/**
	 * Returns a description of the meta data of the element. Examples are {@link NumericalProperties} for numerical
	 * data and {@link CategoricalClassDescription} for categorical data.
	 *
	 * @param dimensionID
	 * @param recordID
	 * @return
	 */
	public Object getDataClassSpecificDescription(Integer dimensionID, Integer recordID) {
		Integer columnID = recordID;
		if (isColumnDimension)
			columnID = dimensionID;

		return columns.get(columnID).getDataClassSpecificDescription();
	}

	/**
	 * @param defaultDataTransformation
	 *            setter, see {@link defaultDataTransformation}
	 */
	public void setDefaultDataTransformation(String defaultDataTransformation) {
		this.defaultDataTransformation = defaultDataTransformation;
	}

	/**
	 * @return the defaultDataTransformation, see {@link #defaultDataTransformation}
	 */
	public String getDefaultDataTransformation() {
		return defaultDataTransformation;
	}

	// ----------------------------------------------------------------------------
	// END OF PUBLIC INTERFACE
	// ----------------------------------------------------------------------------

	// -------------------- set creation ------------------------------
	// Set creation is achieved by employing methods of SetUtils which utilizes
	// package private methods in the
	// table.

	// ---------------------- helper functions ------------------------------

	void createDefaultRecordPerspective() {
		defaultRecordPerspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
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

		defaultDimensionPerspective = new Perspective(dataDomain, dataDomain.getDimensionIDType());
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

	/**
	 * Normalize all dimensions in the set, based solely on the values within each dimension. Operates with the raw data
	 * as basis by default, however when a logarithmized representation is in the dimension this is used.
	 */
	protected void normalize() {
		for (AColumn<?, ?> dimension : columns) {
			dimension.normalize();
		}
	}

	/** Get the number of columns in the table */
	private int getNrColumns() {
		return columns.size();
	}

	/** Get the number of rows in the table */
	private int getNrRows() {
		return columns.iterator().next().size();
	}
}
