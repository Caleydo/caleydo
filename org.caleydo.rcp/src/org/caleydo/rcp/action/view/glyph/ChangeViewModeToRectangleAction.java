package org.caleydo.rcp.action.view.glyph;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;

public class ChangeViewModeToRectangleAction
	extends AToolBarAction
{
	public static final String TEXT = "Switch View To Rectangle";
	public static final String ICON = "resources/icons/view/glyph/sort_zickzack.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public ChangeViewModeToRectangleAction(int iViewID)
	{
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource(ICON)));
		//setChecked(bEnable);
	}

	@Override
	public void run()
	{
		super.run();
		// bEnable = !bEnable;
		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.GLYPH_VIEWMODE_RECTANGLE);
	};
}
