package org.caleydo.core.data.collection.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.NominalDimension;
import org.caleydo.core.data.collection.dimension.NumericalDimension;
import org.caleydo.core.data.collection.dimension.RawDataType;
import org.caleydo.core.data.collection.table.statistics.StatisticsResult;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.dimension.DimensionManager;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
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

	protected HashMap<Integer, ADimension> hashDimensions;

	/** List of dimension IDs in the order as they have been added */
	protected ArrayList<Integer> defaultDimensionIDs;
	protected HashMap<String, RecordPerspective> hashRecordPerspectives;
	protected HashMap<String, DimensionPerspective> hashDimensionPerspectives;

	protected NumericalDimension meanDimension;

	// protected DimensionData defaultDimensionData;
	// protected RecordPerspective defaultRecordPerspective;

	protected ExternalDataRepresentation externalDataRep;

	protected boolean isDataTableHomogeneous = false;

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

	/**
	 * Constructor for the table. Creates and initializes members and registers the set whit the set manager.
	 * Also creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public DataTable(ATableBasedDataDomain dataDomain) {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.DATA_TABLE));
		this.dataDomain = dataDomain;
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
		hashDimensions = new HashMap<Integer, ADimension>();
		hashRecordPerspectives = new HashMap<String, RecordPerspective>(6);
		hashDimensionPerspectives = new HashMap<String, DimensionPerspective>(3);
		defaultDimensionIDs = new ArrayList<Integer>();
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
		return hashDimensions.get(dimensionID).getFloat(dataRepresentation, recordID);
	}

	public <RawType> RawType getRaw(Integer dimensionID, Integer recordID) {
		ADimension dimension = hashDimensions.get(dimensionID);
		return ((NominalDimension<RawType>) dimension).getRaw(recordID);
	}

	public String getRawAsString(Integer dimensionID, Integer recordID) {
		RawDataType rawDataType = getRawDataType(dimensionID, recordID);
		String result;
		if (rawDataType == RawDataType.FLOAT) {
			result = Float.toString(getFloat(DataRepresentation.RAW, dimensionID, recordID));
		}
		else if (rawDataType == RawDataType.STRING) {
			result = getRaw(dimensionID, recordID);
		}
		else {
			throw new IllegalStateException("DataType " + rawDataType + " not implemented");

		}
		return result;
	}

	public boolean containsDataRepresentation(DataRepresentation dataRepresentation, Integer dimensionID) {
		return hashDimensions.get(dimensionID).containsDataRepresentation(dataRepresentation);
	}

	public RawDataType getRawDataType(Integer dimensionID, Integer recordID) {
		return hashDimensions.get(dimensionID).getRawDataType();
	}

	/**
	 * Iterate over the dimensions based on a virtual array
	 * 
	 * @param type
	 * @return
	 */
	public Iterator<ADimension> iterator(String type) {
		return new DimensionIterator(hashDimensions, hashDimensionPerspectives.get(type).getVirtualArray());
	}

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 * 
	 * @param dNormalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(double dNormalized) {
		if (!isDataTableHomogeneous)
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
		if (!isDataTableHomogeneous)
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
	 * Get a copy of the original dimension VA (i.e., the va containing all dimensions in the order loaded
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DimensionVirtualArray getBaseDimensionVA(String vaType) {
		return new DimensionVirtualArray(vaType, (ArrayList<Integer>) defaultDimensionIDs.clone());
	}

	/**
	 * Get a copy of the original content VA (i.e., the one equal to the actual content of the dimensions)
	 * 
	 * @return
	 */
	public RecordVirtualArray getBaseRecordVA(String vaType) {
		return createBaseRecordVA(vaType);
	}

	/**
	 * Returns the current external data rep.
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
		return isDataTableHomogeneous;
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
		DimensionManager sm = gm.getDimensionManager();
		for (Integer dimensionID : hashDimensions.keySet()) {
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
		return "Set for " + dataDomain + " with " + hashDimensions.size() + " dimensions.";
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
	public NumericalDimension getMeanDimension(String dimensionPerspectiveID) {
		if (!tableType.equals(DataTableDataType.NUMERIC) || !isDataTableHomogeneous)
			throw new IllegalStateException(
				"Can not provide a mean dimension if set is not numerical (Set type: " + tableType
					+ ") or not homgeneous (isHomogeneous: " + isDataTableHomogeneous + ")");
		if (meanDimension == null) {
			meanDimension = new NumericalDimension();
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
	 * Add a dimension based on its id. The dimension has to be fully initialized with data
	 * 
	 * @param dimensionID
	 */
	void addDimension(int dimensionID) {
		DimensionManager dimensionManager = GeneralManager.get().getDimensionManager();

		if (!dimensionManager.hasItem(dimensionID))
			throw new IllegalArgumentException("Requested Dimension with ID " + dimensionID
				+ " does not exist.");

		ADimension dimension = dimensionManager.getItem(dimensionID);
		addDimension(dimension);
	}

	/**
	 * Add a dimension by reference. The dimension has to be fully initialized with data
	 * 
	 * @param dimension
	 *            the dimension
	 */
	void addDimension(ADimension dimension) {
		// if (hashDimensions.isEmpty()) {
		if (dimension instanceof NumericalDimension) {
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

		hashDimensions.put(dimension.getID(), dimension);
		defaultDimensionIDs.add(dimension.getID());

	}

	void finalizeAddedDimensions() {

		DimensionPerspective dimensionData = new DimensionPerspective(dataDomain);
		dimensionData.createVA(defaultDimensionIDs);

		// createSubDataTable(dimensionData);

		hashDimensionPerspectives.put(dimensionData.getPerspectiveID(), dimensionData);

	}

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see {@link ExternalDataRepresentation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all dimensions in the set contain equal data. If false,
	 *            each dimension is treated separately, has it's own min and max etc. Sets that contain
	 *            nominal data MUST be inhomogeneous.
	 */
	void setExternalDataRepresentation(ExternalDataRepresentation externalDataRep, boolean bIsSetHomogeneous) {
		this.isDataTableHomogeneous = bIsSetHomogeneous;
		if (externalDataRep == this.externalDataRep)
			return;

		this.externalDataRep = externalDataRep;

		for (ADimension dimension : hashDimensions.values()) {
			if (dimension instanceof NumericalDimension) {
				((NumericalDimension) dimension).setExternalDataRepresentation(externalDataRep);
			}
		}

		if (bIsSetHomogeneous) {
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
		for (ADimension dimension : hashDimensions.values()) {
			return dimension.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW);
		}
		return false;
	}

	// ---------------------- helper functions ------------------------------

	private RecordPerspective createRecordPerspective(boolean initializeEmpty) {
		RecordPerspective recordData = new RecordPerspective(dataDomain);

		RecordVirtualArray recordVA;
		if (initializeEmpty == true) {
			recordVA = new RecordVirtualArray(recordData.getPerspectiveID());
		}
		else {
			recordVA = createBaseRecordVA(recordData.getPerspectiveID());

		}
		recordData.setVirtualArray(recordVA);
		return recordData;

	}

	void createDefaultRecordPerspective() {
		RecordPerspective recordData = createRecordPerspective(false);
		hashRecordPerspectives.put(recordData.getPerspectiveID(), recordData);
	}

	private RecordVirtualArray createBaseRecordVA(String recordPerspectiveID) {
		RecordVirtualArray recordVA = new RecordVirtualArray(recordPerspectiveID);
		for (int count = 0; count < metaData.depth(); count++) {
			recordVA.append(count);
		}
		return recordVA;
	}
}
