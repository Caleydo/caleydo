package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

public class OverviewDetailBandRenderer extends LayoutRenderer {

	private GLBrick leftBrick;
	private GLBrick rightBrick;
	private ConnectionBandRenderer bandRenderer;

	public OverviewDetailBandRenderer(GLBrick leftBrick, GLBrick rightBrick) {
		this.leftBrick = leftBrick;
		this.rightBrick = rightBrick;
		bandRenderer = new ConnectionBandRenderer();
	}

	@Override
	public void render(GL2 gl) {

		ElementLayout leftLayout = leftBrick.getLayout();
		ElementLayout rightLayout = rightBrick.getLayout();

		float leftX = leftLayout.getTranslateX() + leftLayout.getSizeScaledX();
		float leftTopY = leftLayout.getTranslateY()
				+ leftLayout.getSizeScaledY();
		float leftBottomY = leftLayout.getTranslateY();

		float[] leftTopPos = new float[] { leftX, leftTopY };
		float[] leftBottomPos = new float[] { leftX, leftBottomY };

		float rightX = rightLayout.getTranslateX();
		float rightTopY = rightLayout.getTranslateY()
				+ rightLayout.getSizeScaledY();
		float rightBottomY = rightLayout.getTranslateY();

		float[] rightTopPos = new float[] { rightX, rightTopY };
		float[] rightBottomPos = new float[] { rightX, rightBottomY };

		gl.glColor4f(0, 0, 0, 1);

		gl.glTranslatef(-elementLayout.getTranslateX(),
				-elementLayout.getTranslateY(), 0);
		
		bandRenderer.init(gl);

		bandRenderer.renderSingleBand(gl, leftTopPos, leftBottomPos,
				rightTopPos, rightBottomPos, false, x * 0.4f, 0, false,
				new float[] { 0.4f, 0.4f, 0.4f }, 1);

//		gl.glBegin(GL2.GL_QUADS);
//
//		gl.glVertex3f(leftX, leftBottomY, 2);
//		gl.glVertex3f(rightX, rightBottomY, 2);
//		gl.glVertex3f(rightX, rightTopY, 2);
//		gl.glVertex3f(leftX, leftTopY, 2);
//
//		gl.glEnd();
		gl.glTranslatef(elementLayout.getTranslateX(),
				elementLayout.getTranslateY(), 0);

	}

}
