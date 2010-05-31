package org.caleydo.datadomain.tissue;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDCategory;
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

	/**
	 * Constructor.
	 */
	public TissueDataDomain() {
		dataDomainType = "org.caleydo.datadomain.tissue";
		icon = EIconTextures.DATA_DOMAIN_TISSUE;

		possibleViews = new ArrayList<String>();
		possibleViews.add("org.caleydo.view.tissuebrowser");

		possibleIDCategories = new HashMap<EIDCategory, String>();
		// possibleIDCategories.put(EIDCategory.GENE, null);
	}
}
