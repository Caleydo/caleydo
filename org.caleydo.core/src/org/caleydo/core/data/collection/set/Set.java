package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.export.SetExporter;
import org.caleydo.core.data.collection.export.SetExporter.EWhichViewToExport;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
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

	private ESetType setType;

	private ArrayList<IStorage> alStorages;

	private String sLabel;

	private boolean bArtificialMin = false;
	private double dMin = Double.MAX_VALUE;

	private boolean bArtificialMax = false;
	private double dMax = Double.MIN_VALUE;

	private int iDepth = 0;

	private ERawDataType rawDataType;

	private boolean bIsNumerical;

	private HashMap<Integer, IVirtualArray> hashContentVAs;
	private HashMap<Integer, IVirtualArray> hashStorageVAs;

	// clustering stuff
	private ArrayList<Integer> alClusterSizes = null;
	private ArrayList<Integer> alClusterExamples = null;

	private GroupList groupListGenes = new GroupList(0);
	private GroupList groupListExperiments = new GroupList(0);
	private boolean bGeneClusterInfo = false;
	private boolean bExperimentClusterInfo = false;

	/** Tree for content hierarchy */
	private Tree<ClusterNode> contentTree;
	/** Tree for storage hierarchy */
	private Tree<ClusterNode> storageTree;
	/** Root node for storage hierarchy which is only set in metaSets */
	private ClusterNode storageTreeRoot = null;

	private EExternalDataRepresentation externalDataRep;

	private boolean bIsSetHomogeneous = false;

	public Set() {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.SET));

		GeneralManager.get().getSetManager().registerItem(this);

		alStorages = new ArrayList<IStorage>();
		hashContentVAs = new HashMap<Integer, IVirtualArray>();
		hashStorageVAs = new HashMap<Integer, IVirtualArray>();
	}

	@Override
	public void setSetType(ESetType setType) {
		this.setType = setType;
	}

	@Override
	public ESetType getSetType() {
		return setType;
	}

	@Override
	public void addStorage(int iStorageID) {
		IStorageManager storageManager = GeneralManager.get().getStorageManager();

		if (!storageManager.hasItem(iStorageID))
			throw new IllegalArgumentException("Requested Storage with ID " + iStorageID + " does not exist.");

		addStorage(storageManager.getItem(iStorageID));
	}

	@Override
	public void addStorage(IStorage storage) {
		if (alStorages.isEmpty()) {
			// iColumnLength = storage.size();
			// rawDataType = storage.getRawDataType();
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
		alStorages.add(storage);
	}

	@Override
	public IStorage get(int iIndex) {
		return alStorages.get(iIndex);
	}

	@Override
	public StorageIterator VAIterator(int uniqueID) {
		return new StorageIterator(alStorages, hashStorageVAs.get(iUniqueID));
	}

	@Override
	public int size() {
		return alStorages.size();
	}

	@Override
	public int sizeVA(int iUniqueID) {
		if (hashStorageVAs.containsKey(iUniqueID))
			return hashStorageVAs.get(iUniqueID).size();
		else if (hashContentVAs.containsKey(iUniqueID))
			return hashContentVAs.get(iUniqueID).size();
		else
			throw new IllegalArgumentException("No such virtual array has been registered:" + iUniqueID);

	}

	@Override
	public int depth() {
		if (iDepth == 0) {
			for (IStorage storage : alStorages) {
				if (iDepth == 0)
					iDepth = storage.size();
				else {
					if (iDepth != storage.size())
						throw new IllegalArgumentException("All storages in a set must be of the same length");
				}

			}
		}
		return iDepth;
	}

	private void normalize() {
		bIsSetHomogeneous = false;
		for (IStorage storage : alStorages) {
			storage.normalize();
		}
	}

	private void normalizeGlobally() {
		bIsSetHomogeneous = true;
		for (IStorage storage : alStorages) {
			if (storage instanceof INumericalStorage) {
				INumericalStorage nStorage = (INumericalStorage) storage;

				nStorage.normalizeWithExternalExtrema(getMin(), getMax());

			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal storages, currently not supported!");
		}
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
	public Iterator<IStorage> iterator() {
		return alStorages.iterator();
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
	public void setMin(double dMin) {
		bArtificialMin = true;
		this.dMin = dMin;
	}

	@Override
	public void setMax(double dMax) {
		bArtificialMax = true;
		this.dMax = dMax;
	}

	@Override
	public double getRawForNormalized(double dNormalized) {
		if (!bIsSetHomogeneous)
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

	public double getNormalizedForRaw(double dRaw) {
		if (!bIsSetHomogeneous)
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
	public void log10() {
		for (IStorage storage : alStorages) {
			if (storage instanceof INumericalStorage) {
				INumericalStorage nStorage = (INumericalStorage) storage;
				nStorage.log10();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
		}
	}

	@Override
	public void log2() {

		for (IStorage storage : alStorages) {
			if (storage instanceof INumericalStorage) {
				INumericalStorage nStorage = (INumericalStorage) storage;
				nStorage.log2();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
		}
	}

	@Override
	public Histogram getHistogram() {
		if (!bIsSetHomogeneous) {
			throw new UnsupportedOperationException(
				"Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		}
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (IStorage storage : alStorages) {
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
	public int createVA(EVAType vaType, List<Integer> iAlSelections) {
		if (vaType == EVAType.STORAGE) {
			IVirtualArray virtualArray = new VirtualArray(vaType, size(), iAlSelections);
			return createStorageVA(virtualArray);
		}
		else {
			IVirtualArray va = new VirtualArray(vaType, depth(), iAlSelections);
			return createContentVA(va);
		}

	}

	private int createContentVA(IVirtualArray virtualArray) {
		int iUniqueID = virtualArray.getID();
		hashContentVAs.put(iUniqueID, virtualArray);
		return iUniqueID;
	}

	private int createStorageVA(IVirtualArray virtualArray) {
		int iUniqueID = virtualArray.getID();
		hashStorageVAs.put(iUniqueID, virtualArray);
		return iUniqueID;
	}

	public IVirtualArray createCompleteStorageVA() {

		ArrayList<Integer> storages;
		if (storageTree == null) {
			storages = new ArrayList<Integer>();
			for (int count = 0; count < alStorages.size(); count++) {
				storages.add(count);
			}
		}
		else {
			storages = storageTreeRoot.getLeaveIds();
		}
		VirtualArray virtualArray = new VirtualArray(EVAType.STORAGE, size(), storages);
		return virtualArray;
	}

	@SuppressWarnings("unused")
	private int createStorageVA(EVAType vaType, ArrayList<Integer> iAlSelections) {
		VirtualArray virtualArray = new VirtualArray(vaType, size(), iAlSelections);
		int iUniqueID = virtualArray.getID();

		hashStorageVAs.put(iUniqueID, virtualArray);

		return iUniqueID;
	}

	@Override
	public void resetVirtualArray(int iUniqueID) {
		if (hashStorageVAs.containsKey(iUniqueID)) {
			hashStorageVAs.get(iUniqueID).reset();
			return;
		}

		if (hashContentVAs.containsKey(iUniqueID)) {
			hashContentVAs.get(iUniqueID).reset();
		}
	}

	@Override
	public void removeVirtualArray(int iUniqueID) {
		hashStorageVAs.remove(iUniqueID);
		hashContentVAs.remove(iUniqueID);
	}

	@Override
	public IVirtualArray getVA(int iUniqueID) {
		if (hashStorageVAs.containsKey(iUniqueID))
			return hashStorageVAs.get(iUniqueID);
		else if (hashContentVAs.containsKey(iUniqueID))
			return hashContentVAs.get(iUniqueID);
		else
			throw new IllegalArgumentException("No Virtual Array for the unique id: " + iUniqueID);
	}

	@Override
	public void replaceVA(int iUniqueID, IVirtualArray virtualArray) {
		virtualArray.setID(iUniqueID);
		if (hashStorageVAs.containsKey(iUniqueID))
			hashStorageVAs.put(iUniqueID, virtualArray);
		else if (hashContentVAs.containsKey(iUniqueID))
			hashContentVAs.put(iUniqueID, virtualArray);
		else
			throw new IllegalArgumentException("No Virtual Array for the unique id: " + iUniqueID);
	}

	private void calculateGlobalExtrema() {
		double dTemp = 1.0;
		if (alStorages.get(0) instanceof INumericalStorage) {
			for (IStorage storage : alStorages) {
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
		else if (alStorages.get(0) instanceof INominalStorage<?>)
			throw new UnsupportedOperationException("No minimum or maximum can be calculated "
				+ "on nominal data");
	}

	@Override
	public void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep,
		boolean bIsSetHomogeneous) {
		this.bIsSetHomogeneous = bIsSetHomogeneous;
		if (externalDataRep == this.externalDataRep)
			return;

		this.externalDataRep = externalDataRep;

		for (IStorage storage : alStorages) {
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
					normalize();
					break;
				case LOG10:
					log10();
					normalize();
					break;
				case LOG2:
					log2();
					normalize();
					break;
			}
		}
	}

	@Override
	public boolean isSetHomogeneous() {
		return bIsSetHomogeneous;
	}

	@Override
	public void export(String sFileName, EWhichViewToExport eWichViewToExport) {
		SetExporter exporter = new SetExporter();
		exporter.export(this, sFileName, eWichViewToExport);
	}

	@Override
	public void exportGroups(String sFileName, ArrayList<Integer> alGenes, ArrayList<Integer> alExperiments) {
		SetExporter exporter = new SetExporter();
		exporter.exportGroups(this, sFileName, alGenes, alExperiments);
	}

	@Override
	public ArrayList<IVirtualArray> cluster(ClusterState clusterState) {

		if (bIsNumerical == true && bIsSetHomogeneous == true) {

			ClusterManager clusterManager = new ClusterManager(this);
			return clusterManager.cluster(clusterState);

		}
		else
			return null;

	}

	public void setAlClusterSizes(ArrayList<Integer> alClusterSizes) {
		this.alClusterSizes = alClusterSizes;
	}

	public ArrayList<Integer> getAlClusterSizes() {
		return alClusterSizes;
	}

	@Override
	public ArrayList<Integer> getAlExamples() {
		return alClusterExamples;
	}

	@Override
	public void setAlExamples(ArrayList<Integer> alExamples) {
		this.alClusterExamples = alExamples;
	}

	@Override
	public void setGroupNrInfo(int[] arGroupInfo, boolean bGeneGroupInfo) {

		int cluster = 0, cnt = 0;

		GroupList groupListTemp = null;

		if (bGeneGroupInfo) {
			groupListTemp = groupListGenes;
			bGeneClusterInfo = true;
		}
		else {
			groupListTemp = groupListExperiments;
			bExperimentClusterInfo = true;
		}

		groupListTemp.clear();

		for (int i = 0; i < arGroupInfo.length; i++) {
			Group group = null;
			if (cluster != arGroupInfo[i]) {
				group = new Group(cnt, false, 0, SelectionType.NORMAL);
				groupListTemp.append(group);
				cluster++;
				cnt = 0;
			}
			cnt++;
			if (i == arGroupInfo.length - 1) {
				group = new Group(cnt, false, 0, SelectionType.NORMAL);
				groupListTemp.append(group);
			}
		}
	}

	@Override
	public void setGroupReprInfo(int[] arGroupRepr, boolean bGeneGroupInfo) {

		int group = 0;

		GroupList groupListTemp = null;

		if (bGeneGroupInfo) {
			groupListTemp = groupListGenes;
		}
		else {
			groupListTemp = groupListExperiments;
		}

		groupListTemp.get(group).setIdxExample(0);
		group++;

		for (int i = 1; i < arGroupRepr.length; i++) {
			if (arGroupRepr[i] != arGroupRepr[i - 1]) {
				groupListTemp.get(group).setIdxExample(i);
				group++;
			}
		}
	}

	@Override
	public boolean isGeneClusterInfo() {
		return bGeneClusterInfo;
	}

	@Override
	public boolean isExperimentClusterInfo() {
		return bExperimentClusterInfo;
	}

	@Override
	public void setGroupListGenes(GroupList groupList) {
		groupListGenes = groupList;
		bGeneClusterInfo = true;
	}

	@Override
	public void setGroupListExperiments(GroupList groupList) {
		groupListExperiments = groupList;
		bExperimentClusterInfo = true;

	}

	@Override
	public void setGeneClusterInfoFlag(boolean bGeneClusterInfo) {
		this.bGeneClusterInfo = bGeneClusterInfo;
	}

	@Override
	public void setExperimentClusterInfoFlag(boolean bExperimentClusterInfo) {
		this.bExperimentClusterInfo = bExperimentClusterInfo;
	}

	@Override
	public GroupList getGroupListGenes() {
		return this.groupListGenes;
	}

	@Override
	public GroupList getGroupListExperiments() {
		return this.groupListExperiments;
	}

	@Override
	public void setContentTree(Tree<ClusterNode> contentTree) {
		this.contentTree = contentTree;
	}

	@Override
	public Tree<ClusterNode> getContentTree() {
		return contentTree;
	}

	@Override
	public void setStorageTree(Tree<ClusterNode> storageTree) {
		this.storageTree = storageTree;
	}

	@Override
	public Tree<ClusterNode> getStorageTree() {
		return storageTree;
	}

	@Override
	public ClusterNode getStorageTreeRoot() {
		if (storageTreeRoot == null)
			return storageTree.getRoot();
		return storageTreeRoot;
	}

	@Override
	public void setStorageTreeRoot(ClusterNode storageTreeRoot) {
		this.storageTreeRoot = storageTreeRoot;
	}

	@Override
	public void destroy() {
		IGeneralManager gm = GeneralManager.get();
		IStorageManager sm = gm.getStorageManager();
		for (IStorage storage : alStorages) {
			sm.unregisterItem(storage.getID());
		}
		gm.getSetManager().unregisterItem(iUniqueID);
		// clearing the VAs. This should not be necessary since they should be destroyed automatically.
		// However, to make sure.
	}

	@Override
	public void finalize() {
		GeneralManager.get().getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Set " + this + "destroyed"));
	}

	@Override
	public String toString() {
		return "Set " + getLabel() + " of type " + setType + " with " + alStorages.size() + " storages.";
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

	public void createMetaSets() {
		ClusterNode rootNode = storageTree.getRoot();
		rootNode.createMetaSets(this);

		// test
		ISet metaSet = rootNode.getChildren().get(0).getChildren().get(0).getMetaSet();

		IUseCase useCase = GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA);
		useCase.setSet(metaSet);
		useCase.updateSetInViews();
		// tree.get
	}

	@Override
	public ISet getShallowClone() {
		Set metaSet = new Set();
		metaSet.setType = this.setType;
		metaSet.bIsSetHomogeneous = this.bIsSetHomogeneous;
		metaSet.externalDataRep = this.externalDataRep;

		// try {
		// metaSet = (Set)super.clone();
		// }
		// catch (CloneNotSupportedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return metaSet;
	}

}
