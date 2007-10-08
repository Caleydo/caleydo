package org.geneview.core.view.swt.jogl;

import java.util.Collection;

import javax.media.opengl.GLCanvas;

//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ISWTGUIManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.swt.widget.SWTEmbeddedJoglWidget;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.ViewType;
import org.geneview.core.view.jogl.JoglCanvasDirectForwarder;
import org.geneview.core.view.jogl.JoglCanvasForwarder;
import org.geneview.core.view.jogl.JoglCanvasForwarderType;
import org.geneview.core.view.jogl.TriggeredAnimator;
import org.geneview.core.view.opengl.IGLCanvasDirector;
import org.geneview.core.view.opengl.IGLCanvasUser;
//import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * 
 * Attention: Each IGLCanvasUser object has to take care of its  initGLCanvas(GLCanvas canvas); method is called.
 * The IGLCanvasDirector tries to call it once inside the initGLCanvasUser(), if the IGLCanvasUser is registered 
 * by that time to the Vector <IGLCanvasUser> vecGLCanvasUser;
 * 
 * @see import org.geneview.core.view.IView
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class SwtJoglGLCanvasViewRep 
extends AViewRep
implements IGLCanvasDirector {
	
	protected int iGlEventListernerId;  // = 99000;
	
	private JoglCanvasForwarder forwarder_GLEventListener = null;
	
	private final JoglCanvasForwarderType canvasForwarderType;
	
	/**
	 * Animator for Jogl thread; namely JoglCanvasForwarder
	 * 
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder
	 * @see com.sun.opengl.util.Animator
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
		
		//iGLCanvasId = iParentContainerId;
		this.iGlEventListernerId = iCanvasForwarderId;
		//iGlForwarderId = iGLCanvasId + iGlForwarderId;
		
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
	
	public final JoglCanvasForwarder getJoglCanvasForwarder() {

		return forwarder_GLEventListener;
	}
	
	/* ----- END: forward to org.geneview.core.view.jogl.JoglCanvasForwarder ----- */
	/* ------------------------------------------------------------------ */
	
	/**
	 * Expose Animator for internal GLCanvas
	 *
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder
	 * @see com.sun.opengl.util.Animator
	 * 
	 * @return Animator for GLCanvas
	 */
	public final TriggeredAnimator getAnimator() {
	
		return refAnimator;
	}
	

	/**
	 * @see org.geneview.core.view.opengl.AGLCanvasDirector#setAnimator(TriggeredAnimator)
	 * @see org.geneview.core.view.opengl.IGLCanvasDirector#setAnimator(org.geneview.core.view.jogl.TriggeredAnimator)
	 */
	public final void setAnimator( final TriggeredAnimator setTriggeredAnimator) {

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

	/**
	 * 
	 * @see org.geneview.core.view.opengl.IGLCanvasDirector#getGlEventListernerId()
	 * 
	 * @return iGLEventListernerId id for gl_forwarder
	 */
	public final int getGlEventListernerId() {
	
		return iGlEventListernerId;
	}
	
	/**
	 * Attention: call setOpenGLCanvasId(int) before calling this method!
	 * 
	 * @see org.geneview.core.view.AViewRep#retrieveGUIContainer()
	 * @see org.geneview.core.view.IView#initView()
	 */
	public void initViewSwtComposit(Composite swtContainer) {
			
		if  (forwarder_GLEventListener == null) {
			
			switch (canvasForwarderType) {
			case DEFAULT_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGlEventListernerId );
				break;
			case GLEVENT_LISTENER_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasDirectForwarder(refGeneralManager,
						this, 
						iGlEventListernerId );
				break;
				
			case ONLY_2D_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGlEventListernerId );
				break;
				
				default:
					throw new GeneViewRuntimeException("initView() unsupported JoglCanvasForwarderType=[" +
							canvasForwarderType.toString() + "]");
			}
			
		}
		

		/* instead of calling AViewRep#retrieveGUIContainer() we request a JOGL widget.. */
		initGLContainer();
		
		assert forwarder_GLEventListener != null : "initView() called more than once! forwarder_GLEventListener!=null !";
				
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
		canvasManager.registerGLCanvasDirector( this, iUniqueId);
		
		canvasManager.registerGLEventListener( forwarder_GLEventListener, iGlEventListernerId );
		canvasManager.addGLEventListener2GLCanvasById( iGlEventListernerId, iUniqueId );
		
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
	
	/**
	 * This method must not be called.
	 * It is empty on intention that AViewRep.initView() is not called if SwtJoglGLCanvasViewRep is derived.
	 * use SwtJoglGLCanvasViewRep.initViewSwtComposit() instead.
	 * 
	 */
	public final void initView() {
		assert false : "Do not call this method! Call SwtJoglGLCanvasViewRep.initViewSwtComposit()";
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.geneview.core.view.opengl.IGLCanvasDirector#destroyDirector()
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
		canvasManager.removeGLEventListener2GLCanvasById( iGlEventListernerId, iUniqueId );
		
		/*
		 * see AGLCanvasDirector.destroyDirector() {
		 */	
			if ( refAnimator != null ) {
				/* stop Animator if Animator was set. */
				refAnimator.stopEventCount();
				
				if ( ! refAnimator.isAnimating() ) {
					/* If not more listeners are registered 
					 * to Animator, deallocate the Animator. */
					refAnimator = null;
				}
			}
		
		removeAllGLCanvasUsers();
		
		forwarder_GLEventListener = null;
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglCanvasViewRep.destroyDirector()  id=" +
				iUniqueId + " ...[DONE]",
				LoggerType.STATUS );
	}

	
	public void setAttributes(int iWidth, int iHeight, int iGLEventListenerId) {
		
		super.setAttributes(iWidth, iHeight);
		
		if ( iGLEventListenerId != -1 ) 
		{
			this.iGlEventListernerId = iGLEventListenerId;
		}
	}


	
	/* ----- AViewRep stuff .. ----- */
	
	
	/**
	 * Creates a GLCanvas
	 * 
	 * @see javax.media.opengl.GLCanvas
	 * @see javax.media.opengl.GLEventListener#init(GLAutoDrawable drawable)
	 * 
	 * @see com.sun.opengl.util.Animator
	 * @see com.sun.opengl.util.Animator#start()
	 * 
	 * @see org.geneview.core.view.AViewRep#retrieveGUIContainer()
	 * @see org.geneview.core.view.jogl.TriggeredAnimator#startEventCount()
	 */
	protected void initGLContainer() {
		
		ISWTGUIManager refISWTGUIManager = refGeneralManager.getSingelton().getSWTGUIManager();
		
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget) refISWTGUIManager.createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
						iParentContainerId, -1, -1);
				
		refSWTContainer = refSWTEmbeddedJoglWidget.getParentComposite();
		
		refSWTEmbeddedJoglWidget.createEmbeddedComposite();

		GLCanvas refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
		assert refGLCanvas != null : "GLCanvas was not be created";
		
		/**
		 * Register GLCanvas; javax.media.opengl.GLEventListener#init(GLAutoDrawable drawable) 
		 */
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
					,LoggerType.ERROR );
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

	

}
