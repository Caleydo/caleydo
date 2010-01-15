package org.caleydo.core.view.opengl.canvas.glyph.gridview.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.clinical.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loading data into the glyph storage. For the internal Glyph ID is either the mapped
 * EXPERIMENT_2_EXPERIMENT_INDEX used, or a running number if the mapping is not present.
 * 
 * @author Stefan Sauer
 */
public class GlyphDataLoader {

	private IGeneralManager generalManager;
	private GlyphManager gman = null;

	private HashMap<Integer, GlyphEntry> glyphs = new HashMap<Integer, GlyphEntry>();

	public GlyphDataLoader() {
		this.generalManager = GeneralManager.get();
		this.gman = generalManager.getGlyphManager();
	}

	public HashMap<Integer, GlyphEntry> getGlyphList() {

		return glyphs;
	}

	@SuppressWarnings("unchecked")
	public void loadGlyphs(ISet glyphData) {
		if (gman.storageLoaded(glyphData.getLabel()))
			return;

		glyphs = new HashMap<Integer, GlyphEntry>();

		ArrayList<int[]> aliStoreMapped = new ArrayList<int[]>();
		ArrayList<INominalStorage<String>> alsStoreString = new ArrayList<INominalStorage<String>>();
		// ArrayList<String> alsStoreStringColTitel = new ArrayList<String>();

		{ // convert values to dictionary indices
			int counter = 0;
			int pcounter = 0;
			for (IStorage tmpStorage : glyphData) {
				GlyphAttributeType glyphAttributeType =
					generalManager.getGlyphManager().getGlyphAttributeTypeWithExternalColumnNumber(counter);

				if (glyphAttributeType != null) { // input column is defined

					// INumericalStorage numericalStorage = (INumericalStorage)
					// tmpStorage;
					int[] temp2 = new int[tmpStorage.size()];
					String value = "";

					for (int i = 0; i < tmpStorage.size(); ++i) {
						// get value from storage
						if (tmpStorage instanceof NominalStorage) {
							INominalStorage<String> storage = (INominalStorage<String>) tmpStorage;
							if (storage.getRaw(i) == null) {
								GeneralManager.get().getLogger().log(
									new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
										"GlyphDataLoader: no numerical data found"
											+ " - empty line in csv file?????"));
								temp2[i] = -1;
								continue;
							}
							value = storage.getRaw(i);

						}
						else if (tmpStorage instanceof NumericalStorage) {
							INumericalStorage storage = (INumericalStorage) tmpStorage;
							if (storage.get(EDataRepresentation.RAW, i) == null) {
								GeneralManager.get().getLogger().log(
									new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
										"GlyphDataLoader: no numerical data found"
											+ " - empty line in csv file?????"));
								temp2[i] = -1;
								continue;
							}
							value = storage.get(EDataRepresentation.RAW, i).toString();
						}

						int t2 = glyphAttributeType.getIndex(value);

						if (glyphAttributeType.doesAutomaticAttribute() && t2 == -1) {
							try {
								t2 = Integer.parseInt(value);
							}
							catch (NumberFormatException ex) {
							}
							glyphAttributeType.addAttribute(t2, value, t2);
						}

						if (t2 == -1 && !value.equals("-1")) {
							this.generalManager.getLogger().log(
								new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
									"GlyphDataLoader: No data mapping found for " + tmpStorage.getLabel()
										+ " value " + value));
						}

						temp2[i] = t2;

						glyphAttributeType.incDistribution(t2);
					}
					aliStoreMapped.add(temp2);

					glyphAttributeType.setInternalColumnNumber(pcounter);
					++pcounter;

				}
				else { // its something for the string storage
					if (tmpStorage instanceof NominalStorage
						&& tmpStorage.getRawDataType() == ERawDataType.STRING) {
						alsStoreString.add((INominalStorage<String>) tmpStorage);
					}
					else {
						System.out.println("ERROR " + tmpStorage.getLabel());
					}
				}

				++counter;
			}
		}

		if (aliStoreMapped.size() <= 0) {
			this.generalManager.getLogger()
				.log(
					new Status(IStatus.ERROR, IGeneralManager.PLUGIN_ID,
						"GlyphDataLoader: No data in file found"));
			return;
		}

		IIDMappingManager IdMappingManager = generalManager.getIDMappingManager();
		// now convert the storages to real glyphs

		// if (!IdMappingManager.hasMapping(EIDType.EXPERIMENT, EIDType.EXPERIMENT_INDEX)) {
		// this.generalManager.getLogger().log(
		// new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
		// "GlyphDataLoader: No ID Mapping found - using internal ids"));
		// }

		int counter = gman.getGlyphs().size();
		int iExperimentID = 0;
		for (int i = 0; i < aliStoreMapped.get(0).length; ++i) {
			// Extract glyph ID from mapping
			try {
				iExperimentID =
					IdMappingManager.getID(EIDType.EXPERIMENT, EIDType.EXPERIMENT_INDEX, alsStoreString
						.get(0).getRaw(i));
			}
			catch (Exception NullPointerException) {
				iExperimentID = counter;
				++counter;
			}

			GlyphEntry g = new GlyphEntry(iExperimentID);

			for (int[] s : aliStoreMapped) {
				g.addParameter(s[i]);
			}

			for (int j = 0; j < alsStoreString.size(); ++j) {
				g.addStringParameter(alsStoreString.get(j).getLabel(), alsStoreString.get(j).getRaw(i));
			}

			glyphs.put(iExperimentID, g);
		}

		generalManager.getGlyphManager().addGlyphs(glyphs, glyphData.getLabel());
	}
}
