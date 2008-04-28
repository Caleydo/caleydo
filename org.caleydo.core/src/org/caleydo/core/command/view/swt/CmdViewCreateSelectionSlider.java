package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.slider.SelectionSliderViewRep;

/**
 * Class implementes the command for creating a slider view 
 * that is able to change selection data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateSelectionSlider 
extends ACmdCreate_IdTargetLabelParentXY {
	
	protected int iSelectionId = 0;
	
	protected String sSelectionFieldName = "";
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateSelectionSlider(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
		SelectionSliderViewRep sliderView = (SelectionSliderViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_SELECTION_SLIDER,
							iUniqueId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				sliderView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		sliderView.setAttributes(iWidthX, iHeightY, iSelectionId, sSelectionFieldName);
		sliderView.initView();
		sliderView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		refParameterHandler.setValueAndTypeAndDefault("iSelectionId",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.INT,
				"0");
		
		refParameterHandler.setValueAndTypeAndDefault("sSelectionFieldName",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.STRING,
				"invalid selection field");
		
		iSelectionId = refParameterHandler.getValueInt( "iSelectionId" );
		
		sSelectionFieldName = refParameterHandler.getValueString( "sSelectionFieldName" );

	}
	
	public void undoCommand() throws CaleydoRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
