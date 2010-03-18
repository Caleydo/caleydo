package org.caleydo.view.compare.state;

import java.util.ArrayList;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class DetailBand {

	private ArrayList<Integer> contentIDs;
	
	private GLHeatMap leftHeatMap;
	private GLHeatMap rightHeatMap;
	
	public void setContentIDs(ArrayList<Integer> contentIDs) {
		this.contentIDs = contentIDs;
	}
	
	public ArrayList<Integer> getContentIDs() {
		return contentIDs;
	}
	
	public void setLeftHeatMap(GLHeatMap leftHeatMap) {
		this.leftHeatMap = leftHeatMap;
	}
	
	public GLHeatMap getLeftHeatMap() {
		return leftHeatMap;
	}
	
	public void setRightHeatMap(GLHeatMap rightHeatMap) {
		this.rightHeatMap = rightHeatMap;
	}
	
	public GLHeatMap getRightHeatMap() {
		return rightHeatMap;
	}
}
