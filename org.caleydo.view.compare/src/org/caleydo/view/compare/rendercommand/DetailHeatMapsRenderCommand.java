package org.caleydo.view.compare.rendercommand;

import gleem.linalg.Vec3f;

import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.Group;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.view.compare.GroupInfo;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class DetailHeatMapsRenderCommand implements IHeatMapRenderCommand {

	private PickingManager pickingManager;
	private int viewID;

	public DetailHeatMapsRenderCommand(int viewID, PickingManager pickingManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
	}

	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {
		int numTotalSamples = 0;
		HashMap<Group, GroupInfo> selectedGroups = heatMapWrapper
				.getSelectedGroups();
		AHeatMapLayout layout = heatMapWrapper.getLayout();

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = heatMapWrapper
					.getHeatMap(group.getGroupIndex());
			numTotalSamples += heatMap.getNumberOfVisibleElements();
		}

		heatMapWrapper.calculateHeatMapPositions();

		for (Group group : selectedGroups.keySet()) {

			GLHeatMap heatMap = heatMapWrapper
					.getHeatMap(group.getGroupIndex());
			if (heatMap == null)
				continue;
			int numSamplesInHeatMap = heatMap.getNumberOfVisibleElements();
			
			//FIXME: The heatmap overhead is not always present
			float heatMapHeight = layout.getDetailHeatMapHeight(
					numSamplesInHeatMap, numTotalSamples,
					selectedGroups.size(),
					heatMap.getRequiredOverheadSpacing(),
					selectedGroups.size(), true);
			Vec3f heatMapPosition = heatMapWrapper.getHeatMapPosition(group
					.getGroupIndex());

			gl.glTranslatef(heatMapPosition.x(), heatMapPosition.y(),
					heatMapPosition.z());
			heatMap.getViewFrustum().setLeft(heatMapPosition.x());
			heatMap.getViewFrustum().setBottom(heatMapPosition.y());
			heatMap.getViewFrustum().setRight(
					heatMapPosition.x() + layout.getDetailWidth());
			heatMap.getViewFrustum()
					.setTop(heatMapPosition.y() + heatMapHeight);

			if (heatMapWrapper.isNewSelection()) {
				heatMap.setDisplayListDirty();
			}
			gl.glPushName(pickingManager.getPickingID(viewID, layout
					.getHeatMapPickingType(), group.getGroupIndex()));
			heatMap.displayRemote(gl);
			gl.glPopName();

			gl.glTranslatef(-heatMapPosition.x(), -heatMapPosition.y(),
					-heatMapPosition.z());

			// ContentVirtualArray va = heatMap.getContentVA();
			//
			// for (int i = 0; i < va.size(); i++) {
			// // Vec2f position = getLeftLinkPositionFromContentID(va.get(i));
			// // GLHelperFunctions.drawPointAt(gl, position.x(), position.y(),
			// // 1);v
			// Vec2f position = getLeftDetailLinkPositionFromContentID(va
			// .get(i));
			// GLHelperFunctions
			// .drawPointAt(gl, position.x(), position.y(), 1);
			// }
		}

	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.DETAIL_HEATMAPS;
	}

}
