/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;

/**
 * @author Christian
 * 
 */
public abstract class ANode {

	public final static int DEFAULT_HEIGHT_PIXELS = 20;
	public final static int DEFAULT_WIDTH_PIXELS = 70;

	protected PixelGLConverter pixelGLConverter;

	protected int nodeId;

	/**
	 * Position of the node (center).
	 */
	protected Vec3f position;

	/**
	 * Height of the node in Pixels.
	 */
	protected int heightPixels = DEFAULT_HEIGHT_PIXELS;

	/**
	 * Width of the node in Pixels.
	 */
	protected int widthPixels = DEFAULT_WIDTH_PIXELS;

	/**
	 * The number of rows that show the data associated with this node renderer.
	 */
	protected int numAssociatedRows = 0;

	protected GLLinearizedPathway view;

	protected PickingManager pickingManager;

	public ANode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view, int nodeId) {
		this.pixelGLConverter = pixelGLConverter;
		this.view = view;
		this.pickingManager = view.getPickingManager();
		this.nodeId = nodeId;
		registerPickingListeners();
	}

	/**
	 * Method that is automatically called upon node creation and intended to
	 * register all picking listeners for this node.
	 */
	protected abstract void registerPickingListeners();

	/**
	 * Method that shall be called when the node is no longer needed to
	 * unregister its picking listeners.
	 */
	public abstract void unregisterPickingListeners();

	/**
	 * Renders the node.
	 * 
	 * @param gl
	 */
	public abstract void render(GL2 gl, GLU glu);

	/**
	 * @return A {@link PathwayVertexRep} object that is representative for this
	 *         node.
	 */
	public abstract PathwayVertexRep getPathwayVertexRep();

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
				- pixelGLConverter.getGLWidthForPixelWidth(widthPixels) / 2.0f,
				position.y(), position.z());
	}

	/**
	 * @return The point a connection line can connect to at the right side of
	 *         the node.
	 */
	public Vec3f getRightConnectionPoint() {
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
	 * @param numAssociatedRows
	 *            setter, see {@link #numAssociatedRows}
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

	/**
	 * @return The minimum height that is required to render this node properly.
	 */
	public abstract int getMinRequiredHeightPixels();

}
