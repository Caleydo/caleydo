package org.caleydo.rcp.dialog.file;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * File dialog for exporting data files.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ExportDataDialog
	extends Dialog {

	private ExportDataAction exportDataAction;

	private ArrayList<Integer> genesToExport = null;
	private ArrayList<Integer> experimentsToExport = null;

	/**
	 * Constructor.
	 */
	public ExportDataDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Add data for group exports.
	 * 
	 * @param genesToExport
	 *            the list of genes to export
	 * @param experimentsToExport
	 *            the list of experiments to export
	 */
	public void addGroupData(ArrayList<Integer> genesToExport, ArrayList<Integer> experimentsToExport) {
		this.genesToExport = genesToExport;
		this.experimentsToExport = experimentsToExport;

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Export Data");
		newShell.setImage(GeneralManager.get().getResourceLoader().getImage(newShell.getDisplay(),
			"resources/icons/general/export_data.png"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		exportDataAction = new ExportDataAction(parent);
		exportDataAction.addGroupData(genesToExport, experimentsToExport);
		exportDataAction.run();
		return parent;
	}

	@Override
	protected void okPressed() {
		exportDataAction.execute();

		super.okPressed();
	}

}
