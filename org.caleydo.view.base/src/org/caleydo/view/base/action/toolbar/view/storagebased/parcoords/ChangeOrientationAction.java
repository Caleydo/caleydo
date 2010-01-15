package org.caleydo.view.base.action.toolbar.view.storagebased.parcoords;

import org.caleydo.core.manager.event.view.storagebased.ChangeOrientationParallelCoordinatesEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.base.action.toolbar.AToolBarAction;
import org.caleydo.view.base.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeOrientationAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Switch dimensions";
	public static final String ICON = "resources/icons/view/storagebased/change_orientation.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public ChangeOrientationAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		bEnable =
			((AStorageBasedView) GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID))
				.isInDefaultOrientation();
		setChecked(bEnable);
	}

	@Override
	public void run() {
		super.run();
		bEnable = !bEnable;
		GeneralManager.get().getEventPublisher().triggerEvent(
			new ChangeOrientationParallelCoordinatesEvent(bEnable));
	};
}
