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
import cerberus.view.swt.progressbar.ProgressBarViewRep;

/**
 * Class implementes the command for creating a progress bar view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateProgressBar 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	int iProgressBarCurrentValue = 0;
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateProgressBar(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a progress bar view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		ProgressBarViewRep progressBarView = (ProgressBarViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_PROGRESS_BAR,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				progressBarView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		progressBarView.setAttributes(iProgressBarCurrentValue);
		progressBarView.initView();
		progressBarView.drawView();
		
		refCommandManager.runDoCommand(this);
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
	
		refParameterHandler.setValueAndTypeAndDefault( "iProgressBarCurrentValue",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.INT,
				"0");
		
		iProgressBarCurrentValue = 
			refParameterHandler.getValueInt("iProgressBarCurrentValue");
	}

	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
