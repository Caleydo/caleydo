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

package com.jme.renderer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.jme.curve.Curve;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.QuadMesh;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.StippleState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.JmeException;

/**
 * <code>Renderer</code> defines an abstract class that handles displaying of
 * graphics data to the context. Creation of this object is typically handled
 * via a call to a <code>DisplaySystem</code> subclass.
 * 
 * All rendering state and tasks can be handled through this class.
 * 
 * Example Usage: <br>
 * NOTE: This example uses the <code>DisplaySystem</code> class to obtain the
 * <code>Renderer</code>.
 * 
 * <code>
 * DisplaySystem ds = new LWJGLDisplaySystem();<br>
 * ds.createWindow(640,480,16,60,false);<br>
 * Renderer r = ds.getRenderer();<br>
 * r.draw(point);<br>
 * </code>
 * 
 * @see com.jme.system.DisplaySystem
 * @author Mark Powell
 * @author Tijl Houtbeckers (added VBO delete methods)
 * @version $Id: Renderer.java 4446 2009-06-29 07:36:41Z mulova $
 */
public abstract class Renderer {

    /** The Spatial will inherit its render queue state from its parent. */
    public static final int QUEUE_INHERIT = 0;

    /** The Spatial will skip render queueing. */
    public static final int QUEUE_SKIP = 1;

    /** The Spatial will render in the opaque bucket. */
    public static final int QUEUE_OPAQUE = 2;

    /** The Spatial will render in the transparent bucket. */
    public static final int QUEUE_TRANSPARENT = 3;

    /** The Spatial will render in the ortho bucket. */
    public static final int QUEUE_ORTHO = 4;
    
    protected AbstractCamera camera;
    
    // clear color
    protected ColorRGBA backgroundColor;

    protected boolean processingQueue;

    protected RenderQueue queue;

    private boolean headless = false;
    
 // width and height of renderer
    protected int width;

    protected int height;

    /** List of default states all spatials take if none is set. */
    public static final RenderState[] defaultStateList = new RenderState[RenderState.StateType.values().length];

    /**
     * <code>setCamera</code> sets the reference to the applications camera
     * object.
     * 
     * @param camera
     *            the camera object to use with this <code>Renderer</code>.
     */
    public abstract void setCamera(Camera camera);



    /**
     * <code>getCamera</code> returns the camera used by this renderer.
     * 
     * @see com.jme.renderer.Renderer#getCamera()
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * 
     * <code>createCamera</code> retrieves a default camera for this renderer.
     * 
     * @param width
     *            the width of the frame.
     * @param height
     *            the height of the frame.
     * @return a default camera for this renderer.
     */
    public abstract Camera createCamera(int width, int height);

    /**
     * 
     * <code>createBlendState</code> retrieves the blend state object for the
     * proper renderer.
     * 
     * @return the <code>BlendState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract BlendState createBlendState();

    /**
     * 
     * <code>createCullState</code> retrieves the cull state object for the
     * proper renderer.
     * 
     * @return the <code>CullState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract CullState createCullState();

    /**
     * 
     * <code>createFogState</code> retrieves the fog state object for the
     * proper renderer.
     * 
     * @return the <code>FogState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract FogState createFogState();

    /**
     * 
     * <code>createLightState</code> retrieves the light state object for the
     * proper renderer.
     * 
     * @return the <code>LightState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract LightState createLightState();

    /**
     * 
     * <code>createMaterialState</code> retrieves the material state object
     * for the proper renderer.
     * 
     * @return the <code>MaterialState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract MaterialState createMaterialState();

    /**
     * 
     * <code>createShadeState</code> retrieves the shade state object for the
     * proper renderer.
     * 
     * @return the <code>ShadeState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract ShadeState createShadeState();

    /**
     * 
     * <code>createTextureState</code> retrieves the texture state object for
     * the proper renderer.
     * 
     * @return the <code>TextureState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract TextureState createTextureState();

    /**
     * 
     * <code>createWireframeState</code> retrieves the wireframe state object
     * for the proper renderer.
     * 
     * @return the <code>WireframeState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract WireframeState createWireframeState();

    /**
     * Retrieves the Z buffer state object for the proper renderer.
     * 
     * @return The <code>ZBufferState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract ZBufferState createZBufferState();

    /**
     * Retrieves the vertex program state object for the proper renderer.
     * 
     * @return The <code>VertexProgramState</code> object that can make use of
     *         the proper renderer.
     */
    public abstract VertexProgramState createVertexProgramState();

    /**
     * Retrieves the fragment program state object for the proper renderer.
     * 
     * @return The <code>VertexProgramState</code> object that can make use of
     *         the proper renderer.
     */
    public abstract FragmentProgramState createFragmentProgramState();

    /**
     * <code>createShaderObjectsState</code> retrieves the shader object state
     * object for the proper renderer.
     * 
     * @return the <code>ShaderObjectsState</code> object that can make use of
     *         the proper renderer.
     */
    public abstract GLSLShaderObjectsState createGLSLShaderObjectsState();

    /**
     * Retrieves the stencil state object for the proper renderer.
     * 
     * @return The <code>StencilState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract StencilState createStencilState();
    
    /**
    * Retrieves the clip state object for the proper renderer.
    *
    * @return The <code>ClipState</code> object that can make use of the
    *         proper renderer.
    */
    public abstract ClipState createClipState();

    /**
     * Retrieves the color mask state object for the proper renderer.
     * 
     * @return The <code>ColorMaskState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract ColorMaskState createColorMaskState();

    /**
     * Retrieves the stipple state object for the proper renderer.
     * 
     * @return The <code>StippleState</code> object that can make use of the
     *         proper renderer.
     */
    public abstract StippleState createStippleState();
    
    /**
     * <code>setBackgroundColor</code> sets the color of window. This color
     * will be shown for any pixel that is not set via typical rendering
     * operations.
     * 
     * @param c
     *            the color to set the background to.
     */
    public abstract void setBackgroundColor(ColorRGBA c);

    /**
     * <code>getBackgroundColor</code> retrieves the clear color of the
     * current OpenGL context.
     * 
     * @see com.jme.renderer.Renderer#getBackgroundColor()
     * @return the current clear color.
     */
    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * <code>clearZBuffer</code> clears the depth buffer of the renderer. The
     * Z buffer allows sorting of pixels by depth or distance from the view
     * port. Clearing this buffer prepares it for the next frame.
     *  
     */
    public abstract void clearZBuffer();

    /**
     * <code>clearBackBuffer</code> clears the back buffer of the renderer.
     * The backbuffer is the buffer being rendered to before it is displayed to
     * the screen. Clearing this buffer frees it for rendering the next frame.
     *  
     */
    public abstract void clearColorBuffer();

    /**
     * <code>clearStencilBuffer</code> clears the stencil buffer of the renderer.
     */
    public abstract void clearStencilBuffer();

    /**
     * <code>clearBuffers</code> clears both the depth buffer and the back
     * buffer.
     *  
     */
    public abstract void clearBuffers();

    /**
     * <code>clearBuffers</code> clears both the depth buffer and the back
     * buffer restricting the clear to the rectangle defined by the width and
     * height of the renderer.
     *  
     */
    public abstract void clearStrictBuffers();

    /**
     * <code>displayBackBuffer</code> swaps the back buffer with the currently
     * displayed buffer. Swapping (page flipping) allows the renderer to display
     * a prerenderer display without any flickering.
     *  
     */
    public abstract void displayBackBuffer();

    /**
     * 
     * <code>setOrtho</code> sets the display system to be in orthographic
     * mode. If the system has already been set to orthographic mode a
     * <code>JmeException</code> is thrown. The origin (0,0) is the bottom
     * left of the screen.
     *  
     */
    public abstract void setOrtho();

    /**
     * @return true if the renderer is currently in ortho mode.
     */
    public abstract boolean isInOrthoMode();
    
    /**
     * render queue if needed
     */
    public void renderQueue() {
        processingQueue = true;
        queue.renderBuckets();
        processingQueue = false;
    }

    /**
     * clear the render queue
     */
    public void clearQueue() {
        queue.clearBuckets();
    }

    /**
     * 
     * <code>setOrthoCenter</code> sets the display system to be in
     * orthographic mode. If the system has already been set to orthographic
     * mode a <code>JmeException</code> is thrown. The origin (0,0) is the
     * center of the screen.
     * 
     *  
     */
    public abstract void setOrthoCenter();

    /**
     * 
     * <code>unsetOrhto</code> unsets the display system from orthographic
     * mode back into regular projection mode. If the system is not in
     * orthographic mode a <code>JmeException</code> is thrown.
     * 
     *  
     */
    public abstract void unsetOrtho();

    /**
     * 
     * <code>takeScreenShot</code> saves the current buffer to a png file. The
     * filename is provided, .png will be appended to the end of the name.
     * 
     * @param filename
     *            the name of the screenshot file.
     * @return true if the screen capture was successful, false otherwise.
     */
    public abstract void takeScreenShot(String filename);

    /**
     * <code>grabScreenContents</code> reads a block of data as bytes from the
     * current framebuffer. The format determines how many bytes per pixel are
     * read and thus how big the buffer must be that you pass in.
     * 
     * @param buff
     *            a buffer to store contents in.
     * @param format
     *            the format to read in bytes for.
     * @param x -
     *            x starting point of block
     * @param y -
     *            y starting point of block
     * @param w -
     *            width of block
     * @param h -
     *            height of block
     */
    public abstract void grabScreenContents(ByteBuffer buff, Image.Format format, int x, int y, int w, int h);

    /**
     * <code>draw</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached.
     * 
     * @param s
     *            the scene to render.
     */
    public abstract void draw(Spatial s);

    /**
     * <code>draw</code> renders a single TriMesh to the back buffer.
     * 
     * @param mesh
     *            the mesh to be rendered.
     */
    public abstract void draw(TriMesh mesh);
    
    /**
     * <code>draw</code> renders a single QuadMesh to the back buffer.
     * 
     * @param mesh
     *            the mesh to be rendered.
     */
    public abstract void draw(QuadMesh mesh);

    /**
     * <code>draw</code> renders a single Point collection to the back buffer.
     * 
     * @param point
     *            the points to be rendered.
     */
    public abstract void draw(Point point);

    /**
     * <code>draw</code> renders a single Line collection to the back buffer.
     * 
     * @param line
     *            the line to be rendered.
     */
    public abstract void draw(Line line);

    /**
     * 
     * <code>draw</code> renders a curve to the back buffer.
     * 
     * @param c
     *            the curve to be rendered.
     */
    public abstract void draw(Curve c);

    /**
     * 
     * <code>draw</code> renders text to the back buffer.
     * 
     * @param t
     *            the text object to be rendered.
     */
    public abstract void draw(Text t);

    /**
     * <code>flush</code> tells opengl to send through all currently waiting
     * commands in the buffer.
     */
    public abstract void flush();

    /**
     * <code>finish</code> is similar to flush, however it blocks until all
     * waiting OpenGL commands have been finished.
     */
    public abstract void finish();
    
    /**
     * Get the render queue associated with this Renderer.
     * 
     * @return RenderQueue
     */
    public RenderQueue getQueue() {
        return queue;
    }

    /**
     * Return true if this renderer is in the middle of processing its
     * RenderQueue.
     * 
     * @return boolean
     */
    public boolean isProcessingQueue() {
        return processingQueue;
    }

    /**
     * Check a given Spatial to see if it should be queued. return true if it
     * was queued.
     * 
     * @param s
     *            Spatial to check
     * @return true if it was queued.
     */
    public abstract boolean checkAndAdd(Spatial s);

    /**
     * Return true if the system running this supports VBO
     * 
     * @return boolean
     */
    public abstract boolean supportsVBO();

    /**
     * See Renderer.isHeadless()
     * 
     * @return boolean
     */
    public boolean isHeadless() {
        return headless;
    }

    /**
     * See Renderer.setHeadless()
     */
    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    /**
     * Retrieve the width set on this renderer.
     * 
     * @return width
     */
    public int getWidth() {
        return width;
    }


    /**
     * Retrieve the height set on this renderer.
     * 
     * @return height
     */
    public int getHeight() {
        return height;
    }

    
    /**
     * Reinitialize the renderer with the given width/height. Also calls resize
     * on the attached camera if present.
     * 
     * @param width
     *            int
     * @param height
     *            int
     */
    public abstract void reinit(int width, int height);


    /**
     * Generate a DisplayList for drawing the given Geometry.
     * 
     * @param geom
     *            the geometry to make a display list for
     * @return the id of the list
     */
    public abstract int createDisplayList(Geometry geom);

    /**
     * Releases a DisplayList from the card.
     * 
     * @param listId
     *            the id of the display list to release
     */
    public abstract void releaseDisplayList(int listId);
    
    /**
     * Sets an offset to the zbuffer to be used when comparing an incoming
     * polygon for depth buffer pass/fail.
     * 
     * @param factor
     *            Specifies a scale factor that is used to create a variable
     *            depth offset for each polygon. The initial value is 0.
     * @param offset
     *            Is multiplied by an implementation-specific value to create a
     *            constant depth offset. The initial value is 0.
     */
    public abstract void setPolygonOffset(float factor, float offset);
    
    /**
     * Removes any previously set offset from the renderer.
     */
    public abstract void clearPolygonOffset();
        
    /**
     * Checks the VBO cache to see if this Buffer is mapped to a VBO-id.
     * If it does the mapping will be removed from the cache and the VBO with the
     * VBO-id found will be deleted.
     * 
     * If no mapped VBO-id is found, this method does not do anything else.
     * 
     * @param buffer
     *            The Buffer who's associated VBO should be deleted.
     */
    public abstract void deleteVBO(Buffer buffer);

    /**
     * Attempts to delete the VBO with this VBO id. Ignores ids < 1.
     * 
     * @param vboid
     */
    public abstract void deleteVBO(int vboid);

    /**
     * Clears all entries from the VBO cache. Does not actually delete any VBO
     * buffer, only all mappings between Buffers and VBO-ids.
     * 
     */
    public abstract void clearVBOCache();

    /**
     * Removes the mapping between this Buffer and it's VBO-id. Does not
     * actually delete the VBO. <br>
     * This method is usefull if you want to use the same Buffer to create
     * several VBOs. After a VBO is created for this Buffer, update the Buffer
     * and remove it from the VBO cache. You can now reuse the same buffer with
     * another Geometry object. <br>
     * If no association is found, this method does nothing.
     * 
     * @param buffer
     *            The nio Buffer whose associated VBO should be deleted.
     * @return An int wrapped in an Integer object that's the VBO-id of the VBO
     *         previously mapped to this Buffer, or null is no mapping existed.
     */
    public abstract Integer removeFromVBOCache(Buffer buffer);


    /**
     * Create a renderstate via a given renderstate type.
     * 
     * @param type
     *            one of RenderState.RS_****
     * @return the new RenderState or null if an invalid type is given.
     * @deprecated As of 2.0, use {@link #createState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public RenderState createState(int type) {
    	
    	return createState(RenderState.StateType.values()[type]);
    }


    /**
     * Create a {@link RenderState} via a given {@link RenderState.StateType} type.
     * 
     * @param types
     *            one of {@link RenderState.StateType} types
     * @return the new {@link RenderState} or null if an invalid type is given.
     */
    public RenderState createState(RenderState.StateType type) {
    	
        switch (type) {
            case Blend:
                return createBlendState();
            case Clip:
                return createClipState();
            case ColorMask:
                return createColorMaskState();
            case Cull:
                return createCullState();
            case Fog:
                return createFogState();
            case FragmentProgram:
                return createFragmentProgramState();
            case GLSLShaderObjects:
                return createGLSLShaderObjectsState();
            case Light:
                return createLightState();
            case Material:
                return createMaterialState();
            case Shade:
                return createShadeState();
            case Stencil:
                return createStencilState();
            case Texture:
                return createTextureState();
            case VertexProgram:
                return createVertexProgramState();
            case Wireframe:
                return createWireframeState();
            case ZBuffer:
                return createZBufferState();
            case Stipple:
            	return createStippleState();
            default:
                return null;
        }
    }


    /**
     * @return a generated StateRecord representing gl line values for a gl
     *         context.
     */
    public abstract StateRecord createLineRecord();


    /**
     * @return a generated StateRecord representing basic values for this
     *         renderer context.
     */
    public abstract StateRecord createRendererRecord();

    /**
     * Updates a region of the content area of the provided texture using the
     * specified region of the given data.
     * 
     * @param dstTexture
     *            the texture to be updated
     * @param dstX
     *            the x offset relative to the lower-left corner of this texture
     *            where the update will be applied
     * @param dstY
     *            the y offset relative to the lower-left corner of this texture
     *            where the update will be applied
     * @param srcImage
     *            the image data to be uploaded to the texture
     * @param srcX
     *            the x offset relative to the lower-left corner of the supplied
     *            buffer from which to fetch the update rectangle
     * @param srcY
     *            the y offset relative to the lower-left corner of the supplied
     *            buffer from which to fetch the update rectangle
     * @param width
     *            the width of the region to be updated
     * @param height
     *            the height of the region to be updated
     * @throws JmeException
     *             if unable to update the texture
     * @throws UnsupportedOperationException
     *             if updating for the provided texture type is unsupported
     * @see com.sun.opengl.util.texture.Texture#updateSubImage(com.sun.opengl.util.texture.TextureData,
     *      int, int, int, int, int, int, int)
     * @since 2.0
     */
    public abstract void updateTextureSubImage(Texture dstTexture, int dstX,
            int dstY, Image srcImage, int srcX, int srcY, int width, int height)
            throws JmeException, UnsupportedOperationException;
    
    /**
     * Check the underlying rendering system (opengl, etc.) for exceptions.
     * @throws JmeException if an error is found.
     */
    public abstract void checkCardError() throws JmeException;


    /**
     * Perform any necessary cleanup operations such as deleting VBOs, etc.
     */
    public abstract void cleanup();
}
