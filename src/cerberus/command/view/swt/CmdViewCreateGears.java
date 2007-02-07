package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a gears view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateGears 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 */
	public CmdViewCreateGears(
			final IGeneralManager refGeneralManager) {
		
		super(refGeneralManager);
	}

	/**
	 * Method creates a gears view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
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
		
		gearsView.setAttributes(iWidthX, iHeightY);
		gearsView.retrieveGUIContainer();
		gearsView.initView();
		gearsView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {

		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);
	}
	
	public void undoCommand() throws CerberusRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
