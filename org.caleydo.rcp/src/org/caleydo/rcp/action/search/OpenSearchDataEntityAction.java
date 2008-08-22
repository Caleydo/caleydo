package org.caleydo.rcp.action.search;

import org.caleydo.rcp.dialog.search.OpenSearchDataEntityDialog;
import org.caleydo.rcp.image.IImageKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Marc Streit
 */
public class OpenSearchDataEntityAction
	extends Action
	implements ActionFactory.IWorkbenchAction
{

	private final IWorkbenchWindow window;

	public final static String ID = "org.caleydo.rcp.openSearchDialog";

	/**
	 * Constructor
	 * 
	 * @param window
	 */
	public OpenSearchDataEntityAction(IWorkbenchWindow window)
	{
		super("Search...");
		this.window = window;
		setId(ID);
		setToolTipText("Open data search entity dialog");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.caleydo.rcp",
				IImageKeys.FILE_OPEN_XML_CONFIG_FILE)); // TODO: change image

	}

	public void dispose()
	{

	}

	@Override
	public void run()
	{

		OpenSearchDataEntityDialog searchDialog = new OpenSearchDataEntityDialog(window
				.getShell());

		searchDialog.open();
	}
}
