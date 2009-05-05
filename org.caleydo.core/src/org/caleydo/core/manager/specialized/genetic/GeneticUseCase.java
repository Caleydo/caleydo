package org.caleydo.core.manager.specialized.genetic;

import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EUseCaseMode;

/**
 * Use case specialized to genetic data.
 * 
 * @author Marc Streit
 */
public class GeneticUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public GeneticUseCase() {

		super();
		eUseCaseMode = EUseCaseMode.GENETIC_DATA;
	}
}
