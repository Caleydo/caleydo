/**
 * 
 */
package org.caleydo.rcp.action.file;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.Action;
//import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.caleydo.rcp.dialog.file.OpenXmlConfigFileDialog;
import org.caleydo.rcp.image.IImageKeys;
//import org.caleydo.rcp.model.ContactsEntry;
//import org.caleydo.rcp.model.ContactsGroup;
//import org.eclipsercp.hyperbola.model.ContactsGroup;

/**
 * @author Michael Kalkusch
 *
 */
public class FileOpenXmlConfigFileAction extends Action implements
		ISelectionListener, ActionFactory.IWorkbenchAction {

	private final IWorkbenchWindow window;
	public final static String ID = "org.caleydo.rcp.openXmlConfiFile";
	private IStructuredSelection selection;

	  
	public FileOpenXmlConfigFileAction(IWorkbenchWindow window) {
		super("Load Project");
		this.window = window;
	    setId(ID);
	    setToolTipText("open a Xml config file to load your workspace settings");
	    setImageDescriptor(
	        AbstractUIPlugin.imageDescriptorFromPlugin(
	        "org.caleydo.rcp", IImageKeys.FILE_OPEN_XML_CONFIG_FILE));
	    window.getSelectionService().addSelectionListener(this);
	}
	
	
//	/**	
//	 * 
//	 */
//	public FileOpenXmlConfigFileAction() {
//		// TODO Auto-generated constructor stub
//
//	}
//
//	/**
//	 * @param text
//	 */
//	public FileOpenXmlConfigFileAction(String text) {
//		super(text);
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @param text
//	 * @param image
//	 */
//	public FileOpenXmlConfigFileAction(String text, ImageDescriptor image) {
//		super(text, image);
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @param text
//	 * @param style
//	 */
//	public FileOpenXmlConfigFileAction(String text, int style) {
//		super(text, style);
//		// TODO Auto-generated constructor stub
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		  // Selection containing elements
		  if (incoming instanceof IStructuredSelection) {
		    selection = (IStructuredSelection) incoming;
		    setEnabled(selection.size() == 1 &&
		        selection.getFirstElement() instanceof PlatformObject);
		    
//		    setEnabled(selection.size() == 1 &&
//			        selection.getFirstElement() instanceof ContactsGroup);
		  } else {
		    // Other selections, for example containing text or of other kinds.
		    setEnabled(false);
		  }
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	public void run() {
		OpenXmlConfigFileDialog d = new OpenXmlConfigFileDialog(window.getShell());
		  int code = d.open();
		  if (code == Window.OK) {
//		    Object item = selection.getFirstElement();
//		    ContactsGroup group = (ContactsGroup) item;
//		    ContactsEntry entry =
//		        new ContactsEntry(group, d.getNickname(), d.getNickname(),
//		            d.getServerText());
//		    group.addEntry(entry);
		  }
		}
}
