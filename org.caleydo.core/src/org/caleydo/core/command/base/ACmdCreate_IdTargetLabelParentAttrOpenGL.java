package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

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
	 * Constructor.
	 */
	public ACmdCreate_IdTargetLabelParentAttrOpenGL(final CommandType cmdType)
	{
		super(cmdType);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		iGLCanvasID = parameterHandler.getValueInt(CommandType.TAG_GLCANVAS
				.getXmlKey());

		if (iGLCanvasID == -1)
		{
			throw new CaleydoRuntimeException("GL Canvas ID is not assigned in XML file.", 
					CaleydoRuntimeExceptionType.COMMAND);
		}
	}
}
