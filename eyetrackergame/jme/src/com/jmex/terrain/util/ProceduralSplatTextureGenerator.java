/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.terrain.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

/**
 * <code>ProceduralSplatTexture</code> is an extension of the
 * <code>ProceduralTexture</code>. It provides the capability to overlay one
 * or more textures on a <code>ProceduralTexture</code>. To define a splat
 * texture layer an alpha map and texture map are provided and the final overlay
 * texture is calculated by using the alpha map to add the texture map color to
 * the existing <code>ProceduralTexture</code>.
 * 
 * @author Chris Gray
 * @version $Id: ProceduralSplatTextureGenerator.java 4133 2009-03-19 20:40:11Z blaine.dev $
 *  */
public class ProceduralSplatTextureGenerator extends ProceduralTextureGenerator {
    private static final Logger logger = Logger
            .getLogger(ProceduralSplatTextureGenerator.class.getName());
    
	// collection of alpha maps
	protected List<BufferedImage> splatMaps;

	// collection of texture maps
	protected List<BufferedImage> splatTextures;

	/**
	 * Constructor instantiates a new <code>ProceduralSplatTexture</code>
	 * object initializing the list for textures and the height map.
	 * 
	 * @param heightMap
	 *            the height map to use for the texture generation.
	 */
	public ProceduralSplatTextureGenerator(AbstractHeightMap heightMap) {
		super(heightMap);

		splatMaps = new ArrayList<BufferedImage>();
		splatTextures = new ArrayList<BufferedImage>();
	}

	/**
	 * <code>addSplatTexture</code> adds an additional splat texture to the
	 * list of splat textures. Each texture has an alpha map and a texture map
	 * associated with it. The alpha map determines the amount of color from the
	 * texture map to add to the existing procedural texture.
	 * 
	 * @param map
	 *            the alpha map.
	 * 
	 * @param texture
	 *            the texture map.
	 */
	public void addSplatTexture(ImageIcon map, ImageIcon texture) {
		// create the texture data.
		BufferedImage img = new BufferedImage(map.getIconWidth(), map.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = (Graphics2D) img.getGraphics();
		g.drawImage(map.getImage(), null, null);
		g.dispose();

		splatMaps.add(img);

		img = new BufferedImage(texture.getIconWidth(), texture.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) img.getGraphics();
		g.drawImage(texture.getImage(), null, null);
		g.dispose();

		splatTextures.add(img);
	}

	/**
	 * <code>createTexture</code> overrides the method in
	 * <code>ProcduralTextureGenerator</code> to provide the capability to
	 * overlay the existing procedural texture with one or more 'splat' maps.
	 */
	public void createTexture(int textureSize) {
		super.createTexture(textureSize);

		BufferedImage img = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
		BufferedImage splatTexture;
		BufferedImage splatMap;

		float alpha;
		int scaledX;
		int scaledY;

		int rgb;
		int red;
		int green;
		int blue;

		int splatSize = splatTextures.size();

		img.getGraphics().drawImage(proceduralTexture.getImage(), 0, 0, null);

		for (int x = 0; x < textureSize; x++) {
			for (int y = 0; y < textureSize; y++) {
				rgb = img.getRGB(x, y);
				red = (rgb & 0x00FF0000) >> 16;
				green = (rgb & 0x0000FF00) >> 8;
				blue = (rgb & 0x000000FF);

				for (int i = 0; i < splatSize; i++) {
					splatMap = splatMaps.get(i);
					splatTexture = splatTextures.get(i);

					// Retrieve the amount of the color to use for this texture.
					scaledX = (int) (x * (splatMap.getWidth() / (float) textureSize));
					scaledY = (int) (splatMap.getHeight() - ((y * (splatMap.getHeight() / (float) textureSize)) + 1));

					alpha = ((splatMap.getRGB(scaledX, scaledY) >> 24) & 0x000000FF) / 255.0f;

					// We may have to tile the texture if the terrain is larger
					// than the texture.
					scaledX = x % splatTexture.getWidth();
					scaledY = y % splatTexture.getHeight();

					// perform alpha composite
					if (alpha > 0) {
						red = (int) ((red * (1.0f - alpha)) + (((splatTexture.getRGB(scaledX, scaledY) & 0x00FF0000) >> 16) * alpha));
						green = (int) ((green * (1.0f - alpha)) + (((splatTexture.getRGB(scaledX, scaledY) & 0x0000FF00) >> 8) * alpha));
						blue = (int) ((blue * (1.0f - alpha)) + (((splatTexture.getRGB(scaledX, scaledY) & 0x000000FF)) * alpha));
					}
				}

				// set the color for the final texture.
				rgb = red << 16 | green << 8 | blue;
				img.setRGB(x, y, rgb);

				red = 0;
				green = 0;
				blue = 0;
			}
		}

		// create the new image from the data.
		proceduralTexture = new ImageIcon(img);
		proceduralTexture.setDescription("TerrainTexture");

		logger.fine("Created splat texture successfully.");
	}

	/**
	 * @return Returns the number of splat maps currently defined.
	 */
	public int getSplatSize() {
		return (splatMaps.size());
	}

	/**
	 * @return Returns the splat alpha map at the specified index.
	 */
	public BufferedImage getSplatMap(int index) {
		return splatMaps.get(index);
	}

	/**
	 * @return Returns the splat texture map at the specified index.
	 */
	public BufferedImage getSplatTexture(int index) {
		return splatTextures.get(index);
	}
        
        public void clearTextures() {
            super.clearTextures();
            splatMaps.clear();
            splatTextures.clear();
}
}