package org.caleydo.view.radial.actions;

import org.caleydo.core.event.view.ToggleMagnifyingGlassEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleMagnifyingGlassAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Magnifying glass";
	public static final String ICON = "resources/icons/general/search.png";

	public ToggleMagnifyingGlassAction() {

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher()
				.triggerEvent(new ToggleMagnifyingGlassEvent());
	}

}
