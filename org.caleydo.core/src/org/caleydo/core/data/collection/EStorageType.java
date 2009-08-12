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
	ABORT(false, null),
	SKIP(false, null),
	// FIXME: neither a control sequence nor a data type
	GROUP_NUMBER(false, null),
	GROUP_REPRESENTATIVE(false, null),

	INT(true, Integer.class),
	FLOAT(true, Float.class),
	STRING(true, String.class),
	// FIXME??
	NONE(false, null);

	private boolean bIsControlSequence;
	private Class<?> storageClass; 

	private <T> EStorageType(final boolean bIsControlSequence, Class<T> storageClass) {

		this.bIsControlSequence = bIsControlSequence;
	}

	public boolean isControlSequence() {

		return bIsControlSequence;
	}

}
