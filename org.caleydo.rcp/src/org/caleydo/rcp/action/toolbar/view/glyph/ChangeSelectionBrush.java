package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.core.command.view.rcp.EExternalObjectSetterType;
import org.caleydo.rcp.action.toolbar.AToolBarAction;

public class ChangeSelectionBrush
	extends AToolBarAction
{
	public static final String TEXT = "Change Selection Brush";
	public static final String ICON = "resources/icons/view/glyph/sort_random.png";

	private Integer iBrushSize = 0;

	/**
	 * Constructor.
	 */
	public ChangeSelectionBrush(int iViewID, int iBrushSize)
	{
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		// setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
		// .getResource(ICON)));

		this.iBrushSize = iBrushSize;
	}

	@Override
	public void run()
	{
		super.run();

		triggerCmdExternalObjectSetter(iBrushSize,
				EExternalObjectSetterType.GLYPH_SELECTIONBRUSH);
	};
}
