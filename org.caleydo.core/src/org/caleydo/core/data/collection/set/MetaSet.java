package org.caleydo.core.data.collection.set;

import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ContentData;

public class MetaSet
	extends Set
	implements ISet {

	ISet originalSet;

	// public MetaSet() {
	// init();
	// }

	@SuppressWarnings("unchecked")
	public MetaSet(Set originalSet, Tree<ClusterNode> storageTree, ClusterNode storageTreeRoot) {
		init();
		this.setSetType(originalSet.getSetType());
		this.isSetHomogeneous = originalSet.isSetHomogeneous();
		this.externalDataRep = originalSet.getExternalDataRep();
		this.hashContentData = (HashMap<ContentVAType, ContentData>) originalSet.hashContentData.clone();
		
		defaultStorageData.setStorageTree(storageTree);
		defaultStorageData.setStorageTreeRoot(storageTreeRoot);
		hashStorageData.put(StorageVAType.STORAGE, defaultStorageData.clone());
	}

	public ISet getOriginalSet() {
		return originalSet;
	}

	@Override
	public ContentVirtualArray getContentVA(ContentVAType vaType) {

		return super.getContentVA(vaType);
	}

}
