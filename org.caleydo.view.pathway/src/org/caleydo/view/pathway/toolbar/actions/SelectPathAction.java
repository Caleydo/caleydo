/**
 * 
 */
package org.caleydo.view.pathway.toolbar.actions;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.pathway.event.SelectPathModeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Button for toggling path selection.
 * 
 * @author Christian Partl
 * 
 */
public class SelectPathAction
	extends Action
	implements IToolBarItem {
	public static final String TEXT = "Toggle path selection (Ctrl + O)";
	public static final String ICON = "resources/icons/view/pathway/path_selection.png";

	/**
	 * Constructor.
	 */
	public SelectPathAction(boolean isChecked) {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI.getWorkbench()
				.getDisplay(), ICON)));
		setChecked(isChecked);
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new SelectPathModeEvent(isChecked()));
	}

}
