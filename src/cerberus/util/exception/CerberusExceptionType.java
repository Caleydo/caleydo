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
	
	ANY_ERROR("cerberus-error"),
	COMMAND("Command"),
	DATAHANDLING("LOAD,SAVE"),
	MEMENTO("Memeont"),	
	OBSERVER("Observer"),
	JOGL_AWT("Jogl AWT"),
	JOGL_SWT("Jogl SWT"),
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
