package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.data.explorer.DataExplorerViewRep;

/**
 * Class implementes the command for creating a data explorer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateDataExplorer 
extends ACmdCreate_IdTargetLabelParentXY {
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateDataExplorer(
			final IGeneralManager generalManager, 
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		super(generalManager, 
				commandManager,
				commandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY#checkOpenGLSetting()
	 */
	protected final void checkOpenGLSetting() {
		/** not openGL settings are required */	
	}
	
	/**
	 * Method creates a data explorer view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
		DataExplorerViewRep dataExplorerView = (DataExplorerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER,
							iUniqueId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				dataExplorerView, 
				iUniqueId, 
				ManagerObjectType.VIEW);
		
		viewManager.addViewRep(dataExplorerView);
		
		dataExplorerView.setAttributes(iWidthX, iHeightY);
		dataExplorerView.initView();
		dataExplorerView.drawView();
		
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
