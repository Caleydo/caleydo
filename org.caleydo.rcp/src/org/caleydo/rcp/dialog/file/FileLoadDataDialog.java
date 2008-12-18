package org.caleydo.rcp.dialog.file;

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
public class FileLoadDataDialog
	extends Dialog
{

	// private FileLoadDataAction fileLoadDataAction;

	/**
	 * Constructor.
	 */
	public FileLoadDataDialog(Shell parentShell)
	{
		super(parentShell);

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
		newShell.setText("Open Text Data File");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		// fileLoadDataAction = new FileLoadDataAction(parent);
		// fileLoadDataAction.run();

		return parent;
	}

	@Override
	protected void okPressed()
	{
		// fileLoadDataAction.execute();

		super.okPressed();
	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{

		FileLoadDataDialog dialog = new FileLoadDataDialog(new Shell());
		dialog.open();
	}
}
