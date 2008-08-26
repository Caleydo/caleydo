package org.caleydo.rcp.action.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class ToggleRenderContextAction
extends AToolBarAction
{
	public static final String TEXT = "Toggle render context";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/toggle_render_context.png";

	/**
	 * Constructor.
	 */
	public ToggleRenderContextAction(int iViewID)
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
			
		triggerCmdExternalAction(EExternalActionType.STORAGEBASED_TOGGLE_RENDER_CONTEXT);
	};
}
