package org.caleydo.core.manager.specialized;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;

/**
 * TODO The use case for pathway input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class PathwayUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public PathwayUseCase() {
		useCaseMode = EDataDomain.PATHWAY_DATA;
		possibleViews = new ArrayList<String>();
		possibleViews.add("org.caleydo.view.pathwaybrowser");

		possibleIDCategories = new HashMap<EIDCategory, String>();
		// possibleIDCategories.put(EIDCategory.GENE, null);
		possibleIDCategories.put(EIDCategory.PATHWAY, null);
	}

	@Override
	public void setSet(ISet set) {

		super.setSet(set);
	}

	@Override
	public void handleContentVAUpdate(ContentVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleStorageVAUpdate(StorageVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}
}
