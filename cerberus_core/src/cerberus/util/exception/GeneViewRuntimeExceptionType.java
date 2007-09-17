/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.util.exception;

import cerberus.util.IGeneViewDefaultType;

/**
 * Enumeration of Exceptions.
 * 
 * @author Michael Kalkusch
 *
 */
public enum GeneViewRuntimeExceptionType 
implements IGeneViewDefaultType <GeneViewRuntimeExceptionType> {
	
	ANY_ERROR("cerberus-error"),
	COMMAND("Command"),
	CONVERSION("NumberFormatError"),
	DATAHANDLING("LOAD,SAVE"),
	MEMENTO("Memeont"),	
	OBSERVER("Observer"),
	JOGL_AWT("Jogl AWT"),
	JOGL_SWT("Jogl SWT"),
	RUNTIME("Runtime"),
	SAXPARSER("Sax parser"),
	VIRTUALARRAY("Virtual array");
	
	private String sExceptionDetail;
	
	private GeneViewRuntimeExceptionType( String ssExceptionDetail ) {
		sExceptionDetail = ssExceptionDetail;
	}
	
	public String getExceptionDetail() {
		return sExceptionDetail;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.util.IGeneViewDefaultType#getTypeDefault()
	 */
	public GeneViewRuntimeExceptionType getTypeDefault() {

		return GeneViewRuntimeExceptionType.ANY_ERROR;
	}
	

}
