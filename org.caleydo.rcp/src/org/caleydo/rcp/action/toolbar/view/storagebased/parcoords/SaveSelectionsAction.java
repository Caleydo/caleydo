package org.caleydo.rcp.action.toolbar.view.storagebased.parcoords;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.view.storagebased.ApplyCurrentSelectionToVirtualArrayEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
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
