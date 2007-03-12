package cerberus.view.gui.swt.jogl.sample;

//import javax.media.opengl.GLCanvas;
//import javax.media.opengl.GLEventListener;

import cerberus.manager.IGeneralManager;
import cerberus.view.gui.swt.base.AJoglViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.awt.jogl.TriangleMain;


public class TestTriangleViewRep 
extends AJoglViewRep 
implements IView
{
	
	public TestTriangleViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	public void initView()
	{
		retrieveGUIContainer();
		
		TriangleMain renderer = new TriangleMain();		
		
		refGLEventListener = renderer;
		
		super.setGLEventListener( refGLEventListener );
	}
	
}
