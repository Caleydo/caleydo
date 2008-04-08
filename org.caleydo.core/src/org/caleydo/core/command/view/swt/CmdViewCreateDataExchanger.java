package org.geneview.core.command.view.swt;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.swt.data.exchanger.DataExchangerViewRep;

/**
 * Class implements the command for 
 * the data exchanger view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateDataExchanger 
extends ACmdCreate_IdTargetLabelParentXY  {
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdViewCreateDataExchanger(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentXY#setParameterHandler(org.geneview.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		assert parameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(parameterHandler);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		DataExchangerViewRep dataExchangerView = (DataExchangerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_EXCHANGER,
							iUniqueId, 
							iParentContainerId,
							sLabel);
		
		viewManager.registerItem(
				dataExchangerView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		viewManager.addViewRep(dataExchangerView);

		dataExchangerView.setAttributes(iWidthX, iHeightY);
		dataExchangerView.initView();
		dataExchangerView.drawView();
		
		refCommandManager.runDoCommand(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() {

		refCommandManager.runUndoCommand(this);
	}
}
