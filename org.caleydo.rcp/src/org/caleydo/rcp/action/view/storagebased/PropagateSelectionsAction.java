package org.caleydo.rcp.action.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;


public class PropagateSelectionsAction
extends AToolBarAction
{
	public static final String TEXT = "Broadcast elements to other views";
	public static final String ICON = "resources/icons/view/storagebased/broadcast_elements.png";

	/**
	 * Constructor.
	 */
	public PropagateSelectionsAction(int iViewID)
	{
		super(iViewID);
		
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}
	
	@Override
	public void run()
	{
		super.run();
			
		triggerCmdExternalAction(EExternalActionType.STORAGEBASED_PROPAGATE_SELECTIONS);
	};
}
