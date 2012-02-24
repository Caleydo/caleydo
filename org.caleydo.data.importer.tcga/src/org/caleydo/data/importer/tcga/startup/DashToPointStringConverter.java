package org.caleydo.data.importer.tcga.startup;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.parser.ascii.AStringConverter;

/**
 * @author Alexander Lex
 * 
 */
@XmlRootElement
public class DashToPointStringConverter extends AStringConverter {

	@Override
	public String convert(String string) {
		string = string.replace("-", ".");

		return string;
	}

}
