package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.core.manager.event.view.glyph.RemoveUnselectedGlyphsEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class RemoveUnselectedFromViewAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "remove unselected glyphs from view";
	public static final String ICON = "resources/icons/view/glyph/glyph_remove_unselected.png";

	public RemoveUnselectedFromViewAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new RemoveUnselectedGlyphsEvent());
	};
}
