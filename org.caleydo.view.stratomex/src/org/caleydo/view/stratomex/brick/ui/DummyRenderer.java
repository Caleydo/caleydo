/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * Dummy renderer that only requires some minimal space.
 * 
 * @author Partl
 * 
 */
public class DummyRenderer extends ALayoutRenderer {

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
	public void renderContent(GL2 gl) {

	}
	
	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
