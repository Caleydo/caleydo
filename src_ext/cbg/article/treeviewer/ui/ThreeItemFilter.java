package cbg.article.treeviewer.ui;

import cbg.article.model.BoardGame;
import cbg.article.model.MovingBox;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ThreeItemFilter extends ViewerFilter {

	/*
	 * @see ViewerFilter#select(Viewer, Object, Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return parentElement instanceof MovingBox && ((MovingBox)parentElement).size() >= 3;
	}

}
