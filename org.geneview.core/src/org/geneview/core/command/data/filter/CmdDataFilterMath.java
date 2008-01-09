package org.geneview.core.command.data.filter;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttr;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.StorageType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;

/**
 * @author Marc Streit
 * @author Alexander Lex
 *
 *
 */
public class CmdDataFilterMath 
extends ACmdCreate_IdTargetLabelAttr {

	public enum EDataFilterMathType {
		LIN_2_LOG,
		LOG_2_LIN,
		NORMALIZE
	}
	
//	public enum EStorageHandlingType 
//	{
//		OVERWRITE,
//		COPY
//	}
//		
	private ArrayList<Integer> iAlSrcStorageId;
	private ArrayList<Integer> iAlTargetStorageId = null;
	
	private EDataFilterMathType dataFilterMathType;
	
	
	/**
	 * Constructor.
	 * 
	 * TODO: implemented only for float
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdDataFilterMath(IGeneralManager refGeneralManager,
			ICommandManager refCommandManager,
			CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
		
		iAlSrcStorageId = new ArrayList<Integer>();
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		dataFilterMathType = EDataFilterMathType.valueOf(sAttribute1);
		
		/**
		 * Fill storage IDs
		 */
		StringTokenizer strToken_DataTypes = 
			new StringTokenizer( sAttribute2,
					IGeneralManager.sDelimiter_Parser_DataItems); 

		while ( strToken_DataTypes.hasMoreTokens() ) {
			iAlSrcStorageId.add(new Integer(strToken_DataTypes.nextToken()));
		}
	}
	
	
	/**
	 * Writes the result of the filter specified into the targetStorages
	 * Obviously the length of the target and the src array list has to be the same
	 * The result of the storage at index n is written to the corresponding index n in the target array
	 * 
	 * @param dataFilterMathType The type of operation
	 * @param iAlSrcStorageId The source storage ids.
	 * @param iAlTargetStorageId The target storage ids.
	 */
	public void setAttributes(EDataFilterMathType dataFilterMathType, 
			ArrayList<Integer> iAlSrcStorageId,
			ArrayList<Integer> iAlTargetStorageId)
	{
		if (iAlSrcStorageId.size() != iAlTargetStorageId.size())
		{
			throw new GeneViewRuntimeException("Size of specified src and target storage are different", GeneViewRuntimeExceptionType.COMMAND);
		}
		this.iAlTargetStorageId = iAlTargetStorageId;
		setAttributes(dataFilterMathType, iAlSrcStorageId);		
	}
	
	/**
	 * Overwrites the specified storage with the results of the operation 
	 * 
	 * @param dataFilterMathType The type of operation
	 * @param iAlSrcStorageID The source storage ids. This storage is overwritten with the result.
	 */
	public void setAttributes(EDataFilterMathType dataFilterMathType, ArrayList<Integer> iAlSrcStorageID)
	{
		this.dataFilterMathType = dataFilterMathType;
		this.iAlSrcStorageId = iAlSrcStorageID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {

		//Iterator<Integer> iterStorageId = iAlSrcStorageId.iterator();
		IStorage tmpStorage = null;
		
		
		for(int iCount = 0; iCount < iAlSrcStorageId.size(); iCount++)
		//while (iterStorageId.hasNext())
		{
			tmpStorage = refGeneralManager.getSingelton().getStorageManager()
					.getItemStorage(iAlSrcStorageId.get(iCount));
		
			if (tmpStorage == null)
				continue;	
			
			if (dataFilterMathType.equals(EDataFilterMathType.LIN_2_LOG))
			{
				if(iAlTargetStorageId == null)
						tmpStorage.setArrayFloat(calculateLinToLog(tmpStorage));
				else
				{
					refGeneralManager.getSingelton().getStorageManager()
						.getItemStorage(iAlTargetStorageId.get(iCount))
						.setArrayFloat(calculateLinToLog(tmpStorage));
				}
			}
			else if (dataFilterMathType.equals(EDataFilterMathType.LOG_2_LIN))
			{
				if(iAlTargetStorageId == null)
					tmpStorage.setArrayFloat(calculateLogToLin(tmpStorage));
				else
				{	
					refGeneralManager.getSingelton().getStorageManager()
						.getItemStorage(iAlTargetStorageId.get(iCount))
						.setArrayFloat(calculateLogToLin(tmpStorage));
				}				
			}
			else if (dataFilterMathType.equals(EDataFilterMathType.NORMALIZE))
			{
				if(iAlTargetStorageId == null)
				{
					tmpStorage.getWriteToken();
					tmpStorage.setArrayFloat(normalize(tmpStorage));
					tmpStorage.returnWriteToken();
				}
				else
				{
					IStorage targetStorage = refGeneralManager.getSingelton().getStorageManager()
						.getItemStorage(iAlTargetStorageId.get(iCount));
						
						targetStorage.getWriteToken();					
						targetStorage.setArrayFloat(normalize(tmpStorage));
						targetStorage.returnWriteToken();
				}
				
			}
		
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {

		// TODO Auto-generated method stub
		
	}
	/**
	 * TODO: Different data types need to be implemented. Clear way of handling the stuff is neccesary
	 * 
	 * @param tmpStorage
	 * @return
	 */
	private float[] calculateLinToLog(IStorage tmpStorage)
	{
		
		float[] fArTmpTarget = new float[tmpStorage.getSize(StorageType.FLOAT)];

		//float[] test = tmpStorage.getArrayInt().toArray(new float[]);
		
		if (tmpStorage.getSize(StorageType.FLOAT) > 1)
		{
			float[] fArTmpSrc = tmpStorage.getArrayFloat();
			
				
			float fTmp;
			for(int index = 0; index < fArTmpSrc.length; index++)
			{
				fTmp = fArTmpSrc[index];
				
				//TODO: what about negative values
				//TODO: what about values between 0 and 1
				// Shifting space so that all values are >= 1
				fTmp += 1;
				
				// Clip data why?
//				if (fTmp <= 1)
//				{
//					fTmp = 1f;
//				}
//				else if(fTmp >= 1000)
//				{
//					fTmp = 1000f;
//				}
				
				fArTmpTarget[index] = (float) Math.log10(fTmp);					
			}			
		}
		return fArTmpTarget;
	}
	
	private float[] calculateLogToLin(IStorage tmpStorage)
	{
		float[] fArTmpTarget = new float[tmpStorage.getSize(StorageType.FLOAT)];
		
		if (tmpStorage.getSize(StorageType.FLOAT) > 1)
		{
			float[] fArTmpSrc = tmpStorage.getArrayFloat();

		
			for(int iCount = 0; iCount < fArTmpSrc.length; iCount++)
			{	
				fArTmpTarget[iCount] = (float) Math.pow(10, fArTmpSrc[iCount]);
			}	
		
		}
		
		return fArTmpTarget;
	}
	
	//TODO: Normalize to values other than 0-1
	private float[] normalize(IStorage tmpStorage)
	{
		float[] fArTmpTarget = new float[tmpStorage.getSize(StorageType.FLOAT)];
		
		if (tmpStorage.getSize(StorageType.FLOAT) > 1)
		{
			float[] fArTmpSrc = tmpStorage.getArrayFloat();
			
			for (int iCount = 0; iCount < fArTmpSrc.length; iCount++)
			{
				
				//if (tmpStorage.getMinFloat() >= 0)
				//{
					fArTmpTarget[iCount] = (fArTmpSrc[iCount] - tmpStorage.getMinFloat()) / 
										(tmpStorage.getMaxFloat() - tmpStorage.getMinFloat());
				//}
				//else
				//{
				//	fArTmpTarget[iCount] = (fArTmpSrc[iCount] - )
				//}
			}			
		}		
		return fArTmpTarget;		
	}

}
