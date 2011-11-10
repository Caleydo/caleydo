package org.caleydo.view.parcoords.toolbar;

import org.caleydo.core.event.view.tablebased.ResetAxisSpacingEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Action that resets the spacing of the axis in the PCs
 * 
 * @author Alexander
 */
public class ResetAxisSpacingAction extends AToolBarAction implements IToolBarItem {
	public static final String TEXT = "Reset Axis Spacing";
	public static final String ICON = "resources/icons/view/tablebased/parcoords/reset_axis_spacing.png";

	/**
	 * Constructor.
	 */
	public ResetAxisSpacingAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher()
				.triggerEvent(new ResetAxisSpacingEvent());
	};
}
