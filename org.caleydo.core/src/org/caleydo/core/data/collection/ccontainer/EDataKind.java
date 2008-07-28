package org.caleydo.core.data.collection.ccontainer;


/**
 * 
 * @author Alexander Lex
 * 
 * Describes of what kind a container is, independent of its data type.
 * Examples are raw data, normalized data etc.
 * 
 * Raw data can e.g. be primitive float, int, double or (numerical) objects, or even 
 * nominal data. 
 *
 */
public enum EDataKind
{
	RAW,
	NORMALIZED;
}
