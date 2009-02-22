package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.file.FileLoadDataAction;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class LoadDataAction
	extends AToolBarAction
{
	public static final String TEXT = "Load data";
	public static final String ICON = "resources/icons/general/load_data.png";

	/**
	 * Constructor.
	 */
	public LoadDataAction()
	{
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run()
	{
		super.run();

		new FileLoadDataAction(null).run();
	}
}
