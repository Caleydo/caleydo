package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Dummy renderer that only requires some minimal space.
 * 
 * @author Partl
 * 
 */
public class DummyRenderer extends LayoutRenderer {

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

}
