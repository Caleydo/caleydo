package org.caleydo.view.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * PDDrawingStrategyLabelDecorator sets up a {@link LabelInfo} for the partial
 * disc that shall be drawn and adds it to the {@link LabelManager}.
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategyLabelDecorator extends APDDrawingStrategyDecorator {

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the
	 *            drawn elements.
	 * @param iViewID
	 *            ID of the view where the elements will be displayed. Needed
	 *            for picking.
	 */
	public PDDrawingStrategyLabelDecorator() {
		super(null, 0);
	}

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
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
	public void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
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
		PDDrawingStrategyLabelDecorator clone = new PDDrawingStrategyLabelDecorator();
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
		ColorMapping cmExpression = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		float fArRGB[] = cmExpression.getColor(fAverageExpressionValue
				- fStandardDeviation);
		RectangleItem leftRectangleItem = new RectangleItem(fArRGB, 1, 1, true);

		fArRGB = cmExpression.getColor(fAverageExpressionValue);
		RectangleItem middleRectangleItem = new RectangleItem(fArRGB, 1, 1, true);

		fArRGB = cmExpression.getColor(fAverageExpressionValue + fStandardDeviation);
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
