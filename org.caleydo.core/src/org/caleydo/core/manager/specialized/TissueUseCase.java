package org.caleydo.core.manager.specialized;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;

/**
 * TODO The use case for tissue input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class TissueUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public TissueUseCase() {
		useCaseMode = EDataDomain.TISSUE_DATA;

		possibleViews = new ArrayList<EManagedObjectType>();
		possibleViews.add(EManagedObjectType.GL_TISSUE_VIEW_BROWSER);

		possibleIDCategories = new HashMap<EIDCategory, String>();
		// possibleIDCategories.put(EIDCategory.GENE, null);
	}

	@Override
	public void setSet(ISet set) {

		super.setSet(set);
	}
}
