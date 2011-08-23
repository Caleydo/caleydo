package org.caleydo.core.data.configuration;

import org.caleydo.core.io.gui.IDataOKListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ChooseDataConfigurationDialog
	extends Dialog
	implements IDataOKListener {

	private DataConfiguration dataConfiguration;

	private DataChooserComposite dataChooserComposite;

	public ChooseDataConfigurationDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	public ChooseDataConfigurationDialog(Shell parent, int style) {
		super(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		createGUI(parent);
		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		if (!dataChooserComposite.isOK())
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		return control;
	}

	private void createGUI(Composite shell) {
		shell.setLayout(new GridLayout(1, false));
		dataChooserComposite = new DataChooserComposite(this, shell, SWT.BORDER);
		dataChooserComposite.initGui();
	}

	@Override
	protected void okPressed() {
		super.okPressed();
		dataConfiguration = dataChooserComposite.getDataConfiguration();
	}

	/**
	 * @return the dataConfiguration, see {@link #dataConfiguration}
	 */
	public DataConfiguration getDataConfiguration() {
		return dataConfiguration;
	}

	public static void main(String[] args) {
		ChooseDataConfigurationDialog dialog = new ChooseDataConfigurationDialog(new Shell());
		dialog.open();
	}

	@Override
	public void dataOK() {
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
}
