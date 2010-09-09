package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.dialog.file.LoadDataDialog;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ImportDataAction
	extends AToolBarAction {
	public static final String TEXT = "Load data";
	public static final String ICON = "resources/icons/general/load_data.png";

	/**
	 * Constructor.
	 */
	public ImportDataAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		LoadDataDialog dialog = new LoadDataDialog(new Shell());
		dialog.open();
	}
}
