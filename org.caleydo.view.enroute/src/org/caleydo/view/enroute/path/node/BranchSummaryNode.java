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

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
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
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;

/**
 * Node that represents multiple nodes in a branch.
 *
 * @author Christian
 *
 */
public class BranchSummaryNode extends ANode {

	// protected static final int MIN_NODE_WIDTH_PIXELS = 90;
	protected static final int SPACING_PIXELS = 2;
	// protected static final int COLLAPSE_BUTTON_SIZE_PIXELS = 12;
	// protected static final int LABEL_HEIGHT_PIXELS = 14;
	protected static final int NUM_NODES_LABEL_WIDTH_PIXELS = 16;
	// protected static final int BRANCH_NODE_SPACING = 20;
	// protected static final int BRANCH_AREA_SPACING = 8;

	protected LayoutManager layoutManager;

	/**
	 * Determines whether the node is collapsed and shall be rendered itself, or if it is expanded to display the branch
	 * nodes.
	 */
	private boolean isCollapsed = true;

	/**
	 * Nodes that are collapsed within this one.
	 */
	private List<ALinearizableNode> branchNodes = new ArrayList<ALinearizableNode>();

	/**
	 * The linearized node this branch refers to.
	 */
	private ALinearizableNode associatedLinearizedNode;

	private ColorRenderer colorRenderer;

	private BranchNodeLabelRenderer labelRenderer;

	private Button collapseButton;

	private final APathwayPathRenderer pathwayPathRenderer;

	/**
	 * @param pixelGLConverter
	 */
	public BranchSummaryNode(AGLView view, ALinearizableNode associatedLinearizedNode,
			APathwayPathRenderer pathwayPathRenderer) {
		super(view);
		layoutManager = new LayoutManager(new ViewFrustum(), pixelGLConverter);
		this.associatedLinearizedNode = associatedLinearizedNode;
		this.pathwayPathRenderer = pathwayPathRenderer;
		setupLayout();
	}

	/**
	 * @param branchNodes
	 *            setter, see {@link #branchNodes}
	 */
	public void setBranchNodes(List<ALinearizableNode> branchNodes) {
		this.branchNodes = branchNodes;
	}

	/**
	 * @return the branchNodes, see {@link #branchNodes}
	 */
	public List<ALinearizableNode> getBranchNodes() {
		return branchNodes;
	}

	public void addBranchNode(ALinearizableNode node) {
		branchNodes.add(node);
	}

	/**
	 * @param isCollapsed
	 *            setter, see {@link #isCollapsed}
	 */
	public void setCollapsed(boolean isCollapsed) {
		this.isCollapsed = isCollapsed;
		if (isCollapsed) {
			collapseButton.setIconTexture(EIconTextures.GROUPER_COLLAPSE_PLUS);
		} else {
			collapseButton.setIconTexture(EIconTextures.GROUPER_COLLAPSE_MINUS);
		}
	}

	/**
	 * @return the isCollapsed, see {@link #isCollapsed}
	 */
	public boolean isCollapsed() {
		return isCollapsed;
	}

	@Override
	public void init() {
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {

				if (isCollapsed) {
					setCollapsed(false);
					BranchSummaryNode node = pathwayPathRenderer.getExpandedBranchSummaryNode();
					// colorRenderer.setBorderColor(new float[] { 1, 1, 1, 1 });
					if (node != null)
						node.setCollapsed(true);
					pathwayPathRenderer.setExpandedBranchSummaryNode(BranchSummaryNode.this);
				} else {
					setCollapsed(true);
					// colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
					pathwayPathRenderer.setExpandedBranchSummaryNode(null);
				}
				// view.setLayoutDirty();
			}
		}, EPickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), hashCode());

	}

	@Override
	public void destroy() {
		view.removeAllIDPickingListeners(EPickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), hashCode());
	}

	/**
	 * @param associatedLinearizedNode
	 *            setter, see {@link #associatedLinearizedNode}
	 */
	public void setAssociatedLinearizedNode(ALinearizableNode associatedLinearizedNode) {
		this.associatedLinearizedNode = associatedLinearizedNode;
	}

	/**
	 * @return the associatedLinearizedNode, see {@link #associatedLinearizedNode}
	 */
	public ALinearizableNode getAssociatedLinearizedNode() {
		return associatedLinearizedNode;
	}

	protected void setupLayout() {

		Column baseColumn = new Column("baseColumn");
		// baseColumn.setDebug(true);
		// baseColumn.setFrameColor(1, 0, 0, 1);
		colorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 });
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer.setColor(new float[] { 0.9f, 0.9f, 0.9f, 1 });
		colorRenderer.setDrawBorder(true);
		colorRenderer.setView(view);
		colorRenderer.addPickingID(EPickingType.BRANCH_SUMMARY_NODE.name(), hashCode());
		baseColumn.addBackgroundRenderer(colorRenderer);
		baseColumn.setBottomUp(false);

		Row baseRow = new Row("baseRow");
		// baseRow.setDebug(true);
		// baseRow.setFrameColor(0, 1, 0, 1);
		int textHeight = pathwayPathRenderer.getSizeConfig().getBranchSummaryNodeTextHeight();

		baseRow.setPixelSizeY(textHeight);

		ElementLayout collapseButtonLayout = new ElementLayout("collapseButton");
		collapseButton = new Button(EPickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), hashCode(),
				EIconTextures.GROUPER_COLLAPSE_PLUS);
		ButtonRenderer collapseButtonRenderer = new ButtonRenderer.Builder(view, collapseButton).build();
		collapseButtonLayout.setRenderer(collapseButtonRenderer);
		collapseButtonLayout.setPixelSizeX(textHeight - SPACING_PIXELS);
		collapseButtonLayout.setPixelSizeY(textHeight - SPACING_PIXELS);

		ElementLayout captionLayout = new ElementLayout("label");
		labelRenderer = new BranchNodeLabelRenderer(this, view);

		captionLayout.setRenderer(labelRenderer);
		captionLayout.setPixelSizeY(textHeight);

		ElementLayout numNodesLabelLayout = new ElementLayout("numNodeslabel");
		// numNodesLabelLayout.setDebug(true);
		// numNodesLabelLayout.setFrameColor(1, 0, 0, 1);
		LabelRenderer numNodesLabelRenderer = new LabelRenderer(view, this);
		// numNodesLabelRenderer.setAlignment(LabelRenderer.ALIGN_RIGHT);

		numNodesLabelLayout.setRenderer(numNodesLabelRenderer);
		numNodesLabelLayout.setPixelSizeY(textHeight);
		numNodesLabelLayout.setPixelSizeX(textHeight);

		ElementLayout horizontalSpacing = new ElementLayout();
		horizontalSpacing.setPixelSizeX(SPACING_PIXELS);

		baseRow.append(horizontalSpacing);
		baseRow.append(collapseButtonLayout);
		baseRow.append(horizontalSpacing);
		baseRow.append(captionLayout);
		baseRow.append(horizontalSpacing);
		baseRow.append(numNodesLabelLayout);
		baseRow.append(horizontalSpacing);

		ElementLayout verticalSpacing = new ElementLayout();

		verticalSpacing.setPixelSizeY(SPACING_PIXELS);
		// ElementLayout x = new ElementLayout();
		// x.setRatioSizeY(1);
		baseColumn.append(verticalSpacing);
		baseColumn.append(baseRow);
		baseColumn.append(verticalSpacing);
		// baseColumn.append(x);

		layoutManager.setBaseElementLayout(baseColumn);
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		gl.glPushMatrix();
		gl.glTranslatef(position.x() - width / 2.0f, position.y() - height / 2.0f, position.z()
				- (isCollapsed ? 0 : 0.001f));
		layoutManager.setViewFrustum(new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, width, 0, height, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();

		if (!isCollapsed) {

			float titleAreaHeight = pixelGLConverter.getGLHeightForPixelHeight(getTitleAreaHeightPixels());
			float branchAreaSpacing = pixelGLConverter.getGLHeightForPixelHeight(pathwayPathRenderer.getSizeConfig()
					.getBranchSummaryNodeBranchAreaSpacing());
			float branchNodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(pathwayPathRenderer.getSizeConfig()
					.getBranchSummaryNodeBranchNodeSpacing());
			float currentPositionY = position.y() + height / 2.0f - titleAreaHeight - branchAreaSpacing;

			for (ANode node : branchNodes) {
				float nodeHeight = node.getHeight();
				currentPositionY -= nodeHeight / 2.0f;
				node.setPosition(new Vec3f(position.x(), currentPositionY, position.z()));
				node.render(gl, glu);
				currentPositionY -= (nodeHeight / 2.0f + branchNodeSpacing);
			}
		}

	}


	@Override
	public void renderHighlight(GL2 gl, GLU glu) {
		if (!isCollapsed) {
			for (ANode node : branchNodes) {
				node.renderHighlight(gl, glu);
			}
		}

	}

	/**
	 * @return Height of the title area of the branch node in pixels.
	 */
	public int getTitleAreaHeightPixels() {
		return 2 * SPACING_PIXELS + pathwayPathRenderer.getSizeConfig().getBranchSummaryNodeTextHeight();
	}

	/**
	 * @return Height of the area where the branch nodes are accommodated in pixels.
	 */
	public int getBranchAreaHeightPixels() {

		int minHeight = 0;
		for (ANode node : branchNodes) {
			minHeight += node.getHeightPixels();
		}

		minHeight += (branchNodes.size() - 1)
				* pathwayPathRenderer.getSizeConfig().getBranchSummaryNodeBranchNodeSpacing();
		minHeight += 2 * pathwayPathRenderer.getSizeConfig().getBranchSummaryNodeBranchAreaSpacing();

		return minHeight;
	}

	@Override
	public String getLabel() {
		return " " + branchNodes.size();
	}

	@Override
	public int getHeightPixels() {
		return getTitleAreaHeightPixels() + (isCollapsed ? 0 : getBranchAreaHeightPixels());
	}

	@Override
	public int getWidthPixels() {
		return isCollapsed ? pathwayPathRenderer.getSizeConfig().getBranchSummaryNodeWidthCollapsed()
				: pathwayPathRenderer.getSizeConfig().getBranchSummaryNodeWidthExpanded();
	}

	@Override
	public String getProviderName() {
		return "Branch Summary Node";
	}

}
