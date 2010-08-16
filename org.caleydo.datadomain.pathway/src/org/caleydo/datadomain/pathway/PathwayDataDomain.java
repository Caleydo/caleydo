package org.caleydo.datadomain.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

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

	IDType primaryIDType;

	/**
	 * Constructor.
	 */
	public PathwayDataDomain() {
		
		dataDomainType = "org.caleydo.datadomain.pathway";
		icon = EIconTextures.DATA_DOMAIN_PATHWAY;

//		PathwayManager.get().triggerParsingPathwayDatabases();
//		
//		// Trigger pathway loading
//		new PathwayLoadingProgressIndicatorAction().run(null);
		
		primaryIDType = IDType.getIDType("PATHWAY_VERTEX");
	}
	
	public IDType getPrimaryIDType() {
		return primaryIDType;
	}
	
	public IDType getDavidIDType()
	{
		return IDType.getIDType("DAVID");
	}
}
