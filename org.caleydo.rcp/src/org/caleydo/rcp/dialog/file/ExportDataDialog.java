package org.caleydo.rcp.dialog.file;

import org.caleydo.core.manager.general.GeneralManager;
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

	/**
	 * Constructor.
	 */
	public ExportDataDialog(Shell parentShell) {
		super(parentShell);
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
		exportDataAction.run();
		return parent;
	}

	@Override
	protected void okPressed() {
		exportDataAction.execute();

		super.okPressed();
	}

}
