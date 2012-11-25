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

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.util.mapping.color.ColorMapper;

/**
 * PDDrawingStrategyLabelDecorator sets up a {@link LabelInfo} for the partial
 * disc that shall be drawn and adds it to the {@link LabelManager}.
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategyLabelDecorator extends APDDrawingStrategyDecorator {

	private ColorMapper colorMapper;

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
	public PDDrawingStrategyLabelDecorator(ColorMapper colorMapper) {
		super(null, 0);
		this.colorMapper = colorMapper;
	}

	@Override
	public void drawFullCircle(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {
		if (drawingStrategy != null) {
			drawingStrategy.drawFullCircle(gl, glu, pdDiscToDraw);
		}

		LabelInfo labelInfo = new LabelInfo(0, 0, 0,
				pdDiscToDraw.getDrawingStrategyDepth());

		AHierarchyElement<?> hierarchyData = pdDiscToDraw.getHierarchyData();
		ClusterNode clusterNode = null;

		if (hierarchyData instanceof ClusterNode) {
			clusterNode = (ClusterNode) hierarchyData;
			setupClusterNodeLabel(pdDiscToDraw, clusterNode, labelInfo);
		} else {
			setupDefaultLabel(hierarchyData, labelInfo);
		}
		LabelManager.get().addLabel(labelInfo);
	}

	@Override
	public void drawPartialDisc(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {
		if (drawingStrategy != null) {
			drawingStrategy.drawPartialDisc(gl, glu, pdDiscToDraw);
		}

		float fCenterRadius = pdDiscToDraw.getCurrentInnerRadius()
				+ (pdDiscToDraw.getCurrentWidth() / 2.0f);
		float fMidAngle = pdDiscToDraw.getCurrentStartAngle()
				+ (pdDiscToDraw.getCurrentAngle() / 2.0f);

		// This seemingly awkward angle transformation comes from the fact, that
		// Partial Disc drawing angles
		// start vertically at the top and move clockwise. But here the angle
		// starts horizontally to the right
		// and moves counter-clockwise
		fMidAngle = -1 * (fMidAngle - 90);
		float fMidAngleRadiants = fMidAngle * (float) Math.PI / 180.0f;
		float fSegmentXCenter = (float) Math.cos(fMidAngleRadiants) * fCenterRadius;
		float fSegmentYCenter = (float) Math.sin(fMidAngleRadiants) * fCenterRadius;

		LabelInfo labelInfo = new LabelInfo(fSegmentXCenter, fSegmentYCenter,
				fCenterRadius, pdDiscToDraw.getDrawingStrategyDepth());

		AHierarchyElement<?> hierarchyData = pdDiscToDraw.getHierarchyData();
		ClusterNode clusterNode = null;

		if (hierarchyData instanceof ClusterNode) {
			clusterNode = (ClusterNode) hierarchyData;
			setupClusterNodeLabel(pdDiscToDraw, clusterNode, labelInfo);
		} else {
			setupDefaultLabel(hierarchyData, labelInfo);
		}

		LabelManager.get().addLabel(labelInfo);
	}

	@Override
	public EPDDrawingStrategyType getDrawingStrategyType() {
		return EPDDrawingStrategyType.LABEL_DECORATOR;
	}

	@Override
	public APDDrawingStrategyDecorator clone() {
		PDDrawingStrategyLabelDecorator clone = new PDDrawingStrategyLabelDecorator(
				colorMapper);
		clone.setDrawingStrategy(drawingStrategy);
		return clone;
	}

	/**
	 * Sets up a label specific for the representation of cluster nodes.
	 * 
	 * @param pdDiscToDraw
	 *            The partial disc that corresponds to the label.
	 * @param clusterNode
	 *            The cluster node the label shall be set up for.
	 * @param labelInfo
	 *            LabelInfo object that shall contain the label data.
	 */
	private void setupClusterNodeLabel(PartialDisc pdDiscToDraw, ClusterNode clusterNode,
			LabelInfo labelInfo) {

		float fAverageExpressionValue = clusterNode.getAverageExpressionValue();
		float fStandardDeviation = clusterNode.getStandardDeviation();

		float fArRGB[] = colorMapper.getColor(fAverageExpressionValue
				- fStandardDeviation);
		RectangleItem leftRectangleItem = new RectangleItem(fArRGB, 1, 1, true);

		fArRGB = colorMapper.getColor(fAverageExpressionValue);
		RectangleItem middleRectangleItem = new RectangleItem(fArRGB, 1, 1, true);

		fArRGB = colorMapper.getColor(fAverageExpressionValue + fStandardDeviation);
		RectangleItem rightRectangleItem = new RectangleItem(fArRGB, 1, 1, true);

		TextItem meanItem = new TextItem("Mean/Std-Dev:  ");
		LabelLine expressionLine = new LabelLine();
		expressionLine.addLabelItem(meanItem);
		expressionLine.addLabelItem(leftRectangleItem);
		expressionLine.addLabelItem(middleRectangleItem);
		expressionLine.addLabelItem(rightRectangleItem);

		if (pdDiscToDraw.hasChildren()) {

			TextItem numElementsItem = new TextItem("Elements: "
					+ new Integer((int) pdDiscToDraw.getSize()).toString());
			LabelLine numElementsLine = new LabelLine();
			numElementsLine.addLabelItem(numElementsItem);

			TextItem hierarchyDepthItem = new TextItem("Hierarchy Depth: "
					+ pdDiscToDraw.getDepth());
			LabelLine hierarchyDepthLine = new LabelLine();
			hierarchyDepthLine.addLabelItem(hierarchyDepthItem);

			labelInfo.addLine(numElementsLine);
			labelInfo.addLine(hierarchyDepthLine);
		} else {

			TextItem nameItem = new TextItem(clusterNode.getLabel());
			LabelLine nameLine = new LabelLine();
			nameLine.addLabelItem(nameItem);

			labelInfo.addLine(nameLine);
		}

		labelInfo.addLine(expressionLine);
	}

	/**
	 * Sets up a default label for the given hierarchy data object.
	 * 
	 * @param hierarchyData
	 *            Hierarchy data object the label is set up for.
	 * @param labelInfo
	 *            LabelInfo object that shall contain the label data.
	 */
	private void setupDefaultLabel(AHierarchyElement<?> hierarchyData, LabelInfo labelInfo) {

		TextItem textItem = new TextItem(hierarchyData.getLabel());
		LabelLine labelLine = new LabelLine();
		labelLine.addLabelItem(textItem);
		labelInfo.addLine(labelLine);
	}

	@Override
	public float[] getColor(PartialDisc disc) {
		return drawingStrategy.getColor(disc);
	}
}
