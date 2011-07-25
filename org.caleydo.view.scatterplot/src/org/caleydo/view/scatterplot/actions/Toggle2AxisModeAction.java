package org.caleydo.view.scatterplot.actions;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.dimensionbased.Toggle2AxisEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class Toggle2AxisModeAction extends AToolBarAction implements IToolBarItem {
	public static final String TEXT = "Enable/Disable 2x2 Axis Mode (b)";
	public static final String ICON = "resources/icons/view/dimensionbased/parcoords/bookmark.png";

	/**
	 * Constructor.
	 */
	public Toggle2AxisModeAction() {

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new Toggle2AxisEvent());
	};
}
