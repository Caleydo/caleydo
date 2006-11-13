package cerberus.view.gui.swt.histogram;

import javax.media.opengl.GLCanvas;

import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;
import cerberus.view.gui.awt.jogl.Histogram2DMain;


public class Heatmap2DViewRep 
extends AViewRep 
implements IView
{
	protected GLCanvas refGLCanvas;
	
	public Heatmap2DViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	
		
		initView();
		retrieveGUIContainer();
		drawView();
	}
	
	public void initView()
	{
		// TODO Auto-generated method stub
		
	}

	public void drawView()
	{
		Histogram2DMain newCanvas = new Histogram2DMain();
		newCanvas.runMain();
		
	    refGLCanvas.addGLEventListener( newCanvas );

	    final Animator animator = new Animator(refGLCanvas);
	    animator.start();
	    
	    //newCanvas.runMain();
		
	}

	public void retrieveGUIContainer()
	{
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(
				ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
				iParentContainerId, -1, -1);

		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
	}
}
