/**
 * 
 */
package org.caleydo.data.importer.tcga.startup;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.parser.ascii.AStringConverter;

/**
 * Converter for TCGA sample IDs. Makes an ID that looks like TCGA-02-0001-01C-01R-0177-01 to 02-0001
 * 
 * @author Alexander Lex
 */
@XmlRootElement
public class TCGAIDStringConverter
	extends AStringConverter {

	@Override
	public String convert(String string) {
		string = string.replace("-", ".");

		if (string.length() > 12)
			string = string.substring(5, 12);
		else
			string = string.substring(5);
		return string;
	}

}
