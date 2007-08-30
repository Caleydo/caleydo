package cerberus.view.swt.jogl.gears;

import javax.media.opengl.GLCanvas;

import org.eclipse.swt.widgets.Composite;

import cerberus.manager.IGeneralManager;
import cerberus.view.jogl.JoglCanvasDirectForwarder;
import cerberus.view.jogl.JoglCanvasForwarderType;
import cerberus.view.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.view.IView;

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
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				iParentContainerId, 
				sLabel,
				JoglCanvasForwarderType.GLEVENT_LISTENER_FORWARDER);		
	}
	
	/**
	 * 
	 * @see cerberus.view.AViewRep#retrieveGUIContainer()
	 * @see cerberus.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		Gears gears = new Gears();
				
		JoglCanvasDirectForwarder forwarder = 
			(JoglCanvasDirectForwarder) this.getJoglCanvasForwarder();
		
		forwarder.setDirectGLEventListener( gears );	
	}

}
	
	
