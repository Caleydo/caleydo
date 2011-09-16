package org.caleydo.view.radial.actions;

import org.caleydo.core.event.view.radial.GoBackInHistoryEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class GoBackInHistoryAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Back";
	public static final String ICON = "resources/icons/view/general/undo.png";

	public GoBackInHistoryAction() {
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
