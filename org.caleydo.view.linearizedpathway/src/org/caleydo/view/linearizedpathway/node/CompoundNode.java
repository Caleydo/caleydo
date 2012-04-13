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

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.linearizedpathway.node.mode.CompoundNodeLinearizedMode;
import org.caleydo.view.linearizedpathway.node.mode.CompoundNodePreviewMode;

/**
 * Node that represents a compound in a pathway.
 * 
 * @author Christian
 * 
 */
public class CompoundNode extends ALinearizableNode {

	/**
	 * The vertex in the graph this compound belongs to.
	 */
	protected PathwayVertexRep pathwayVertexRep;

	/**
	 * @param pixelGLConverter
	 */
	public CompoundNode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		// This node does not use layouts.
		float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);

		gl.glPushName(pickingManager.getPickingID(view.getID(),
				PickingType.LINEARIZABLE_NODE.name(), nodeId));
		gl.glPushMatrix();
		gl.glTranslatef(position.x(), position.y(), position.z());
		gl.glColor4f(1, 1, 1, 0);
		GLPrimitives.renderCircle(glu, height / 2.0f, 16);
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderCircleBorder(gl, glu, height / 2.0f, 16, 0.1f);
		gl.glPopMatrix();
		gl.glPopName();

	}

	/**
	 * @param pathwayVertexRep
	 *            setter, see {@link #pathwayVertexRep}
	 */
	public void setPathwayVertexRep(PathwayVertexRep pathwayVertexRep) {
		this.pathwayVertexRep = pathwayVertexRep;
	}

	@Override
	public PathwayVertexRep getPathwayVertexRep() {
		return pathwayVertexRep;
	}

	@Override
	protected ALinearizeableNodeMode getLinearizedMode() {
		return new CompoundNodeLinearizedMode(view);
	}

	@Override
	protected ALinearizeableNodeMode getPreviewMode() {
		return new CompoundNodePreviewMode(view);
	}

	@Override
	public String getCaption() {
		return pathwayVertexRep.getName();
	}

	@Override
	protected void registerPickingListeners() {

	}

}
