package org.caleydo.core.data.collection.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;

public class SetExporter
{
	private String fileName = "/home/alexsb/Desktop/out.txt";

	public void export(ISet set)
	{
		try
		{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			out.println("Test 2");
			for (int iCount = 0; iCount < set.depth(); iCount++)
			{
				for (IStorage storage : set)
				{
					out.print(storage.getFloat(EDataRepresentation.RAW, iCount));
					out.print("\t");
				}
				out.println();
			}

			out.close();
		}
		catch (IOException e)
		{

		}
	}
}
