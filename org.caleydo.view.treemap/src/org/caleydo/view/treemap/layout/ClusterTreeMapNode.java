/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.treemap.layout;

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.mapping.ColorMapper;

/**
 * This class represents a the treemap model filled with a tree of ClusterNode
 *
 * @author Michael Lafer
 *
 */

public class ClusterTreeMapNode extends ATreeMapNode {

	public static ClusterTreeMapNode createFromClusterNodeTree(Tree<ClusterNode> clusterTree, ColorMapper colorMapper,
			int maxDepth) {
		return createFromClusterNodeTree(clusterTree.getRoot(), colorMapper, maxDepth);
	}

	/**
	 * Creates a treemap model form a tree of ClusterNodes
	 *
	 * @param clusterNode
	 *            root of ClusterNode tree
	 * @param colorMapper
	 *            colomapper for mapping color attribute
	 * @param maxDepth
	 *            maximal depth of the treemap model
	 * @return root of the treemap model
	 */
	public static ClusterTreeMapNode createFromClusterNodeTree(ClusterNode clusterNode, ColorMapper colorMapper,
			int maxDepth) {

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

			createHelp(treemapNode, clusterNode, referenz, maxDepth - 1);

			return treemapNode;
		}
		return null;
	}

	private static void createHelp(ClusterTreeMapNode treemapNode, ClusterNode clusterNode,
			ClusterReferenzData referenz, int maxDepth) {
		if (clusterNode.getChildren() == null || maxDepth == 0) {
			if (clusterNode.getChildren() != null && clusterNode.getChildren().size() > 0)
				treemapNode.bIsAbstraction = true;

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
			createHelp(treemapChild, clusterChild, referenz, maxDepth - 1);
		}
	}

	ClusterReferenzData referenzData;
	ClusterNode data;
	boolean bIsAbstraction = false;

	/**
	 * Switch between coloring average value or only from selected experiments.
	 *
	 * @param bUseDimension
	 *            true when using only selected experiments.
	 * @param dataDomain
	 *            Data for experiments.
	 */
	public void setColorData(boolean bUseDimension, ATableBasedDataDomain dataDomain) {
		referenzData.bUseExpressionValues = bUseDimension;
		referenzData.dataDomain = dataDomain;
	}

	/**
	 * Returns different color depending how <code>setColorData</code> is used.
	 */
	@Override
	public float[] getColorAttribute() {
		// TODO check how to handle when node is not leave
		if (referenzData.bUseExpressionValues && data.getLeafID() >= 0) {
			DimensionSelectionManager dimensionSelectionManager = referenzData.dataDomain
					.getDimensionSelectionManager();
			Set<Integer> dimensionIDs = dimensionSelectionManager.getElements(SelectionType.SELECTION);
			if (dimensionIDs != null && dimensionIDs.size() > 0) {
				float expressionValue = 0;
				for (Integer dimensionID : dimensionIDs) {
					expressionValue += referenzData.dataDomain.getTable().getNormalizedValue(dimensionID,
							data.getLeafID());
				}
				expressionValue /= dimensionIDs.size();
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
		if (bIsAbstraction)
			return data.getLabel() + "+";
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
