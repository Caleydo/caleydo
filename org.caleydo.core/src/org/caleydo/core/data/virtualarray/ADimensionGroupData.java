package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;

/**
 * Holds different information about a dimension group. A {@link IDataDomain} can have several
 * {@link ADimensionGroupData}. A dimension group can be partitioned into segment groups ({@link Group}) with
 * {@link ISegmentData} providing additional information about them.
 * 
 * @author Partl
 */
public abstract class ADimensionGroupData {

	/**
	 * @return The recordVA of the whole dimension group.
	 */
	public abstract RecordVirtualArray getSummaryVA();

	/**
	 * @return A list of recordVAs that correspond to the segments of the dimension group.
	 */
	public abstract ArrayList<RecordVirtualArray> getSegmentVAs();

	/**
	 * @return The data domain this dimesion group belongs to.
	 */
	public abstract IDataDomain getDataDomain();

	/**
	 * @return The groups the dimension group is partitioned in.
	 */
	public abstract ArrayList<Group> getGroups();

	/**
	 * @return ID of the dimension group.
	 */
	public abstract int getID();

	/**
	 * @return List of segment data.
	 */
	public abstract List<ISegmentData> getSegmentData();

	/**
	 * @return Label of the dimension group.
	 */
	public abstract String getLabel();

}
