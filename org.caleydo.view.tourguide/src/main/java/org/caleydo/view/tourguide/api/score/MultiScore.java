/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

import com.google.common.collect.Iterators;

/**
 * marker for a default multi score
 *
 * @author Samuel Gratzl
 *
 */
public class MultiScore extends DefaultLabelProvider implements IScore, Iterable<IScore> {
	private final Collection<IScore> children = new ArrayList<>();
	private final Color color;
	private final Color bgColor;
	/**
	 * the combined modi for creating a combined rank column
	 */
	private final int combinedType;

	public MultiScore(String label, Color color, Color bgColor) {
		this(label, color, bgColor, 0);
	}

	public MultiScore(String label, Color color, Color bgColor, int combinedType) {
		setLabel(label);
		this.color = color;
		this.bgColor = bgColor;
		this.combinedType = combinedType;
	}

	/**
	 * @return the combinedType, see {@link #combinedType}
	 */
	public int getCombinedType() {
		return combinedType;
	}

	/**
	 * @param createJaccardME
	 */
	public void add(IScore score) {
		children.add(score);
	}

	@Override
	public Iterator<IScore> iterator() {
		return Iterators.unmodifiableIterator(children.iterator());
	}

	@Override
	public PiecewiseMapping createMapping() {
		return null;
	}

	@Override
	public String getAbbreviation() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Color getBGColor() {
		return bgColor;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public final double apply(IComputeElement elem, Group g) {
		return Double.NaN;
	}

	@Override
	public boolean supports(ITourGuideDataMode mode) {
		for (IScore s : children) {
			if (!s.supports(mode))
				return false;
		}
		return true;
	}

	public Collection<? extends IScore> getChildren() {
		return children;
	}

}
