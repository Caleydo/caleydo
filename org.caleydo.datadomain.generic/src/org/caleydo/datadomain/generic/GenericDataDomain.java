package org.caleydo.datadomain.generic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.DimensionType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.GeneralManager;
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
	
	private IDMappingManager idMappingManager = GeneralManager.get()
	.getIDMappingManager();

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
	}

	@Override
	protected void initIDMappings() {

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
		
		String resolvedID = idMappingManager.getID(idType,
				humanReadableRecordIDType, id);
		
		return resolvedID;
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

		recordIDCategory = IDCategory.registerCategory("UNSPECIFIED_RECORD");
		dimensionIDCategory = IDCategory.registerCategory("UNSPECIFIED_DIMENSION");
		
		recordIDType = IDType.registerType("UNSPECIFIED_RECORD", recordIDCategory, DimensionType.STRING);
		dimensionIDType = IDType.registerType("UNSPECIFIED_DIMENSION", dimensionIDCategory, DimensionType.STRING);
		
		primaryRecordMappingType = IDType.getIDType(DataTable.RECORD);
		humanReadableRecordIDType = recordIDType;
		humanReadableDimensionIDType = dimensionIDType;
	}
}
