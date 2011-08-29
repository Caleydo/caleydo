package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;
import org.caleydo.datadomain.pathway.data.PathwaySegmentData;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.visbricks.brick.GLBrick;

public class PathwayBrickData extends PathwaySegmentData implements IBrickData {

	public PathwayBrickData(ATableBasedDataDomain dataDomain,
			PathwayDataDomain pathwayDataDomain, RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, Group group, PathwayGraph pathway,
			PathwayDimensionGroupData dimensionGroupData) {
		super(dataDomain, pathwayDataDomain, recordPerspective, dimensionPerspective,
				group, pathway, dimensionGroupData);
	}

	@Override
	public RecordVirtualArray getRecordVA() {
		return recordPerspective.getVirtualArray();
	}

	@Override
	public void setBrickData(GLBrick brick) {
		brick.setDataDomain(dataDomain);
//		brick.setRecordVA(getGroup(), recordPerspective.getVirtualArray());

	}

	@Override
	public DimensionVirtualArray getDimensionVA() {
		return dimensionPerspective.getVirtualArray();
	}

}
