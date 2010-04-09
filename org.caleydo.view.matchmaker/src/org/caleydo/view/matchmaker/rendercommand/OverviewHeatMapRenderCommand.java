package org.caleydo.view.matchmaker.rendercommand;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.view.heatmap.hierarchical.HeatMapUtil;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.layout.AHeatMapLayout;

import com.sun.opengl.util.texture.Texture;

public class OverviewHeatMapRenderCommand implements IHeatMapRenderCommand {

	private PickingManager pickingManager;
	private int viewID;

	public OverviewHeatMapRenderCommand(int viewID,
			PickingManager pickingManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
	}

	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {

		AHeatMapLayout layout = heatMapWrapper.getLayout();

		ContentVirtualArray contentVA = heatMapWrapper.getOverview()
				.getContentVA();
		ContentGroupList contentGroupList = contentVA.getGroupList();

		for (int i = 0; i < contentGroupList.size(); i++) {
			Vec3f overviewHeatMapGroupPosition = layout
					.getOverviewHeatMapGroupPosition(i);
			float height = layout.getOverviewHeatMapGroupHeight(i);

			gl.glPushName(pickingManager.getPickingID(viewID, layout
					.getGroupPickingType(), i));
			gl.glPushMatrix();
			gl.glTranslatef(overviewHeatMapGroupPosition.x(),
					overviewHeatMapGroupPosition.y(),
					overviewHeatMapGroupPosition.z());

			ArrayList<Texture> overviewTextures = heatMapWrapper.getOverview()
					.getClusterTextures(i);
			HeatMapUtil.renderHeatmapTextures(gl, overviewTextures, height,
					layout.getOverviewHeatMapWidth());
			gl.glPopMatrix();
			gl.glPopName();
		}

//		drawSelections(gl, layout, contentSelectionManager, contentVA);

	}

	
	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.OVERVIEW_HEATMAP;
	}

}
