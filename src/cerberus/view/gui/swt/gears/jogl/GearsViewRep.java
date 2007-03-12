package cerberus.view.gui.swt.gears.jogl;

import javax.media.opengl.GLCanvas;

//import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
//import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.base.AJoglViewRep;
import cerberus.view.gui.IView;
//import cerberus.view.gui.awt.jogl.GearsMain;
//import cerberus.view.gui.awt.jogl.TriangleMain;
//import cerberus.view.gui.awt.jogl.Histogram2DMain;
//import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;

import demos.gears.Gears;

public class GearsViewRep 
extends AJoglViewRep 
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
		retrieveGUIContainer();
		
		Gears gears = new Gears();		
		super.setGLEventListener( gears );		
	}
}
