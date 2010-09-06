package org.caleydo.view.treemap.layout;

import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

public class GlPainter {

	GL gl;
	ViewFrustum viewFrustum;
	PickingManager pickingManager;
	int viewID;
	
	int treemapList, highlightList;
	
	SelectionManager selectionManager;
	
	public GlPainter(GL gl, ViewFrustum viewFrustum, PickingManager pickingManager, int viewID, SelectionManager selectionManager) {
		this.pickingManager=pickingManager;
		this.gl = gl;
		this.viewFrustum = viewFrustum;
		this.viewID=viewID;
		this.selectionManager=selectionManager;
		
		treemapList=gl.glGenLists(1);
		highlightList=gl.glGenLists(1);
	}

	public void paintHighlighting(Tree<ATreeMapNode> tree, SelectionManager selection){
		gl.glNewList(highlightList, GL.GL_COMPILE);
		
		for(int id:selection.getElements(SelectionType.MOUSE_OVER)){
			ATreeMapNode node= tree.getNodeByNumber(id);
			if(node!=null)
				paintRectangle(node.getMinX(), node.getMinY(), node.getMaxX(), node.getMaxY(), SelectionType.MOUSE_OVER.getColor());
		}
		
		
		for(int id:selection.getElements(SelectionType.SELECTION)){
			ATreeMapNode node= tree.getNodeByNumber(id);
			if(node!=null)
				paintRectangle(node.getMinX(), node.getMinY(), node.getMaxX(), node.getMaxY(), SelectionType.SELECTION.getColor());
		}
		
		gl.glEndList();
		
	}
	
	public void paintTreeMapFromCache(){
		gl.glCallList(treemapList);
		gl.glCallList(highlightList);
	}
	
	public void paintTreeMap(ATreeMapNode tree) {
		gl.glNewList(treemapList, GL.GL_COMPILE);
		paintHelp(tree);
		gl.glEndList();

	}
	
	private void paintHelp(ATreeMapNode root){
		List<ATreeMapNode> children = root.getChildren();
		if(children==null||children.size()==0){
			gl.glPushName(pickingManager.getPickingID(viewID, EPickingType.TREEMAP_ELEMENT_SELECTED, root.getID()));
			//System.out.println("picking ID: "+root.getID());
			fillRectangle(root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), root.getColorAttribute());
			gl.glPopName();
//			ArrayList<SelectionType> selections = selectionManager.getSelectionTypes(root.getPickingID());
//			if(selections!=null&&selections.contains(SelectionType.SELECTION))
//				paintRectangle(root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), Color.YELLOW);
		}
		else{
			for(ATreeMapNode node: children){
				paintHelp(node);
			}
		}
	}

	public void paintRectangle(float x, float y, float xmax, float ymax, float[] color) {
		gl.glLineWidth(6);
		
		gl.glBegin(GL.GL_LINE_LOOP);

		gl.glColor4f(color[0], color[1], color[2],1);

		
		
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
	
	
	public void fillRectangle(float x, float y, float xmax, float ymax, float[] color) {
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
	}


}
