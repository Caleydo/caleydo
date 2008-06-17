package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.manager.IGeneralManager;


public class GlyphDataLoader {
	private IGeneralManager generalManager;

	private GLCanvasGlyphGenerator generator = null;
	private HashMap<Integer, GlyphEntry> glyphs = new HashMap<Integer, GlyphEntry>();
	private HashMap<String, GlyphAttributeType> dataTypes = new HashMap<String, GlyphAttributeType>();
	private HashMap<Integer, String> parameterLabels = new HashMap<Integer, String>();
	private HashMap<String, Integer> parameterLabelIndex = new HashMap<String, Integer>();

	
	private Integer[] aSortOrder = null;
	private Vec2i scatterPlotAxis = new Vec2i();
	private int iSendParameter = 0;

	public GlyphDataLoader(final IGeneralManager generalManager) {
		this.generalManager = generalManager;
		generator = new GLCanvasGlyphGenerator();
	}

	public Integer[] getSortOrder() {
		return aSortOrder;
	}

	
	public final Vec2i getScatterPlotAxis() {
		return scatterPlotAxis;
	}

	public HashMap<Integer, GlyphEntry> getGlyphList() {
		return glyphs;
	}

	public int getSendParameter() {
		return iSendParameter;
	}
	
	
	public GlyphAttributeType getGlphAttributeType(String name)
	{
		if(dataTypes.containsKey(name))
			return dataTypes.get(name);
		return null;
	}

	public GlyphAttributeType getGlphAttributeType(int index)
	{
		if(parameterLabels.containsKey(index))
			return getGlphAttributeType(parameterLabels.get(index));
		return null;
	}
	
	public int getParameterIndex(String name)
	{
		if(parameterLabelIndex.containsKey(name))
			return parameterLabelIndex.get(name);
		return -1;
	}
	
	
	


	public void setupGlyphGenerator(ISet glyphMapping) {

		IStorage storageGlyph = glyphMapping.getStorageByDimAndIndex(0, 0);
		IStorage storageData = glyphMapping.getStorageByDimAndIndex(0, 1);

		String[] g = storageGlyph.getArrayString();
		String[] d = storageData.getArrayString();

		ArrayList<Integer> sort = new ArrayList<Integer>();

		for(int i=0;i<g.length;++i) {
			int index = parameterLabelIndex.get(d[i]);
			
			if(g[i].equals("sort"))					sort.add(index);
			if(g[i].equals("topColor"))				generator.setIndexTopColor(index);
			if(g[i].equals("boxColor"))				generator.setIndexBoxColor(index);
			if(g[i].equals("boxHeight"))			generator.setIndexHeight(index);
			if(g[i].equals("scatterPlotAxisX")) 	scatterPlotAxis.setX(index);
			if(g[i].equals("scatterPlotAxisY"))		scatterPlotAxis.setY(index);
			if(g[i].equals("updateSendParameter"))	iSendParameter = index;

		}

		//set max height value
		try {
			int maxHeight = dataTypes.get( parameterLabels.get( generator.getIndexHeight() ) ).getMaxIndex();
			generator.setMaxHeight(maxHeight);
		} catch(Exception ex) {
			this.generalManager.getLogger().log(Level.WARNING, "height index does not match!" );
		}

		aSortOrder = sort.toArray(new Integer[sort.size()]);
	}



	public HashMap<Integer, GlyphEntry> loadGlyphs(ISet glyphData)
	{
		glyphs = new HashMap<Integer, GlyphEntry>();
		IStorage[] stores = glyphData.getStorageByDim(0);

		ArrayList<int[]> aliStoreMapped = new ArrayList<int[]>();
		ArrayList<String[]> alsStoreString = new ArrayList<String[]>(); 


		{	//convert values to dictionary indices
			int counter=0;
			for(IStorage s : stores)
			{
				GlyphAttributeType t = this.dataTypes.get(s.getLabel());
				
				if(t!=null) { //std mapping
					//nominal
					if(null != s.getArrayString() ) {
						String[] temp1 = s.getArrayString();
						int[]    temp2 = new int[temp1.length];
						
						for(int i=0;i<temp1.length;++i) {
							int t2 = t.getIndex(temp1[i]);
							
							if(temp1[i] == null) {
								this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: no String data found - empty line in csv file?????");
								temp2[i] = -1;
							}
								
							if(t2 == -1	&& !(temp1[i]).equals("-1"))								
								this.generalManager.getLogger().log(Level.WARNING, "GlyphDataLoader: No data mapping found for " + s.getLabel() + " value " + temp1[i]);
							
							temp2[i] = t2;
						}
						aliStoreMapped.add(temp2);
						parameterLabels.put(aliStoreMapped.size()-1, s.getLabel());
						parameterLabelIndex.put(s.getLabel() , aliStoreMapped.size()-1);
					}
					//ordinal
					else if(null != s.getArrayFloat() ) {
						
						parameterLabels.put(aliStoreMapped.size()-1, s.getLabel());
						parameterLabelIndex.put(s.getLabel() , aliStoreMapped.size()-1);
					}
					//already mapped integer
					else if(null != s.getArrayInt() ) {
						aliStoreMapped.add(s.getArrayInt());
						parameterLabels.put(aliStoreMapped.size()-1, s.getLabel());
						parameterLabelIndex.put(s.getLabel() , aliStoreMapped.size()-1);
					}
					else {
						
					}
//					g.addParameter(s.getArrayInt()[i]);
				}
				else 
				{
					//try if integer
					if(null != s.getArrayInt()) {
						aliStoreMapped.add(s.getArrayInt());
						parameterLabels.put(aliStoreMapped.size()-1, s.getLabel());
						parameterLabelIndex.put(s.getLabel() , aliStoreMapped.size()-1);
						//t.setParameterIndex(aliStoreMapped.size()-1);
					}
					
					//try if string
					else if(null != s.getArrayString())
						alsStoreString.add( s.getArrayString() );

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
		

		//IStorage storageId = glyphData.getStorageByDimAndIndex(0, 0);
		int size = aliStoreMapped.get(0).length;

		
		int counter=1;
		for(int i=0;i<size; ++i)
		{
				GlyphEntry g = new GlyphEntry(counter, generator);

				for(int[] s : aliStoreMapped)
					g.addParameter(s[i]);
				//for(IStorage s : stores)
				//	g.addParameter(s.getArrayInt()[i]);

				glyphs.put(counter, g);
				++counter;
				
		}

		return glyphs;
	}



	public void setupGlyphDictionary(ISet glyphDictionary)
	{
		if(glyphDictionary == null)
		{
			this.generalManager.getLogger().log(Level.SEVERE, "No Glyph dictionary datastructure found");
			return;
		}



		//IStorage storageIDs     = glyphDictionary.getStorageByDimAndIndex(0, 0);
		IStorage storageDomains = glyphDictionary.getStorageByDimAndIndex(0, 1);
		IStorage storageNominal = glyphDictionary.getStorageByDimAndIndex(0, 2);
		IStorage storageGroup   = glyphDictionary.getStorageByDimAndIndex(0, 3);
		IStorage storageOrdinal = glyphDictionary.getStorageByDimAndIndex(0, 4);

		int size = storageDomains.getSize(StorageType.STRING);

		//System.out.println( storageDomains.getLabel() );

		//generate attribure groups
		for(int i=0;i<size;++i)
		{
			String key = storageDomains.getArrayString()[i];
			if( !dataTypes.containsKey(key) )
				dataTypes.put(key, new GlyphAttributeType(generalManager, key) );
		}

		//insert data
		for(int i=0;i<size;++i)
		{
			String key           = storageDomains.getArrayString()[i];
			String value_nominal = storageNominal.getArrayString()[i];
			float  value_ordinal = storageOrdinal.getArrayFloat()[i];
			int    value_group   = storageGroup.getArrayInt()[i];

			dataTypes.get(key).addAttribute(value_group, value_nominal, value_ordinal);
		}
		
		



		/*
		for(int i=0;i<size;++i) {
			System.out.println( storageNominal.getArrayString()[i] );
		}
*/


	}






}
