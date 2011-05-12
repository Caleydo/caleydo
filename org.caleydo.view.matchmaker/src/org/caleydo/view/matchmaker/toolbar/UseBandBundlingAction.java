package org.caleydo.view.matchmaker.toolbar;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.matchmaker.event.UseBandBundlingEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * @author Marc Streit
 * 
 */
public class UseBandBundlingAction extends AToolBarAction implements IToolBarItem {
	public static final String TEXT = "Use ribbons";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/reset_axis_spacing.png";

	private boolean renderBands = true;

	/**
	 * Constructor.
	 */
	public UseBandBundlingAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		super.setChecked(renderBands);
	}

	public void setBandBundling(boolean renderBands) {
		this.renderBands = renderBands;
	}

	@Override
	public void run() {
		super.run();
		if (renderBands)
			renderBands = false;
		else
			renderBands = true;

		super.setChecked(renderBands);
		GeneralManager.get().getEventPublisher()
				.triggerEvent(new UseBandBundlingEvent(renderBands));
	};
}
