package org.caleydo.rcp.action.toolbar.view;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ClearSelectionsAction
	extends AToolBarAction {

	public static final String TEXT = "Clear all selections";
	public static final String ICON = "resources/icons/view/storagebased/clear_selections.png";

	public ClearSelectionsAction() {
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		triggerCmdExternalAction(EExternalActionType.CLEAR_SELECTIONS);
	};
}
