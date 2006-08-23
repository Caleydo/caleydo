package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;

/**
 * Class implementes the command for creating a pathway view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreatePathway extends ACmdCreate implements ICommand
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreatePathway( 
			IGeneralManager refGeneralManager,
			final LinkedList <String> listAttributes) 
	{
		super(refGeneralManager, listAttributes);
	}

	/**
	 * Method creates a pathway view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{				
		PathwayViewRep pathwayView = (PathwayViewRep) ((IViewManager)refGeneralManager.
			getManagerByBaseType(ManagerObjectType.VIEW)).
				createView(ManagerObjectType.VIEW_SWT_PATHWAY, 
						iUniqueId, iParentContainerId, sLabel);

		pathwayView.setAttributes(refVecAttributes);
		pathwayView.extractAttributes();
		pathwayView.retrieveGUIContainer();
		pathwayView.initView();
		pathwayView.drawView();
	}
}
