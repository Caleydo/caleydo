/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;

/**
 * Utility class to get the min size for element containers with common layouts.
 *
 * @author Christian
 *
 */
public class GLMinSizeProviders {

	public static IHasMinSize createHorizontalFlowMinSizeProvider(final Iterable<GLElement> container, final float gap,
			final GLPadding padding) {
		return new IHasMinSize() {

			@Override
			public Vec2f getMinSize() {
				return getHorizontalFlowMinSize(container, gap, padding);
			}
		};
	}

	public static IHasMinSize createVerticalFlowMinSizeProvider(final Iterable<GLElement> container, final float gap,
			final GLPadding padding) {
		return new IHasMinSize() {

			@Override
			public Vec2f getMinSize() {
				return getVerticalFlowMinSize(container, gap, padding);
			}
		};
	}

	public static IHasMinSize createLayeredMinSizeProvider(final Iterable<GLElement> container) {
		return new IHasMinSize() {

			@Override
			public Vec2f getMinSize() {
				return getLayeredMinSize(container);
			}
		};
	}

	public static IHasMinSize createDefaultMinSizeProvider(final Vec2f minSize) {
		return new IHasMinSize() {

			@Override
			public Vec2f getMinSize() {
				return minSize;
			}
		};
	}

	public static IHasMinSize createDefaultMinSizeProvider(float x, float y) {
		return createDefaultMinSizeProvider(new Vec2f(x, y));
	}

	public static Vec2f getHorizontalFlowMinSize(final Iterable<GLElement> container, final float gap,
			final GLPadding padding) {

		float maxHeight = Float.MIN_VALUE;
		float sumWidth = 0;
		int numItems = 0;
		for (GLElement child : container) {
			if (child.getVisibility() != EVisibility.NONE) {
				Vec2f minSize = child.getMinSize();
				sumWidth += minSize.x();
				if (maxHeight < minSize.y())
					maxHeight = minSize.y();

				numItems++;
			}
		}
		return new Vec2f(sumWidth + (numItems - 1) * gap + padding.left + padding.right, maxHeight + padding.top
				+ padding.bottom);

	}

	public static Vec2f getVerticalFlowMinSize(final Iterable<GLElement> container, final float gap,
			final GLPadding padding) {

		float maxWidth = Float.MIN_VALUE;
		float sumHeight = 0;
		int numItems = 0;
		for (GLElement child : container) {
			if (child.getVisibility() != EVisibility.NONE) {
				Vec2f minSize = child.getMinSize();
				sumHeight += minSize.y();
				if (maxWidth < minSize.x())
					maxWidth = minSize.x();

				numItems++;
			}
		}
		return new Vec2f(maxWidth + padding.left + padding.right, sumHeight + (numItems - 1) * gap + padding.top
				+ padding.bottom);

	}

	public static Vec2f getLayeredMinSize(final Iterable<GLElement> container) {

		float maxHeight = Float.MIN_VALUE;
		float maxWidth = Float.MIN_VALUE;
		for (GLElement child : container) {
			if (child.getVisibility() != EVisibility.NONE) {
				Vec2f minSize = child.getMinSize();
				if (maxWidth < minSize.x())
					maxWidth = minSize.x();
				if (maxHeight < minSize.y())
					maxHeight = minSize.y();
			}
		}
		return new Vec2f(maxWidth, maxHeight);

	}

}
