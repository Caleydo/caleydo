package org.caleydo.core.view.swt.jogl.gears;

import org.eclipse.swt.widgets.Composite;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;

import demos.gears.Gears;

/**
 * Sample for running Gears demo. Alter it to fit any 
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class GearsViewRep 
extends SwtJoglGLCanvasViewRep 
implements IView
{	
	/**
	 * Constructor
	 * 
	 */
	public GearsViewRep(IGeneralManager refGeneralManager, 
			int iViewID, 
			int iParentContainerID, 
			int iGLCanvasID,
			String sLabel)
	{
		super(refGeneralManager, 
				iViewID, 
				iParentContainerID,
				iGLCanvasID,
				sLabel);		
	}
	
	public void initViewSwtComposit(Composite swtContainer) {
		
		Gears gears = new Gears();
		
		gLCanvas.addGLEventListener(gears);	
	}
}
	
	
