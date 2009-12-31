package org.caleydo.rcp.action.toolbar.view.storagebased.scatterplot;

import org.caleydo.core.manager.event.view.storagebased.ResetScatterSelectionEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ScatterplotResetSelectionAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Reset Selections";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/reset_axis_spacing.png";

	/**
	 * Constructor.
	 */
	public ScatterplotResetSelectionAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ResetScatterSelectionEvent());
	};
}
