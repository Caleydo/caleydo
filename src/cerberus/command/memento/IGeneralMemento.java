/*
 * Project: GenView
 *  
 */
package cerberus.command.memento;

//import cerberus.manager.IMementoManager;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * One base implementation of the IMemento interface.
 * 
 * Desing Patern "IMemento"
 * 
 * @see cerberus.manager.IMementoManager
 * 
 * @author Michael Kalkusch
 *
 */
public class IGeneralMemento 
	implements IMemento {

	
	/**
	 * Stores all inforamtion needed to reset an object to a previouse state.
	 * Must only be used by the object, that created the IMemento!
	 * Object which creates the IMemento must derive a class from MementoStateInterface
	 * and cast to the derived class once the obejct revices the MementoStateInterface-object.
	 * 
	 */
	protected IMementoState refMementoState = null;
	//TODO: write TEST for IMemento objects!

	/**
	 * Reference to creator of IMemento.
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
	public IGeneralMemento(final Object setMementoCreator) {
		assert setMementoCreator != null: "IMemento(Object)Can not creat a memento with null-pointer as parent";
		
		refMementoCreator = setMementoCreator;
	}

	/**
	 * Constructor.
	 * 
	 * @param setMementoCreator creator of memento
	 * @param setMementoData data from the creator object
	 */
	public IGeneralMemento(final Object setMementoCreator,
			final IMementoState setMementoData) {
		assert setMementoCreator != null: "IMemento(Object,MementoType) Can not creat a memento with null-pointer as parent";
		
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
	public IGeneralMemento(final Object setMementoCreator, 
			final IMementoState setMementoData,
			final MementoType setMementoType) {
		assert setMementoCreator != null: "IMemento(Object,MementoType,IMementoState)Can not creat a memento with null-pointer as parent";
		
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
			final IMementoState setMemetoState) 
	throws CerberusRuntimeException 
	{
		if ( refMementoCreator != setMementoCreator) {
			throw new CerberusRuntimeException("IGeneralMemento.setMementoState() failed due to setting data not from the creator of the IMemento.",
					CerberusExceptionType.MEMENTO );
		}
		//assert  refMementoCreator == setMementoCreator: "IGeneralMemento.setMementoState() failed due to setting data not from the creator of the IMemento.";
		
		this.refMementoState = setMemetoState;
	}

	
	/* (non-Javadoc)
	 * @see prometheus.command.memento.MementoPrivateLayer#getMementoState()
	 */
	/* (non-Javadoc)
	 * @see prometheus.command.memento.Memento#getMementoState(java.lang.Object)
	 */
	public IMementoState getMementoState(final Object setMementoCreator) {		
		assert setMementoCreator != refMementoCreator :"getMemento() can only ba called by the creator of the memento!";
		
		return refMementoState;
	}
	
	/**
	 * Get the estimated size of a IMemento 
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
