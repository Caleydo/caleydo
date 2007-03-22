  package cerberus.view.gui.swt.jogl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.view.gui.swt.jogl.AJoglViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.jogl.JoglCanvasForwarder;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * 
 * Attention: Each IGLCanvasUser object has to take care of its  initGLCanvas(GLCanvas canvas); method is called.
 * The IGLCanvasDirector tries to call it once inside the initGLCanvasUser(), if the IGLCanvasUser is registered 
 * by that time to the Vector <IGLCanvasUser> vecGLCanvasUser;
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class SwtJoglGLCanvasViewRep 
extends AJoglViewRep 
implements IView, IGLCanvasDirector {
	
	protected int iGLEventListernerId = 99000;
	
	protected int iGLCanvasId;
	
	protected GLEventListener refGLEventListener;
	
	protected Vector <IGLCanvasUser> vecGLCanvasUser;
	
	public SwtJoglGLCanvasViewRep(final IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
		
		vecGLCanvasUser = new Vector <IGLCanvasUser> ();			
		
		refGeneralManager.getSingelton().getViewGLCanvasManager(
				).registerGLCanvasDirector( this, iViewId );
		
		iGLCanvasId = iParentContainerId;
	}
	
	/**
	 * Attention, side effect! Call this before calling initView() !
	 * 
	 * @param iOpenGLCanvasId
	 */
	public void setOpenGLCanvasId( int iOpenGLCanvasId ) {
		this.iGLCanvasId = iOpenGLCanvasId;		
	}
	
	/**
	 * Attension: call setOpenGLCanvasId(int) before calling this method!
	 * 
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#initView()
	 */
	public void initView() {
			
		retrieveGUIContainer();
		
		assert refGLEventListener == null : "initView() called more than once! refGLEventListener!=null !";
				
		/* Start Animator thread, if anaimort is not running already. */
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglGLCanvasViewRep [" +
				getId() + "] start Animator; start thread ...",
				LoggerType.TRANSITION );
		
		//refGLEventListener = new JoglCanvasTextureForwarder();
		
		refGLEventListener = new JoglCanvasForwarder(refGeneralManager,
				this, 
				iGLEventListernerId );
		
		super.initView();
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglGLCanvasViewRep [" +
				getId() + "] Animator started, thread running.",
				LoggerType.TRANSITION );
		
		refGLEventListener = new JoglCanvasForwarder(refGeneralManager,
				this, 
				iGLEventListernerId );
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();

		canvasManager.registerGLCanvas( refGLCanvas, iGLCanvasId );
		canvasManager.registerGLCanvasDirector( this, iGLCanvasId);
		
		canvasManager.registerGLEventListener( refGLEventListener, iGLEventListernerId );
		canvasManager.addGLEventListener2GLCanvasById( iGLEventListernerId, iGLCanvasId );
		
		setGLEventListener( refGLEventListener );
		
		/**
		 * registering of GLCavnas GLEventListener and GLCanvasDirector to
		 * JogleManager is done!
		 */
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglGLCanvasViewRep [" +
				getId() + "] was initalized!",
				LoggerType.TRANSITION );
		
//		super.initView();
//		
//		refGeneralManager.getSingelton().logMsg(
//				"SwtJoglGLCanvasViewRep [" +
//				getId() + "] Animator started, thread running.",
//				LoggerType.TRANSITION );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#addGLCanvasUser(cerberus.view.gui.opengl.IGLCanvasUser)
	 */
	public void addGLCanvasUser( IGLCanvasUser user ) {
		if ( vecGLCanvasUser.contains( user ) ) {
			throw new CerberusRuntimeException("addGLCanvasUser() try to same user twice!");
		}
		
		/**
		 * Try to avoid java.util.ConcurrentModificationException
		 * 
		 * Critical section:
		 */
		
		//FIXME: test adding of IGLCanvasUser later on
		//super.abEnableRendering.set( false );
						
		vecGLCanvasUser.addElement( user );
		
		/**
		 * End of critical section
		 */		
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglGLCanvasViewRep [" +
				getId() + "] added GLCanvas user=[" +
				user.getId() + "] " + 
				user.toString() ,
				LoggerType.TRANSITION );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#removeGLCanvasUser(cerberus.view.gui.opengl.IGLCanvasUser)
	 */
	public void removeGLCanvasUser( IGLCanvasUser user ) {
		if ( ! vecGLCanvasUser.remove( user ) ) {
			throw new CerberusRuntimeException("removeGLCanvasUser() failed, because the user is not registered!");
		}
		user.destroyGLCanvas();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#removeAllGLCanvasUsers()
	 */
	public void removeAllGLCanvasUsers() {
		
		abEnableRendering.set( false );
		
		Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
		
		while ( iter.hasNext() ) {		
			IGLCanvasUser refIGLCanvasUser = iter.next();
			refIGLCanvasUser.destroyGLCanvas();
			refIGLCanvasUser = null;
		}
		vecGLCanvasUser.clear();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#getAllGLCanvasUsers()
	 */
	public Collection<IGLCanvasUser> getAllGLCanvasUsers() {
		
		return new LinkedList <IGLCanvasUser> ();
	}	
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#getGLCanvas()
	 */
	public GLCanvas getGLCanvas() {
		return this.refGLCanvas;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasDirector#initGLCanvasUser()
	 */
	public synchronized void initGLCanvasUser() {
		
		if ( this.refGLCanvas != null ) 
		{
			refGeneralManager.getSingelton().logMsg(
					"SwtJoglCanvasViewRep.initGLCanvasUser() [" + iUniqueId + "] " + 
					this.getClass().toString(),
					LoggerType.STATUS);
						
			if ( vecGLCanvasUser.isEmpty() ) 
			{
				refGeneralManager.getSingelton().logMsg(
						"SwtJoglCanvasViewRep.initGLCanvasUser() [" + iUniqueId + "] " + 
						this.getClass().toString() + "  no GLCanvasUSer yet!",
						LoggerType.MINOR_ERROR);
				return;
			}
			
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			refGeneralManager.getSingelton().logMsg(
					"SwtJoglCanvasViewRep.initGLCanvasUser() [" + iUniqueId + "] " + 
					this.getClass().toString() + "  init GLCanvasUSer ..",
					LoggerType.STATUS);
			
			while ( iter.hasNext() ) {

				IGLCanvasUser glCanvas = iter.next();
				if  ( ! glCanvas.isInitGLDone() )
				{
					glCanvas.initGLCanvas( refGLCanvas );
				}
				
			} // while ( iter.hasNext() ) {
			
			return;
			
		} // if ( this.refGLCanvas != null ) 
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglCanvasViewRep.initGLCanvasUser() [" + iUniqueId + "] " + 
				this.getClass().toString() + "  no GLCanvas yet!",
				LoggerType.MINOR_ERROR);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasDirector#renderGLCanvasUser(javax.media.opengl.GLAutoDrawable)
	 */
	public void renderGLCanvasUser(GLAutoDrawable drawable) {
		
		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				
//				//old code: test if canvas was initialized
//				IGLCanvasUser glCanvas = iter.next();
//				if ( glCanvas.isInitGLDone() )
//				{
//					glCanvas.render( drawable );
//				}
				
				iter.next().render( drawable );
			}
		}
	}
	
	public void reshapeGLCanvasUser(GLAutoDrawable drawable, 
			final int x, final int y, 
			final int width, final int height) {

		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				IGLCanvasUser glCanvas = iter.next();
				if ( glCanvas.isInitGLDone() )
				{
					glCanvas.reshape( drawable, x, y, width, height );
				}
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasDirector#updateGLCanvasUser(javax.media.opengl.GLAutoDrawable)
	 */
	public void updateGLCanvasUser(GLAutoDrawable drawable) {
		
		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				iter.next().update( drawable );
			}
		}
	}
	
	public void displayGLChanged(GLAutoDrawable drawable, final boolean modeChanged, final boolean deviceChanged) {

		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				iter.next().displayChanged(drawable, modeChanged, deviceChanged);
			}
		}
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasDirector#destroyDirector()
	 */
	public void destroyDirector() {
		
		refGeneralManager.getSingelton().logMsg("SwtJoglCanvasViewRep.destroyDirector()  id=" +
				iUniqueId,
				LoggerType.STATUS );
		
		super.removeGLEventListener( refGLEventListener );
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		canvasManager.unregisterGLCanvasDirector( this );
		canvasManager.unregisterGLCanvas( refGLCanvas );		
		canvasManager.unregisterGLEventListener( refGLEventListener );
		canvasManager.removeGLEventListener2GLCanvasById( iGLEventListernerId, iGLCanvasId );
		
		destroyOnExitViewRep();
		
		removeAllGLCanvasUsers();
		
		refGLEventListener = null;
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglCanvasViewRep.destroyDirector()  id=" +
				iUniqueId + " ...[DONE]",
				LoggerType.STATUS );
	}

	public void setAttributes(int iWidth, int iHeight, int iGLCanvasId, int iGLEventListenerId) {
		
		super.setAttributes(iWidth, iHeight);
		
		if ( iGLCanvasId != -1 ) 
		{
			this.iGLCanvasId = iGLCanvasId;
		}
		
		if ( iGLEventListenerId != -1 ) 
		{
			this.iGLEventListernerId = iGLEventListenerId;
		}
	}
}
