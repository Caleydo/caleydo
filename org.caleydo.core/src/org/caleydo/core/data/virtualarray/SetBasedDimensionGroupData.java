package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;

public class SetBasedDimensionGroupData extends ADimensionGroupData {

	private ATableBasedDataDomain dataDomain;
	private DataTable set;

	public SetBasedDimensionGroupData(ATableBasedDataDomain dataDomain, DataTable set) {
		this.dataDomain = dataDomain;
		this.set = set;
	}

	@Override
	public ContentVirtualArray getSummaryVA() {
		return set.getContentData(DataTable.RECORD).getContentVA();
	}

	@Override
	public ArrayList<ContentVirtualArray> getSegmentVAs() {
		ContentVirtualArray contentVA = set.getContentData(DataTable.RECORD)
				.getContentVA();

		if (contentVA.getGroupList() == null)
			return null;

		ContentGroupList groupList = contentVA.getGroupList();
		groupList.updateGroupInfo();

		ArrayList<ContentVirtualArray> segmentBrickVAs = new ArrayList<ContentVirtualArray>();

		for (Group group : groupList) {

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT",
					contentVA.getVirtualArray().subList(group.getStartIndex(),
							group.getEndIndex() + 1));
			segmentBrickVAs.add(subVA);
		}

		return segmentBrickVAs;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setSet(DataTable set) {
		this.set = set;
	}

	public DataTable getSet() {
		return set;
	}

	@Override
	public ArrayList<Group> getGroups() {
		ContentVirtualArray contentVA = set.getContentData(DataTable.RECORD)
				.getContentVA();

		if (contentVA.getGroupList() == null)
			return null;

		ContentGroupList groupList = contentVA.getGroupList();
		groupList.updateGroupInfo();

		return groupList.getGroups();
	}

	@Override
	public int getID() {
		return set.getID();
	}

	@Override
	public List<ISegmentData> getSegmentData() {

		ContentVirtualArray contentVA = set.getContentData(DataTable.RECORD)
				.getContentVA();

		if (contentVA.getGroupList() == null)
			return null;

		ContentGroupList groupList = contentVA.getGroupList();
		groupList.updateGroupInfo();

		List<ISegmentData> segmentBrickData = new ArrayList<ISegmentData>();

		for (Group group : groupList) {

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT",
					contentVA.getVirtualArray().subList(group.getStartIndex(),
							group.getEndIndex() + 1));
			segmentBrickData.add(new SetBasedSegmentData(dataDomain, set, subVA,
					group, this));
		}

		return segmentBrickData;
	}

	@Override
	public String getLabel() {
		return set.getLabel();
	}

}
