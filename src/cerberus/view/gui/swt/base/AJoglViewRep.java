package cerberus.view.gui.swt.base;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
//import cerberus.view.gui.awt.jogl.GearsMain;
//import cerberus.view.gui.awt.jogl.TriangleMain;
//import cerberus.view.gui.awt.jogl.Histogram2DMain;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;

//import demos.gears.Gears;

public abstract class AJoglViewRep 
extends AViewRep 
implements IView
{
	/**
	 * reference to GLCanvas
	 */
	protected GLCanvas refGLCanvas;
	
	protected GLEventListener refGLEventListener;
	
	private Animator refAnimator;
	
	public AJoglViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
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
		
		assert refGLCanvas != null : "can not set GLEventListener, because refGLCanves == null !";
		
		refGLEventListener = setGListener;
		
		refGLCanvas.addGLEventListener( refGLEventListener );
	}
	
	public final GLEventListener getGLEventListener() {
		return refGLEventListener;
	}
	
	public final void removeGLEventListener( final GLEventListener refGLEventListener) {
		this.refGLCanvas.removeGLEventListener( refGLEventListener );
	}
	


	public final void retrieveGUIContainer()
	{
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget)refGeneralManager.getSingelton()
				.getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
						iParentContainerId, -1, -1);

		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();		
	}
	
	public void drawView()
	{
		
		refAnimator = new Animator(refGLCanvas);
		
	    refAnimator.start();
	}
	
	protected void destroyOnExitViewRep() {
		
		refAnimator.stop();
	}
}
