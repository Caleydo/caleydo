/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.ILabelTextProvider;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;

/**
 * Renderer for a node that belongs to a gene.
 * 
 * @author Christian
 * 
 */
public class GeneNode extends ALinearizableNode implements ILabelTextProvider {

	public static final int TEXT_SPACING_PIXELS = 3;

	protected CaleydoTextRenderer textRenderer;

	/**
	 * The vertex in the graph this gene belongs to. This can either be a direct
	 * relationship, or the vertex can contain multiple genes.
	 */
	protected PathwayVertexRep pathwayVertexRep;

	/**
	 * The caption displayed on the node.
	 */
	protected String caption = "";

	/**
	 * @param pixelGLConverter
	 */
	public GeneNode(PixelGLConverter pixelGLConverter, CaleydoTextRenderer textRenderer,
			GLLinearizedPathway view, int nodeId) {
		super(pixelGLConverter, view, nodeId);
		this.textRenderer = textRenderer;
	}

	// @Override
	// public void render(GL2 gl, GLU glu) {
	//
	// float width = pixelGLConverter.getGLWidthForPixelWidth(widthPixels);
	// float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);
	//
	// gl.glPushName(pickingManager.getPickingID(view.getID(),
	// PickingType.LINEARIZABLE_NODE.name(), nodeId));
	// Vec3f lowerLeftPosition = new Vec3f(position.x() - width / 2.0f,
	// position.y()
	// - height / 2.0f, position.z());
	//
	// gl.glColor3f(0, 0, 0);
	// gl.glBegin(GL2.GL_LINE_LOOP);
	// gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y(),
	// lowerLeftPosition.z());
	// gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y(),
	// lowerLeftPosition.z());
	// gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y() +
	// height,
	// position.z());
	// gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y() + height,
	// lowerLeftPosition.z());
	// gl.glEnd();
	//
	// gl.glColor4f(1, 1, 1, 0);
	// gl.glBegin(GL2.GL_QUADS);
	// gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y(),
	// lowerLeftPosition.z());
	// gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y(),
	// lowerLeftPosition.z());
	// gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y() +
	// height,
	// position.z());
	// gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y() + height,
	// lowerLeftPosition.z());
	// gl.glEnd();
	//
	// textRenderer.setColor(0, 0, 0, 1);
	//
	// float textSpacing =
	// pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS);
	// float textWidth = textRenderer.getRequiredTextWidthWithMax(caption,
	// height - 2
	// * textSpacing, width - 2 * textSpacing);
	//
	// textRenderer.renderTextInBounds(gl, caption + " " + numAssociatedRows,
	// position.x() - textWidth / 2.0f + textSpacing, lowerLeftPosition.y()
	// + 1.5f * textSpacing, lowerLeftPosition.z(), width - 2
	// * textSpacing, height - 2 * textSpacing);
	//
	// gl.glPopName();
	// }

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

	@Override
	protected ElementLayout setupLayout() {
		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");
		ColorRenderer colorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 });
		colorRenderer.setView(view);
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer.addPickingID(PickingType.LINEARIZABLE_NODE.name(), nodeId);
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		LabelRenderer labelRenderer = new LabelRenderer(view, this);
		labelRenderer.setAlignment(LabelRenderer.ALIGN_CENTER);

		labelLayout.setRenderer(labelRenderer);
		labelLayout.setPixelSizeY(16);

		ElementLayout horizontalSpacing = new ElementLayout();
		horizontalSpacing.setPixelSizeX(2);

		// baseRow.append(horizontalSpacing);
		baseRow.append(labelLayout);
		// baseRow.append(horizontalSpacing);

		ElementLayout verticalSpacing = new ElementLayout();
		verticalSpacing.setPixelSizeY(2);

		baseColumn.append(verticalSpacing);
		baseColumn.append(baseRow);
		baseColumn.append(verticalSpacing);

		return baseColumn;
	}

	@Override
	public String getLabelText() {
		return caption;
	}

}
