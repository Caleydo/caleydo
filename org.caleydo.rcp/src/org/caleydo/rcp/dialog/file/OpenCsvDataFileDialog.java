package org.caleydo.rcp.dialog.file;

import org.eclipse.jface.dialogs.Dialog;
//import org.eclipse.jface.window.IShellProvider;
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

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.data.parser.CmdLoadFileViaImporter;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.rcp.Application;

/**
 * copy of RCP tutorial "org.eclipsercp.hyperbola.AddContactDialog.java"
 * 
 * @author Michael Kalkusch
 *
 */
public class OpenCsvDataFileDialog extends Dialog {

	private Text csvFileNameText;

	private Text inputPatternText;

	private Text targetSetIdText;

	private String csvFileName;

	private String inputPattern = "SKIP;INT;ABORT";

	private int targetSetId = 35101;
	
	protected String cvsPath = "D:/src/java/ICG/cerberus/org.caleydo.data/data/genome/microarray/gpr_format";
	
	/**
	 * Constructor.
	 */
	public OpenCsvDataFileDialog(Shell parentShell) {
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
		userIdLabel.setText("cvs &Fiel name:");
		userIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		csvFileNameText = new Text(composite, SWT.BORDER);
		csvFileNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("&Input pattern:");
		serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		inputPatternText = new Text(composite, SWT.BORDER);
		inputPatternText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		/* write back default value to GUI */
		inputPatternText.setText(inputPattern);

		Label nicknameLabel = new Label(composite, SWT.NONE);
		nicknameLabel.setText("target&SetId:");
		nicknameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		targetSetIdText = new Text(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				false);
		gridData.widthHint = convertHeightInCharsToPixels(20);
		targetSetIdText.setLayoutData(gridData);
		/* write back default value to GUI */
		targetSetIdText.setText( Integer.toString(targetSetId) );

		 FileDialog fd = new FileDialog(parent.getShell());
	        fd.setText("Open");
	        fd.setFilterPath( cvsPath );
	        String[] filterExt = { "*.gpr","*.csv","*.*" };
	        fd.setFilterExtensions(filterExt);
	        csvFileName = fd.open();
	        String sBufferPath = fd.getFilterPath();
	        
	        if  (sBufferPath != "") {
	        	cvsPath = sBufferPath;
	        }
	        
	        /* file name to GUI.. */
	        csvFileNameText.setText(csvFileName);
	        
		return composite;
	}

	protected void okPressed() {
				
		try {
			targetSetId = Integer.valueOf(targetSetIdText.getText());
		} catch (NumberFormatException nfe) {
			MessageDialog.openError(getShell(), "Invalid targetSetId",
				" targetSetId must be an integer.");
			
			/* write back last valid id to GUI */
			targetSetIdText.setText( Integer.toString(targetSetId) );
		}
		
		inputPattern = inputPatternText.getText();
		csvFileName = csvFileNameText.getText();
		
		if (inputPattern.equals("")) {
			MessageDialog.openError(getShell(), "Invalid InputPatern",
					"Patern ust be:  [INT|FLOAT|STRING] [[;]*]");
			return;
		}
		if (csvFileName.equals("")) {
			MessageDialog.openError(getShell(), "Invalid filename",
					"invalid file name");
			return;
		}

		CmdLoadFileViaImporter cmdLoadCsv = (CmdLoadFileViaImporter) 
			Application.caleydo_core.getGeneralManager().getCommandManager().createCommandByType(
					CommandQueueSaxType.LOAD_DATA_FILE);
		
		ISWTGUIManager refISWTGUIManager= Application.caleydo_core.getGeneralManager().getSWTGUIManager();
		refISWTGUIManager.setProgressbarVisible(true);
		
		cmdLoadCsv.setAttributes(csvFileName, 
				inputPattern, 				
				32,
				-1,
				targetSetId);
		
		try {
			cmdLoadCsv.doCommand();
		} catch (CaleydoRuntimeException e) {
			if ( e.getType().equals(CaleydoRuntimeExceptionType.SET)) {
				MessageDialog.openError(getShell(), "Invalid SetId",
				"targetSetId is invalid");
				return;
			}
		}
		finally 
		{
			refISWTGUIManager.setProgressbarVisible(false);
		}
		
		super.okPressed();
	}

}
