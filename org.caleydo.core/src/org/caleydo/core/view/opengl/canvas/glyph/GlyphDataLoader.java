package org.caleydo.core.view.opengl.canvas.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
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
		 
		IStorage[] stores = glyphData.getStorageByDim(0);
		
		
		ArrayList<int[]> aliStoreMapped = new ArrayList<int[]>();
		ArrayList<String[]> alsStoreString = new ArrayList<String[]>();
		ArrayList<String> alsStoreStringColTitel = new ArrayList<String>();


		{	//convert values to dictionary indices
			int counter=0;
			int pcounter=0;
			for(IStorage s : stores)
			{
				GlyphAttributeType t = generalManager.getGlyphManager().getGlyphAttributeTypeWithExternalColumnNumber(counter);
				
				if(t!=null) { //input column is defined
					
					if(null != s.getArrayString() ) {
						String[] temp1 = s.getArrayString();
						int[]    temp2 = new int[temp1.length];
						
						for(int i=0;i<temp1.length;++i) {
							int t2 = t.getIndex(temp1[i]);
							
							if(temp1[i] == null) {
								this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: no String data found - empty line in csv file?????");
								temp2[i] = -1;
							}
							
							if(t.doesAutomaticAttribute() && t2 == -1)
							{
								try {
									t2 = Integer.parseInt(temp1[i]);
								} catch (NumberFormatException ex) { }
								t.addAttribute(t2, temp1[i], (float)t2);
							}
							
							if(t2 == -1	&& !(temp1[i]).equals("-1"))								
								this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: No data mapping found for " + s.getLabel() + " value " + temp1[i]);
							
							temp2[i] = t2;
							
							t.incDistribution(t2);
						}
						aliStoreMapped.add(temp2);
					}
					else
					{
						this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: ERROR. There should be only STRING values in the storag " + s.getLabel() );
					}
					
					t.setInternalColumnNumber(pcounter);
					++pcounter;
				}
				else 
				{
					//try if string
					if(null != s.getArrayString())
					{
						alsStoreStringColTitel.add( s.getLabel() );
						alsStoreString.add( s.getArrayString() );
					}
					else
						System.out.println("ERROR" + s.getLabel());
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
