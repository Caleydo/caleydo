package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

public class Label {

	private ArrayList<LabelLine> alLines;
	private int iSegmentDepth;
	private float fSegmentXCenter;
	private float fSegmentYCenter;
	private float fSegmentCenterRadius;
	
	public Label(float fSegmentXCenter, float fSegmentYCenter, float fSegmentCenterRadius, int iSegmentDepth) {
		this.fSegmentXCenter = fSegmentXCenter;
		this.fSegmentYCenter = fSegmentYCenter;
		this.fSegmentCenterRadius = fSegmentCenterRadius;
		this.iSegmentDepth = iSegmentDepth;
		alLines = new ArrayList<LabelLine>();
	}

	public float getSegmentCenterRadius() {
		return fSegmentCenterRadius;
	}

	public void setSegmentCenterRadius(float fSegmentCenterRadius) {
		this.fSegmentCenterRadius = fSegmentCenterRadius;
	}

	public ArrayList<LabelLine> getLines() {
		return alLines;
	}

	public void addLine(LabelLine labelLine) {
		alLines.add(labelLine);
	}

	public float getSegmentXCenter() {
		return fSegmentXCenter;
	}

	public void setSegmentXCenter(float fSegmentXCenter) {
		this.fSegmentXCenter = fSegmentXCenter;
	}

	public float getSegmentYCenter() {
		return fSegmentYCenter;
	}

	public void setSegmentYCenter(float fSegmentYCenter) {
		this.fSegmentYCenter = fSegmentYCenter;
	}

	public int getSegmentDepth() {
		return iSegmentDepth;
	}

	public void setSegmentDepth(int iSegmentDepth) {
		this.iSegmentDepth = iSegmentDepth;
	}
	
	
	
}
