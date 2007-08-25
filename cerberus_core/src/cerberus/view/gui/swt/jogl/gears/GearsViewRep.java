package cerberus.view.gui.swt.jogl.gears;

import javax.media.opengl.GLCanvas;

//import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
//import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.opengl.GLCanvasUserWrapper;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.view.gui.IView;
//import cerberus.view.gui.awt.jogl.GearsMain;
//import cerberus.view.gui.awt.jogl.TriangleMain;
//import cerberus.view.gui.awt.jogl.Histogram2DMain;
//import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;

import demos.gears.Gears;

public class GearsViewRep 
extends SwtJoglGLCanvasViewRep 
implements IView
{
	protected GLCanvas refGLCanvas;
	
	public GearsViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, iParentContainerId, sLabel);
	}
	
	public void initView()
	{
		retrieveGUIContainer();
		
		Gears gears = new Gears();			
		GLCanvasUserWrapper wrapper = new GLCanvasUserWrapper(refGeneralManager,
				iUniqueId,
				iParentContainerId,
				sLabel );
		wrapper.addGL( gears );
		
		addGLCanvasUser( wrapper );		
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.IView#drawView()
	 */
	public void drawView() {
		
//	    final Animator animator = new Animator(refGLCanvas);
//	    animator.start();
		
	}
}
	
	
