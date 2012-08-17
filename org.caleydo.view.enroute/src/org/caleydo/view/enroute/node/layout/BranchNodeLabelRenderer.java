/**
 * 
 */
package org.caleydo.view.enroute.node.layout;

import java.util.List;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.ALinearizableNode;
import org.caleydo.view.enroute.node.ANode;
import org.caleydo.view.enroute.node.BranchSummaryNode;

/**
 * Renderer for the label of a {@link BranchSummaryNode} that contains several
 * captions.
 * 
 * @author Christian
 * 
 */
public class BranchNodeLabelRenderer extends LayoutRenderer {

	private static final String DOTS = " ...";
	private static final String SEPARATOR = ", ";

	/**
	 * The node for which the label is rendererd
	 */
	private BranchSummaryNode node;

	private GLEnRoutePathway view;

	private CaleydoTextRenderer textRenderer;

	public BranchNodeLabelRenderer(BranchSummaryNode node, GLEnRoutePathway view) {
		this.node = node;
		this.view = view;
		this.textRenderer = view.getTextRenderer();
	}

	@Override
	public void renderContent(GL2 gl) {
		List<ALinearizableNode> branchNodes = node.getBranchNodes();

		float spacing = view.getPixelGLConverter().getGLHeightForPixelHeight(1);
		float textHeight = y - 2 * spacing;
		float dotWidth = textRenderer.getRequiredTextWidth(DOTS, textHeight);
		float separatorWidth = textRenderer.getRequiredTextWidth(SEPARATOR, textHeight);

		StringBuffer buffer = new StringBuffer();
		float currentLabelWidth = 0;

		for (int i = 0; i < branchNodes.size(); i++) {
			ANode branchNode = branchNodes.get(i);
			String branchNodeCaption = branchNode.getCaption();
			float captionWidth = textRenderer.getRequiredTextWidth(branchNodeCaption,
					textHeight);

			float precalculatedTotalWidth = currentLabelWidth + captionWidth
					+ ((i != 0) ? separatorWidth : 0)
					+ ((i != branchNodes.size() - 1) ? dotWidth : 0);

			if (precalculatedTotalWidth < x - 7 * spacing) {
				if (i != 0)
					buffer.append(SEPARATOR);
				buffer.append(branchNodeCaption);

				currentLabelWidth = textRenderer.getRequiredTextWidth(buffer.toString(),
						textHeight);
			} else {
				buffer.append(DOTS);
				break;
			}
		}

		textRenderer.setColor(0, 0, 0, 1);

		textRenderer.renderTextInBounds(gl, buffer.toString(), 0, spacing, 0.1f, x,
				textHeight);

	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
