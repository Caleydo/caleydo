package org.caleydo.core.manager.mapping;

import org.caleydo.core.data.mapping.IDType;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Edge of a mapping graph. It holds the the source and target ID type.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class MappingEdge
	extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	private IDType fromIDType;
	private IDType toIDType;

	/**
	 * Constructor.
	 * 
	 * @param fromIDType
	 *            Source ID type.
	 * @param toIDType
	 *            Target ID type.
	 */
	public MappingEdge(IDType fromIDType, IDType toIDType) {
		super();
		this.fromIDType = fromIDType;
		this.toIDType = toIDType;
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
}
