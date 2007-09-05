package cerberus.view.opengl;

import java.util.Collection;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.view.opengl.IGLCanvasDirector;
import cerberus.view.jogl.JoglCanvasForwarder;
import cerberus.view.jogl.TriggeredAnimator;

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
	/* ----- END: forward to cerberus.view.jogl.JoglCanvasForwarder ----- */
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#addGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public final void addGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.addGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public final void removeGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.removeGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeAllGLCanvasUsers()
	 */
	public final void removeAllGLCanvasUsers() {
		
		forwarder_GLEventListener.removeAllGLCanvasUsers();
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#containsGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public final boolean containsGLCanvasUser(IGLCanvasUser user) {

		return forwarder_GLEventListener.containsGLCanvasUser(user);		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#getAllGLCanvasUsers()
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
	
	/* ----- END: forward to cerberus.view.jogl.JoglCanvasForwarder ----- */
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