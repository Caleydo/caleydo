/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.graph.tree;

import gleem.linalg.Vec3f;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionType;

/**
 * Implementation of a node used in the cluster tree. Cluster node contains information needed in the
 * dendrogram and the radial hierarchy view. Additionally cluster node implements {@link Comparable}.
 *
 * @author Bernhard Schlegl
 * @author Christian Partl
 * @author Alexander Lex
 */
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterNode
	extends AHierarchyElement<ClusterNode>
	implements Comparable<ClusterNode> {

	@XmlElement
	private Vec3f vPos;
	@XmlElement
	private SelectionType selectionType;
	@XmlElement
	private boolean isRootNode;
	@XmlElement
	private float averageExpressionValue;
	@XmlElement
	private float uncertainty;
	@XmlElement
	private float standardDeviation;
	@XmlTransient
	private boolean isPartOfSubTree = false;
	private Vec3f vPosSubTree;

	private Perspective perspective;

	public ClusterNode() {
	}

	/**
	 * Constructor for a cluster node.
	 *
	 * @param tree
	 *            The tree the clusternode belongs to
	 * @param nodeName
	 *            the name of the node
	 * @param clusterNr
	 *            the id, the cluster number
	 * @param isRootNode
	 *            true if this is a root node
	 * @param leafID
	 *            the id of the leaf, or -1 if this is not a leaf
	 */
	public ClusterNode(ClusterTree tree, String label, int clusterNr, boolean isRootNode, int leafID) {
		super(tree);
		this.label = label;
		this.id = clusterNr;
		super.setLeafID(leafID);
		this.isRootNode = isRootNode;
		this.selectionType = SelectionType.NORMAL;
		this.averageExpressionValue = 0f;
		this.standardDeviation = 0f;

	}

	@SuppressWarnings("unchecked")
	public <PerspectiveType extends Perspective> PerspectiveType getSubPerspective(
		Class<PerspectiveType> concreteClass, ATableBasedDataDomain dataDomain) {
		if (perspective != null)
			return (PerspectiveType) perspective;
		try {
			perspective = concreteClass.newInstance();
		}
		catch (Exception e) {

			e.printStackTrace();
			throw new IllegalStateException("Could not instantiate class");
		}

		perspective.setDataDomain(dataDomain);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData((ClusterTree) getTree(), this);
		perspective.init(data);
		return (PerspectiveType) perspective;
	}

	@Override
	public String toString() {
		return label;
	}

	public void setPos(Vec3f vPos) {
		this.vPos = vPos;
	}

	public Vec3f getPos() {
		return vPos;
	}

	public void setSelectionType(SelectionType SelectionType) {
		this.selectionType = SelectionType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	public void togglSelectionType() {
		this.selectionType =
			(selectionType == SelectionType.SELECTION) ? SelectionType.NORMAL : SelectionType.SELECTION;
	}

	public boolean isRootNode() {
		return isRootNode;
	}

	public void setAverageExpressionValue(float fAverageExpressionValue) {
		this.averageExpressionValue = fAverageExpressionValue;
	}

	public float getAverageExpressionValue() {
		return averageExpressionValue;
	}

	public void setStandardDeviation(float fStandardDeviation) {
		this.standardDeviation = fStandardDeviation;
	}

	public float getStandardDeviation() {
		return standardDeviation;
	}

	public void setIsPartOfSubTree(boolean bIsPartOfSubTree) {
		this.isPartOfSubTree = bIsPartOfSubTree;
	}

	public boolean isPartOfSubTree() {
		return isPartOfSubTree;
	}

	public void setPosSubTree(Vec3f vPosSubTree) {
		this.vPosSubTree = vPosSubTree;
	}

	public Vec3f getPosSubTree() {
		return vPosSubTree;
	}

	public void setUncertainty(float uncertainty) {
		this.uncertainty = uncertainty;
	}

	@Override
	public int getComparableValue() {

		// FIXME: is it ok that the ID is null?
		if (id == null)
			return 0;

		return id;
	}

	@Override
	public int compareTo(ClusterNode node) {
		ESortingStrategy strategy = tree.getSortingStrategy();

		switch (strategy) {
			case AVERAGE_VALUE:
				if (averageExpressionValue < node.averageExpressionValue)
					return 1;
				else
					return -1;
			case CERTAINTY:
				if (uncertainty < node.uncertainty)
					return 1;
				else
					return -1;
			case DEFAULT:
			default:
				return super.compareTo(node);
		}

	}
}
