package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;

public class GlPainter implements IGlPainter {

	GL gl;
	IViewFrustum viewFrustum;

	public GlPainter(GL gl, IViewFrustum viewFrustum) {
		this.gl = gl;
		this.viewFrustum = viewFrustum;
	}

	public void paint(AbstractTree tree) {
		paintHelp(tree.getRoot());

	}
	
	private void paintHelp(AbstractTreeNode root){
		List<AbstractTreeNode> children = root.getChildren();
		if(children==null||children.size()==0)
			fillRectangle(root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), root.getColorAttribute());
		else{
			for(AbstractTreeNode node: children){
				paintHelp(node);
			}
		}
	}

	public void paintRectangle(float x, float y, float xmax, float ymax, Color c) {
		gl.glLineWidth(6);
		
		gl.glBegin(GL.GL_LINE_LOOP);

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

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("Start painting");

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		System.out.println("Finished painting");

	}
}
