/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLPrimitives;

/**
 * PDDrawingStrategyExpressionColor draws the partial discs using gene
 * expression color. The color value is determinded by using gene expression
 * color mapping on the partial disc's average gene expression value.
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategyExpressionColor extends APDDrawingStrategyChildIndicator {

	ColorMapper colorMapper;

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
	public PDDrawingStrategyExpressionColor(ColorMapper colorMapper,
			PickingManager pickingManager, int viewID) {
		super(pickingManager, viewID);
		this.colorMapper = colorMapper;
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
		return EPDDrawingStrategyType.EXPRESSION_COLOR;
	}

	@Override
	public float[] getColor(PartialDisc disc) {
		float fAverageExpressionValue;

		AHierarchyElement<?> hierarchyData = disc.getHierarchyData();
		ClusterNode clusterNode = null;
		if (hierarchyData instanceof ClusterNode) {
			clusterNode = (ClusterNode) hierarchyData;
			fAverageExpressionValue = clusterNode.getAverageExpressionValue();
		} else {
			fAverageExpressionValue = 0.0f;
		}

		float fArRGB[] = colorMapper.getColor(fAverageExpressionValue);

		return new float[] { fArRGB[0], fArRGB[1], fArRGB[2], fTransparency };
	}

}
