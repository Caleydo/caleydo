package org.caleydo.core.data.container;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;

/**
 * Holds different information about a dimension group. A {@link IDataDomain} can have several
 * {@link ADimensionGroupData}. A dimension group can be partitioned into segment groups ({@link Group}) with
 * {@link ISegmentData} providing additional information about them.
 * 
 * @author Partl
 * @author Alexander Lex
 */
public abstract class ADimensionGroupData
	extends ADataContainer {

	public ADimensionGroupData() {
	}

	public ADimensionGroupData(ATableBasedDataDomain dataDomain, RecordPerspective recordPerspective,
		DimensionPerspective dimensionPerspective) {
		super(dataDomain, recordPerspective, dimensionPerspective);
	}

	/**
	 * @return The recordVA of the whole dimension group.
	 */
	public abstract RecordVirtualArray getSummaryVA();

	/**
	 * @return A list of recordVAs that correspond to the segments of the dimension group.
	 */
	public abstract ArrayList<RecordVirtualArray> getSegmentVAs();

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
