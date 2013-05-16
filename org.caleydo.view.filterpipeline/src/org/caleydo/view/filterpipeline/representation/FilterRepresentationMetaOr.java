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
package org.caleydo.view.filterpipeline.representation;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.RecordMetaOrFilter;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.picking.SpacePickingManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.PickingType;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * @author Thomas Geymayer
 *
 */
public class FilterRepresentationMetaOr extends FilterRepresentation {
	public static boolean renderPassedAll = true;

	protected SortedSet<Integer> elementsPassed = new TreeSet<Integer>();
	protected int[] subFilterSizes = new int[0];
	protected ArrayList<SortedSet<Integer>> subFiltersPassedElements = null;
	protected ArrayList<Intersection> intersections = new ArrayList<Intersection>();

	protected boolean sizesDirty = true;

	public FilterRepresentationMetaOr(FilterPipelineRenderStyle renderStyle,
 SpacePickingManager pickingManager,
			int viewId) {
		super(renderStyle, pickingManager, viewId);
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer) {
		if (sizesDirty)
			calculateSizes();

		heightLeft = getHeightLeft();
		heightRight = vSize.y() * (filter.getOutput().size() / 100.f);

		gl.glPushName(iPickingID);
		renderBasicShape(gl, textRenderer, renderStyle.FILTER_OR_COLOR);

		for (int i = 0; i < subFilterSizes.length; ++i) {
			gl.glPushName(pickingManager.getPickingID(PickingType.FILTERPIPE_SUB_FILTER.name(), i));
			heightRight = vSize.y() * (subFilterSizes[i] / 100.f);
			renderShape(gl, GL2.GL_QUADS, renderStyle.getFilterColorCombined(i),
					Z_POS_BODY);
			gl.glPopName();
		}
		gl.glPopName();

		// if (renderPassedAll)
		// {
		// // render elements passed all filters
		// heightRight = vSize.y() * (elementsPassedAll.size() / 100.f);
		// renderShape(gl, GL2.GL_QUADS, renderStyle.FILTER_PASSED_ALL_COLOR,
		// Z_POS_BODY);
		// }

		if (mouseOverItem >= 0) {
			heightRight = vSize.y() * (subFilterSizes[mouseOverItem] / 100.f);
			gl.glLineWidth(SelectionType.MOUSE_OVER.getLineWidth());

			renderShape(gl, GL.GL_LINE_LOOP, SelectionType.MOUSE_OVER.getColor(),
					Z_POS_MARK);
		}

		// reset height
		heightRight = vSize.y() * (filter.getOutput().size() / 100.f);

		// render selection/mouseover if needed
		if (selectionType != SelectionType.NORMAL && mouseOverItem < 0) {
			gl.glLineWidth((selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
					.getLineWidth() : SelectionType.MOUSE_OVER.getLineWidth());

			renderShape(
					gl,
					GL.GL_LINE_LOOP,
					(selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
							.getColor() : SelectionType.MOUSE_OVER.getColor(), Z_POS_MARK);
		}
	}

	protected void calculateSizes() {
		sizesDirty = false;

		// TODO also handle dimension filter
		@SuppressWarnings("unchecked")
		VirtualArray input = filter
				.getInput().clone();
		ArrayList<Filter> filterList = ((RecordMetaOrFilter) filter.getFilter())
				.getFilterList();

		subFilterSizes = new int[filterList.size()];
		subFiltersPassedElements = new ArrayList<SortedSet<Integer>>(filterList.size());

		int i = 0;
		for (Filter subFilter : filterList) {
			VirtualArray tempInput = input.clone();
			tempInput.setDelta(subFilter.getVADelta());

			SortedSet<Integer> passedElements = new TreeSet<Integer>();
			for (Integer element : tempInput)
				passedElements.add(element);

			subFilterSizes[i++] = passedElements.size();
			subFiltersPassedElements.add(passedElements);

			System.out.println("SubfilterOut=" + passedElements.size());
		}

		// cache output
		elementsPassed.clear();
		for (Integer element : filter.getOutput())
			elementsPassed.add(element);
		System.out.println("TotalOut=" + elementsPassed.size());

		calculateIntersections();
	}

	/**
	 * Calculate all intersections between every combination of subfilters
	 */
	private void calculateIntersections() {
		System.out.println("Check intersections (" + elementsPassed.size()
				+ " elements):");

		intersections.clear();
		int numSubFilters = subFilterSizes.length;

		// Use to store single contribution of last element to show it on top
		// to reduce unneeded cluttering
		Intersection lastIntersection = null;

		// get all intersections between each permutation of filters
		for (int count = 1; count <= numSubFilters; ++count) {
			System.out.println(" Count = " + count);
			int[] currentFilters = new int[count];

			// start with count filters beginning at the first filter
			for (int i = 0; i < count; ++i)
				currentFilters[i] = i;

			boolean doCheck = true;

			do {
				String s = new String("  [");
				for (int i = 0; i < count; ++i) {
					if (s.length() > 3)
						s += ',';
					s += currentFilters[i];
				}
				System.out.println(s + "]");

				// get output of first filter
				SortedSet<Integer> intersection = new TreeSet<Integer>(
						subFiltersPassedElements.get(currentFilters[0]));

				// remove all elements passing another filter not contained
				// in the intersection
				for (int other = 0; other < numSubFilters; ++other) {
					boolean skipFilter = false;
					for (int i = 0; i < count; ++i) {
						if (currentFilters[i] == other) {
							skipFilter = true;
							break;
						}
					}

					if (!skipFilter)
						intersection.removeAll(subFiltersPassedElements.get(other));
				}

				// calculate intersection with remaining elements
				for (int i = 1; i < count; ++i) {
					intersection.retainAll(subFiltersPassedElements
							.get(currentFilters[i]));
				}

				System.out.println("Got intersection containing " + intersection.size()
						+ " elements.");

				if (!intersection.isEmpty()) {
					Intersection curIntersection = new Intersection(
							currentFilters.clone(), intersection.size());

					if (count == 1 && currentFilters[0] == numSubFilters - 1)
						lastIntersection = curIntersection;
					else
						intersections.add(curIntersection);
				}

				// increment last filter
				currentFilters[count - 1] += 1;

				// and update all filters
				for (int check = count - 1; check >= 0; --check) {
					if (currentFilters[check] > numSubFilters - count + check) {
						if (check > 0) {
							++currentFilters[check - 1];

							// set all following filters directly after
							// incremented filter
							for (int f = check; f < count; ++f)
								currentFilters[f] = currentFilters[f - 1] + 1;
						} else
							doCheck = false;
					}
				}
			} while (doCheck);
		}

		if (lastIntersection != null)
			intersections.add(lastIntersection);
	}

	protected class Intersection {
		public int[] filterIds;
		public int numElements;

		public Intersection(int[] filterIds, int numElements) {
			this.filterIds = filterIds;
			this.numElements = numElements;
		}
	}

}
