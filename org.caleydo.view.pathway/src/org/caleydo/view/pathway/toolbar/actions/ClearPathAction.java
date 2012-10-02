/**
 * 
 */
package org.caleydo.view.pathway.toolbar.actions;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.pathway.event.ClearPathEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Button to clear a selected path in the pathway view.
 * 
 * @author Christian Partl
 *
 */
public class ClearPathAction extends Action implements IToolBarItem {
	public static final String TEXT = "Clear path";
	public static final String ICON = "resources/icons/view/pathway/clear_path.png";

	/**
	 * Constructor.
	 */
	public ClearPathAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		setChecked(false);
		GeneralManager.get().getEventPublisher()
				.triggerEvent(new ClearPathEvent());
	}
}
