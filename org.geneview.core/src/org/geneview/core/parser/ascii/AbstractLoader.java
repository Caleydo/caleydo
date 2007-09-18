/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package org.geneview.core.parser.ascii;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.geneview.core.data.xml.IMementoXML;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.gui.SWTGUIManager;


/**
 * Loader for MircoArray data sets in *.gpr format.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AbstractLoader 
implements IMementoXML, IParserObject {

	/**
	 * Work around, disabel progressbar, 
	 * because it is not threadsafe yet.
	 */
	private final boolean bUseMultipleThreads;
	
	/**
	 *  file name of *.gpr file
	 */  
	private String sFileName = "";
	
	
	/**
	 * Defiens the nubmer of leines to beread from a file.
	 * only usefull, if loadData_TestLinesToBeRead() was called before reading the file.
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#loadData_TestLinesToBeRead(BufferedReader)
	 * @see org.geneview.core.parser.ascii.AbstractLoader#loadData_TestLinesToBeRead(String)
	 */
	private int iLinesInFileToBeRead = -1;
		
	/**
	 * Position of progress bar stored in method progressBarSetStoreInitTitle()
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarSetStoreInitTitle(String, int, int)
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarResetTitle()
	 * @see org.geneview.core.parser.ascii.AbstractLoader#sLastProgressBarText
	 */
	private int iProgressBarLastPosition;
	
	/**
	 * Text progress bar stored in method progressBarSetStoreInitTitle()
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarSetStoreInitTitle(String, int, int)
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarResetTitle()
	 * @see org.geneview.core.parser.ascii.AbstractLoader#iProgressBarLastPosition	 
	 */
	private String sLastProgressBarText;
	

	/**
	 * Stores the current position of the progress bar.
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarCurrentPosition()
	 */
	private int iProgressBarCurrentPosition;
	

	/**
	 * Stores the current positon of the progress bar after calling
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarSetStoreInitTitle(String, int, int)
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarStoredIncrement()
	 * @see org.geneview.core.parser.ascii.AbstractLoader#fProgressBarInc
	 */
	private float fProgressBarIndex;
	
	/**
	 * Increments progressbar index.
	 * Call progressBarIncrement() increment the progress bar.
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarStoredIncrement()
	 * @see org.geneview.core.parser.ascii.AbstractLoader#fProgressBarIndex
	 */
	private float fProgressBarInc;
	
	
	/**
	 * ref to singelton
	 */
	protected final IGeneralManager refGeneralManager;
	
	/**
	 * ref to SwtGuiManager.
	 */
	protected final SWTGUIManager refSWTGUIManager;	

	
	/**
	 * Define numbers of lines to skip as assumed to be the header of a file.
	 * Defines how many lines are part of the header file.
	 * By default these lines are skipped during parsing.
	 * Default is 32, because gpr files have a header of that size!
	 */
	protected int iStartParsingAtLine = 0;
	
	/**
	 * Define numbers of lines to skip as assumed to be the header of a file.
	 * Default is -1 which means until the end of file.
	 */
	protected int iStopParsingAtLine = Integer.MAX_VALUE;

	
	/**
	 * Define the speperator
	 * TAB is the default token.
	 */
	protected String sTokenSeperator = IGeneralManager.sDelimiter_Parser_DataItems_Tab;
	
	/**
	 * Define the speperator
	 * TAB is the default token.
	 */
	protected String sTokenInnerLoopSeperator = IGeneralManager.sDelimiter_Parser_DataItems;
	
	/**
	 * Define, if exact file size need to be computed prior to loading the file.
	 * Default is fales.
	 * 
	 * @see AbstractLoader#loadData_TestLinesToBeRead(BufferedReader)
	 */
	protected boolean bRequiredSizeOfReadableLines = false;

	protected int iLineInFile = 1;
	
	protected int iLineInFile_CurrentDataIndex = 0;


	
	public AbstractLoader(final IGeneralManager setGeneralManager, 
			final String setFileName,
			final boolean enableMultipeThreads) {

		refGeneralManager = setGeneralManager;
		
		refSWTGUIManager = (SWTGUIManager) this.refGeneralManager.getSingelton().getSWTGUIManager();
		
		assert refGeneralManager!= null :"null-pointer in constructor";		
		
		this.sFileName = setFileName;
		
		bUseMultipleThreads = enableMultipeThreads;
		
		init();
	}
	
	/**
	 * Set the current token seperator.
	 * 
	 * @param token current token seperator
	 */
	public final void setTokenSeperator(final String token) {			
		sTokenSeperator = token;
	}
	
	/**
	 * Get the current token seperator.
	 * 
	 * @return current token seperator
	 */
	public final String getTokenSeperator() {			
		return sTokenSeperator;
	}
	
	/**
	 * Set the current token seperator.
	 * 
	 * @param token current token seperator
	 */
	public final void setTokenSeperatorInnerLoop(final String token) {			
		sTokenInnerLoopSeperator = token;
	}
	
	/**
	 * Get the current token seperator.
	 * 
	 * @return current token seperator
	 */
	public final String getTokenSeperatorInnerLoop() {			
		return sTokenInnerLoopSeperator;
	}
	
	
	/**
	 * Set the current file name.
	 * 
	 * @param setFileName set current file name
	 */
	public final void setFileName(String setFileName) {
		this.sFileName = setFileName;
	}
	
	/**
	 * Get the filename for the current file.
	 * 
	 * @return curretn file name
	 */
	public final String getFileName( ) {
		return this.sFileName;
	}

	
	public final void setStartParsingStopParsingAtLine( final int iStartParsingAtLine,
			final int iStopParsingAtLine ) 
	{	
		this.iStartParsingAtLine = iStartParsingAtLine;
		
		if ( iStopParsingAtLine < 0 )
		{
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			return;
		}
		
		if ( iStartParsingAtLine > iStopParsingAtLine )
		{
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			refGeneralManager.getSingelton().logMsg(
					"AMicroArrayLoader.setStartParsingStopParsingAtLine() stop index is smaller than start index. set stop index to end of file!",
					LoggerType.MINOR_ERROR );			
			return;
		}		
		this.iStopParsingAtLine = iStopParsingAtLine;
	}
	
	/**
	 * Get the line the parser starts to read from the file.
	 * 
	 * @return first line to be read
	 */
	public final int getStartParsingAtLine()
	{	
		return this.iStartParsingAtLine;
	}
	
	/**
	 * Return line to stop parsing.
	 * 
	 * @return last line to be parses
	 */
	public final int getStopParsingAtLine()
	{	
		return this.iStopParsingAtLine;
	}
	
	/**
	 * Reads the file and counts the numbers of lines to be read.
	 * 
	 * @param sFileName file name
	 * @return number of lines in file to be read or -1 if an error occurred.
	 * @throws IOException
	 */
	protected final int loadData_TestLinesToBeRead(final String sFileName) 
	{
		int iNumberOfLinesInFile = -1;
		
		try {
		    BufferedReader brFile = null;
		    
		    if (this.getClass().getClassLoader().getResource(sFileName) != null)
		    {
		    	brFile = new BufferedReader(
		    		new InputStreamReader(this.getClass().getClassLoader().
		    				getResource(sFileName).openStream()));
		    }
		    else
		    {
		    	brFile = new BufferedReader(new FileReader(sFileName));
		    }			   
		    iNumberOfLinesInFile = 
		    	loadData_TestLinesToBeRead( brFile );
		    
		    brFile.close();
		}
		catch (IOException ioe) {
			refGeneralManager.getSingelton().logMsg(
					"AbstractLoader: IO-error line=[" + iLineInFile +
					"] while testing file: " + ioe.toString(),
					LoggerType.MINOR_ERROR );
		    
			 iLinesInFileToBeRead = -1;
		    return -1;
		    //System.exit(1);
		}
		catch (Exception ex) {
			refGeneralManager.getSingelton().logMsg(
					"AbstractLoader: ERROR line=[" + iLineInFile +
					"] while testing file: " + ex.toString(),
					LoggerType.ERROR_ONLY );
			
			ex.printStackTrace();
			iLinesInFileToBeRead = -1;
			
		    return -1;
		}	
		
		iLinesInFileToBeRead = iNumberOfLinesInFile;
		
		return iNumberOfLinesInFile;
	}
	
	
	/**
	 * Reads the file and counts the numbers of lines to be read.
	 * 
	 * @param brFile file handler
	 * @return number of liens in file to be read.
	 * @throws IOException
	 */
	protected final int loadData_TestLinesToBeRead(BufferedReader brFile) 
		throws IOException  
	{
		
		int iCountLinesToBeRead = 0;
		int iCountLines = 1;
		
		 while ( (( brFile.readLine()) != null)&&
		    		( iCountLines <= iStopParsingAtLine) )  
		    {
				if( iCountLines > this.iStartParsingAtLine ){
					iCountLinesToBeRead++;
					
					
				} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
				
				iCountLines++;
		    }
		 
		 iLinesInFileToBeRead = iCountLinesToBeRead;
		 
		 return iCountLinesToBeRead;
	}
	
	/**
	 * Get the nubmer of lines to be read from the current file.
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#iLinesInFileToBeRead
	 * @see org.geneview.core.parser.ascii.AbstractLoader#loadData_TestLinesToBeRead(BufferedReader)
	 * @see org.geneview.core.parser.ascii.AbstractLoader#loadData_TestLinesToBeRead(String)
	 * 
	 * @return -1 if invalid or number of liens to be read
	 */
	protected final int getLinesInCurrentFileToBeRead() {
		return iLinesInFileToBeRead;
	}
	
	public boolean loadData() {					
		
		int iNumberOfLinesInFile = -1;
		
		if ( bRequiredSizeOfReadableLines ) 
		{
			iNumberOfLinesInFile =
				this.loadData_TestLinesToBeRead( sFileName );			   			  
		}		
		
		try {
		    BufferedReader brFile = null;
		    
		    if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null)
		    {
		    	brFile = new BufferedReader(
		    		new InputStreamReader(
		    				this.getClass().getClassLoader().getResourceAsStream(sFileName)));
		    }
		    else
		    {
		    	brFile = new BufferedReader(new FileReader(sFileName));
		    }		   
		    		
		    // sample line: 1110 Kybernetik
		    refGeneralManager.getSingelton().logMsg(
		    		"Read file \""+ 
				       this.sFileName + "\" ...",
				       LoggerType.VERBOSE );

		    this.loadDataParseFile( brFile, iNumberOfLinesInFile );
		    

		    if ( brFile != null ) {
		    	brFile.close();
		    }
		    
		   
		    
		    // sample line: E016|Zentrale Medienstelle|Media Centre|00
		    
		    refGeneralManager.getSingelton().logMsg(
		    		" read file \""+ 
				       this.sFileName + "\"  ....  [DONE]",
				     LoggerType.STATUS );

		    copyDataToInternalDataStructures();
		    
		 
		    
		    refGeneralManager.getSingelton().logMsg(
		    		"  Read file \""+ 
				       this.sFileName + "\" .... copy to storage ...[DONE]",
				       LoggerType.VERBOSE_EXTRA );
		    
		}
		catch (IOException ioe) {
			refGeneralManager.getSingelton().logMsg(
					"MicroArrayLoader: IO-error line=[" + iLineInFile +
					"] while parsing: " + ioe.toString(),
					LoggerType.MINOR_ERROR );
		    
		    return false;
		    //System.exit(1);
		}
		catch (Exception ex) {
			refGeneralManager.getSingelton().logMsg(
					"MicroArrayLoader: ERROR line=[" + iLineInFile +
					"] while parsing: " + ex.toString(),
					LoggerType.ERROR_ONLY );
			
			ex.printStackTrace();
		    return false;
		}		
		
		return true;
	}

	/**
	 * Sets the progress bar to iPosition [0..199] and 
	 * define the number of incremetns iStepsTill100_Percent [>0] needed to 
	 * the the progressbar to iMaxProgressBarPosition
	 * 
	 * Call progressBarIncrement() to incremetn the progressabr using the settings.
	 * 
	 * Use progressBarResetTitle() to reset the progressbar to the previouse position.
	 * 
	 * set fProgressBarIndex = iPosition
	 * set fProgressBarInc = (200 - iPosition) / iStepsTill100_Percent
	 * 
	 * @param sText new text for progress bar
	 * @param iPosition range [0..200]
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#fProgressBarInc
	 * @see org.geneview.core.parser.ascii.AbstractLoader#fProgressBarIndex
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarStoredIncrement()
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarResetTitle()
	 */
	protected final void progressBarSetStoreInitTitle(final String sText, 
			final int iCurrentProgressBarPosition,
			final int iMaxProgressBarPosition,
			final int iStepsTill100_Percent ) {
		
		int iMaxPosition = iMaxProgressBarPosition;
		
		if ( iMaxProgressBarPosition > SWTGUIManager.PROGRESSBAR_MAXIMUM  ) 
		{
			iMaxPosition = SWTGUIManager.PROGRESSBAR_MAXIMUM;
		}
		
		assert sText != null : "can not init text with 'null'";
		assert iMaxPosition > iCurrentProgressBarPosition : "iMaxPosition is smaller than iCurrentProgressBarPosition !";
		
		iProgressBarLastPosition = refSWTGUIManager.getLoadingProgressBarPercentage();
		sLastProgressBarText = refSWTGUIManager.setLoadingProgressBarTitle(
				"load " + this.getFileName(), 
				iCurrentProgressBarPosition);	
		
		iProgressBarCurrentPosition = iCurrentProgressBarPosition;		
		fProgressBarIndex = iCurrentProgressBarPosition;
		fProgressBarInc = (float) (iMaxPosition - iCurrentProgressBarPosition) / 
			(float) iStepsTill100_Percent;
	}
	
	/**
	 * 
	 * @param sText new text
	 * @param iCurrentProgressBarPosition new progress bar position
	 * @param iStepsTill100_Percent number of incremtens to reach 100 %
	 * 
	 * @see org.geneview.core.parser.ascii.AbstractLoader#progressBarSetStoreInitTitle(String, int, int, int)
	 */
	protected final void progressBarSetStoreInitTitle(final String sText, 
			final int iCurrentProgressBarPosition,
			final int iStepsTill100_Percent ) {
		/* Multi Threaded Version: remove next lines or make call thread safe */
		if ( !bUseMultipleThreads )
		{
			progressBarSetStoreInitTitle(sText,
					iCurrentProgressBarPosition,
					SWTGUIManager.PROGRESSBAR_MAXIMUM ,
					iStepsTill100_Percent);
		}
	}
	
	public final void progressBarStoredIncrement() {
		/* Multi Threaded Version: remove next lines or make call thread safe */
		if ( !bUseMultipleThreads )
		{
			fProgressBarIndex += fProgressBarInc;
			
			if ( (int)fProgressBarIndex != iProgressBarCurrentPosition ) {
				iProgressBarCurrentPosition = (int)fProgressBarIndex;			
				refSWTGUIManager.setLoadingProgressBarPercentage( iProgressBarCurrentPosition );
			}
		}
	}
	
	/**
	 * Reset to the previouse
	 *
	 */
	protected final void progressBarResetTitle() {
		/* Multi Threaded Version: remove next lines or make call thread safe */		
		if ( !bUseMultipleThreads )
		{
			refSWTGUIManager.setLoadingProgressBarTitle(sLastProgressBarText,iProgressBarLastPosition);
			
			assert fProgressBarInc != 0.0f : "call progressBarResetTitle() without calling progressBarSetStoreInitTitle() first!";
			
			fProgressBarInc = 0.0f;
			fProgressBarIndex = iProgressBarCurrentPosition;		
			iProgressBarCurrentPosition = iProgressBarLastPosition;
		}
	}
	
	/**
	 * 
	 * @param iTicks must be in the range of: currentPercentage - [0..200]
	 */
	protected final void progressBarIncrement( int iTicks ) {
		/* Multi Threaded Version: remove next lines or make call thread safe */
		if ( !bUseMultipleThreads )
		{
			iProgressBarCurrentPosition += iTicks;
			refSWTGUIManager.setLoadingProgressBarPercentage( iProgressBarCurrentPosition );
		}
	}
	
	protected final int progressBarCurrentPosition() {
		return this.iProgressBarCurrentPosition;
	}
	


	
	/**
	 * @param brFile input stream
	 * @param iNumberOfLinesInFile optional, number of lines in file, only valid if bRequiredSizeOfReadableLines==true
	 */
	protected abstract int loadDataParseFile( BufferedReader brFile,
			final int iNumberOfLinesInFile )
		throws IOException; 
	
	protected abstract boolean copyDataToInternalDataStructures();
	

}
