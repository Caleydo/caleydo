package org.caleydo.datadomain.generic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;

/**
 * Use case for generic set-based data which is not further specified.
 * 
 * @author Marc Streit
 * @author Alexander lex
 */
@XmlType
@XmlRootElement
public class GenericDataDomain extends ATableBasedDataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.generic";

	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static int extensionID = 0;

	/**
	 * Constructor.
	 */
	public GenericDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER + extensionID++);

		recordLabelSingular = "entity";
		recordLabelPlural = "entities";

		primaryRecordMappingType = IDType.getIDType("DAVID");
		humanReadableRecordIDType = IDType.getIDType("GENE_SYMBOL");
		humanReadableDimensionIDType = IDType.getIDType("STORAGE");
	}

	@Override
	protected void initIDMappings() {
		// nothing to do ATM
	}

	@Override
	public void handleVAUpdate(RecordVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleVAUpdate(DimensionVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForeignRecordVAUpdate(int tableID, String dataDomainType,
			String vaType, RecordVirtualArray virtualArray) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRecordLabel(IDType idType, Object id) {
		return "";
	}

	@Override
	public String getDimensionLabel(IDType idType, Object id) {
		String label = table.get((Integer) id).getLabel();
		if (label == null)
			label = "";
		return label;
	}

	@Override
	protected void assignIDCategories() {

		recordIDCategory = IDCategory.registerCategory("UNSPECIFIED_CONTENT");
		dimensionIDCategory = IDCategory.registerCategory("UNSPECIFIED_STORAGE");
	}
}
