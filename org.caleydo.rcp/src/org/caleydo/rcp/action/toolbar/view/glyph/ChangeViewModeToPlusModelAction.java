package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeViewModeToPlusModelAction
	extends AToolBarAction
{
	public static final String TEXT = "Switch View To Distribution Orientation";
	public static final String ICON = "resources/icons/view/glyph/sort_plus.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public ChangeViewModeToPlusModelAction(int iViewID)
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
		// bEnable = !bEnable;
		triggerCmdExternalFlagSetter(bEnable,
				EExternalFlagSetterType.GLYPH_VIEWMODE_PLUS);
	};
}
