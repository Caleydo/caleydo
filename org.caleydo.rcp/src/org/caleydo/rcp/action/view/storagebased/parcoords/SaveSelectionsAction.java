package org.caleydo.rcp.action.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;

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
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource(ICON)));
	}

	@Override
	public void run()
	{
		super.run();

		triggerCmdExternalAction(EExternalActionType.PARCOORDS_SAVE_SELECTIONS);
	};
}
