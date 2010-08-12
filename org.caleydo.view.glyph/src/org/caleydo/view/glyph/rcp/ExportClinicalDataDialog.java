package org.caleydo.rcp.dialog.file;

import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * File dialog for opening raw text data files.
 * 
 * @author Marc Streit
 */
public class ExportClinicalDataDialog
	extends Dialog {

	private ExportClinicalDataAction exportDataAction;
	private int iViewID;

	/**
	 * Constructor.
	 */
	public ExportClinicalDataDialog(Shell parentShell, int iViewID) {
		super(parentShell);
		this.iViewID = iViewID;
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
		exportDataAction = new ExportClinicalDataAction(parent, iViewID);
		exportDataAction.run();
		return parent;
	}

	@Override
	protected void okPressed() {
		exportDataAction.execute();

		super.okPressed();
	}

}
