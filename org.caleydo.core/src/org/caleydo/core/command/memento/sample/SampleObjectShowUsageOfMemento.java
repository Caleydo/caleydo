package org.caleydo.core.command.memento.sample;

import org.caleydo.core.command.memento.IGeneralMemento;
import org.caleydo.core.command.memento.IMemento;
import org.caleydo.core.command.memento.IMementoCreator;

/**
 * Sample code for IMemento objects. Implementation of Desing Pattern "IMemento" \sa
 * SampleMementoApplicationCmd
 * 
 * @author Michael Kalkusch
 */
public class SampleObjectShowUsageOfMemento
	implements IMementoCreator {

	/**
	 * Data stored in this class.
	 */
	private int iMyData = 5;

	/**
	 * Data stored in this class.
	 */
	private float fMyData = 7.345f;

	/**
	 * Default constructor.
	 */
	public SampleObjectShowUsageOfMemento() {

	}

	@Override
	public IMemento createMemento() {

		// Create new state object, which stores the current state...
		SampleMementoState myMementoState = new SampleMementoState(iMyData);
		myMementoState.setPostData(fMyData);

		// First possibility to set a IMemento.
		// -------------------------------------
		// create new memento for this object...
		IGeneralMemento myMemento = new IGeneralMemento(this, myMementoState);

		// Second possibility to set the IMemento state data.
		// ---------------------------------------------------
		// set current data of this object and store it in "myMementoState" ...
		myMementoState.setPostData(fMyData);

		// second possibility to set the IMemento state data.
		myMemento.setMementoState(this, myMementoState);

		// return IMemento to be stored in IMemento-Manager...
		return myMemento; // cast to super class "IMemento"
	}

	@Override
	public void setMemento(IMemento setMemento) {

		SampleMementoState bufferMementoState = null;

		// Test if getMementoState() returns the correct "SampleMementoState"
		// object...
		// try
		// {
		bufferMementoState = (SampleMementoState) setMemento.getMementoState(this);

		// Reset parameters...
		this.iMyData = bufferMementoState.getData();
		this.fMyData = bufferMementoState.getPostData();

		// }
		// catch (Exception e)
		// {
		// throw new
		// CaleydoRuntimeException("setMemento() with wrong IMementoState! "
		// + e.toString(), CaleydoRuntimeExceptionType.MEMENTO);
		// }

	}

	/**
	 * Any method of this object.
	 */
	public void doSomething() {

	}

	/**
	 * Placeholder for any data stored in this class.
	 * 
	 * @return
	 */
	public int getData() {

		return iMyData;
	}

	/**
	 * Placeholder for any data stored in this class.
	 */
	public void setData(int iSetData) {

		fMyData = iMyData + iSetData * 0.5f;
		iMyData = iSetData;
	}

}
