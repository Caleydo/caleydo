// ---------------------------------------------------------------------
//    COPYRIGHT (C) 1999-2001. All Rights Reserved.
//    Claudio Rosati, Colleferro (RM), Italy
// ---------------------------------------------------------------------

/* 
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.util;


import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;


/**
 * This class transform a <CODE>ImageIcon</CODE> into a bean, allowing for
 * encoding and decoding in XML using <CODE>XMLEncoder</CODE> and 
 * <CODE>XMLDecoder</CODE>.
 *
 * @author  Claudio Rosati
 * @version 1.0.0 (June 26, 2002)
 */
public class ImageIconBean 
	extends ImageIcon
{
	
	private String filename = null;
	
	/** 
	 * Creates an uninitialized image icon.
	 */
	public ImageIconBean()
	{
		super();
	}
	
	/** 
	 * Creates an image icon from the specified file. The image will be preloaded 
	 * by using <CODE>MediaTracker</CODE> to monitor the loading answer of the image. 
	 * The specified <CODE>String</CODE> can be a file name or a file path. When 
	 * specifying a path, use the Internet-standard forward-slash ("/") as a separator. 
	 * (The string is converted to an <CODE>URL</CODE>, so the forward-slash works 
	 * on all systems.) For example, specify: 
	 * <PRE>
	 *   new BeanifiedIcon("images/myImage.gif") 
	 * </PRE>
	 * The <CODE>description</CODE> is initialized to the filename string.
	 *
	 * @param filename A <CODE>String</CODE> specifying a filename or path.
	 */
	public ImageIconBean ( String filename )
	{
		super(filename);
		this.filename = filename;
	}

	/**
	 * Creates an image icon from the specified file. The image will be preloaded 
	 * by using <CODE>MediaTracker</CODE> to monitor the loading answer of the image. 
	 *
	 * @param filename    The name of the file containing the image.
	 * @param description A brief textual description of the image.
	 */
	public ImageIconBean ( String filename, String description )
	{
		super(filename, description);
		this.filename = filename;
	}
	
	/** 
	 * Creates an image icon from the specified <CODE>URL</CODE>. The image will 
	 * be preloaded by using <CODE>MediaTracker</CODE> to monitor the loaded 
	 * answer of the image. The icon's <CODE>description</CODE> is initialized to 
	 * be a string representation of the URL.
	 *
	 * @param location The <CODE>URL</CODE> for the image.
	 */
	public ImageIconBean ( URL location )
	{
		super(location);
		this.filename = location.toExternalForm();
	}

	/**
	 * 	Creates an image icon from the specified <CODE>URL</CODE>. The image will 
	 * be preloaded by using <CODE>MediaTracker</CODE> to monitor the loaded answer 
	 * of the image. 
	 *
	 * @param location    The URL for the image.
	 * @param description A brief textual description of the image.
	 */
	public ImageIconBean ( URL location, String description )
	{
		super(location, description);
		this.filename = location.toExternalForm();
	}

	/**
	 * Returns the file name used to initialize the image.
	 */
	public String getFileName ( )
	{
		return filename;
	}
	
	/** 
	 * Initializes this image icon from the specified file. The image will be preloaded 
	 * by using <CODE>MediaTracker</CODE> to monitor the loading answer of the image. 
	 * The specified <CODE>String</CODE> can be a file name or a file path. When 
	 * specifying a path, use the Internet-standard forward-slash ("/") as a separator. 
	 * (The string is converted to an <CODE>URL</CODE>, so the forward-slash works 
	 * on all systems.) For example, specify: 
	 * <PRE>
	 *   new BeanifiedIcon().setFileName("images/myImage.gif") 
	 * </PRE>
	 *
	 * @param filename A <CODE>String</CODE> specifying a filename or path.
	 */
	public void setFileName ( String filename )
	{
		
		URL	loadableName;

		try
		{
			try
			{
				loadableName = new URL(filename);
			}
			catch ( MalformedURLException ex )
			{
				loadableName = new File(filename).toURL();
			}
			this.filename = loadableName.toExternalForm();
		}
		catch ( Exception ex )
		{
			return;
		}
			
		if ( getDescription() == null )
			setDescription(filename);
	
		setImage(Toolkit.getDefaultToolkit().getImage(loadableName));

	}
	
}
