package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class implementes the command for creating a slider view that is able to
 * change selection data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateSelectionSlider
	extends ACmdCreate_IdTargetLabelParentXY
{

	protected int iSelectionId = 0;

	protected String sSelectionFieldName = "";

	/**
	 * Constructor
	 */
	public CmdViewCreateSelectionSlider(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	/**
	 * Method creates a slider view, sets the attributes and calls the init and
	 * draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		// IViewManager viewManager = ((IViewManager) generalManager
		// .getManagerByObjectType(ManagerObjectType.VIEW));
		//		
		// SelectionSliderViewRep sliderView =
		// (SelectionSliderViewRep)viewManager
		// .createView(ManagerObjectType.VIEW_SWT_SELECTION_SLIDER,
		// iUniqueID,
		// iParentContainerId,
		// sLabel);
		//		
		// viewManager.registerItem(
		// sliderView,
		// iUniqueID);
		//
		// sliderView.setAttributes(iWidthX, iHeightY, iSelectionId,
		// sSelectionFieldName);
		// sliderView.initView();
		// sliderView.drawView();
		//		
		// commandManager.runDoCommand(this);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);

		parameterHandler.setValueAndTypeAndDefault("iSelectionId", parameterHandler
				.getValueString(CommandQueueSaxType.TAG_DETAIL.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "0");

		parameterHandler.setValueAndTypeAndDefault("sSelectionFieldName", parameterHandler
				.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey()),
				IParameterHandler.ParameterHandlerType.STRING, "invalid selection field");

		iSelectionId = parameterHandler.getValueInt("iSelectionId");

		sSelectionFieldName = parameterHandler.getValueString("sSelectionFieldName");

	}

	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
}
