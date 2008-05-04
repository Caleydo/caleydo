package org.caleydo.core.command.data.parser;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.ascii.microarray.MicroArrayLoaderValues2MultipleStorages;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;


/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use AMicroArrayLoader to load dataset.
 * 
 * @author Michael Kalkusch
 *
 * @see org.caleydo.core.data.collection.ISet
 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader
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
	 * Default is -1 indicateing read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopPareseFileAtLine = -1;
	
	protected int iTargetSetId;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdLoadFileNStorages( 
			final IGeneralManager generalManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(-1,
				generalManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		setCommandQueueSaxType(CommandQueueSaxType.LOAD_DATA_FILE_N_STORAGES);	
	}
	
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		super.setParameterHandler(refParameterHandler);
		
		this.setId( refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		this.sTokenPattern =  refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		this.iTargetSetId =	StringConversionTool.convertStringToInt(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey()),
				-1 );
		
		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() ),
				" " );
		
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
	
	
	
	/**
	 * Set attributes.
	 * 
	 * @param fileName
	 * @param tokenPattern
	 * @param iTargetSet
	 */
	public void setAttributes( String fileName, 
			String tokenPattern,
			final int iTargetSet ) {
			
		this.sFileName = fileName;		
		this.sTokenPattern =tokenPattern;
		this.iTargetSetId = iTargetSet;
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#loadData()
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
//		generalManager.logMsg(
//	    		"load file via importer... ([" +
//				sFileName + "] tokens:[" +
//				sTokenPattern + "]  targetSet(s)=[" +
//				iTargetSetId + "])",
//				LoggerType.STATUS );
		
		ISet useSet = generalManager.getSetManager(
				).getItemSet( iTargetSetId );
		
		if ( useSet == null ) {
			String errorMsg = "Could not load data via MicroArrayLoaderValues2MultipleStorages, target Set is not valid! file=["+
			sFileName + "] tokens:[" +
			sTokenPattern + "]  targetSet(s)=[" +
			iTargetSetId + "])";
			
//			generalManager.logMsg(
//					errorMsg,
//					LoggerType.ERROR );
	
			return;
		}
		
		MicroArrayLoaderValues2MultipleStorages loader = null;
		
		try 
		{
			loader = new MicroArrayLoaderValues2MultipleStorages( generalManager,
					sFileName, 
					IGeneralManager.bEnableMultipelThreads );
			
			//loader.setFileName( sFileName );
			loader.setTokenPattern( sTokenPattern );
			loader.setTargetSet( useSet );
			loader.setStartParsingStopParsingAtLine( iStartPareseFileAtLine,
					iStopPareseFileAtLine );
			
			loader.loadData();
			
			
		} //try
		catch ( Exception e ) 
		{
			String errorMsg = "Could not load data via MicroArrayLoaderValues2MultipleStorages, error during loading! file=["+
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				iTargetSetId + "])";
			
//			generalManager.logMsg(
//					errorMsg,
//					LoggerType.ERROR );
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
		// no undo of system shutdown!
	}
}
