/**
 * 
 */
package org.caleydo.datadomain.pathway.graph.item.vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * A pathway vertex representation that groups together multiple
 * {@link PathwayVertexRep} objects.
 * 
 * @author Marc Streit
 * 
 */
public class PathwayVertexGroupRep
	extends PathwayVertexRep {

	private static final long serialVersionUID = 1L;

	private List<PathwayVertexRep> groupedVertexReps = new ArrayList<PathwayVertexRep>();

	/**
	 * Constructor that takes a list of {@link PathwayVertexReps} and calculates
	 * the merged position and size.
	 */
	public PathwayVertexGroupRep() {

		super("group", "rectangle", (short) -1, (short) -1,
				(short) 40, (short) 40);
	}

	public void addVertexRep(PathwayVertexRep vertexRep) {
		groupedVertexReps.add(vertexRep);

		// TODO set proper size and pos for group
		setRectangularCoords(vertexRep.getXOrigin(), vertexRep.getYOrigin(), width, height);
	}

	/**
	 * @return the groupedVertexReps, see {@link #groupedVertexReps}
	 */
	public List<PathwayVertexRep> getGroupedVertexReps() {
		return groupedVertexReps;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep#getType()
	 */
	@Override
	public EPathwayVertexType getType() {
		return EPathwayVertexType.group;
	}
}
