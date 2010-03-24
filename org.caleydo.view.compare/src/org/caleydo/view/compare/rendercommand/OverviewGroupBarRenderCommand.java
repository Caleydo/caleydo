package org.caleydo.view.compare.rendercommand;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.layout.AHeatMapLayout;

public class OverviewGroupBarRenderCommand implements IHeatMapRenderCommand {

	private PickingManager pickingManager;
	private int viewID;
	private TextureManager textureManager;

	public OverviewGroupBarRenderCommand(int viewID,
			PickingManager pickingManager, TextureManager textureManager) {
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
		ContentGroupList contentGroupList = contentVA.getGroupList();
		float groupWidth = layout.getOverviewGroupBarWidth();

		if (contentGroupList != null) {

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			for (Group group : contentGroupList) {

				float groupHeight = layout.getOverviewHeatMapGroupHeight(group
						.getGroupIndex());

				Vec3f groupPosition = layout.getOverviewGroupPosition(group
						.getGroupIndex());
				EIconTextures iconTextures = (group.getSelectionType() == SelectionType.SELECTION) ? EIconTextures.HEAT_MAP_GROUP_SELECTED
						: EIconTextures.HEAT_MAP_GROUP_NORMAL;

				gl.glPushName(pickingManager.getPickingID(viewID, layout
						.getGroupPickingType(), group.getGroupIndex()));
				Vec3f lowerLeftCorner = new Vec3f(groupPosition.x(), groupPosition.y(), groupPosition.z());
				Vec3f lowerRightCorner = new Vec3f(groupPosition.x() + groupWidth,
						groupPosition.y(), groupPosition.z());
				Vec3f upperRightCorner = new Vec3f(groupPosition.x() + groupWidth,
						groupPosition.y() + groupHeight, groupPosition.z());
				Vec3f upperLeftCorner = new Vec3f(groupPosition.x(), groupPosition.y() + groupHeight, groupPosition.z());

				textureManager.renderTexture(gl, iconTextures, lowerLeftCorner,
						lowerRightCorner, upperRightCorner, upperLeftCorner, 1,
						1, 1, 1);

				gl.glPopName();
			}
		}

		// gl.glTranslatef(overviewGroupsPosition.x(),
		// overviewGroupsPosition.y(),
		// overviewGroupsPosition.z());
		//
		// HeatMapUtil.renderGroupBar(gl, contentVA, layout.getOverviewHeight(),
		// layout.getOverviewGroupBarWidth(), pickingManager, viewID,
		// layout.getGroupPickingType(), textureManager);
		//
		// gl.glPopMatrix();
	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.OVERVIEW_GROUP_BAR;
	}

}
