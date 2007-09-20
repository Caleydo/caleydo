/**
 * 
 */
package org.geneview.rcp.dialog.file;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

import org.geneview.rcp.Application;

/**
 * copy of RCP tutorial "org.eclipsercp.hyperbola.AddContactDialog.java"
 * 
 * @author Michael Kalkusch
 *
 */
public class OpenXmlConfigFileDialog extends Dialog {

	private Text userIdText;

	private Text serverText;

	private Text nicknameText;

	private String userId;

	private String server;

	private String nickname;
	
	/**
	 * @param parentShell
	 */
	public OpenXmlConfigFileDialog(Shell parentShell) {
		super(parentShell);
	}

//	/**
//	 * @param parentShell
//	 */
//	public OpenXmlConfigFileDialog(IShellProvider parentShell) {
//		super(parentShell);
//		// TODO Auto-generated constructor stub
//	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Open XML config File");
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Label userIdLabel = new Label(composite, SWT.NONE);
		userIdLabel.setText("&User id:");
		userIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		userIdText = new Text(composite, SWT.BORDER);
		userIdText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("&Server:");
		serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		serverText = new Text(composite, SWT.BORDER);
		serverText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label nicknameLabel = new Label(composite, SWT.NONE);
		nicknameLabel.setText("&Nickname:");
		nicknameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		nicknameText = new Text(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				false);
		gridData.widthHint = convertHeightInCharsToPixels(20);
		nicknameText.setLayoutData(gridData);

		 FileDialog fd = new FileDialog(parent.getShell());
	        fd.setText("Open");
	        fd.setFilterPath("C:/");
	        String[] filterExt = { "*.xml" };
	        fd.setFilterExtensions(filterExt);
	        String selected = fd.open();
	        System.out.println(selected);
	        
	    Application.geneview_core.run_parseXmlConfigFile(selected);
	    
		return composite;
	}

	protected void okPressed() {
		nickname = nicknameText.getText();
		server = serverText.getText();
		userId = userIdText.getText();

		if (nickname.equals("")) {
			MessageDialog.openError(getShell(), "Invalid Nickname",
					"Nickname field must not be blank.");
			return;
		}
		if (server.equals("")) {
			MessageDialog.openError(getShell(), "Invalid Server",
					"Server field must not be blank.");
			return;
		}
		if (userId.equals("")) {
			MessageDialog.openError(getShell(), "Invalid User id",
					"User id field must not be blank.");
			return;
		}

		super.okPressed();
	}

	public String getUserId() {
		return userId;
	}

	public String getServer() {
		return server;
	}

	public String getNickname() {
		return nickname;
	}
	
	public String getServerText() {
		return serverText.getText();
	}
}
