package org.caleydo.core.view.opengl.canvas.radial;

import gleem.linalg.Vec2f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public class LabelLine {
	
	private static final float DEFAULT_LINE_HEIGHT = 0.1f;
	
	private ArrayList<ALabelItem> alLabelItems;
	private ArrayList<TextItem> alTextItems;
	private float fWidth;
	private float fHeight;
	private Vec2f vecPosition;
	
	public LabelLine() {
		alLabelItems = new ArrayList<ALabelItem>();
		alTextItems = new ArrayList<TextItem>();
		vecPosition = new Vec2f(0,0);
		fWidth = 0;
		fHeight = DEFAULT_LINE_HEIGHT;
	}
	
	public void addLabelItem(ALabelItem labelItem) {
		alLabelItems.add(labelItem);
		
		if(labelItem instanceof TextItem) {
			alTextItems.add((TextItem) labelItem);
		}
	}
	
	public void calculateSize(TextRenderer textRenderer, float fTextScaling) {
		
		fHeight = 0;
		fWidth = 0;
		
		for(TextItem currentItem : alTextItems) {
			currentItem.setRenderingProperties(textRenderer, fTextScaling);
			float fItemHeight = currentItem.getHeight();
			if(fItemHeight > fHeight) {
				fHeight = fItemHeight;
			}
		}
		
		if(fHeight <= 0) {
			fHeight = DEFAULT_LINE_HEIGHT;
		}
		
		for(ALabelItem currentItem : alLabelItems) {
			currentItem.setHeight(fHeight);
			fWidth += currentItem.getWidth();
		}
	}
	
	public void draw(GL gl) {
		
		for(ALabelItem currentItem : alLabelItems) {
			currentItem.draw(gl);
		}
	}

	public float getHeight() {
		return fHeight;
	}

	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
	}

	public float getWidth() {
		return fWidth;
	}

	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
	}
	
	public void setPosition(float fXPosition, float fYPosition) {
		vecPosition.set(fXPosition, fYPosition);
		
		for(ALabelItem currentItem : alLabelItems) {
			currentItem.setPosition(fXPosition, fYPosition);
			fXPosition += currentItem.getWidth();
		}
	}
	
	public Vec2f getPosition() {
		return vecPosition;
	}
}
