package org.caleydo.view.visbricks.brick;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Template;

public class BrickLayout extends ElementLayout {

	private BrickRenderer brickRenderer;
	
	public BrickLayout(GLBrick brick) {
		super("brickLayout");
		brickRenderer = new BrickRenderer(brick);
	
		setRenderer(brickRenderer);
	}
	
	@Override
	protected void updateSpacings(Template template) {
		super.updateSpacings(template);
	}
}
