package org.caleydo.core.gui.toolbar.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SaveProjectAction
	extends AToolBarAction {
	public static final String TEXT = "Save Project";
	public static final String ICON = "resources/icons/general/save.png";

	/**
	 * Constructor.
	 */
	public SaveProjectAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		// ExportDataDialog dialog = new ExportDataDialog(new Shell());
		// dialog.open();

		FileDialog fileDialog = new FileDialog(new Shell(), SWT.SAVE);
		fileDialog.setText("Save Project");
		// fileDialog.setFilterPath(sFilePath);
		String[] filterExt = { "*.cal" };
		fileDialog.setFilterExtensions(filterExt);

		String sFilePath =
			"caleydo_project" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".cal";

		fileDialog.setFileName(sFilePath);
		String sFileName = fileDialog.open();

		ProjectSaver save = new ProjectSaver();
		save.save(sFileName);
		// txtFileName.setText(sFileName);
	}
}
