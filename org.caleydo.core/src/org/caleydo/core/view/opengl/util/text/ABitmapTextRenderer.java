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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * a text renderer similar to {@link TextRenderer} but on a by glph basis
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ABitmapTextRenderer {

	private final Font renderFont;
	private final Font layoutFont;
	/**
	 * maximal bounds of a glyph
	 */
	private final Rectangle2D maxBounds;

	protected final float baseLine;

	private final Graphics2D graphics;
	private final Dimension graphicsSize;
	private final FontRenderContext frc;

	/**
	 * current position within the texture glyph cache
	 */
	private final Vec2f pos;

	/**
	 * whether scaling should be based on the {@link #baseLine} or on the {@link #maxBounds} height
	 */
	private boolean scaleByBaseLine = true;

	/**
	 * cache of layouted text
	 */
	private final Cache<String, GlyphVector> cache = CacheBuilder.newBuilder().maximumSize(200)
			.build(new CacheLoader<String, GlyphVector>() {
				@Override
				public GlyphVector load(String t) throws Exception {
					return layoutFont.layoutGlyphVector(frc, t.toCharArray(), 0, t.length(), Font.LAYOUT_LEFT_TO_RIGHT);
				}
			});

	/**
	 * information where a char is within the texture
	 */
	protected final Map<Character, CharacterInfo> chars = new TreeMap<>();

	public ABitmapTextRenderer(Font base) {
		this.renderFont = advancedFeatures(base, false);
		this.layoutFont = advancedFeatures(base, true);
		Rectangle2D bounds = this.renderFont.getMaxCharBounds(new FontRenderContext(null, true, false)); // just a guess

		Pair<Graphics2D, Dimension> p = createGraphics(bounds.getBounds());
		this.graphics = p.getFirst();
		this.graphicsSize = p.getSecond();
		this.frc = this.graphics.getFontRenderContext();
		this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		this.graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		this.graphics.setFont(renderFont);

		maxBounds = this.renderFont.getMaxCharBounds(frc); // real values
		this.baseLine = -(float) maxBounds.getY();

		pos = new Vec2f(0, baseLine);

		upload('!', '~'); // upload a bunch of characters
	}

	protected abstract Pair<Graphics2D, Dimension> createGraphics(Rectangle maxBounds);

	/**
	 * uploads all chars between the two given one to the texture
	 *
	 * @param from
	 * @param to
	 */
	public final void upload(char from, char to) {
		StringBuilder b = new StringBuilder();
		for (char c = from; c <= to; ++c) {
			if (isCached(c))
				continue;
			b.append(c);
		}
		uploadImpl(b);
	}

	/**
	 * do we need to render this char
	 *
	 * @param c
	 * @return
	 */
	protected final boolean filterChar(char c) {
		return !renderFont.canDisplay(c) || Character.isWhitespace(c);
	}

	/**
	 * whether this char is {@link #filterChar(char)} or already in the cache
	 *
	 * @param c
	 * @return
	 */
	protected final boolean isCached(char c) {
		return filterChar(c) || chars.containsKey(c);
	}

	/**
	 * uploads all chars in the given {@link CharSequence} if they aren't alredy in it
	 *
	 * @param text
	 */
	public final void upload(CharSequence text) {
		StringBuilder b = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (isCached(c))
				continue;
			b.append(c);
		}
		uploadImpl(b);
	}


	/**
	 * does the actual uploading
	 *
	 * @param csq
	 */
	private void uploadImpl(CharSequence csq) {
		if (csq.length() <= 0)
			return;
		int act = 0;
		do {
			int max = maxFreeChars(); // guess the minimal batch size which can be used
			if (max == 0) { //next line
				pos.set(0.0f, pos.y() + (float) maxBounds.getHeight());
				max = maxFreeChars();
			}
			// render this batch
			act += render(csq.subSequence(act, Math.min(csq.length(), act + max)));
		} while (act < csq.length());

	}

	/**
	 * returns the number of chars that can be at least renderes within the remaining line
	 *
	 * @return
	 */
	private int maxFreeChars() {
		return (int) Math.round(Math.floor(graphicsSize.getWidth() - pos.x()) / maxBounds.getWidth());
	}

	/**
	 * renders a given {@link CharSequence} to the texture
	 *
	 * @param subSequence
	 */
	private int render(CharSequence csq) {
		GlyphVector glyphVector = renderFont.createGlyphVector(frc, csq.toString());

		Rectangle bounds = glyphVector.getVisualBounds().getBounds();
		bounds.y += baseLine;
		// draw it
		graphics.drawGlyphVector(glyphVector, pos.x(), pos.y());


		float next = (pos.x() + bounds.width + (float) maxBounds.getWidth() * 0.25f);
		int rendered;
		if (next > graphicsSize.width) {
			// not all could be rendered
			// TODO
			rendered = 0;
		} else {
			rendered = csq.length();
			// collect data
			for (int i = 0; i < csq.length(); ++i) {
				char c = csq.charAt(i);
				// Rectangle gbounds = glyphVector.getGlyphPixelBounds(i, frc, pos.x(), pos.y());
				Rectangle2D gbounds2 = (Rectangle2D) glyphVector.getGlyphVisualBounds(i).getBounds2D().clone();
				gbounds2.setRect(gbounds2.getX() + pos.x(), gbounds2.getY() + pos.y(), gbounds2.getWidth(),
						gbounds2.getHeight());
				CharacterInfo info = new CharacterInfo(gbounds2);
				this.chars.put(c, info);
			}
		}

		pos.setX(next); // some space for line spacing
		markDirty(bounds);
		return rendered;
	}

	/**
	 * hook for marking regions of a texture dirty
	 *
	 * @param bounds
	 */
	protected abstract void markDirty(Rectangle bounds);

	/**
	 * enable/disable some advanced font features
	 *
	 * @param base
	 * @param enable
	 * @return the derived font
	 */
	private static Font advancedFeatures(Font base, boolean enable) {
		HashMap<TextAttribute, Object> map = new HashMap<>(base.getAttributes());
		if (enable) {
			map.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		} else {
			map.remove(TextAttribute.KERNING);
			map.remove(TextAttribute.LIGATURES);
			map.put(TextAttribute.TRACKING, 0.3f);
		}
		return base.deriveFont(map);
	}

	protected final static class CharacterInfo {
		private final Rectangle2D bounds;

		public CharacterInfo(Rectangle2D bounds) {
			this.bounds = bounds;
		}

		/**
		 * @return the bounds, see {@link #bounds}
		 */
		public Rectangle2D getBounds() {
			return bounds;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("CharacterInfo [bounds=");
			builder.append(bounds);
			builder.append("]");
			return builder.toString();
		}

	}

	public final float getTextWidth(String text, float height) {
		GlyphVector glyphVector = get(text);
		if (glyphVector == null)
			return 0;
		return (float) (glyphVector.getVisualBounds().getWidth() * scale(height));
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

	/**
	 * scaling factor to use for rendering
	 *
	 * @param height
	 * @return
	 */
	protected final double scale(float height) {
		return height / (scaleByBaseLine ? baseLine : maxBounds.getHeight());
	}
}
