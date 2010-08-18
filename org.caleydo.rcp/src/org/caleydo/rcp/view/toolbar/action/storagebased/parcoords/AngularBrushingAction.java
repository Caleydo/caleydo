package org.caleydo.rcp.view.toolbar.action.storagebased.parcoords;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.storagebased.AngularBrushingEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class AngularBrushingAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Set angular brush";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/angular_brush.png";

	/**
	 * Constructor.
	 */
	public AngularBrushingAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();
		GeneralManager.get().getEventPublisher().triggerEvent(new AngularBrushingEvent());
	};
}
