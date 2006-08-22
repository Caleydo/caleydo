package cerberus.view.gui.swt.gears.jogl;

import javax.media.opengl.GLCanvas;

import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.awt.jogl.GearsMain;
import cerberus.view.gui.awt.jogl.TriangleMain;
import cerberus.view.gui.awt.jogl.Histogram2DMain;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;

public class GearsViewRep 
extends AViewRep 
implements IView
{
	protected GLCanvas refGLCanvas;
	
	public GearsViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	public void initView()
	{
		// TODO Auto-generated method stub
		
	}

	public void drawView()
	{
		Histogram2DMain canvas = new Histogram2DMain();
		
		/**
		 * Calling "canvas.runMain();" starts a new thread an a new AWT-Frame
		 */
		//canvas.runMain();
		
		refGLCanvas.addGLEventListener( canvas );
		
		/**
		 * old code
		 */
		//refGLCanvas.addGLEventListener( new TriangleMain() );
		
	    //refGLCanvas.addGLEventListener(new GearsMain());

	    final Animator animator = new Animator(refGLCanvas);
	    animator.start();
		
	}

	public void retrieveGUIContainer()
	{
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET);

		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
	}
}
