package org.caleydo.core.parser.ascii;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander Lex
 */
@XmlRootElement
public class RefSeqStringConverter
	 {

	
	public String convert(String string) {
		// Remove multiple RefSeqs because all point to the
		// same gene DAVID ID
		if (string.contains(";")) {
			string = string.substring(0, string.indexOf(";"));
		}

		// Remove version in RefSeq (NM_*.* -> NM_*)
		if (string.contains(".")) {
			string = string.substring(0, string.indexOf("."));
		}

		return string;
	}

}
