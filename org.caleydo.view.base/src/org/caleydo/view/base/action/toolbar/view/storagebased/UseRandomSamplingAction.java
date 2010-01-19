package org.caleydo.view.base.action.toolbar.view.storagebased;

import org.caleydo.core.manager.event.view.storagebased.UseRandomSamplingEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.base.action.toolbar.AToolBarAction;
import org.caleydo.view.base.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class UseRandomSamplingAction extends AToolBarAction
		implements
			IToolBarItem {

	public boolean bFlag = true;
	public static final String TEXT = "Use random sampling";
	public static final String ICON = "resources/icons/view/storagebased/random_sampling.png";

	public UseRandomSamplingAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(bFlag);
	}

	@Override
	public void run() {
		super.run();
		bFlag = !bFlag;
		GeneralManager.get().getEventPublisher().triggerEvent(
				new UseRandomSamplingEvent(bFlag));
	};
}
