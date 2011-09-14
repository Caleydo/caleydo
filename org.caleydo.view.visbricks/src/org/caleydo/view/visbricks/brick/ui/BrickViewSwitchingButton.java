package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.layout.IViewTypeChangeListener;

/**
 * Button that is supposed to be used for switching views in bricks.
 * 
 * @author Christian Partl
 * 
 */
public class BrickViewSwitchingButton extends Button implements
		IViewTypeChangeListener {

	private EContainedViewType viewType;

	/**
	 * @param pickingType
	 * @param buttonID
	 * @param iconTexture
	 * @param viewType
	 *            The view type that is associated with this button.
	 */
	public BrickViewSwitchingButton(PickingType pickingType, int buttonID,
			EIconTextures iconTexture, EContainedViewType viewType) {
		super(pickingType, buttonID, iconTexture);
		this.viewType = viewType;
	}

	/**
	 * Sets the view type that is associated with this button.
	 * 
	 * @param viewType
	 */
	public void setViewType(EContainedViewType viewType) {
		this.viewType = viewType;
	}

	/**
	 * @return the view type that is associated with this button.
	 */
	public EContainedViewType getViewType() {
		return viewType;
	}

	@Override
	public void viewTypeChanged(EContainedViewType viewType) {
		if (viewType == this.viewType) {
			setSelected(true);
		} else {
			setSelected(false);
		}

	}

}
