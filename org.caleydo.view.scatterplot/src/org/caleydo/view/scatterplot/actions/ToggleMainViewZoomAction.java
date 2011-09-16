package org.caleydo.view.scatterplot.actions;

import org.caleydo.core.event.view.tablebased.ToggleMainViewZoomEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleMainViewZoomAction extends AToolBarAction implements IToolBarItem {
	
	public static final String TEXT = "Toggle Main View Zoom Mode (z)";
	public static final String ICON = "resources/icons/view/tablebased/parcoords/bookmark.png";

	/**
	 * Constructor.
	 */
	public ToggleMainViewZoomAction() {

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher()
				.triggerEvent(new ToggleMainViewZoomEvent());
	};
}
