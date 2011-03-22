package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.EContainedViewType;

public class BrickViewSwitchingButton extends Button {

	private EContainedViewType viewType;

	public BrickViewSwitchingButton(EPickingType pickingType, int buttonID,
			EIconTextures iconTexture, EContainedViewType viewType) {
		super(pickingType, buttonID, iconTexture);
		this.viewType = viewType;
	}

	public void setViewType(EContainedViewType viewType) {
		this.viewType = viewType;
	}

	public EContainedViewType getViewType() {
		return viewType;
	}

}
