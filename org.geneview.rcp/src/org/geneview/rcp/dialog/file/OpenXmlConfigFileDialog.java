/**
 * 
 */
package org.geneview.rcp.dialog.file;

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

import org.geneview.core.manager.ISWTGUIManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.rcp.Application;

/**
 * copy of RCP tutorial "org.eclipsercp.hyperbola.AddContactDialog.java"
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class OpenXmlConfigFileDialog 
extends Dialog {

	private Shell parentShell;
	
	private Text xmlFileNameText;
	
	private Text statusOnLoading;
	
	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 */
	public OpenXmlConfigFileDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Open XML config File");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Label userIdLabel = new Label(composite, SWT.NONE);
		userIdLabel.setText("&Xml file name:");
		userIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		xmlFileNameText = new Text(composite, SWT.BORDER);
		xmlFileNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		
		Label statusIdLabel = new Label(composite, SWT.NONE);
		statusIdLabel.setText("loading status:");
		statusIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));
	
		statusOnLoading = new Text(composite, SWT.BORDER);
		xmlFileNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		statusOnLoading.setEditable(false);
		statusOnLoading.setText("not loaded");
		
		if  (parentShell==null)
		{
			parentShell = parent.getShell();
		}
		
		FileDialog fd = new FileDialog(parentShell);
        fd.setText("Open");
        fd.setFilterPath("D:/src/java/ICG/cerberus/org.geneview.data/data/bootstrap");
        String[] filterExt = { "*.xml" };
        fd.setFilterExtensions(filterExt);
        String result = fd.open();
        
        if (result == null)
        	xmlFileNameText.setText("");
        else
        	xmlFileNameText.setText(result);
	    
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		
		String xmlFileName = xmlFileNameText.getText();

		if (xmlFileName.equals("")) 
		{
			MessageDialog.openError(getShell(), "Invalid Xml file name",
					"xml file name must not be blank.");
			
			FileDialog fd = new FileDialog(parentShell);
	        fd.setText("Open");
	        fd.setFilterPath("D:/src/java/ICG/cerberus/org.geneview.data/data/bootstrap");
	        String[] filterExt = { "*.xml" };
	        fd.setFilterExtensions(filterExt);
	        xmlFileName = fd.open();
		        
			return;
		}
		
		ISWTGUIManager refISWTGUIManager = 
			Application.geneview_core.getGeneralManager().getSingelton().getSWTGUIManager();
		refISWTGUIManager.setProgressbarVisible(true);
				
		try 
		{		
			Application.geneview_core.setXmlFileName(xmlFileName);
			super.okPressed();			
		} 
		catch (Exception e) 
		{
			Application.geneview_core.getGeneralManager().getSingelton().logMsg("Error while loading Xml file=[" +
					xmlFileName + "] " + e.toString(),
					LoggerType.MINOR_ERROR_XML);
			statusOnLoading.setText("system error while laoding XML file.");
		}
		finally 
		{
			refISWTGUIManager.setProgressbarVisible(false);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	protected void cancelPressed() {

		Application.geneview_core.setXmlFileName("");
		super.cancelPressed();
	}
}
