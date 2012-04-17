/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.ANode;
import org.caleydo.view.linearizedpathway.node.ANodeAttributeRenderer;
import org.caleydo.view.linearizedpathway.node.CompoundNode;

/**
 * Base class for modes of a {@link CompoundNode}.
 * 
 * @author Christian
 * 
 */
public abstract class ACompoundNodeMode extends ALinearizeableNodeMode {

	protected final static float[] DEFAULT_CIRCLE_COLOR = new float[] { 1, 1, 1, 0 };

	protected PixelGLConverter pixelGLConverter;

	/**
	 * Fill color of the rendered circle.
	 */
	protected float[] circleColor = DEFAULT_CIRCLE_COLOR;

	/**
	 * @param view
	 */
	public ACompoundNodeMode(GLLinearizedPathway view) {
		super(view);
		this.pixelGLConverter = view.getPixelGLConverter();
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		float height = pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels());
		Vec3f position = node.getPosition();

		gl.glPushName(pickingManager.getPickingID(view.getID(),
				PickingType.LINEARIZABLE_NODE.name(), node.getNodeId()));
		gl.glPushMatrix();
		gl.glTranslatef(position.x(), position.y(), position.z());
		gl.glColor4fv(circleColor, 0);
		GLPrimitives.renderCircle(glu, height / 2.0f, 16);
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderCircleBorder(gl, glu, height / 2.0f, 16, 0.1f);
		gl.glPopMatrix();
		gl.glPopName();

		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl);
		}

	}

	@Override
	public int getMinHeightPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

	@Override
	public int getMinWidthPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

}
