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
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IGroupList;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.clusterer.AffinityClusterer;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.HierarchicalClusterer;
import org.caleydo.core.util.clusterer.IClusterer;
import org.caleydo.core.util.clusterer.KMeansClusterer;
import org.caleydo.core.util.clusterer.TreeClusterer;

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

	private HashMap<Integer, IVirtualArray> hashStorageVAs;
	private HashMap<Integer, IVirtualArray> hashSetVAs;

	// clustering stuff
	private ArrayList<Integer> alClusterSizes = null;
	private ArrayList<Integer> alClusterExamples = null;
	private Tree<ClusterNode> clusteredTree;
	private GroupList groupList = new GroupList(0);
	private boolean bClusterInfo = false;

	private EExternalDataRepresentation externalDataRep;

	private boolean bIsSetHomogeneous = false;

	public Set() {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.SET));

		GeneralManager.get().getSetManager().registerItem(this);

		alStorages = new ArrayList<IStorage>();
		hashStorageVAs = new HashMap<Integer, IVirtualArray>();
		hashSetVAs = new HashMap<Integer, IVirtualArray>();
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
			// if (storage.size() != iColumnLength)
			// throw new
			// CaleydoRuntimeException("Storages must be of the same length",
			// CaleydoRuntimeExceptionType.DATAHANDLING);
			// if (rawDataType != storage.getRawDataType())
			// throw new CaleydoRuntimeException(
			// "Storages in a set must have the same raw data type",
			// CaleydoRuntimeExceptionType.DATAHANDLING);
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
	public SetIterator VAIterator(int uniqueID) {
		return new SetIterator(alStorages, hashSetVAs.get(iUniqueID));
	}

	@Override
	public IStorage getStorageFromVA(int iUniqueID, int iIndex) {
		if (hashSetVAs.containsKey(iUniqueID)) {
			int iTmp = hashSetVAs.get(iUniqueID).get(iIndex);
			return alStorages.get(iTmp);
		}
		else
			throw new IllegalArgumentException("No such virtual array " + iUniqueID
				+ " registered for storages");
	}

	@Override
	public int size() {
		return alStorages.size();
	}

	@Override
	public int sizeVA(int iUniqueID) {
		if (hashSetVAs.containsKey(iUniqueID))
			return hashSetVAs.get(iUniqueID).size();
		else if (hashStorageVAs.containsKey(iUniqueID))
			return hashStorageVAs.get(iUniqueID).size();
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

		if (dNormalized == 0)
			return getMin();
		// if(getMin() > 0)
		return getMin() + dNormalized * (getMax() - getMin());
		// return (dNormalized) * (getMax() + getMin());
	}

	public double getNormalizedForRaw(double dRaw) {
		if (!bIsSetHomogeneous)
			throw new IllegalStateException(
				"Can not produce normalized data on set level for inhomogenous sets. Access via storages");

		if (dRaw < getMin() || dRaw > getMax())
			throw new IllegalArgumentException("Value may not be smaller than min or larger than max");

		return (dRaw - getMin()) / (getMax() - getMin());
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
	public int createStorageVA() {
		VirtualArray virtualArray = new VirtualArray(depth());
		return doCreateStorageVA(virtualArray);

	}

	@Override
	public int createStorageVA(List<Integer> iAlSelections) {
		IVirtualArray virtualArray = new VirtualArray(depth(), iAlSelections);
		return doCreateStorageVA(virtualArray);
	}

	@Override
	public int createSetVA() {
		VirtualArray virtualArray = new VirtualArray(size());
		int iUniqueID = virtualArray.getID();
		hashSetVAs.put(iUniqueID, virtualArray);
		return iUniqueID;
	}

	@Override
	public int createSetVA(ArrayList<Integer> iAlSelections) {
		VirtualArray virtualArray = new VirtualArray(size(), iAlSelections);
		int iUniqueID = virtualArray.getID();

		hashSetVAs.put(iUniqueID, virtualArray);

		return iUniqueID;
	}

	@Override
	public void resetVirtualArray(int iUniqueID) {
		if (hashSetVAs.containsKey(iUniqueID)) {
			hashSetVAs.get(iUniqueID).reset();
			return;
		}

		if (hashStorageVAs.containsKey(iUniqueID)) {
			hashStorageVAs.get(iUniqueID).reset();
		}
	}

	@Override
	public void removeVirtualArray(int iUniqueID) {
		hashSetVAs.remove(iUniqueID);
		for (IStorage storage : alStorages) {
			storage.removeVirtualArray(iUniqueID);
		}
		hashStorageVAs.remove(iUniqueID);
	}

	@Override
	public IVirtualArray getVA(int iUniqueID) {
		if (hashSetVAs.containsKey(iUniqueID))
			return hashSetVAs.get(iUniqueID);
		else if (hashStorageVAs.containsKey(iUniqueID))
			return hashStorageVAs.get(iUniqueID);
		else
			throw new IllegalArgumentException("No Virtual Array for the unique id: " + iUniqueID);
	}

	private void calculateGlobalExtrema() {
		double dTemp = 0.0;
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
		else if (alStorages.get(0) instanceof INominalStorage)
			throw new UnsupportedOperationException("No minimum or maximum can be calculated "
				+ "on nominal data");
	}

	private int doCreateStorageVA(IVirtualArray virtualArray) {
		int iUniqueID = virtualArray.getID();
		hashStorageVAs.put(iUniqueID, virtualArray);
		for (IStorage storage : alStorages) {
			storage.setVirtualArray(iUniqueID, hashStorageVAs.get(iUniqueID));
		}
		return iUniqueID;
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

	public void export(String sFileName, boolean bExportBucketInternal) {
		SetExporter exporter = new SetExporter();
		exporter.export(this, sFileName, bExportBucketInternal);
	}

	public Integer cluster(Integer iVAIdContent, Integer iVAIdStorage, EClustererAlgo eClustererAlgo,
		EClustererType eClustererType) {

		Integer VAId = 0;

		if (bIsNumerical == true && bIsSetHomogeneous == true) {

			IClusterer clusterer;

			switch (eClustererAlgo) {
				case TREE_CLUSTERER:

					if (eClustererType == EClustererType.GENE_CLUSTERING)
						clusterer = new TreeClusterer(getVA(iVAIdContent).size());
					else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING)
						clusterer = new TreeClusterer(getVA(iVAIdStorage).size());
					else {
						System.out.println("Not implemented yet");
						clusterer = new TreeClusterer(getVA(iVAIdContent).size());
					}

					System.out.println("treeClustering in progress ... ");
					VAId = clusterer.getSortedVAId(this, iVAIdContent, iVAIdStorage, eClustererType);
					System.out.println("treeClustering done");

					break;

				case COBWEB_CLUSTERER:

					clusterer = new HierarchicalClusterer(0);

					System.out.println("Cobweb in progress ... ");
					VAId = clusterer.getSortedVAId(this, iVAIdContent, iVAIdStorage, eClustererType);
					System.out.println("Cobweb done");

					break;

				case AFFINITY_PROPAGATION:

					if (eClustererType == EClustererType.GENE_CLUSTERING)
						clusterer = new AffinityClusterer(getVA(iVAIdContent).size());
					else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING)
						clusterer = new AffinityClusterer(getVA(iVAIdStorage).size());
					else {
						System.out.println("Not implemented yet");
						clusterer = new AffinityClusterer(getVA(iVAIdContent).size());
					}

					System.out.println("affinityPropagation in progress ... ");
					VAId = clusterer.getSortedVAId(this, iVAIdContent, iVAIdStorage, eClustererType);
					System.out.println("affinityPropagation done");

					break;

				case KMEANS_CLUSTERER:

					clusterer = new KMeansClusterer(0);

					System.out.println("KMeansClusterer in progress ... ");
					VAId = clusterer.getSortedVAId(this, iVAIdContent, iVAIdStorage, eClustererType);
					System.out.println("KMeansClusterer done");

					break;
			}

			IVirtualArray virtualArray = getVA(VAId);

			if (eClustererAlgo == EClustererAlgo.AFFINITY_PROPAGATION
				|| eClustererAlgo == EClustererAlgo.KMEANS_CLUSTERER
				|| eClustererAlgo == EClustererAlgo.COBWEB_CLUSTERER) {

				IGroupList groupList = new GroupList(virtualArray.size());

				ArrayList<Integer> examples = getAlExamples();
				int cnt = 0;
				for (Integer iter : getAlClusterSizes()) {
					Group temp = new Group(iter, false, examples.get(cnt), ESelectionType.NORMAL);
					groupList.append(temp);
					cnt++;
				}
				virtualArray.setGroupList(groupList);
			}

			hashSetVAs.put(virtualArray.getID(), virtualArray);

			return VAId;
		}
		else {
			System.out.println("Set is not numerical/homogeneous --> clustering not allowed !");
			return null;
		}
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
	public void setClusteredTree(Tree<ClusterNode> clusteredTree) {
		this.clusteredTree = clusteredTree;
	}

	@Override
	public Tree<ClusterNode> getClusteredTree() {
		return clusteredTree;
	}

	@Override
	public void setGroupNrInfo(int[] arGroupInfo) {

		int cluster = 0, cnt = 0;

		groupList.clear();
		
		for (int i = 0; i < arGroupInfo.length; i++) {
			Group group = null;
			if (cluster != arGroupInfo[i]) {
				group = new Group(cnt, false, 0, ESelectionType.NORMAL);
				groupList.append(group);
				cluster++;
				cnt = 0;
			}
			cnt++;
		}
		bClusterInfo = true;
	}

	@Override
	public void setGroupReprInfo(int[] arGroupRepr) {

		int group = 0;
		int repr = 0;
		int offset = 0;

		for (int i = 0; i < arGroupRepr.length; i++) {
			if (arGroupRepr[i] == 1) {
				repr = i - offset;
				groupList.get(group).setIdxExample(repr);
				offset = offset + groupList.get(group).getNrElements();
				group++;
			}
		}
	}

	@Override
	public boolean isClusterInfo() {
		return bClusterInfo;
	}

	@Override
	public GroupList getGroupList() {
		return this.groupList;
	}

}
