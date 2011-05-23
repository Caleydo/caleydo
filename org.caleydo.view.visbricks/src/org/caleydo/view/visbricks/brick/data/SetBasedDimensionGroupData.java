package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.ESetDataType;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.layout.ASetBasedDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.NominalDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.NumericalDataConfigurer;

public class SetBasedDimensionGroupData implements IDimensionGroupData {

	private ASetBasedDataDomain dataDomain;
	private ISet set;
	private ASetBasedDataConfigurer setBasedDataConfigurer;

	public SetBasedDimensionGroupData(ASetBasedDataDomain dataDomain, ISet set) {
		this.dataDomain = dataDomain;
		this.set = set;
		if (set.getSetType().equals(ESetDataType.NUMERIC)) {
			setBasedDataConfigurer =  new NumericalDataConfigurer(set);
		} else {
			setBasedDataConfigurer =  new NominalDataConfigurer(set);
		}
	}

	@Override
	public ContentVirtualArray getSummaryBrickVA() {
		return set.getContentData(Set.CONTENT).getContentVA();
	}

	@Override
	public ArrayList<ContentVirtualArray> getSegmentBrickVAs() {
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT)
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

	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public IBrickConfigurer getBrickConfigurer() {
		return setBasedDataConfigurer;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public ISet getSet() {
		return set;
	}

	@Override
	public ArrayList<Group> getGroups() {
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT)
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
	public List<IBrickData> getSegmentBrickData() {

		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT)
				.getContentVA();

		if (contentVA.getGroupList() == null)
			return null;

		ContentGroupList groupList = contentVA.getGroupList();
		groupList.updateGroupInfo();

		List<IBrickData> segmentBrickData = new ArrayList<IBrickData>();

		for (Group group : groupList) {

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT",
					contentVA.getVirtualArray().subList(group.getStartIndex(),
							group.getEndIndex() + 1));
			segmentBrickData.add(new SetBasedBrickData(dataDomain, set, subVA,
					group, this));
		}

		return segmentBrickData;
	}

	@Override
	public IBrickData getSummaryBrickData() {
		return new SetBasedBrickData(dataDomain, set, getSummaryBrickVA(),
				new Group(), this);
	}

	@Override
	public IBrickSortingStrategy getDefaultSortingStrategy() {
		return new AverageValueSortingStrategy();
	}

}
