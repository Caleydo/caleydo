package org.caleydo.view.matchmaker.toolbar;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.matchmaker.event.UseZoomEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * @author Alexander Lex
 * 
 */
public class UseZoomAction extends AToolBarAction implements IToolBarItem {
	public static final String TEXT = "Use heat map zoom";
	public static final String ICON = "resources/icons/general/search.png";

	private boolean useZoom = true;

	/**
	 * Constructor.
	 */
	public UseZoomAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
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

		super.setChecked(useZoom);
		GeneralManager.get().getEventPublisher().triggerEvent(new UseZoomEvent(useZoom));
	};
}
