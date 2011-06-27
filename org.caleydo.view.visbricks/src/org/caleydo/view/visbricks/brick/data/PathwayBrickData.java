package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.datadomain.pathway.data.PathwaySegmentData;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.visbricks.brick.GLBrick;

public class PathwayBrickData implements IBrickData {

	private PathwaySegmentData segmentData;

	public PathwayBrickData(PathwaySegmentData segmentData) {
		this.segmentData = segmentData;
	}

	@Override
	public IDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return segmentData.getDataDomain();
	}

	@Override
	public ContentVirtualArray getContentVA() {
		// TODO Auto-generated method stub
		return segmentData.getContentVA();
	}

	@Override
	public Group getGroup() {
		// TODO Auto-generated method stub
		return segmentData.getGroup();
	}

	@Override
	public void setBrickData(GLBrick brick) {
		brick.setDataDomain(segmentData.getMappingDataDomain());
		brick.setContentVA(segmentData.getGroup(), segmentData.getContentVA());
	}

	@Override
	public String getLabel() {
		return segmentData.getLabel();
	}

	public PathwayGraph getPathway() {
		return segmentData.getPathway();
	}

}
