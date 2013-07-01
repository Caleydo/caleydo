/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.path.node;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.PickingManager;

/**
 * Base class for all nodes that can be displayed in the linearized pathway view.
 *
 * @author Christian
 *
 */
public abstract class ANode implements ILabelProvider {

	// public final static int DEFAULT_HEIGHT_PIXELS = 20;
	// public final static int DEFAULT_WIDTH_PIXELS = 70;

	protected PixelGLConverter pixelGLConverter;

	/**
	 * Position of the node center.
	 */
	protected Vec3f position;

	protected AGLView view;

	protected PickingManager pickingManager;

	/**
	 * Determines whether the node shall be pickable.
	 */
	protected boolean isPickable = true;

	public ANode(AGLView view) {
		this.pixelGLConverter = view.getPixelGLConverter();
		this.view = view;
		this.pickingManager = view.getPickingManager();
	}

	/**
	 * Method that initializes the node. This should be called before first time use.
	 */
	public abstract void init();

	/**
	 * Method that removes any resources used by the node. This shall be called when the node is no longer needed.
	 */
	public abstract void destroy();

	/**
	 * Renders the node.
	 *
	 * @param gl
	 */
	public abstract void render(GL2 gl, GLU glu);

	/**
	 * Renders the highlight of a node.
	 *
	 * @param gl
	 * @param glu
	 */
	public abstract void renderHighlight(GL2 gl, GLU glu);

	/**
	 * @return The point a connection line can connect to at the top of the node.
	 */
	public Vec3f getTopConnectionPoint() {
		return new Vec3f(position.x(), position.y() + getHeight() / 2.0f, position.z());
	}

	/**
	 * @return The point a connection line can connect to at the bottom of the node.
	 */
	public Vec3f getBottomConnectionPoint() {
		return new Vec3f(position.x(), position.y() - getHeight() / 2.0f, position.z());
	}

	/**
	 * @return The point a connection line can connect to at the left side of the node.
	 */
	public Vec3f getLeftConnectionPoint() {
		return new Vec3f(position.x() - getWidth() / 2.0f, position.y(), position.z());
	}

	/**
	 * @return The point a connection line can connect to at the right side of the node.
	 */
	public Vec3f getRightConnectionPoint() {
		return new Vec3f(position.x() + getWidth() / 2.0f, position.y(), position.z());
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
	 * @return the height of the node in pixels.
	 */
	public abstract int getHeightPixels();

	/**
	 * @return the width of the node in pixels.
	 */
	public abstract int getWidthPixels();

	/**
	 * @return GL height of the nodes.
	 */
	public float getHeight() {
		return pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());
	}

	public float getWidth() {
		return pixelGLConverter.getGLHeightForPixelHeight(getWidthPixels());
	}

	/**
	 * @param isPickable
	 *            setter, see {@link isPickable}
	 */
	public void setPickable(boolean isPickable) {
		this.isPickable = isPickable;
	}

	/**
	 * @return the isPickable, see {@link #isPickable}
	 */
	public boolean isPickable() {
		return isPickable;
	}

}
