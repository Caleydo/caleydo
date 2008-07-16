package org.caleydo.rcp.action.file;

import org.caleydo.rcp.dialog.file.OpenCsvDataFileDialog;
import org.caleydo.rcp.image.IImageKeys;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Triggers file loading for textual raw data files.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class FileOpenCsvDataFileAction extends Action implements
		ISelectionListener, ActionFactory.IWorkbenchAction {

	private final IWorkbenchWindow window;
	public final static String ID = "org.caleydo.rcp.openXmlConfiFile";
	private IStructuredSelection selection;

	/**
	 * Constructor.
	 */
	public FileOpenCsvDataFileAction(IWorkbenchWindow window) {
		
		super("Load CSV data");
		this.window = window;
	    setId(ID);
	    setToolTipText("Load CSV data");
	    setImageDescriptor(
	        AbstractUIPlugin.imageDescriptorFromPlugin(
	        "org.caleydo.rcp", IImageKeys.FILE_OPEN_XML_CONFIG_FILE));
	    window.getSelectionService().addSelectionListener(this);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection incoming) 
	{	
		  // Selection containing elements
		  if (incoming instanceof IStructuredSelection) 
		  {
		    selection = (IStructuredSelection) incoming;
		    setEnabled(selection.size() == 1 &&
		        selection.getFirstElement() instanceof PlatformObject);
		  } else {
		    // Other selections, for example containing text or of other kinds.
		    setEnabled(false);
		  }
	}


	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	public void run() {
		
		OpenCsvDataFileDialog d = new OpenCsvDataFileDialog(window.getShell());
		int code = d.open();
//		if (code == Window.OK) 
//		{
//			
//		}
	}
}
