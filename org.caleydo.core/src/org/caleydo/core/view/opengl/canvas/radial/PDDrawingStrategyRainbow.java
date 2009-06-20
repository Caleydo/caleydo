package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.ColorMarkerPoint;
import org.caleydo.core.util.mapping.color.EColorMappingType;

public class PDDrawingStrategyRainbow
	extends APDDrawingStrategyChildIndicator {

	public PDDrawingStrategyRainbow(PickingManager pickingManager, int iViewID) {
		super(pickingManager, iViewID);

		ArrayList<ColorMarkerPoint> alMarkerPoints = new ArrayList<ColorMarkerPoint>();

		alMarkerPoints.add(new ColorMarkerPoint(0.0f, 1.0f, 0.0f, 0.0f));
		alMarkerPoints.add(new ColorMarkerPoint((120.0f / 360.0f), 0.0f, 1.0f, 0.0f));
		alMarkerPoints.add(new ColorMarkerPoint((240.0f / 360.0f), 0.0f, 0.0f, 1.0f));
		alMarkerPoints.add(new ColorMarkerPoint(1.0f, 1.0f, 0.0f, 0.0f));

		ColorMappingManager.get().initColorMapping(EColorMappingType.RAINBOW, alMarkerPoints);
	}

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		float fRadius = pdDiscToDraw.getCurrentWidth();

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((pdDiscToDraw.getCurrentDepth() == 1) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, pdDiscToDraw.getCurrentInnerRadius(), fRadius, pdDiscToDraw
				.getCurrentStartAngle(), pdDiscToDraw.getCurrentAngle());
		}

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR, 0);
		GLPrimitives.renderCircle(gl, glu, fRadius, iNumSlicesPerFullDisc);

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_COLOR, 0);
		GLPrimitives.renderCircleBorder(gl, glu, fRadius, iNumSlicesPerFullDisc,
			RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

		gl.glPopAttrib();
		gl.glPopName();
	}

	@Override
	public void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		float fStartAngle = pdDiscToDraw.getCurrentStartAngle();
		float fAngle = pdDiscToDraw.getCurrentAngle();
		float fInnerRadius = pdDiscToDraw.getCurrentInnerRadius();
		float fWidth = pdDiscToDraw.getCurrentWidth();

		float fMidAngle = fStartAngle + (fAngle / 2.0f);
		while (fMidAngle >= 360) {
			fMidAngle -= 360;
		}
		while (fMidAngle < 0) {
			fMidAngle += 360;
		}

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((pdDiscToDraw.getCurrentDepth() == 1) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, fInnerRadius, fWidth, fStartAngle, fAngle);
		}

		ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
		
		float fArRGB[] = cmRainbow.getColor(fMidAngle / 360.0f);
		gl.glColor4f(fArRGB[0], fArRGB[1], fArRGB[2], 1);

		GLPrimitives.renderPartialDisc(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_COLOR, 0);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle,
			fAngle, iNumSlicesPerFullDisc, RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

		gl.glPopAttrib();
		gl.glPopName();
	}

}
