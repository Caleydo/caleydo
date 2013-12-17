/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
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

	/**
	 *
	 */
	private static final double NAN_THRESHOLD = 0.8;

	public class Transformation {
		/** Untransformed data */
		public static final String LINEAR = "None";
		public static final String INVERT = "Inverted";
	}

	/** The data domain holding this table */
	protected final ATableBasedDataDomain dataDomain;

	/** The color-mapper for this table */
	private ColorMapper colorMapper;

	/**
	 * The transformation delivered when calling the {@link #getNormalizedValue(Integer, Integer)} method without and
	 * explicit transformation
	 */
	protected String defaultDataTransformation = Table.Transformation.LINEAR;

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
	 * Sampled record perspective that only contains a subset of the full dataset. How big the sampled subset is, is
	 * determined during the data loading. Will return the default record perspective if no sampled version is
	 * available.
	 */
	private Perspective sampledRecordPerspective;

	/** Same as {@link #defaultRecordPerspective} for dimensions */
	private Perspective sampledDimensionPerspective;

	/**
	 * Flag telling whether the columns correspond to dimensions (false) or whether the columns correspond to records
	 * (true)
	 */
	protected boolean isColumnDimension = false;

	protected final DataSetDescription dataSetDescription;

	/**
	 * Constructor for the table. Creates and initializes members and registers the set whit the set manager. Also
	 * creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public Table(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		this.dataSetDescription = dataDomain.getDataSetDescription();
		isColumnDimension = !dataSetDescription.isTransposeMatrix();
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
		if (dimensionID == null || recordID == null) {
			throw new IllegalArgumentException("Dimension ID or record ID was null. Dimension ID: " + dimensionID
					+ ", Record ID: " + recordID);
		}
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

		if (columnID < 0 || columnID >= columns.size())
			return null;
		return (RAW_DATA_TYPE) columns.get(columnID).getRaw(rowID);
	}

	/**
	 * Returns the 3-component color for the given table cell. This works independent of the data type.
	 *
	 * FIXME: inhomogeneous numerical is not implemented
	 *
	 * @param dimensionID
	 * @param recordID
	 * @return
	 */
	public float[] getColor(Integer dimensionID, Integer recordID) {
		if (isDataHomogeneous()) {
			return getColorMapper().getColor(getNormalizedValue(dimensionID, recordID));
		} else {
			if (EDataClass.CATEGORICAL.equals(getDataClass(dimensionID, recordID))) {
				CategoricalClassDescription<?> specific = (CategoricalClassDescription<?>) getDataClassSpecificDescription(
						dimensionID, recordID);
				Object category = getRaw(dimensionID, recordID);
				if (category == null)
					return Color.NOT_A_NUMBER_COLOR.getRGBA();
				CategoryProperty<?> p = specific.getCategoryProperty(category);
				if (p == null)
					return Color.NOT_A_NUMBER_COLOR.getRGBA();
				return specific.getCategoryProperty(category).getColor().getRGBA();
			} else {
				// simple implementation just gray scale
				Float v = getNormalizedValue(dimensionID, recordID);
				if (v == null || v.isNaN())
					return Color.NOT_A_NUMBER_COLOR.getRGBA();
				return new Color(v.floatValue()).getRGBA();
				// not implemented
				// throw new IllegalStateException("not implemented");
			}
		}

	}

	/**
	 * <p>
	 * Returns the ColorMapper for this dataset. Warning: this only works properly for homogeneous numerical datasets.
	 * </p>
	 * <p>
	 * Use {@link Table#getColor(Integer, Integer)} instead if you want access to a cell's color - which works for all
	 * data types.
	 * </p>
	 * TODO: move this to Table and provide separate interfaces for homogeneous and inhomogeneous datasets.
	 *
	 * @return the colorMapper, see {@link #colorMapper}
	 */
	public ColorMapper getColorMapper() {

		if (colorMapper == null) {
			if (this instanceof NumericalTable && ((NumericalTable) this).getDataCenter() != null) {
				colorMapper = ColorMapper.createDefaultThreeColorMapper();
			} else if (this instanceof CategoricalTable<?>) {
				colorMapper = ((CategoricalTable<?>) this).createColorMapper();
			} else {
				colorMapper = ColorMapper.createDefaultTwoColorMapper();
			}
		}
		// FIXME this is a hack due to a bug in colorMapper
		// colorMapper.update();
		return colorMapper;
	}

	/**
	 * @param colorMapper
	 *            setter, see {@link #colorMapper}
	 */
	public void setColorMapper(ColorMapper colorMapper) {
		this.colorMapper = colorMapper;
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
	 * @param sampled
	 *            Determines whether the full or the sampled default record perspective is requested
	 * @return the defaultRecordPerspective, see {@link #defaultRecordPerspective}
	 */
	public Perspective getDefaultRecordPerspective(boolean sampled) {

		if (sampled && sampledRecordPerspective != null)
			return sampledRecordPerspective;

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
	 * @param sampled
	 *            Determines whether the full or the sampled default dimension perspective is requested
	 * @return the defaultDimensionPerspective, see {@link #defaultDimensionPerspective}
	 */
	public Perspective getDefaultDimensionPerspective(boolean sampled) {

		if (sampled && sampledDimensionPerspective != null)
			return sampledDimensionPerspective;

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
	public void finalize() throws Throwable {
		Logger.log(new Status(IStatus.INFO, this.toString(), "Data table  " + this + "destroyed"));
		super.finalize();
	}

	@Override
	public String toString() {
		String message = "Inhomogeneous ";
		if (this instanceof NumericalTable)
			message = "Numerical ";
		else if (this instanceof CategoricalTable)
			message = "Categorical ";

		return message + "table for " + dataSetDescription.getDataSetName() + "(" + size() + "," + depth() + ")";
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

	void createDefaultRecordPerspectives() {

		List<Integer> recordIDs;
		List<Integer> sampleIDs;
		Integer nrRecordsInSample = null;
		if (isColumnDimension) {
			if (dataSetDescription.getDataProcessingDescription() != null) {
				nrRecordsInSample = dataSetDescription.getDataProcessingDescription().getNrRowsInSample();
			}
			recordIDs = getRowIDList();
			sampleIDs = getColumnIDList();
		} else {
			if (dataSetDescription.getDataProcessingDescription() != null) {
				nrRecordsInSample = dataSetDescription.getDataProcessingDescription().getNrColumnsInSample();
			}
			recordIDs = getColumnIDList();
			sampleIDs = getRowIDList();
		}

		defaultRecordPerspective = createDefaultRecordPerspective(false, recordIDs);

		if (nrRecordsInSample != null) {
			// recordIDs = sampleMostVariableRecords(nrRecordsInSample, recordIDs, sampleIDs);
			// sampledRecordPerspective = createDefaultRecordPerspective(true, recordIDs);
			throw new NotImplementedException();
		}

		triggerUpdateEvent();
	}

	private Perspective createDefaultRecordPerspective(boolean sampled, List<Integer> recordIDs) {

		Perspective recordPerspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
		recordPerspective.setDefault(!sampled);

		String label = sampled ? "Ungrouped Sampled" : "Ungrouped";
		recordPerspective.setLabel(label, true);

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(recordIDs);
		recordPerspective.init(data);

		hashRecordPerspectives.put(recordPerspective.getPerspectiveID(), recordPerspective);

		return recordPerspective;
	}

	private List<Integer> sampleMostVariableDimensions(int sampleSize, List<Integer> recordIDs,
			List<Integer> dimensionIDs) {

		if (dimensionIDs.size() <= sampleSize)
			return dimensionIDs;

		List<Pair<Double, Integer>> allDimVar = new ArrayList<Pair<Double, Integer>>();

		for (Integer dimID : dimensionIDs) {
			double[] allDimsPerRecordArray = new double[recordIDs.size()];

			for (int i = 0; i < recordIDs.size(); i++) {
				// allDimsPerRecordArray[i] = dataDomain.getNormalizedValue(dataDomain.getDimensionIDType(), dimID,
				// dataDomain.getRecordIDType(), recordIDs.get(i));
				allDimsPerRecordArray[i] = (Float) getRaw(dimID, recordIDs.get(i));
			}

			AdvancedDoubleStatistics stats = AdvancedDoubleStatistics.of(allDimsPerRecordArray);
			// throwing out all values with more than 80% NAN
			if (stats.getNaNs() < stats.getN() * NAN_THRESHOLD) {
				allDimVar.add(new Pair<Double, Integer>(stats.getMedianAbsoluteDeviation(), dimID));
			}
		}

		Collections.sort(allDimVar, Collections.reverseOrder(Pair.<Double> compareFirst()));

		allDimVar = allDimVar.subList(0, sampleSize);

		List<Integer> sampledDimensionIDs = new ArrayList<>();
		for (Pair<Double, Integer> recordVar : allDimVar) {
			sampledDimensionIDs.add(recordVar.getSecond());
		}
		return sampledDimensionIDs;
	}

	void createDefaultDimensionPerspectives() {

		List<Integer> dimensionIDs;
		List<Integer> recordIDs;
		Integer nrDimensionsInSample = null;
		if (isColumnDimension) {
			if (dataSetDescription.getDataProcessingDescription() != null) {
				nrDimensionsInSample = dataSetDescription.getDataProcessingDescription().getNrColumnsInSample();
			}
			dimensionIDs = getColumnIDList();
			recordIDs = getRowIDList();
		} else {
			if (dataSetDescription.getDataProcessingDescription() != null) {
				nrDimensionsInSample = dataSetDescription.getDataProcessingDescription().getNrRowsInSample();
			}
			dimensionIDs = getRowIDList();
			recordIDs = getColumnIDList();
		}

		defaultDimensionPerspective = createDefaultDimensionPerspective(false, dimensionIDs);

		if (nrDimensionsInSample != null) {
			dimensionIDs = sampleMostVariableDimensions(nrDimensionsInSample, recordIDs, dimensionIDs);
			sampledDimensionPerspective = createDefaultDimensionPerspective(true, dimensionIDs);
		}

		triggerUpdateEvent();
	}

	private Perspective createDefaultDimensionPerspective(boolean sampled, List<Integer> dimensionIDs) {

		Perspective dimensionPerspective = new Perspective(dataDomain, dataDomain.getDimensionIDType());
		dimensionPerspective.setDefault(!sampled);

		String label = sampled ? "Ungrouped Sampled" : "Ungrouped";

		dimensionPerspective.setLabel(label, true);

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(dimensionIDs);
		dimensionPerspective.init(data);

		hashDimensionPerspectives.put(dimensionPerspective.getPerspectiveID(), dimensionPerspective);

		return dimensionPerspective;
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
	protected int getNrColumns() {
		return columns.size();
	}

	/** Get the number of rows in the table */
	protected int getNrRows() {
		return columns.iterator().next().size();
	}
}
