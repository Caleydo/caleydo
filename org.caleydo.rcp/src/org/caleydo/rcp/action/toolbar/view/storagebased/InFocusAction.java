package org.caleydo.rcp.action.toolbar.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class InFocusAction
	extends AToolBarAction {
	public static final String TEXT = "Switch Focus";
	// TODO: own icon for "Switch Focus"
	public static final String ICON = "resources/icons/view/storagebased/change_orientation.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public InFocusAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		bEnable =
			((GLHierarchicalHeatMap) GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID))
				.isInFocus();
		setChecked(bEnable);
	}

	@Override
	public void run() {
		super.run();
		bEnable = !bEnable;
		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.STORAGEBASED_HEATMAP_IN_FOCUS);
	};
}
