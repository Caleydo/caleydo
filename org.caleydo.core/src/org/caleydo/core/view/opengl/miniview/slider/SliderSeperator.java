package org.caleydo.core.view.opengl.miniview.slider;

/**
 * OpenGL Slider Seperator
 * 
 * @author Stefan Sauer
 */
public class SliderSeperator {
	private int iID = 0;
	private float fPos = 0;
	private SliderSeperatorBond bond = null;

	public SliderSeperator(int id) {
		iID = id;
	}

	public SliderSeperator(int id, float pos) {
		iID = id;
		fPos = pos;
	}

	public int getID() {
		return iID;
	}

	public float getPos() {
		return fPos;
	}

	public void setPos(float pos) {
		fPos = pos;
	}

	public boolean hasSeperatorBond() {
		return bond != null ? true : false;
	}

	public SliderSeperatorBond getBond() {
		return bond;
	}

	public void setBond(SliderSeperatorBond bond) {
		this.bond = bond;
	}

}
