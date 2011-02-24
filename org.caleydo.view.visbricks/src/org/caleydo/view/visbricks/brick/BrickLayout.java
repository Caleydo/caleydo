package org.caleydo.view.visbricks.brick;

import org.caleydo.core.view.opengl.layout.ElementLayout;

public class BrickLayout extends ElementLayout {

	private BrickRenderer brickRenderer;
	
	public BrickLayout(GLBrick brick) {
		
		brickRenderer = new BrickRenderer(brick);
	
		setRenderer(brickRenderer);
	}
}
