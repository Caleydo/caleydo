package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.image.ImageViewRep;

/**
 * Class implementes the command for importing an existing image.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateImage
	extends ACmdCreate_IdTargetLabelParentXY
{

	String sImagePath = "";

	/**
	 * Constructor
	 */
	public CmdViewCreateImage(final IGeneralManager generalManager,
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

		ImageViewRep imageView = (ImageViewRep) viewManager.createView(
				EManagerObjectType.VIEW_SWT_IMAGE, iUniqueId, iParentContainerId, sLabel);

		viewManager.registerItem(imageView, iUniqueId);

		imageView.setAttributes(iWidthX, iHeightY, sImagePath);
		imageView.initView();
		imageView.drawView();

		commandManager.runDoCommand(this);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);

		parameterHandler.setValueAndTypeAndDefault("sImagePath", parameterHandler
				.getValueString(CommandQueueSaxType.TAG_DETAIL.getXmlKey()),
				IParameterHandler.ParameterHandlerType.STRING, "");

		sImagePath = parameterHandler.getValueString("sImagePath");
	}

	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
}
