package org.caleydo.view.base.action.toolbar.view.radial;

import org.caleydo.core.manager.event.view.radial.ChangeColorModeEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.base.action.toolbar.AToolBarAction;
import org.caleydo.view.base.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeColorModeAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Change Color Mode";
	public static final String ICON = "resources/icons/view/radial/radial_color_mapping.png";

	public ChangeColorModeAction(int viewID) {
		super(viewID);
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ChangeColorModeEvent());
		setChecked(false);
	};

}
