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

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;

/**
 * Node that represents multiple nodes in a branch.
 * 
 * @author Christian
 * 
 */
public class BranchSummaryNode extends ANode {

	public static final int MIN_NODE_WIDTH_PIXELS = 176;
	public static final int TEXT_SPACING_PIXELS = 3;

	protected LayoutManager layoutManager;

	/**
	 * Determines whether the node is collapsed and shall be rendered itself, or
	 * if it is expanded to display the branch nodes.
	 */
	private boolean isCollapsed = true;

	/**
	 * Nodes that are collapsed within this one.
	 */
	private List<ANode> branchNodes = new ArrayList<ANode>();

	/**
	 * The linearized node this branch refers to.
	 */
	private ANode associatedLinearizedNode;

	private ColorRenderer colorRenderer;

	private LabelRenderer labelRenderer;

	private Button collapseButton;

	/**
	 * @param pixelGLConverter
	 */
	public BranchSummaryNode(GLLinearizedPathway view, int nodeId,
			ANode associatedLinearizedNode) {
		super(view.getPixelGLConverter(), view, nodeId);
		layoutManager = new LayoutManager(new ViewFrustum(), pixelGLConverter);
		this.associatedLinearizedNode = associatedLinearizedNode;
		setupLayout();
	}

	@Override
	public PathwayVertexRep getPathwayVertexRep() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param branchNodes
	 *            setter, see {@link #branchNodes}
	 */
	public void setBranchNodes(List<ANode> branchNodes) {
		this.branchNodes = branchNodes;
		labelRenderer.setLabel("..." + branchNodes.size());
	}

	/**
	 * @return the branchNodes, see {@link #branchNodes}
	 */
	public List<ANode> getBranchNodes() {
		return branchNodes;
	}

	public void addBranchNode(ANode node) {
		branchNodes.add(node);
		labelRenderer.setLabel("..." + branchNodes.size());
	}

	/**
	 * @param isCollapsed
	 *            setter, see {@link #isCollapsed}
	 */
	public void setCollapsed(boolean isCollapsed) {
		this.isCollapsed = isCollapsed;
	}

	/**
	 * @return the isCollapsed, see {@link #isCollapsed}
	 */
	public boolean isCollapsed() {
		return isCollapsed;
	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {

				if (isCollapsed) {
					isCollapsed = false;
					view.setExpandedBranchSummaryNode(BranchSummaryNode.this);
//					colorRenderer.setBorderColor(new float[] { 1, 1, 1, 0 });
//					colorRenderer.setColor(new float[] { 1, 1, 1, 0 });
					collapseButton.setIconTexture(EIconTextures.GROUPER_COLLAPSE_MINUS);
					labelRenderer.setLabel("");
				} else {
					isCollapsed = true;
					view.setExpandedBranchSummaryNode(null);
//					colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
//					colorRenderer.setColor(new float[] { 1, 1, 1, 1 });
					collapseButton.setIconTexture(EIconTextures.GROUPER_COLLAPSE_PLUS);
					labelRenderer.setLabel("..." + branchNodes.size());
				}
				view.setDisplayListDirty();
			}
		}, PickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), nodeId);

	}

	@Override
	public void unregisterPickingListeners() {
		view.removeAllIDPickingListeners(
				PickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), nodeId);
	}

	/**
	 * @param associatedLinearizedNode
	 *            setter, see {@link #associatedLinearizedNode}
	 */
	public void setAssociatedLinearizedNode(ANode associatedLinearizedNode) {
		this.associatedLinearizedNode = associatedLinearizedNode;
	}

	/**
	 * @return the associatedLinearizedNode, see
	 *         {@link #associatedLinearizedNode}
	 */
	public ANode getAssociatedLinearizedNode() {
		return associatedLinearizedNode;
	}

	@Override
	public int getMinRequiredHeightPixels() {
		return DEFAULT_HEIGHT_PIXELS;
	}

	protected void setupLayout() {

		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");
		colorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 });
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		baseColumn.addBackgroundRenderer(colorRenderer);
		baseColumn.setBottomUp(false);

		ElementLayout collapseButtonLayout = new ElementLayout("collapseButton");
		collapseButton = new Button(
				PickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), nodeId,
				EIconTextures.GROUPER_COLLAPSE_PLUS);
		ButtonRenderer collapseButtonRenderer = new ButtonRenderer(collapseButton, view,
				view.getTextureManager());
		collapseButtonLayout.setRenderer(collapseButtonRenderer);
		collapseButtonLayout.setPixelSizeX(12);
		collapseButtonLayout.setPixelSizeY(12);

		ElementLayout labelLayout = new ElementLayout("label");
		labelRenderer = new LabelRenderer(view, "...0");

		labelLayout.setRenderer(labelRenderer);
		labelLayout.setPixelSizeY(16);

		ElementLayout horizontalSpacing = new ElementLayout();
		horizontalSpacing.setPixelSizeX(2);

		baseRow.append(horizontalSpacing);
		baseRow.append(collapseButtonLayout);
		baseRow.append(horizontalSpacing);
		baseRow.append(labelLayout);
		baseRow.append(horizontalSpacing);

		ElementLayout verticalSpacing = new ElementLayout();
		
		verticalSpacing.setPixelSizeY(2);
//		ElementLayout x = new ElementLayout();
//		x.setRatioSizeY(1);
		baseColumn.append(verticalSpacing);
		baseColumn.append(baseRow);
		baseColumn.append(verticalSpacing);
//		baseColumn.append(x);

		layoutManager.setBaseElementLayout(baseColumn);
	}

	@Override
	public int getMinRequiredWidthPixels() {
		return MIN_NODE_WIDTH_PIXELS;
	}

	@Override
	public String getCaption() {
		return "..." + branchNodes.size();
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		gl.glPushMatrix();
		gl.glTranslatef(position.x() - width / 2.0f, position.y() - height / 2.0f, 0f);
		layoutManager.setViewFrustum(new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC,
				0, width, 0, height, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();

	}

}
