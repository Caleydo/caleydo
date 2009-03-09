package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.core.command.view.rcp.EExternalObjectSetterType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.util.glyph.TextInputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class EnterViewNameAction
	extends AToolBarAction
{

	public static final String TEXT = "Enter a name for this view";
	public static final String ICON = "resources/icons/view/glyph/glyph_rename.png";

	public EnterViewNameAction(int iViewID)
	{
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run()
	{
		super.run();

		GLGlyph glyphview = null;

		for (AGLEventListener view : GeneralManager.get().getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (view instanceof GLGlyph)
			{
				if (view.getID() == iViewID)
					glyphview = (GLGlyph) view;
			}
		}

		if (glyphview == null)
		{
			throw new IllegalStateException(
					"Clinical Data Export in Toolbar wants to export a view witch doesn't exist");
		}
		
		String oldname = glyphview.getPersonalName();

		Shell shell = new Shell();
		TextInputDialog dialog = new TextInputDialog(shell, oldname);
		String newname = dialog.open();

		if (newname != null)
			triggerCmdExternalObjectSetter(newname,
					EExternalObjectSetterType.GLYPH_CHANGEPERSONALNAME);

	};
}
