package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.image.ImageViewRep;

/**
 * Class implements the command for importing an existing image.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateImage
	extends ACmdCreate_IdTargetLabelParentXY
{

	String sImagePath = "";

	/**
	 * Constructor.
	 */
	public CmdViewCreateImage(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		ImageViewRep imageView = (ImageViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_IMAGE, iExternalID, iParentContainerId, sLabel);

		viewManager.registerItem(imageView, iExternalID);

		imageView.setAttributes(iWidthX, iHeightY, sImagePath);
		imageView.initView();
		imageView.drawView();

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		parameterHandler.setValueAndTypeAndDefault("sImagePath", parameterHandler
				.getValueString(CommandType.TAG_DETAIL.getXmlKey()),
				IParameterHandler.ParameterHandlerType.STRING, "");

		sImagePath = parameterHandler.getValueString("sImagePath");
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
