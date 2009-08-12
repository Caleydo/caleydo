package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.opengl.GLGlyphView;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenNewWindowAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Open new GlyphWindow";
	public static final String ICON = "resources/icons/view/glyph/glyph_new_window.png";

	public OpenNewWindowAction(int iViewID) {
		super(iViewID);

		setText("New");
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		try {
			String rcpid = GLGlyphView.ID;

			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(rcpid,
				rcpid + Integer.toString(GLGlyphView.viewCount), IWorkbenchPage.VIEW_CREATE);

		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

	};
}
