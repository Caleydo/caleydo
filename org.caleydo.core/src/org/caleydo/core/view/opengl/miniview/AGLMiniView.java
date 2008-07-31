package org.caleydo.core.view.opengl.miniview;

import java.util.ArrayList;
import javax.media.opengl.GL;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.selection.Selection;

/**
 * Abstract class for all kinds of mini views.
 * 
 * @author Marc Streit
 */
public abstract class AGLMiniView
	implements IGLMiniView
{

	protected float fHeight;

	protected float fWidth;

	protected ArrayList<IStorage> alStorage;

	protected ArrayList<IStorage> alSetSelection;

	public void setData(ArrayList<IStorage> alStorages, ArrayList<Selection> alSetSelection)
	{

	}

	public abstract void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin);

	public final float getWidth()
	{

		return fWidth;
	}

	public final float getHeight()
	{

		return fHeight;
	}

	public void setWidth(final float fWidth)
	{

		this.fWidth = fWidth;
	}

	public void setHeight(final float fHeight)
	{

		this.fHeight = fHeight;
	}
}
