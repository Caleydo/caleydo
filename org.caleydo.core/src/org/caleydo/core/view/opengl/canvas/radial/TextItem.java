package org.caleydo.core.view.opengl.canvas.radial;

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Represents some text that can be drawn within a label. Note, that changes to the size of a text item cannot
 * be made by the methods setHeight and setWidth, since the size of a text is determined by the font type, the
 * font size, the font style and the font scaling.
 * 
 * @author Christian Partl
 */
public class TextItem
	extends ALabelItem {

	private static final String sTextForHeightCalculation =
		"Text without characters below the bottom textline";

	private String sText;
	private TextRenderer textRenderer;
	private float fTextScaling;

	/**
	 * Constructor.
	 * 
	 * @param sText
	 *            Text that should be displayed in the label.
	 */
	public TextItem(String sText) {
		this.sText = sText;
	}

	@Override
	public void draw(GL gl) {
		float[] text_color = RadialHierarchyRenderStyle.LABEL_TEXT_COLOR;
		textRenderer.setColor(text_color[0], text_color[1], text_color[2], text_color[3]);
		textRenderer.begin3DRendering();

		textRenderer.draw3D(sText, vecPosition.x(), vecPosition.y(), 0, fTextScaling);

		textRenderer.end3DRendering();
		textRenderer.flush();
	}

	/**
	 * Sets the properties for rendering text which indirectly sets the size of the text item.
	 * 
	 * @param textRenderer
	 *            TextRenderer that shall be used for rendering the text.
	 * @param fTextScaling
	 *            Scaling factor of the text.
	 */
	public void setRenderingProperties(TextRenderer textRenderer, float fTextScaling) {
		this.textRenderer = textRenderer;
		this.fTextScaling = fTextScaling;

		Rectangle2D bounds = textRenderer.getBounds(sTextForHeightCalculation);
		fHeight = (float) bounds.getHeight() * fTextScaling;
		bounds = textRenderer.getBounds(sText);
		fWidth = (float) bounds.getWidth() * fTextScaling;
	}

	@Override
	public int getLabelItemType() {
		return LabelItemTypes.LABEL_ITEM_TYPE_TEXT;
	}
}
