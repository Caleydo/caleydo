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
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
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
 * A set relies heavily upon {@link DataTableUtils} for being created. Many creation related functions are provided
 * there, sometimes interfacing with package private methods in this class.
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataTable
	extends AUniqueObject implements ICollection {

	public static final String CONTENT = "Content";
	public static final String STORAGE = "Storage";
	public static final String CONTENT_CONTEXT = "Content_Context";
	
	
	protected HashMap<Integer, AStorage> hashStorages;

	private String sLabel = "Rootset";

	private boolean bArtificialMin = false;
	private double dMin = Double.MAX_VALUE;

	private boolean bArtificialMax = false;
	private double dMax = Double.MIN_VALUE;

	protected int depth = 0;

	// private ERawDataType rawDataType;

	// private boolean bIsNumerical;

	protected HashMap<String, ContentData> hashContentData;
	protected HashMap<String, StorageData> hashStorageData;

	protected NumericalStorage meanStorage;

	protected StorageData defaultStorageData;
	protected ContentData defaultContentData;

	protected EExternalDataRepresentation externalDataRep;

	protected boolean isSetHomogeneous = false;

	protected StatisticsResult statisticsResult;

	protected EDataTableDataType setType = EDataTableDataType.NUMERIC;

	ASetBasedDataDomain dataDomain;

	private boolean containsUncertaintyData = false;
	/**
	 * the uncertainties for the whole storage aggregated across the storageVA based on the normalized
	 * uncertainty values
	 */
	private float[] aggregatedNormalizedUncertainties;
	/**
	 * the uncertainties for the whole storage aggregated across the storageVA based on the raw uncertainty
	 * values
	 */
	private float[] aggregatedRawUncertainties;

	public DataTable() {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.SET));
	}

	/**
	 * Constructor for the set. Creates and initializes members and registers the set whit the set manager.
	 * Also creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public DataTable(ASetBasedDataDomain dataDomain) {
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
	}

	public EDataTableDataType getSetType() {
		return setType;
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
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		initWithDataDomain();
	}

	/**
	 * Get the data domain that is responsible for the set
	 * 
	 * @param dataDomain
	 */

	public ASetBasedDataDomain getDataDomain() {
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

	/**
	 * Get the number of storages in a set
	 * 
	 * @return
	 */
	public int size() {
		return hashStorages.size();
	}

	/**
	 * Get the depth of the set, which is the length of the storages (i.e. the number of content elements)
	 * 
	 * @return the number of elements in the storages contained in the list
	 */
	public int depth() {
		if (depth == 0) {
			for (AStorage storage : hashStorages.values()) {
				if (depth == 0)
					depth = storage.size();
				else {
					if (depth != storage.size())
						throw new IllegalArgumentException("All storages in a set must be of the same length");
				}

			}
		}
		return depth;
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
	 * Get the minimum value in the set.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMin() {
		if (dMin == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		return dMin;
	}

	/**
	 * Get the maximum value in the set.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMax() {
		if (dMax == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		return dMax;
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
			result = getMin();
		// if(getMin() > 0)
		result = getMin() + dNormalized * (getMax() - getMin());
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

		result = (result - getMin()) / (getMax() - getMin());

		return result;
	}

	/**
	 * Returns a histogram of the values of all storages in the set (not considering VAs). The number of the
	 * bins is sqrt(numberOfElements). This only works for homogeneous sets, if used on other sets an
	 * exception is thrown.
	 * 
	 * @return the Histogram of the values in the set
	 * @throws UnsupportedOperationException
	 *             when used on non-homogeneous sets
	 */
	public Histogram getHistogram() {
		if (!isSetHomogeneous) {
			throw new UnsupportedOperationException(
				"Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		}
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (AStorage storage : hashStorages.values()) {
			NumericalStorage nStorage = (NumericalStorage) storage;
			Histogram storageHistogram = nStorage.getHistogram();

			if (bIsFirstLoop) {
				bIsFirstLoop = false;
				for (int iCount = 0; iCount < storageHistogram.size(); iCount++) {
					histogram.add(0);
				}
			}
			int iCount = 0;
			for (Integer histoValue : histogram) {
				histoValue += storageHistogram.get(iCount);
				histogram.set(iCount++, histoValue);
			}
		}

		return histogram;
	}

	/**
	 * Returns a histogram of the values of all storages in the set considering the VA of the default content
	 * data. The number of the bins is sqrt(VA size). This only works for homogeneous sets, if used on other
	 * sets an exception is thrown.
	 * 
	 * @return the Histogram of the values in the set
	 * @throws UnsupportedOperationException
	 *             when used on non-homogeneous sets
	 */
	public Histogram getBaseHistogram() {
		if (!isSetHomogeneous) {
			throw new UnsupportedOperationException(
				"Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		}
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (AStorage storage : hashStorages.values()) {
			NumericalStorage nStorage = (NumericalStorage) storage;
			Histogram storageHistogram = nStorage.getHistogram(defaultContentData.getContentVA());

			if (bIsFirstLoop) {
				bIsFirstLoop = false;
				for (int iCount = 0; iCount < storageHistogram.size(); iCount++) {
					histogram.add(0);
				}
			}
			int iCount = 0;
			for (Integer histoValue : histogram) {
				histoValue += storageHistogram.get(iCount);
				histogram.set(iCount++, histoValue);
			}
		}

		return histogram;
	}

	/**
	 * Returns a histogram of the values of all storages in the set considering the specified VA. The number
	 * of the bins is sqrt(VA size). This only works for homogeneous sets, if used on other sets an exception
	 * is thrown.
	 * 
	 * @return the Histogram of the values in the set
	 * @throws UnsupportedOperationException
	 *             when used on non-homogeneous sets
	 */
	public Histogram getHistogram(ContentVirtualArray contentVA) {
		// FIXME put that back
		// if (!isSetHomogeneous) {
		// throw new UnsupportedOperationException(
		// "Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		// }
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (AStorage storage : hashStorages.values()) {
			NumericalStorage nStorage = (NumericalStorage) storage;
			Histogram storageHistogram = nStorage.getHistogram(contentVA);

			if (bIsFirstLoop) {
				bIsFirstLoop = false;
				for (int iCount = 0; iCount < storageHistogram.size(); iCount++) {
					histogram.add(0);
				}
			}
			int iCount = 0;
			for (Integer histoValue : histogram) {
				histoValue += storageHistogram.get(iCount);
				histogram.set(iCount++, histoValue);
			}
		}

		return histogram;
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

	// private int createStorageVA(IVirtualArray virtualArray) {
	// int uniqueID = virtualArray.getID();
	// hashStorageVAs.put(uniqueID, virtualArray);
	// return uniqueID;
	// }

	// public IVirtualArray createCompleteStorageVA() {
	//
	// ArrayList<Integer> storages;
	// if (storageTree == null || !storageTreeRoot.isRootNode()) {
	// storages = new ArrayList<Integer>();
	// for (int count = 0; count < hashStorages.size(); count++) {
	// storages.add(count);
	// }
	// }
	// else {
	// storages = storageTreeRoot.getLeaveIds();
	// // if (!storageTreeRoot.isRootNode()) {
	// // Collections.min(storages);
	// //
	// // // ArrayList<ClusterNode> siblings = storageTreeRoot.getParent().getChildren();
	// // // int siblingsLeavesCount = 0;
	// // // for (ClusterNode sibling : siblings) {
	// // // if (sibling == storageTreeRoot)
	// // // break;
	// // //
	// // // siblingsLeavesCount+=sibling.getNrLeaves();
	// // // }
	// // //
	// // // for (Integer storageID : storages)
	// // // storageID-=siblingsLeavesCount;
	// // }
	// }
	// VirtualArray virtualArray = new VirtualArray(VAType.STORAGE, size(), storages);
	// return virtualArray;
	// }

	// public IVirtualArray createCompleteContentVA() {
	// ArrayList<Integer> content = new ArrayList<Integer>();
	// for (int count = 0; count < hashStorages.get(0).size(); count++) {
	// content.add(count);
	// }
	// VirtualArray virtualArray = new VirtualArray(VAType.CONTENT, size(), content);
	// return virtualArray;
	// }

	// @SuppressWarnings("unused")
	// private int createStorageVA(VAType vaType, ArrayList<Integer> iAlSelections) {
	// VirtualArray virtualArray = new VirtualArray(vaType, size(), iAlSelections);
	// int uniqueID = virtualArray.getID();
	//
	// hashStorageVAs.put(uniqueID, virtualArray);
	//
	// return uniqueID;
	// }
	// @Override
	// public void resetVirtualArray(int uniqueID) {
	// if (hashStorageVAs.containsKey(uniqueID)) {
	// hashStorageVAs.get(uniqueID).reset();
	// return;
	// }
	//
	// if (hashContentVAs.containsKey(uniqueID)) {
	// hashContentVAs.get(uniqueID).reset();
	// }
	// }
	// @Override
	// public void removeVirtualArray(int uniqueID) {
	// hashStorageVAs.remove(uniqueID);
	// hashContentVAs.remove(uniqueID);
	// }
	// @Override
	// public IVirtualArray getVA(int uniqueID) {
	// if (hashStorageVAs.containsKey(uniqueID))
	// return hashStorageVAs.get(uniqueID);
	// else if (hashContentVAs.containsKey(uniqueID))
	// return hashContentVAs.get(uniqueID);
	// else
	// throw new IllegalArgumentException("No Virtual Array for the unique id: " + uniqueID);
	// }

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
	 * Gets the minimum value in the set in the specified data representation.
	 * 
	 * @param dataRepresentation
	 *            Data representation the minimum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute minimum value in the set in the specified data representation.
	 */
	public double getMinAs(EExternalDataRepresentation dataRepresentation) {
		if (dMin == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == externalDataRep)
			return dMin;
		double result = getRawFromExternalDataRep(dMin);

		return getDataRepFromRaw(result, dataRepresentation);
	}

	/**
	 * Gets the maximum value in the set in the specified data representation.
	 * 
	 * @param dataRepresentation
	 *            Data representation the maximum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute maximum value in the set in the specified data representation.
	 */
	public double getMaxAs(EExternalDataRepresentation dataRepresentation) {
		if (dMax == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == externalDataRep)
			return dMax;
		double result = getRawFromExternalDataRep(dMax);

		return getDataRepFromRaw(result, dataRepresentation);
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
		if (!setType.equals(EDataTableDataType.NUMERIC) || !isSetHomogeneous)
			throw new IllegalStateException(
				"Can not provide a mean storage if set is not numerical (Set type: " + setType
					+ ") or not homgeneous (isHomogeneous: " + isSetHomogeneous + ")");
		if (meanStorage == null) {
			meanStorage = new NumericalStorage();
			meanStorage.setExternalDataRepresentation(EExternalDataRepresentation.NORMAL);

			float[] meanValues = new float[depth()];
			StorageVirtualArray storageVA = defaultStorageData.getStorageVA();
			for (int contentCount = 0; contentCount < depth(); contentCount++) {
				float sum = 0;
				for (int storageID : storageVA) {
					sum += get(storageID).getFloat(EDataRepresentation.RAW, contentCount);
				}
				meanValues[contentCount] = sum / size();
			}
			meanStorage.setRawData(meanValues);
			// meanStorage.normalize();
		}
		return meanStorage;
	}

	public void setStatisticsResult(StatisticsResult statisticsResult) {
		this.statisticsResult = statisticsResult;
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
			if (setType == null)
				setType = EDataTableDataType.NUMERIC;
			else if (setType.equals(EDataTableDataType.NOMINAL))
				setType = EDataTableDataType.HYBRID;
		}
		else {
			if (setType == null)
				setType = EDataTableDataType.NOMINAL;
			else if (setType.equals(EDataTableDataType.NUMERIC))
				setType = EDataTableDataType.HYBRID;
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
	 * Set an artificial minimum for the dataset. All elements smaller than that are clipped to this value in
	 * the representation. This only affects the normalization, does not alter the raw data
	 */
	void setMin(double dMin) {
		bArtificialMin = true;
		this.dMin = dMin;
	}

	/**
	 * Set an artificial maximum for the dataset. All elements smaller than that are clipped to this value in
	 * the representation. This only affects the normalization, does not alter the raw data
	 */
	void setMax(double dMax) {
		bArtificialMax = true;
		this.dMax = dMax;
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
					normalizeGlobally();
					break;
				case LOG10:
					log10();
					normalizeGlobally();
					break;
				case LOG2:
					log2();
					normalizeGlobally();
					break;
			}
		}
		else {
			switch (externalDataRep) {
				case NORMAL:
					normalizeLocally();
					break;
				case LOG10:
					log10();
					normalizeLocally();
					break;
				case LOG2:
					log2();
					normalizeLocally();
					break;
			}
		}
	}

	/**
	 * Calculates log10 on all storages in the set. Take care that the set contains only numerical storages,
	 * since nominal storages will cause a runtime exception. If you have mixed data you have to call log10 on
	 * all the storages that support it manually.
	 */
	void log10() {
		for (AStorage storage : hashStorages.values()) {
			if (storage instanceof NumericalStorage) {
				NumericalStorage nStorage = (NumericalStorage) storage;
				nStorage.log10();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
		}
	}

	/**
	 * Calculates log2 on all storages in the set. Take care that the set contains only numerical storages,
	 * since nominal storages will cause a runtime exception. If you have mixed data you have to call log2 on
	 * all the storages that support it manually.
	 */
	void log2() {

		for (AStorage storage : hashStorages.values()) {
			if (storage instanceof NumericalStorage) {
				NumericalStorage nStorage = (NumericalStorage) storage;
				nStorage.log2();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
		}
	}

	/**
	 * Normalize all storages in the set, based solely on the values within each storage. Operates with the
	 * raw data as basis by default, however when a logarithmized representation is in the storage this is
	 * used.
	 */
	private void normalizeLocally() {
		isSetHomogeneous = false;
		for (AStorage storage : hashStorages.values()) {
			storage.normalize();
		}
	}

	/**
	 * Normalize all storages in the set, based on values of all storages. For a numerical storage, this would
	 * mean, that global minima and maxima are retrieved instead of local ones (as is done with normalize())
	 * Operates with the raw data as basis by default, however when a logarithmized representation is in the
	 * storage this is used. Make sure that all storages are logarithmized.
	 */
	private void normalizeGlobally() {
		isSetHomogeneous = true;
		for (AStorage storage : hashStorages.values()) {
			if (storage instanceof NumericalStorage) {
				NumericalStorage nStorage = (NumericalStorage) storage;
				nStorage.normalizeWithExternalExtrema(getMin(), getMax());
			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal storages, currently not supported!");
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
		for (int count = 0; count < depth(); count++) {
			contentVA.append(count);
		}
		return contentVA;
	}

	/**
	 * Converts a raw value to the specified data representation.
	 * 
	 * @param dRaw
	 *            Raw value that shall be converted
	 * @param dataRepresentation
	 *            Data representation the raw value shall be converted to.
	 * @return Value in the specified data representation converted from the raw value.
	 */
	private double getDataRepFromRaw(double dRaw, EExternalDataRepresentation dataRepresentation) {
		switch (dataRepresentation) {
			case NORMAL:
				return dRaw;
			case LOG2:
				return Math.log(dRaw) / Math.log(2);
			case LOG10:
				return Math.log10(dRaw);
			default:
				throw new IllegalStateException("Conversion to data rep not implemented for data rep"
					+ dataRepresentation);
		}
	}

	/**
	 * Converts the specified value into raw using the current external data representation.
	 * 
	 * @param dNumber
	 *            Value in the current external data representation.
	 * @return Raw value converted from the specified value.
	 */
	private double getRawFromExternalDataRep(double dNumber) {
		switch (externalDataRep) {
			case NORMAL:
				return dNumber;
			case LOG2:
				return Math.pow(2, dNumber);
			case LOG10:
				return Math.pow(10, dNumber);
			default:
				throw new IllegalStateException("Conversion to raw not implemented for data rep"
					+ externalDataRep);
		}
	}

	private void calculateGlobalExtrema() {
		double dTemp = 1.0;

		if (setType.equals(EDataTableDataType.NUMERIC)) {
			for (AStorage storage : hashStorages.values()) {
				NumericalStorage nStorage = (NumericalStorage) storage;
				dTemp = nStorage.getMin();
				if (!bArtificialMin && dTemp < dMin) {
					dMin = dTemp;
				}
				dTemp = nStorage.getMax();
				if (!bArtificialMax && dTemp > dMax) {
					dMax = dTemp;
				}
			}
		}
		else if (hashStorages.get(0) instanceof NominalStorage<?>)
			throw new UnsupportedOperationException("No minimum or maximum can be calculated "
				+ "on nominal data");
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

	public boolean containsUncertaintyData() {
		return containsUncertaintyData;
	}

	public void setContainsUncertaintyData(boolean containsUncertaintyData) {
		this.containsUncertaintyData = containsUncertaintyData;
	}

	public float getNormalizedUncertainty(int contentIndex) {

		if (aggregatedRawUncertainties == null) {
			// calculateRawAverageUncertainty();
			// calculateNormalizedAverageUncertainty(2, 3);
			throw new IllegalStateException("Certainty has not been calculated yet.");

		}

		return aggregatedNormalizedUncertainties[contentIndex];
	}

	public float[] getNormalizedUncertainty() {

		// if (aggregatedRawUncertainties == null) {
		// calculateRawAverageUncertainty();
		// // throw new IllegalStateException("Certainty has not been calculated yet.");
		// }

		return aggregatedNormalizedUncertainties;
	}

	public float[] getRawUncertainty() {

		if (aggregatedRawUncertainties == null)
			throw new IllegalStateException("Certainty has not been calculated yet.");

		return aggregatedRawUncertainties;
	}

	public void calculateNormalizedAverageUncertainty(float invalidThreshold, float validThreshold) {

		for (AStorage storage : hashStorages.values()) {

			if (storage instanceof NumericalStorage)
				((NumericalStorage) storage).normalizeUncertainty(invalidThreshold, validThreshold);
		}

		aggregatedNormalizedUncertainties = new float[depth()];
		for (int contentIndex = 0; contentIndex < depth(); contentIndex++) {
			// float aggregatedUncertainty = calculateMaxUncertainty(contentIndex);
			float aggregatedUncertainty =
				calcualteAverageUncertainty(contentIndex, EDataRepresentation.UNCERTAINTY_NORMALIZED);
			aggregatedNormalizedUncertainties[contentIndex] = aggregatedUncertainty;
		}
	}

	public void calculateRawAverageUncertainty() {
		aggregatedRawUncertainties = new float[depth()];
		for (int contentIndex = 0; contentIndex < depth(); contentIndex++) {
			float aggregatedUncertainty;

			aggregatedUncertainty =
				calcualteAverageUncertainty(contentIndex, EDataRepresentation.UNCERTAINTY_RAW);

			// aggregatedUncertainty =
			// calculateMaxUncertainty(contentIndex, EDataRepresentation.UNCERTAINTY_RAW);

			aggregatedRawUncertainties[contentIndex] = aggregatedUncertainty;
		}
	}

	private float calcualteAverageUncertainty(int contentIndex, EDataRepresentation dataRepresentation) {
		float uncertaintySum = 0;
		StorageVirtualArray storageVA = hashStorageData.get(STORAGE).getStorageVA();
		for (Integer storageID : storageVA) {
			try {
				uncertaintySum += hashStorages.get(storageID).getFloat(dataRepresentation, contentIndex);
			}
			catch (Exception e) {
				System.out.println("storageID: " + storageID);
			}
		}
		return uncertaintySum / storageVA.size();
	}

	@SuppressWarnings("unused")
	private float calculateMaxUncertainty(int contentIndex, EDataRepresentation dataRepresentation) {
		float maxUncertainty = Float.MAX_VALUE;
		for (Integer storageID : hashStorageData.get(STORAGE).getStorageVA()) {
			float cellUncertainty = 0;
			try {
				cellUncertainty = hashStorages.get(storageID).getFloat(dataRepresentation, contentIndex);
			}
			catch (Exception e) {
				System.out.println("storageID: " + storageID);

			}
			if (cellUncertainty < maxUncertainty) {
				maxUncertainty = cellUncertainty;
			}
		}
		return maxUncertainty;
	}
}
