package cbg.article.treeviewer.ui;

import cbg.article.model.Book;
import cbg.article.model.MovingBox;

import org.eclipse.jface.viewers.ViewerSorter;

public class BookBoxBoardSorter extends ViewerSorter {
	
	/*
	 * @see ViewerSorter#category(Object)
	 */
	/** Orders the items in such a way that books appear 
	 * before moving boxes, which appear before board games. */
	public int category(Object element) {
		if(element instanceof Book) return 1;
		if(element instanceof MovingBox) return 2;
		return 3;
	}

}
