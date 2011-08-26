package org.caleydo.core.data.container;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.DataPerspective;
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

	/**
	 * Constructor that creates a new object with the specified dimensionPerspective and recordPerspective
	 * 
	 * @param dataDomain
	 * @param dimensionPerspectiveID
	 * @param recordPerspectiveID
	 */
	public TableBasedDimensionGroupData(ATableBasedDataDomain dataDomain, String dimensionPerspectiveID,
		String recordPerspectiveID) {
		super(dataDomain, recordPerspectiveID, dimensionPerspectiveID);
	}

	/**
	 * Creates a new {@link TableBasedDimensionGroupData} object with a new dataPerspective
	 * 
	 * @param dataDomain
	 * @param dimensionPerspectiveID
	 * @param recordPerspectiveID
	 * @param rootNode
	 */
	public TableBasedDimensionGroupData(ATableBasedDataDomain dataDomain, String dimensionPerspectiveID,
		String recordPerspectiveID, ClusterNode rootNode,
		Class<? extends DataPerspective<?, ?, ?, ?>> dataPerspectiveClass) {
		this.dataDomain = dataDomain;

		if (dataPerspectiveClass.equals(RecordPerspective.class)) {
			RecordPerspective perspective = new RecordPerspective();
			perspective.createVA(rootNode.getLeaveIds());
			perspective.setTree((ClusterTree) rootNode.getTree());
			perspective.setTreeRoot(rootNode);
			perspective.finish();
			dataDomain.getTable().registerRecordPerspecive(perspective);
			this.recordPerspectiveID = perspective.getPerspectiveID();
			this.dimensionPerspectiveID = dimensionPerspectiveID;
		}
		else if (dataPerspectiveClass.equals(DimensionPerspective.class)) {
			DimensionPerspective perspective = new DimensionPerspective(dataDomain);
			perspective.createVA(rootNode.getLeaveIds());
			perspective.setTree((ClusterTree) rootNode.getTree());
			perspective.setTreeRoot(rootNode);
			perspective.finish();
			dataDomain.getTable().registerDimensionPerspective(perspective);
			this.dimensionPerspectiveID = perspective.getPerspectiveID();
			this.recordPerspectiveID = recordPerspectiveID;
		}
		else {
			throw new IllegalStateException("Unknown type of " + dataPerspectiveClass);
		}

	}

	@Override
	public RecordVirtualArray getSummaryVA() {
		return dataDomain.getTable().getRecordPerspective(recordPerspectiveID).getVirtualArray();
	}

	@Override
	public ArrayList<RecordVirtualArray> getSegmentVAs() {
		RecordVirtualArray recordVA =
			dataDomain.getTable().getRecordPerspective(recordPerspectiveID).getVirtualArray();

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
		RecordVirtualArray recordVA =
			dataDomain.getTable().getRecordPerspective(recordPerspectiveID).getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		return groupList.getGroups();
	}

	@Override
	public int getID() {
		// TODO this is probably no longer the ID we want
		return dataDomain.getTable().getID();
	}

	@Override
	public List<ISegmentData> getSegmentData() {

		RecordVirtualArray recordVA =
			dataDomain.getTable().getRecordPerspective(recordPerspectiveID).getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		List<ISegmentData> segmentBrickData = new ArrayList<ISegmentData>();

		for (Group group : groupList) {

			ArrayList<Integer> indices =
				(ArrayList<Integer>) recordVA.getVirtualArray().subList(group.getStartIndex(),
					group.getEndIndex() + 1);

			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.createVA(indices);

			segmentBrickData.add(new TableBasedSegmentData(dataDomain, recordPerspective, dataDomain
				.getTable().getDimensionPerspective(dimensionPerspectiveID), group, this));

		}
		return segmentBrickData;
	}

	/**
	 * @return the {@link RecordPerspective} for this dimension group
	 */
	public RecordPerspective getRecordPerspective() {
		return dataDomain.getTable().getRecordPerspective(recordPerspectiveID);
	}

	/**
	 * @return the {@link RecordPerspective} for this dimension group
	 */
	public DimensionPerspective getDimensionPerspective() {
		return dataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID);
	}

	@Override
	public String getLabel() {
		// TODO: Probably not the label we want
		return dataDomain.getTable().getLabel();
	}

}
