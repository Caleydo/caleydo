package org.caleydo.datadomain.clinical;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * TODO The use case for clinical input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class ClinicalDataDomain extends ASetBasedDataDomain {
	
	/**
	 * Constructor.
	 */
	public ClinicalDataDomain() {
		super("org.caleydo.datadomain.clinical");
		icon = EIconTextures.DATA_DOMAIN_CLINICAL;

		possibleIDCategories = new HashMap<EIDCategory, String>();
		possibleIDCategories.put(EIDCategory.EXPERIMENT, null);

		contentIDType = EIDType.EXPERIMENT_INDEX;
		storageIDType = EIDType.EXPERIMENT_RECORD;
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

	@Override
	public void handleForeignContentVAUpdate(int setID,
			String dataDomainType, ContentVAType vaType, ContentVirtualArray virtualArray) {
		// TODO Auto-generated method stub
		
	}


}
