/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.test.objecttest.command.memento;

import cerberus.command.memento.IMemento;
//import cerberus.command.memento.MementoState;
import cerberus.command.memento.IMementoCreator;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * Class for testing IMemento.
 * 
 * @author Michael Kalkusch
 *
 */
public final class MementoTesterObject {

	private IMementoCreator refCreator;
	
	private int iTestRuns;
	
	public MementoTesterObject(final int iTestRuns) {
		this.iTestRuns = iTestRuns;
	}
	
	public void setMementoCreator( IMementoCreator creatorObject, final int iTestMementos ) {
		refCreator = creatorObject;
		if ( iTestRuns > 0 ) {
			iTestRuns = iTestMementos;
		}
	}
	
	public boolean testGetSetMemento() {
		
		IMemento[] testMementos = new IMemento[iTestRuns];
		
		for ( int i=0; i < iTestRuns; i++ ) {
			testMementos[i] = refCreator.createMemento();
		}
				
		try {
			for ( int i=(iTestRuns-1); i > -1; i-- ) {
				refCreator.setMemento( testMementos[i] );
			}
		}
		catch (CerberusRuntimeException pe) {
			return false;
		}
		catch (Exception e) {
			return false;
		}
		
		return true;
	}
}
