package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.general.GeneralManager;
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
	extends ACmdCreational<ISet>
{
	private ESetType setType;

	private ArrayList<Integer> iAlStorageIDs;

	private ArrayList<Integer> iAlVirtualArrayIDs;

	/**
	 * Constructor.
	 */
	public CmdDataCreateSet(final ECommandType cmdType)
	{
		super(cmdType);

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
	public void doCommand()
	{
		ISetManager setManager = generalManager.getSetManager();

		createdObject = setManager.createSet(setType);
		createdObject.setLabel(sLabel);

		if (iExternalID != -1)
			generalManager.getIDManager().mapInternalToExternalID(createdObject.getID(),
					iExternalID);

		fillSets(createdObject);

		generalManager.getLogger().log(Level.INFO,
				"New Set with ID " + iExternalID + " created.");

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand()
	{
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		/**
		 * Read TAG_ATTRIBUTE2 "attrib2" for storage!
		 */

		/**
		 * Separate "text1@text2"
		 */
		StringTokenizer strToken_StorageBlock = new StringTokenizer(parameterHandler
				.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey()),
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

		// Convert external IDs from XML file to internal IDs
		iAlStorageIDs = GeneralManager.get().getIDManager().convertExternalToInternalIDs(
				iAlStorageIDs);

		/**
		 * read "attrib3" key ...
		 */
		String sAttrib3 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE3
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

	public void setAttributes(ArrayList<Integer> iAlVirtualArrayIDs,
			ArrayList<Integer> iAlStorageIDs, ESetType setType)
	{
		this.setType = setType;
		this.iAlStorageIDs = iAlStorageIDs;
		this.iAlVirtualArrayIDs = iAlVirtualArrayIDs;
	}
}
