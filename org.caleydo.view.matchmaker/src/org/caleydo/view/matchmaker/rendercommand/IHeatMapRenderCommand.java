package org.caleydo.view.matchmaker.rendercommand;

import javax.media.opengl.GL2;

import org.caleydo.view.matchmaker.HeatMapWrapper;

public interface IHeatMapRenderCommand {

	public void render(GL2 gl, HeatMapWrapper heatMapWrapper);

	public ERenderCommandType getRenderCommandType();
}
