package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.swt.data.exchanger.DataExchangerViewRep;

/**
 * Class implementes the command for 
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
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabelParentXY#checkOpenGLSetting()
	 */
	protected final void checkOpenGLSetting() {
		/** not openGL settings are required */	
	}
	
	/**
	 * Method creates a data exchanger view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		DataExchangerViewRep dataExchangerView = (DataExchangerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_EXCHANGER,
							iUniqueTargetId, 
							iParentContainerId,
							sLabel,
							iGLCanvasId,
							iGLEventListernerId);
		
		viewManager.registerItem(
				dataExchangerView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		viewManager.addViewRep(dataExchangerView);

		dataExchangerView.setAttributes(iWidthX, iHeightY);
		dataExchangerView.initView();
		dataExchangerView.drawView();
		
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
