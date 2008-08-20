package org.caleydo.rcp.action.file;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.image.IImageKeys;
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

	@Override
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
		GeneralManager.get().getViewGLCanvasManager().cleanup();

		GeneralManager.get().getCommandManager().readSerializedObjects(sFileName);

		Application.applicationWorkbenchAdvisor.openLoadedViews();
	}

	@Override
	public void dispose()
	{

		// nothing to do
	}
}
