package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
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
	extends ACmdExternalAttributes
{

	String sImagePath = "";

	/**
	 * Constructor.
	 */
	public CmdViewCreateImage(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1)
		{
			iParentContainerId = 
				generalManager.getIDManager().getInternalFromExternalID(iParentContainerId);
		}
		
		ImageViewRep imageView = (ImageViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_IMAGE, iParentContainerId, sLabel);

		viewManager.registerItem(imageView);

		imageView.setAttributes(sImagePath);
		imageView.initView();
		imageView.drawView();
		
		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(imageView.getID(), iExternalID);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		parameterHandler.setValueAndTypeAndDefault("sImagePath", parameterHandler
				.getValueString(ECommandType.TAG_DETAIL.getXmlKey()),
				IParameterHandler.ParameterHandlerType.STRING, "");

		sImagePath = parameterHandler.getValueString("sImagePath");
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
