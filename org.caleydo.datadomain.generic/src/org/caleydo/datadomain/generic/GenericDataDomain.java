package org.caleydo.datadomain.generic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDType;
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
	public void handleForeignContentVAUpdate(int setID, String dataDomainType,
			ContentVAType vaType, ContentVirtualArray virtualArray) {
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
}
