package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class PDDrawingStrategyLabelDecorator
	extends PDDrawingStrategyDecorator {

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		if (drawingStrategy != null) {
			drawingStrategy.drawFullCircle(gl, glu, pdDiscToDraw);
		}

		Label label = new Label(0, 0, 0, pdDiscToDraw.getDrawingStrategyDepth());
		label.addLine(pdDiscToDraw.getName());
		label.addLine("Coefficient: " + pdDiscToDraw.getCoefficient());
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

		label.addLine(pdDiscToDraw.getName());
		label.addLine("Coefficient: " + pdDiscToDraw.getCoefficient());
		LabelManager.get().addLabel(label);
	}

}
