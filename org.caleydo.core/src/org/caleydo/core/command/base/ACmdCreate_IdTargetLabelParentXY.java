package org.geneview.core.command.base;

import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttr;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class ACmdCreate_IdTargetLabelParentXY 
extends ACmdCreate_IdTargetLabelParentAttr {

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
