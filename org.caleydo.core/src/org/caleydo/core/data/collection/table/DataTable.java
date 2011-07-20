package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.ICollection;
import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.collection.table.statistics.StatisticsResult;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.id.EManagedObjectType;
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
 * A set is the main container for tabular data in Caleydo. A set is made up of {@link IStorage}s, where each
 * storage corresponds to a column in a tabular data set. Columns are therefore always refered to as
 * <b>Storages</b> and rows as <b>Content</b> The data should be accessed through {@link VirtualArray}s, which
 * are stored in {@link StorageData}s for Storages and {@link ContentData}s for Content.
 * </p>
 * <h2>Set Creation</h2>
 * <p>
 * A set relies heavily upon {@link DataTableUtils} for being created. Many creation related functions are
 * provided there, sometimes interfacing with package private methods in this class.
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataTable
	extends AUniqueObject
	implements ICollection {

	public static final String CONTENT = "Content";
	public static final String STORAGE = "Storage";
	public static final String CONTENT_CONTEXT = "Content_Context";

	protected HashMap<Integer, AStorage> hashStorages;

	private String sLabel = "Rootset";

	protected HashMap<String, ContentData> hashContentData;
	protected HashMap<String, StorageData> hashStorageData;

	protected NumericalStorage meanStorage;

	protected StorageData defaultStorageData;
	protected ContentData defaultContentData;

	protected EExternalDataRepresentation externalDataRep;

	protected boolean isSetHomogeneous = false;

	protected StatisticsResult statisticsResult;

	protected EDataTableDataType dataTableType = EDataTableDataType.NUMERIC;

	ATableBasedDataDomain dataDomain;

	boolean containsUncertaintyData = false;

	/** all metaData for this DataTable is held in or accessible through this object */
	private MetaData metaData;
	/** everything related to uncertainty is held in or accessible through this object */
	private Uncertainty uncertainty;
	/** everything related to normalization of the data is held in or accessible through this object */
	private Normalization normalization;

	public DataTable() {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.SET));
	}

	/**
	 * Constructor for the set. Creates and initializes members and registers the set whit the set manager.
	 * Also creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public DataTable(ATableBasedDataDomain dataDomain) {
		this();
		this.dataDomain = dataDomain;
		initWithDataDomain();
	}

	private void initWithDataDomain() {
		SetManager.getInstance().registerItem(this);
		init();
		ClusterTree tree = new ClusterTree(dataDomain.getStorageIDType());
		ClusterNode root = new ClusterNode(tree, "Root", 1, true, -1);
		tree.setRootNode(root);
		defaultStorageData.setStorageTree(tree);
		dataDomain.createDimensionGroupsFromStorageTree(tree);
		// hashStorageData.put(StorageVAType.STORAGE, defaultStorageData.clone());
	}

	/**
	 * Initialization of member variables. Safe to be called by sub-classes.
	 */
	protected void init() {

		hashStorages = new HashMap<Integer, AStorage>();
		hashContentData = new HashMap<String, ContentData>(6);
		hashStorageData = new HashMap<String, StorageData>(3);
		defaultStorageData = new StorageData();
		defaultStorageData.setStorageVA(new StorageVirtualArray(STORAGE));
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

	public EDataTableDataType getSetType() {
		return dataTableType;
	}

	/**
	 * Creates a {@link SubDataTable} for every node in the storage tree.
	 */
	public void createMetaSets() {
		// ClusterNode rootNode = hashStorageData.get(STORAGE).getStorageTreeRoot();
		// rootNode.createMetaSets(this);
		defaultStorageData.getStorageTree().createMetaSets(this);
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
	 * Get the storage associated with the ID provided. Returns null if no such storage is registered.
	 * 
	 * @param storageID
	 *            a unique storage ID
	 * @return
	 */
	public AStorage get(Integer storageID) {
		return hashStorages.get(storageID);
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
	 * Iterate over the storages based on a virtual array
	 * 
	 * @param type
	 * @return
	 */
	public Iterator<AStorage> iterator(String type) {
		return new StorageIterator(hashStorages, hashStorageData.get(type).getStorageVA());
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
				"Can not produce raw data on set level for inhomogenous sets. Access via storages");

		double result;

		if (dNormalized == 0)
			result = metaData.getMin();
		// if(getMin() > 0)
		result = metaData.getMin() + dNormalized * (metaData.getMax() - metaData.getMin());
		// return (dNormalized) * (getMax() + getMin());
		if (externalDataRep == EExternalDataRepresentation.NORMAL) {
			return result;
		}
		else if (externalDataRep == EExternalDataRepresentation.LOG2) {
			return Math.pow(2, result);
		}
		else if (externalDataRep == EExternalDataRepresentation.LOG10) {
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
				"Can not produce normalized data on set level for inhomogenous sets. Access via storages");

		double result;

		if (externalDataRep == EExternalDataRepresentation.NORMAL) {
			result = dRaw;
		}
		else if (externalDataRep == EExternalDataRepresentation.LOG2) {
			result = Math.log(dRaw) / Math.log(2);
		}
		else if (externalDataRep == EExternalDataRepresentation.LOG10) {
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
	public void restoreOriginalContentVA() {
		ContentData contentData = createContentData(CONTENT);
		hashContentData.put(CONTENT, contentData);
	}

	/**
	 * Get a copy of the original storage VA (i.e., the va containing all storages in the order loaded
	 * 
	 * @return
	 */
	public StorageVirtualArray getBaseStorageVA() {
		return defaultStorageData.getStorageVA().clone();
	}

	/**
	 * Get a copy of the original content VA (i.e., the one equal to the actual content of the storages)
	 * 
	 * @return
	 */
	public ContentVirtualArray getBaseContentVA() {
		if (defaultContentData == null)
			defaultContentData = createContentData(CONTENT);
		return defaultContentData.getContentVA().clone();
	}

	/**
	 * Set a contentVA. The contentVA in the contentData object is replaced and the other elements in the
	 * contentData are reset.
	 * 
	 * @param vaType
	 * @param virtualArray
	 */
	public void setContentVA(String vaType, ContentVirtualArray virtualArray) {
		ContentData contentData = hashContentData.get(vaType);
		if (contentData == null)
			contentData = createContentData(vaType);
		else
			contentData.reset();
		contentData.setContentVA(virtualArray);
		// FIXME - this happens when we filter genes based on pathway occurrences. However, we should consider
		// this as a filter instead of the new default
		// if (vaType == CONTENT)
		// defaultContentData = contentData;
		hashContentData.put(vaType, contentData);
	}

	/**
	 * Sets a storageVA. The storageVA in the storageData object is replaced and the other elements in the
	 * storageData are reset.
	 * 
	 * @param vaType
	 * @param virtualArray
	 */
	public void setStorageVA(String vaType, StorageVirtualArray virtualArray) {
		StorageData storageData = hashStorageData.get(vaType);
		if (storageData == null)
			storageData = defaultStorageData.clone();
		// else
		// storageData.reset();
		storageData.setStorageVA(virtualArray);
		hashStorageData.put(vaType, storageData);
	}

	/**
	 * Returns the current external data rep.
	 * 
	 * @return
	 */
	public EExternalDataRepresentation getExternalDataRep() {
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
	 * Clusters a Storage
	 * 
	 * @param clusterState
	 * @return ArrayList<IVirtualArray> Virtual arrays holding cluster result
	 */
	public void cluster(ClusterState clusterState) {

		// if (setType.equals(ESetDataType.NUMERIC) && isSetHomogeneous == true) {

		String contentVAType = clusterState.getContentVAType();
		if (contentVAType != null) {
			clusterState.setContentVA(getContentData(contentVAType).getContentVA());
			clusterState.setContentIDType(dataDomain.getContentIDType());
			// this.setContentGroupList(getContentVA(contentVAType).getGroupList());
		}

		String storageVAType = clusterState.getStorageVAType();
		if (storageVAType != null) {
			clusterState.setStorageVA(getStorageData(storageVAType).getStorageVA());
			clusterState.setStorageIDType(dataDomain.getStorageIDType());
			// this.setStorageGroupList(getStorageVA(storageVAType).getGroupList());
		}

		ClusterManager clusterManager = new ClusterManager(this);
		ClusterResult result = clusterManager.cluster(clusterState);

		ContentData contentResult = result.getContentResult();
		if (contentResult != null) {
			hashContentData.put(clusterState.getContentVAType(), contentResult);
		}
		StorageData storageResult = result.getStorageResult();
		if (storageResult != null) {
			hashStorageData.put(clusterState.getStorageVAType(), storageResult);
		}
		// }
		// else
		// throw new IllegalStateException("Cannot cluster a non-numerical or non-homogeneous Set");

	}

	/**
	 * Returns a {@link ContentData} object for the specified ContentVAType. The ContentData provides access
	 * to all data on a storage, e.g., virtualArryay, cluster tree, group list etc.
	 * 
	 * @param vaType
	 * @return
	 */
	public ContentData getContentData(String vaType) {
		ContentData contentData = hashContentData.get(vaType);
		if (contentData == null) {
			contentData = createContentData(vaType);
			hashContentData.put(vaType, contentData);
		}
		return contentData;
	}

	/**
	 * Returns a {@link StorageData} object for the specified StorageVAType. The StorageData provides access
	 * to all data on a storage, e.g., virtualArryay, cluster tree, group list etc.
	 * 
	 * @param vaType
	 * @return
	 */
	public StorageData getStorageData(String vaType) {
		return hashStorageData.get(vaType);
	}

	/**
	 * Removes all data related to the set (Storages, Virtual Arrays and Sets) from the managers so that the
	 * garbage collector can handle it.
	 */
	public void destroy() {
		GeneralManager gm = GeneralManager.get();
		StorageManager sm = gm.getStorageManager();
		for (Integer storageID : hashStorages.keySet()) {
			sm.unregisterItem(storageID);
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
		return "Set " + getLabel() + " with " + hashStorages.size() + " storages.";
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
	 * Returns a storage containing the mean values of all the storages in the set. The mean storage contains
	 * raw and normalized values. The mean is calculated based on the raw data, that means for calculating the
	 * means possibly specified cut-off values are not considered, since cut-off values are meant for
	 * visualization only.
	 * 
	 * @return the storage containing means for all content elements
	 */
	public NumericalStorage getMeanStorage() {
		if (!dataTableType.equals(EDataTableDataType.NUMERIC) || !isSetHomogeneous)
			throw new IllegalStateException(
				"Can not provide a mean storage if set is not numerical (Set type: " + dataTableType
					+ ") or not homgeneous (isHomogeneous: " + isSetHomogeneous + ")");
		if (meanStorage == null) {
			meanStorage = new NumericalStorage();
			meanStorage.setExternalDataRepresentation(EExternalDataRepresentation.NORMAL);

			float[] meanValues = new float[metaData.depth()];
			StorageVirtualArray storageVA = defaultStorageData.getStorageVA();
			for (int contentCount = 0; contentCount < metaData.depth(); contentCount++) {
				float sum = 0;
				for (int storageID : storageVA) {
					sum += get(storageID).getFloat(EDataRepresentation.RAW, contentCount);
				}
				meanValues[contentCount] = sum / metaData.size();
			}
			meanStorage.setRawData(meanValues);
			// meanStorage.normalize();
		}
		return meanStorage;
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
	// set.

	/**
	 * Add a storage based on its id. The storage has to be fully initialized with data
	 * 
	 * @param storageID
	 */
	void addStorage(int iStorageID) {
		StorageManager storageManager = GeneralManager.get().getStorageManager();

		if (!storageManager.hasItem(iStorageID))
			throw new IllegalArgumentException("Requested Storage with ID " + iStorageID + " does not exist.");

		AStorage storage = storageManager.getItem(iStorageID);
		addStorage(storage);
	}

	/**
	 * Add a storage by reference. The storage has to be fully initialized with data
	 * 
	 * @param storage
	 *            the storage
	 */
	void addStorage(AStorage storage) {
		// if (hashStorages.isEmpty()) {
		if (storage instanceof NumericalStorage) {
			if (dataTableType == null)
				dataTableType = EDataTableDataType.NUMERIC;
			else if (dataTableType.equals(EDataTableDataType.NOMINAL))
				dataTableType = EDataTableDataType.HYBRID;
		}
		else {
			if (dataTableType == null)
				dataTableType = EDataTableDataType.NOMINAL;
			else if (dataTableType.equals(EDataTableDataType.NUMERIC))
				dataTableType = EDataTableDataType.HYBRID;
		}

		// rawDataType = storage.getRawDataType();
		// iDepth = storage.size();
		// }
		// else {
		// if (!bIsNumerical && storage instanceof INumericalStorage)
		// throw new IllegalArgumentException(
		// "All storages in a set must be of the same basic type (nunmerical or nominal)");
		// if (rawDataType != storage.getRawDataType())
		// throw new IllegalArgumentException("All storages in a set must have the same raw data type");
		// // if (iDepth != storage.size())
		// // throw new IllegalArgumentException("All storages in a set must be of the same length");
		// }
		hashStorages.put(storage.getID(), storage);
		defaultStorageData.getStorageVA().append(storage.getID());

	}

	void finalizeAddedStorages() {
		// this needs only be done by the root set
		if ((this.getClass().equals(DataTable.class))) {
			ClusterTree tree = defaultStorageData.getStorageTree();
			int count = 1;
			for (Integer storageID : defaultStorageData.getStorageVA()) {
				ClusterNode node =
					new ClusterNode(tree, get(storageID).getLabel(), count++, false, storageID);
				tree.addChild(tree.getRoot(), node);
			}

			createMetaSets();

		}
		hashStorageData.put(STORAGE, defaultStorageData.clone());

	}

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see {@link EExternalDataRepresentation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all storages in the set contain equal data. If false, each
	 *            storage is treated separately, has it's own min and max etc. Sets that contain nominal data
	 *            MUST be inhomogeneous.
	 */
	void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep, boolean bIsSetHomogeneous) {
		this.isSetHomogeneous = bIsSetHomogeneous;
		if (externalDataRep == this.externalDataRep)
			return;

		this.externalDataRep = externalDataRep;

		for (AStorage storage : hashStorages.values()) {
			if (storage instanceof NumericalStorage) {
				((NumericalStorage) storage).setExternalDataRepresentation(externalDataRep);
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

	// ---------------------- helper functions ------------------------------

	private ContentData createContentData(String vaType) {
		ContentData contentData = new ContentData(dataDomain.getContentIDType());

		ContentVirtualArray contentVA;
		if (vaType != CONTENT_CONTEXT) {
			contentVA = createBaseContentVA(vaType);
		}
		else {
			contentVA = new ContentVirtualArray(vaType);
		}
		contentData.setContentVA(contentVA);
		return contentData;

	}

	private ContentVirtualArray createBaseContentVA(String vaType) {
		ContentVirtualArray contentVA = new ContentVirtualArray(vaType);
		for (int count = 0; count < metaData.depth(); count++) {
			contentVA.append(count);
		}
		return contentVA;
	}

	/**
	 * Return a list of content VA types that have registered {@link ContentData}.
	 * 
	 * @return
	 */
	public java.util.Set<String> getRegisteredContentVATypes() {
		return hashContentData.keySet();
	}

	/**
	 * Return a list of storage VA types that have registered {@link StorageData}
	 * 
	 * @return
	 */
	public Set<String> getRegisteredStorageVATypes() {
		return hashStorageData.keySet();
	}
}
