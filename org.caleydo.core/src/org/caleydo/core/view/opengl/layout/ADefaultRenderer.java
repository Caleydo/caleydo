/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

/**
 * Convenience default implementation of {@link IRenderer}.
 *
 * @author Christian Partl
 *
 */
public abstract class ADefaultRenderer implements IRenderer {

	protected float minX;
	protected float minY;
	protected float maxX;
	protected float maxY;

	@Override
	public void setLimits(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public int getMinHeightPixels() {
		return 0;
	}

	@Override
	public int getMinWidthPixels() {
		return 0;
	}

}
