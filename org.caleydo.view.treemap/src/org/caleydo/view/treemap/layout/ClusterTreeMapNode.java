package org.caleydo.view.treemap.layout;

import java.util.Set;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;

/**
 * This class represents a the treemap model filled with a tree of ClusterNode
 * 
 * @author Michael Lafer
 *
 */

public class ClusterTreeMapNode extends ATreeMapNode {

	public static ClusterTreeMapNode createFromClusterNodeTree(Tree<ClusterNode> clusterTree, ColorMapping colorMapper, int maxDepth) {
		return createFromClusterNodeTree(clusterTree.getRoot(), colorMapper, maxDepth);
	}

	/**
	 * Creates a treemap model form a tree of ClusterNodes
	 * @param clusterNode root of ClusterNode tree
	 * @param colorMapper colomapper for mapping color attribute
	 * @param maxDepth maximal depth of the treemap model
	 * @return root of the treemap model
	 */
	public static ClusterTreeMapNode createFromClusterNodeTree(ClusterNode clusterNode, ColorMapping colorMapper, int maxDepth) {

		// ClusterNode clusterNode = clusterTree.getRoot();
		if (clusterNode != null) {
			Tree<ATreeMapNode> tree = new Tree<ATreeMapNode>();
			ClusterTreeMapNode treemapNode = new ClusterTreeMapNode();
			ClusterReferenzData referenz = new ClusterReferenzData();
			referenz.sizeReferenzValue = 0;
			referenz.colorMapper = colorMapper;
			tree.setRootNode(treemapNode);
			treemapNode.setTree(tree);
			treemapNode.data = clusterNode;
			treemapNode.referenzData = referenz;

			createHelp(treemapNode, clusterNode, referenz, maxDepth-1);

			return treemapNode;
		}
		return null;
	}

	private static void createHelp(ClusterTreeMapNode treemapNode, ClusterNode clusterNode, ClusterReferenzData referenz, int maxDepth) {
		if (clusterNode.getChildren() == null||maxDepth==0) {
			if(clusterNode.getChildren()!=null&&clusterNode.getChildren().size()>0)
				treemapNode.bIsAbstraction=true;
			
			referenz.sizeReferenzValue += clusterNode.getSize();

			referenz.colorMin = Math.min(referenz.colorMin, clusterNode.getAverageExpressionValue());
			referenz.colorMax = Math.max(referenz.colorMax, clusterNode.getAverageExpressionValue());

			return;
		}
		for (ClusterNode clusterChild : clusterNode.getChildren()) {
			ClusterTreeMapNode treemapChild = new ClusterTreeMapNode();
			treemapChild.data = clusterChild;
			treemapChild.tree = treemapNode.tree;
			treemapChild.referenzData = referenz;
			treemapNode.tree.addChild(treemapNode, treemapChild);
			createHelp(treemapChild, clusterChild, referenz, maxDepth-1);
		}
	}

	ClusterReferenzData referenzData;
	ClusterNode data;
	boolean bIsAbstraction=false;


	/**
	 * Switch between coloring average value or only from selected experiments.
	 * @param bUseStorage true when using only selected experiments.
	 * @param dataDomain Data for experiments.
	 */
	public void setColorData(boolean bUseStorage, ASetBasedDataDomain dataDomain){
		referenzData.bUseExpressionValues=bUseStorage;
		referenzData.dataDomain=dataDomain;
	}
	
	/**
	 * Returns different color depending how <code>setColorData</code> is used.
	 */
	@Override
	public float[] getColorAttribute() {
		// TODO check how to handle when node is not leave
		if (referenzData.bUseExpressionValues&& data.getLeafID()>=0) {
			StorageSelectionManager storageSelectionManager = referenzData.dataDomain.getStorageSelectionManager();
			Set<Integer> storageIDs = storageSelectionManager.getElements(SelectionType.SELECTION);
			if (storageIDs != null && storageIDs.size() > 0) {
				float expressionValue = 0;
				for (Integer storageID : storageIDs) {
					expressionValue += referenzData.dataDomain.getSet().get(storageID).getFloat(EDataRepresentation.NORMALIZED, data.getLeafID());
				}
				expressionValue /= storageIDs.size();
				return referenzData.colorMapper.getColor(expressionValue);
			}
		}
		
		return referenzData.colorMapper.getColor(data.getAverageExpressionValue() / referenzData.colorReferenzSpace);

	}

	@Override
	public float getSizeAttribute() {
		return data.getSize() / referenzData.sizeReferenzValue;
	}

	@Override
	public String getLabel() {
		if(bIsAbstraction)
			return data.getLabel()+"+";
		return data.getLabel();
	}

	@Override
	public Integer getID() {
		return data.getID();
	}

	public ClusterNode getData() {
		return data;
	}

	public void setData(ClusterNode data) {
		this.data = data;
	}
	


}
