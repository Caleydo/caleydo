/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.memento.sample;

import org.geneview.core.command.memento.IGeneralMemento;
import org.geneview.core.command.memento.IMemento;
import org.geneview.core.command.memento.IMementoCreator;
import org.geneview.core.command.memento.sample.SampleMementoState;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;


/**
 * Sample code for IMemento objects.
 * 
 * Implementation of Desing Pattern "IMemento"
 * 
 * \sa SampleMementoApplicationCmd
 * 
 * @author Michael Kalkusch
 *
 */
public class SampleObjectShowUsageOfMemento implements IMementoCreator {
	
	
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


	/* (non-Javadoc)
	 * @see prometheus.command.memento.MementoCreator#createMemento()
	 */
	public IMemento createMemento() {
		
		// Create new state object, which stores the current state...
		SampleMementoState myMementoState = new SampleMementoState(iMyData);
		myMementoState.setPostData( fMyData );
		
		// First possibility to set a IMemento.
		//-------------------------------------
		// create new memento for this object...
		IGeneralMemento myMemento = new IGeneralMemento(this,myMementoState);

		// Second possibility to set the IMemento state data.
		//---------------------------------------------------
		// set current data of this object and store it in "myMementoState" ...
		myMementoState.setPostData(fMyData);
		
		// second possibility to set the IMemento state data.
		myMemento.setMementoState(this,myMementoState);
				
		// return IMemento to be stored in IMemento-Manager...
		return  myMemento;	// cast to super class "IMemento"
	}

	
	/* (non-Javadoc)
	 * @see prometheus.command.memento.MementoCreator#setMemento(prometheus.command.memento.Memento)
	 */
	public void setMemento(IMemento setMemento)
			throws GeneViewRuntimeException {
		
		SampleMementoState 	bufferMementoState 	= null;
		
		//Test if getMementoState() returns the correct "SampleMementoState" object...
		try {
			bufferMementoState = 
				(SampleMementoState) setMemento.getMementoState(this); 
			
			//Reset parameters...
			this.iMyData = bufferMementoState.getData();
			this.fMyData = bufferMementoState.getPostData();
				
		} catch (Exception e) {
			throw new GeneViewRuntimeException("setMemento() with wrong IMementoState! "+ e.toString(),
					GeneViewRuntimeExceptionType.MEMENTO );
		}

	}
	
	/**
	 * Any method of this object.
	 *
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
