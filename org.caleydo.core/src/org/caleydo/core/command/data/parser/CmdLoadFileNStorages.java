package org.caleydo.core.command.data.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.ascii.tabular.TabularAsciiDataReader;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;


/**
 * Command loads data from file using a token pattern and a target ISet.
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
	
	protected int iStartParseFileAtLine = 0;
	
	/**
	 * Default is -1 indicating read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.tabular.TabularAsciiDataReader#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.tabular.TabularAsciiDataReader#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.tabular.TabularAsciiDataReader#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopParseFileAtLine = -1;
	
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
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACommand#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
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

		iAlTargetStorageId = new ArrayList<Integer>();
		
		while (tokenizer.hasMoreTokens())
		{
			iAlTargetStorageId.add(StringConversionTool.convertStringToInt(tokenizer.nextToken(), -1));
		}
		
		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey()), " " );
		
		if ( iArrayStartStop.length > 0 ) 
		{
			iStartParseFileAtLine = iArrayStartStop[0];
			
			if ( iArrayStartStop.length > 1 ) 
			{
				
				if (( iArrayStartStop[0] > iArrayStartStop[1] )&&
						(iArrayStartStop[1] != -1 )) 
				{
					generalManager.getLogger().log(Level.SEVERE, 
							"Ignore stop inde="+iArrayStartStop[1]+" because it is maller that start index="+iArrayStartStop[0]);

					return;
				}
				iStopParseFileAtLine = iArrayStartStop[1];
			} // if ( iArrayStartStop.length > 0 ) 
		} // if ( iArrayStartStop.length > 0 ) 
	}
	
	public void setAttributes(final ArrayList<Integer> iAlStorageId,
			final String sFileName,
			final String sTokenPattern,
			final int iStartParseFileAtLine,
			final int iStopParseFileAtLine) {
		
		iAlTargetStorageId = iAlStorageId;
		this.sFileName = sFileName;
		this.sTokenPattern = sTokenPattern;
		this.iStartParseFileAtLine = iStartParseFileAtLine;
		this.iStopParseFileAtLine = iStopParseFileAtLine;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		generalManager.getLogger().log(Level.INFO, 
				"Load data file " +sFileName +" using token pattern " 
				+sTokenPattern +". Data is stored in Storage with ID " +iAlTargetStorageId.toString());
		
		TabularAsciiDataReader loader = null;
		
		try 
		{
			loader = new TabularAsciiDataReader(generalManager, sFileName);
			
			loader.setTokenPattern(sTokenPattern);
			loader.setTargetStorages(iAlTargetStorageId);
			loader.setStartParsingStopParsingAtLine(iStartParseFileAtLine, iStopParseFileAtLine );
			
			loader.loadData();	
			
			commandManager.runDoCommand(this);
			
		} //try
		catch ( Exception e ) 
		{
			generalManager.getLogger().log(Level.SEVERE, "Error during parsing data file " +sFileName);
			e.printStackTrace();	
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
