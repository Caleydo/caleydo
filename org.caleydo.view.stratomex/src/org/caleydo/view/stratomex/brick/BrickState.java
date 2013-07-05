/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick;

/**
 * This class holds visual parameters of a brick, allowing to reconstruct a brick's appearance.
 *
 * @author Christian Partl
 *
 */
public class BrickState {

	/**
	 * ID of the renderer that is currently active.
	 */
	private int rendererID;
	/**
	 * Height of the brick.
	 */
	private float height;
	/**
	 * Width of the brick.
	 */
	private float width;

	public BrickState(int rendererID, float height, float width) {
		this.rendererID = rendererID;
		this.height = height;
		this.width = width;
	}

	/**
	 * @return the rendererID, see {@link #rendererID}
	 */
	public int getRendererID() {
		return rendererID;
	}

	/**
	 * @param rendererID
	 *            setter, see {@link rendererID}
	 */
	public void setRendererID(int rendererID) {
		this.rendererID = rendererID;
	}

	// public EContainedViewType getViewType() {
	// return viewType;
	// }
	//
	// public void setViewType(EContainedViewType viewType) {
	// this.viewType = viewType;
	// }

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

}
