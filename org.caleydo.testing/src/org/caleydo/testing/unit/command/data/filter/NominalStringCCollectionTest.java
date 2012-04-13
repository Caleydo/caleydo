/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.testing.unit.command.data.filter;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.caleydo.core.data.collection.ccontainer.FloatCContainer;
import org.caleydo.core.data.collection.ccontainer.ICContainer;
import org.caleydo.core.data.collection.ccontainer.NominalCContainer;

public class NominalStringCCollectionTest extends TestCase {
	NominalCContainer<String> sCollection;
	ArrayList<String> sAlTestWords;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sAlTestWords = new ArrayList<String>();
		sAlTestWords.add("Flu");
		sAlTestWords.add("Cancer");
		sAlTestWords.add("Flu");
		sAlTestWords.add("Gastritis");

		sCollection = new NominalCContainer<String>(sAlTestWords);
	}

	public void testGet() {
		assertEquals(sCollection.get(0), sAlTestWords.get(0));

	}

	public void testNormalize() {
		ICContainer normalizedStorage = sCollection.normalize();
		if (!(normalizedStorage instanceof FloatCContainer))
			fail("Should be primitive float");

		FloatCContainer normalizedFloatStorage = (FloatCContainer) normalizedStorage;
		assertEquals(sCollection.getDiscreteForNominalValue("Flu"),
				normalizedFloatStorage.get(0), 0.01);
		assertEquals(sCollection.getDiscreteForNominalValue("Cancer"),
				normalizedFloatStorage.get(1), 0.01);
		assertEquals(sCollection.getDiscreteForNominalValue("Flu"),
				normalizedFloatStorage.get(2), 0.01);
		assertEquals(sCollection.getDiscreteForNominalValue("Gastritis"),
				normalizedFloatStorage.get(3), 0.01);
	}

	public void testSize() {
		assertEquals(4, sCollection.size());
	}

	public void testGetNominalForDiscreteValue() {

		assertEquals("Flu", sCollection.getNominalForDiscreteValue(sCollection
				.getDiscreteForNominalValue("Flu")));
		assertEquals("Cancer", sCollection.getNominalForDiscreteValue(sCollection
				.getDiscreteForNominalValue("Cancer")));
		assertEquals("Gastritis", sCollection.getNominalForDiscreteValue(sCollection
				.getDiscreteForNominalValue("Gastritis")));

	}

}
