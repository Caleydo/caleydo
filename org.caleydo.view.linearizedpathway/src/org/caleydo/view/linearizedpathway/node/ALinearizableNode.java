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
package org.caleydo.view.linearizedpathway.node;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.mode.ALinearizeableNodeMode;

/**
 * Base class for all nodes that can be linearized.
 * 
 * @author Christian
 * 
 */
public abstract class ALinearizableNode extends ANode {

	/**
	 * Determines whether the node shows a preview of its data.
	 */
	protected boolean isPreviewMode = false;

	/**
	 * Determines whether the button to remove the
	 */
	protected boolean showRemoveButton = false;

	/**
	 * The {@link SelectionType} of the node.
	 */
	protected SelectionType selectionType;

	/**
	 * The current mode of the node.
	 */
	protected ALinearizeableNodeMode mode;

	/**
	 * @param pixelGLConverter
	 * @param view
	 * @param nodeId
	 */
	public ALinearizableNode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
		mode = getLinearizedMode();
		mode.apply(this);
	}

	@Override
	public void unregisterPickingListeners() {
		view.removeAllIDPickingListeners(PickingType.GENE_NODE.name(), nodeId);
		mode.unregisterPickingListeners();
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		mode.render(gl, glu);
	}

	/**
	 * @param isPreviewMode
	 *            setter, see {@link #isPreviewMode}
	 */
	public void setPreviewMode(boolean isPreviewMode) {

		if (this.isPreviewMode == isPreviewMode)
			return;
		this.isPreviewMode = isPreviewMode;
		mode.unregisterPickingListeners();

		if (isPreviewMode) {
			mode = getPreviewMode();
		} else {
			mode = getLinearizedMode();
		}
		mode.apply(this);
	}

	/**
	 * @return the isPreviewMode, see {@link #isPreviewMode}
	 */
	public boolean isPreviewMode() {
		return isPreviewMode;
	}

	/**
	 * @return A new linearized mode object for the concrete node.
	 */
	protected abstract ALinearizeableNodeMode getLinearizedMode();

	/**
	 * @return A new preview mode object for the concrete node.
	 */
	protected abstract ALinearizeableNodeMode getPreviewMode();

	/**
	 * @param selectionType
	 *            setter, see {@link #selectionType}
	 */
	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	/**
	 * @return the selectionType, see {@link #selectionType}
	 */
	public SelectionType getSelectionType() {
		return selectionType;
	}

	@Override
	public int getHeightPixels() {
		return mode.getMinHeightPixels();
	}

	@Override
	public int getWidthPixels() {
		return mode.getMinWidthPixels();
	}

	// @Override
	// public int getMinRequiredHeightPixels() {
	// return mode.getMinHeightPixels();
	// }
	//
	// @Override
	// public int getMinRequiredWidthPixels() {
	// return mode.getMinWidthPixels();
	// }

}
