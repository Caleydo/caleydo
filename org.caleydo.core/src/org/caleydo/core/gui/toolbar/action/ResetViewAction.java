package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.ResetAllViewsEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ResetViewAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Reset View";
	public static final String ICON = "resources/icons/view/general/reset_view.png";

	public ResetViewAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ResetAllViewsEvent());
	};
}
