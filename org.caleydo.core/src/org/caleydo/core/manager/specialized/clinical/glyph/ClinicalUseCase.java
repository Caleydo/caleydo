package org.caleydo.core.manager.specialized.clinical.glyph;

import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EUseCaseMode;

/**
 * TODO Write docu
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
}
