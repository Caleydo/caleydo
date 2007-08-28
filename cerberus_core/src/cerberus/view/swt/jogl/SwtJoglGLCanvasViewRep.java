  package cerberus.view.swt.jogl;

import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;

import org.eclipse.swt.widgets.Composite;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.swt.widget.SWTEmbeddedJoglWidget;
import cerberus.view.AViewRep;
import cerberus.view.ViewType;
import cerberus.view.jogl.JoglCanvasDirectForwarder;
import cerberus.view.jogl.JoglCanvasForwarder;
import cerberus.view.jogl.JoglCanvasForwarderType;
import cerberus.view.jogl.TriggeredAnimator;
import cerberus.view.opengl.IGLCanvasDirector;
import cerberus.view.opengl.IGLCanvasUser;
//import cerberus.util.exception.GeneViewRuntimeException;

/**
 * 
 * Attention: Each IGLCanvasUser object has to take care of its  initGLCanvas(GLCanvas canvas); method is called.
 * The IGLCanvasDirector tries to call it once inside the initGLCanvasUser(), if the IGLCanvasUser is registered 
 * by that time to the Vector <IGLCanvasUser> vecGLCanvasUser;
 * 
 * @see import cerberus.view.IView
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class SwtJoglGLCanvasViewRep 
extends AViewRep
//extends AJoglViewRep 
implements IGLCanvasDirector {
	
	protected int iGLEventListernerId = 99000;
	
	protected int iGLCanvasId;
	
	private JoglCanvasForwarder forwarder_GLEventListener = null;
	
	private Composite refSWTContainer;
	
	private final JoglCanvasForwarderType canvasForwarderType;
	
	//protected Vector <IGLCanvasUser> vecGLCanvasUser;
	
	/**
	 * Animator for Jogl thread
	 */
	protected TriggeredAnimator refAnimator = null;
	
	
	public SwtJoglGLCanvasViewRep(final IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			int iCanvasForwarderId,
			String sLabel,
			JoglCanvasForwarderType type) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId,
				sLabel, 
				ViewType.SWT_JOGL_VIEW );
		
		canvasForwarderType = type;
						
		refGeneralManager.getSingelton().getViewGLCanvasManager(
				).registerGLCanvasDirector( this, iViewId );
		
		iGLCanvasId = iParentContainerId;
		//iGLEventListernerId = iGLCanvasId + iGLEventListernerId;
		
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
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#initView()
	 */
	public void initView() {
			
		if  (forwarder_GLEventListener == null) {
			
			switch (canvasForwarderType) {
			case DEFAULT_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGLEventListernerId );
				break;
			case GLEVENT_LISTENER_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasDirectForwarder(refGeneralManager,
						this, 
						iGLEventListernerId );
				break;
				
			case ONLY_2D_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGLEventListernerId );
				break;
				
				default:
					throw new GeneViewRuntimeException("initView() unsupported JoglCanvasForwarderType=[" +
							canvasForwarderType.toString() + "]");
			}
			
		}
		
		retrieveGUIContainer();
		
		assert forwarder_GLEventListener == null : "initView() called more than once! forwarder_GLEventListener!=null !";
				
		/* Start Animator thread, if animator is not running already. */
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglGLCanvasViewRep [" +
				getId() + "] start Animator; start thread ...",
				LoggerType.TRANSITION );
		

		
		//this.vecGLCanvasUser.add(forwarder_GLEventListener);
		
		//super.initView();
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglGLCanvasViewRep [" +
				getId() + "] Animator started, thread running.",
				LoggerType.TRANSITION );
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();

		// next line importatne???
		//canvasManager.registerGLCanvas( refGLCanvas, iGLCanvasId );
		canvasManager.registerGLCanvasDirector( this, iGLCanvasId);
		
		canvasManager.registerGLEventListener( forwarder_GLEventListener, iGLEventListernerId );
		canvasManager.addGLEventListener2GLCanvasById( iGLEventListernerId, iGLCanvasId );
		
		//setGLEventListener( forwarder_GLEventListener );
		
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
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#addGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public void addGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.addGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public void removeGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.removeGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeAllGLCanvasUsers()
	 */
	public void removeAllGLCanvasUsers() {
		
		forwarder_GLEventListener.removeAllGLCanvasUsers();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#getAllGLCanvasUsers()
	 */
	public Collection<IGLCanvasUser> getAllGLCanvasUsers() {
		
		return forwarder_GLEventListener.getAllGLCanvasUsers();
	}	
	

//	public GLAutoDrawable getGLDrawable() {
//		return this.refGLCanvas;
//	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#initGLCanvasUser()
	 */
	public synchronized void initGLCanvasUser(GL gl) {
		
		forwarder_GLEventListener.initGLCanvasUser(gl);
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#renderGLCanvasUser(javax.media.opengl.GLAutoDrawable)
	 */
	public void renderGLCanvasUser( GL gl) {
		
		forwarder_GLEventListener.renderGLCanvasUser(gl);
	}
	
	public void reshapeGLCanvasUser(GL gl, 
			final int x, final int y, 
			final int width, final int height) {

		forwarder_GLEventListener.reshapeGLCanvasUser( gl, x, y, width, height );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#updateGLCanvasUser(javax.media.opengl.GLAutoDrawable)
	 */
	public void updateGLCanvasUser(GL gl) {
		
		forwarder_GLEventListener.updateGLCanvasUser( gl );
	}
	
	public void displayGLChanged(GL gl, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		forwarder_GLEventListener.displayGLChanged(gl, modeChanged, deviceChanged);
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#destroyDirector()
	 */
	public void destroyDirector() {
		
		refGeneralManager.getSingelton().logMsg("SwtJoglCanvasViewRep.destroyDirector()  id=" +
				iUniqueId,
				LoggerType.STATUS );
		
		//super.removeGLEventListener( forwarder_GLEventListener );
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		canvasManager.unregisterGLCanvasDirector( this );
		//canvasManager.unregisterGLCanvas( refGLCanvas );		
		canvasManager.unregisterGLEventListener( forwarder_GLEventListener );
		canvasManager.removeGLEventListener2GLCanvasById( iGLEventListernerId, iGLCanvasId );
		
		destroyOnExitViewRep();
		
		removeAllGLCanvasUsers();
		
		forwarder_GLEventListener = null;
		
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

	public final JoglCanvasForwarder getJoglCanvasForwarder() {

		return forwarder_GLEventListener;
	}
	
	/* ----- AViewRep stuff .. ----- */
	
	protected void retrieveGUIContainer() {
		
		ISWTGUIManager refISWTGUIManager = refGeneralManager.getSingelton().getSWTGUIManager();
		
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget) refISWTGUIManager.createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
						iParentContainerId, -1, -1);
				
		refSWTContainer = refSWTEmbeddedJoglWidget.getParentComposite();
		
		refSWTEmbeddedJoglWidget.createEmbeddedComposite();

		GLCanvas refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
		assert refGLCanvas != null : "GLCanvas was not be created";
		
		refGLCanvas.addGLEventListener( forwarder_GLEventListener );
		
		//
		// Currently all Animators are register with their ID's
		//
		refAnimator = refISWTGUIManager.getAnimatorById( iUniqueId );
		
		if (( refAnimator != null ) &&( refAnimator.isAnimating() ))
		{			
			refGeneralManager.getSingelton().logMsg(
					"AJoglViewRep.retrieveGUIContainer() was called more than once + " +
					this.getClass()
					,LoggerType.ERROR_ONLY );
		}
		
		refAnimator.add(refGLCanvas);
		refAnimator.startEventCount();			
			    
	    //abEnableRendering.set( true );
	}
	
	public void drawView() {
		 refGeneralManager.getSingelton().logMsg(
					"SwtJoglGLCanvasViewRep.drawView() [" + 
					this.iUniqueId + "]"
					,LoggerType.VERBOSE );
	}
	
	protected void destroyOnExitViewRep() {
		
		//abEnableRendering.set( false );
		
		refAnimator.stop();
	}
}
