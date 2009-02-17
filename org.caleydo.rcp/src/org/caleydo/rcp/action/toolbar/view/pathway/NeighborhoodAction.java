package org.caleydo.rcp.action.toolbar.view.pathway;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

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
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(bEnable);
	}

	@Override
	public void run()
	{
		super.run();

		bEnable = !bEnable;

		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.PATHWAY_NEIGHBORHOOD);
	};
}
