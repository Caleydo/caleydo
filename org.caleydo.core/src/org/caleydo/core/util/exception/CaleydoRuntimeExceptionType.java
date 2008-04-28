package org.caleydo.core.util.exception;

import org.caleydo.core.util.ICaleydoDefaultType;

/**
 * Enumeration of Exceptions.
 * 
 * @author Michael Kalkusch
 *
 */
public enum CaleydoRuntimeExceptionType 
implements ICaleydoDefaultType <CaleydoRuntimeExceptionType> {
	
	ANY_ERROR("Caleydo-error"),
	COMMAND("Command"),
	CONVERSION("NumberFormatError"),
	DATAHANDLING("LOAD,SAVE"),
	MEMENTO("Memeont"),
	MANAGER("Manager"),
	OBSERVER("Observer"),
	JOGL_AWT("Jogl AWT"),
	JOGL_SWT("Jogl SWT"),
	RUNTIME("Runtime"),
	SAXPARSER("Sax parser"),
	VIRTUALARRAY("Virtual array"),
	VIEW("View"),
	SET("Set");
	
	private String sExceptionDetail;
	
	private CaleydoRuntimeExceptionType( String ssExceptionDetail ) {
		sExceptionDetail = ssExceptionDetail;
	}
	
	public String getExceptionDetail() {
		return sExceptionDetail;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.util.ICaleydoDefaultType#getTypeDefault()
	 */
	public CaleydoRuntimeExceptionType getTypeDefault() {

		return CaleydoRuntimeExceptionType.ANY_ERROR;
	}
}
