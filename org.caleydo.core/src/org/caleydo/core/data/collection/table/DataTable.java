package org.caleydo.core.data.collection.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.NominalColumn;
import org.caleydo.core.data.collection.dimension.NumericalColumn;
import org.caleydo.core.data.collection.dimension.RawDataType;
import org.caleydo.core.data.collection.table.statistics.StatisticsResult;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.dimension.ColumnManager;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <h2>General Information</h2>
 * <p>
 * A set is the main container for tabular data in Caleydo. A set is made up of {@link IDimension}s, where
 * each dimension corresponds to a column in a tabular data table. Columns are therefore always referred to as
 * <b>Dimensions</b> and rows as <b>Record</b> The data should be accessed through {@link VirtualArray}s,
 * which are stored in {@link DimensionPerspective}s for Dimensions and {@link RecordPerspective}s for Record.
 * </p>
 * <h2>DataTable Creation</h2>
 * <p>
 * A data table relies heavily upon {@link DataTableUtils} for being created. Many creation related functions
 * are provided there, sometimes interfacing with package private methods in this class.
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataTable
	extends AUniqueObject {

	protected HashMap<Integer, AColumn> hashColumns;

	/** List of dimension IDs in the order as they have been added */
	protected ArrayList<Integer> defaultColumnIDs;
	protected HashMap<String, RecordPerspective> hashRecordPerspectives;
	protected HashMap<String, DimensionPerspective> hashDimensionPerspectives;

	protected NumericalColumn meanDimension;

	// protected DimensionData defaultDimensionData;
	// protected RecordPerspective defaultRecordPerspective;

	protected ExternalDataRepresentation externalDataRep;

	protected boolean isTableHomogeneous = false;

	protected StatisticsResult statisticsResult;

	protected DataTableDataType tableType = DataTableDataType.NUMERIC;

	ATableBasedDataDomain dataDomain;

	boolean containsUncertaintyData = false;

	/** all metaData for this DataTable is held in or accessible through this object */
	protected MetaData metaData;

	/** everything related to uncertainty is held in or accessible through this object */
	private Uncertainty uncertainty;

	/** everything related to normalization of the data is held in or accessible through this object */
	private Normalization normalization;

	private boolean isColumnDimension = false;

	/**
	 * Constructor for the table. Creates and initializes members and registers the set whit the set manager.
	 * Also creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public DataTable(ATableBasedDataDomain dataDomain) {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.DATA_TABLE));
		this.dataDomain = dataDomain;
		isColumnDimension = dataDomain.isColumnDimension();
		// initWithDataDomain();
	}

	// private void initWithDataDomain() {
	// ClusterTree tree = new ClusterTree(dataDomain.getDimensionIDType());
	// ClusterNode root = new ClusterNode(tree, "Root", 1, true, -1);
	// tree.setRootNode(root);
	// }

	/**
	 * Initialization of member variables. Safe to be called by sub-classes.
	 */
	{
		hashColumns = new HashMap<Integer, AColumn>();
		hashRecordPerspectives = new HashMap<String, RecordPerspective>(6);
		hashDimensionPerspectives = new HashMap<String, DimensionPerspective>(3);
		defaultColumnIDs = new ArrayList<Integer>();
		statisticsResult = new StatisticsResult(this);
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

	/**
	 * Get the dimension associated with the ID provided. Returns null if no such dimension is registered.
	 * 
	 * @param dimensionID
	 *            a unique dimension ID
	 * @return
	 */
	// public ADimension get(Integer dimensionID) {
	// return hashDimensions.get(dimensionID);
	// }

	public float getFloat(DataRepresentation dataRepresentation, Integer dimensionID, Integer recordID) {
		if (isColumnDimension)
			return hashColumns.get(dimensionID).getFloat(dataRepresentation, recordID);
		else
			return hashColumns.get(recordID).getFloat(dataRepresentation, dimensionID);

	}

	public <RawType> RawType getRaw(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		Integer rowID = recordID;
		if (!isColumnDimension) {
			columnID = recordID;
			rowID = dimensionID;
		}

		AColumn dimension = hashColumns.get(columnID);
		return ((NominalColumn<RawType>) dimension).getRaw(rowID);
	}

	public String getRawAsString(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		Integer rowID = recordID;
		if (!isColumnDimension) {
			columnID = recordID;
			rowID = dimensionID;
		}
		RawDataType rawDataType = hashColumns.get(columnID).getRawDataType();
		String result;
		if (rawDataType == RawDataType.FLOAT) {
			result = Float.toString(getFloat(DataRepresentation.RAW, columnID, rowID));
		}
		else if (rawDataType == RawDataType.STRING) {
			result = getRaw(columnID, rowID);
		}
		else {
			throw new IllegalStateException("DataType " + rawDataType + " not implemented");

		}
		return result;
	}

	public boolean containsDataRepresentation(DataRepresentation dataRepresentation, Integer dimensionID,
		Integer recordID) {
		Integer columnID = dimensionID;

		if (!isColumnDimension)
			columnID = recordID;

		return hashColumns.get(columnID).containsDataRepresentation(dataRepresentation);
	}

	public RawDataType getRawDataType(Integer dimensionID, Integer recordID) {
		Integer columnID = dimensionID;
		if (!isColumnDimension)
			columnID = recordID;
		return hashColumns.get(columnID).getRawDataType();
	}

	/**
	 * Iterate over the dimensions based on a virtual array
	 * 
	 * @param type
	 * @return
	 */
	public Iterator<AColumn> iterator(String type) {
		return new DimensionIterator(hashColumns, hashDimensionPerspectives.get(type).getVirtualArray());
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
		if (externalDataRep == ExternalDataRepresentation.NORMAL) {
			return result;
		}
		else if (externalDataRep == ExternalDataRepresentation.LOG2) {
			return Math.pow(2, result);
		}
		else if (externalDataRep == ExternalDataRepresentation.LOG10) {
			return Math.pow(10, result);
		}
		throw new IllegalStateException("Conversion raw to normalized not implemented for data rep"
			+ externalDataRep);
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

		if (externalDataRep == ExternalDataRepresentation.NORMAL) {
			result = dRaw;
		}
		else if (externalDataRep == ExternalDataRepresentation.LOG2) {
			result = Math.log(dRaw) / Math.log(2);
		}
		else if (externalDataRep == ExternalDataRepresentation.LOG10) {
			result = Math.log10(dRaw);
		}
		else {
			throw new IllegalStateException("Conversion raw to normalized not implemented for data rep"
				+ externalDataRep);
		}

		result = (result - metaData.getMin()) / (metaData.getMax() - metaData.getMin());

		return result;
	}

	/**
	 * @return Returns a list of all dimension ids in the order they were initialized
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getColumnIDList() {
		return (List<Integer>) defaultColumnIDs.clone();
	}

	/**
	 * @return Returns a list of all record ids in the order they were initialized
	 */
	public List<Integer> getRowIDList() {
		ArrayList<Integer> list = new ArrayList<Integer>(metaData.depth);
		for (int count = 0; count < metaData.depth(); count++) {
			list.add(count);
		}
		return list;
	}

	/**
	 * Returns the current {@link ExternalDataRepresentation}, which tells which was the input source before
	 * normalization. E.g., if this tells you {@link ExternalDataRepresentation#LOG2} that means that the
	 * normalized data used for rendering is based on data that has been logarithmized by the base 2 before.
	 * 
	 * @return
	 */
	public ExternalDataRepresentation getExternalDataRep() {
		return externalDataRep;
	}

	/**
	 * Returns true if the set contains homgeneous data (data of the same kind, with one global minimum and
	 * maximum), else false
	 * 
	 * @return
	 */
	public boolean isSetHomogeneous() {
		return isTableHomogeneous;
	}

	/**
	 * Returns a {@link RecordPerspective} object for the specified ID. The {@link RecordPerspective} provides
	 * access to all mutable data on how to access the DataTable, e.g., {@link VirtualArray},
	 * {@link ClusterTree}, {@link GroupList}, etc.
	 * 
	 * @param recordPerspectiveID
	 * @return the associated {@link RecordPerspective} object, or null if no such object is registered.
	 */
	public RecordPerspective getRecordPerspective(String recordPerspectiveID) {
		RecordPerspective recordData = hashRecordPerspectives.get(recordPerspectiveID);
		return recordData;
	}

	/**
	 * Register a new {@link RecordPerspective} with this DataTable
	 * 
	 * @param recordPerspective
	 */
	public void registerRecordPerspecive(RecordPerspective recordPerspective) {
		if (recordPerspective.getPerspectiveID() == null)
			throw new IllegalStateException("Record perspective not correctly initiaklized: "
				+ recordPerspective);
		hashRecordPerspectives.put(recordPerspective.getPerspectiveID(), recordPerspective);
	}

	/**
	 * Returns a {@link DimensionPerspective} object for the specified ID. The {@link DimensionPerspective}
	 * provides access to all mutable data on how to access the DataTable, e.g., {@link VirtualArray},
	 * {@link ClusterTree}, {@link GroupList}, etc.
	 * 
	 * @param dimensionPerspectiveID
	 * @return the associated {@link DimensionPerspective} object, or null if no such object is registered.
	 */
	public DimensionPerspective getDimensionPerspective(String dimensionPerspectiveID) {
		return hashDimensionPerspectives.get(dimensionPerspectiveID);
	}

	/**
	 * Register a new {@link DimensionPerspective} with this DataTable
	 * 
	 * @param dimensionPerspective
	 */
	public void registerDimensionPerspective(DimensionPerspective dimensionPerspective) {
		if (dimensionPerspective.getPerspectiveID() == null)
			throw new IllegalStateException("Dimension perspective not correctly initiaklized: "
				+ dimensionPerspective);
		hashDimensionPerspectives.put(dimensionPerspective.getPerspectiveID(), dimensionPerspective);
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
	 * Removes all data related to the set (Dimensions, Virtual Arrays and Sets) from the managers so that the
	 * garbage collector can handle it.
	 */
	public void destroy() {
		GeneralManager gm = GeneralManager.get();
		ColumnManager sm = gm.getColumnManager();
		for (Integer dimensionID : hashColumns.keySet()) {
			sm.unregisterItem(dimensionID);
		}
		// clearing the VAs. This should not be necessary since they should be destroyed automatically.
		// However, to make sure.
	}

	@Override
	public void finalize() {
		Logger.log(new Status(IStatus.INFO, this.toString(), "Set " + this + "destroyed"));
	}

	@Override
	public String toString() {
		return "Set for " + dataDomain + " with " + hashColumns.size() + " dimensions.";
	}

	/**
	 * Returns the statistics results. E.g. comparative t-test between sets.
	 * 
	 * @return the statistics result object containing all results.
	 */
	@XmlTransient
	public StatisticsResult getStatisticsResult() {
		return statisticsResult;
	}

	/**
	 * Returns a dimension containing the mean values of all the dimensions in the table. The mean dimension
	 * contains raw and normalized values. The mean is calculated based on the raw data, that means for
	 * calculating the means possibly specified cut-off values are not considered, since cut-off values are
	 * meant for visualization only.
	 * 
	 * @return the dimension containing means for all content elements
	 */
	public NumericalColumn getMeanDimension(String dimensionPerspectiveID) {
		if (!tableType.equals(DataTableDataType.NUMERIC) || !isTableHomogeneous)
			throw new IllegalStateException(
				"Can not provide a mean dimension if set is not numerical (Set type: " + tableType
					+ ") or not homgeneous (isHomogeneous: " + isTableHomogeneous + ")");
		if (meanDimension == null) {
			meanDimension = new NumericalColumn();
			meanDimension.setExternalDataRepresentation(ExternalDataRepresentation.NORMAL);

			float[] meanValues = new float[metaData.depth()];
			DimensionVirtualArray dimensionVA =
				hashDimensionPerspectives.get(dimensionPerspectiveID).getVirtualArray();
			for (int contentCount = 0; contentCount < metaData.depth(); contentCount++) {
				float sum = 0;
				for (int dimensionID : dimensionVA) {
					sum += getFloat(DataRepresentation.RAW, dimensionID, contentCount);
				}
				meanValues[contentCount] = sum / metaData.size();
			}
			meanDimension.setRawData(meanValues);
			// meanDimension.normalize();
		}
		return meanDimension;
	}

	public void setStatisticsResult(StatisticsResult statisticsResult) {
		this.statisticsResult = statisticsResult;
	}

	public void setContainsUncertaintyData(boolean containsUncertaintyData) {
		this.containsUncertaintyData = containsUncertaintyData;
		if (containsUncertaintyData == true)
			uncertainty = new Uncertainty(this);
	}

	public Uncertainty getUncertainty() {
		return uncertainty;
	}

	public boolean containsUncertaintyData() {
		return containsUncertaintyData;
	}

	// ----------------------------------------------------------------------------
	// END OF PUBLIC INTERFACE
	// ----------------------------------------------------------------------------

	// -------------------- set creation ------------------------------
	// Set creation is achieved by employing methods of SetUtils which utilizes package private methods in the
	// table.

	/**
	 * Add a column based on its id. The column has to be fully initialized with data
	 * 
	 * @param columnID
	 */
	void addColumn(int columnID) {
		ColumnManager columnManager = GeneralManager.get().getColumnManager();

		if (!columnManager.hasItem(columnID))
			throw new IllegalArgumentException("Requested Dimension with ID " + columnID + " does not exist.");

		AColumn dimension = columnManager.getItem(columnID);
		addColumn(dimension);
	}

	/**
	 * Add a column by reference. The column has to be fully initialized with data
	 * 
	 * @param column
	 *            the column
	 */
	void addColumn(AColumn column) {
		// if (hashDimensions.isEmpty()) {
		if (column instanceof NumericalColumn) {
			if (tableType == null)
				tableType = DataTableDataType.NUMERIC;
			else if (tableType.equals(DataTableDataType.NOMINAL))
				tableType = DataTableDataType.HYBRID;
		}
		else {
			if (tableType == null)
				tableType = DataTableDataType.NOMINAL;
			else if (tableType.equals(DataTableDataType.NUMERIC))
				tableType = DataTableDataType.HYBRID;
		}

		hashColumns.put(column.getID(), column);
		defaultColumnIDs.add(column.getID());

	}

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see {@link ExternalDataRepresentation}
	 * @param isTableHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all dimensions in the set contain equal data. If false,
	 *            each dimension is treated separately, has it's own min and max etc. Sets that contain
	 *            nominal data MUST be inhomogeneous.
	 */
	void setExternalDataRepresentation(ExternalDataRepresentation externalDataRep, boolean isTableHomogeneous) {
		this.isTableHomogeneous = isTableHomogeneous;
		if (externalDataRep == this.externalDataRep)
			return;

		this.externalDataRep = externalDataRep;

		for (AColumn dimension : hashColumns.values()) {
			if (dimension instanceof NumericalColumn) {
				((NumericalColumn) dimension).setExternalDataRepresentation(externalDataRep);
			}
		}

		if (isTableHomogeneous) {
			switch (externalDataRep) {
				case NORMAL:
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
		}
		else {
			switch (externalDataRep) {
				case NORMAL:
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

	public void createFoldChangeRepresentation() {
		normalization.normalizeUsingFoldChange();
	}

	public boolean containsFoldChangeRepresentation() {
		for (AColumn dimension : hashColumns.values()) {
			return dimension.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW);
		}
		return false;
	}

	// ---------------------- helper functions ------------------------------

	void createDefaultRecordPerspective() {
		RecordPerspective recordPerspective = new RecordPerspective(dataDomain);

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		if (isColumnDimension)
			data.setData(getRowIDList());
		else
			data.setData(getColumnIDList());
		recordPerspective.init(data);

		hashRecordPerspectives.put(recordPerspective.getPerspectiveID(), recordPerspective);
	}

	void createDefaultDimensionPerspective() {

		DimensionPerspective dimensionPerspective = new DimensionPerspective(dataDomain);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		if (isColumnDimension)
			data.setData(getColumnIDList());
		else
			data.setData(getRowIDList());
		dimensionPerspective.init(data);

		hashDimensionPerspectives.put(dimensionPerspective.getPerspectiveID(), dimensionPerspective);
	}
}
