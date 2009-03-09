package org.caleydo.rcp.dialog.file;

import org.caleydo.rcp.action.file.ExportClinicalDataAction;
import org.caleydo.rcp.action.file.ExportDataAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * File dialog for opening raw text data files.
 * 
 * @author Marc Streit
 */
public class ExportClinicalDataDialog
	extends Dialog
{

	private ExportClinicalDataAction exportDataAction;
	private int iViewID;

	/**
	 * Constructor.
	 */
	public ExportClinicalDataDialog(Shell parentShell, int iViewID)
	{
		super(parentShell);
		this.iViewID = iViewID;

		parentShell.setText("Open project file");
		Monitor primary = parentShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = parentShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		parentShell.setLocation(x, y);
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("Export Data");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		exportDataAction = new ExportClinicalDataAction(parent, iViewID);
		exportDataAction.run();
		return parent;
	}

	@Override
	protected void okPressed()
	{
		exportDataAction.execute();

		super.okPressed();
	}

}
