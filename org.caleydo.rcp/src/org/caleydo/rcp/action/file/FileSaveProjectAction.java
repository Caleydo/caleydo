package org.caleydo.rcp.action.file;

import org.caleydo.rcp.Application;
import org.caleydo.rcp.image.IImageKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action responsible for saving Caleydo project files.
 * 
 * @author Marc Streit
 *
 */
public class FileSaveProjectAction 
extends Action 
implements ActionFactory.IWorkbenchAction {
	
	public final static String ID = "org.caleydo.rcp.saveProjectDialog";
	
	public final Composite parentComposite;
	
	/**
	 * Constructor.
	 */  
	public FileSaveProjectAction(final Composite parentComposite) {

		super("Save Project");

	    setId(ID);
	    setToolTipText("Save project dialog");
	    setImageDescriptor(
	        AbstractUIPlugin.imageDescriptorFromPlugin(
	        "org.caleydo.rcp", IImageKeys.FILE_OPEN_XML_CONFIG_FILE)); // TODO: change image
	    
	    this.parentComposite = parentComposite;

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {

		super.run();

		String sFilePath = "";
		FileDialog fileDialog = new FileDialog(parentComposite.getShell());
        fileDialog.setText("Open Project");
        fileDialog.setFilterPath(sFilePath);
        String[] filterExt = {"*.cal"};
        fileDialog.setFilterExtensions(filterExt);
        
        String sFileName = fileDialog.open();

        Application.generalManager.getCommandManager().writeSerializedObjects(sFileName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {
		
		// nothing to do
	}
}
