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

package com.jmex.angelfont;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;
import com.jmex.angelfont.BitmapFont.Align;

/**
 * 
 * {@link BitmapText} represents a block of characters. The looks of the
 * {@link BitmapText} are defined by the {@link BitmapFont} from which the text
 * was created. A {@link BitmapText} can be restricted by a {@link Rectangle}
 * and will be wrapped accordingly. If no {@link Rectangle} is defined, the text
 * will not be restricted and not wrapped. After setting up the text and after
 * every change, the method update() has to be invoked. This will recreate the
 * text and assemble the various characters into one {@link TriMesh}. For more
 * info about the various Parameters of the Text, look at the JavaDoc of the
 * setter methods.
 * 
 * @author dhdd, Andreas Grabner
 * @author Momoko_Fan (enhancements)
 */
public class BitmapText extends TriMesh {

    private static final long serialVersionUID = 34354997170150440L;

    private BitmapFont font;

    private StringBlock block;

    private QuadList quadList = new QuadList();

    private float lineWidth = 0f;

    private boolean rightToLeft = false;

    private static BitmapFont defaultFont=null;
    
    
    
    /**
     * 
     * constructor for a BitmapText using default font and setting text and color 
     * 
     * @param text
     * @param color
     * 
     */
    public BitmapText(String text,ColorRGBA color)
    {
    	this(defaultFont==null?defaultFont=BitmapFontLoader.loadDefaultFont():defaultFont,false);
    	setText(text);
    	setDefaultColor(color);
    	update();
    }
    /**
     * 
     * Constructor sets up and initializes the text with zero length
     * 
     * @param font
     *            the {@link BitmapFont} from which to create the text
     * @param rightToLeft
     *            option for writing from the right to the left side. e.g.
     *            Chinese
     */
    public BitmapText(BitmapFont font, boolean rightToLeft) {
        super("BitmapFont");

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setCullHint(CullHint.Never);

        this.rightToLeft = rightToLeft;
        this.font = font;
        this.block = new StringBlock();

        setRenderState(getDefaultBlendState());
        setRenderState(DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
        ((TextureState) getRenderState(StateType.Texture)).setEnabled(true);
        ((TextureState) getRenderState(StateType.Texture)).setTexture(font.getFontTexture());
        updateRenderState();
        // initialize buffers

        setVertexBuffer(BufferUtils.createFloatBuffer(0));
        setIndexBuffer(BufferUtils.createIntBuffer(0));
        setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(0), 2), 0);

        updateGeometricState(0.0f, true);
    }

    private BlendState getDefaultBlendState() {
        BlendState bs = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        bs.setTestEnabled(true);
        bs.setTestFunction(BlendState.TestFunction.GreaterThan);
        bs.setEnabled(true);
        return bs;
    } // getDefaultBlendState

    /**
     * Updates the {@link BitmapText} and merges the multiple characters,
     * represented by {@link FontQuad}s into one {@link TriMesh}
     */
    public void update() {
        // first generate quadlist
        if (block.getTextBox() == null) {
            lineWidth = font.updateText(block, quadList, rightToLeft);
        } else {
            font.updateTextRect(block, quadList);
        } // else

        setVertexCount(quadList.getNumActive() * 4);
        setTriangleQuantity(quadList.getNumActive() * 2);

        FloatBuffer vb = getVertexBuffer();
        FloatBuffer tb = getTextureCoords(0).coords;
        IntBuffer ib = getIndexBuffer();

        // increase capacity of buffers as needed
        vb.rewind();
        vb = BufferUtils.ensureLargeEnough(vb, getVertexCount() * 3);
        vb.rewind();
        setVertexBuffer(vb);

        tb.rewind();
        tb = BufferUtils.ensureLargeEnough(tb, getVertexCount() * 2);
        tb.rewind();
        getTextureCoords(0).coords = tb;

        ib.rewind();
        ib = BufferUtils.createIntBuffer(ib, getTriangleCount() * 3);
        ib.rewind();
        setIndexBuffer(ib);

        // go for each quad and append it to the buffers
        for (int i = 0; i < quadList.getQuantity() && i < quadList.getNumActive(); i++) {
            FontQuad fq = quadList.getQuad(i);
            fq.appendPositions(vb);
            fq.appendTexCoords(tb);
            fq.appendIndices(ib, i);
        } // for

        vb.rewind();
        tb.rewind();
        ib.rewind();

        updateGeometricState(0.0f, true);
        updateRenderState();
    } // update

    /**
     * 
     * Set the size of the {@link BitmapText}
     * 
     * @param size
     *            the size of one line of the {@link BitmapText} in pixels
     */
    public void setSize(float size) {
        block.setSize(size);
    }

    /**
     * Set the text content of the {@link BitmapText}
     * 
     * @param text
     *            the content the {@link BitmapText} is to show
     */
    public void setText(String text) {
        block.setText(text);
    }

    /**
     * Set a restricting {@link Rectangle}
     * 
     * @param rect
     *            the {@link Rectangle} that restricts the text and in which the
     *            text will be wrapped accordingly
     */
    public void setBox(Rectangle rect) {
        block.setTextBox(rect);
    }

    /**
     * Set kerning mode
     * 
     * @param kerning
     *            lets the {@link BitmapText} class use kerning, as defined
     *            here: {@link http://en.wikipedia.org/wiki/Kerning}
     */
    public void setUseKerning(boolean kerning) {
        block.setKerning(kerning);
    }

    /**
     * Set the alignment of the Text at point 0 on the X-axis
     * 
     * @param alignment
     *            Either {@link Align}.Center, {@link Align}.Left or
     *            {@link Align}.Right {@link Align}.Left is set as default.
     */
    public void setAlignment(Align alignment) {
        block.setAlignment(alignment);
    }

    public float getHeight() {
        return font.getLineHeight(block) * block.getNumLines();
    }

    public float getLineHeight() {
        return font.getLineHeight(block);
    }

    public float getLineWidth() {
        return lineWidth;
    }

} // BitmapText
