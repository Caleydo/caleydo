package org.geneview.rcp.action.search;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geneview.rcp.dialog.file.OpenCsvDataFileDialog;
import org.geneview.rcp.dialog.search.OpenSearchDataEntityDialog;
import org.geneview.rcp.image.IImageKeys;

/**
 * 
 * @author Marc Streit
 *
 */
public class OpenSearchDataEntityAction 
extends Action 
implements ActionFactory.IWorkbenchAction {

	private final IWorkbenchWindow window;
	
	public final static String ID = "org.geneview.rcp.openSearchDialog";

	/**
	 * Constructor 
	 * 
	 * @param window
	 */  
	public OpenSearchDataEntityAction(IWorkbenchWindow window) {
		super("Search...");
		this.window = window;
	    setId(ID);
	    setToolTipText("Open data search entity dialog");
	    setImageDescriptor(
	        AbstractUIPlugin.imageDescriptorFromPlugin(
	        "org.geneview.rcp", IImageKeys.FILE_OPEN_XML_CONFIG_FILE)); // TODO: change image
	}


	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		
		OpenSearchDataEntityDialog searchDialog = 
			new OpenSearchDataEntityDialog(window.getShell());
		
		searchDialog.open();
	}
}
