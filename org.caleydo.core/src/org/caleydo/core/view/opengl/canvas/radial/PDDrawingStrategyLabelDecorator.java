package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;


public class PDDrawingStrategyLabelDecorator
	extends APDDrawingStrategyDecorator {
	
	public PDDrawingStrategyLabelDecorator() {
		super(null, 0);
	}

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		if (drawingStrategy != null) {
			drawingStrategy.drawFullCircle(gl, glu, pdDiscToDraw);
		}

		Label label = new Label(0, 0, 0, pdDiscToDraw.getDrawingStrategyDepth());
		
		float fAverageExpressionValue = pdDiscToDraw.getAverageExpressionValue();
		float fStandardDeviation = pdDiscToDraw.getStandardDeviation();
		ColorMapping cmExpression =
			ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
		
		float fArRGB[] = cmExpression.getColor(fAverageExpressionValue - fStandardDeviation);
		RectangleItem leftRectangleItem = new RectangleItem(fArRGB, 1);
		
		fArRGB = cmExpression.getColor(fAverageExpressionValue);
		RectangleItem middleRectangleItem = new RectangleItem(fArRGB, 1);
		
		fArRGB = cmExpression.getColor(fAverageExpressionValue + fStandardDeviation);
		RectangleItem rightRectangleItem = new RectangleItem(fArRGB, 1);
		
		TextItem meanItem = new TextItem("Mean/Std-Dev:  ");
		LabelLine expressionLine = new LabelLine();
		expressionLine.addLabelItem(meanItem);
		expressionLine.addLabelItem(leftRectangleItem);
		expressionLine.addLabelItem(middleRectangleItem);
		expressionLine.addLabelItem(rightRectangleItem);
		
		if(pdDiscToDraw.hasChildren()) {
			TextItem numElementsItem = new TextItem("Elements: " + new Integer((int)pdDiscToDraw.getSize()).toString());
			LabelLine numElementsLine = new LabelLine();
			numElementsLine.addLabelItem(numElementsItem);
			
			TextItem hierarchyDepthItem = new TextItem("Hierarchy Depth: " + pdDiscToDraw.getHierarchyDepth());
			LabelLine hierarchyDepthLine = new LabelLine();
			hierarchyDepthLine.addLabelItem(hierarchyDepthItem);
			
			label.addLine(numElementsLine);
			label.addLine(hierarchyDepthLine);
		}
		else {
			TextItem nameItem = new TextItem(pdDiscToDraw.getName());
			LabelLine nameLine = new LabelLine();
			nameLine.addLabelItem(nameItem);
			
			label.addLine(nameLine);
		}
		
		label.addLine(expressionLine);	
		LabelManager.get().addLabel(label);
	}

	@Override
	public void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		if (drawingStrategy != null) {
			drawingStrategy.drawPartialDisc(gl, glu, pdDiscToDraw);
		}

		float fCenterRadius = pdDiscToDraw.getCurrentInnerRadius() + (pdDiscToDraw.getCurrentWidth() / 2.0f);
		float fMidAngle = pdDiscToDraw.getCurrentStartAngle() + (pdDiscToDraw.getCurrentAngle() / 2.0f);

		// This seemingly awkward angle transformation comes from the fact, that Partial Disk drawing angles
		// start vertically at the top and move clockwise. But here the angle starts horizontally to the right
		// and moves counter-clockwise
		fMidAngle = -1 * (fMidAngle - 90);
		float fMidAngleRadiants = fMidAngle * (float) Math.PI / 180.0f;
		float fSegmentXCenter = (float) Math.cos(fMidAngleRadiants) * fCenterRadius;
		float fSegmentYCenter = (float) Math.sin(fMidAngleRadiants) * fCenterRadius;

		Label label =
			new Label(fSegmentXCenter, fSegmentYCenter, fCenterRadius, pdDiscToDraw.getDrawingStrategyDepth());

		float fAverageExpressionValue = pdDiscToDraw.getAverageExpressionValue();
		float fStandardDeviation = pdDiscToDraw.getStandardDeviation();
		ColorMapping cmExpression =
			ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
		
		float fArRGB[] = cmExpression.getColor(fAverageExpressionValue - fStandardDeviation);
		RectangleItem leftRectangleItem = new RectangleItem(fArRGB, 1);
		
		fArRGB = cmExpression.getColor(fAverageExpressionValue);
		RectangleItem middleRectangleItem = new RectangleItem(fArRGB, 1);
		
		fArRGB = cmExpression.getColor(fAverageExpressionValue + fStandardDeviation);
		RectangleItem rightRectangleItem = new RectangleItem(fArRGB, 1);
		
		TextItem meanItem = new TextItem("Mean/Std-Dev:  ");
		LabelLine expressionLine = new LabelLine();
		expressionLine.addLabelItem(meanItem);
		expressionLine.addLabelItem(leftRectangleItem);
		expressionLine.addLabelItem(middleRectangleItem);
		expressionLine.addLabelItem(rightRectangleItem);
		
		if(pdDiscToDraw.hasChildren()) {
			TextItem numElementsItem = new TextItem("Elements: " + new Integer((int)pdDiscToDraw.getSize()).toString());
			LabelLine numElementsLine = new LabelLine();
			numElementsLine.addLabelItem(numElementsItem);
			
			TextItem hierarchyDepthItem = new TextItem("Hierarchy Depth: " + pdDiscToDraw.getHierarchyDepth());
			LabelLine hierarchyDepthLine = new LabelLine();
			hierarchyDepthLine.addLabelItem(hierarchyDepthItem);
			
			label.addLine(numElementsLine);
			label.addLine(hierarchyDepthLine);
		}
		else {
			TextItem nameItem = new TextItem(pdDiscToDraw.getName());
			LabelLine nameLine = new LabelLine();
			nameLine.addLabelItem(nameItem);
			
			label.addLine(nameLine);
		}
		
		label.addLine(expressionLine);	
		LabelManager.get().addLabel(label);
	}

}
