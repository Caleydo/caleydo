package org.caleydo.core.command.data.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.ascii.microarray.MicroArrayLoaderValues2MultipleStorages;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;


/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use AMicroArrayLoader to load data set.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdLoadFileNStorages 
extends ACommand {
	
	protected String sFileName;
	
	protected String sTokenPattern;
	
	/**
	 * Default is 32, because gpr files have a header of that size!
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#iStartParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#getStartParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStartPareseFileAtLine = 32;
	
	/**
	 * Default is -1 indicating read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopPareseFileAtLine = -1;
	
	protected ArrayList<Integer> iAlTargetStorageId;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param commandManager
	 * @param commandQueueSaxType
	 */
	public CmdLoadFileNStorages(final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		super(-1, generalManager, commandManager, commandQueueSaxType);
		
		setCommandQueueSaxType(CommandQueueSaxType.LOAD_DATA_FILE);	
		
		iAlTargetStorageId = new ArrayList<Integer>();
	}
	
	
	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		super.setParameterHandler(parameterHandler);
		
		this.setId( parameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = parameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		this.sTokenPattern =  parameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );

		StringTokenizer tokenizer = new StringTokenizer(
				parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey()), 
				GeneralManager.sDelimiter_Parser_DataItems);

		while (tokenizer.hasMoreTokens())
		{
			iAlTargetStorageId.add(StringConversionTool.convertStringToInt(tokenizer.nextToken(), -1));
		}
		
		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey()), " " );
		
		if ( iArrayStartStop.length > 0 ) 
		{
			iStartPareseFileAtLine = iArrayStartStop[0];
			
			if ( iArrayStartStop.length > 1 ) 
			{
				
				if (( iArrayStartStop[0] > iArrayStartStop[1] )&&
						(iArrayStartStop[1] != -1 )) {
//					generalManager.logMsg(
//							"CmdSystemLoadFileNStorages ignore stop index=(" + 
//							iArrayStartStop[1]  + 
//							"), because it is smaller than start index (" + 
//							iArrayStartStop[0] + ") !",
//							LoggerType.STATUS );
					return;
				}
				iStopPareseFileAtLine = iArrayStartStop[1];
			} // if ( iArrayStartStop.length > 0 ) 
		} // if ( iArrayStartStop.length > 0 ) 
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		generalManager.getLogger().log(Level.INFO, 
				"Load data file " +sFileName +" using token pattern " 
				+sTokenPattern +". Data is stored in Storage with ID " +iAlTargetStorageId.toString());
		
		MicroArrayLoaderValues2MultipleStorages loader = null;
		
		try 
		{
			loader = new MicroArrayLoaderValues2MultipleStorages( generalManager,
					sFileName, 
					IGeneralManager.bEnableMultipelThreads );
			
			loader.setTokenPattern(sTokenPattern);
			loader.setTargetStorages(iAlTargetStorageId);
			loader.setStartParsingStopParsingAtLine(iStartPareseFileAtLine, iStopPareseFileAtLine );
			
			loader.loadData();	
		} //try
		catch ( Exception e ) 
		{
			generalManager.getLogger().log(Level.SEVERE, "Error during parsing data file " +sFileName);
		} // catch
		finally 
		{
			if ( loader != null ) 
			{
				loader.destroy();
				loader = null;
			}
		} // finally
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {

	}
}
