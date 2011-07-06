package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;

import org.caleydo.core.util.collection.Pair;

public class BandInfo {
	private Pair<Point2D, Point2D> anchorPoints1;
	private Pair<Point2D, Point2D> anchorPoints2;
	private float offset1;
	private float offset2;
	private boolean isOffset1Horizontal;
	private boolean isOffset2Horizontal;

	public BandInfo(Pair<Point2D, Point2D> anchorPoints1,
			Pair<Point2D, Point2D> anchorPoints2, float offset1, float offset2,
			boolean isOffset1Horizontal, boolean isOffset2Horizontal) {
		this.anchorPoints1 = anchorPoints1;
		this.anchorPoints2 = anchorPoints2;
		this.offset1 = offset1;
		this.offset2 = offset2;
		this.isOffset1Horizontal = isOffset1Horizontal;
		this.isOffset2Horizontal = isOffset2Horizontal;

	}

	public void setAnchorPoints1(Pair<Point2D, Point2D> anchorPoints1) {
		this.anchorPoints1 = anchorPoints1;
	}

	public Pair<Point2D, Point2D> getAnchorPoints1() {
		return anchorPoints1;
	}

	public void setAnchorPoints2(Pair<Point2D, Point2D> anchorPoints2) {
		this.anchorPoints2 = anchorPoints2;
	}

	public Pair<Point2D, Point2D> getAnchorPoints2() {
		return anchorPoints2;
	}

	public void setOffset1(float offset1) {
		this.offset1 = offset1;
	}

	public float getOffset1() {
		return offset1;
	}

	public void setOffset2(float offset2) {
		this.offset2 = offset2;
	}

	public float getOffset2() {
		return offset2;
	}

	public void setOffset1Horizontal(boolean isOffset1Horizontal) {
		this.isOffset1Horizontal = isOffset1Horizontal;
	}

	public boolean isOffset1Horizontal() {
		return isOffset1Horizontal;
	}

	public void setOffset2Horizontal(boolean isOffset2Horizontal) {
		this.isOffset2Horizontal = isOffset2Horizontal;
	}

	public boolean isOffset2Horizontal() {
		return isOffset2Horizontal;
	}

}
