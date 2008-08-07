package org.caleydo.core.view.swt.jogl.gears;

import org.caleydo.core.view.IView;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;
import org.eclipse.swt.widgets.Composite;
import demos.gears.Gears;

/**
 * Sample for running Gears demo. Alter it to fit any
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GearsViewRep
	extends SwtJoglGLCanvasViewRep
	implements IView
{

	/**
	 * Constructor
	 */
	public GearsViewRep(int iViewID, int iParentContainerID,
			int iGLCanvasID, String sLabel)
	{
		super(iViewID, iParentContainerID, iGLCanvasID, sLabel);
	}

	public void initViewSwtComposit(Composite swtContainer)
	{
		Gears gears = new Gears();

		gLCanvas.addGLEventListener(gears);
	}
}
