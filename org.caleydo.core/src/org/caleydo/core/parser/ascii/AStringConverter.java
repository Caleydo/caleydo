/**
 * 
 */
package org.caleydo.core.parser.ascii;

/**
 * Base class for converting ID Mapping strings. StringConverters are very special to one specific data file
 * or IDType and should be created in a place where the speciality is appropriate (e.g. GeneticDataDomain
 * plug-in for refseq stuff)
 * 
 * @author Alexander Lex
 */
public abstract class AStringConverter {

	public abstract String convert(String string);

}
