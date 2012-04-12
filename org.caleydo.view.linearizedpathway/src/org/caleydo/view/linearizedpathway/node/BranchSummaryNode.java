/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
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
public class BranchSummaryNode extends ALayoutBasedNode {

	public static final int TEXT_SPACING_PIXELS = 3;

	private CaleydoTextRenderer textRenderer;

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
	public BranchSummaryNode(PixelGLConverter pixelGLConverter,
			CaleydoTextRenderer textRenderer, GLLinearizedPathway view, int nodeId,
			ANode associatedLinearizedNode) {
		super(pixelGLConverter, view, nodeId);
		this.textRenderer = textRenderer;
		this.associatedLinearizedNode = associatedLinearizedNode;
	}

//	@Override
//	public void render(GL2 gl, GLU glu) {
//		float width = pixelGLConverter.getGLWidthForPixelWidth(widthPixels);
//		float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);
//
//		Vec3f lowerLeftPosition = new Vec3f(position.x() - width / 2.0f, position.y()
//				- height / 2.0f, position.z());
//
//		gl.glPushName(pickingManager.getPickingID(view.getID(),
//				PickingType.BRANCH_SUMMARY_NODE.name(), nodeId));
//
//		gl.glColor3f(0, 0, 0);
//		gl.glBegin(GL2.GL_LINE_LOOP);
//		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y(), lowerLeftPosition.z());
//		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y(),
//				lowerLeftPosition.z());
//		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y() + height,
//				position.z());
//		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y() + height,
//				lowerLeftPosition.z());
//		gl.glEnd();
//
//		gl.glColor4f(1, 1, 1, 0);
//		gl.glBegin(GL2.GL_QUADS);
//		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y(), lowerLeftPosition.z());
//		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y(),
//				lowerLeftPosition.z());
//		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y() + height,
//				position.z());
//		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y() + height,
//				lowerLeftPosition.z());
//		gl.glEnd();
//
//		float buttonSpacing = pixelGLConverter.getGLHeightForPixelHeight(2);
//
//		// Vec3f lowerLeftCorner = new Vec3f(0, 0, zCoordinate);
//		// Vec3f lowerRightCorner = new Vec3f(x, 0, zCoordinate);
//		// Vec3f upperRightCorner = new Vec3f(x, y, zCoordinate);
//		// Vec3f upperLeftCorner = new Vec3f(0, y, zCoordinate);
//		//
//		// switch (textureRotation) {
//		// case TEXTURE_ROTATION_0:
//		// textureManager.renderTexture(gl, button.getIconTexture(),
//		// lowerLeftCorner, lowerRightCorner,
//		// upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
//
//		textRenderer.setColor(0, 0, 0, 1);
//
//		float textSpacing = pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS);
//		float textWidth = textRenderer.getRequiredTextWidthWithMax(
//				"..." + branchNodes.size(), height - 2 * textSpacing, width - 2
//						* textSpacing);
//
//		textRenderer.renderTextInBounds(gl, branchNodes.size() + "...", position.x()
//				- textWidth / 2.0f + textSpacing, lowerLeftPosition.y() + 1.5f
//				* textSpacing, lowerLeftPosition.z(), width - 2 * textSpacing, height - 2
//				* textSpacing);
//		gl.glPopName();
//	}

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
				
				if(isCollapsed) {
					isCollapsed = false;
					view.setExpandedBranchSummaryNode(BranchSummaryNode.this);
					colorRenderer.setBorderColor(new float[] {1,1,1,0});
					colorRenderer.setColor(new float[] {1,1,1,0});
					collapseButton.setIconTexture(EIconTextures.GROUPER_COLLAPSE_MINUS);
					labelRenderer.setLabel("");
				} else {
					isCollapsed = true;
					view.setExpandedBranchSummaryNode(null);
					colorRenderer.setBorderColor(new float[] {0,0,0,1});
					colorRenderer.setColor(new float[] {1,1,1,1});
					collapseButton.setIconTexture(EIconTextures.GROUPER_COLLAPSE_PLUS);
					labelRenderer.setLabel("..." + branchNodes.size());
				}
				view.setDisplayListDirty();
			}
		}, PickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), nodeId);

	}

	@Override
	public void unregisterPickingListeners() {
		view.removeAllIDPickingListeners(PickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(), nodeId);
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

	@Override
	protected ElementLayout setupLayout() {
		
		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");
		colorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 });
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout collapseButtonLayout = new ElementLayout("collapseButton");
		collapseButton = new Button(PickingType.BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON.name(),
				nodeId, EIconTextures.GROUPER_COLLAPSE_PLUS);
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
		
		baseColumn.append(verticalSpacing);
		baseColumn.append(baseRow);
		baseColumn.append(verticalSpacing);
		
		return baseColumn;
	}

	@Override
	public int getMinRequiredWidthPixels() {
		return DEFAULT_WIDTH_PIXELS;
	}

}
