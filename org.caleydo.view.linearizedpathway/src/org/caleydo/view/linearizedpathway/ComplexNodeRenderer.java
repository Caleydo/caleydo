/**
 * 
 */
package org.caleydo.view.linearizedpathway;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * The complex node renderer renders a node that represents multiple
 * {@link PathwayVertexRep} objects.
 * 
 * @author Christian
 * 
 */
public class ComplexNodeRenderer extends ANodeRenderer {

	/**
	 * List of {@link PathwayVertexRep} objects that are combined in this
	 * complex node.
	 */
	private List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();

	/**
	 * @param pixelGLConverter
	 */
	public ComplexNodeRenderer(PixelGLConverter pixelGLConverter) {
		super(pixelGLConverter);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		// TODO Auto-generated method stub

	}

	/**
	 * Adds a {@link PathwayVertexRep} object to this node renderer.
	 * 
	 * @param vertexRep
	 */
	public void addVertexRep(PathwayVertexRep vertexRep) {
		vertexReps.add(vertexRep);
	}

	/**
	 * @param vertexReps
	 *            setter, see {@link #vertexReps}
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
