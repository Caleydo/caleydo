package org.caleydo.core.util.exception;

/**
 * Enumeration of Exceptions.
 * 
 * @author Michael Kalkusch
 */
public enum CaleydoRuntimeExceptionType
{

	ANY_ERROR("Caleydo-error"), COMMAND("Command"), CONVERSION("NumberFormatError"), DATAHANDLING(
			"LOAD,SAVE"), MEMENTO("Memeont"), MANAGER("Manager"), OBSERVER("Observer"), JOGL_AWT(
			"Jogl AWT"), JOGL_SWT("Jogl SWT"), RUNTIME("Runtime"), SAXPARSER("Sax parser"), VIRTUALARRAY(
			"Virtual array"), VIEW("View"), SET("Set");

	private String sExceptionDetail;

	private CaleydoRuntimeExceptionType(String ssExceptionDetail)
	{

		sExceptionDetail = ssExceptionDetail;
	}

	public String getExceptionDetail()
	{

		return sExceptionDetail;
	}
}
