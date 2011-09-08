package org.caleydo.core.data.container;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;

/**
 * Implementation of {@link ADimensionGroupData} for table based data sets ({@link ATableBasedDataDomain}. In
 * this case a dimension group refers to a group of columns in the data table.
 * 
 * @author Partl
 * @author Alexander Lex
 */
public class TableBasedDimensionGroupData
	extends ADimensionGroupData {

	String label;

	/**
	 * Creates a new {@link TableBasedDimensionGroupData} object with a new dataPerspective class. The new
	 * dataPerspective is created using the clusterNode (all leaves of the clusterNode eg are in the VA of the
	 * new perspective). Since nodes and trees are independent of the perspective's data type, we need the
	 * dataPersperctivClass parameter to tell us which perspective should be created.
	 * 
	 * @param dataDomain
	 * @param recordPerspective
	 * @param dimensionPerspective
	 * @param rootNode
	 * @param dataPerspectiveClass
	 *            the class type of the newly generated perspective
	 */
	public TableBasedDimensionGroupData(ATableBasedDataDomain dataDomain,
		RecordPerspective recordPerspective, DimensionPerspective dimensionPerspective, ClusterNode rootNode,
		Class<? extends ADataPerspective<?, ?, ?, ?>> dataPerspectiveClass) {
		super(dataDomain, recordPerspective, dimensionPerspective);

		label = rootNode.getLabel();
		if (dataPerspectiveClass.equals(RecordPerspective.class)) {
			this.recordPerspective = new RecordPerspective();
			this.recordPerspective.createVA(rootNode.getLeaveIds());
			this.recordPerspective.setTree((ClusterTree) rootNode.getTree());
			this.recordPerspective.setTreeRoot(rootNode);
			this.recordPerspective.finish();
			dataDomain.getTable().registerRecordPerspecive(recordPerspective);

		}
		else if (dataPerspectiveClass.equals(DimensionPerspective.class)) {
			this.dimensionPerspective = new DimensionPerspective(dataDomain);
			this.dimensionPerspective.createVA(rootNode.getLeaveIds());
			this.dimensionPerspective.setTree((ClusterTree) rootNode.getTree());
			this.dimensionPerspective.setTreeRoot(rootNode);
			this.dimensionPerspective.finish();
			dataDomain.getTable().registerDimensionPerspective(this.dimensionPerspective);
		}
		else {
			throw new IllegalStateException("Unknown type of " + dataPerspectiveClass);
		}

	}

	@Override
	public RecordVirtualArray getSummaryVA() {
		return recordPerspective.getVirtualArray();
	}

	@Override
	public ArrayList<RecordVirtualArray> getSegmentVAs() {
		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		ArrayList<RecordVirtualArray> segmentBrickVAs = new ArrayList<RecordVirtualArray>();

		for (Group group : groupList) {

			RecordVirtualArray subVA =
				new RecordVirtualArray("CONTENT", recordVA.getVirtualArray().subList(group.getStartIndex(),
					group.getEndIndex() + 1));
			segmentBrickVAs.add(subVA);
		}

		return segmentBrickVAs;
	}

	@Override
	public ArrayList<Group> getGroups() {
		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		return groupList.getGroups();
	}


	@Override
	public List<ISegmentData> getSegmentData() {

		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		List<ISegmentData> segmentBrickData = new ArrayList<ISegmentData>();

		for (Group group : groupList) {

			List<Integer> indices =
				 recordVA.getVirtualArray().subList(group.getStartIndex(),
					group.getEndIndex() + 1);

			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.createVA(indices);

			segmentBrickData.add(new TableBasedSegmentData(dataDomain, recordPerspective,
				dimensionPerspective, group, this));

		}
		return segmentBrickData;
	}

	/**
	 * @return the {@link RecordPerspective} for this dimension group
	 */
	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	/**
	 * @return the {@link RecordPerspective} for this dimension group
	 */
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public ISegmentData getSummarySegementData() {
		ISegmentData tempSegmentData =
			new TableBasedSegmentData(dataDomain, recordPerspective, dimensionPerspective, new Group(), this);

		return tempSegmentData;
	}

}
