/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.parser;

/**
 * @author Michael Kalkusch
 *
 */
public enum ParserTokenType {

	/** string */
	STRING,
	/** integer */
	INT,
	/** long */
	LONG,
	/** float */
	FLOAT,
	/** double */
	DOUBLE,
	/** vec2f, contains of (float,float) */
	VEC2F,
	/** vec3f, contains of (float,float,float) */ 
	VEC3F,
	/** vec4f, contains of (float,float,float,float) */
	VEC4F,
	/** boolean */
	BOOLEAN,
	/** short */
	SHORT,
	
	/** not specified as data type. skip value in tokenize. */
	SKIP,
	/** not specified as data type. abort in tokenizer. */
	ABORT,
	
	/** not specified as data type. not any specified. */
	NONE,
	
	/** not specified as data type. used by prometheus.statistic.*.
	 * Defines an iterator as data type. */
	ITERATOR
	
}
