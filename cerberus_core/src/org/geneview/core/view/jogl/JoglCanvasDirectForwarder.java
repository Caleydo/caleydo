/**
 * 
 */
package org.geneview.core.view.jogl;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.view.opengl.IGLCanvasDirector;


/**
 * Grant direct access to GLAutoDrawable for external GLEventListener object.
 * 
 * @see
 * 
 * @author Michael Kalkusch
 *
 */
public final class JoglCanvasDirectForwarder 
extends JoglCanvasForwarder
implements GLEventListener {

	private GLEventListener listerer;
	
	/**
	 * @param refGeneralManager
	 * @param refGLCanvasDirector
	 * @param uniqueId
	 */
	public JoglCanvasDirectForwarder(IGeneralManager refGeneralManager,
			IGLCanvasDirector refGLCanvasDirector,
			int uniqueId) {

		super(refGeneralManager, refGLCanvasDirector, uniqueId);
	}
	
	public JoglCanvasDirectForwarder(IGeneralManager refGeneralManager,
			IGLCanvasDirector refGLCanvasDirector,
			int uniqueId,
			GLEventListener listener) {

		super(refGeneralManager, refGLCanvasDirector, uniqueId);
		
		setDirectGLEventListener(listerer);
	}
	
	public void setDirectGLEventListener(GLEventListener listerer) {
		this.listerer = listerer;
	}
	
	public GLEventListener getDirectGLEventListener() {
		return this.listerer;
	}


	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(GLAutoDrawable drawable) {

		super.display(drawable);
		listerer.display(drawable);
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {

		super.displayChanged(drawable, modeChanged, deviceChanged);		
		listerer.displayChanged(drawable, modeChanged, deviceChanged);
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(GLAutoDrawable drawable) {

		super.init(drawable);		
		listerer.init(drawable);
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		super.reshape(drawable, x, y, width, height);
		listerer.reshape(drawable, x, y, width, height);		
	}

}
