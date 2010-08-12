package org.caleydo.rcp.view.toolbar.action.glyph;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.util.glyph.TextInputDialog;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class EnterViewNameAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Enter a name for this view";
	public static final String ICON = "resources/icons/view/glyph/glyph_rename.png";

	public EnterViewNameAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		GLGlyph glyphview = null;

		for (AGLView view : GeneralManager.get().getViewGLCanvasManager().getAllGLViews()) {
			if (view instanceof GLGlyph) {
				if (view.getID() == iViewID) {
					glyphview = (GLGlyph) view;
				}
			}
		}

		if (glyphview == null)
			throw new IllegalStateException(
				"Clinical Data Export in Toolbar wants to export a view witch doesn't exist");

		String oldname = glyphview.getPersonalName();

		Shell shell = new Shell();
		TextInputDialog dialog = new TextInputDialog(shell, oldname);
		String newname = dialog.open();

		if (newname != null) {
			GeneralManager.get().getEventPublisher().triggerEvent(
				new GlyphChangePersonalNameEvent(iViewID, newname));

		}
	};
}
