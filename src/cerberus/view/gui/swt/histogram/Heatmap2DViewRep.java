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
	protected final int iNewId;
	protected IGeneralManager refGeneralManager;
	protected GLCanvas refGLCanvas;
	
	public Heatmap2DViewRep(int iNewId, IGeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
		
		//FIXME: do the following code in a method
		initView();
		retrieveNewGUIContainer();
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

	public void retrieveNewGUIContainer()
	{
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET);

		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
	}

	public void retrieveExistingGUIContainer()
	{
		// TODO Auto-generated method stub
		
	}

}
