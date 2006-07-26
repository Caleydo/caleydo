/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.base;

import cerberus.command.CommandInterface;
import cerberus.data.AbstractUniqueItem;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class CommandAbstractBase 
	extends AbstractUniqueItem
	implements CommandInterface {

	protected int iCmdCollectionId;
	
	/**
	 * Defautl constructor, collectionId ist set to -1.
	 */
	public CommandAbstractBase() {
		super( -1 );
	}
	
	/**
	 * Set CollectionId using this constructor.
	 * 
	 * @param iSetCmdCollectionId set collection Id
	 */
	public CommandAbstractBase( int iSetCmdCollectionId) {
		super( iSetCmdCollectionId );
	}

	
	/* (non-Javadoc)
	 * @see prometheus.command.CommandInterface#isEqualType(prometheus.command.CommandInterface)
	 */
	public final boolean isEqualType(CommandInterface compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}

}
