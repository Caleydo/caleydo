/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLPrimitives;

/**
 * PDDrawingStrategySelected is responsible for drawing partial discs which have
 * been selected in some fashion. Therefore it is using a predefined fill color
 * and a definable border color (for distinguishing between e.g. Mouse Over and
 * Click).
 *
 * @author Christian Partl
 */
public class PDDrawingStrategySelected extends APDDrawingStrategyChildIndicator {

	private float[] fArBorderColor;

	/**
	 * Constructor.
	 *
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the
	 *            drawn elements.
	 * @param viewID
	 *            ID of the view where the elements will be displayed. Needed
	 *            for picking.
	 */
	public PDDrawingStrategySelected(PickingManager pickingManager, int viewID) {
		super(pickingManager, viewID);
		fArBorderColor = SelectionType.MOUSE_OVER.getColor().getRGBA();
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

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_MOUSE_OVER_COLOR, 0);
		GLPrimitives.renderCircle(glu, fRadius, iNumSlicesPerFullDisc);

		gl.glColor4fv(fArBorderColor, 0);
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

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_MOUSE_OVER_COLOR, 0);
		GLPrimitives.renderPartialDisc(glu, fInnerRadius, fInnerRadius + fWidth,
				fStartAngle, fAngle, iNumSlicesPerFullDisc);
		gl.glColor4fv(fArBorderColor, 0);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius,
				fInnerRadius + fWidth, fStartAngle, fAngle, iNumSlicesPerFullDisc,
				RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

		gl.glPopAttrib();
		gl.glPopName();

	}

	/**
	 * Gets the color which is used for drawing the partial disc's border.
	 *
	 * @return RGB-Color which is used to draw the partial disc's border.
	 */
	public float[] getBorderColor() {
		return fArBorderColor;
	}

	/**
	 * Sets the color that shall be used for drawing the partial disc's border.
	 *
	 * @param fArBorderColor
	 *            RGB-Color which shall be used to draw the partial disc's
	 *            border. Only the first three values of the array will be used.
	 */
	public void setBorderColor(float[] fArBorderColor) {
		if (fArBorderColor.length >= 3) {
			this.fArBorderColor = fArBorderColor;
		}
	}

	@Override
	public EPDDrawingStrategyType getDrawingStrategyType() {
		return EPDDrawingStrategyType.SELECTED;
	}

	@Override
	public float[] getColor(PartialDisc disc) {
		return RadialHierarchyRenderStyle.PARTIAL_DISC_MOUSE_OVER_COLOR;
	}

	/**
	 * Gets the coordinates of the connection point that is used for drawing a
	 * connection line to the specified partial disc.
	 *
	 * @param disc
	 *            Partial disc the connection point shall be obtained for.
	 * @param fHierarchyCenterX
	 *            X coordinate of the radial hierarchy center.
	 * @param fHierarchyCenterY
	 *            Y coordinate of the radial hierarchy center.
	 * @param fHierarchyCenterZ
	 *            Z coordinate of the radial hierarchy center.
	 * @return Connection point coordinates as float array with length 3.
	 */
	public float[] getElementRepConnectionPoint(PartialDisc disc,
			float fHierarchyCenterX, float fHierarchyCenterY, float fHierarchyCenterZ) {
		float fStartAngle = disc.getCurrentStartAngle();
		float fInnerRadius = disc.getCurrentInnerRadius();

		// This seemingly awkward angle transformation comes from the fact, that
		// Partial Disc drawing angles
		// start vertically at the top and move clockwise. But here the angle
		// starts horizontally to the right
		// and moves counter-clockwise
		fStartAngle = -1 * (fStartAngle - 90);
		float fStartAngleRadiants = fStartAngle * (float) Math.PI / 180.0f;
		float fConnectionPointX = ((float) Math.cos(fStartAngleRadiants) * fInnerRadius)
				+ fHierarchyCenterX;
		float fConnectionPointY = ((float) Math.sin(fStartAngleRadiants) * fInnerRadius)
				+ fHierarchyCenterY;

		return new float[] { fConnectionPointX, fConnectionPointY, fHierarchyCenterZ };
	}
}
