package org.caleydo.core.data.selection;

public class SelectionQuadruple
{
	private int iTexture;
	private int iPos;
	private int iContentIndex;
	private ESelectionType selectionType;

	public SelectionQuadruple()
	{
		this.iTexture = 0;
		this.iPos = 0;
		this.iContentIndex = 0;
		this.selectionType = ESelectionType.NORMAL;
	}

	public SelectionQuadruple(int iTexture, int iPos, int iContentIndex,
			ESelectionType selectionType)
	{
		this.iTexture = iTexture;
		this.iPos = iPos;
		this.iContentIndex = iContentIndex;
		this.setSelectionType(selectionType);
	}

	public void setTexture(int iTexture)
	{
		this.iTexture = iTexture;
	}

	public int getTexture()
	{
		return iTexture;
	}

	public void setPos(int iPos)
	{
		this.iPos = iPos;
	}

	public int getPos()
	{
		return iPos;
	}

	public void setContentIndex(int iContentIndex)
	{
		this.iContentIndex = iContentIndex;
	}

	public int getContentIndex()
	{
		return iContentIndex;
	}

	public void setSelectionType(ESelectionType selectionType)
	{
		this.selectionType = selectionType;
	}

	public ESelectionType getSelectionType()
	{
		return selectionType;
	}

}
