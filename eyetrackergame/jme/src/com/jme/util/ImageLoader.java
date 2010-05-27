package com.jme.util;

import java.io.IOException;
import java.io.InputStream;

import com.jme.image.Image;

/**
 * Interface for image loaders. Implementing classes can be registerd with the
 * TextureManager to decode image formats with a certain file extention.
 * 
 * @see com.jme.util.TextureManager#addHandler(String, ImageLoader)
 * 
 * @author Galun
 * @author Tijl Houtbeckers -- javadoc
 * @version $Id: ImageLoader.java 4131 2009-03-19 20:15:28Z blaine.dev $
 * 
 */
public interface ImageLoader {

	/**
	 * Decodes image data from an InputStream.
	 * 
	 * @param is
	 *            The InputStream to create the image from. The inputstream
	 *            should be closed before this method returns.
	 * @return The decoded Image.
	 * @throws IOException
	 */
	public Image load(InputStream is) throws IOException;
}