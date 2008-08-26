package org.caleydo.rcp.action.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class OcclusionPreventionAction
extends AToolBarAction
{
	public static final String TEXT = "Toggle occlusion prevention";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/occlusion_prevention.png";

	private boolean bEnable = false;
	
	/**
	 * Constructor.
	 */
	public OcclusionPreventionAction(int iViewID)
	{
		super(iViewID);
		
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ICON)));
	}
	
	@Override
	public void run()
	{
		super.run();
			
		bEnable = !bEnable;
		
		triggerCmdSExternalFlagSetter(bEnable, EExternalFlagSetterType.PARCOORDS_OCCLUSION_PREVENTION);
	};
}
