package cerberus.view.gui.swt.scatterplot.jogl;

import javax.media.opengl.GLCanvas;

import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;
import cerberus.view.gui.ViewType;

import demos.gears.Gears;

public class Scatterplot2DViewRep 
extends AViewRep 
implements IView
{
	protected IGeneralManager refGeneralManager;
	protected GLCanvas refGLCanvas;
	
	public Scatterplot2DViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_SCATTERPLOT2D);

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
	    refGLCanvas.addGLEventListener(new Gears());

	    final Animator animator = new Animator(refGLCanvas);
	    animator.start();
		
	}

	public void retrieveGUIContainer()
	{
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
				iParentContainerId, -1, -1);
		
		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
	}
}
