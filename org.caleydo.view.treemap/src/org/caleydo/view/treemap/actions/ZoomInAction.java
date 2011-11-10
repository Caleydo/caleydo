package org.caleydo.view.treemap.actions;

import org.caleydo.core.event.view.treemap.ZoomInEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Action for zoom in function.
 * 
 * @author Michael Lafer
 * 
 */

public class ZoomInAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Zoom";
	public static final String ICON = "resources/icons/general/search.png";

	public ZoomInAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ZoomInEvent());
		setChecked(false);
	};

}
