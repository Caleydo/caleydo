package org.caleydo.core.view.opengl.canvas.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.ccontainer.EDataKind;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.manager.IGeneralManager;


/**
 * Loading data into the glyph storage
 * 
 * @author Stefan Sauer
 *
 */
public class GlyphDataLoader {
	private IGeneralManager generalManager;

	private HashMap<Integer, GlyphEntry> glyphs = new HashMap<Integer, GlyphEntry>();

	public GlyphDataLoader(final IGeneralManager generalManager) {
		this.generalManager = generalManager;
	}

	public HashMap<Integer, GlyphEntry> getGlyphList() {
		return glyphs;
	}



	public HashMap<Integer, GlyphEntry> loadGlyphs(ISet glyphData)
	{
		GLCanvasGlyphGenerator generator = generalManager.getGlyphManager().getGlyphGenerator();
		
		glyphs = new HashMap<Integer, GlyphEntry>();
		 
		ArrayList<IStorage> alStorages = new ArrayList<IStorage>();
		for(IStorage tmpStorage : glyphData)
		{	
			alStorages.add(tmpStorage);
		}
		
		ArrayList<int[]> aliStoreMapped = new ArrayList<int[]>();
		ArrayList<String[]> alsStoreString = new ArrayList<String[]>();
		ArrayList<String> alsStoreStringColTitel = new ArrayList<String>();


		{	//convert values to dictionary indices
			int counter=0;
			int pcounter=0;
			for(IStorage tmpStorage : alStorages)
			{
				GlyphAttributeType glyphAttributeType = generalManager.getGlyphManager().getGlyphAttributeTypeWithExternalColumnNumber(counter);
				
				if(glyphAttributeType!=null) { //input column is defined
					
					if(tmpStorage instanceof NominalStorage && 
							tmpStorage.getRawDataType() == ERawDataType.STRING)
					{
						INominalStorage<String> nominalStorage = (INominalStorage<String>)tmpStorage;
						//String[] temp1 = tmpStorage.getArrayString();
						int[]    temp2 = new int[nominalStorage.size()];
						
						for(int i=0;i<nominalStorage.size();++i) 
						{
							int t2 = glyphAttributeType.getIndex(nominalStorage.get(EDataKind.RAW,i));
							
							if(nominalStorage.get(EDataKind.RAW, i) == null) {
								this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: no String data found - empty line in csv file?????");
								temp2[i] = -1;
							}
							
							if(glyphAttributeType.doesAutomaticAttribute() && t2 == -1)
							{
								try {
									t2 = Integer.parseInt(nominalStorage.get(EDataKind.RAW, i));
								} catch (NumberFormatException ex) { }
								glyphAttributeType.addAttribute(t2, nominalStorage.get(EDataKind.RAW, i), (float)t2);
							}
							
							if(t2 == -1	&& !(nominalStorage.get(EDataKind.RAW, i)).equals("-1"))								
								this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: No data mapping found for " + tmpStorage.getLabel() + " value " + nominalStorage.get(EDataKind.RAW, i));
							
							temp2[i] = t2;
							
							glyphAttributeType.incDistribution(t2);
						}
						aliStoreMapped.add(temp2);
					}
					else
					{
						this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: ERROR. There should be only STRING values in the storag " + tmpStorage.getLabel() );
					}
					
					glyphAttributeType.setInternalColumnNumber(pcounter);
					++pcounter;
				}
				else 
				{
					if(tmpStorage instanceof NominalStorage && 
							tmpStorage.getRawDataType() == ERawDataType.STRING)
					{
						alsStoreStringColTitel.add( tmpStorage.getLabel() );
						
						// FIXME hack
						String[] sArTmp = new String[tmpStorage.size()];
						for (int iCount = 0; iCount < tmpStorage.size(); iCount++)
						{
							sArTmp[iCount] = ((INominalStorage<String>)tmpStorage).get(EDataKind.RAW, iCount);
						}
						alsStoreString.add(sArTmp);
					}
					else
						System.out.println("ERROR" + tmpStorage.getLabel());
				}

			
				++counter;
			}
		}
		
		if(aliStoreMapped.size() <= 0) {
			this.generalManager.getLogger().log(Level.SEVERE, "GlyphDataLoader: No data in file found");
			return null;
		}
		

		int size = aliStoreMapped.get(0).length;

		// now convert the storages to real glyphs
		int counter=1;
		for(int i=0;i<size; ++i)
		{
			GlyphEntry g = new GlyphEntry(generalManager, counter, generator);

			for(int[] s : aliStoreMapped)
				g.addParameter(s[i]);
				
			for(int j=0;j<alsStoreStringColTitel.size();++j)
				g.addStringParameter(alsStoreStringColTitel.get(j), alsStoreString.get(j)[i]);

			glyphs.put(counter, g);
			++counter;
		}
		
		generalManager.getGlyphManager().addGlyphs(glyphs);

		return glyphs;
	}
	
}
