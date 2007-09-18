package org.geneview.core.view.swt.jogl.gears;

import javax.media.opengl.GLCanvas;

import org.eclipse.swt.widgets.Composite;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.view.jogl.JoglCanvasDirectForwarder;
import org.geneview.core.view.jogl.JoglCanvasForwarderType;
import org.geneview.core.view.swt.jogl.SwtJoglGLCanvasViewRep;
import org.geneview.core.view.IView;

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
			int iViewId, 
			int iParentContainerId, 
			String sLabel,
			int iGLEventListenerId)
	{
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId,
				iGLEventListenerId,
				sLabel,
				JoglCanvasForwarderType.GLEVENT_LISTENER_FORWARDER);		
	}
	
	/**
	 * 
	 * @see cerberus.view.AViewRep#retrieveGUIContainer()
	 * @see cerberus.view.IView#initView()
	 */
	public void initViewSwtComposit(Composite swtContainer) {
		
		Gears gears = new Gears();
				
		JoglCanvasDirectForwarder forwarder = 
			(JoglCanvasDirectForwarder) this.getJoglCanvasForwarder();
		
		forwarder.setDirectGLEventListener( gears );	
	}

}
	
	
