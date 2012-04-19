/**
 * 
 */
package org.caleydo.view.pathway.event;

import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Event to show the a bubble set in the pathway view for a specified list of
 * {@link PathwayVertexRep}s.
 * 
 * @author Christian
 * 
 */
public class ShowBubbleSetForPathwayVertexRepsEvent extends AEvent {

	private List<PathwayVertexRep> vertexReps;

	public ShowBubbleSetForPathwayVertexRepsEvent(List<PathwayVertexRep> vertexReps) {
		this.vertexReps = vertexReps;
	}

	@Override
	public boolean checkIntegrity() {
		return vertexReps != null;
	}
	
	/**
	 * @param vertexReps setter, see {@link #vertexReps}
	 */
	public void setVertexReps(List<PathwayVertexRep> vertexReps) {
		this.vertexReps = vertexReps;
	}
	
	/**
	 * @return the vertexReps, see {@link #vertexReps}
	 */
	public List<PathwayVertexRep> getVertexReps() {
		return vertexReps;
	}

}
