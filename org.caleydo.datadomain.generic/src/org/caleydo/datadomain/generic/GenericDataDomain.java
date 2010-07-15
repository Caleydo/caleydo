package org.caleydo.datadomain.generic;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;

/**
 * Use case for generic set-based data which is not further specified.
 * 
 * @author Marc Streit
 * @author Alexander lex
 */
@XmlType
@XmlRootElement
public class GenericDataDomain extends ASetBasedDataDomain {

	/**
	 * Constructor.
	 */
	public GenericDataDomain() {

		super("org.caleydo.datadomain.generic");
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

	@Override
	public void handleContentVAUpdateForForeignDataDomain(int setID,
			String dataDomainType, ContentVAType vaType, ContentVirtualArray virtualArray) {
		// TODO Auto-generated method stub

	}
}
