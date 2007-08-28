package cerberus.view.gui.swt.data.explorer;

import java.util.HashMap;
import java.util.Iterator;
//import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import cerberus.view.gui.swt.data.explorer.model.SelectionModel;
import cerberus.view.gui.swt.data.explorer.model.DataCollectionModel;
import cerberus.view.gui.swt.data.explorer.model.StorageModel;

public class DataExplorerLabelProvider extends LabelProvider
{
	private HashMap<ImageDescriptor, Image> imageCache = 
		new HashMap<ImageDescriptor, Image>();
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor;
		if (element instanceof DataCollectionModel) {
			descriptor = ImageDescriptor.createFromImageData(new ImageData("data/icons/set.gif"));
		} else if (element instanceof SelectionModel) {
			descriptor = ImageDescriptor.createFromImageData(new ImageData("data/icons/selection.gif"));
		} else if (element instanceof StorageModel) {
			descriptor = ImageDescriptor.createFromImageData(new ImageData("data/icons/storage.gif"));
		} else {
			throw unknownElement(element);
		}

		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return null;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) 
	{
		if (element instanceof DataCollectionModel) 
		{
			if(((DataCollectionModel)element).getLabel() == null) 
			{
				return "SET";
			} 
			else 
			{
				// format: "ID - Label"
				return (Integer.toString(((DataCollectionModel)element).getID()) 
						+ " - " + ((DataCollectionModel)element).getLabel());
			}
		} 
		else if (element instanceof StorageModel) 
		{
			// format: "ID - Label"
			return (Integer.toString(((StorageModel)element).getID()) 
					+ " - " + ((StorageModel)element).getLabel());
		}
		else if (element instanceof SelectionModel) 
		{
			// format: "ID - Label"
			return (Integer.toString(((SelectionModel)element).getID()) 
					+ " - " + ((SelectionModel)element).getLabel());
		}
		else 
		{
			throw unknownElement(element);
		}
	}

	public void dispose() 
	{
		for (Iterator <Image> i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) 
	{
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}
}
