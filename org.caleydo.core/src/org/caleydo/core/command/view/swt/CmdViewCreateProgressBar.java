package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.progressbar.ProgressBarViewRep;

/**
 * Class implementes the command for creating a progress bar view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateProgressBar 
extends ACmdCreate_IdTargetLabelParentXY {
	
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
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
		ProgressBarViewRep progressBarView = (ProgressBarViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_PROGRESS_BAR,
							iUniqueId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				progressBarView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		progressBarView.setAttributes(iProgressBarCurrentValue);
		progressBarView.initView();
		progressBarView.drawView();
		
		commandManager.runDoCommand(this);
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

	public void undoCommand() throws CaleydoRuntimeException {
		
		commandManager.runUndoCommand(this);
	}
}
