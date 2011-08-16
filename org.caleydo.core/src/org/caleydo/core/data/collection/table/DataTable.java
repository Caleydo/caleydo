package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.ICollection;
import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.NumericalDimension;
import org.caleydo.core.data.collection.table.statistics.StatisticsResult;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.dimension.DimensionManager;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <h2>General Information</h2>
 * <p>
 * A set is the main container for tabular data in Caleydo. A set is made up of {@link IDimension}s, where each
 * dimension corresponds to a column in a tabular data table. Columns are therefore always referred to as
 * <b>Dimensions</b> and rows as <b>Record</b> The data should be accessed through {@link VirtualArray}s, which
 * are stored in {@link DimensionData}s for Dimensions and {@link RecordData}s for Record.
 * </p>
 * <h2>DataTable Creation</h2>
 * <p>
 * A data table relies heavily upon {@link DataTableUtils} for being created. Many creation related functions are
 * provided there, sometimes interfacing with package private methods in this class.
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataTable
	extends AUniqueObject
	implements ICollection {

	public static final String DIMENSION = "Dimension";
	public static final String RECORD = "Record";
	public static final String RECORD_CONTEXT = "Record_Context";

	protected HashMap<Integer, ADimension> hashDimensions;

	private String sLabel = "Rootset";

	protected HashMap<String, RecordData> hashRecordData;
	protected HashMap<String, DimensionData> hashDimensionData;

	protected NumericalDimension meanDimension;

	protected DimensionData defaultDimensionData;
	protected RecordData defaultRecordData;

	protected ExternalDataRepresentation externalDataRep;

	protected boolean isSetHomogeneous = false;

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
	
	public DataTable() {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.DATA_TABLE));
	}

	/**
	 * Constructor for the table. Creates and initializes members and registers the set whit the set manager.
	 * Also creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public DataTable(ATableBasedDataDomain dataDomain) {
		this();
		this.dataDomain = dataDomain;
		initWithDataDomain();
	}

	private void initWithDataDomain() {
		init();
		ClusterTree tree = new ClusterTree(dataDomain.getDimensionIDType());
		ClusterNode root = new ClusterNode(tree, "Root", 1, true, -1);
		tree.setRootNode(root);
		defaultDimensionData.setDimensionTree(tree);
		dataDomain.createDimensionGroupsFromDimensionTree(tree);
		// hashDimensionData.put(DimensionVAType.STORAGE, defaultDimensionData.clone());
	}

	/**
	 * Initialization of member variables. Safe to be called by sub-classes.
	 */
	protected void init() {

		hashDimensions = new HashMap<Integer, ADimension>();
		hashRecordData = new HashMap<String, RecordData>(6);
		hashDimensionData = new HashMap<String, DimensionData>(3);
		defaultDimensionData = new DimensionData();
		defaultDimensionData.setDimensionVA(new DimensionVirtualArray(DIMENSION));
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
	 * Creates a {@link SubDataTable} for every node in the dimension tree.
	 */
	public void createSubDataTable() {
		// ClusterNode rootNode = hashDimensionData.get(STORAGE).getDimensionTreeRoot();
		// rootNode.createSubDataTables(this);
		defaultDimensionData.getDimensionTree().createSubDataTables(this);
	}

	/**
	 * Set the data domain that is responsible for the set
	 * 
	 * @param dataDomain
	 */
	@XmlTransient
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		initWithDataDomain();
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
	public ADimension get(Integer dimensionID) {
		return hashDimensions.get(dimensionID);
	}

	@Override
	public void setLabel(String sLabel) {
		this.sLabel = sLabel;
	}

	@Override
	public String getLabel() {
		return sLabel;
	}

	/**
	 * Iterate over the dimensions based on a virtual array
	 * 
	 * @param type
	 * @return
	 */
	public Iterator<ADimension> iterator(String type) {
		return new DimensionIterator(hashDimensions, hashDimensionData.get(type).getDimensionVA());
	}

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 * 
	 * @param dNormalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(double dNormalized) {
		if (!isSetHomogeneous)
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
		if (!isSetHomogeneous)
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
	 * Restores the original virtual array using the whole set data.
	 */
	public void restoreOriginalRecordVA() {
		RecordData recordData = createRecordData(RECORD);
		hashRecordData.put(RECORD, recordData);
	}

	/**
	 * Get a copy of the original dimension VA (i.e., the va containing all dimensions in the order loaded
	 * 
	 * @return
	 */
	public DimensionVirtualArray getBaseDimensionVA() {
		return defaultDimensionData.getDimensionVA().clone();
	}

	/**
	 * Get a copy of the original content VA (i.e., the one equal to the actual content of the dimensions)
	 * 
	 * @return
	 */
	public RecordVirtualArray getBaseRecordVA() {
		if (defaultRecordData == null)
			defaultRecordData = createRecordData(RECORD);
		return defaultRecordData.getRecordVA().clone();
	}

	/**
	 * Set a recordVA. The recordVA in the recordData object is replaced and the other elements in the
	 * recordData are retable.
	 * 
	 * @param vaType
	 * @param virtualArray
	 */
	public void setRecordVA(String vaType, RecordVirtualArray virtualArray) {
		RecordData recordData = hashRecordData.get(vaType);
		if (recordData == null)
			recordData = createRecordData(vaType);
		else
			recordData.reset();
		recordData.setRecordVA(virtualArray);
		// FIXME - this happens when we filter genes based on pathway occurrences. However, we should consider
		// this as a filter instead of the new default
		// if (vaType == CONTENT)
		// defaultContentData = recordData;
		hashRecordData.put(vaType, recordData);
	}

	/**
	 * Sets a dimensionVA. The dimensionVA in the dimensionData object is replaced and the other elements in the
	 * dimensionData are retable.
	 * 
	 * @param vaType
	 * @param virtualArray
	 */
	public void setDimensionVA(String vaType, DimensionVirtualArray virtualArray) {
		DimensionData dimensionData = hashDimensionData.get(vaType);
		if (dimensionData == null)
			dimensionData = defaultDimensionData.clone();
		// else
		// dimensionData.reset();
		dimensionData.setDimensionVA(virtualArray);
		hashDimensionData.put(vaType, dimensionData);
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
		return isSetHomogeneous;
	}

	/**
	 * Clusters a Dimension
	 * 
	 * @param clusterState
	 * @return ArrayList<IVirtualArray> Virtual arrays holding cluster result
	 */
	public void cluster(ClusterState clusterState) {

		// if (setType.equals(ESetDataType.NUMERIC) && isSetHomogeneous == true) {

		String recordVAType = clusterState.getRecordVAType();
		if (recordVAType != null) {
			clusterState.setRecordVA(getRecordData(recordVAType).getRecordVA());
			clusterState.setRecordIDType(dataDomain.getRecordIDType());
			// this.setContentGroupList(getRecordVA(recordVAType).getGroupList());
		}

		String dimensionVAType = clusterState.getDimensionVAType();
		if (dimensionVAType != null) {
			clusterState.setDimensionVA(getDimensionData(dimensionVAType).getDimensionVA());
			clusterState.setDimensionIDType(dataDomain.getDimensionIDType());
			// this.setDimensionGroupList(getDimensionVA(dimensionVAType).getGroupList());
		}

		ClusterManager clusterManager = new ClusterManager(this);
		ClusterResult result = clusterManager.cluster(clusterState);

		if (result != null) {
			RecordData recordResult = result.getRecordResult();
			if (recordResult != null) {
				hashRecordData.put(clusterState.getRecordVAType(), recordResult);
			}
			DimensionData dimensionResult = result.getDimensionResult();
			if (dimensionResult != null) {
				hashDimensionData.put(clusterState.getDimensionVAType(), dimensionResult);
			}
			// }
			// else
			// throw new IllegalStateException("Cannot cluster a non-numerical or non-homogeneous Set");
		}
	}

	/**
	 * Returns a {@link RecordData} object for the specified RecordVAType. The ContentData provides access
	 * to all data on a dimension, e.g., virtualArryay, cluster tree, group list etc.
	 * 
	 * @param vaType
	 * @return
	 */
	public RecordData getRecordData(String vaType) {
		RecordData recordData = hashRecordData.get(vaType);
		if (recordData == null) {
			recordData = createRecordData(vaType);
			hashRecordData.put(vaType, recordData);
		}
		return recordData;
	}

	/**
	 * Returns a {@link DimensionData} object for the specified DimensionVAType. The DimensionData provides access
	 * to all data on a dimension, e.g., virtualArryay, cluster tree, group list etc.
	 * 
	 * @param vaType
	 * @return
	 */
	public DimensionData getDimensionData(String vaType) {
		return hashDimensionData.get(vaType);
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
		return "Set " + getLabel() + " with " + hashDimensions.size() + " dimensions.";
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
	 * Returns a dimension containing the mean values of all the dimensions in the table. The mean dimension contains
	 * raw and normalized values. The mean is calculated based on the raw data, that means for calculating the
	 * means possibly specified cut-off values are not considered, since cut-off values are meant for
	 * visualization only.
	 * 
	 * @return the dimension containing means for all content elements
	 */
	public NumericalDimension getMeanDimension() {
		if (!tableType.equals(DataTableDataType.NUMERIC) || !isSetHomogeneous)
			throw new IllegalStateException(
				"Can not provide a mean dimension if set is not numerical (Set type: " + tableType
					+ ") or not homgeneous (isHomogeneous: " + isSetHomogeneous + ")");
		if (meanDimension == null) {
			meanDimension = new NumericalDimension();
			meanDimension.setExternalDataRepresentation(ExternalDataRepresentation.NORMAL);

			float[] meanValues = new float[metaData.depth()];
			DimensionVirtualArray dimensionVA = defaultDimensionData.getDimensionVA();
			for (int contentCount = 0; contentCount < metaData.depth(); contentCount++) {
				float sum = 0;
				for (int dimensionID : dimensionVA) {
					sum += get(dimensionID).getFloat(DataRepresentation.RAW, contentCount);
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
	void addDimension(int iDimensionID) {
		DimensionManager dimensionManager = GeneralManager.get().getDimensionManager();

		if (!dimensionManager.hasItem(iDimensionID))
			throw new IllegalArgumentException("Requested Dimension with ID " + iDimensionID + " does not exist.");

		ADimension dimension = dimensionManager.getItem(iDimensionID);
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

		// rawDataType = dimension.getRawDataType();
		// iDepth = dimension.size();
		// }
		// else {
		// if (!bIsNumerical && dimension instanceof INumericalDimension)
		// throw new IllegalArgumentException(
		// "All dimensions in a set must be of the same basic type (nunmerical or nominal)");
		// if (rawDataType != dimension.getRawDataType())
		// throw new IllegalArgumentException("All dimensions in a set must have the same raw data type");
		// // if (iDepth != dimension.size())
		// // throw new IllegalArgumentException("All dimensions in a set must be of the same length");
		// }
		hashDimensions.put(dimension.getID(), dimension);
		defaultDimensionData.getDimensionVA().append(dimension.getID());

	}

	void finalizeAddedDimensions() {
		
		// this needs only be done by the root set
		if ((this.getClass().equals(DataTable.class))) {
			ClusterTree tree = defaultDimensionData.getDimensionTree();
			int count = 1;
			for (Integer dimensionID : defaultDimensionData.getDimensionVA()) {
				ClusterNode node =
					new ClusterNode(tree, get(dimensionID).getLabel(), count++, false, dimensionID);
				tree.addChild(tree.getRoot(), node);
			}

			createSubDataTable();

		}
		hashDimensionData.put(DIMENSION, defaultDimensionData.clone());
	}

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see {@link ExternalDataRepresentation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all dimensions in the set contain equal data. If false, each
	 *            dimension is treated separately, has it's own min and max etc. Sets that contain nominal data
	 *            MUST be inhomogeneous.
	 */
	void setExternalDataRepresentation(ExternalDataRepresentation externalDataRep, boolean bIsSetHomogeneous) {
		this.isSetHomogeneous = bIsSetHomogeneous;
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

	private RecordData createRecordData(String vaType) {
		RecordData recordData = new RecordData(dataDomain.getRecordIDType());

		RecordVirtualArray recordVA;
		if (vaType != RECORD_CONTEXT) {
			recordVA = createBaseRecordVA(vaType);
		}
		else {
			recordVA = new RecordVirtualArray(vaType);
		}
		recordData.setRecordVA(recordVA);
		return recordData;

	}

	private RecordVirtualArray createBaseRecordVA(String vaType) {
		RecordVirtualArray recordVA = new RecordVirtualArray(vaType);
		for (int count = 0; count < metaData.depth(); count++) {
			recordVA.append(count);
		}
		return recordVA;
	}

	/**
	 * Return a list of content VA types that have registered {@link RecordData}.
	 * 
	 * @return
	 */
	public java.util.Set<String> getRegisteredRecordVATypes() {
		return hashRecordData.keySet();
	}

	/**
	 * Return a list of dimension VA types that have registered {@link DimensionData}
	 * 
	 * @return
	 */
	public Set<String> getRegisteredDimensionVATypes() {
		return hashDimensionData.keySet();
	}
}
