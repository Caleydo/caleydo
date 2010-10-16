package org.caleydo.view.treemap.actions;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.treemap.ToggleColoringModeEvent;
import org.caleydo.core.manager.event.view.treemap.ToggleLabelEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Action for toggling coloring mode.
 * @author Michael Lafer
 *
 */

public class ToggleColoringModeAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Toggle ColorMode Average/Selected";
	public static final String ICON = "resources/icons/view/storagebased/clustering.png";
	
	public ToggleColoringModeAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}
	
	@Override
	public void run() {
		super.run();
		ToggleColoringModeEvent event = new ToggleColoringModeEvent();
		event.setCalculateColor(isChecked());
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	};


}