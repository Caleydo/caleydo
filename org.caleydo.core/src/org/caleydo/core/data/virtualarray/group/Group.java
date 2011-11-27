package org.caleydo.core.data.virtualarray.group;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;

/**
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class Group
	extends AUniqueObject {

	/** number of elements in the group/cluster */
	private int size = 0;

	/** The virtual array index of the first element of the group */
	private int startIndex = 0;

	/**
	 * The id of the group, also the index of the group in the group list. For a unique ID of the group use
	 * {@link #getID()}
	 */
	private Integer groupID = -1;

	/** Flag specifying whether this group is considered collapsed (i.e. the elements are not shown) */
	private boolean isCollapsed = false;

	/** index of the representative element in the VA */
	private int representativeElementIndex = -1;

	private SelectionType selectionType = SelectionType.NORMAL;

	/**
	 * In case of groups determined in dendrogram view the corresponding node in the tree must be stored for
	 * use in HHM
	 */
	private ClusterNode clusterNode;

	/**
	 * In some cases a perspective is created for a group, this is the corresponding ID.
	 */
	private String perspectiveID = null;

	/**
	 * array with mean expression values --> representative element
	 */
	private float[] meanValuesRepresentativeElement;

	private int visibleNrGenes = 0;

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
	}

	/**
	 * Constructor
	 * 
	 * @param size
	 *            the size of the group
	 * @param representativeElementIndex
	 *            the index of an element considered to be representative of this group (i.e. the most typical
	 *            element)
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
	 *            the index of an element considered to be representative of this group (i.e. the most typical
	 *            element)
	 * @param clusterNode
	 *            a cluster node from which the pre-order of the leaves is this group
	 */
	public Group(int size, int representativeElementIndex, ClusterNode clusterNode) {
		this.setSize(size);
		this.setRepresentativeElementIndex(representativeElementIndex);
		this.setClusterNode(clusterNode);
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	/**
	 * @return the groupID, see {@link #groupID}
	 */
	public Integer getGroupID() {
		return groupID;
	}

	/**
	 * @param groupID
	 *            setter, see {@link #groupID}
	 */
	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
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
		this.selectionType =
			(selectionType == SelectionType.SELECTION) ? SelectionType.NORMAL : SelectionType.SELECTION;
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
		return "Group: " + groupID + ", size: " + size;
	}

	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	public String getPerspectiveID() {
		return perspectiveID;
	}
}
