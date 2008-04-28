package org.caleydo.core.command.memento.sample;

import java.io.Serializable;

import org.caleydo.core.command.memento.IMementoState;

/**
 * Sample code for a IMemento state.
 * 
 * Implementation of Design Pattern "IMemento"
 * 
 * \sa SampleMementoApplicationCmd
 * 
 * @author Michael Kalkusch
 *
 */
public class SampleMementoState implements IMementoState,Serializable {

	static final long serialVersionUID = 8300;
	
	/**
	 * Data stored, in order to be able to restore the state of the SampleMementoObject.
	 * 
	 * \sa SampleMementoObject::iMyData
	 */
	private int iData;
	
	/**
	 * Additional data, that is accessed not via the constructor.
	 */
	private float fData = 0;
	
	/**
	 * Constructor.
	 */
	public SampleMementoState( int iSetData ) {
		iData = iSetData;
	}
	
	/**
	 * ISet any data required to restore the state of the object...
	 */
	public void setPostData( float fSetData ) {
		fData = fSetData;
	}
	
	/**
	 * Get any data required to restore the state of the object...
	 */
	public int getData() {
		return iData;
	}
	
	/**
	 * Get any data required to restore the state of the object...
	 */
	public float getPostData() {
		return fData;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String("{data="+iData+" postData=" + fData + "}");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.command.memento.MementoState#toStringRecursive()
	 */
	public String toStringRecursive() {
		return new String("[" + this.toString() + " no recursive data]");
	}

}
