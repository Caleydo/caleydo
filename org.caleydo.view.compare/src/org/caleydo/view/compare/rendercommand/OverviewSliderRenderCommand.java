package org.caleydo.view.compare.rendercommand;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.VerticalSlider;

public class OverviewSliderRenderCommand implements IHeatMapRenderCommand {

	private PickingManager pickingManager;
	private int viewID;
	private TextureManager textureManager;

	public OverviewSliderRenderCommand(int viewID,
			PickingManager pickingManager, TextureManager textureManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
		this.textureManager = textureManager;
	}

	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {

		VerticalSlider slider = heatMapWrapper.getOverview()
				.getOverviewSlider();

		slider.draw(gl, pickingManager, textureManager, viewID, heatMapWrapper
				.getID());

	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.OVERVIEW_SLIDER;
	}

}
