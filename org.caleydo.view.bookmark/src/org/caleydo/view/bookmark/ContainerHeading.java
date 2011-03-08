package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class ContainerHeading extends LayoutRenderer implements ILayoutedElement {

	private ElementLayout layoutElement;
	private GLBookmarkView manager;
	private String caption = "CAPTION";

	public ContainerHeading(GLBookmarkView manager) {
		this.manager = manager;
		layoutElement = new ElementLayout();
		layoutElement.setPixelGLConverter(manager.getParentGLCanvas()
				.getPixelGLConverter());
		layoutElement.setRatioSizeX(1);
		layoutElement.setPixelSizeY(20);
		layoutElement.setRenderer(this);

	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public ElementLayout getLayout() {
		return layoutElement;
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		float height = (layoutElement.getSizeScaledY() - (float) manager
				.getMinSizeTextRenderer().getBounds("Bla").getHeight()) / 2;
		RenderingHelpers.renderText(gl, manager.getMinSizeTextRenderer(), caption,
				0 + BookmarkRenderStyle.SIDE_SPACING, height,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);
	}

}
