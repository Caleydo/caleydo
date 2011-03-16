package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.view.visbricks.brick.GLBrick;

public abstract class ABrickLayoutTemplate extends LayoutTemplate {
	
	protected GLBrick brick;
	protected LayoutRenderer viewRenderer;
	protected boolean showHandles;
	
	public ABrickLayoutTemplate(GLBrick brick) {
		this.brick = brick;
		showHandles = false;
	}
	
	public void setViewRenderer(LayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}
	
	public boolean isShowHandles() {
		return showHandles;
	}

	public void setShowHandles(boolean showHandles) {
		this.showHandles = showHandles;
	}

}
