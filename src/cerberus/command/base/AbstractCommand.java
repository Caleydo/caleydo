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
public abstract class AbstractCommand 
	extends AbstractUniqueItem
	implements CommandInterface {

	/**
	 * Defautl constructor, collectionId ist set to -1.
	 */
	public AbstractCommand() {
		super( -1 );
	}
	
	/**
	 * Set CollectionId using this constructor.
	 * 
	 * @param iSetCmdCollectionId set collection Id
	 */
	public AbstractCommand( int iSetCmdCollectionId) {
		super( iSetCmdCollectionId );
	}

	
	/**
	 * @see prometheus.command.CommandInterface#isEqualType(prometheus.command.CommandInterface)
	 *  * @see base.AbstractManagedCommand#isEqualType(cerberus.command.CommandInterface)
	 */
	public final boolean isEqualType(CommandInterface compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}

}
