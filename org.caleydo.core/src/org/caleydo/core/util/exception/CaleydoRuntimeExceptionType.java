package org.caleydo.core.util.exception;

/**
 * Enumeration of Exceptions.
 * 
 * @author Michael Kalkusch
 */
@Deprecated
public enum CaleydoRuntimeExceptionType
{

	ANY_ERROR("Caleydo-error"),
	COMMAND("Command"),
	CONVERSION("NumberFormatError"),
	DATAHANDLING("LOAD,SAVE"),
	MEMENTO("Memeont"),
	MANAGER("Manager"),
	OBSERVER("Observer"),
	GUI_RCP("RCP GUI"),
	GUI_STANDALONE("Standalone GUI"),
	RUNTIME("Runtime"),
	SAXPARSER("Sax parser"),
	VIRTUALARRAY("Virtual array"),
	VIEW("View"),
	ID("ID Management"),
	SET("Set"),
	SELECTION("Selection"),
	EVENT("Event handling"),
	COLOR_MAPPING("Color Mapping");

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
