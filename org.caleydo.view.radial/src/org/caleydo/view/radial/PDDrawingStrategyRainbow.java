/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLPrimitives;

/**
 * PDDrawingStrategyRainbow draws partial discs using rainbow colors. The actual
 * color of a partial disc is determined by using the rainbow color mapping on
 * the start angle of the partial disc's center.
 *
 * @author Christian Partl
 */
public class PDDrawingStrategyRainbow extends APDDrawingStrategyChildIndicator {

	ColorMapper colorMapper;

	/**
	 * Constructor. Initialized the rainbow color mapping. The colors generated
	 * by this color mapping are always combinations of maximum two base colors
	 * where the sum of the intensities of the base colors is equal to 1.
	 *
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the
	 *            drawn elements.
	 * @param viewID
	 *            ID of the view where the elements will be displayed. Needed
	 *            for picking.
	 */
	public PDDrawingStrategyRainbow(PickingManager pickingManager, int viewID) {
		super(pickingManager, viewID);

		ArrayList<ColorMarkerPoint> alMarkerPoints = new ArrayList<ColorMarkerPoint>();

		alMarkerPoints.add(new ColorMarkerPoint(0.0f, new Color( 1.0f, 0.0f, 0.0f)));
		alMarkerPoints.add(new ColorMarkerPoint(120.0f / 360.0f, new Color(0.0f, 1.0f, 0.0f)));
		alMarkerPoints.add(new ColorMarkerPoint(240.0f / 360.0f, new Color(0.0f, 0.0f, 1.0f)));
		alMarkerPoints.add(new ColorMarkerPoint(1.0f, new Color(1.0f, 0.0f, 0.0f)));

		colorMapper = new ColorMapper(alMarkerPoints);

	}

	@Override
	public void drawFullCircle(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		float fRadius = pdDiscToDraw.getCurrentWidth();

		gl.glPushName(pickingManager.getPickingID(viewID,
				PickingType.RAD_HIERARCHY_PDISC_SELECTION, pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((!pdDiscToDraw.isAChildDrawn()) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, pdDiscToDraw.getCurrentInnerRadius(), fRadius,
					pdDiscToDraw.getCurrentStartAngle(), pdDiscToDraw.getCurrentAngle());
		}

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR, 0);
		GLPrimitives.renderCircle(glu, fRadius, iNumSlicesPerFullDisc);

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_COLOR, 0);
		GLPrimitives.renderCircleBorder(gl, glu, fRadius, iNumSlicesPerFullDisc,
				RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

		gl.glPopAttrib();
		gl.glPopName();
	}

	@Override
	public void drawPartialDisc(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		float fStartAngle = pdDiscToDraw.getCurrentStartAngle();
		float fAngle = pdDiscToDraw.getCurrentAngle();
		float fInnerRadius = pdDiscToDraw.getCurrentInnerRadius();
		float fWidth = pdDiscToDraw.getCurrentWidth();

		gl.glPushName(pickingManager.getPickingID(viewID,
				PickingType.RAD_HIERARCHY_PDISC_SELECTION, pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((!pdDiscToDraw.isAChildDrawn()) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, fInnerRadius, fWidth, fStartAngle, fAngle);
		}

		gl.glColor4fv(getColor(pdDiscToDraw), 0);

		GLPrimitives.renderPartialDisc(glu, fInnerRadius, fInnerRadius + fWidth,
				fStartAngle, fAngle, iNumSlicesPerFullDisc);

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_COLOR, 0);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius,
				fInnerRadius + fWidth, fStartAngle, fAngle, iNumSlicesPerFullDisc,
				RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

		gl.glPopAttrib();
		gl.glPopName();
	}

	@Override
	public EPDDrawingStrategyType getDrawingStrategyType() {
		return EPDDrawingStrategyType.RAINBOW_COLOR;
	}

	@Override
	public float[] getColor(PartialDisc disc) {

		float fStartAngle = disc.getCurrentStartAngle();
		float fAngle = disc.getCurrentAngle();
		float fMidAngle = fStartAngle + (fAngle / 2.0f);

		while (fMidAngle >= 360) {
			fMidAngle -= 360;
		}
		while (fMidAngle < 0) {
			fMidAngle += 360;
		}

		float fArRGB[] = colorMapper.getColor(fMidAngle / 360.0f);

		return new float[] { fArRGB[0], fArRGB[1], fArRGB[2], fTransparency };
	}

}
