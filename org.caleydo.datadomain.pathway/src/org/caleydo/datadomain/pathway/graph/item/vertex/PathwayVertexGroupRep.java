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

		super("group", "rectangle", (short) -1, (short) -1, (short) 40, (short) 40);
	}

	public void addVertexRep(PathwayVertexRep vertexRep) {
		groupedVertexReps.add(vertexRep);

		if (groupedVertexReps.size() == 1)
			coords = vertexRep.getCoords();
		else {
			if (vertexRep.getCoords().get(0).getFirst() < coords.get(0).getFirst())
				coords.get(0).setFirst(vertexRep.getCoords().get(0).getFirst());

			if (vertexRep.getCoords().get(0).getSecond() < coords.get(0).getSecond())
				coords.get(0).setSecond(vertexRep.getCoords().get(0).getSecond());

			if (vertexRep.getCoords().get(1).getFirst() > coords.get(1).getFirst())
				coords.get(1).setFirst(vertexRep.getCoords().get(1).getFirst());

			if (vertexRep.getCoords().get(1).getSecond() < coords.get(1).getSecond())
				coords.get(1).setSecond(vertexRep.getCoords().get(1).getSecond());

			if (vertexRep.getCoords().get(2).getFirst() > coords.get(2).getFirst())
				coords.get(2).setFirst(vertexRep.getCoords().get(2).getFirst());

			if (vertexRep.getCoords().get(2).getSecond() > coords.get(2).getSecond())
				coords.get(2).setSecond(vertexRep.getCoords().get(2).getSecond());

			if (vertexRep.getCoords().get(3).getFirst() < coords.get(3).getFirst())
				coords.get(3).setFirst(vertexRep.getCoords().get(3).getFirst());

			if (vertexRep.getCoords().get(3).getSecond() > coords.get(3).getSecond())
				coords.get(3).setSecond(vertexRep.getCoords().get(3).getSecond());
		}
	}
	
	/**
	 * @return the groupedVertexReps, see {@link #groupedVertexReps}
	 */
	public List<PathwayVertexRep> getGroupedVertexReps() {
		return groupedVertexReps;
	}

	@Override
	public EPathwayVertexType getType() {
		return EPathwayVertexType.group;
	}
}
