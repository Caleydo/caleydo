/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;

/**
 * @author Christian
 * 
 */
public abstract class ALayoutBasedNode extends ANode {

	protected LayoutManager layoutManager;

	/**
	 * @param pixelGLConverter
	 * @param view
	 * @param nodeId
	 */
	public ALayoutBasedNode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
		layoutManager = new LayoutManager(new ViewFrustum(), pixelGLConverter);
		ElementLayout baseLayout = setupLayout();
		layoutManager.setBaseElementLayout(baseLayout);
	}

	/**
	 * Subclasses are intended to setup their layout within this method, which
	 * is automatically called upon node creation.
	 * 
	 * @return The base layout element of the node layout.
	 */
	protected abstract ElementLayout setupLayout();

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		gl.glPushMatrix();
		gl.glTranslatef(position.x() - width / 2.0f, position.y() - height / 2.0f, 0f);
		// layoutManager.setViewFrustum(new ViewFrustum(
		// ECameraProjectionMode.ORTHOGRAPHIC, x - spacingWidth, x
		// + spacingWidth, y - spacingHeight, y + spacingHeight,
		// -1, 20));
		layoutManager.setViewFrustum(new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC,
				0, width, 0, height, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();

	}

}
