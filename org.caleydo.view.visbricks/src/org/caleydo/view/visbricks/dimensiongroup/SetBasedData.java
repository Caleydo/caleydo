package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.ESetDataType;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.NominalDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.NumericalDataConfigurer;

public class SetBasedData implements IDimensionGroupData {
	
	private IDataDomain dataDomain;
	private ISet set;
	
	public SetBasedData(IDataDomain dataDomain, ISet set) {
		this.dataDomain = dataDomain;
		this.set = set;

	}
	
	public void update() {
		
	}
	

	@Override
	public ContentVirtualArray getSummaryBrickVA() {
		return set.getContentData(Set.CONTENT).getContentVA();
	}

	@Override
	public ArrayList<ContentVirtualArray> getSegmentBrickVAs() {
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();

		if (contentVA.getGroupList() == null)
			return null;

		ContentGroupList groupList = contentVA.getGroupList();
		groupList.updateGroupInfo();
		
		ArrayList<ContentVirtualArray> segmentBrickVAs = new ArrayList<ContentVirtualArray>();

		for (Group group : groupList) {

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT", contentVA
					.getVirtualArray().subList(group.getStartIndex(),
							group.getEndIndex() + 1));
			segmentBrickVAs.add(subVA);
		}
		
		return segmentBrickVAs;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}
	
	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public IBrickConfigurer getBrickConfigurer() {

		if (set.getSetType().equals(ESetDataType.NUMERIC)) {
			return new NumericalDataConfigurer();
		} else {
			return new NominalDataConfigurer();
		}
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public ISet getSet() {
		return set;
	}

	@Override
	public ArrayList<Group> getGroups() {
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();

		if (contentVA.getGroupList() == null)
			return null;

		ContentGroupList groupList = contentVA.getGroupList();
		groupList.updateGroupInfo();
		
		return groupList.getGroups();
	}


}
