/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.memento;

//import cerberus.manager.MementoManager;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * One base implementation of the Memento interface.
 * 
 * Desing Patern "Memento"
 * 
 * @see cerberus.manager.MementoManager
 * 
 * @author Michael Kalkusch
 *
 */
public class GeneralMemento 
	implements Memento {

	
	/**
	 * Stores all inforamtion needed to reset an object to a previouse state.
	 * Must only be used by the object, that created the Memento!
	 * Object which creates the Memento must derive a class from MementoStateInterface
	 * and cast to the derived class once the obejct revices the MementoStateInterface-object.
	 * 
	 */
	protected MementoState refMementoState = null;
	//TODO: write TEST for Memento objects!

	/**
	 * Reference to creator of Memento.
	 */
	final protected Object refMementoCreator;
	
	/**
	 * Define type of memento
	 */
	protected MementoType refMementoType = MementoType.GENERAL;
	
	/**
	 * Constructor.
	 * 
	 * @param setMementoCreator creator of memento
	 */
	public GeneralMemento(final Object setMementoCreator) {
		assert setMementoCreator != null: "Memento(Object)Can not creat a memento with null-pointer as parent";
		
		refMementoCreator = setMementoCreator;
	}

	/**
	 * Constructor.
	 * 
	 * @param setMementoCreator creator of memento
	 * @param setMementoData data from the creator object
	 */
	public GeneralMemento(final Object setMementoCreator,
			final MementoState setMementoData) {
		assert setMementoCreator != null: "Memento(Object,MementoType) Can not creat a memento with null-pointer as parent";
		
		refMementoCreator = setMementoCreator;
		refMementoState = setMementoData;		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param setMementoCreator creator of memento
	 * @param setMementoData data from the creator object
	 * @param setMementoType type of memento; used for memento manger
	 */
	public GeneralMemento(final Object setMementoCreator, 
			final MementoState setMementoData,
			final MementoType setMementoType) {
		assert setMementoCreator != null: "Memento(Object,MementoType,MementoState)Can not creat a memento with null-pointer as parent";
		
		refMementoCreator = setMementoCreator;
		refMementoType = setMementoType;
		refMementoState = setMementoData;		
	}

	/* (non-Javadoc)
	 * @see prometheus.command.memento.Memento#getMementoType()
	 */
	public final MementoType getMementoType() {
		return refMementoType;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.command.memento.Memento#setMementoState(java.lang.Object, prometheus.command.memento.MementoState)
	 */
	public void setMementoState(final Object setMementoCreator,
			final MementoState setMemetoState) 
	throws CerberusRuntimeException 
	{
		if ( refMementoCreator != setMementoCreator) {
			throw new CerberusRuntimeException("GeneralMemento.setMementoState() failed due to setting data not from the creator of the Memento.",
					CerberusExceptionType.MEMENTO );
		}
		//assert  refMementoCreator == setMementoCreator: "GeneralMemento.setMementoState() failed due to setting data not from the creator of the Memento.";
		
		this.refMementoState = setMemetoState;
	}

	
	/* (non-Javadoc)
	 * @see prometheus.command.memento.MementoPrivateLayer#getMementoState()
	 */
	/* (non-Javadoc)
	 * @see prometheus.command.memento.Memento#getMementoState(java.lang.Object)
	 */
	public MementoState getMementoState(final Object setMementoCreator) {		
		assert setMementoCreator != refMementoCreator :"getMemento() can only ba called by the creator of the memento!";
		
		return refMementoState;
	}
	
	/**
	 * Get the estimated size of a Memento 
	 * 
	 * @return size in 4*Byte
	 */
	//public int getSizeMemento();
	
	/* (non-Javadoc)
	 * @see prometheus.command.memento.Memento#toString()
	 */
	public String toString() {
		return refMementoState.toString();
	}
	
	/* (non-Javadoc)
	 * @see prometheus.command.memento.Memento#toStringRecursive()
	 */
	public String toStringRecursive() {
		return refMementoState.toStringRecursive();
	}

}
