package org.caleydo.datadomain.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;

/**
 * TODO The use case for pathway input data.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class PathwayDataDomain
	extends ADataDomain {

	/**
	 * Constructor.
	 */
	public PathwayDataDomain() {
		
		dataDomainType = "org.caleydo.datadomain.pathway";
		icon = EIconTextures.DATA_DOMAIN_PATHWAY;

	
		
		// Trigger pathway loading
		new PathwayLoadingProgressIndicatorAction().run(null);
	}

}
