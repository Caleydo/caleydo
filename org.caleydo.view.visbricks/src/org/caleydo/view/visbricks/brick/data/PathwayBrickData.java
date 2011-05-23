package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.visbricks.brick.GLBrick;

public class PathwayBrickData implements IBrickData {

	private IDataDomain dataDomain;
	private ASetBasedDataDomain mappingDataDomain;
	private ContentVirtualArray contentVA;
	private PathwayGraph pathway;
	private Group group;
	private PathwayDimensionGroupData dimensionGroupData;

	public PathwayBrickData(IDataDomain dataDomain,
			ASetBasedDataDomain mappingDataDomain,
			ContentVirtualArray contentVA, Group group, PathwayGraph pathway,
			PathwayDimensionGroupData dimensionGroupData) {
		this.dataDomain = dataDomain;
		this.mappingDataDomain = mappingDataDomain;
		this.contentVA = contentVA;
		this.group = group;
		this.pathway = pathway;
		this.dimensionGroupData = dimensionGroupData;
	}

	@Override
	public IDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return dataDomain;
	}

	@Override
	public ContentVirtualArray getContentVA() {
		// TODO Auto-generated method stub
		return contentVA;
	}

	@Override
	public Group getGroup() {
		// TODO Auto-generated method stub
		return group;
	}

	@Override
	public void setBrickData(GLBrick brick) {
		brick.setDataDomain(mappingDataDomain);
		brick.setContentVA(group, contentVA);
	}

	@Override
	public String getLabel() {
		if(pathway != null)
			return pathway.getTitle();
		return "";
	}

	public PathwayGraph getPathway() {
		return pathway;
	}


}
