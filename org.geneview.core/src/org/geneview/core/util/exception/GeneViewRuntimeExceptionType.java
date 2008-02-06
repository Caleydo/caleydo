/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.util.exception;

import org.geneview.core.util.IGeneViewDefaultType;

/**
 * Enumeration of Exceptions.
 * 
 * @author Michael Kalkusch
 *
 */
public enum GeneViewRuntimeExceptionType 
implements IGeneViewDefaultType <GeneViewRuntimeExceptionType> {
	
	ANY_ERROR("GeneView-error"),
	COMMAND("Command"),
	CONVERSION("NumberFormatError"),
	DATAHANDLING("LOAD,SAVE"),
	MEMENTO("Memeont"),	
	OBSERVER("Observer"),
	JOGL_AWT("Jogl AWT"),
	JOGL_SWT("Jogl SWT"),
	RUNTIME("Runtime"),
	SAXPARSER("Sax parser"),
	VIRTUALARRAY("Virtual array"),
	VIEW("View"),
	SET("Set");
	
	private String sExceptionDetail;
	
	private GeneViewRuntimeExceptionType( String ssExceptionDetail ) {
		sExceptionDetail = ssExceptionDetail;
	}
	
	public String getExceptionDetail() {
		return sExceptionDetail;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.util.IGeneViewDefaultType#getTypeDefault()
	 */
	public GeneViewRuntimeExceptionType getTypeDefault() {

		return GeneViewRuntimeExceptionType.ANY_ERROR;
	}
	

}
