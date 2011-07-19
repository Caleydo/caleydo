package org.caleydo.datadomain.pathway.data;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.ISegmentData;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

public class PathwaySegmentData implements ISegmentData {

	private IDataDomain dataDomain;
	private ATableBasedDataDomain mappingDataDomain;
	private ContentVirtualArray contentVA;
	private PathwayGraph pathway;
	private Group group;
	private PathwayDimensionGroupData dimensionGroupData;

	public PathwaySegmentData(IDataDomain dataDomain,
			ATableBasedDataDomain mappingDataDomain,
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
	public String getLabel() {
		if(pathway != null)
			return pathway.getTitle();
		return "";
	}

	public PathwayGraph getPathway() {
		return pathway;
	}

	public ATableBasedDataDomain getMappingDataDomain() {
		return mappingDataDomain;
	}


}
