package org.caleydo.rcp.action.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class RenderContextAction
extends AToolBarAction
{
	public static final String TEXT = "Render context / Render all";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/toggle_render_context.png";

	boolean bFlag = true;
	/**
	 * Constructor.
	 */
	public RenderContextAction(int iViewID)
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
		bFlag = !bFlag;
		triggerCmdExternalFlagSetter(bFlag, EExternalFlagSetterType.STORAGEBASED_RENDER_CONTEXT);
	};
}
