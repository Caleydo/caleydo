package cerberus.view.gui.swt.gears.jogl;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.media.opengl.GLCanvas;

import org.jgraph.graph.DefaultGraphCell;

import com.sun.opengl.util.Animator;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.ViewInter;
import cerberus.view.gui.swt.SWTEmbeddedGraphWidget;
import cerberus.view.gui.swt.SWTEmbeddedJoglWidget;
import demos.gears.Gears;

public class GearsViewRep implements ViewInter
{
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected GLCanvas refGLCanvas;
	
	public GearsViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
		
		//FIXME: do the following code in a method
		initView();
		retrieveNewWidget();
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

	public void retrieveNewWidget()
	{
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET);

		refGLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
	}

	public void retrieveExistingWidget()
	{
		// TODO Auto-generated method stub
		
	}

}
