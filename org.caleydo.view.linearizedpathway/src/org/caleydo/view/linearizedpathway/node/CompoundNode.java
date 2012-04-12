/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;

/**
 * @author Christian
 * 
 */
public class CompoundNode extends ALinearizableNode {

	/**
	 * The vertex in the graph this compound belongs to.
	 */
	protected PathwayVertexRep pathwayVertexRep;

	/**
	 * @param pixelGLConverter
	 */
	public CompoundNode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);

		gl.glPushName(pickingManager.getPickingID(view.getID(),
				PickingType.LINEARIZABLE_NODE.name(), nodeId));
		gl.glPushMatrix();
		gl.glTranslatef(position.x(), position.y(), position.z());
		gl.glColor4f(1, 1, 1, 0);
		GLPrimitives.renderCircle(glu, height / 2.0f, 16);
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderCircleBorder(gl, glu, height / 2.0f, 16, 0.1f);
		gl.glPopMatrix();
		gl.glPopName();

	}

	/**
	 * @param pathwayVertexRep
	 *            setter, see {@link #pathwayVertexRep}
	 */
	public void setPathwayVertexRep(PathwayVertexRep pathwayVertexRep) {
		this.pathwayVertexRep = pathwayVertexRep;
	}

	@Override
	public PathwayVertexRep getPathwayVertexRep() {
		return pathwayVertexRep;
	}

	@Override
	public int getMinRequiredHeightPixels() {
		return DEFAULT_HEIGHT_PIXELS;
	}

	@Override
	public int getMinRequiredWidthPixels() {
		return DEFAULT_HEIGHT_PIXELS;
	}

}
