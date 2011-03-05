package org.caleydo.view.parcoords.toolbar;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.storagebased.ResetAxisSpacingEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Action that resets the spacing of the axis in the PCs
 * 
 * @author Alexander
 */
public class ResetAxisSpacingAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Reset Axis Spacing";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/reset_axis_spacing.png";

	/**
	 * Constructor.
	 */
	public ResetAxisSpacingAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ResetAxisSpacingEvent());
	};
}
