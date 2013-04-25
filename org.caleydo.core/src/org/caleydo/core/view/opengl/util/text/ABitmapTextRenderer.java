package org.caleydo.core.view.opengl.util.text;
/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/


import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.geom.Rect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ABitmapTextRenderer {

	private final Font renderFont;
	private final Font layoutFont;
	private final Rectangle2D maxBounds;

	protected final float baseLine;
	private final Graphics2D graphics;
	private final Dimension graphicsSize;
	private final FontRenderContext frc;
	private final Vec2f pos;

	private final Cache<String, GlyphVector> cache = CacheBuilder.newBuilder().maximumSize(200)
			.build(new CacheLoader<String, GlyphVector>() {
				@Override
				public GlyphVector load(String t) throws Exception {
					return layoutFont.layoutGlyphVector(frc, t.toCharArray(), 0, t.length(), Font.LAYOUT_LEFT_TO_RIGHT);
				}
			});

	protected final Map<Character, CharacterInfo> chars = new TreeMap<>();

	public ABitmapTextRenderer(Font base) {
		this.renderFont = advancedFeatures(base, false);
		this.layoutFont = advancedFeatures(base, true);
		Rectangle2D bounds = this.renderFont.getMaxCharBounds(new FontRenderContext(null, true, false)); // just a guess

		Pair<Graphics2D, Dimension> p = createGraphics(bounds.getBounds());
		this.graphics = p.getFirst();
		this.graphicsSize = p.getSecond();
		this.frc = this.graphics.getFontRenderContext();
		this.graphics.setFont(renderFont);
		this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		this.graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		maxBounds = this.renderFont.getMaxCharBounds(frc); // real values
		this.baseLine = -(float) maxBounds.getY();

		pos = new Vec2f(0, baseLine);

		upload('!', '~');
	}

	protected abstract Pair<Graphics2D, Dimension> createGraphics(Rectangle maxBounds);

	public final void upload(char from, char to) {
		StringBuilder b = new StringBuilder();
		for (char c = from; c <= to; ++c) {
			if (isCached(c))
				continue;
			b.append(c);
		}
		uploadImpl(b);
	}

	protected final boolean filterChar(char c) {
		return !renderFont.canDisplay(c) || Character.isWhitespace(c);
	}

	protected final boolean isCached(char c) {
		return filterChar(c) || chars.containsKey(c);
	}

	public final void upload(CharSequence text) {
		StringBuilder b = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (isCached(c))
				continue;
			b.append(c);
		}
		if (b.length() > 0)
			uploadImpl(b);
	}


	private void uploadImpl(CharSequence csq) {
		int act = 0;
		do {
			int max = maxFreeChars();
			if (max == 0) { //next line
				pos.set(0.0f, pos.y() + (float) maxBounds.getHeight());
				max = maxFreeChars();
			}
			CharSequence subSequence = csq.subSequence(act, Math.min(csq.length(), act + max));
			render(subSequence);
			act += subSequence.length();
		} while (act < csq.length());

	}

	private int maxFreeChars() {
		return (int) Math.round(Math.floor(graphicsSize.getWidth() - pos.x()) / maxBounds.getWidth());
	}

	/**
	 * @param subSequence
	 */
	private void render(CharSequence csq) {
		GlyphVector glyphVector = renderFont.createGlyphVector(frc, csq.toString());

		Rectangle bounds = glyphVector.getVisualBounds().getBounds();
		bounds.y += baseLine;
		graphics.drawGlyphVector(glyphVector, pos.x(), pos.y());
		for(int i = 0; i < csq.length(); ++i) {
			char c = csq.charAt(i);
			Rectangle gbounds = glyphVector.getGlyphPixelBounds(i, frc, pos.x(), pos.y());

			Rect r = new Rect(gbounds.x, gbounds.y, gbounds.width, gbounds.height);
			CharacterInfo info = new CharacterInfo(r);
			chars.put(c, info);
		}
		pos.setX(pos.x() + bounds.width);
		markDirty(bounds);
	}

	protected abstract void markDirty(Rectangle bounds);

	private static Font advancedFeatures(Font base, boolean enable) {
		HashMap<TextAttribute, Object> map = new HashMap<>(base.getAttributes());
		if (enable) {
			map.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		} else {
			map.remove(TextAttribute.KERNING);
			map.put(TextAttribute.TRACKING, 0.5);
		}
		return base.deriveFont(map);
	}

	protected final static class CharacterInfo {
		private final Rect bounds;

		public CharacterInfo(Rect bounds) {
			this.bounds = bounds;
		}

		/**
		 * @return the bounds, see {@link #bounds}
		 */
		public Rect getBounds() {
			return bounds;
		}
	}

	public final float getTextWidth(String text, float height) {
		GlyphVector glyphVector = get(text);
		if (glyphVector == null)
			return 0;
		return (float) glyphVector.getVisualBounds().getWidth() * scale(height);
	}


	protected final GlyphVector get(String text) {
		GlyphVector glyphVector;
		try {
			glyphVector = cache.get(text);
		} catch (ExecutionException e) {
			return null;
		}
		return glyphVector;
	}

	protected final float scale(float height) {
		return height / (float) maxBounds.getHeight();
	}
}
