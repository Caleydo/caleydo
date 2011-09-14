/**
 * 
 */
package org.caleydo.core.parser.ascii;

/**
 * @author Alexander Lex
 */
public class RefSeqStringConverter
	extends AStringConverter {

	@Override
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
