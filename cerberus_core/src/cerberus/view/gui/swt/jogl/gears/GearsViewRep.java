package cerberus.view.gui.swt.jogl.gears;

import javax.media.opengl.GLCanvas;

import cerberus.manager.IGeneralManager;
import cerberus.view.gui.jogl.JoglCanvasDirectForwarder;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.view.gui.IView;

import demos.gears.Gears;

/**
 * Sample for running Gears demo. Alter it to fit any 
 * 
 * @author Michael Kalkusch
 *
 */
public class GearsViewRep 
extends SwtJoglGLCanvasViewRep 
implements IView
{
	protected GLCanvas refGLCanvas;
	
	public GearsViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, iParentContainerId, sLabel);
		
		useJoglCanvasDirectForwarder();
	}
	
	public void initView()
	{
		retrieveGUIContainer();
		
		Gears gears = new Gears();
				
		JoglCanvasDirectForwarder forwarder = 
			(JoglCanvasDirectForwarder) this.getJoglCanvasForwarder();
		
		forwarder.setDirectGLEventListener( gears );	
	}

}
	
	
