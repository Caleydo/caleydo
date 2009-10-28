package org.caleydo.rcp.action.toolbar.view.remote.vislinks;

import org.caleydo.core.view.opengl.util.vislink.VisLinksAttributeManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleVisLinkColor
	extends Action
	implements IToolBarItem {
	
		public static final String TEXT = "Switch between colors";
		public static final String ICON = "resources/icons/view/remote/vislinks/vislink_color.png";
		
		public ToggleVisLinkColor() {
			setText(TEXT);
			setToolTipText(TEXT);
			setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
				.getWorkbench().getDisplay(), ICON)));	
		}
		
		@Override
		public void run() {
			super.run();
		
			VisLinksAttributeManager.switchConnectionLineColor();
		}
}
