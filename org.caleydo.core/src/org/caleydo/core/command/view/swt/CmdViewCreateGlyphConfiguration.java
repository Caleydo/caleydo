package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.glyph.GlyphMappingConfigurationViewRep;

/**
 * Class implementes the command for creating a view.
 * 
 * @author Sauer Stefan
 */
public class CmdViewCreateGlyphConfiguration
	extends ACmdCreate_IdTargetLabelParentXY
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5818853536669040162L;

	int iNumberOfSliders = 1;

	/**
	 * Constructor.
	 */
	public CmdViewCreateGlyphConfiguration(final IGeneralManager generalManager,
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

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		GlyphMappingConfigurationViewRep view = (GlyphMappingConfigurationViewRep) viewManager
				.createView(EManagerObjectType.VIEW_SWT_GLYPH_MAPPINGCONFIGURATION, iUniqueId,
						iParentContainerId, sLabel);

		viewManager.registerItem(view, iUniqueId);

		// view.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		view.initView();
		view.drawView();

		commandManager.runDoCommand(this);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);
		/*
		 * parameterHandler.setValueAndTypeAndDefault("iNumberOfSliders",
		 * parameterHandler
		 * .getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()),
		 * IParameterHandler.ParameterHandlerType.INT, "-1"); iNumberOfSliders =
		 * parameterHandler.getValueInt("iNumberOfSliders");
		 */
	}

	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
}
