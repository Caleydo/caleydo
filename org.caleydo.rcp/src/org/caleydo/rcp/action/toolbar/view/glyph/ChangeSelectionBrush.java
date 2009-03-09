package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.core.command.view.rcp.EExternalObjectSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeSelectionBrush
	extends AToolBarAction {

	private Integer iBrushSize = 0;

	private String sText;
	private ImageDescriptor idIcon;

	private ChangeSelectionBrushAction parent;

	/**
	 * Constructor.
	 */
	public ChangeSelectionBrush(int iViewID, ChangeSelectionBrushAction parent, int iBrushSize, String text,
		String iconresource) {
		super(iViewID);
		this.parent = parent;

		this.iBrushSize = iBrushSize;

		this.sText = text;
		this.idIcon =
			ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI.getWorkbench().getDisplay(),
				iconresource));

		setText(text);
		setToolTipText(sText);
		setImageDescriptor(idIcon);

	}

	@Override
	public void run() {
		super.run();

		if (parent != null)
			parent.setImageDescriptor(idIcon);

		triggerCmdExternalObjectSetter(iBrushSize, EExternalObjectSetterType.GLYPH_SELECTIONBRUSH);
	};
}
