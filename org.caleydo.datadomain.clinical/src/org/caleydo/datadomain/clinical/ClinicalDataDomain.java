package org.caleydo.datadomain.clinical;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.EDataDomain;

/**
 * TODO The use case for clinical input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class ClinicalDataDomain
	extends ADataDomain {

	/**
	 * Constructor.
	 */
	public ClinicalDataDomain() {

		useCaseMode = EDataDomain.CLINICAL_DATA;

		possibleViews = new ArrayList<String>();
		possibleViews.add("org.caleydo.view.glyph");
		possibleViews.add("org.caleydo.view.parcoords");

		possibleIDCategories = new HashMap<EIDCategory, String>();
		possibleIDCategories.put(EIDCategory.EXPERIMENT, null);
		
		contentIDType = EIDType.EXPERIMENT_RECORD;
		storageIDType = EIDType.EXPERIMENT_INDEX;
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
