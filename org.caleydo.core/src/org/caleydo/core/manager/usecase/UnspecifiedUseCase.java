package org.caleydo.core.manager.usecase;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.EVAType;

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
		
		possibleIDCategories = new HashMap<EIDCategory, String>();
		possibleIDCategories.put(EIDCategory.OTHER, EVAType.CONTENT_PRIMARY);
		possibleIDCategories.put(EIDCategory.EXPERIMENT, EVAType.STORAGE_PRIMARY);
	}
}
