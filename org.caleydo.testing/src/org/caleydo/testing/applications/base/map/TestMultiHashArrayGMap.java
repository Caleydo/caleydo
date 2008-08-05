package org.caleydo.testing.applications.base.map;

import java.util.ArrayList;
import org.caleydo.core.data.map.MultiHashArrayGMap;

/**
 * @author Michael Kalkusch
 */
public class TestMultiHashArrayGMap
{

	public MultiHashArrayGMap<String, ArrayList<String>> map;

	/**
	 * 
	 */
	public TestMultiHashArrayGMap()
	{

		map = new MultiHashArrayGMap<String, ArrayList<String>>();
	}

	public void test()
	{

		String testString[][] = { { "A", "1" }, { "A", "2" }, { "B", "2" }, { "C", "3" },
				{ "C", "7" }, { "D", "4" } };
		int iLength = 6;

		for (int i = 0; i < iLength; i++)
		{
			map.putItem(testString[i][0], testString[i][1]);
		}

		/* output */
		for (int i = 0; i < iLength; i++)
		{
			System.out.println(" " + i + ": " + testString[i][0]);
			System.out.println(" " + i + ": " + map.get(testString[i][0]));
		}

		/* remove items */
		map.removeItem("B", "2");

		/* output */
		for (int i = 0; i < iLength; i++)
		{
			System.out.println(" " + i + ": " + map.get(testString[i][0]));
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		TestMultiHashArrayGMap tester = new TestMultiHashArrayGMap();

		tester.test();

	}

}
