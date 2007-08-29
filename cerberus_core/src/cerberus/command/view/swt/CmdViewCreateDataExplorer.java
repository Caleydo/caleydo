package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.swt.data.explorer.DataExplorerViewRep;

/**
 * Class implementes the command for creating a data explorer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateDataExplorer 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateDataExplorer(
			final IGeneralManager refGeneralManager, 
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a data explorer view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		DataExplorerViewRep dataExplorerView = (DataExplorerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel,
							iGLCanvasId,
							iGLEventListernerId);
		
		viewManager.registerItem(
				dataExplorerView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);
		
		viewManager.addViewRep(dataExplorerView);
		
		dataExplorerView.setAttributes(iWidthX, iHeightY);
		dataExplorerView.initView();
		dataExplorerView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {

		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);
	}
	
	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
