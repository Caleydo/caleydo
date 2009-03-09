package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.export.SetExporter;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.clusterer.CNode;
import org.caleydo.core.util.clusterer.HierarchicalClusterer;
import org.caleydo.core.util.clusterer.KMeansClusterer;
import org.caleydo.util.graph.IGraph;

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
	private HashMap<Integer, IGraph> hashVAIdToGraph;
	private CNode clusteredGraph = null;

	private EExternalDataRepresentation externalDataRep;

	private boolean bIsSetHomogeneous = false;

	public Set() {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.SET));

		GeneralManager.get().getSetManager().registerItem(this);

		alStorages = new ArrayList<IStorage>();
		hashStorageVAs = new HashMap<Integer, IVirtualArray>();
		hashSetVAs = new HashMap<Integer, IVirtualArray>();
		hashVAIdToGraph = new HashMap<Integer, IGraph>();
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
			if (storage instanceof INumericalStorage)
				bIsNumerical = true;
			else
				bIsNumerical = false;

			rawDataType = storage.getRawDataType();
			iDepth = storage.size();
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
			if (iDepth != storage.size())
				throw new IllegalArgumentException("All storages in a set must be of the same length");
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
		else {
			throw new IllegalArgumentException("No such virtual array " + iUniqueID + " registered for storages");
		}
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
			else {
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal storages, currently not supported!");
			}
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
		if (dMin == Double.MAX_VALUE)
			calculateGlobalExtrema();
		return dMin;
	}

	@Override
	public double getMax() {
		if (dMax == Double.MIN_VALUE)
			calculateGlobalExtrema();
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
			else {
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
			}
		}
	}

	@Override
	public void log2() {

		for (IStorage storage : alStorages) {
			if (storage instanceof INumericalStorage) {
				INumericalStorage nStorage = (INumericalStorage) storage;
				nStorage.log2();
			}
			else {
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
			}
		}
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
		VirtualArray virtualArray = new VirtualArray(depth());
		int iUniqueID = virtualArray.getID();
		hashSetVAs.put(iUniqueID, virtualArray);
		return iUniqueID;
	}

	@Override
	public int createSetVA(ArrayList<Integer> iAlSelections) {
		VirtualArray virtualArray = new VirtualArray(depth(), iAlSelections);
		int iUniqueID = virtualArray.getID();

		// FIXME: Without the next line nothing works - please check this out
		// Alex!
		hashSetVAs.put(iUniqueID, virtualArray);

		return iUniqueID;
	}

	@Override
	public void resetVirtualArray(int iUniqueID) {
		if (hashSetVAs.containsKey(iUniqueID)) {
			hashSetVAs.get(iUniqueID).reset();
			return;
		}

		if (hashStorageVAs.containsKey(iUniqueID))
			hashStorageVAs.get(iUniqueID).reset();
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
				if (!bArtificialMin && dTemp < dMin)
					dMin = dTemp;
				dTemp = nStorage.getMax();
				if (!bArtificialMax && dTemp > dMax)
					dMax = dTemp;
			}
		}
		else if (alStorages.get(0) instanceof INominalStorage) {
			throw new UnsupportedOperationException("No minimum or maximum can be calculated " + "on nominal data");

		}
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

	public ArrayList<Integer> cluster(Integer iVAIdOriginal, Integer iVAIdStorage,
		boolean bHierarchicalClustering) {
		ArrayList<Integer> VAIds = new ArrayList<Integer>();

		long tic, toc, duration;

		if (bHierarchicalClustering) {
			System.out.println("hierarchical clustering ...");

			tic = System.currentTimeMillis();

			HierarchicalClusterer clusterer = new HierarchicalClusterer();
			VAIds = clusterer.cluster(this, iVAIdOriginal, 0, iVAIdStorage);

			toc = System.currentTimeMillis();
		}
		else {
			System.out.println("KMeans clustering ...");
			tic = System.currentTimeMillis();

			KMeansClusterer clusterer = new KMeansClusterer();
			VAIds = clusterer.cluster(this, iVAIdOriginal, 0, iVAIdStorage);

			toc = System.currentTimeMillis();
		}
		duration = (toc - tic) / 1000;
		System.out.println("cluster duration: ~" + duration + "sec");

		if (VAIds.size() != 2) {
			throw new IllegalStateException("Problems during clustering!!");
		}

		IVirtualArray virtualArray = getVA(VAIds.get(0));
		hashSetVAs.put(virtualArray.getID(), virtualArray);
		VAIds.add(VAIds.get(0));

		virtualArray = getVA(VAIds.get(1));
		hashSetVAs.put(virtualArray.getID(), virtualArray);
		VAIds.add(VAIds.get(1));

		return VAIds;
	}

	public void setClusteredGraph(CNode clusteredGraph) {
		this.clusteredGraph = clusteredGraph;
	}

	public CNode getClusteredGraph() {
		return clusteredGraph;
	}
}
