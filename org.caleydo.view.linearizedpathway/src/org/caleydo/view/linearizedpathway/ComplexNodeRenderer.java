/**
 * 
 */
package org.caleydo.view.linearizedpathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * The complex node renderer renders a node that represents multiple
 * {@link PathwayVertexRep} objects.
 * 
 * @author Christian
 * 
 */
public class ComplexNodeRenderer extends ANodeRenderer {
	
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
	public ComplexNodeRenderer(PixelGLConverter pixelGLConverter, CaleydoTextRenderer textRenderer) {
		super(pixelGLConverter);
		this.textRenderer = textRenderer;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(widthPixels);
		float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);

		Vec3f lowerLeftPosition = new Vec3f(position.x() - width / 2.0f, position.y()
				- height / 2.0f, position.z());

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
		if(vertexReps.size() > 0)
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

}
