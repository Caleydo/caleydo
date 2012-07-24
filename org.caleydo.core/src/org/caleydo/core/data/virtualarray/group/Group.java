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
package org.caleydo.core.data.virtualarray.group;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.base.AUniqueObject;
import org.caleydo.core.util.base.ILabelHolder;

/**
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class Group extends AUniqueObject implements ILabelHolder {

	/** number of elements in the group/cluster */
	private int size = 0;

	/** The virtual array index of the first element of the group */
	private int startIndex = 0;

	/**
	 * The index of the group in the group list. For a unique ID of the group
	 * use {@link #getID()}
	 */
	private Integer groupIndex = -1;

	/**
	 * Flag specifying whether this group is considered collapsed (i.e. the
	 * elements are not shown)
	 */
	private boolean isCollapsed = false;

	/** index of the representative element in the VA */
	private int representativeElementIndex = -1;

	private SelectionType selectionType = SelectionType.NORMAL;

	/**
	 * In case of groups determined in dendrogram view the corresponding node in
	 * the tree must be stored for use in HHM
	 */
	private ClusterNode clusterNode;

	/**
	 * In some cases a perspective is created for a group, this is the
	 * corresponding ID.
	 */
	private String perspectiveID = null;

	/**
	 * array with mean expression values --> representative element
	 */
	private float[] meanValuesRepresentativeElement;

	@XmlElement
	private String label;
	@XmlElement
	private boolean isDefaultLabel = true;

	/**
	 * Default Constructor
	 */
	public Group() {
	}

	public Group(int size) {
		this.setSize(size);
	}

	{
		uniqueID = GeneralManager.get().getIDCreator().createID(ManagedObjectType.GROUP);
		label = "Group " + uniqueID;
	}

	/**
	 * Constructor
	 * 
	 * @param size
	 *            the size of the group
	 * @param representativeElementIndex
	 *            the index of an element considered to be representative of
	 *            this group (i.e. the most typical element)
	 */
	public Group(int size, int representativeElementIndex) {
		this.setSize(size);
		this.setRepresentativeElementIndex(representativeElementIndex);
	}

	/**
	 * Constructor
	 * 
	 * @param size
	 *            the size of the group
	 * @param representativeElementIndex
	 *            the index of an element considered to be representative of
	 *            this group (i.e. the most typical element)
	 * @param clusterNode
	 *            a cluster node from which the pre-order of the leaves is this
	 *            group
	 */
	public Group(int size, int representativeElementIndex, ClusterNode clusterNode) {
		this.setSize(size);
		this.setRepresentativeElementIndex(representativeElementIndex);
		this.setClusterNode(clusterNode);
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @return the isDefaultLabel, see {@link #isDefaultLabel}
	 */
	public boolean isLabelDefault() {
		return isDefaultLabel;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	/**
	 * @return the groupIndex, see {@link #groupIndex}
	 */
	public Integer getGroupIndex() {
		return groupIndex;
	}

	/**
	 * @param groupIndex
	 *            setter, see {@link #groupIndex}
	 */
	public void setGroupIndex(Integer groupIndex) {
		this.groupIndex = groupIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return startIndex + size - 1;
	}

	public void setCollapsed(boolean bCollapsed) {
		this.isCollapsed = bCollapsed;
	}

	public boolean isCollapsed() {
		return isCollapsed;
	}

	public void setRepresentativeElementIndex(int representativeElementIndex) {
		this.representativeElementIndex = representativeElementIndex;
	}

	public int getRepresentativeElementIndex() {
		return representativeElementIndex;
	}

	public void setSelectionType(SelectionType SelectionType) {
		this.selectionType = SelectionType;
	}

	@XmlTransient
	public SelectionType getSelectionType() {
		return selectionType;
	}

	public void togglSelectionType() {
		this.selectionType = (selectionType == SelectionType.SELECTION) ? SelectionType.NORMAL
				: SelectionType.SELECTION;
	}

	public void setClusterNode(ClusterNode clusterNode) {
		this.clusterNode = clusterNode;
	}

	public ClusterNode getClusterNode() {
		return clusterNode;
	}

	public void setRepresentativeElement(float[] fArRepresentativeElement) {
		this.meanValuesRepresentativeElement = fArRepresentativeElement;
	}

	public float[] getRepresentativeElement() {
		return meanValuesRepresentativeElement;
	}

	@Override
	public String toString() {
		return "Group: " + groupIndex + ", size: " + size;
	}

	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	public String getPerspectiveID() {
		return perspectiveID;
	}


	@Override
	public void setLabel(String label, boolean isDefaultLabel) {
		this.label = label;
		this.isDefaultLabel = isDefaultLabel;

	}
}
