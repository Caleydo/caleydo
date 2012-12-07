/**
 *
 */
package org.caleydo.view.enroute.toolbar.actions;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.enroute.event.FitToViewWidthEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Button that enables the fit to view width mode in enRoute.
 *
 * @author Christian Partl
 *
 */
public class FitToViewWidthAction extends Action {

	public static final String LABEL = "Fit to view width";
	public static final String ICON = "resources/icons/view/enroute/fit_to_width.png";

	/**
	 * Constructor.
	 */
	public FitToViewWidthAction(boolean fitToViewWidth) {
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(fitToViewWidth);
	}

	@Override
	public void run() {
		super.run();
		GeneralManager.get().getEventPublisher()
				.triggerEvent(new FitToViewWidthEvent(isChecked()));
	}
}
