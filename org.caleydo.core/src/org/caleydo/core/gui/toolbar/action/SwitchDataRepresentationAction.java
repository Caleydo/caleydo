package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.SwitchDataRepresentationEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class SwitchDataRepresentationAction
	extends AToolBarAction {

	public static final String TEXT = "Switch Data Representation (Fold change, normal)";
	public static final String ICON = "resources/icons/view/storagebased/clear_selections.png";

	public SwitchDataRepresentationAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		SwitchDataRepresentationEvent event = new SwitchDataRepresentationEvent();
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	};
}
