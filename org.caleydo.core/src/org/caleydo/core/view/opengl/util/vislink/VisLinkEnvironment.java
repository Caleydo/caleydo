package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;

/**
 * This class provides higher level features to VisLinks as halo and animation.
 * To provide this features, more context is needed than a single line. This
 * class needs to know all connection lines that should be displayed on the screen.
 *  
 * @author oliver
 *
 */

public class VisLinkEnvironment {
	
//	private int activeViewID;
	
	ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews;
	
	
	/**
	 * Constructor
	 * @param connectionLines the connection lines of the whole screen
	 */
	public VisLinkEnvironment(ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews) {
		this.connectionLinesAllViews = connectionLinesAllViews;
	}
	
//	public void setActiveViewID(int id) {
//		activeViewID = id;
//	}
	
	public void renderLines(final GL gl) {
		
//		callRenderLine(gl);
//		callRenderPolygonLine(gl);
//		callRenderPolygonLineWithHalo(gl);
//		callRenderAnimatedPolygonLine(gl);
		callRenderAnimatedPolygonLineWithHalo(gl);
	}
	
	
	
	protected void callRenderLine(final GL gl) {
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				VisLink.renderLine(gl, currentLine, 0, 10, true);
			}
		}
	}
	
	protected void callRenderPolygonLine(final GL gl) {
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				VisLink.renderPolygonLine(gl, currentLine, 0, 10, true, true);
			}
		}
	}
	
	protected void callRenderPolygonLineWithHalo(final GL gl) {
		
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				gl.glStencilFunc(GL.GL_NOTEQUAL, 0x1, 0x1);
				gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
				gl.glEnable(GL.GL_STENCIL_TEST);
				VisLink.renderPolygonLineHalo(gl, currentLine, 0);
				gl.glDisable(GL.GL_STENCIL_TEST);;
				VisLink.renderPolygonLine(gl, currentLine, 0);
			}
		}
	}
	
	protected void callRenderAnimatedPolygonLine(final GL gl) {
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				VisLink.renderAnimatedPolygonLine(gl, currentLine, 0, 30, true, true);
//				VisLink.renderAnimatedPolygonLineReverse(gl, currentLine, 0, 30, true, true);
			}
		}
	}
	
	protected void callRenderAnimatedPolygonLineWithHalo(final GL gl) {
		
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				gl.glStencilFunc(GL.GL_NOTEQUAL, 0x1, 0x1);
				gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
				gl.glEnable(GL.GL_STENCIL_TEST);
				VisLink.renderPolygonLineHalo(gl, currentLine, 0);
				gl.glDisable(GL.GL_STENCIL_TEST);;
				VisLink.renderPolygonLine(gl, currentLine, 0);
			}
		}
	}

}
