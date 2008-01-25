package org.geneview.core.view.opengl.canvas.jukebox;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.util.slerp.SlerpAction;
import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.canvas.pathway.JukeboxHierarchyLayer;

/**
 * Implementation of the overall Jukebox setup.
 * It supports the user with the ability to navigate
 * and interact with arbitrary views.
 * 
 * @author Marc Streit
 *
 */
public class GLCanvasOverallJukebox3D
extends AGLCanvasUser
implements IMediatorReceiver, IMediatorSender {
	
//	private GLPathwayMemoPad memoPad;

	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackedLayer;
	private JukeboxHierarchyLayer poolLayer;
	
	private ArrayList<SlerpAction> arSlerpActions;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasOverallJukebox3D(final IGeneralManager generalManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(generalManager, null, iViewId, iParentContainerId, "");

		this.refViewCamera.setCaller(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
	 */
	public void initGLCanvas(GL gl) {
	
		super.initGLCanvas(gl);
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		
		retrieveContainedViews();
	}

	private void retrieveContainedViews() {
		
	}
	
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
		}
	}
}
