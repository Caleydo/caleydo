package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.data.exchanger.NewSetEditorViewRep;
//import org.caleydo.core.view.swt.data.exchanger.SetEditorViewRep;

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
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
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
	
	public void undoCommand() throws CaleydoRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
