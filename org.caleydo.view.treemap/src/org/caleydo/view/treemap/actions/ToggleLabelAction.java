package org.caleydo.view.treemap.actions;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.treemap.ToggleLabelEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Action for switching labels on/off.
 * @author Michael Lafer
 *
 */

public class ToggleLabelAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Toggle Labels";
	public static final String ICON = "resources/icons/view/hyperbolic/tree_switch_lin.png";
	
	public ToggleLabelAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(true);
	}
	
	@Override
	public void run() {
		super.run();
		//System.out.println("label: "+isChecked());
		ToggleLabelEvent event=new ToggleLabelEvent();
		event.setDrawLabel(isChecked());
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	};


}
