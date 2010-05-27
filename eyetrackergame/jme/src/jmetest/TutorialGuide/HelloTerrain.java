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

package jmetest.TutorialGuide;

import java.net.URL;

import javax.swing.ImageIcon;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.ImageBasedHeightMap;
import com.jmex.terrain.util.MidPointHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * Started Date: Aug 19, 2004<br><br>
 *
 * This program introduces jME's terrain utility classes and how they are used.  It
 * goes over ProceduralTextureGenerator, ImageBasedHeightMap, MidPointHeightMap, and
 * TerrainBlock.
 * 
 * @author Jack Lindamood
 */
public class HelloTerrain extends SimpleGame {
    public static void main(String[] args) {
        HelloTerrain app = new HelloTerrain();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        // First a hand made terrain
        homeGrownHeightMap();
        // Next an automatically generated terrain with a texture
        generatedHeightMap();
        // Finally a terrain loaded from a greyscale image with fancy textures on it.
        complexTerrain();
    }


    private void homeGrownHeightMap() {
        // The map for our terrain.  Each value is a height on the terrain
        float[] map=new float[]{
            1,2,3,4,
            2,1,2,3,
            3,2,1,2,
            4,3,2,1
        };

        // Create a terrain block.  Our integer height values will scale on the map 2x larger x,
        //   and 2x larger z.  Our map's origin will be the regular origin.
        TerrainBlock tb=new TerrainBlock("block",4,
                new Vector3f(2,1,2),
                map,
                new Vector3f(0,0,0));

        // Give the terrain a bounding box.
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();

        // Attach the terrain TriMesh to our rootNode
        rootNode.attachChild(tb);
    }


    private void generatedHeightMap() {
        // This will be the texture for the terrain.
        URL grass=HelloTerrain.class.getClassLoader().getResource(
            "jmetest/data/texture/grassb.png");

        //  Use the helper class to create a terrain for us.  The terrain will be 64x64
        MidPointHeightMap mph=new MidPointHeightMap(64,1.5f);
        // Create a terrain block from the created terrain map.
        TerrainBlock tb=new TerrainBlock("midpoint block",mph.getSize(),
                new Vector3f(1,.11f,1),
                mph.getHeightMap(),
                new Vector3f(0,-25,0));

        // Add the texture
        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(grass,
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        tb.setRenderState(ts);

        // Give the terrain a bounding box.
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();

        // Attach the terrain TriMesh to rootNode
        rootNode.attachChild(tb);
    }

    private void complexTerrain() {
        // This grayscale image will be our terrain
        URL grayScale=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/bubble.jpg");

        // These will be the textures of our terrain.
        URL waterImage=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/water.png");
        URL dirtImage=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/dirt.jpg");
        URL highest=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/highest.jpg");


        //  Create an image height map based on the gray scale of our image.
        ImageBasedHeightMap ib=new ImageBasedHeightMap(
                new ImageIcon(grayScale).getImage()
        );
        // Create a terrain block from the image's grey scale
        TerrainBlock tb=new TerrainBlock("image icon",ib.getSize(),
                new Vector3f(.5f,.05f,.5f),ib.getHeightMap(),
                new Vector3f(0,0,0));

        //  Create an object to generate textured terrain from the image based height map.
        ProceduralTextureGenerator pg=new ProceduralTextureGenerator(ib);
        //  Look like water from height 0-60 with the strongest "water look" at 30
        pg.addTexture(new ImageIcon(waterImage),0,30,60);
        //  Look like dirt from height 40-120 with the strongest "dirt look" at 80
        pg.addTexture(new ImageIcon(dirtImage),40,80,120);
        //  Look like highest (pure white) from height 110-256 with the strongest "white look" at 130
        pg.addTexture(new ImageIcon(highest),110,130,256);

        //  Tell pg to create a texture from the ImageIcon's it has recieved.
        pg.createTexture(256);
        TextureState ts=display.getRenderer().createTextureState();
        // Load the texture and assign it.
        ts.setTexture(
                TextureManager.loadTexture(
                        pg.getImageIcon().getImage(),
                        Texture.MinificationFilter.Trilinear,
                        Texture.MagnificationFilter.Bilinear,
                        true
                )
        );
        tb.setRenderState(ts);

        // Give the terrain a bounding box
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();

        // Move the terrain in front of the camera
        tb.setLocalTranslation(new Vector3f(0,0,-50));

        // Attach the terrain to our rootNode.
        rootNode.attachChild(tb);
    }
}