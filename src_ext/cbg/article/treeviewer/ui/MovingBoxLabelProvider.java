package cbg.article.treeviewer.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cbg.article.model.Book;
import cbg.article.model.BoardGame;
import cbg.article.model.MovingBox;

public class MovingBoxLabelProvider extends LabelProvider {	
	private Map imageCache = new HashMap(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof MovingBox) {
			descriptor = TreeViewerPlugin.getImageDescriptor("movingBox.gif");
		} else if (element instanceof Book) {
			descriptor = TreeViewerPlugin.getImageDescriptor("book.gif");
		} else if (element instanceof BoardGame) {
			descriptor = TreeViewerPlugin.getImageDescriptor("gameboard.gif");
		} else {
			throw unknownElement(element);
		}

		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof MovingBox) {
			if(((MovingBox)element).getName() == null) {
				return "Box";
			} else {
				return ((MovingBox)element).getName();
			}
		} else if (element instanceof Book) {
			return ((Book)element).getTitle();
		} else if (element instanceof BoardGame) {
			return ((BoardGame)element).getTitle();
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
