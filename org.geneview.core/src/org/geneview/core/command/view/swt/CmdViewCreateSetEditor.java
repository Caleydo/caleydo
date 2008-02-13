package org.geneview.core.command.view.swt;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.swt.data.exchanger.NewSetEditorViewRep;
//import org.geneview.core.view.swt.data.exchanger.SetEditorViewRep;

/**
 * Class implementes the command for 
 * the data exchanger view.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdViewCreateSetEditor 
extends ACmdCreate_IdTargetLabelParentXY {
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdViewCreateSetEditor(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a data exchanger view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		NewSetEditorViewRep setEditorView = (NewSetEditorViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_SET_EDITOR,
							iUniqueId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				setEditorView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		viewManager.addViewRep(setEditorView);

		setEditorView.setAttributes(iWidthX, iHeightY);
		setEditorView.initView();
		setEditorView.drawView();
		
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
