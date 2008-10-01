package org.caleydo.rcp.action.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;

public class RenderContextAction
	extends AToolBarAction
{
	public static final String TEXT = "Render Context / Render All";
	public static final String ICON = "resources/icons/view/storagebased/toggle_render_context.png";

	boolean bEnable;

	/**
	 * Constructor.
	 */
	public RenderContextAction(int iViewID)
	{
		super(iViewID);
		AStorageBasedView storageBasedView = (AStorageBasedView) GeneralManager.get()
				.getViewGLCanvasManager().getGLEventListener(iViewID);
		bEnable = storageBasedView.isRenderingOnlyContext();
	
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource(ICON)));

	

		setChecked(bEnable);
//		setEnabled(bEnable);
	}

	@Override
	public void run()
	{
		super.run();
		bEnable = !bEnable;
		triggerCmdExternalFlagSetter(bEnable,
				EExternalFlagSetterType.STORAGEBASED_RENDER_CONTEXT);
	};
}
