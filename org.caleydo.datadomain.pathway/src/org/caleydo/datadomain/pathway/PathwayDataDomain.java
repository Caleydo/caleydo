package org.caleydo.datadomain.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.mapping.IDMappingLoader;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.datadomain.pathway.rcp.PathwayLoadingProgressIndicatorAction;

/**
 * TODO The use case for pathway input data.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class PathwayDataDomain extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.pathway";

	IDType primaryIDType;

	/**
	 * Constructor.
	 */
	public PathwayDataDomain() {

		super(DATA_DOMAIN_TYPE);
		
		icon = EIconTextures.DATA_DOMAIN_PATHWAY;

		PathwayManager.get().triggerParsingPathwayDatabases();

		// Trigger pathway loading
		new PathwayLoadingProgressIndicatorAction().run(null);

		primaryIDType = IDType.getIDType("PATHWAY_VERTEX");
	}

	@Override
	protected void initIDMappings() {
		// Load IDs needed in this datadomain
		IDMappingLoader.get().loadMappingFile(fileName);
	}
	
	public IDType getPrimaryIDType() {
		return primaryIDType;
	}

	public IDType getDavidIDType() {
		return IDType.getIDType("DAVID");
	}
}
