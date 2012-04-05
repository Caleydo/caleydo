/**
 * 
 */
package org.caleydo.view.linearizedpathway;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * @author Christian
 * 
 */
public abstract class ANodeRenderer {

	protected PixelGLConverter pixelGLConverter;

	/**
	 * Position of the node (center).
	 */
	protected Vec3f position;

	/**
	 * Height of the node in Pixels.
	 */
	protected int heightPixels;

	/**
	 * Width of the node in Pixels.
	 */
	protected int widthPixels;
	
	/**
	 * The number of rows that show the data associated with this node renderer.
	 */
	protected int numAssociatedRows = 0;

	public ANodeRenderer(PixelGLConverter pixelGLConverter) {
		this.pixelGLConverter = pixelGLConverter;
	}

	/**
	 * Renders the node.
	 * 
	 * @param gl
	 */
	public abstract void render(GL2 gl, GLU glu);

	/**
	 * @return The point a connection line can connect to at the top of the
	 *         node.
	 */
	public Vec3f getTopConnectionPoint() {
		return new Vec3f(position.x(), position.y()
				+ pixelGLConverter.getGLHeightForPixelHeight(heightPixels) / 2.0f,
				position.z());
	}

	/**
	 * @return The point a connection line can connect to at the bottom of the
	 *         node.
	 */
	public Vec3f getBottomConnectionPoint() {
		return new Vec3f(position.x(), position.y()
				- pixelGLConverter.getGLHeightForPixelHeight(heightPixels) / 2.0f,
				position.z());
	}

	/**
	 * @return The point a connection line can connect to at the left side of
	 *         the node.
	 */
	public Vec3f getLeftConnectionPoint() {
		return new Vec3f(position.x()
				+ pixelGLConverter.getGLWidthForPixelWidth(widthPixels) / 2.0f,
				position.y(), position.z());
	}

	/**
	 * @param position
	 *            setter, see {@link #position}
	 */
	public void setPosition(Vec3f position) {
		this.position = position;
	}

	/**
	 * @return the position, see {@link #position}
	 */
	public Vec3f getPosition() {
		return position;
	}

	/**
	 * @param heightPixels
	 *            setter, see {@link #heightPixels}
	 */
	public void setHeightPixels(int heightPixels) {
		this.heightPixels = heightPixels;
	}

	/**
	 * @return the heightPixels, see {@link #heightPixels}
	 */
	public int getHeightPixels() {
		return heightPixels;
	}

	/**
	 * @param widthPixels
	 *            setter, see {@link #widthPixels}
	 */
	public void setWidthPixels(int widthPixels) {
		this.widthPixels = widthPixels;
	}

	/**
	 * @return the widthPixels, see {@link #widthPixels}
	 */
	public int getWidthPixels() {
		return widthPixels;
	}
	
	/**
	 * @param numAssociatedRows setter, see {@link #numAssociatedRows}
	 */
	public void setNumAssociatedRows(int numAssociatedRows) {
		this.numAssociatedRows = numAssociatedRows;
	}
	
	/**
	 * @return the numAssociatedRows, see {@link #numAssociatedRows}
	 */
	public int getNumAssociatedRows() {
		return numAssociatedRows;
	}

}
