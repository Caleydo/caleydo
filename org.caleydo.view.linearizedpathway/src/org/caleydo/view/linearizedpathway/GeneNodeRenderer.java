/**
 * 
 */
package org.caleydo.view.linearizedpathway;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Renderer for a node that belongs to a gene.
 * 
 * @author Christian
 * 
 */
public class GeneNodeRenderer extends ANodeRenderer {

	public static final int TEXT_SPACING_PIXELS = 3;

	protected CaleydoTextRenderer textRenderer;

	/**
	 * The vertex in the graph this gene belongs to. This can either be a direct
	 * relationship, or the vertex can contain multiple genes.
	 */
	protected PathwayVertexRep vertex;

	/**
	 * The caption displayed on the node.
	 */
	protected String caption = "";

	/**
	 * @param pixelGLConverter
	 */
	public GeneNodeRenderer(PixelGLConverter pixelGLConverter,
			CaleydoTextRenderer textRenderer) {
		super(pixelGLConverter);
		this.textRenderer = textRenderer;
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

		textRenderer.renderTextInBounds(gl, caption, position.x() - textWidth / 2.0f
				+ textSpacing, lowerLeftPosition.y() + 1.5f * textSpacing,
				lowerLeftPosition.z(), width - 2 * textSpacing, height - 2 * textSpacing);
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
