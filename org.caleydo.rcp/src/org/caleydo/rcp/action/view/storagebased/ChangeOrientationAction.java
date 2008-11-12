package org.caleydo.rcp.action.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeOrientationAction
	extends AToolBarAction
{
	public static final String TEXT = "Switch dimensions";
	public static final String ICON = "resources/icons/view/storagebased/change_orientation.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public ChangeOrientationAction(int iViewID)
	{
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		bEnable = ((AStorageBasedView) GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID)).isInDefaultOrientation();
		setChecked(bEnable);
	}

	@Override
	public void run()
	{
		super.run();
		bEnable = !bEnable;
		triggerCmdExternalFlagSetter(bEnable,
				EExternalFlagSetterType.STORAGEBASED_CHANGE_ORIENTATION);
	};
}
