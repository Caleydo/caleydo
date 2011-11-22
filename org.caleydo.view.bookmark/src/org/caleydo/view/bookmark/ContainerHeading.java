package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class ContainerHeading extends LayoutRenderer implements ILayoutedElement {

	private ElementLayout layout;
	private GLBookmarkView manager;
	private String caption = "CAPTION";

	protected final static int Y_SPACING_PIXEL = 4;
	protected final static int X_SPACING_PIXEL = 5;

	public ContainerHeading(GLBookmarkView manager) {
		this.manager = manager;
		layout = new ElementLayout("ContainerHeading");
		layout.setRatioSizeX(1);
		layout.setPixelSizeY(20);
		layout.setRenderer(this);

	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public ElementLayout getLayout() {
		return layout;
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);

		PixelGLConverter pixelGLConverter = manager.getPixelGLConverter();
		float ySpacing = pixelGLConverter.getGLHeightForPixelHeight(Y_SPACING_PIXEL);
		float xSpacing = pixelGLConverter.getGLWidthForPixelWidth(X_SPACING_PIXEL);

		manager.getTextRenderer().setColor(0, 0, 0, 1);
		manager.getTextRenderer().renderTextInBounds(gl, caption, 0 + xSpacing,
				0 + ySpacing, 0, x - xSpacing, y - 2 * ySpacing);

	}
}
