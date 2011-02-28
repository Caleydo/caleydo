package org.caleydo.view.visbricks.dimensiongroup;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.visbricks.brick.BrickRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Container for a group of dimensions. Manages layouts as well
 * 
 * @author Alexander Lex
 * 
 */
public class DimensionGroup extends Column {

	private Column bottomCol;
	private GLBrick centerBrick;
	private Column topCol;
	private ViewFrustum brickFrustum;

	public DimensionGroup(GLCaleydoCanvas canvas,
			IGLRemoteRenderingView remoteRenderingView) {
		super("dimensionGroup");

		bottomCol = new Column("dimensionGroupColumnBottom");
		appendElement(bottomCol);
		brickFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 0, 0, 0, -4,
				4);

		centerBrick = (GLBrick) GeneralManager.get().getViewGLCanvasManager()
				.createGLView(GLBrick.class, canvas, brickFrustum);
		centerBrick.setRemoteRenderingGLView(remoteRenderingView);
		ElementLayout brickLayout = new ElementLayout("brick");
		BrickRenderer brickRenderer = new BrickRenderer(centerBrick);
		brickLayout.setRenderer(brickRenderer);
		brickLayout.setFrameColor(1, 0, 0, 1);

		centerBrick.setWrappingLayout(brickLayout);

		appendElement(brickLayout);

		topCol = new Column("dimensionGroupColumnTop");

		appendElement(topCol);
	}

	/**
	 * Set the spacing of the arch: the total height in absolute gl values, the
	 * rest in ratio (i.e. the sum of the values has to be 1
	 * 
	 * @param totalArchHeight
	 *            the height of the arch from top to bottom, in abolute gl
	 *            coordinates
	 * @param below
	 *            the ratio size of the space below the arch
	 * @param archThickness
	 *            the ratio thickness in y of the arch
	 * @param above
	 *            the ratio size of the space above the arch
	 */
	public void setArchBounds(float totalArchHeight, float below, float archThickness,
			float above) {
		bottomCol.setRatioSizeY(below);
		topCol.setRatioSizeY(above);
		centerBrick.getWrappingLayout().setRatioSizeY(archThickness);
		brickFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0,
				totalArchHeight * archThickness, 0, totalArchHeight * archThickness, -4,
				4);
		centerBrick.setFrustum(brickFrustum);
	}
}
