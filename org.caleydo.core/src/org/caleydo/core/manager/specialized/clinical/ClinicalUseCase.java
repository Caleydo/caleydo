package org.caleydo.core.manager.specialized.clinical;

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
 * TODO The use case for clinical input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class ClinicalUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public ClinicalUseCase() {

		useCaseMode = EDataDomain.CLINICAL_DATA;

		possibleViews = new ArrayList<EManagedObjectType>();
		// possibleViews.add(EManagedObjectType.GL_HEAT_MAP);
		possibleViews.add(EManagedObjectType.GL_GLYPH);
		possibleViews.add(EManagedObjectType.GL_PARALLEL_COORDINATES);

		possibleIDCategories = new HashMap<EIDCategory, Boolean>();
		// possibleIDCategories.put(EIDCategory., null);
		possibleIDCategories.put(EIDCategory.EXPERIMENT, null);
	}

	@Override
	public void setSet(ISet set) {

		super.setSet(set);
	}

}
