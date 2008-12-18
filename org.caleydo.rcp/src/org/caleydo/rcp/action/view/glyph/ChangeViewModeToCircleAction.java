package org.caleydo.rcp.action.view.glyph;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeViewModeToCircleAction
	extends AToolBarAction
{
	public static final String TEXT = "Switch View To Spiral Orientation";
	public static final String ICON = "resources/icons/view/glyph/sort_spirale.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public ChangeViewModeToCircleAction(int iViewID)
	{
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		// setChecked(false);
	}

	@Override
	public void run()
	{
		super.run();
		// bEnable = !bEnable;
		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.GLYPH_VIEWMODE_CIRCLE);
	};
}
