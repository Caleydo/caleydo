package org.caleydo.core.data.collection.table;

import java.util.HashMap;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.table.statistics.StatisticsResult;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * <p>
 * A MetaSet is a set containing a sub-set of dimensions of a root set. Therefore, every MetaSet is associated
 * with a root set. The sub set is defined through the dimensionTree of a root set. The subset is defined by a
 * dimension tree and a ClusterNode (which is part of the tree). The metaSet manages all dimensions which are
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
	public SubDataTable(DataTable originalSet, ClusterTree dimensionTree, ClusterNode dimensionTreeRoot) {
		super();
		this.dataDomain = originalSet.getDataDomain();
		// init();

		this.uniqueID = GeneralManager.get().getIDCreator().createID(ManagedObjectType.DATA_TABLE);
		// this.setSetType(originalSet.getSetType());
		// FIXME: this is not always true, but if we create the MetaSet from the serialization, we didn't
		// check yet whether it was homogeneous
		this.isSetHomogeneous = true;
		this.externalDataRep = originalSet.getExternalDataRep();

		// this.hashContentData = (HashMap<String, ContentData>) originalSet.hashContentData.clone();
		this.hashContentData = (HashMap<String, RecordData>) originalSet.hashContentData.clone();

		this.hashDimensions = new HashMap<Integer, ADimension>();

		defaultDimensionData = new DimensionData();
		defaultDimensionData.setDimensionVA(new DimensionVirtualArray(DIMENSION));
		defaultDimensionData.setDimensionTree(dimensionTree);
		defaultDimensionData.setDimensionTreeRoot(dimensionTreeRoot);

		hashDimensionData = new HashMap<String, DimensionData>();
		hashDimensionData.put(DIMENSION, defaultDimensionData.clone());

		defaultDimensionData.setDimensionVA(new DimensionVirtualArray(DIMENSION));
		statisticsResult = new StatisticsResult(this);

		metaData = new MetaData(this);
		metaData.min = originalSet.metaData.min;
		metaData.max = originalSet.metaData.max;

	}

	public DataTable getOriginalSet() {
		return originalSet;
	}

}
