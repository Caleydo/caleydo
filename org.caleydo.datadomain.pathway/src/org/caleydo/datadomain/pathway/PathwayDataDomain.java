package org.caleydo.datadomain.pathway;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.manager.datadomain.ADataDomain;
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
		possibleViews = new ArrayList<String>();
		possibleViews.add("org.caleydo.view.pathwaybrowser");

		possibleIDCategories = new HashMap<EIDCategory, String>();
		// possibleIDCategories.put(EIDCategory.GENE, null);
		possibleIDCategories.put(EIDCategory.PATHWAY, null);
		
		// Trigger pathway loading
		new PathwayLoadingProgressIndicatorAction().run(null);
	}

}
