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
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.jme.system.JmeException;

/**
 * <code>ProceduralTexture</code> generates an <code>ImageIcon</code>
 * using an <code>AbstractHeightMap</code> and one or more <code>ImageIcon</code>s
 * as input textures. The final texture is built based on a pixel's current
 * height and the values associated with each input image. When adding
 * an input texture, a low, optimal and high value is given to denote how much
 * of the RGB value to use for a given output pixel. For example, if a
 * texture is added with (0, 10, 20), and a pixel is denoted as having a height
 * of 5 50% of the texture will be used for the output texture. When combining
 * multiple input textures with overlapping values, you can create a smooth
 * blending for the output texture.
 *
 * Currently, the output texture will have the same dimensions as the input
 * heightmap.
 *
 * @author Mark Powell
 * @version $Id: ProceduralTextureGenerator.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class ProceduralTextureGenerator {
    private static final Logger logger = Logger
            .getLogger(ProceduralTextureGenerator.class.getName());
    
  //output image
  protected ImageIcon proceduralTexture;

  //inputs: height map and all input textures.
  private AbstractHeightMap heightMap;
  private ArrayList<TextureTile> textureList;

  //the size of the texture.
  protected int size;

  /**
   * Constructor instantiates a new <code>ProceduralTexture</code> object
   * initializing the list for textures and the height map.
   * @param heightMap the height map to use for the texture generation.
   */
  public ProceduralTextureGenerator(AbstractHeightMap heightMap) {
    textureList = new ArrayList<TextureTile>();
    this.heightMap = heightMap;
    this.size = heightMap.getSize();
  }

  /**
   * <code>createTexture</code> takes the current height map and
   * the current loaded textures and produces an <code>ImageIcon</code>
   * which can be retrieved with a call to <code>getImageIcon</code>.
   */
  public void createTexture(int textureSize) {
    BufferedImage img =
        new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
    DataBufferInt data =
        (DataBufferInt) img.getRaster().getDataBuffer();
    int[] pixels = data.getData();

    //tempvalues for the color
    int red = 0;
    int green = 0;
    int blue = 0;

    int tlSize = textureList.size();
    int twidths[] = new int[tlSize];
    int theights[] = new int[tlSize];
    for (int i = 0; i < tlSize; i++) {
      BufferedImage tempImg =
          textureList.get(i).imageData;
      twidths[i] = tempImg.getWidth();
      theights[i] = tempImg.getHeight();
    }

    int scaledX;
    int scaledZ;

    float mapRatio = (float) size / (float) textureSize;

    //check each pixel of the heightmap
    BufferedImage tempImg;
    float scalar;
    for (int x = 0; x < textureSize; x++) {
      for (int z = 0; z < textureSize; z++) {
        //combine every texture for this pixel
        for (int i = 0; i < tlSize; i++) {
          tempImg =
              textureList.get(i).imageData;
          data =
              (DataBufferInt) tempImg.getRaster()
              .getDataBuffer();
          pixels = data.getData();

          //We may have to tile the texture if the terrain is
          //larger than the texture.
          scaledX = x % twidths[i];
          scaledZ = z % theights[i];

          //Retrieve the amount of the color to use for this
          //texture.
          scalar = getTextureScale(interpolateHeight(x, z, mapRatio), i);
          red += scalar
              * ( (pixels[scaledZ * twidths[i]
                   + scaledX] & 0x00FF0000)
                 >> 16);
          green += scalar
              * ( (pixels[scaledZ * twidths[i]
                   + scaledX] & 0x0000FF00)
                 >> 8);
          blue += scalar
              * ( (pixels[scaledZ * twidths[i]
                   + scaledX] & 0x000000FF));
        }

        //set the color for the final texture.
        int rgb = red << 16 | green << 8 | blue;
        img.setRGB(x, textureSize - (z + 1), rgb);

        red = 0;
        green = 0;
        blue = 0;
      }
    }

    //create the new image from the data.
    proceduralTexture = new ImageIcon(img);
    proceduralTexture.setDescription("TerrainTexture");

    logger.fine("Created procedural texture successfully.");
  }

  /**
     * Saves the final texture this class has created to the given filename as a
     * png. Note that this function will fail if createTexture is not called
     * first.
     * 
     * @param filename
     *            The filename to save the texture too.
     * @return False if the texture could not be written.
     * @see #createTexture(int)
     */
  public boolean saveTexture(String filename) {

    if (null == filename) {
      throw new JmeException("Screenshot filename cannot be null");
    }
    logger.fine("Taking screenshot: " + filename + ".png");

    BufferedImage imageData = (BufferedImage) proceduralTexture.getImage();

    //write out the screenshot image to a file.
    try {
      File out = new File(filename + ".png");
      return ImageIO.write(imageData, "png", out);
    }
    catch (IOException e) {
      logger.warning("Could not create file: " + filename + ".png");
      return false;
    }
  }

  /**
   * <code>addTexture</code> adds an additional texture to the list of
   * input textures. Each texture has a low, optimal and high value
   * associated with it. This determines how much of the texture color
   * to use for a particular pixel. Where optimal is 100% of the color,
   * less than low is 0% and higher than high is 0%. For example if the
   * values are (0, 10, 20), and the height is 5, then 50% of the
   * color will be used.
   *
   * @param image the input texture.
   * @param low the low color value for this texture.
   * @param optimal the optimal color value for this texture.
   * @param high the high color value for this texture.
   */
  public void addTexture(ImageIcon image, int low, int optimal, int high) {
    //create the texture data.
    BufferedImage img =
        new BufferedImage(
        image.getIconWidth(),
        image.getIconHeight(),
        BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D) img.getGraphics();
    g.drawImage(image.getImage(), null, null);
    g.dispose();

    //set the texture data and add it to the list.
    TextureTile tile = new TextureTile();
    tile.highHeight = high;
    tile.optimalHeight = optimal;
    tile.lowHeight = low;
    tile.imageData = img;
    textureList.add(tile);
  }

  public void clearTextures() {
      for ( TextureTile tile : textureList ) {
          tile.imageData = null;
      }
      textureList.clear();
  }
  
  /**
   * <code>setHeightMap</code> sets the input heightmap to use
   * for the texture generation.
   * @param hm the new heightmap.
   * @throws JmeException if hm is null.
   */
  public void setHeightMap(AbstractHeightMap hm) {
    if (null == hm) {
      throw new JmeException("Heightmap cannot be null");
    }
    heightMap = hm;
  }

  /**
   * <code>getImageIcon</code> retrieves the procedural texture that
   * has been created. Note that this will return null until
   * <code>createTexture</code> has been called.
   * @return the <code>ImageIcon</code> of the output texture.
   */
  public ImageIcon getImageIcon() {
    return proceduralTexture;
  }

  /**
   * <code>getTextureScale</code> returns the percentage of the
   * color to use for a given height.
   * @param height the height to compare.
   * @param tileIndex the texture id.
   * @return the percentage to use 0 to 1.
   */
  private float getTextureScale(float height, int tileIndex) {
    TextureTile tile = textureList.get(tileIndex);

    //check if the height is within the textures boundary's, if not
    //use 0%, otherwise determine where it lies on the scale.
    if (height < tile.optimalHeight && height > tile.lowHeight) {
      return ( (float) (height - tile.lowHeight))
          / (tile.optimalHeight - tile.lowHeight);
    } else if (height > tile.optimalHeight && height < tile.highHeight) {
      return ( (float) (tile.highHeight - height))
          / (tile.highHeight - tile.optimalHeight);
    } else if (height == tile.optimalHeight) {
      return 1.0f;
    } else {
      return 0.0f;
    }
  }

  private float interpolateHeight(int x, int z, float ratio) {
	float low, highX, highZ;
    float intX, intZ;
    float scaledX = x * ratio;
    float scaledZ = z * ratio;
    float interpolation;

    low = heightMap.getTrueHeightAtPoint( (int) scaledX, (int) scaledZ);

    if (scaledX + 1 >= size) {
      return low;
    }
      
    highX = heightMap.getTrueHeightAtPoint( (int) scaledX + 1, (int) scaledZ);    

    interpolation = scaledX - (int) scaledX;
    intX = ( (highX - low) * interpolation) + low;

    if (scaledZ + 1 >= size) {
      return low;
    } 
      
    highZ = heightMap.getTrueHeightAtPoint( (int) scaledX, (int) scaledZ + 1);    

    interpolation = scaledZ - (int) scaledZ;
    intZ = ( (highZ - low) * interpolation) + low;

    return (int) ( (intX + intZ) / 2f);
  }

  /**
   * <code>TextureTile</code> is an inner class that contains data
   * for each input texture. All data is public with no methods.
   */
  private static class TextureTile {
    public BufferedImage imageData;
    public int lowHeight;
    public int optimalHeight;
    public int highHeight;
  }

}
