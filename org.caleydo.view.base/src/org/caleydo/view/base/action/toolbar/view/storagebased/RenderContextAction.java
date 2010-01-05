package org.caleydo.view.base.action.toolbar.view.storagebased;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.base.action.toolbar.AToolBarAction;
import org.caleydo.view.base.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class RenderContextAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Render Context / Render All";
	public static final String ICON = "resources/icons/view/storagebased/toggle_render_context.png";

	boolean bEnable;

	/**
	 * Constructor.
	 */
	public RenderContextAction(int iViewID) {
		super(iViewID);
		AStorageBasedView storageBasedView =
			(AStorageBasedView) GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);
		bEnable = storageBasedView.isRenderingOnlyContext();

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));

		setChecked(bEnable);
		// setEnabled(bEnable);
	}

	@Override
	public void run() {
		super.run();
		bEnable = !bEnable;
		// triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.STORAGEBASED_RENDER_CONTEXT);
	};
}
