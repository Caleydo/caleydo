package org.caleydo.rcp.action.toolbar.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class UseRandomSamplingAction
	extends AToolBarAction
	implements IToolBarItem {

	public boolean bFlag = true;
	public static final String TEXT = "Use random sampling";
	public static final String ICON = "resources/icons/view/storagebased/random_sampling.png";

	public UseRandomSamplingAction(int iViewID) {
		super(iViewID);

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
		triggerCmdExternalFlagSetter(bFlag, EExternalFlagSetterType.STORAGEBASED_USE_RANDOM_SAMPLING);
	};
}
