package org.caleydo.datadomain.generic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
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

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.generic";

	/**
	 * Constructor.
	 */
	public GenericDataDomain() {

		super(DATA_DOMAIN_TYPE);

		contentLabelSingular = "entity";
		contentLabelPlural = "entities";
	}

	@Override
	protected void initIDMappings() {
		// nothing to do ATM
	}
	
	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleVAUpdate(StorageVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForeignContentVAUpdate(int setID, String dataDomainType,
			String vaType, ContentVirtualArray virtualArray) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getContentLabel(IDType idType, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStorageLabel(IDType idType, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void assignIDCategories() {
		contentIDCategory = IDCategory.getIDCategory("UNSPECIFIED_CONTENT");
		storageIDCategory = IDCategory.getIDCategory("UNSPECIFIED_STORAGE");
	}
}
