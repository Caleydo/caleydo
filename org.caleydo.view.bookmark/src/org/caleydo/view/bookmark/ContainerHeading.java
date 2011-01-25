package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ARenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class ContainerHeading extends ARenderer implements ILayoutedElement {

	private ElementLayout layoutElement;
	private GLBookmarkView manager;

	public ContainerHeading(GLBookmarkView manager) {
		this.manager = manager;
		layoutElement = new ElementLayout();
		layoutElement.setSizeX(1);
		layoutElement.setScaleY(false);
		layoutElement.setSizeY(0.3f);
		layoutElement.setRenderer(this);
		
	}

	@Override
	public ElementLayout getElementLayout() {
		return layoutElement;
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		RenderingHelpers.renderText(gl, manager.getTextRenderer(), "HEADING",
				0 + BookmarkRenderStyle.SIDE_SPACING, 0,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);
	}

}
