/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
