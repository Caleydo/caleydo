package org.caleydo.rcp.action.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;

public class UseRandomSamplingAction
	extends AToolBarAction
{

	public boolean bFlag = true;
	public static final String TEXT = "Use random sampling";
	public static final String ICON = "resources/icons/view/storagebased/propagate_selection.png";

	public UseRandomSamplingAction(int iViewID)
	{
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource(ICON)));
	}

	@Override
	public void run()
	{
		super.run();
		bFlag = !bFlag;
		triggerCmdExternalFlagSetter(bFlag,
				EExternalFlagSetterType.STORAGEBASED_USE_RANDOM_SAMPLING);
	};
}
