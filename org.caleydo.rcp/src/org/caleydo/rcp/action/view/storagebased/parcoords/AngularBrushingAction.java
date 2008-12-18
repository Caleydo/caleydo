package org.caleydo.rcp.action.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class AngularBrushingAction
	extends AToolBarAction
{
	public static final String TEXT = "Set angular brush";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/angular_brush.png";

	/**
	 * Constructor.
	 */
	public AngularBrushingAction(int iViewID)
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

		triggerCmdExternalAction(EExternalActionType.PARCOORDS_ANGULAR_BRUSHING);
	};
}
