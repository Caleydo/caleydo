package org.caleydo.view.compare.rendercommand;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.AHeatMapLayout;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.heatmap.HeatMapUtil;

public class OverviewGroupBarRenderCommand implements IHeatMapRenderCommand {

	private PickingManager pickingManager;
	private int viewID;
	private TextureManager textureManager;
	
	public OverviewGroupBarRenderCommand(int viewID, PickingManager pickingManager, TextureManager textureManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
		this.textureManager = textureManager;
	}
	
	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {
		
		AHeatMapLayout layout = heatMapWrapper.getLayout();
		ContentVirtualArray contentVA = heatMapWrapper.getOverview()
		.getContentVA();
		
		gl.glPushMatrix();
		Vec3f overviewGroupsPosition = layout.getOverviewGroupBarPosition();
		gl.glTranslatef(overviewGroupsPosition.x(), overviewGroupsPosition.y(),
				overviewGroupsPosition.z());

		HeatMapUtil.renderGroupBar(gl, contentVA, layout.getOverviewHeight(),
				layout.getOverviewGroupWidth(), pickingManager, viewID, layout
						.getGroupPickingType(), textureManager);

		gl.glPopMatrix();
	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.OVERVIEW_GROUP_BAR;
	}

}
