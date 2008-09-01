package org.caleydo.rcp.action.view.pathway;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class NeighborhoodAction
extends AToolBarAction
{
	public static final String TEXT = "Turn on/off neighborhood";
	public static final String ICON = "resources/icons/view/pathway/neighborhood.png";

	private boolean bEnable = false;
	
	/**
	 * Constructor.
	 */
	public NeighborhoodAction(int iViewID)
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
		
		triggerCmdSExternalFlagSetter(bEnable, EExternalFlagSetterType.PATHWAY_NEIGHBORHOOD);
	};
}
