package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class TreeMapRenderer {

	ViewFrustum viewFrustum;
	PickingManager pickingManager;
	int viewID;

	int treemapList, highlightList;

	SelectionManager selectionManager;

	CaleydoTextRenderer textRenderer;

	private boolean bDrawNodeFrame = false;
	private Color frameColor = Color.WHITE;

	private boolean bDrawLabel = true;

	public void initRenderer(ViewFrustum viewFrustum, PickingManager pickingManager, int viewID, SelectionManager selectionManager,
			CaleydoTextRenderer textRenderer) {
		this.pickingManager = pickingManager;

		this.viewFrustum = viewFrustum;
		this.viewID = viewID;
		this.selectionManager = selectionManager;
		this.textRenderer = textRenderer;

		
	}
	
	public void initCache(GL gl){
		treemapList = gl.glGenLists(1);
		highlightList = gl.glGenLists(1);
	}

	public void setNodeFrame(boolean flag, Color color) {
		bDrawNodeFrame = flag;
		frameColor = color;
	}

	public void setDrawLabel(boolean flag) {
		bDrawLabel = flag;
	}

	public void paintHighlighting(GL gl, Tree<ATreeMapNode> tree, SelectionManager selection) {
		gl.glNewList(highlightList, GL.GL_COMPILE);

		for (int id : selection.getElements(SelectionType.MOUSE_OVER)) {
			ATreeMapNode node = tree.getNodeByNumber(id);
			if (node != null)
				paintRectangle(gl, node.getMinX(), node.getMinY(), node.getMaxX(), node.getMaxY(), SelectionType.MOUSE_OVER.getColor());
		}

		for (int id : selection.getElements(SelectionType.SELECTION)) {
			ATreeMapNode node = tree.getNodeByNumber(id);
			if (node != null)
				paintRectangle(gl, node.getMinX(), node.getMinY(), node.getMaxX(), node.getMaxY(), SelectionType.SELECTION.getColor());
		}

		gl.glEndList();
	}

	public void renderTreeMapFromCache(GL gl) {
		gl.glCallList(treemapList);
		gl.glCallList(highlightList);
	}

	public void renderTreeMap(GL gl, ATreeMapNode tree) {
		gl.glNewList(treemapList, GL.GL_COMPILE);
		renderHelp(gl, tree);
		gl.glEndList();

	}

	private void renderHelp(GL gl, ATreeMapNode root) {
		List<ATreeMapNode> children = root.getChildren();
		if (children == null || children.size() == 0) {
			gl.glPushName(pickingManager.getPickingID(viewID, EPickingType.TREEMAP_ELEMENT_SELECTED, root.getID()));

			fillRectangle(gl, root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), root.getColorAttribute());
			gl.glPopName();

			if (bDrawLabel && bDrawNodeFrame)
				displayText(gl, root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), root.getLabel());

		} else {
			for (ATreeMapNode node : children) {
				renderHelp(gl, node);
			}
		}
	}

	private void paintRectangle(GL gl, float x, float y, float xmax, float ymax, float[] color) {
		gl.glLineWidth(6);

		gl.glBegin(GL.GL_LINE_LOOP);

		gl.glColor4f(color[0], color[1], color[2], 1);

		x = viewFrustum.getWidth() * x;
		y = viewFrustum.getHeight() * y;
		xmax = viewFrustum.getWidth() * xmax;
		ymax = viewFrustum.getHeight() * ymax;

		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, ymax, 0);
		gl.glVertex3f(xmax, ymax, 0);
		gl.glVertex3f(xmax, y, 0);

		gl.glEnd();
	}

	private void fillRectangle(GL gl, float x, float y, float xmax, float ymax, float[] color) {
		gl.glBegin(GL.GL_QUADS);

		gl.glColor3f(color[0], color[1], color[2]);

		x = viewFrustum.getWidth() * x;
		y = viewFrustum.getHeight() * y;
		xmax = viewFrustum.getWidth() * xmax;
		ymax = viewFrustum.getHeight() * ymax;

		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, ymax, 0);
		gl.glVertex3f(xmax, ymax, 0);
		gl.glVertex3f(xmax, y, 0);

		gl.glEnd();

		if (bDrawNodeFrame) {
			gl.glColor3f(frameColor.getColorComponents(null)[0], frameColor.getColorComponents(null)[1], frameColor.getColorComponents(null)[2]);
			gl.glLineWidth(2);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(x, y, 0);
			gl.glVertex3f(x, ymax, 0);
			gl.glVertex3f(xmax, ymax, 0);
			gl.glVertex3f(xmax, y, 0);
			gl.glEnd();
		}
	}

	
	private void displayText(GL gl, float x, float y, float xmax, float ymax, String text) {
		float minScaling=1.0f;
		float maxScaling=3.0f;
		float border=0.03f;
		
		float scaling=1.0f;
		
		
		 Rectangle2D bbox= textRenderer.getScaledBounds(gl, text, GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR*scaling, 20);
		 
		 float width=(float) (bbox.getWidth() / viewFrustum.getWidth()) +border;
		 float height= (float) (bbox.getHeight()/viewFrustum.getHeight())+ border;
		 
		 scaling=Math.min((xmax-x)/width, (ymax-y)/height);
		
		 if(scaling<minScaling)
			 return;
		 scaling=Math.min(scaling,maxScaling);
	
		 textRenderer.renderText(gl, text, x * viewFrustum.getWidth() + 0.03f, y * viewFrustum.getHeight() + 0.03f, 0,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR * scaling, 20);
	}

}
