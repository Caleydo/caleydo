package org.caleydo.view.radial.actions;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.radial.GoBackInHistoryEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class GoBackInHistoryAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Back";
	public static final String ICON = "resources/icons/view/general/undo.png";

	public GoBackInHistoryAction(int viewID) {
		super(viewID);
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		GeneralManager.get().getEventPublisher().triggerEvent(new GoBackInHistoryEvent());
		setChecked(false);
	};
}
