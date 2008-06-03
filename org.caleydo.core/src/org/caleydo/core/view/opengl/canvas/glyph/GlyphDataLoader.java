package org.caleydo.core.view.opengl.canvas.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.manager.IGeneralManager;


public class GlyphDataLoader {
	private IGeneralManager generalManager;
	
	private GLCanvasGlyphGenerator generator = null;
	HashMap<Integer, GlyphEntry> glyphs = new HashMap<Integer, GlyphEntry>();

	Integer[] aSortOrder = null;
	int iSelectReturnParameter = 0;

	public GlyphDataLoader(final IGeneralManager generalManager) {
		this.generalManager = generalManager;
	}

	
	public Integer[] getSortOrder() {
		return aSortOrder;
	}
	
	public int getSelectionSubmitIndex() {
		return iSelectReturnParameter;
	}
	
	public HashMap<Integer, GlyphEntry> getGlyphList() {
		return glyphs;
	}
	
	
	
	public void setupGlyphGenerator(ISet glyphMapping) {
		generator = new GLCanvasGlyphGenerator();
		
		IStorage storageGlyph = glyphMapping.getStorageByDimAndIndex(0, 0);
		IStorage storageData = glyphMapping.getStorageByDimAndIndex(0, 1);
		
		int[] g = storageGlyph.getArrayInt();
		int[] d = storageData.getArrayInt();
		
		ArrayList<Integer> sort = new ArrayList<Integer>();
		
		for(int i=0;i<g.length;++i) {
			if(g[i] ==  0) sort.add(d[i]);
			if(g[i] ==  1) generator.setIndexTopColor(d[i]);
			if(g[i] ==  2) generator.setIndexBoxColor(d[i]);
			if(g[i] ==  3) generator.setIndexHeight(d[i]);
			if(g[i] ==  4) iSelectReturnParameter = d[i];
		}
		
		aSortOrder = sort.toArray(new Integer[sort.size()]);
	}
	
	
	
	public HashMap<Integer, GlyphEntry> loadGlyphs(GL gl, ISet glyphData) {
		glyphs = new HashMap<Integer, GlyphEntry>();

		
		IStorage[] stores = glyphData.getStorageByDim(0);
		
		IStorage storageId = glyphData.getStorageByDimAndIndex(0, 0);
		int size = storageId.getSize(StorageType.INT);
		
		int maxHeight = glyphData.getStorageByDimAndIndex(0, generator.getIndexHeight() ).getMaxInt();
		generator.setMaxHeight(maxHeight);
		
		int counter=1;
		for(int i=0;i<size; ++i) {
				GlyphEntry g = new GlyphEntry(counter, generator);
				//GlyphEntry g = new GlyphEntry(storageId.getArrayInt()[i], this);
				
				for(IStorage s : stores)
					g.addParameter(s.getArrayInt()[i]);
				
				glyphs.put(counter, g);
				//glyphs.put(storageId.getArrayInt()[i], g);
				++counter;
		}
		 
		return glyphs;
	}
	
	

	public void setupGlyphDictionary(ISet glyphDictionary) {
		if(glyphDictionary == null) {
			this.generalManager.getLogger().log(Level.WARNING, "No Glyph dictionary datastructure found");
			return;
		}
		
		
		
		HashMap<String, GlyphAttributeType> dataTypes = new HashMap<String, GlyphAttributeType>();

		IStorage storageIDs     = glyphDictionary.getStorageByDimAndIndex(0, 0);
		IStorage storageDomains = glyphDictionary.getStorageByDimAndIndex(0, 1);
		int size = storageDomains.getSize(StorageType.STRING);
		
		System.out.println( storageDomains.getLabel() );
		
		
		
		for(int i=0;i<size;++i) {
			System.out.println( storageDomains.getArrayString()[i] );
		}
		
		
		// TODO Auto-generated method stub
		
	}
	

	
	
	

}
