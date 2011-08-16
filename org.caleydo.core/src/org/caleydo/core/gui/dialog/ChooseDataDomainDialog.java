package org.caleydo.core.gui.dialog;

import java.util.Collection;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ChooseDataDomainDialog
	extends Dialog {

	private IDataDomain selectedDataDomain;
	private IDataDomain[] possibleDataDomains;
	
	public ChooseDataDomainDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	public ChooseDataDomainDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Choose data domain");
	}

	public void setPossibleDataDomains(Collection<IDataDomain> possibleDataDomains)
	{
		this.possibleDataDomains = (IDataDomain[])possibleDataDomains.toArray(new IDataDomain[possibleDataDomains.size()]);
	}
	
	public IDataDomain open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return selectedDataDomain;
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, false));

		final Combo comboDropDown = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.minimumWidth = 400;
		comboDropDown.setLayoutData(data);
		for (int index = 0; index < possibleDataDomains.length; index++) {
			IDataDomain possibleDataDomain = possibleDataDomains[index];
			comboDropDown.add(possibleDataDomain.getFileName(), index);	
		}

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				selectedDataDomain = possibleDataDomains[comboDropDown.getSelectionIndex()];
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}

	public static void main(String[] args) {
		ChooseDataDomainDialog dialog = new ChooseDataDomainDialog(new Shell());
		dialog.open();
	}
}
