package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.dialog.file.ExportClinicalDataDialog;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class OpenDataExportAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Open Glyph Data Export Tool";
	public static final String ICON = "resources/icons/view/glyph/glyph_generate_report.png";

	public OpenDataExportAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		ExportClinicalDataDialog dialog = new ExportClinicalDataDialog(new Shell(), iViewID);
		dialog.open();
		// try
		// {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
		// SWTGlyphDataExportView.ID, SWTGlyphDataExportView.ID,
		// IWorkbenchPage.VIEW_CREATE);
		// }
		// catch (PartInitException e)
		// {
		// e.printStackTrace();
		// }

	};
}
