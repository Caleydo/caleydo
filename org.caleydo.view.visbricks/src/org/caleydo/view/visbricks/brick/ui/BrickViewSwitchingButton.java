package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.layout.IViewTypeChangeListener;

public class BrickViewSwitchingButton extends Button implements IViewTypeChangeListener {

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

	@Override
	public void viewTypeChanged(EContainedViewType viewType) {
		if(viewType == this.viewType) {
			setSelected(true);
		} else {
			setSelected(false);
		}
		
	}

}
