/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.text;


import java.util.NavigableMap;
import java.util.TreeMap;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.util.function.IFloatFunction;

/**
 * composite pattern, that has a pool of underlying renderer with different text sizes and automatically selects the
 * best matching one using the given height of the desired text
 *
 * @author Samuel Gratzl
 *
 */
public class CompositeTextRenderer implements ITextRenderer {
	/**
	 * pool of underlying text renderers, with reference_height x renderer
	 */
	private NavigableMap<Float, CaleydoTextRenderer> pool = new TreeMap<>();
	/**
	 * maps the given height in the reference height space, i.e. pixels
	 */
	private final IFloatFunction mapper;

	public CompositeTextRenderer(ETextStyle style, int size, int... sizes) {
		this(FloatFunctions.IDENTITY, style, size, sizes);
	}

	public CompositeTextRenderer(IFloatFunction mapper, ETextStyle style, int size, int... sizes) {
		this.mapper = mapper;
		add(style, size);
		for (int s : sizes)
			add(style, s);
	}

	/**
	 * @param size
	 */
	private void add(ETextStyle style, int size) {
		int awtstyle = style.toAWTFontStyle();
		CaleydoTextRenderer t = new CaleydoTextRenderer(size, awtstyle);
		pool.put(t.getReferenceHeight(), t);
	}

	@Override
	public void setColor(Color color) {
		for (CaleydoTextRenderer t : pool.values())
			t.setColor(color);
	}


	@Override
	public float getTextWidth(String text, float height) {
		CaleydoTextRenderer best = selectBest(height);
		return best.getRequiredTextWidth(text, height);
	}

	@Override
	public void renderTextInBounds(GL2 gl, String text, float x, float y, float z, float w, float h) {
		CaleydoTextRenderer best = selectBest(h);
		best.renderTextInBounds(gl, text, x, y, z, w, h);
	}

	/**
	 * selects the best matching text renderer given the height using the {@link #mapper}
	 *
	 * @param h
	 * @return
	 */
	private CaleydoTextRenderer selectBest(float h) {
		Float hv = Float.valueOf(mapper.apply(h));
		Float select = this.pool.ceilingKey(hv);
		if (select == null)
			select = this.pool.lastKey();
		return this.pool.get(select);
	}

	@Override
	public boolean isDirty() {
		boolean dirty = false;
		for (ITextRenderer t : this.pool.values())
			dirty = t.isDirty() || dirty;
		return dirty;
	}

	@Override
	public boolean isOriginTopLeft() {
		return false;
	}


}
