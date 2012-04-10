/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Node that represents multiple nodes in a branch.
 * 
 * @author Christian
 * 
 */
public class CollapsedBranchNode extends ANode {

	public static final int TEXT_SPACING_PIXELS = 3;

	private CaleydoTextRenderer textRenderer;

//	/**
//	 * Determines whether the branch nodes are have incoming edges to the node
//	 * in the linearized path.
//	 */
//	private boolean isIncoming;

	/**
	 * Nodes that are collapsed within this one.
	 */
	private List<ANode> branchNodes = new ArrayList<ANode>();

	/**
	 * @param pixelGLConverter
	 */
	public CollapsedBranchNode(PixelGLConverter pixelGLConverter,
			CaleydoTextRenderer textRenderer) {
		super(pixelGLConverter);
		this.textRenderer = textRenderer;
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(widthPixels);
		float height = pixelGLConverter.getGLHeightForPixelHeight(heightPixels);

		Vec3f lowerLeftPosition = new Vec3f(position.x() - width / 2.0f, position.y()
				- height / 2.0f, position.z());

		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y(), lowerLeftPosition.z());
		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y(),
				lowerLeftPosition.z());
		gl.glVertex3f(lowerLeftPosition.x() + width, lowerLeftPosition.y() + height,
				position.z());
		gl.glVertex3f(lowerLeftPosition.x(), lowerLeftPosition.y() + height,
				lowerLeftPosition.z());
		gl.glEnd();

		textRenderer.setColor(0, 0, 0, 1);

		float textSpacing = pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS);
		float textWidth = textRenderer.getRequiredTextWidthWithMax(
				"" + branchNodes.size(), height - 2 * textSpacing, width - 2
						* textSpacing);

		textRenderer.renderTextInBounds(gl, "" + branchNodes.size(), position.x()
				- textWidth / 2.0f + textSpacing, lowerLeftPosition.y() + 1.5f
				* textSpacing, lowerLeftPosition.z(), width - 2 * textSpacing, height - 2
				* textSpacing);
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
	}

	/**
	 * @return the branchNodes, see {@link #branchNodes}
	 */
	public List<ANode> getBranchNodes() {
		return branchNodes;
	}

	public void addBranchNode(ANode node) {
		branchNodes.add(node);
	}
	
//	/**
//	 * @param isIncoming setter, see {@link #isIncoming}
//	 */
//	public void setIncoming(boolean isIncoming) {
//		this.isIncoming = isIncoming;
//	}
//	
//	/**
//	 * @return the isIncoming, see {@link #isIncoming}
//	 */
//	public boolean isIncoming() {
//		return isIncoming;
//	}

}
