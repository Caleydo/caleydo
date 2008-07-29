package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 *
 * @see org.caleydo.core.data.collection.IStorage
 */
public class CmdDataCreateStorage 
extends ACmdCreate_IdTargetLabel {

	
	ArrayList<String> sAlParserControlTypes;
	
	/**
	 * Constructor.
	 */
	public CmdDataCreateStorage( 
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) 
	{		
		super(generalManager, commandManager,
				commandQueueSaxType);
	}

	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IStorageManager storageManager = 
			generalManager.getStorageManager();
		
		IStorage storage = (IStorage) storageManager.createStorage(
				ManagerObjectType.STORAGE);
		
		storage.setId( iUniqueId );
		storage.setLabel( sLabel );			
		
				
		storageManager.registerItem(storage, 
				storage.getId());

		generalManager.getLogger().log(Level.INFO, "Created Storage with ID: " + iUniqueId);		
		commandManager.runDoCommand(this);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException 
	{		
		commandManager.runUndoCommand(this);
	}	

	public void setParameterHandler( final IParameterHandler parameterHandler ) 
	{		
		super.setParameterHandler(parameterHandler);
	}

	public void setAttributes(int iStorageID) 
	{
		iUniqueId = iStorageID;
	}
}
