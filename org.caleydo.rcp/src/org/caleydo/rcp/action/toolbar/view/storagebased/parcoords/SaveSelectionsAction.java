package org.caleydo.rcp.action.toolbar.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class SaveSelectionsAction
	extends AToolBarAction
{
	public static final String TEXT = "Save Selections";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/save_selections.png";

	/**
	 * Constructor.
	 */
	public SaveSelectionsAction(int iViewID)
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

		triggerCmdExternalAction(EExternalActionType.PARCOORDS_SAVE_SELECTIONS);
	};
}
