package org.caleydo.rcp.action.file;

import org.caleydo.rcp.image.IImageKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action responsible for creating new Caleydo project files.
 * 
 * @author Marc Streit
 */
public class FileNewProjectAction
	extends Action
	implements ActionFactory.IWorkbenchAction
{

	public final static String ID = "org.caleydo.rcp.FileNewProjectAction";

	public final Composite parentComposite;

	/**
	 * Constructor.
	 */
	public FileNewProjectAction(final Composite parentComposite)
	{

		super("New Project");

		setId(ID);
		setToolTipText("Create new project");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.caleydo.rcp",
				IImageKeys.FILE_OPEN_XML_CONFIG_FILE));

		this.parentComposite = parentComposite;
	}

	@Override
	public void run()
	{

	}

	@Override
	public void dispose()
	{

		// nothing to do
	}
}
