/**
 * 
 */
package org.caleydo.data.importer.tcga.startup;

import org.caleydo.core.parser.ascii.AStringConverter;

/**
 * @author alexsb
 */
public class TCGAIDStringConverter
	extends AStringConverter {

	@Override
	public String convert(String string) {
		return string.replace("-", ".");
	}

}
