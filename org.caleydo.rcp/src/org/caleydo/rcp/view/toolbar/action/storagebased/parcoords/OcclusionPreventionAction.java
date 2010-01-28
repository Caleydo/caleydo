package org.caleydo.rcp.view.toolbar.action.storagebased.parcoords;

import org.caleydo.core.manager.event.view.storagebased.PreventOcclusionEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class OcclusionPreventionAction extends AToolBarAction
		implements
			IToolBarItem {
	public static final String TEXT = "Toggle occlusion prevention";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/occlusion_prevention.png";

	private boolean bEnable = true;

	/**
	 * Constructor.
	 */
	public OcclusionPreventionAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(bEnable);
	}

	@Override
	public void run() {
		super.run();

		bEnable = !bEnable;

		GeneralManager.get().getEventPublisher().triggerEvent(
				new PreventOcclusionEvent(bEnable));
	};
}
