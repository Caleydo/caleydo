package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.jogl.gears.GearsViewRep;

/**
 * Class implements the command for creating a gears view.
 * 
 * @see org.caleydo.core.command.ICommand
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateGears 
extends ACmdCreate_IdTargetLabelParentAttrOpenGL  {
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateGears(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		super(generalManager, 
				commandManager,
				commandQueueSaxType);
	}

	/**
	 * Method creates a gears view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
		GearsViewRep gearsView = (GearsViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_GEARS,
						iUniqueId, 
						iParentContainerId,
						sLabel);
		
		viewManager.registerItem(gearsView, 
				iUniqueId);
		
		gearsView.setAttributes(iWidthX, iHeightY);
		gearsView.initView();
		gearsView.drawView();
		
		commandManager.runDoCommand(this);
	}

	
	public void setParameterHandler( final IParameterHandler parameterHandler ) {

		assert parameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(parameterHandler);
	}
	
	public void undoCommand() throws CaleydoRuntimeException {
		
		commandManager.runUndoCommand(this);
	}
}
