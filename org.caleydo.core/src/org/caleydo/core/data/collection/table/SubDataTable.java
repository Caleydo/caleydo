package org.caleydo.core.data.collection.table;

import java.util.HashMap;

import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.table.statistics.StatisticsResult;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * <p>
 * A MetaSet is a set containing a sub-set of storages of a root set. Therefore, every MetaSet is associated
 * with a root set. The sub set is defined through the storageTree of a root set. The subset is defined by a
 * storage tree and a ClusterNode (which is part of the tree). The metaSet manages all storages which are
 * below or at the level of the ClusterNode.
 * </p>
 * <p>
 * Other properties of the MetaSet, such as the contentData is shared with the original data.
 * </p>
 * 
 * @author Alexander Lex
 */
public class SubDataTable
	extends DataTable {

	DataTable originalSet;

	// public MetaSet() {
	// init();
	// }

	@SuppressWarnings("unchecked")
	public SubDataTable(DataTable originalSet, ClusterTree storageTree, ClusterNode storageTreeRoot) {
		super();
		this.dataDomain = originalSet.getDataDomain();
		// init();

		this.uniqueID = GeneralManager.get().getIDCreator().createID(ManagedObjectType.SET);
		// this.setSetType(originalSet.getSetType());
		// FIXME: this is not always true, but if we create the MetaSet from the serialization, we didn't
		// check yet whether it was homogeneous
		this.isSetHomogeneous = true;
		this.externalDataRep = originalSet.getExternalDataRep();

		// this.hashContentData = (HashMap<String, ContentData>) originalSet.hashContentData.clone();
		this.hashContentData = (HashMap<String, ContentData>) originalSet.hashContentData.clone();

		this.hashStorages = new HashMap<Integer, AStorage>();

		defaultStorageData = new StorageData();
		defaultStorageData.setStorageVA(new StorageVirtualArray(STORAGE));
		defaultStorageData.setStorageTree(storageTree);
		defaultStorageData.setStorageTreeRoot(storageTreeRoot);

		hashStorageData = new HashMap<String, StorageData>();
		hashStorageData.put(STORAGE, defaultStorageData.clone());

		defaultStorageData.setStorageVA(new StorageVirtualArray(STORAGE));
		statisticsResult = new StatisticsResult(this);

		metaData = new MetaData(this);

	}

	public DataTable getOriginalSet() {
		return originalSet;
	}

}
