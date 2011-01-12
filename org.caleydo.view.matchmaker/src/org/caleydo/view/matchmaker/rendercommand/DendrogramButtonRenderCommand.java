package org.caleydo.view.matchmaker.rendercommand;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.layout.AHeatMapLayout;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutDetailViewLeft;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutDetailViewRight;

public class DendrogramButtonRenderCommand implements IHeatMapRenderCommand {

	private PickingManager pickingManager;
	private int viewID;
	private TextureManager textureManager;

	public DendrogramButtonRenderCommand(int viewID, PickingManager pickingManager,
			TextureManager textureManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
		this.textureManager = textureManager;
	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.DENDROGRAM_BUTTON;
	}

	@Override
	public void render(GL2 gl, HeatMapWrapper heatMapWrapper) {

		AHeatMapLayout layout = heatMapWrapper.getLayout();
		Vec3f position = layout.getDendrogramButtonPosition();
		float height = layout.getDendrogramButtonHeight();
		float width = layout.getDendrogramButtonWidth();
		Vec3f linePosition = layout.getDendrogramLinePosition();

		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.COMPARE_DENDROGRAM_BUTTON_SELECTION, heatMapWrapper.getID()));
		Vec3f lowerLeftCorner = new Vec3f(position.x(), position.y(), position.z());
		Vec3f lowerRightCorner = new Vec3f(position.x() + width, position.y(),
				position.z());
		Vec3f upperRightCorner = new Vec3f(position.x() + width, position.y() + height,
				position.z());
		Vec3f upperLeftCorner = new Vec3f(position.x(), position.y() + height,
				position.z());

		if ((layout instanceof HeatMapLayoutDetailViewRight && !layout.isDendrogramUsed())
				|| (layout instanceof HeatMapLayoutDetailViewLeft && layout
						.isDendrogramUsed())) {
			textureManager.renderTexture(gl, EIconTextures.HEAT_MAP_ARROW,
					upperLeftCorner, lowerLeftCorner, lowerRightCorner, upperRightCorner,
					1, 1, 1, 1);
		} else {
			textureManager.renderTexture(gl, EIconTextures.HEAT_MAP_ARROW,
					lowerRightCorner, upperRightCorner, upperLeftCorner, lowerLeftCorner,
					1, 1, 1, 1);
		}

		gl.glPopName();

		if (layout instanceof HeatMapLayoutDetailViewLeft) {
			linePosition.setX(linePosition.x() + layout.getDendrogramLineWidth());
		}

		// gl.glLineWidth(1f);
		// gl.glColor4f(1, 0, 0, 1);
		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3f(linePosition.x(), linePosition.y(), linePosition.z());
		// gl.glVertex3f(linePosition.x(), linePosition.y()
		// + layout.getDendrogramLineHeight(), linePosition.z());
		// gl.glEnd();

		gl.glPopAttrib();

	}

}
