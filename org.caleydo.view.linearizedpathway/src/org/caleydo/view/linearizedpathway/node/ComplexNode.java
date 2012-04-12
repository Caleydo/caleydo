/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;

/**
 * The complex node renderer renders a node that represents multiple
 * {@link PathwayVertexRep} objects.
 * 
 * @author Christian
 * 
 */
public class ComplexNode extends ALinearizableNode {

	public static final int TEXT_SPACING_PIXELS = 3;

	private CaleydoTextRenderer textRenderer;

	/**
	 * List of {@link PathwayVertexRep} objects that are combined in this
	 * complex node.
	 */
	private List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();

	/**
	 * The caption displayed on the node.
	 */
	protected String caption = "Complex";

	/**
	 * @param pixelGLConverter
	 */
	public ComplexNode(PixelGLConverter pixelGLConverter,
			CaleydoTextRenderer textRenderer, GLLinearizedPathway view, int nodeId) {
		super(pixelGLConverter, view, nodeId);
		this.textRenderer = textRenderer;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(widthPixels);
		float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);

		Vec3f lowerLeftPosition = new Vec3f(position.x() - width / 2.0f, position.y()
				- height / 2.0f, position.z());

		gl.glPushName(pickingManager.getPickingID(view.getID(),
				PickingType.LINEARIZABLE_NODE.name(), nodeId));

		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y(), lowerLeftPosition.z());
		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y(),
				lowerLeftPosition.z());
		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y() + height,
				position.z());
		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y() + height,
				lowerLeftPosition.z());
		gl.glEnd();

		textRenderer.setColor(0, 0, 0, 1);

		float textSpacing = pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS);
		float textWidth = textRenderer.getRequiredTextWidthWithMax(caption, height - 2
				* textSpacing, width - 2 * textSpacing);

		textRenderer.renderTextInBounds(gl, caption + " " + numAssociatedRows,
				position.x() - textWidth / 2.0f + textSpacing, lowerLeftPosition.y()
						+ 1.5f * textSpacing, lowerLeftPosition.z(), width - 2
						* textSpacing, height - 2 * textSpacing);

		gl.glPopName();

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

	@Override
	public PathwayVertexRep getPathwayVertexRep() {
		if (vertexReps.size() > 0)
			return vertexReps.get(0);
		return null;
	}

	/**
	 * @param caption
	 *            setter, see {@link #caption}
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * @return the caption, see {@link #caption}
	 */
	public String getCaption() {
		return caption;
	}

	@Override
	public int getMinRequiredHeightPixels() {
		return DEFAULT_HEIGHT_PIXELS;
	}

	@Override
	public int getMinRequiredWidthPixels() {
		return DEFAULT_WIDTH_PIXELS;
	}

}
