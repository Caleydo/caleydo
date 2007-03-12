package cerberus.view.gui.swt.base;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import org.eclipse.swt.widgets.Composite;

import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.jogl.TriggeredAnimator;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;

//import demos.gears.Gears;

/**
 * Abstract Class for all Jogl ViewRep objects.
 * 
 * @author Michael Kalkusch
 */
public abstract class AJoglViewRep 
extends AViewRep 
implements IView {
	
	/**
	 * reference to GLCanvas
	 */
	protected GLCanvas refGLCanvas;
	
	protected GLEventListener refGLEventListener;
	
	protected Composite refSWTContainer;
	
	/**
	 * This flag indicates, that the canvas was created.
	 */
	protected AtomicBoolean abEnableRendering;
	
	/**
	 * Aminator for Jogl thead
	 */
	protected TriggeredAnimator refAnimator = null;
	
	public AJoglViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_JOGL_VIEW);
		
		abEnableRendering = new AtomicBoolean( false );
	}

	/**
	 * Needs to be callled before calling drawView().
	 * Either call this methode from Constructor or inside initView().
	 * 
	 * @see cerberus.view.gui.swt.base.AJoglViewRep#drawView()
	 * @see cerberus.view.gui.swt.base.AJoglViewRep#initView()
	 * 
	 * @param setGListener
	 */
	protected final void setGLEventListener( GLEventListener setGListener) {
		
		assert refGLCanvas != null : "setGLEventListener() can not set GLEventListener, because refGLCanves == null !";
		assert setGListener != null : "setGLEventListener() can not register setGListener==null!";
		
		refGLEventListener = setGListener;
		
		refGLCanvas.addGLEventListener( refGLEventListener );
	}
	
	public final GLEventListener getGLEventListener() {
		return refGLEventListener;
	}
	
	public final void removeGLEventListener( final GLEventListener refGLEventListener) {
		this.refGLCanvas.removeGLEventListener( refGLEventListener );
	}
	
	/**
	 * Method uses the parent container ID to retrieve the 
	 * GUI widget by calling the createWidget method from
	 * the SWT GUI Manager.
	 * 
	 */
	protected void retrieveGUIContainer() {
		
		ISWTGUIManager refISWTGUIManager = refGeneralManager.getSingelton().getSWTGUIManager();
		
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget) refISWTGUIManager.createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
						iParentContainerId, -1, -1);
				
		refSWTContainer = refSWTEmbeddedJoglWidget.getParentComposite();
		
		refSWTEmbeddedJoglWidget.createEmbeddedComposite();

		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
		assert refGLCanvas != null : "GLCanvas was not be created";
		
		//
		// Currently all Animators are registerd with thier ID's
		//
		refAnimator = refISWTGUIManager.getAnimatorById( iUniqueId );
		
		if (( refAnimator != null ) &&( refAnimator.isAnimating() ))
		{			
			refGeneralManager.getSingelton().logMsg(
					"AJoglViewRep.retrieveGUIContainer() cas called more than once + " +
					this.getClass()
					,LoggerType.ERROR_ONLY );
		}
		
		refAnimator.add(refGLCanvas);
		refAnimator.startEventCount();			
			    
	    abEnableRendering.set( true );
	}
	
	/**
	 * Attention: local initView() of derived class must be called before calling this super.initView()
	 * A GLCanvas must be set the refAnimator creates a new thread isdie thsi call.
	 * 
	 */
	public void initView() {
		
//		assert refGLCanvas != null : "Can not start GLCanvas Animator thread with refGLCanvas==null!";
//		
//		if (( refAnimator != null ) &&( refAnimator.isAnimating() ))
//		{			
//			refGeneralManager.getSingelton().logMsg(
//					"AJoglViewRep.drawView() cas called more than once + " +
//					this.getClass()
//					,LoggerType.ERROR_ONLY );
//		}
//		
//		/*
//		refAnimator = new Animator(refGLCanvas);
//		
//	    refAnimator.start();
//	    */
//		
//		refAnimator.add(refGLCanvas);
//		refAnimator.startEventCount();		
//		
//	    abEnableRendering.set( true );
	    
	    refGeneralManager.getSingelton().logMsg(
				"AJoglViewRep.intView() [" + 
				this.iUniqueId + 
				"] GLCavas created, Animator thread is running."
				,LoggerType.VERBOSE );
	    
	    //this.refGLEventListener.init( refGLCanvas );
	}
	
	public void drawView() {
		 refGeneralManager.getSingelton().logMsg(
					"AJoglViewRep.drawView() [" + 
					this.iUniqueId + "]"
					,LoggerType.VERBOSE );
	}
	
	protected void destroyOnExitViewRep() {
		
		abEnableRendering.set( false );
		
		refAnimator.stop();
	}
	
	/**
	 * Work around, fix as soon as possible!
	 * 
	 * FIXME: Work around, fix as soon as possible
	 * 
	 * @deprecated work around, fix as soon as possible
	 * 
	 * @return
	 */
	public Composite getSWTContainer() {
		
		return refSWTContainer;
	}
}
