package cbg.article.treeviewer.ui;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class NoArticleSorter extends ViewerSorter {	/*
	 * @see ViewerSorter#compare(Viewer, Object, Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);
		if (cat1 != cat2) return cat1 - cat2;
		String name1, name2;
		if (viewer == null || !(viewer instanceof ContentViewer)) {
			name1 = e1.toString();
			name2 = e2.toString();
		} else {
			IBaseLabelProvider prov = ((ContentViewer)viewer).getLabelProvider();
			if (prov instanceof ILabelProvider) {
				ILabelProvider lprov = (ILabelProvider)prov;
				name1 = lprov.getText(e1);
				name2 = lprov.getText(e2);
			} else {
				name1 = e1.toString();
				name2 = e2.toString();
			}
		}
		if(name1 == null) name1 = "";
		if(name2 == null) name2 = "";
		name1 = stripArticles(name1);
		name2 = stripArticles(name2);
		return collator.compare(name1, name2);
	}
	
	protected String stripArticles(String name) {
		String test = name.toLowerCase();
		if(test.startsWith("the ") && test.length() > 3) {
			return name.substring(4);
		} else if(test.startsWith("a ") && test.length() > 1) {
			return name.substring(2);
		} else if(test.startsWith("el ") && test.length() > 2) {
			return name.substring(3);
		} else if(test.startsWith("la ") && test.length() > 2) {
			return name.substring(3);
		}
		return name;	
	}

}
