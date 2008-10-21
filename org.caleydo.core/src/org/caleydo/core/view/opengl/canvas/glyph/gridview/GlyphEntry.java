package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.open.Vec2i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.media.opengl.GL;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;

public class GlyphEntry
{

	private IGeneralManager generalManager;

	private int id_;

	private Vec2i pos_ = new Vec2i();

	private Vec2i posGoTo_;

	private boolean selected_ = false;

	private Vector<Integer> parameter_ = null;

	private HashMap<String, String> vsParameterString = new HashMap<String, String>();

	private GLGlyphGenerator generator_ = null;

	private int glList_ = 0;

	private int glListSelected_ = 0;

	public GlyphEntry(int id, GLGlyphGenerator generator)
	{
		this.generalManager = GeneralManager.get();
		id_ = id;
		generator_ = generator;
		parameter_ = new Vector<Integer>();

		posGoTo_ = new Vec2i();
		posGoTo_.setXY(0, 0);
		pos_ = new Vec2i();
		pos_.setXY(0, 0);
	}

	public int getID()
	{

		return id_;
	}

	public int getX()
	{

		return pos_.x();
	}

	public int getY()
	{

		return pos_.y();
	}

	public Vec2i getXY()
	{

		return pos_;
	}

	public void setPosition(int x, int y)
	{

		pos_.setXY(x, y);
	}

	public void select()
	{
		if (selected_)
			return;

		selected_ = true;
		for (int i = 0; i < parameter_.size(); ++i)
			generalManager.getGlyphManager().getGlyphAttributeTypeWithInternalColumnNumber(i)
					.incSelectedDistribution(parameter_.get(i));
	}

	public void deSelect()
	{
		if (!selected_)
			return;

		selected_ = false;
		for (int i = 0; i < parameter_.size(); ++i)
			generalManager.getGlyphManager().getGlyphAttributeTypeWithInternalColumnNumber(i)
					.decSelectedDistribution(parameter_.get(i));
	}

	public boolean isSelected()
	{

		return selected_;
	}

	public int getGlList(GL gl)
	{

		if (selected_)
		{
			return glListSelected_;
		}

		return glList_;
	}

	public void addParameter(int value)
	{

		parameter_.add(value);
	}

	public void addStringParameter(String column, String value)
	{

		if (vsParameterString.containsKey(column))
			vsParameterString.remove(column);
		vsParameterString.put(column, value);
	}

	public int getParameter(int index)
	{

		if (parameter_.size() <= index)
			return -1;
		return parameter_.get(index);
	}

	public String getStringParameter(String colname)
	{

		if (!vsParameterString.containsKey(colname))
			return "";
		return vsParameterString.get(colname);
	}

	public ArrayList<String> getStringParameterColumnNames()
	{
		ArrayList<String> temp = new ArrayList<String>();
		temp.addAll(vsParameterString.keySet());
		return temp;
	}

	public void generateGLLists(GL gl)
	{

		glListSelected_ = generator_.generateGlyph(gl, this, true);
		glList_ = generator_.generateGlyph(gl, this, false);
	}

}
