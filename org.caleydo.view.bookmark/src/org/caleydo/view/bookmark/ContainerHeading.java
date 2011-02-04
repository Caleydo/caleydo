package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ARenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;

public class ContainerHeading extends ARenderer implements ILayoutedElement {

	private ElementLayout layoutElement;
	private GLBookmarkView manager;
	private String caption = "CAPTION";

	public ContainerHeading(GLBookmarkView manager) {
		this.manager = manager;
		layoutElement = new ElementLayout();
		layoutElement.setSizeX(1);
		// layoutElement.setScaleY(false);
		layoutElement.setPixelSizeY(20);
		layoutElement.setRenderer(this);
		layoutElement.setPixelGLConverter(manager.getPixelGLConverter());

	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public ElementLayout getElementLayout() {
		return layoutElement;
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		float height = (layoutElement.getSizeScaledY() - (float) manager
				.getTextRenderer().getBounds("Bla").getHeight()) / 2;
		RenderingHelpers.renderText(gl, manager.getTextRenderer(), caption,
				0 + BookmarkRenderStyle.SIDE_SPACING, height,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);
		// GLHelperFunctions.drawPointAt(gl, layoutElement.getSizeScaledX(),
		// layoutElement.getSizeScaledY(), 0);
	}

}
