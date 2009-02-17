package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ClearSelectionsAction
	extends AToolBarAction
{

	public static final String TEXT = "Clear the selection";
	public static final String ICON = "resources/icons/view/glyph/glyph_reset_view.png";

	public ClearSelectionsAction(int iViewID)
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

		triggerCmdExternalFlagSetter(false, EExternalFlagSetterType.GLYPH_SELECTION);
	};
}
