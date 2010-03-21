package org.caleydo.view.compare.toolbar;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.caleydo.view.compare.event.UseZoomEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * @author Alexander Lex
 * 
 */
public class UseZoomAction extends AToolBarAction implements IToolBarItem {
	public static final String TEXT = "Use heat map zoom";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/angular_brush.png";

	private boolean useZoom = true;

	/**
	 * Constructor.
	 */
	public UseZoomAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
		super.setChecked(useZoom);
	}

	public void setUseZoom(boolean useZoom) {
		this.useZoom = useZoom;
	}

	@Override
	public void run() {
		super.run();
		if (useZoom)
			useZoom = false;
		else
			useZoom = true;

		GeneralManager.get().getEventPublisher().triggerEvent(
				new UseZoomEvent(useZoom));
	};
}
