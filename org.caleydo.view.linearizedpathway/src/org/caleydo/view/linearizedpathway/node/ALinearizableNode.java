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

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;

/**
 * Base class for all nodes that can be linearized.
 * 
 * @author Christian
 * 
 */
public abstract class ALinearizableNode extends ALayoutBasedNode {
	
	/**
	 * Determines whether the node shows a preview of its data.
	 */
	protected boolean isPreviewMode = false;

	/**
	 * @param pixelGLConverter
	 * @param view
	 * @param nodeId
	 */
	public ALinearizableNode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				
				if(isPreviewMode) {
					view.setExpandedBranchSummaryNode(null);
					view.selectBranch(ALinearizableNode.this);
				}
			}
		}, PickingType.LINEARIZABLE_NODE.name(), nodeId);
	}
	
	@Override
	public void unregisterPickingListeners() {
		view.removeAllIDPickingListeners(PickingType.GENE_NODE.name(), nodeId);
	}
	
	/**
	 * @param isPreviewMode
	 *            setter, see {@link #isPreviewMode}
	 */
	public void setPreviewMode(boolean isPreviewMode) {
		this.isPreviewMode = isPreviewMode;
	}

	/**
	 * @return the isPreviewMode, see {@link #isPreviewMode}
	 */
	public boolean isPreviewMode() {
		return isPreviewMode;
	}

}
