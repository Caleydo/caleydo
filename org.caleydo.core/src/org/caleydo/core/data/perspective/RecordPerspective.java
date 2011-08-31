package org.caleydo.core.data.perspective;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.RecordFilterManager;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;

/**
 * Fixme: consolidate this and DimensionData
 * 
 * @author Alexander Lex
 */
@XmlRootElement
public class RecordPerspective
	extends DataPerspective<RecordVirtualArray, RecordGroupList, RecordVADelta, RecordFilterManager> {

	private static int recordDataRunningNumber = 0;

	public RecordPerspective() {
	}

	public RecordPerspective(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	protected void init() {
		perspectiveID = "RecordPerspective_" + recordDataRunningNumber;
		recordDataRunningNumber++;
		filterManager = new RecordFilterManager(dataDomain, this);
		idType = dataDomain.getRecordIDType();
	}

	@Override
	protected RecordGroupList createGroupList() {
		return new RecordGroupList();
	}

	@Override
	protected RecordVirtualArray newConcreteVirtualArray(List<Integer> indexList) {
		return new RecordVirtualArray(perspectiveID, indexList);
	}

	@Override
	protected String getLabel(Integer id) {
		return dataDomain.getRecordLabel(id);
	}

	// ATableBasedDataDomain dataDomain;
	//
	// RecordVirtualArray recordVA;
	// /** indices of examples (cluster centers) */
	// ArrayList<Integer> contentSampleElements;
	// /** number of elements per cluster */
	// ArrayList<Integer> contentClusterSizes;
	//
	// ClusterTree contentTree;
	//
	// RecordFilterManager recordFilterManager = new RecordFilterManager(dataDomain);
	//
	// boolean isPartitionallyClustered = false;
	//
	// IDType recordIDType;
	//
	// // public ContentData() {
	// // }
	//
	// public RecordData(ATableBasedDataDomain dataDomain) {
	// this.dataDomain = dataDomain;
	// this.recordIDType = dataDomain.getRecordIDType();
	// }
	//
	// public void setContentIDIdType(IDType recordIDIdType) {
	// this.recordIDType = recordIDIdType;
	// }
	//
	// public RecordVirtualArray getRecordVA() {
	// return recordVA;
	// }
	//
	// public void setRecordVA(RecordVirtualArray recordVA) {
	// this.recordVA = recordVA;
	// }
	//
	// public ArrayList<Integer> getContentSampleElements() {
	// return contentSampleElements;
	// }
	//
	// public void setRecordSampleElements(ArrayList<Integer> contentSampleElements) {
	// this.contentSampleElements = contentSampleElements;
	// }
	//
	// public ArrayList<Integer> getContentClusterSizes() {
	// return contentClusterSizes;
	// }
	//
	// public void setRecordClusterSizes(ArrayList<Integer> contentClusterSizes) {
	// this.contentClusterSizes = contentClusterSizes;
	// }
	//
	// public Tree<ClusterNode> getRecordTree() {
	// return contentTree;
	// }
	//
	// public void setRecordTree(ClusterTree contentTree) {
	// this.contentTree = contentTree;
	// }
	//
	// public void reset() {
	// contentClusterSizes = null;
	// contentSampleElements = null;
	// if (contentTree != null)
	// contentTree.destroy();
	// contentTree = null;
	// }
	//
	// /**
	// * Sets the delta to the virtual array and resets other related data (groups, trees) accordingly.
	// *
	// * @param delta
	// */
	// public void setVADelta(RecordVADelta delta) {
	// recordVA.setDelta(delta);
	// if (contentTree != null)
	// contentTree.destroy();
	// contentTree = null;
	// recordVA.setGroupList(null);
	// }
	//
	// public void finish() {
	// // calculate the group list based on contentClusterSizes (for example for affinity propagation
	// if (recordVA != null && contentClusterSizes != null && contentSampleElements != null) {
	// isPartitionallyClustered = true;
	// RecordGroupList contentGroupList = new RecordGroupList();
	//
	// int cnt = 0;
	// // int iOffset = 0;
	// contentTree = new ClusterTree(recordIDType);
	// int clusterNr = 0;
	// ClusterNode root = new ClusterNode(contentTree, "Root", clusterNr++, true, -1);
	// contentTree.setRootNode(root);
	// ClusterNode node;
	// int from = 0;
	// int to = 0;
	// for (Integer clusterSize : contentClusterSizes) {
	//
	// node = new ClusterNode(contentTree, "Group: " + clusterNr, clusterNr++, true, -1);
	// Group temp = new Group(clusterSize, contentSampleElements.get(cnt), node);
	// contentTree.addChild(root, node);
	// contentGroupList.append(temp);
	// cnt++;
	// // iOffset += iter;
	// to += clusterSize;
	// ClusterNode leaf;
	// for (int vaIndex = from; vaIndex < to; vaIndex++) {
	// Integer recordID = recordVA.get(vaIndex);
	// leaf = new ClusterNode(contentTree, "Leaf: " + recordID, clusterNr++, true, recordID);
	// contentTree.addChild(node, leaf);
	// }
	// from = to;
	//
	// }
	//
	// recordVA.setGroupList(contentGroupList);
	// }
	// // calculate the group list based on the tree's first level
	// else if (recordVA != null && contentTree != null) {
	// recordVA.buildNewGroupList(contentTree.getRoot().getChildren());
	// }
	// }
	//
	// public void updateVABasedOnSortingStrategy() {
	// // ContentGroupList groupList = recordVA.getGroupList();
	//
	// recordVA = new RecordVirtualArray(DataTable.RECORD, contentTree.getRoot().getLeaveIds());
	// recordVA.buildNewGroupList(contentTree.getRoot().getChildren());
	// // recordVA.setGroupList(groupList);
	// }
	//
	// public boolean isPartitionallyClustered() {
	// return isPartitionallyClustered;
	// }

}
