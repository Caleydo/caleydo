package org.caleydo.rcp.dialog.file;

import org.caleydo.rcp.action.file.FileLoadDataAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * File dialog for opening raw text data files.
 * 
 * @author Marc Streit
 *
 */
public class FileLoadDataDialog 
extends Dialog {
	
	/**
	 * Constructor.
	 */
	public FileLoadDataDialog(Shell parentShell) {
		super(parentShell);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Open Text Data File");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		FileLoadDataAction fileLoadDataAction = new FileLoadDataAction(parent);
        fileLoadDataAction.run();
		
		return parent;
	}
	
	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		  
		FileLoadDataDialog dialog = new FileLoadDataDialog(new Shell());
		dialog.open();
	}
}
