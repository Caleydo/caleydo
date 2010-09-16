package org.caleydo.core.manager.specialized;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Organism on which an analysis bases. Currently we support homo sapiens (human) and mus musculus (mouse).
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
