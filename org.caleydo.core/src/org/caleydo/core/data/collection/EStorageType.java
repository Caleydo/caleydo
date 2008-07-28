package org.caleydo.core.data.collection;

/**
 * Collection of different data types used in storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public enum EStorageType 
{
	// Needed by the parser
	ABORT(false),
	SKIP(false),
	
	INT(true),
	FLOAT(true),
	STRING(true);
	
	private boolean bIsControlSequence;
	
	
	private EStorageType(final boolean bIsControlSequence) 
	{
		this.bIsControlSequence = bIsControlSequence;		
	}
	
	public boolean isControlSequence()
	{
		return bIsControlSequence;
	}
	
}
