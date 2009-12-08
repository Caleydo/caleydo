package org.caleydo.testing.unit.command.data.filter;

import java.util.ArrayList;
import junit.framework.TestCase;
import org.caleydo.core.data.collection.ccontainer.FloatCContainer;
import org.caleydo.core.data.collection.ccontainer.ICContainer;
import org.caleydo.core.data.collection.ccontainer.NominalCContainer;

public class NominalStringCCollectionTest
	extends TestCase
{
	NominalCContainer<String> sCollection;
	ArrayList<String> sAlTestWords;

	protected void setUp() throws Exception
	{
		super.setUp();
		sAlTestWords = new ArrayList<String>();
		sAlTestWords.add("Flu");
		sAlTestWords.add("Cancer");
		sAlTestWords.add("Flu");
		sAlTestWords.add("Gastritis");

		sCollection = new NominalCContainer<String>(sAlTestWords);
	}

	public void testGet()
	{
		assertEquals(sCollection.get(0), sAlTestWords.get(0));

	}

	public void testNormalize()
	{
		ICContainer normalizedStorage = sCollection.normalize();
		if (!(normalizedStorage instanceof FloatCContainer))
			fail("Should be primitive float");

		FloatCContainer normalizedFloatStorage = (FloatCContainer) normalizedStorage;
		assertEquals(sCollection.getDiscreteForNominalValue("Flu"), normalizedFloatStorage
				.get(0), 0.01);
		assertEquals(sCollection.getDiscreteForNominalValue("Cancer"), normalizedFloatStorage
				.get(1), 0.01);
		assertEquals(sCollection.getDiscreteForNominalValue("Flu"), normalizedFloatStorage
				.get(2), 0.01);
		assertEquals(sCollection.getDiscreteForNominalValue("Gastritis"),
				normalizedFloatStorage.get(3), 0.01);
	}

	public void testSize()
	{
		assertEquals(4, sCollection.size());
	}

	public void testGetNominalForDiscreteValue()
	{

		assertEquals("Flu", sCollection.getNominalForDiscreteValue(sCollection
				.getDiscreteForNominalValue("Flu")));
		assertEquals("Cancer", sCollection.getNominalForDiscreteValue(sCollection
				.getDiscreteForNominalValue("Cancer")));
		assertEquals("Gastritis", sCollection.getNominalForDiscreteValue(sCollection
				.getDiscreteForNominalValue("Gastritis")));

	}

}
