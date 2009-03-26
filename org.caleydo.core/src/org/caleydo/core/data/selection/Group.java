package org.caleydo.core.data.selection;

public class Group {

	private int iNrElements;
	private boolean bCollapsed;
	private int iIdxExample;
	private ESelectionType eSelectionType;

	public Group(int iNrElements) {
		this.setNrElements(iNrElements);
		this.setCollapsed(false);
		this.setIdxExample(0);
		this.setSelectionType(ESelectionType.NORMAL);
	}

	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, ESelectionType eSelectionType) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setSelectionType(eSelectionType);
	}

	public void setNrElements(int iNrElements) {
		this.iNrElements = iNrElements;
	}

	public int getNrElements() {
		return iNrElements;
	}

	public void setCollapsed(boolean bCollapsed) {
		this.bCollapsed = bCollapsed;
	}

	public boolean isCollapsed() {
		return bCollapsed;
	}

	public void setIdxExample(int iIdxExample) {
		this.iIdxExample = iIdxExample;
	}

	public int getIdxExample() {
		return iIdxExample;
	}

	public void setSelectionType(ESelectionType eSelectionType) {
		this.eSelectionType = eSelectionType;
	}

	public ESelectionType getSelectionType() {
		return eSelectionType;
	}
}
