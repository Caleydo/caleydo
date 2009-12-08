package org.caleydo.core.manager.mapping;

import org.caleydo.core.data.mapping.EMappingType;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Edge of a mapping graph. It holds the mapping type the edge represents.
 * 
 * @author Christian Partl
 */
public class MappingEdge
	extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	EMappingType mappingType;

	/**
	 * Constructor.
	 * 
	 * @param mappingType
	 *            MappingType of the edge.
	 */
	public MappingEdge(EMappingType mappingType) {
		super();
		this.mappingType = mappingType;
	}

	/**
	 * @return MappingType of the edge.
	 */
	public EMappingType getMappingType() {
		return mappingType;
	}

	/**
	 * Sets the MappingType of the edge.
	 * 
	 * @param mappingType
	 *            MappingType the edge shall represent.
	 */
	public void setMappingType(EMappingType mappingType) {
		this.mappingType = mappingType;
	}
}
