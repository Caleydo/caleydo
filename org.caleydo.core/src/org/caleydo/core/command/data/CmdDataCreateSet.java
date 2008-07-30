package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateSet
	extends ACmdCreate_IdTargetLabelAttrDetail
{

	private ESetType setType;

	private ArrayList<Integer> iAlStorageIDs;

	private ArrayList<Integer> iAlVirtualArrayIDs;

	/**
	 * Constructor.
	 */
	public CmdDataCreateSet(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);

		iAlStorageIDs = new ArrayList<Integer>();
		iAlVirtualArrayIDs = new ArrayList<Integer>();

		setType = ESetType.UNSPECIFIED;
	}

	private void fillSets(ISet newSet)
	{

		if (iAlStorageIDs.isEmpty())// ||
		// ( iAlVirtualArrayIDs.isEmpty()))
		{
			throw new CaleydoRuntimeException("No data available for creating storage.",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		}

		for (int iStorageID : iAlStorageIDs)
		{
			newSet.addStorage(iStorageID);
		}

	}

	/**
	 * Load data from file using a token pattern.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		ISetManager setManager = generalManager.getSetManager();

		ISet set = setManager.createSet(setType);
		set.setId(iUniqueId);
		set.setLabel(sLabel);

		setManager.registerItem(set, iUniqueId);

		fillSets(set);

		generalManager.getLogger().log(Level.INFO,
				"New Set with ID " + iUniqueId + " created.");

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#
	 * setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		super.setParameterHandler(parameterHandler);

		/**
		 * Separate "text1@text2"
		 */
		// StringTokenizer strToken_VirtualArrayBlock =
		// new StringTokenizer(
		// parameterHandler.getValueString(
		// CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),
		// IGeneralManager.sDelimiter_Paser_DataItemBlock);
		//		
		// while ( strToken_VirtualArrayBlock.hasMoreTokens() )
		// {
		// /**
		// * Separate "id1 id2 .."
		// */
		// StringTokenizer strToken_VirtualArrayId =
		// new StringTokenizer(
		// strToken_VirtualArrayBlock.nextToken(),
		// IGeneralManager.sDelimiter_Parser_DataItems);
		//			
		// /**
		// * Create buffer list...
		// */
		// LinkedList<String> llRefVirtualArray_1dim =
		// new LinkedList<String> ();
		//			
		// while ( strToken_VirtualArrayId.hasMoreTokens() )
		// {
		// llRefVirtualArray_1dim.addLast( strToken_VirtualArrayId.nextToken()
		// );
		// } // while ( strToken_VirtualArrayId.hasMoreTokens() )
		//			
		// if ( ! llRefVirtualArray_1dim.isEmpty() ) {
		// /**
		// * insert this list into global list..
		// */
		// sAlVirtualArrayIDs.addLast( llRefVirtualArray_1dim );
		// }
		// else
		// {
		// generalManager.getLogger().log(Level.SEVERE,
		// "Error in provided list of virtual arrays during creation of set.");
		//				
		// bErrorOnLoadingXMLData = true;
		// }
		//			
		// } // while ( strToken_VirtualArrayBlock.hasMoreTokens() )
		//		
		// strToken_VirtualArrayBlock = null;
		/**
		 * Read TAG_ATTRIBUTE2 "attrib2" for storage!
		 */

		/**
		 * Separate "text1@text2"
		 */
		StringTokenizer strToken_StorageBlock = new StringTokenizer(parameterHandler
				.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey()),
				IGeneralManager.sDelimiter_Paser_DataItemBlock);

		while (strToken_StorageBlock.hasMoreTokens())
		{
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_StorageId = new StringTokenizer(strToken_StorageBlock
					.nextToken(), IGeneralManager.sDelimiter_Parser_DataItems);

			while (strToken_StorageId.hasMoreTokens())
			{
				iAlStorageIDs.add(StringConversionTool.convertStringToInt(strToken_StorageId
						.nextToken(), -1));
			}
		}

		/**
		 * read "attrib3" key ...
		 */
		String sAttrib3 = parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE3
				.getXmlKey());

		if (sAttrib3.length() > 0)
		{
			setType = ESetType.valueOf(sAttrib3);
		}
		else
		{
			setType = ESetType.UNSPECIFIED;
		}
	}

	public void setAttributes(int iSetId, ArrayList<Integer> iAlVirtualArrayIDs,
			ArrayList<Integer> iAlStorageIDs, ESetType setType)
	{

		this.setType = setType;
		this.iAlStorageIDs = iAlStorageIDs;
		this.iAlVirtualArrayIDs = iAlVirtualArrayIDs;
		this.iUniqueId = iSetId;
	}

}
