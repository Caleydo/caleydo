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
package org.caleydo.view.enroute.node;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.enroute.node.mode.ComplexNodeLinearizedMode;

/**
 * The complex node renderer renders a node that represents multiple
 * {@link PathwayVertexRep} objects.
 * 
 * @author Christian
 * 
 */
public class ComplexNode extends ALinearizableNode {

	public static final int TEXT_SPACING_PIXELS = 3;

	/**
	 * List of {@link PathwayVertexRep} objects that are combined in this
	 * complex node.
	 */
	// private List<PathwayVertexRep> vertexReps = new
	// ArrayList<PathwayVertexRep>();

	/**
	 * List of {@link ANode} objects that belong to this complex node.
	 */
	private List<ALinearizableNode> nodes = new ArrayList<ALinearizableNode>();

	/**
	 * @param pixelGLConverter
	 */
	public ComplexNode(PixelGLConverter pixelGLConverter,
			CaleydoTextRenderer textRenderer, GLEnRoutePathway view, int nodeId) {
		super(pixelGLConverter, view, nodeId);
	}

	// @Override
	// public void render(GL2 gl, GLU glu) {
	// mode.render(gl, glu)
	//
	// // float width = pixelGLConverter.getGLWidthForPixelWidth(widthPixels);
	// // float height =
	// // pixelGLConverter.getGLHeightForPixelHeight(heightPixels);
	// //
	// // Vec3f lowerLeftPosition = new Vec3f(position.x() - width / 2.0f,
	// // position.y()
	// // - height / 2.0f, position.z());
	// //
	// // gl.glPushName(pickingManager.getPickingID(view.getID(),
	// // PickingType.LINEARIZABLE_NODE.name(), nodeId));
	// //
	// // gl.glColor3f(0, 0, 0);
	// // gl.glBegin(GL2.GL_LINE_LOOP);
	// // gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y(),
	// // lowerLeftPosition.z());
	// // gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y(),
	// // lowerLeftPosition.z());
	// // gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y() +
	// // height,
	// // position.z());
	// // gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y() + height,
	// // lowerLeftPosition.z());
	// // gl.glEnd();
	// //
	// // textRenderer.setColor(0, 0, 0, 1);
	// //
	// // float textSpacing =
	// // pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS);
	// // float textWidth = textRenderer.getRequiredTextWidthWithMax(caption,
	// // height - 2
	// // * textSpacing, width - 2 * textSpacing);
	// //
	// // textRenderer.renderTextInBounds(gl, caption + " " +
	// // numAssociatedRows,
	// // position.x() - textWidth / 2.0f + textSpacing, lowerLeftPosition.y()
	// // + 1.5f * textSpacing, lowerLeftPosition.z(), width - 2
	// // * textSpacing, height - 2 * textSpacing);
	// //
	// // gl.glPopName();
	//
	// }

	// /**
	// * Adds a {@link PathwayVertexRep} object to this node renderer.
	// *
	// * @param vertexRep
	// */
	// public void addVertexRep(PathwayVertexRep vertexRep) {
	// vertexReps.add(vertexRep);
	// }
	//
	// /**
	// * @param vertexReps
	// * setter, see {@link #vertexReps}
	// */
	// public void setVertexReps(List<PathwayVertexRep> vertexReps) {
	// this.vertexReps = vertexReps;
	// }
	//
	// /**
	// * @return the vertexReps, see {@link #vertexReps}
	// */
	// public List<PathwayVertexRep> getVertexReps() {
	// return vertexReps;
	// }

	// @Override
	// public PathwayVertexRep getPathwayVertexRep() {
	// if (vertexReps.size() > 0)
	// return vertexReps.get(0);
	// return null;
	// }

	// /**
	// * @param caption
	// * setter, see {@link #caption}
	// */
	// public void setCaption(String caption) {
	// this.caption = caption;
	// }
	//
	// @Override
	// public String getCaption() {
	// return caption;
	// }

	@Override
	protected ALinearizeableNodeMode getLinearizedMode() {
		return new ComplexNodeLinearizedMode(view);
	}

	@Override
	protected ALinearizeableNodeMode getPreviewMode() {
		// FIXME: just temporary
		return new ComplexNodeLinearizedMode(view);
	}

	@Override
	public String getCaption() {
		return "Complex";
	}

	/**
	 * @param nodes
	 *            setter, see {@link #nodes}
	 */
	public void setNodes(List<ALinearizableNode> nodes) {
		this.nodes = nodes;
		mode.apply(this);
	}

	/**
	 * @return the nodes, see {@link #nodes}
	 */
	public List<ALinearizableNode> getNodes() {
		return nodes;
	}

	@Override
	public void setPreviewMode(boolean isPreviewMode) {
		super.setPreviewMode(isPreviewMode);

		for (ALinearizableNode node : nodes) {
			node.setPreviewMode(isPreviewMode);
		}
	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		for (ALinearizableNode node : nodes) {
			node.unregisterPickingListeners();
		}
	}

}
