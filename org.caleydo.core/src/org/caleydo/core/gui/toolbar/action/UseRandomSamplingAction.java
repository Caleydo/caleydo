package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.dimensionbased.UseRandomSamplingEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class UseRandomSamplingAction
	extends AToolBarAction
	implements IToolBarItem {

	public boolean bFlag = true;
	public static final String TEXT = "Use random sampling";
	public static final String ICON = "resources/icons/view/dimensionbased/random_sampling.png";

	public UseRandomSamplingAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(bFlag);
	}

	@Override
	public void run() {
		super.run();
		bFlag = !bFlag;
		GeneralManager.get().getEventPublisher().triggerEvent(new UseRandomSamplingEvent(bFlag));
	};
}
