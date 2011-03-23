package org.caleydo.view.visbricks.brick;

/**
 * This class holds visual parameters of a brick, allowing to reconstruct a
 * brick's appearance.
 * 
 * @author Christian Partl
 * 
 */
public class BrickState {

	private EContainedViewType viewType;
	private float height;
	private float width;

	public BrickState(EContainedViewType viewType, float height, float width) {
		this.viewType = viewType;
		this.height = height;
		this.width = width;
	}

	public EContainedViewType getViewType() {
		return viewType;
	}

	public void setViewType(EContainedViewType viewType) {
		this.viewType = viewType;
	}

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
