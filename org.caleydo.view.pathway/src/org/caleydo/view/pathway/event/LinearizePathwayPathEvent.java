/**
 * 
 */
package org.caleydo.view.pathway.event;

import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Event that specifies a pathway path as a list of {@link PathwayVertexRep}
 * objects in order to be linearized.
 * 
 * @author Christian
 * 
 */
public class LinearizePathwayPathEvent extends AEvent {

	/**
	 * List of {@link PathwayVertexRep} objects that specifies a path in a
	 * pathway. The first object represents the start and the last object the
	 * end of the path. If there are multiple objects that represent a complex
	 * node, these objects must be placed in a sequence.
	 */
	private List<PathwayVertexRep> path;
	
	/**
	 * The pathway whose path shall be linearized.
	 */
	private PathwayGraph pathway;

	@Override
	public boolean checkIntegrity() {
		return (pathway != null) && (path != null);
	}

	/**
	 * @param path
	 *            setter, see {@link #path}
	 */
	public void setPath(List<PathwayVertexRep> path) {
		this.path = path;
	}

	/**
	 * @return the path, see {@link #path}
	 */
	public List<PathwayVertexRep> getPath() {
		return path;
	}
	
	/**
	 * @param pathway setter, see {@link #pathway}
	 */
	public void setPathway(PathwayGraph pathway) {
		this.pathway = pathway;
	}
	
	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

}
