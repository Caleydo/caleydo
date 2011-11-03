package org.caleydo.datadomain.pathway.data;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Implementation of {@link ISegmentData} for pathways. In this case each
 * segment group refers to one pathway ({@link PathwayGraph}).
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * 
 */
public class PathwayDataContainer extends DataContainer {

	private PathwayDataDomain pathwayDataDomain;
	private PathwayGraph pathway;
	
	

	public PathwayDataContainer(ATableBasedDataDomain dataDomain,
			PathwayDataDomain pathwayDataDomain, RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, PathwayGraph pathway) {
		super(dataDomain, recordPerspective, dimensionPerspective);
		this.pathwayDataDomain = pathwayDataDomain;

		this.pathway = pathway;

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
