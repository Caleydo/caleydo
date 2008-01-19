package org.geneview.core.view.opengl.canvas.jukebox;

import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;

/**
 * 
 * @author Marc Streit
 *
 */
public class GLCanvasOverallJukebox3D
extends AGLCanvasUser
implements IMediatorReceiver, IMediatorSender {

	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasOverallJukebox3D(final IGeneralManager refGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(refGeneralManager, null, iViewId, iParentContainerId, "");

		this.refViewCamera.setCaller(this);
	}
	
//	/*
//	 * (non-Javadoc)
//	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
//	 */
//	public void initGLCanvas(GL gl) {
//	
//		super.initGLCanvas(gl);
//	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) {

		Iterator<IGLCanvasUser> iterCanvasUser = 
			refGeneralManager.getSingelton().getViewGLCanvasManager()
			.getAllGLCanvasUsers().iterator();
		
		while(iterCanvasUser.hasNext())
		{
			IGLCanvasUser tmp = iterCanvasUser.next();
			
			if(tmp == this)
				continue;
			
			tmp.renderPart(gl);
			gl.glTranslatef(1, 1, 0);
			tmp.renderPart(gl);
		}	
	}

}
