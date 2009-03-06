package org.caleydo.rcp.dialog.file;

import org.caleydo.rcp.action.file.FileLoadDataAction;
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
public class LoadDataDialog
	extends Dialog
{
	 private FileLoadDataAction fileLoadDataAction;

	/**
	 * Constructor.
	 */
	public LoadDataDialog(Shell parentShell)
	{
		super(parentShell);
		parentShell.setText("Open project file");
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("Open Text Data File");
//		newShell.setMaximized(true);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		fileLoadDataAction = new FileLoadDataAction(parent);
		fileLoadDataAction.run();

		return parent;
	}

	@Override
	protected void okPressed()
	{
		fileLoadDataAction.execute();

		super.okPressed();
	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		LoadDataDialog dialog = new LoadDataDialog(new Shell());
		dialog.open();
	}
}
