/**
 * 
 */
package org.caleydo.data.importer.tcga.startup;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.parser.ascii.AStringConverter;

/**
 * @author alexsb
 */
@XmlRootElement
public class TCGAIDStringConverter
	extends AStringConverter {

	@Override
	public String convert(String string) {
		string = string.replace("-", ".");
		// make stuff like TCGA-02-0001-01C-01R-0177-01 to 02-0001
		if (string.length() > 12)
			string = string.substring(5, 12);
		else
			string = string.substring(5);
		return string;
	}

}
