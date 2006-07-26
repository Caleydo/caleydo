/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.util.exception;

/**
 * Enumeration of Exceptions.
 * 
 * @author Michael Kalkusch
 *
 */
public enum CerberusExceptionType {
	
	COMMAND("Command"),
	DATAHANDLING("LOAD,SAVE"),
	MEMENTO("Memeont"),
	OBSERVER("Observer"),
	RUNTIME("Runtime"),
	SAXPARSER("Sax parser"),
	VIRTUALARRAY("Virtual array");
	
	private String sExceptionDetail;
	
	private CerberusExceptionType( String ssExceptionDetail ) {
		sExceptionDetail = ssExceptionDetail;
	}
	
	public String getExceptionDetail() {
		return sExceptionDetail;
	}
	

}
