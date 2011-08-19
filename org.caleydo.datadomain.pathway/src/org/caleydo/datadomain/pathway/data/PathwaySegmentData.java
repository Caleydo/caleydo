package org.caleydo.datadomain.pathway.data;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.ISegmentData;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Implementation of {@link ISegmentData} for pathways. In this case each
 * segment group refers to one pathway ({@link PathwayGraph}).
 * 
 * @author Partl
 * 
 */
public class PathwaySegmentData implements ISegmentData {

	private IDataDomain dataDomain;
	private ATableBasedDataDomain mappingDataDomain;
	private RecordVirtualArray recordVA;
	private PathwayGraph pathway;
	private Group group;
	private PathwayDimensionGroupData dimensionGroupData;

	public PathwaySegmentData(IDataDomain dataDomain,
			ATableBasedDataDomain mappingDataDomain,
			RecordVirtualArray recordVA, Group group, PathwayGraph pathway,
			PathwayDimensionGroupData dimensionGroupData) {
		this.dataDomain = dataDomain;
		this.mappingDataDomain = mappingDataDomain;
		this.recordVA = recordVA;
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
	public RecordVirtualArray getRecordVA() {
		// TODO Auto-generated method stub
		return recordVA;
	}

	@Override
	public Group getGroup() {
		// TODO Auto-generated method stub
		return group;
	}

	@Override
	public String getLabel() {
		if (pathway != null)
			return pathway.getTitle();
		return "";
	}

	/**
	 * @return The pathway of this segment group.
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * @return The data domain that is used for ID-mapping.
	 */
	public ATableBasedDataDomain getMappingDataDomain() {
		return mappingDataDomain;
	}

}
