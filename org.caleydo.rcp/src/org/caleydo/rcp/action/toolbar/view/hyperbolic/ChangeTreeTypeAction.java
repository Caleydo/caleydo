package org.caleydo.rcp.action.toolbar.view.hyperbolic;

import org.caleydo.core.manager.event.view.hyperbolic.ChangeTreeTypeEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

// TODO: delete! or rebuild it to switch globeprojection type

public class ChangeTreeTypeAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Change Tree Type";
	public static final String[] ICON =
		{ "resources/icons/view/hyperbolic/tree_switch_hyp.png",
				"resources/icons/view/hyperbolic/tree_switch_lin.png" };

	public ChangeTreeTypeAction(int iViewID) {
		super(iViewID);
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON[0])));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		if (isChecked()) {
			// setChecked(true);
			setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
				.getWorkbench().getDisplay(), ICON[1])));
		}
		else {
			// setChecked(false);
			setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
				.getWorkbench().getDisplay(), ICON[0])));
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ChangeTreeTypeEvent());
	}

}
