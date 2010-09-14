package org.caleydo.core.data.collection.set;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.set.statistics.StatisticsResult;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.set.SetManager;
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
 * Implementation of the ISet interface
 * 
 * @author Alexander Lex
 */
public class Set
	extends AUniqueObject
	implements ISet {

	protected HashMap<Integer, IStorage> hashStorages;

	private String sLabel;

	private boolean bArtificialMin = false;
	private double dMin = Double.MAX_VALUE;

	private boolean bArtificialMax = false;
	private double dMax = Double.MIN_VALUE;

	protected int depth = 0;

	private ERawDataType rawDataType;

	private boolean bIsNumerical;

	protected HashMap<String, ContentData> hashContentData;
	protected HashMap<String, StorageData> hashStorageData;

	protected NumericalStorage meanStorage;

	protected StorageData defaultStorageData;

	// private boolean bGeneClusterInfo = false;
	// private boolean bExperimentClusterInfo = false;

	protected EExternalDataRepresentation externalDataRep;

	protected boolean isSetHomogeneous = false;

	protected StatisticsResult statisticsResult;

	ASetBasedDataDomain dataDomain;

	public Set() {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.SET));
	}

	/**
	 * Constructor for the set. Creates and initializes members and registers the set whit the set manager.
	 * Also creates a new default tree. This should not be called by implementing sub-classes.
	 */
	public Set(ASetBasedDataDomain dataDomain) {
		this();
		this.dataDomain = dataDomain;
		initWithDataDomain();
	}

	private void initWithDataDomain() {
		SetManager.getInstance().registerItem(this);
		init();
		Tree<ClusterNode> tree = new Tree<ClusterNode>(dataDomain.getStorageIDType());
		ClusterNode root = new ClusterNode(tree, "Root", 1, true, -1);
		tree.setRootNode(root);
		defaultStorageData.setStorageTree(tree);
		// hashStorageData.put(StorageVAType.STORAGE, defaultStorageData.clone());
	}

	/**
	 * Initialization of member variables. Safe to be called by sub-classes.
	 */
	protected void init() {

		hashStorages = new HashMap<Integer, IStorage>();
		hashContentData = new HashMap<String, ContentData>(6);
		hashStorageData = new HashMap<String, StorageData>(3);
		defaultStorageData = new StorageData();
		defaultStorageData.setStorageVA(new StorageVirtualArray(STORAGE));
		statisticsResult = new StatisticsResult(this);
	}

	/**
	 * Creates a {@link MetaSet} for every node in the storage tree.
	 */
	public void createMetaSets() {
		ClusterNode rootNode = hashStorageData.get(STORAGE).getStorageTreeRoot();
		rootNode.createMetaSets(this);
	}

	@XmlTransient
	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		initWithDataDomain();
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public IStorage get(Integer storageID) {
		return hashStorages.get(storageID);
	}

	@Override
	public int size() {
		return hashStorages.size();
	}

	@Override
	public int depth() {
		if (depth == 0) {
			for (IStorage storage : hashStorages.values()) {
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

	@Override
	public Iterator<IStorage> iterator(String type) {
		return new StorageIterator(hashStorages, hashStorageData.get(type).getStorageVA());
	}

	@Override
	public double getMin() {
		if (dMin == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		return dMin;
	}

	@Override
	public double getMax() {
		if (dMax == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		return dMax;
	}

	@Override
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

	@Override
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

	@Override
	public Histogram getHistogram() {
		if (!isSetHomogeneous) {
			throw new UnsupportedOperationException(
				"Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		}
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (IStorage storage : hashStorages.values()) {
			INumericalStorage nStorage = (INumericalStorage) storage;
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

	@Override
	public void restoreOriginalContentVA() {
		ContentData contentData = createContentData(CONTENT);
		hashContentData.put(CONTENT, contentData);
	}

	// private int createStorageVA(IVirtualArray virtualArray) {
	// int iUniqueID = virtualArray.getID();
	// hashStorageVAs.put(iUniqueID, virtualArray);
	// return iUniqueID;
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
	// int iUniqueID = virtualArray.getID();
	//
	// hashStorageVAs.put(iUniqueID, virtualArray);
	//
	// return iUniqueID;
	// }
	// @Override
	// public void resetVirtualArray(int iUniqueID) {
	// if (hashStorageVAs.containsKey(iUniqueID)) {
	// hashStorageVAs.get(iUniqueID).reset();
	// return;
	// }
	//
	// if (hashContentVAs.containsKey(iUniqueID)) {
	// hashContentVAs.get(iUniqueID).reset();
	// }
	// }
	// @Override
	// public void removeVirtualArray(int iUniqueID) {
	// hashStorageVAs.remove(iUniqueID);
	// hashContentVAs.remove(iUniqueID);
	// }
	// @Override
	// public IVirtualArray getVA(int iUniqueID) {
	// if (hashStorageVAs.containsKey(iUniqueID))
	// return hashStorageVAs.get(iUniqueID);
	// else if (hashContentVAs.containsKey(iUniqueID))
	// return hashContentVAs.get(iUniqueID);
	// else
	// throw new IllegalArgumentException("No Virtual Array for the unique id: " + iUniqueID);
	// }
	@Override
	public void setContentVA(String vaType, ContentVirtualArray virtualArray) {
		ContentData contentData = hashContentData.get(vaType);
		if (contentData == null)
			contentData = createContentData(vaType);
		else
			contentData.reset();
		contentData.setContentVA(virtualArray);
		hashContentData.put(vaType, contentData);
	}

	@Override
	public void setStorageVA(String vaType, StorageVirtualArray virtualArray) {
		StorageData storageData = hashStorageData.get(vaType);
		if (storageData == null)
			storageData = defaultStorageData.clone();
		else
			storageData.reset();
		storageData.setStorageVA(virtualArray);
		hashStorageData.put(vaType, storageData);
	}

	@Override
	public EExternalDataRepresentation getExternalDataRep() {
		return externalDataRep;
	}

	@Override
	public boolean isSetHomogeneous() {
		return isSetHomogeneous;
	}

	@Override
	public void cluster(ClusterState clusterState) {

		if (bIsNumerical == true && isSetHomogeneous == true) {

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
		}
		else
			throw new IllegalStateException("Cannot cluster a non-numerical or non-homogeneous Set");

	}

	@Override
	public ContentData getContentData(String vaType) {
		ContentData contentData = hashContentData.get(vaType);
		if (contentData == null) {
			contentData = createContentData(vaType);
			hashContentData.put(vaType, contentData);
		}
		return contentData;
	}

	@Override
	public StorageData getStorageData(String vaType) {
		return hashStorageData.get(vaType);
	}

	@Override
	public void destroy() {
		GeneralManager gm = GeneralManager.get();
		IStorageManager sm = gm.getStorageManager();
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

	@Override
	public double getMinAs(EExternalDataRepresentation dataRepresentation) {
		if (dMin == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == externalDataRep)
			return dMin;
		double result = getRawFromExternalDataRep(dMin);

		return getDataRepFromRaw(result, dataRepresentation);
	}

	@Override
	public double getMaxAs(EExternalDataRepresentation dataRepresentation) {
		if (dMax == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == externalDataRep)
			return dMax;
		double result = getRawFromExternalDataRep(dMax);

		return getDataRepFromRaw(result, dataRepresentation);
	}

	@XmlTransient
	@Override
	public StatisticsResult getStatisticsResult() {
		return statisticsResult;
	}

	@Override
	public NumericalStorage getMeanStorage() {
		if (!bIsNumerical || !isSetHomogeneous)
			throw new IllegalStateException(
				"Can not provide a mean storage if set is not numerical (isNumerical: " + bIsNumerical
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
		IStorageManager storageManager = GeneralManager.get().getStorageManager();

		if (!storageManager.hasItem(iStorageID))
			throw new IllegalArgumentException("Requested Storage with ID " + iStorageID + " does not exist.");

		addStorage(storageManager.getItem(iStorageID));
	}

	/**
	 * Add a storage by reference. The storage has to be fully initialized with data
	 * 
	 * @param storage
	 *            the storage
	 */
	void addStorage(IStorage storage) {
		if (hashStorages.isEmpty()) {
			if (storage instanceof INumericalStorage) {
				bIsNumerical = true;
			}
			else {
				bIsNumerical = false;
			}

			rawDataType = storage.getRawDataType();
			// iDepth = storage.size();
		}
		else {
			if (!bIsNumerical && storage instanceof INumericalStorage)
				throw new IllegalArgumentException(
					"All storages in a set must be of the same basic type (nunmerical or nominal)");
			if (rawDataType != storage.getRawDataType())
				throw new IllegalArgumentException("All storages in a set must have the same raw data type");
			// if (iDepth != storage.size())
			// throw new IllegalArgumentException("All storages in a set must be of the same length");
		}
		hashStorages.put(storage.getID(), storage);
		defaultStorageData.getStorageVA().append(storage.getID());

	}

	void finalizeAddedStorages() {
		// this needs only be done by the root set
		if ((this.getClass().equals(Set.class))) {
			Tree<ClusterNode> tree = defaultStorageData.getStorageTree();
			int count = 1;
			for (Integer storageID : defaultStorageData.getStorageVA()) {
				ClusterNode node =
					new ClusterNode(tree, get(storageID).getLabel(), count++, false, storageID);
				tree.addChild(tree.getRoot(), node);
			}

			tree.getRoot().createMetaSets(this);

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

		for (IStorage storage : hashStorages.values()) {
			if (storage instanceof INumericalStorage) {
				((INumericalStorage) storage).setExternalDataRepresentation(externalDataRep);
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
		for (IStorage storage : hashStorages.values()) {
			if (storage instanceof INumericalStorage) {
				INumericalStorage nStorage = (INumericalStorage) storage;
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

		for (IStorage storage : hashStorages.values()) {
			if (storage instanceof INumericalStorage) {
				INumericalStorage nStorage = (INumericalStorage) storage;
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
		for (IStorage storage : hashStorages.values()) {
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
		for (IStorage storage : hashStorages.values()) {
			if (storage instanceof INumericalStorage) {
				INumericalStorage nStorage = (INumericalStorage) storage;
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

		ContentVirtualArray contentVA = new ContentVirtualArray(vaType);
		if (vaType != CONTENT_CONTEXT) {
			for (int count = 0; count < depth(); count++) {
				contentVA.append(count);
			}
		}
		contentData.setContentVA(contentVA);
		return contentData;

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

		if (bIsNumerical) {
			for (IStorage storage : hashStorages.values()) {
				INumericalStorage nStorage = (INumericalStorage) storage;
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
		else if (hashStorages.get(0) instanceof INominalStorage<?>)
			throw new UnsupportedOperationException("No minimum or maximum can be calculated "
				+ "on nominal data");
	}

	@Override
	public java.util.Set<String> getRegisteredContentVATypes() {
		return hashContentData.keySet();
	}

	@Override
	public java.util.Set<String> getRegisteredStorageVATypes() {
		return hashStorageData.keySet();
	}

}
