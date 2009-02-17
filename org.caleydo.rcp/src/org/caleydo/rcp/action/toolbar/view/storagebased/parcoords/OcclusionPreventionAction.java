package org.caleydo.rcp.action.toolbar.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class OcclusionPreventionAction
	extends AToolBarAction
{
	public static final String TEXT = "Toggle occlusion prevention";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/occlusion_prevention.png";

	private boolean bEnable = true;

	/**
	 * Constructor.
	 */
	public OcclusionPreventionAction(int iViewID)
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

		triggerCmdExternalFlagSetter(bEnable,
				EExternalFlagSetterType.PARCOORDS_OCCLUSION_PREVENTION);
	};
}
