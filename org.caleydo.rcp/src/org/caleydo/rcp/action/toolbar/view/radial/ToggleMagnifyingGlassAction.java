package org.caleydo.rcp.action.toolbar.view.radial;

import org.caleydo.core.manager.event.view.ToggleMagnifyingGlassEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleMagnifyingGlassAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Magnifying glass";
	public static final String ICON = "resources/icons/general/search.png";

	public ToggleMagnifyingGlassAction() {
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ToggleMagnifyingGlassEvent());
	}

}
