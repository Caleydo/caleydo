/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
