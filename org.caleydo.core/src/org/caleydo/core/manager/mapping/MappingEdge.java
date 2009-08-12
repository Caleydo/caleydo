package org.caleydo.core.manager.mapping;

import org.caleydo.core.data.mapping.EMappingType;
import org.jgrapht.graph.DefaultWeightedEdge;

public class MappingEdge extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	EMappingType mappingType;


	public MappingEdge(EMappingType mappingType) {
		super();
		this.mappingType = mappingType;
	}
	
	public EMappingType getMappingType() {
		return mappingType;
	}

	public void setMappingType(EMappingType mappingType) {
		this.mappingType = mappingType;
	}
}
