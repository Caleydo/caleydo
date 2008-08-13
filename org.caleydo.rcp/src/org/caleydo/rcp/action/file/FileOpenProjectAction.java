package org.caleydo.rcp.action.file;

import org.caleydo.rcp.Application;
import org.caleydo.rcp.image.IImageKeys;
import org.caleydo.rcp.splashHandlers.ExtensibleSplashHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action responsible for opening Caleydo project files.
 * 
 * @author Marc Streit
 */
public class FileOpenProjectAction
	extends Action
	implements ActionFactory.IWorkbenchAction
{

	public final static String ID = "org.caleydo.rcp.FileOpenProjectAction";

	public final Composite parentComposite;

	/**
	 * Constructor.
	 */
	public FileOpenProjectAction(final Composite parentComposite)
	{

		super("Open Project");

		setId(ID);
		setToolTipText("Open Caleydo Project");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.caleydo.rcp",
				IImageKeys.FILE_OPEN_XML_CONFIG_FILE));

		this.parentComposite = parentComposite;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{

		String sFilePath = "";
		FileDialog fileDialog = new FileDialog(parentComposite.getShell());
		fileDialog.setText("Open Project");
		fileDialog.setFilterPath(sFilePath);
		String[] filterExt = { "*.cal" };
		fileDialog.setFilterExtensions(filterExt);

		String sFileName = fileDialog.open();

		// View and GL canvas cleanup
		Application.generalManager.getViewGLCanvasManager().cleanup();

		Application.generalManager.getCommandManager().readSerializedObjects(sFileName);

		Application.applicationWorkbenchAdvisor.openLoadedViews();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose()
	{

		// nothing to do
	}
}
