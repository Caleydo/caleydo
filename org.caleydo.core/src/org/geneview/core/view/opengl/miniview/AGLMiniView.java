package org.geneview.core.view.opengl.miniview;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.set.selection.SetSelection;


public abstract class AGLMiniView 
{
	protected float fHeight;
	protected float fWidth;
	
	protected ArrayList<IStorage> alStorage;
	protected ArrayList<IStorage> alSetSelection;
	
	public void setData(ArrayList<IStorage> alStorages, ArrayList<SetSelection> alSetSelection)
	{
		
	}
	
	public abstract void render(GL gl, float fXOrigin, float fYOrigin);
	
	public final float getWidth()
	{
		return fWidth;
	}
	
	public final float getHeight()
	{
		return fHeight;
	}
	
}
