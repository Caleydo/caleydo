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
 *
 */
public abstract class ACmdCreate_IdTargetLabelParentXY 
extends ACmdCreate_IdTargetLabelParent {

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
	 * @param refGeneralManager
	 */
	protected ACmdCreate_IdTargetLabelParentXY(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}	
	
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);			
		
		StringTokenizer token = new StringTokenizer(
				sAttribute2,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		if  (token.hasMoreTokens())
		{
			refParameterHandler.setValueAndTypeAndDefault("iWidthX",
					token.nextToken(), 
					ParameterHandlerType.INT,
					"-1" );
		
			if (token.hasMoreTokens())
			{
				refParameterHandler.setValueAndTypeAndDefault("iHeightY",
						token.nextToken(), 
						ParameterHandlerType.INT,
						"-1" );
			}
			else
			{
				refParameterHandler.setValueAndType("iHeightY", "-1", ParameterHandlerType.INT);
			}
		}
		else
		{
			refParameterHandler.setValueAndType("iWidthX", "-1", ParameterHandlerType.INT);
			refParameterHandler.setValueAndType("iHeightY", "-1", ParameterHandlerType.INT);
		}
		
		iWidthX = refParameterHandler.getValueInt("iWidthX");
		iHeightY = refParameterHandler.getValueInt("iHeightY");
	}
}
