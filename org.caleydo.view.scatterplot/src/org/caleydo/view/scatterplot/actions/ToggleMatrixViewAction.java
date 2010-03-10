package org.caleydo.view.scatterplot.actions;

import org.caleydo.core.manager.event.view.storagebased.SwitchMatrixViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleMatrixViewAction extends AToolBarAction implements
		IToolBarItem {
	public static final String TEXT = "Switch between Matrix View/ Main View (m)";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/reset_axis_spacing.png";

	/**
	 * Constructor.
	 */
	public ToggleMatrixViewAction(int iViewID) {
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
				new SwitchMatrixViewEvent());
	};
}
