/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.base;

import cerberus.command.ICommand;
import cerberus.data.AUniqueItem;
import cerberus.manager.ICommandManager;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACommand 
	extends AUniqueItem
	implements ICommand {

	/**
	 * Reference ot ICommandManager
	 * 
	 * Used to
	 * 
	 * @see cerberus.manager.ICommandManager#runDoCommand(ICommand)
	 * @see cerberus.manager.ICommandManager#runUndoCommand(ICommand)
	 * @see cerberus.command.ICommand#doCommand()
	 * @see cerberus.command.ICommand#undoCommand()
	 * 
	 */
	protected final ICommandManager refCommandManager;
	
	/**
	 * Default constructor, collectionId is set to -1.
	 */
	public ACommand(final ICommandManager refCommandManager) {
		super( -1 );
		
		this.refCommandManager = refCommandManager;
	}
	
	/**
	 * ISet CollectionId using this constructor.
	 * 
	 * @param iSetCmdCollectionId set collection Id
	 */
	public ACommand( int iSetCmdCollectionId) {
		super( iSetCmdCollectionId );
		
		refCommandManager = null;
	}

	
	/**
	 * @see prometheus.command.ICommand#isEqualType(prometheus.command.ICommand)
	 *  * @see base.AManagedCmd#isEqualType(cerberus.command.ICommand)
	 */
	public final boolean isEqualType(ICommand compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}

	public void setParameterHandler( IParameterHandler phHandler) {
		
	}

}
