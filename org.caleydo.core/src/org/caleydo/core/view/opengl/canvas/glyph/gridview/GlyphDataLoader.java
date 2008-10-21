package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;

/**
 * Loading data into the glyph storage
 * 
 * @author Stefan Sauer
 */
public class GlyphDataLoader
{

	private IGeneralManager generalManager;
	private GlyphManager gman = null;

	private HashMap<Integer, GlyphEntry> glyphs = new HashMap<Integer, GlyphEntry>();

	public GlyphDataLoader()
	{
		this.generalManager = GeneralManager.get();
		this.gman = (GlyphManager) generalManager.getGlyphManager();
	}

	public HashMap<Integer, GlyphEntry> getGlyphList()
	{

		return glyphs;
	}

	@SuppressWarnings("unchecked")
	public void loadGlyphs(ISet glyphData)
	{
		if (gman.storageLoaded(glyphData.getLabel()))
			return;

		glyphs = new HashMap<Integer, GlyphEntry>();

		GLGlyphGenerator generator = generalManager.getGlyphManager().getGlyphGenerator();

		ArrayList<int[]> aliStoreMapped = new ArrayList<int[]>();
		ArrayList<INominalStorage<String>> alsStoreString = new ArrayList<INominalStorage<String>>();
		// ArrayList<String> alsStoreStringColTitel = new ArrayList<String>();

		{ // convert values to dictionary indices
			int counter = 0;
			int pcounter = 0;
			for (IStorage tmpStorage : glyphData)
			{
				GlyphAttributeType glyphAttributeType = generalManager.getGlyphManager()
						.getGlyphAttributeTypeWithExternalColumnNumber(counter);

				if (glyphAttributeType != null)
				{ // input column is defined

					if (tmpStorage instanceof NominalStorage
							&& tmpStorage.getRawDataType() == ERawDataType.STRING)
					{

						INominalStorage<String> nominalStorage = (INominalStorage<String>) tmpStorage;
						int[] temp2 = new int[nominalStorage.size()];

						for (int i = 0; i < nominalStorage.size(); ++i)
						{

							if (nominalStorage.getRaw(i) == null)
							{
								this.generalManager
										.getLogger()
										.log(Level.WARNING,
												"GlyphDataLoader: no String data found - empty line in csv file?????");
								temp2[i] = -1;
							}

							int t2 = glyphAttributeType.getIndex(nominalStorage.getRaw(i));

							if (glyphAttributeType.doesAutomaticAttribute() && t2 == -1)
							{
								try
								{
									t2 = Integer.parseInt(nominalStorage.getRaw(i));
								}
								catch (NumberFormatException ex)
								{
								}
								glyphAttributeType.addAttribute(t2, nominalStorage.getRaw(i),
										t2);
							}

							if (t2 == -1 && !(nominalStorage.getRaw(i)).equals("-1"))
								this.generalManager.getLogger().log(
										Level.WARNING,
										"GlyphDataLoader: No data mapping found for "
												+ tmpStorage.getLabel() + " value "
												+ nominalStorage.getRaw(i));

							temp2[i] = t2;

							glyphAttributeType.incDistribution(t2);
						}
						aliStoreMapped.add(temp2);
					}
					else
					{
						throw new RuntimeException(
								"GlyphDataLoader: ERROR. There should be only STRING values in the storage");
					}

					glyphAttributeType.setInternalColumnNumber(pcounter);
					++pcounter;

				}
				else
				{ // its something for the string storage
					if (tmpStorage instanceof NominalStorage
							&& tmpStorage.getRawDataType() == ERawDataType.STRING)
					{
						alsStoreString.add((INominalStorage<String>) tmpStorage);
					}
					else
						System.out.println("ERROR " + tmpStorage.getLabel());
				}

				++counter;
			}
		}

		if (aliStoreMapped.size() <= 0)
		{
			this.generalManager.getLogger().log(Level.SEVERE,
					"GlyphDataLoader: No data in file found");
			return;
		}

		int size = aliStoreMapped.get(0).length;

		// now convert the storages to real glyphs

		int counter = gman.getGlyphs().size();
		for (int i = 0; i < size; ++i)
		{
			GlyphEntry g = new GlyphEntry(counter, generator);

			for (int[] s : aliStoreMapped)
				g.addParameter(s[i]);

			for (int j = 0; j < alsStoreString.size(); ++j)
				g.addStringParameter(alsStoreString.get(j).getLabel(), alsStoreString.get(j)
						.getRaw(i));

			glyphs.put(counter, g);
			++counter;
		}

		((GlyphManager) generalManager.getGlyphManager()).addGlyphs(glyphs, glyphData
				.getLabel());
	}

}
