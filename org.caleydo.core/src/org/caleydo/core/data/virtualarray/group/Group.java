/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.group;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.util.base.IDefaultLabelHolder;
import org.caleydo.core.util.base.IUniqueObject;

/**
 * @author Bernhard Schlegl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class Group implements IDefaultLabelHolder, IUniqueObject {
	/** unique ID */
	@XmlTransient
	private int id = IDCreator.createVMUniqueID(Group.class);

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


	private String label = "Group " + id;
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

	/**
	 * Initialize a clone of a group with the same size, ID and label. Start index may vary.
	 *
	 * TODO: this is not synchronous for changes
	 *
	 * @param clone
	 */
	public Group(Group clone) {
		this.id = clone.id;
		this.size = clone.size;
		this.label = clone.label;
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
	@Override
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

	public void toggleSelectionType() {
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
		return "Group: " + groupIndex + ", size: " + size + ", id:" + id;
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

	@Override
	public String getProviderName() {
		return "Group";
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	@Override
	public int getID() {
		return id;
	}
}
