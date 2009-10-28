package org.caleydo.rcp.action.toolbar.view.remote.vislinks;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.caleydo.core.view.opengl.util.vislink.VisLinksAttributeManager;

public class ToggleVisLinkAnimationAction
	extends Action
	implements IToolBarItem {
	
	public static final String TEXT = "Animation On/Off";
	public static final String ICON = "resources/icons/view/remote/vislinks/vislink_animation.png";
	
	public ToggleVisLinkAnimationAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));	
	}
	
	@Override
	public void run() {
		super.run();

		VisLinksAttributeManager.toggleAnimation();
	}

}
