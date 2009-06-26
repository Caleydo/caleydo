package org.caleydo.rcp.action.toolbar.view.radial;

import org.caleydo.core.manager.event.view.radial.GoForthInHistoryEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class GoForthInHistoryAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Forth";
	public static final String ICON = "resources/icons/view/general/redo.png";


	public GoForthInHistoryAction(int viewID) {
		super(viewID);
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new GoForthInHistoryEvent());
		setChecked(false);
	};
}
