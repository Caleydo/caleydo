package org.caleydo.rcp.action.toolbar.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class MergeClasses
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Merge classes";
	public static final String ICON = "resources/icons/view/storagebased/merge_classes.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public MergeClasses(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(bEnable);
	}

	@Override
	public void run() {
		super.run();
		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.STORAGEBASED_MERGE_CLASSES);
	};
}