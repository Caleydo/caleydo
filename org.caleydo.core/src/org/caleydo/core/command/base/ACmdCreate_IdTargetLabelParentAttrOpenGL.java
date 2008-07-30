package org.caleydo.core.command.base;

import java.util.logging.Level;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract command that reads OpenGL canvas ID.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACmdCreate_IdTargetLabelParentAttrOpenGL
	extends ACmdCreate_IdTargetLabelParentXY
{

	/**
	 * GLEventListener ID used for OpenGL
	 */
	protected int iGLCanvasID = -1;

	/**
	 * @param generalManager
	 * @param commandManager
	 * @param commandQueueSaxType
	 */
	public ACmdCreate_IdTargetLabelParentAttrOpenGL(IGeneralManager generalManager,
			ICommandManager commandManager, CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		super.setParameterHandler(parameterHandler);

		iGLCanvasID = parameterHandler.getValueInt(CommandQueueSaxType.TAG_GLCANVAS
				.getXmlKey());

		checkOpenGLSetting();
	}

	/**
	 * Overwrite this in derived classes if OpenGL settings are not required.
	 */
	protected void checkOpenGLSetting()
	{

		if (iGLCanvasID == -1)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"GL Canvas ID is not assigned in XML file.");
		}
	}
}
