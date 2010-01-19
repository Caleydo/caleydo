package org.caleydo.view.base.action.toolbar.view.storagebased.parcoords;

import org.caleydo.core.manager.event.view.storagebased.ResetAxisSpacingEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.base.action.toolbar.AToolBarAction;
import org.caleydo.view.base.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Action that resets the spacing of the axis in the PCs
 * 
 * @author Alexander
 */
public class ResetAxisSpacingAction extends AToolBarAction
		implements
			IToolBarItem {
	public static final String TEXT = "Reset Axis Spacing";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/reset_axis_spacing.png";

	/**
	 * Constructor.
	 */
	public ResetAxisSpacingAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(
				new ResetAxisSpacingEvent());
	};
}
