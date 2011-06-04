package org.caleydo.view.visbricks.brick.ui;

import java.awt.Point;

import javax.media.opengl.GL2;

public class DummyRenderer extends AContainedViewRenderer {

	@Override
	public int getMinHeightPixels() {
		// TODO Auto-generated method stub
		return 90;
	}

	@Override
	public int getMinWidthPixels() {
		// TODO Auto-generated method stub
		return 90;
	}
	
	@Override
	public void render(GL2 gl) {
		
	}

	@Override
	public boolean handleMouseWheel(int wheelAmount, Point wheelPosition) {
		return false;
	}

}
