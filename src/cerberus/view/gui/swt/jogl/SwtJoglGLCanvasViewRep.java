package cerberus.view.gui.swt.jogl;

import javax.media.opengl.GLCanvas;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.view.gui.swt.base.AJoglViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.awt.jogl.TriangleMain;


public class SwtJoglGLCanvasViewRep 
extends AJoglViewRep 
implements IView
{
	
	protected int iGLEventListernerId = 99000;
	
	protected int iGLCanvasId = 88000;
	
	
	public SwtJoglGLCanvasViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	public void initView()
	{
		TriangleMain renderer = new TriangleMain();
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewManager();

		canvasManager.registerGLEventListener( renderer, iGLEventListernerId );
		canvasManager.registerGLCanvas( refGLCanvas, iGLCanvasId );
		
		canvasManager.addGLEventListener2GLCanvasById( iGLEventListernerId, iGLCanvasId );
		
		super.setGLEventListener( renderer );
	}
	
	public GLCanvas getGLCanvas() {
		return this.refGLCanvas;
	}
	
}
