package org.geneview.core.view.opengl;

import java.util.Collection;

import org.geneview.core.data.AUniqueManagedObject;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.view.opengl.IGLCanvasDirector;
import org.geneview.core.view.jogl.JoglCanvasForwarder;
import org.geneview.core.view.jogl.TriggeredAnimator;

public abstract class AGLCanvasDirector 
extends AUniqueManagedObject
implements IGLCanvasDirector {

	private TriggeredAnimator refAnimator = null;
	
	// FIXME: must be loaded from XML file!
	protected int iGlEventListernerId = 99000;

	protected JoglCanvasForwarder forwarder_GLEventListener = null;
	
	protected AGLCanvasDirector(int uniqueId, IGeneralManager setGeneralManager) {

		super(uniqueId, setGeneralManager);
	}

	public final TriggeredAnimator getAnimator() {
		
		return refAnimator;
	}

	public final JoglCanvasForwarder getJoglCanvasForwarder() {

		return forwarder_GLEventListener;
	}
	
	public final void setAnimator(TriggeredAnimator setTriggeredAnimator) {
	
		if ( refAnimator != null ) {
			if ( refAnimator.isAnimating() ) {		
				/* Animator is animating, do not replace it! */
				refGeneralManager.getSingelton().logMsg("setAnimator(" +
						setTriggeredAnimator.toString() + ") is ignored, because existing animator=[" +
						refAnimator.toString() + "] is running!",
						LoggerType.MINOR_ERROR);
				
				return;
			} else {
				/* Animator is not animating, replace Animator */
				refGeneralManager.getSingelton().logMsg("setAnimator(" +
						setTriggeredAnimator.toString() + ") existing animator=[" +
						refAnimator.toString() + "] is allocated but is not running; replace it with new Animator-",
						LoggerType.MINOR_ERROR);
			}
		}
		
		this.refAnimator = setTriggeredAnimator;		
	}
	
	/* ------------------------------------------------------------------ */
	/* ----- END: forward to org.geneview.core.view.jogl.JoglCanvasForwarder ----- */
	
	/* (non-Javadoc)
	 * @see org.geneview.core.view.swt.jogl.IGLCanvasDirector#addGLCanvasUser(org.geneview.core.view.opengl.IGLCanvasUser)
	 */
	public final void addGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.addGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.view.swt.jogl.IGLCanvasDirector#removeGLCanvasUser(org.geneview.core.view.opengl.IGLCanvasUser)
	 */
	public final void removeGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.removeGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.view.swt.jogl.IGLCanvasDirector#removeAllGLCanvasUsers()
	 */
	public final void removeAllGLCanvasUsers() {
		
		forwarder_GLEventListener.removeAllGLCanvasUsers();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.IGLCanvasDirector#containsGLCanvasUser(org.geneview.core.view.opengl.IGLCanvasUser)
	 */
	public final boolean containsGLCanvasUser(IGLCanvasUser user) {

		return forwarder_GLEventListener.containsGLCanvasUser(user);		
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.view.swt.jogl.IGLCanvasDirector#getAllGLCanvasUsers()
	 */
	public final Collection<IGLCanvasUser> getAllGLCanvasUsers() {
		
		return forwarder_GLEventListener.getAllGLCanvasUsers();
	}	
	
	/**
	 * @return the iGLEventListernerId
	 */
	public final int getGlEventListernerId() {
	
		return this.iGlEventListernerId;
	}
	
	/* ----- END: forward to org.geneview.core.view.jogl.JoglCanvasForwarder ----- */
	/* ------------------------------------------------------------------ */
	
	
	public void destroyDirector() {
	
		if ( refAnimator != null ) {
			/* stop Animator if Animator was set. */
			refAnimator.stopEventCount();
			
			if ( ! refAnimator.isAnimating() ) {
				/* If not more listeners are registered 
				 * to Animator, deallocate the Animator. */
				refAnimator = null;
			}
		}
		
	}

}