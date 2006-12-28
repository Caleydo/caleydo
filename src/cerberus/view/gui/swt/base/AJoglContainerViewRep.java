package cerberus.view.gui.swt.base;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
//import cerberus.manager.SWTGUIManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.exception.CerberusExceptionType;
import demos.gears.Gears;

/**
 * To be able to create a JOGL canvas you have to register your
 * derived class to this super class AJoglContainerViewRep via super.addGLEventListener( this );
 * you also have to take care, that the JOGL Animator ist set and started 
 * via joglAnimator.start().
 * 
 * If you use an existing JOGL canvas you can assume, that the animator 
 * is aleady running. You can check this by calling getAnimator().isAnimating() and make sure, 
 * that getAnimator() does not return null.
 *  
 * This could be either done inside the dericed mehtode initView() or
 * inside the derived methode drawView()
 * 
 * @see cerberus.view.gui.swt.base.ISwtJoglContainerViewRep
 * 
 * @see com.sun.opengl.util.Animator  
 * @see javax.media.opengl.GLCanvas
 * @see javax.media.opengl.GLEventListener
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AJoglContainerViewRep 
extends AViewRep
implements IView, ISwtJoglContainerViewRep
{
	/**
	 * OpenGL context of thsi canvas handled by JOGL.
	 */
	protected GLCanvas refGLCanvas;
	
	protected GLEventListener refGLEventListener;
	
	/**
	 * Animaotr for this canvas.
	 */
	protected Animator joglAnimator;
	
	/**
	 * Default constructor.
	 * 
	 * After calling the construtor the follogwing methodes should 
	 */
	public AJoglContainerViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	
	}

	/**
	 * Creates a new cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget
	 * from the  cerberus.manager.SWTGUIManager and register the OpenGL canvas.
	 * 
	 * @see cerberus.manager.SWTGUIManager
	 * @see cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget
	 * @see cerberus.view.gui.IView#retrieveNewGUIContainer()
	 */
	public void retrieveNewGUIContainer()
	{
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget)refGeneralManager.getSingelton()
				.getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
						iParentContainerId, -1, -1);

		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
	}

	/**
	 * Empyt default methode.
	 * Shall suit inteface cerberus.view.gui.IView
	 * 
	 * @see cerberus.view.gui.IView#retrieveExistingGUIContainer()
	 */
	public void retrieveExistingGUIContainer()
	{
		assert false : "AJoglContainerViewRep.retrieveExistingGUIContainer() is not implemented!";
	}
	
	/**
	 * @see cerberus.view.gui.swt.base.ISwtJoglContainerViewRep#getAnimator()
	 */
	public final Animator getAnimator() {
		return joglAnimator;
	}
	
	/**
	 * @see cerberus.view.gui.swt.base.ISwtJoglContainerViewRep#setAnimator(com.sun.opengl.util.Animator)
	 */
	public final boolean setAnimator( final Animator setAnimator ) {
		
		if ( setAnimator == null) {
			return false;
		}
		
		joglAnimator = setAnimator;
				
		return true;
	}
	
	/**
	 * @see cerberus.view.gui.swt.base.ISwtJoglContainerViewRep#addGLEventListener(javax.media.opengl.GLEventListener)
	 */
	public final void addGLEventListener( final GLEventListener setGLEventListener ) {
		
		if ( refGLCanvas == null ) {
			throw new CerberusRuntimeException(
					"JoglContainerViewRep::removeGLEventListener_JOGL() failed because GLCanvas was not created!");
		}
		
		refGLCanvas.addGLEventListener( setGLEventListener );
	}
	
	/**
	 * @see cerberus.view.gui.swt.base.ISwtJoglContainerViewRep#removeGLEventListener(javax.media.opengl.GLEventListener)
	 */
	public final void removeGLEventListener( final GLEventListener setGLEventListener ) {
		
		if ( refGLCanvas == null ) {
			throw new CerberusRuntimeException(
					"JoglContainerViewRep::removeGLEventListener_JOGL() failed because GLCanvas was not created!",
					CerberusExceptionType.JOGL_SWT );
		}
		
		refGLCanvas.removeGLEventListener( setGLEventListener );
	}
	
	/**
	 * @see cerberus.view.gui.swt.base.ISwtJoglContainerViewRep#setGLCanvas(javax.media.opengl.GLCanvas)
	 */
	public final boolean setGLCanvas( final GLCanvas setGLCanvas ) {
				
		if ( setGLCanvas == null) {
			return false;
		}
		
		refGLCanvas = setGLCanvas;
		
		return true;
	}
	
	/**
	 * @see cerberus.view.gui.swt.base.ISwtJoglContainerViewRep#getGLCanvas()
	 */
	public final GLCanvas getGLCanvas() {
		return refGLCanvas;
	}
	
	public final GLEventListener getGLEventListener() {
		return refGLEventListener;
	}

}
