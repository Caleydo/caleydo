package org.caleydo.rcp.action.toolbar.view;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.RemoveManagedSelectionTypesEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ClearSelectionsAction
	extends AToolBarAction {

	public static final String TEXT = "Clear all selections";
	public static final String ICON = "resources/icons/view/storagebased/clear_selections.png";

	public ClearSelectionsAction() {
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		ClearSelectionsEvent event = new ClearSelectionsEvent();
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
		
		RemoveManagedSelectionTypesEvent resetSelectionTypesEvent = new RemoveManagedSelectionTypesEvent();
		resetSelectionTypesEvent.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(resetSelectionTypesEvent);
	};
}
