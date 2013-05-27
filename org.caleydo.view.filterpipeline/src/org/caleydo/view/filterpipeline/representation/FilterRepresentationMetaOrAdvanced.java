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
package org.caleydo.view.filterpipeline.representation;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.filter.RecordMetaOrFilter;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * @author Thomas Geymayer
 *
 */
public class FilterRepresentationMetaOrAdvanced extends FilterRepresentationMetaOr {
	private int displayListOutput = -1;
	private boolean displayListDirty = true;
	private float oldHeightRight = 0;

	private static final float spacingLeft = 0.1f;
	private static final float spacingRight = 0.25f;
	private static final float subfilterScalingX = 1.f - spacingLeft - spacingRight;

	private static final float spacingTop = 0.08f;
	private static final float spacingBottom = 0.03f;
	private static final float subfilterScalingY = 1.f - spacingTop - spacingBottom;

	private static int NUMBER_OF_SPLINE_POINTS = 35;

	private ConnectionBandRenderer inputRenderer = new ConnectionBandRenderer();

	public FilterRepresentationMetaOrAdvanced(FilterPipelineRenderStyle renderStyle,
			PickingManager pickingManager, int viewId) {
		super(renderStyle, pickingManager, viewId);
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer) {
		if (sizesDirty)
			calculateSizes();

		heightLeft = getHeightLeft();
		heightRight = getHeightRight();

		if (heightRight != oldHeightRight) {
			displayListDirty = true;
			oldHeightRight = heightRight;
		}

		gl.glPushName(iPickingID);
		renderBasicShape(gl, textRenderer, renderStyle.FILTER_OR_COLOR);

		float scaleY = calculateFilterScalingY();
		float offsetY = scaleY * heightLeft;
		scaleY *= 0.9f;

		float subFiltersBottom = vPos.y() + spacingBottom * heightLeft;
		Vec2f curPos = new Vec2f(vPos.x() + spacingLeft * vSize.x(), subFiltersBottom);
		float delta = heightLeft - heightRight;
		float subFilterWidth = subfilterScalingX * vSize.x();
		float subFilterRight = curPos.x() + subFilterWidth;
		float filterRight = vPos.x() + vSize.x();

		for (int i = 0; i < subFilterSizes.length; ++i, curPos.setY(curPos.y() + offsetY)) {
			heightRight = vSize.y() * (subFilterSizes[i] / 100.f);

			gl.glPushName(pickingManager.getPickingID(viewId,
					PickingType.FILTERPIPE_SUB_FILTER, i));
			renderShape(gl, GL2.GL_QUADS, curPos, subFilterWidth, scaleY * heightLeft,
					scaleY * heightRight, renderStyle.getFilterColor(i),
					Z_POS_BODY + 0.1f);
			gl.glLineWidth(1);
			renderShape(gl, GL.GL_LINE_LOOP, curPos, subFilterWidth,
					scaleY * heightLeft, scaleY * heightRight,
					renderStyle.FILTER_BORDER_COLOR, Z_POS_BORDER + 0.1f);
			gl.glPopName();

			textRenderer.renderText(gl, ((RecordMetaOrFilter) filter.getFilter())
					.getFilterList().get(i).getLabel(), curPos.x() + 0.02f,
					curPos.y() + 0.02f, Z_POS_TEXT, 0.0035f, 18);

			renderInputBand(gl, new Vec2f(vPos.x(), vPos.y() + heightLeft), new Vec2f(
					curPos.x(), curPos.y() + scaleY * heightLeft), vPos, curPos,
					0.7f - ((float) i / subFilterSizes.length) * 0.6f, delta,
					renderStyle.getFilterColorCombined(i));

			if (mouseOverItem == i) {
				// render mouse over
				gl.glLineWidth(SelectionType.MOUSE_OVER.getLineWidth());
				renderShape(gl, GL.GL_LINE_LOOP, curPos, subFilterWidth, scaleY
						* heightLeft, scaleY * heightRight,
						SelectionType.MOUSE_OVER.getColor(), Z_POS_MARK);
			}

		}

		gl.glPopName();

		// reset height
		heightRight = getHeightRight();

		if (displayListDirty) {
			displayListDirty = false;

			if (displayListOutput < 0)
				displayListOutput = gl.glGenLists(1);

			gl.glNewList(displayListOutput, GL2.GL_COMPILE);

			// Steps of individual sub filters
			int[] currentSteps = new int[subFilterSizes.length];

			// Steps of output of total meta filter
			int outputSteps = 0;

			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glLineWidth(1);

			for (Intersection intersection : intersections) {
				float filterBottom = vPos.y() + vSize.y() * (outputSteps / 100.f);
				float height = vSize.y() * (intersection.numElements / 100.f);

				renderOutputBand(gl,
						new float[] { filterRight, filterBottom, Z_POS_BODY },
						new float[] { filterRight, filterBottom + height, Z_POS_BODY },
						new float[] { filterRight + 0.058f * vSize.x(),
								filterBottom + height, Z_POS_BODY }, new float[] {
								filterRight + 0.058f * vSize.x(), filterBottom,
								Z_POS_BODY }, new float[] { 0.8f, 0.8f, 0.8f, .5f },
						new float[] { 0f, 0f, 0f, 1f });

				for (int filterId : intersection.filterIds) {
					float subFilterBottom = subFiltersBottom + filterId * offsetY
							+ scaleY * vSize.y() * (currentSteps[filterId] / 100.f);

					renderOutputBand(gl, new float[] { subFilterRight, subFilterBottom,
							Z_POS_BODY }, new float[] { subFilterRight,
							subFilterBottom + scaleY * height, Z_POS_BODY }, new float[] {
							filterRight, filterBottom + height, Z_POS_BODY },
							new float[] { filterRight, filterBottom, Z_POS_BODY },
							renderStyle.getColorSubfilterOutput(filterId),
							renderStyle.getColorSubfilterOutputBorder(filterId));

					currentSteps[filterId] += intersection.numElements;
				}

				outputSteps += intersection.numElements;
			}

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glEndList();
		}

		gl.glCallList(displayListOutput);

		// render selection/mouseover if needed
		if (selectionType != SelectionType.NORMAL && mouseOverItem < 0) {
			gl.glLineWidth((selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
					.getLineWidth() : SelectionType.MOUSE_OVER.getLineWidth());

			renderShape(
					gl,
					GL.GL_LINE_LOOP,
					(selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
							.getColor() : SelectionType.MOUSE_OVER.getColor(), Z_POS_MARK);
		}
	}

	/**
	 * Calculate the scaling factor for the subfilters
	 *
	 * @return
	 */
	private float calculateFilterScalingY() {
		// calculate the available space for the subfilters
		float delta = heightLeft - heightRight;
		float availableHeightLeft = subfilterScalingY
				* (heightLeft - spacingLeft * delta);
		float availableHeightRight = subfilterScalingY
				* (heightRight + spacingRight * delta);

		float scalingLeft = availableHeightLeft / (subFilterSizes.length * heightLeft);

		// the last filter only uses the space it needs to allow the whole
		// filters use more space upwards
		int totalElementsRight = (subFilterSizes.length - 1) * filter.getInput().size()
				+ subFilterSizes[subFilterSizes.length - 1];
		float totalHeightRight = vSize.y() * (totalElementsRight / 100.f);

		float scalingRight = availableHeightRight / totalHeightRight;

		return scalingLeft < scalingRight ? scalingLeft : scalingRight;
	}

	/**
	 * Render the input as an nurbs band
	 *
	 * @param gl
	 */
	private void renderInputBand(GL2 gl, Vec2f topLeft, Vec2f topRight, Vec2f bottomLeft,
			Vec2f bottomRight, float horizontScale, float delta, float[] color) {
		ArrayList<Vec3f> topInputPoints = new ArrayList<Vec3f>();
		float inputWidth = topRight.x() - topLeft.x();

		// top curve
		topInputPoints.add(new Vec3f(topLeft.x(), topLeft.y(), Z_POS_BODY));
		topInputPoints.add(new Vec3f(topLeft.x() + horizontScale * inputWidth, topLeft
				.y() - spacingLeft * delta, Z_POS_BODY));
		topInputPoints.add(new Vec3f(topLeft.x() + horizontScale * inputWidth, topRight
				.y(), Z_POS_BODY));
		topInputPoints.add(new Vec3f(topRight.x(), topRight.y(), Z_POS_BODY));

		// bottom curve
		ArrayList<Vec3f> bottomInputPoints = new ArrayList<Vec3f>();
		bottomInputPoints.add(new Vec3f(bottomRight.x(), bottomRight.y(), Z_POS_BODY));
		bottomInputPoints.add(new Vec3f(bottomLeft.x() + horizontScale * inputWidth,
				bottomRight.y(), Z_POS_BODY));
		bottomInputPoints.add(new Vec3f(bottomLeft.x() + horizontScale * inputWidth,
				bottomLeft.y(), Z_POS_BODY));
		bottomInputPoints.add(new Vec3f(bottomLeft.x(), bottomLeft.y(), Z_POS_BODY));

		NURBSCurve topCurve = new NURBSCurve(topInputPoints, NUMBER_OF_SPLINE_POINTS);

		// Band border
		gl.glLineWidth(1);
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);
		gl.glBegin(GL.GL_LINE_STRIP);
		{
			for (Vec3f point : topCurve.getCurvePoints())
				gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();

		// Band border
		NURBSCurve bottomCurve = new NURBSCurve(bottomInputPoints,
				NUMBER_OF_SPLINE_POINTS);
		gl.glBegin(GL.GL_LINE_STRIP);
		{
			for (Vec3f point : bottomCurve.getCurvePoints())
				gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();

		ArrayList<Vec3f> points = topCurve.getCurvePoints();
		points.addAll(bottomCurve.getCurvePoints());
		gl.glColor4fv(color, 0);
		inputRenderer.init(gl); // TODO
		inputRenderer.render(gl, points);
	}

}
