package org.caleydo.view.compare.rendercommand;

import javax.media.opengl.GL;

import org.caleydo.view.matchmaker.HeatMapWrapper;

public interface IHeatMapRenderCommand {

	public void render(GL gl, HeatMapWrapper heatMapWrapper);
	
	public ERenderCommandType getRenderCommandType();
}
