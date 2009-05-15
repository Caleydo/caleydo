package org.caleydo.core.manager.specialized.clinical;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EUseCaseMode;

/**
 * TODO The use case for clinical input data.
 * 
 * @author Marc Streit
 */
public class ClinicalUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public ClinicalUseCase() {

		eUseCaseMode = EUseCaseMode.CLINICAL_DATA;
	}
	
	@Override
	public void setSet(ISet set) {

		super.setSet(set);
	}
}
