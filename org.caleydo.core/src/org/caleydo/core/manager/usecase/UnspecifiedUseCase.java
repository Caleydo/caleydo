package org.caleydo.core.manager.usecase;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.StorageVADelta;

/**
 * Use case for arbitrary data which is not further specified.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class UnspecifiedUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public UnspecifiedUseCase() {

		super();
		useCaseMode = EDataDomain.UNSPECIFIED;
		contentLabelSingular = "entity";
		contentLabelPlural = "entities";

		contentIDType = EIDType.UNSPECIFIED;
		storageIDType = EIDType.EXPERIMENT_INDEX;

		possibleIDCategories = new HashMap<EIDCategory, String>();
		possibleIDCategories.put(EIDCategory.OTHER, null);
		possibleIDCategories.put(EIDCategory.EXPERIMENT, null);
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
