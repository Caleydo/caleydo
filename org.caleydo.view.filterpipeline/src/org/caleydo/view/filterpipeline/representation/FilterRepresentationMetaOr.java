package org.caleydo.view.filterpipeline.representation;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.media.opengl.GL2;

import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.ContentMetaOrFilter;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * @author Thomas Geymayer
 * 
 */
public class FilterRepresentationMetaOr extends FilterRepresentation {
	public static boolean renderPassedAll = true;

	protected SortedSet<Integer> elementsPassed = new TreeSet<Integer>();
	protected SortedSet<Integer> elementsPassedAll = new TreeSet<Integer>();
	protected int[] subFilterSizes = new int[0];
	protected ArrayList<SortedSet<Integer>> subFiltersPassedElements = null;

	protected boolean sizesDirty = true;

	public FilterRepresentationMetaOr(FilterPipelineRenderStyle renderStyle,
			PickingManager pickingManager, int viewId) {
		super(renderStyle, pickingManager, viewId);
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer) {
		if (sizesDirty)
			calculateSizes();

		heightLeft = getHeightLeft();
		heightRight = vSize.y() * (filter.getOutput().size() / 100.f);

		renderBasicShape(gl, textRenderer, renderStyle.FILTER_OR_COLOR);
		
		for( int i = 0; i < subFilterSizes.length; ++i )
		{
			gl.glPushName(pickingManager.getPickingID(viewId, EPickingType.FILTERPIPE_SUB_FILTER, i));
			heightRight = vSize.y() * (subFilterSizes[i]/100.f);
			renderShape
			(
				gl,
				GL2.GL_QUADS,
				renderStyle.getFilterColorCombined(i),
				Z_POS_BODY
			);
			gl.glPopName();
		}

		if (renderPassedAll)
		{
			// render elements passed all filters
			heightRight = vSize.y() * (elementsPassedAll.size() / 100.f);
			renderShape(gl, GL2.GL_QUADS, renderStyle.FILTER_PASSED_ALL_COLOR, Z_POS_BODY);
		}

		if( mouseOverItem >= 0 )
		{
			heightRight = vSize.y() * (subFilterSizes[mouseOverItem]/100.f);
			gl.glLineWidth(SelectionType.MOUSE_OVER.getLineWidth());

			renderShape(gl, GL2.GL_LINE_LOOP, SelectionType.MOUSE_OVER.getColor(),
					Z_POS_MARK);
		}

		// reset height
		heightRight = vSize.y() * (filter.getOutput().size() / 100.f);

		// render selection/mouseover if needed
		if (selectionType != SelectionType.NORMAL && mouseOverItem < 0)
		{
			gl.glLineWidth((selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
					.getLineWidth() : SelectionType.MOUSE_OVER.getLineWidth());

			renderShape(
					gl,
					GL2.GL_LINE_LOOP,
					(selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
							.getColor() : SelectionType.MOUSE_OVER.getColor(), Z_POS_MARK);
		}
	}

	protected void calculateSizes() {
		sizesDirty = false;

		// TODO also handle storage filter
		VirtualArray<?, ContentVADelta, ?> input = (VirtualArray<?, ContentVADelta, ?>) filter
				.getInput().clone();
		ArrayList<ContentFilter> filterList = ((ContentMetaOrFilter) filter.getFilter())
				.getFilterList();

		subFilterSizes = new int[filterList.size()];
		subFiltersPassedElements = new ArrayList<SortedSet<Integer>>(filterList.size());

		int i = 0;
		for (ContentFilter subFilter : filterList) {
			VirtualArray<?, ContentVADelta, ?> tempInput = input.clone();
			tempInput.setDelta(subFilter.getVADelta());

			SortedSet<Integer> passedElements = new TreeSet<Integer>();
			for (Integer element : tempInput)
				passedElements.add(element);

			subFilterSizes[i++] = passedElements.size();
			subFiltersPassedElements.add(passedElements);

			System.out.println("Out=" + passedElements.size());
		}

		elementsPassedAll.clear();
		for (Integer value : filter.getOutput()) {
			boolean passedAll = true;

			for (SortedSet<Integer> passedElements : subFiltersPassedElements) {
				if (!passedElements.contains(value))
					passedAll = false;
			}

			if (passedAll)
				elementsPassedAll.add(value);
		}
		System.out.println("CommonOut=" + elementsPassedAll.size());

		// cache output
		elementsPassed.clear();
		for (Integer element : filter.getOutput())
			elementsPassed.add(element);
		System.out.println("TotalOut=" + elementsPassed.size());
	}

}
