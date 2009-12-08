package org.caleydo.rcp.action.toolbar.view.hyperbolic;

import org.caleydo.core.manager.event.view.hyperbolic.ChangeCanvasDrawingEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeCanvasDrawingAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Change Tree Canvas";
	public static final String ICON = "resources/icons/view/hyperbolic/canvas_switch.png";

	public ChangeCanvasDrawingAction(int iViewID) {
		super(iViewID);
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		GeneralManager.get().getEventPublisher().triggerEvent(new ChangeCanvasDrawingEvent());
		setChecked(false);
	}
}
