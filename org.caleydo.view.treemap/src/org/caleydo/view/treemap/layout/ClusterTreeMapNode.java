package org.caleydo.view.treemap.layout;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;

public class ClusterTreeMapNode extends ATreeMapNode {

	public static ClusterTreeMapNode createFromClusterNodeTree(Tree<ClusterNode> clusterTree, ColorMapping colorMapper) {
		return createFromClusterNodeTree(clusterTree.getRoot(), colorMapper);
	}

	public static ClusterTreeMapNode createFromClusterNodeTree(ClusterNode clusterNode, ColorMapping colorMapper) {

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

//			try {
//				referenz.sizeGetMehtod = clusterNode.getClass().getMethod("getSize", new Class[0]);
//			} catch (SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			createHelp(treemapNode, clusterNode, referenz);

			return treemapNode;
		}
		return null;
	}

	private static void createHelp(ClusterTreeMapNode treemapNode, ClusterNode clusterNode, ClusterReferenzData referenz) {
		if (clusterNode.getChildren() == null) {
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
			createHelp(treemapChild, clusterChild, referenz);
		}
	}

	ClusterReferenzData referenzData;
	ClusterNode data;


	public void setColorData(boolean bUseStorage, ASetBasedDataDomain dataDomain){
		referenzData.bUseExpressionValues=bUseStorage;
		referenzData.dataDomain=dataDomain;
	}
	
	@Override
	public float[] getColorAttribute() {
		if (referenzData.bUseExpressionValues) {
			StorageSelectionManager storageSelectionManager = referenzData.dataDomain.getStorageSelectionManager();
			// storageSelectionManager.setDelta()

			// // todo: colors for storages, this should be done somewhere else
			Set<Integer> storageIDs = storageSelectionManager.getElements(SelectionType.SELECTION);

			if (storageIDs != null && storageIDs.size() > 0) {
				float expressionValue = 0;
				for (Integer storageID : storageIDs) {
//					expressionValue += referenzData.dataDomain.getSet().get(storageID).get(EDataRepresentation.NORMALIZED, getLeafID()).floatValue();
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
