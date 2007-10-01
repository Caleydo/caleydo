package org.geneview.core.command.data.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttr;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.StorageType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;


public class CmdDataFilterMath 
extends ACmdCreate_IdTargetLabelAttr {

	enum EDataFilterType {
		LIN_2_LOG,
		LOG_2_LIN,
	}
	
	private ArrayList<Integer> iAlStorageId;
	
	private EDataFilterType dataFilterType;
	
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdDataFilterMath(IGeneralManager refGeneralManager,
			ICommandManager refCommandManager,
			CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
		
		iAlStorageId = new ArrayList<Integer>();
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		dataFilterType = EDataFilterType.valueOf(sAttribute1);
		
		/**
		 * Fill storage IDs
		 */
		StringTokenizer strToken_DataTypes = 
			new StringTokenizer( sAttribute2,
					IGeneralManager.sDelimiter_Parser_DataItems); 

		while ( strToken_DataTypes.hasMoreTokens() ) {
			iAlStorageId.add(new Integer(strToken_DataTypes.nextToken()));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {

		Iterator<Integer> iterStorageId = iAlStorageId.iterator();
		IStorage tmpStorage = null;
		
		while (iterStorageId.hasNext())
		{
			tmpStorage = refGeneralManager.getSingelton().getStorageManager()
					.getItemStorage(iterStorageId.next());

			if (tmpStorage == null)
				continue;

			tmpStorage.getWriteToken();

			if (tmpStorage.getSize(StorageType.FLOAT) > 1)
			{
				float[] fArTmp = tmpStorage.getArrayFloat();
	
				if (dataFilterType.equals(EDataFilterType.LIN_2_LOG))
				{
					float fTmp;
					for(int index = 0; index < fArTmp.length; index++)
					{
						fTmp = fArTmp[index];
						
						// Shifting space so that all values are >= 1
						fTmp += 1;
						
						// Clip data
						if (fTmp <= 1)
						{
							fTmp = 1f;
						}
						else if(fTmp >= 1000)
						{
							fTmp = 1000f;
						}
						
						fArTmp[index] = (float) Math.log10(fTmp);			
					}					
				}
				else if (dataFilterType.equals(EDataFilterType.LOG_2_LIN))
				{
					for(int index = 0; index < fArTmp.length; index++)
					{
						fArTmp[index] = (float) Math.pow(10, fArTmp[index]);
					}		
				}
			}

			tmpStorage.returnWriteToken();
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

}
