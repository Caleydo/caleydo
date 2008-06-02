package org.caleydo.core.test.objecttest.command.memento;

import org.caleydo.core.command.memento.IMemento;
import org.caleydo.core.command.memento.IMementoCreator;

import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class for testing IMemento.
 * 
 * @author Michael Kalkusch
 *
 */
public final class MementoTesterObject {

	private IMementoCreator refCreator;
	
	private int iTestRuns;
	
	/**
	 * Constructor.
	 * 
	 * @param iTestRuns
	 */
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
		catch (CaleydoRuntimeException pe) {
			return false;
		}
		catch (Exception e) {
			return false;
		}
		
		return true;
	}
}
