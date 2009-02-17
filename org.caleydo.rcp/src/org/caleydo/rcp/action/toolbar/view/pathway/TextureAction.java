package org.caleydo.rcp.action.toolbar.view.pathway;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class TextureAction
	extends AToolBarAction
{
	public static final String TEXT = "Turn on/off pathway textures";
	public static final String ICON = "resources/icons/view/pathway/texture_on_off.png";

	private boolean bEnable = true;

	/**
	 * Constructor.
	 */
	public TextureAction(int iViewID)
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

		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.PATHWAY_TEXTURES);

	};
}
