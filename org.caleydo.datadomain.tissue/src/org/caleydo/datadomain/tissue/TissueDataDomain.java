package org.caleydo.datadomain.tissue;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * TODO The use case for tissue input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class TissueDataDomain extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.tissue";

	/**
	 * Constructor.
	 */
	public TissueDataDomain() {
		dataDomainType = DATA_DOMAIN_TYPE;
		icon = EIconTextures.DATA_DOMAIN_TISSUE;

		// possibleIDCategories.put(EIDCategory.GENE, null);
	}
}
