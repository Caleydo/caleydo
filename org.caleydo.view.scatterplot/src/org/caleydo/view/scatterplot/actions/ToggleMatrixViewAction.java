package org.caleydo.view.scatterplot.actions;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.tablebased.SwitchMatrixViewEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleMatrixViewAction extends AToolBarAction implements IToolBarItem {
	
	public static final String TEXT = "Switch between Matrix View/ Main View (m)";
	public static final String ICON = "resources/icons/view/tablebased/parcoords/reset_axis_spacing.png";

	/**
	 * Constructor.
	 */
	public ToggleMatrixViewAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher()
				.triggerEvent(new SwitchMatrixViewEvent());
	};
}
