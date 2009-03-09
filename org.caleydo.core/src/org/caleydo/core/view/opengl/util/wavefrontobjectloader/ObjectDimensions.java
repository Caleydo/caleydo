package org.caleydo.core.view.opengl.util.wavefrontobjectloader;

/**
 * This stores the dimension of a object model. This is used in the glyph part for storing/calculating the
 * actual size of the object(part), so it doesn't need to calculate it every time.
 * 
 * @author Stefan Sauer
 */
public class ObjectDimensions {
	double xLowest = Float.MAX_VALUE;
	double xHighest;

	double yLowest = Float.MAX_VALUE;
	double yHighest;

	double zLowest = Float.MAX_VALUE;
	double zHighest;

	double xScale = 1;
	double yScale = 1;
	double zScale = 1;

	public ObjectDimensions() {
	}

	public ObjectDimensions(ObjectDimensions dim) {
		xLowest = dim.getLowestX(false);
		xHighest = dim.getHighestX(false);

		yLowest = dim.getLowestY(false);
		yHighest = dim.getHighestY(false);

		zLowest = dim.getLowestZ(false);
		zHighest = dim.getHighestZ(false);

		yScale = dim.getScaleY();
	}

	public void updateX(float f) {
		if (xLowest > f)
			xLowest = f;
		if (xHighest < f)
			xHighest = f;
	}

	public void updateY(float f) {
		if (yLowest > f)
			yLowest = f;
		if (yHighest < f)
			yHighest = f;
	}

	public void updateZ(float f) {
		if (zLowest > f)
			zLowest = f;
		if (zHighest < f)
			zHighest = f;
	}

	public float getLowestX(boolean scaled) {
		if (scaled)
			return (float) (xLowest * xScale);
		return (float) xLowest;
	}

	public float getHighestX(boolean scaled) {
		if (scaled)
			return (float) (xHighest * xScale);
		return (float) xHighest;
	}

	public float getLowestY(boolean scaled) {
		if (scaled)
			return (float) (yLowest * yScale);
		return (float) yLowest;
	}

	public float getHighestY(boolean scaled) {
		if (scaled)
			return (float) (yHighest * yScale);
		return (float) yHighest;
	}

	public float getLowestZ(boolean scaled) {
		if (scaled)
			return (float) (zLowest * zScale);
		return (float) zLowest;
	}

	public float getHighestZ(boolean scaled) {
		if (scaled)
			return (float) (zHighest * zScale);
		return (float) zHighest;
	}

	public void scaleY(float scale) {
		yScale = scale;
	}

	public float getScaleY() {
		return (float) yScale;
	}

	public float getScaleOffsetY(boolean scaled) {
		double offset = yLowest - yLowest * yScale;
		if (scaled)
			return (float) (offset * 1.0 / yScale);
		else
			return (float) offset;
	}

}
