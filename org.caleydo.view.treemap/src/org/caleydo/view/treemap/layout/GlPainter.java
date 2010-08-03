package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;

public class GlPainter {

	GL gl;
	IViewFrustum viewFrustum;
	PickingManager pickingManager;
	int viewID;

	int treemapList, highlightList;
	
	public GlPainter(GL gl, IViewFrustum viewFrustum, PickingManager pickingManager, int viewID) {
		this.pickingManager=pickingManager;
		this.gl = gl;
		this.viewFrustum = viewFrustum;
		this.viewID=viewID;
		
		treemapList=gl.glGenLists(1);
		highlightList=gl.glGenLists(1);
	}

	public void paintHighlighting(){
		
	}
	
	public void paintTreeMapFromCache(){
		gl.glCallList(treemapList);
	}
	
	public void paintTreeMap(AbstractTree tree) {
		gl.glNewList(treemapList, GL.GL_COMPILE);
		paintHelp(tree.getRoot());
		gl.glEndList();

	}
	
	private void paintHelp(ATreeMapNode root){
		List<ATreeMapNode> children = root.getChildren();
		if(children==null||children.size()==0){
			gl.glPushName(pickingManager.getPickingID(viewID, EPickingType.TREEMAP_ELEMENT_SELECTED, 1));
			fillRectangle(root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), root.getColorAttribute());
			
		}
		else{
			for(ATreeMapNode node: children){
				paintHelp(node);
			}
		}
	}

	public void paintRectangle(float x, float y, float xmax, float ymax, Color c) {
		gl.glLineWidth(6);
		
		gl.glBegin(GL.GL_LINE_LOOP);

		float color[] = c.getRGBColorComponents(null);
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
	
	
	public void fillRectangle(float x, float y, float xmax, float ymax, Color c) {
		gl.glBegin(GL.GL_QUADS);

		float color[] = c.getRGBColorComponents(null);
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
