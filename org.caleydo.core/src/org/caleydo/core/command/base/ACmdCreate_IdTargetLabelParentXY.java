package org.caleydo.core.command.base;

import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * Abstract command that reads width and height of an view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACmdCreate_IdTargetLabelParentXY
	extends ACmdCreate_IdTargetLabelParent
{

	/**
	 * Width of the widget.
	 */
	protected int iWidthX;

	/**
	 * Height of the widget;
	 */
	protected int iHeightY;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	protected ACmdCreate_IdTargetLabelParentXY(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		super.setParameterHandler(parameterHandler);

		StringTokenizer token = new StringTokenizer(sAttribute2,
				IGeneralManager.sDelimiter_Parser_DataItems);

		if (token.hasMoreTokens())
		{
			parameterHandler.setValueAndTypeAndDefault("iWidthX", token.nextToken(),
					ParameterHandlerType.INT, "-1");

			if (token.hasMoreTokens())
			{
				parameterHandler.setValueAndTypeAndDefault("iHeightY", token.nextToken(),
						ParameterHandlerType.INT, "-1");
			}
			else
			{
				parameterHandler.setValueAndType("iHeightY", "-1", ParameterHandlerType.INT);
			}
		}
		else
		{
			parameterHandler.setValueAndType("iWidthX", "-1", ParameterHandlerType.INT);
			parameterHandler.setValueAndType("iHeightY", "-1", ParameterHandlerType.INT);
		}

		iWidthX = parameterHandler.getValueInt("iWidthX");
		iHeightY = parameterHandler.getValueInt("iHeightY");
	}
}
