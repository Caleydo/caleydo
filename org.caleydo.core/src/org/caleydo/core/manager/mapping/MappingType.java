package org.caleydo.core.manager.mapping;

import org.caleydo.core.data.mapping.IDType;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Edge of a mapping graph. It holds the the source and target ID type.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class MappingType
	extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	private IDType fromIDType;
	private IDType toIDType;
	
	private boolean isMultiMap = false;

	/**
	 * Constructor.
	 * 
	 * @param fromIDType
	 *            Source ID type.
	 * @param toIDType
	 *            Target ID type.
	 */
	public MappingType(IDType fromIDType, IDType toIDType, boolean isMultiMap) {
		super();
		this.fromIDType = fromIDType;
		this.toIDType = toIDType;
		this.isMultiMap = isMultiMap;
	}
	
	public void setFromIDType(IDType fromIDType) {
		this.fromIDType = fromIDType;
	}
	
	public void setToIDType(IDType toIDType) {
		this.toIDType = toIDType;
	}
	
	public IDType getFromIDType() {
		return fromIDType;
	}
	
	public IDType getToIDType() {
		return toIDType;
	}
	
	public boolean isMultiMap() {
		return isMultiMap;
	}
	
	@Override
	public String toString() {
		
		return fromIDType+"_2_"+toIDType;
	}
}
