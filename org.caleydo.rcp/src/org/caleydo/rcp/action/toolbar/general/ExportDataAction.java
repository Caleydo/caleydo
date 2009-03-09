package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.dialog.file.ExportDataDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ExportDataAction
	extends AToolBarAction {
	public static final String TEXT = "Export data";
	public static final String ICON = "resources/icons/general/export_data.png";

	/**
	 * Constructor.
	 */
	public ExportDataAction() {
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		ExportDataDialog dialog = new ExportDataDialog(new Shell());
		dialog.open();
	}
}
