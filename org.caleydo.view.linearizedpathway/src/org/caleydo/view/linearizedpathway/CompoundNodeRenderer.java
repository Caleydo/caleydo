/**
 * 
 */
package org.caleydo.view.linearizedpathway;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * @author Christian
 * 
 */
public class CompoundNodeRenderer extends ANodeRenderer {

	/**
	 * The vertex in the graph this compound belongs to.
	 */
	protected PathwayVertexRep vertex;

	/**
	 * @param pixelGLConverter
	 */
	public CompoundNodeRenderer(PixelGLConverter pixelGLConverter) {
		super(pixelGLConverter);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);

		gl.glPushMatrix();
		gl.glTranslatef(position.x(), position.y(), position.z());
		GLPrimitives.renderCircleBorder(gl, glu, height / 2.0f, 16, 0.1f);
		gl.glPopMatrix();

	}

	/**
	 * @param vertex
	 *            setter, see {@link #vertex}
	 */
	public void setVertex(PathwayVertexRep vertex) {
		this.vertex = vertex;
	}

	/**
	 * @return the vertex, see {@link #vertex}
	 */
	public PathwayVertexRep getVertex() {
		return vertex;
	}

}
