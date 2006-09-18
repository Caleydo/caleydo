package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreateGui;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a gears view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateGears 
extends ACmdCreateGui 
implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateGears(
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
	}

	/**
	 * Method creates a gears view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		GearsViewRep gearsView = (GearsViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_GEARS,
						iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				gearsView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);
		
		gearsView.setAttributes(refParameterHandler);
		
		gearsView.retrieveGUIContainer();
		gearsView.initView();
		gearsView.drawView();
	}

}
