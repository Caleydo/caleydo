package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract base class for all commands.
 * 
 * @author Michael Kalkusch
 * @author Marc  Streit
 *
 */
public abstract class ACommand 
extends AUniqueManagedObject
implements ICommand {

	/**
	 * Reference to ICommandManager
	 */
	protected final ICommandManager commandManager;
	
	private CommandQueueSaxType commandQueueSaxType;
	
	/**
	 * Constructor.
	 */
	public ACommand(final int iSetCollectionId,
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		
		super( iSetCollectionId, generalManager );
		
		this.commandManager = commandManager;
		this.commandQueueSaxType = commandQueueSaxType;
	}

	public final boolean isEqualType(ICommand compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueManagedObject#getBaseType()
	 */
	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.COMMAND;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getCommandType()
	 */
	public final CommandQueueSaxType getCommandType() {
		return commandQueueSaxType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler phHandler) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getInfoText()
	 */
	public String getInfoText() {
		
		return commandQueueSaxType.getInfoText() + " [" + this.getId() + "]";
	}
	
	protected final void setCommandQueueSaxType(final CommandQueueSaxType commandQueueSaxType) {
		this.commandQueueSaxType  =commandQueueSaxType;
	}
}
