package org.caleydo.rcp.view.toolbar.action.storagebased.parcoords;

import org.caleydo.core.manager.event.view.storagebased.ApplyCurrentSelectionToVirtualArrayEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class SaveSelectionsAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Save Selections";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/save_selections.png";

	/**
	 * Constructor.
	 */
	public SaveSelectionsAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();
		ApplyCurrentSelectionToVirtualArrayEvent event = new ApplyCurrentSelectionToVirtualArrayEvent();
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	};
}
