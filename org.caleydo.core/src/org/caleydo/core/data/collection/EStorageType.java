package org.caleydo.core.data.collection;

/**
 * Collection of different data types used in storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum EStorageType {
	// Needed by the parser
	// FIXME: control seqeunces are not a storage type
	ABORT(false),
	SKIP(false),
	// FIXME: neither a control sequence nor a data type
	GROUP_NUMBER(false),
	GROUP_REPRESENTATIVE(false),

	INT(true),
	FLOAT(true),
	STRING(true),
	// FIXME??
	NONE(false);

	private boolean bIsControlSequence;

	private EStorageType(final boolean bIsControlSequence) {

		this.bIsControlSequence = bIsControlSequence;
	}

	public boolean isControlSequence() {

		return bIsControlSequence;
	}

}
