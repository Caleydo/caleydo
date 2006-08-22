package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.view.CmdViewCreateAdapter;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;

/**
 * Class implementes the command for creating a gears view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateGears extends CmdViewCreateAdapter implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateGears(
			IGeneralManager refGeneralManager,
			final LinkedList<String> listAttributes)
	{
		super(refGeneralManager, listAttributes);
	}

	/**
	 * Method creates a gears view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		GearsViewRep gearsView = (GearsViewRep) ((IViewManager)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.VIEW)).
					createView(ManagerObjectType.VIEW_SWT_GEARS, 
							iViewId, iParentContainerId, sLabel);
		
		gearsView.setAttributes(refVecAttributes);
		gearsView.extractAttributes();
		gearsView.retrieveGUIContainer();
		gearsView.initView();
		gearsView.drawView();
	}

}
