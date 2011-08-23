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

		FileDialog fileDialog = new FileDialog(new Shell(), SWT.SAVE);
		fileDialog.setText("Save Project");
		String[] filterExt = { "*.cal" };
		fileDialog.setFilterExtensions(filterExt);

		String filePath =
			"caleydo_project" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".cal";

		fileDialog.setFileName(filePath);
		String fileName = fileDialog.open();

		if (fileName == null)
			return;
		
		ProjectSaver save = new ProjectSaver();
		save.save(fileName);
	}
}
