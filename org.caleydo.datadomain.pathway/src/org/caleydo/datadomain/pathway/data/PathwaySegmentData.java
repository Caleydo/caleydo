package org.caleydo.datadomain.pathway.data;

import org.caleydo.core.data.container.ADataContainer;
import org.caleydo.core.data.container.ISegmentData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Implementation of {@link ISegmentData} for pathways. In this case each
 * segment group refers to one pathway ({@link PathwayGraph}).
 * 
 * @author Partl
 * 
 */
public class PathwaySegmentData extends ADataContainer implements ISegmentData {

	private PathwayDataDomain pathwayDataDomain;
	private PathwayGraph pathway;
	private Group group;
	private PathwayDimensionGroupData dimensionGroupData;

	public PathwaySegmentData(ATableBasedDataDomain dataDomain,
			PathwayDataDomain pathwayDataDomain, RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, Group group, PathwayGraph pathway,
			PathwayDimensionGroupData dimensionGroupData) {
		super(dataDomain, recordPerspective, dimensionPerspective);
		this.pathwayDataDomain = pathwayDataDomain;
		this.group = group;
		this.pathway = pathway;
		this.dimensionGroupData = dimensionGroupData;
	}

	@Override
	public Group getGroup() {
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
	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}
}
