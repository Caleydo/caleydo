package org.caleydo.datadomain.pathway.data;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Specialization of {@link DataContainer} for pathways. Adds a
 * {@link PathwayGraph} to the data container.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * 
 */
public class PathwayDataContainer extends DataContainer {

	/** The datadomain giving access to the pathways themselves */

	private PathwayDataDomain pathwayDataDomain;
	/** The pathway associated with this data container */
	private PathwayGraph pathway;

	/**
	 * 
	 * @param dataDomain
	 *            the data domain used for the mapping of the expression values
	 * @param pathwayDataDomain
	 *            the datadomain holding the actual pathways
	 * @param recordPerspective
	 * @param dimensionPerspective
	 * @param pathway
	 */
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
	 * @return the pathway, see {@link #pathway}
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
