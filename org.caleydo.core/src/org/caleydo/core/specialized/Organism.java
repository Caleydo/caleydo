package org.caleydo.core.specialized;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Organism on which an analysis bases. Currently we support homo sapiens (human) and mus musculus (mouse).
 * FIXME: organism should be moved to the datadomain.genetic plugin. however, we have a dependency problem.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public enum Organism {

	/**
	 * Human
	 */
	HOMO_SAPIENS,

	/**
	 * Mouse
	 */
	MUS_MUSCULUS;
}
