package org.caleydo.rcp.view.toolbar.action.storagebased;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.storagebased.BookmarkButtonEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class PropagateSelectionsAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Bookmark current selection";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/bookmark.png";

	/**
	 * Constructor.
	 */
	public PropagateSelectionsAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new BookmarkButtonEvent());
	};
}
