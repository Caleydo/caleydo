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

package com.jme.system.dummy;

import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme.curve.Curve;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Image.Format;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderQueue;
import com.jme.renderer.Renderer;
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
 * This class makes up a shell renderer with no functionality. It is here to
 * allow the easy creation of renderer-agnostic jME objects (like various
 * RenderState and Spatial) so that they can be used by conversion utilities to
 * read/write jME. It is <b>NOT </b> to be used for rendering as it won't do
 * anything at all.
 * 
 * @version $Id: DummyRenderer.java 4446 2009-06-29 07:36:41Z mulova $
 */
public final class DummyRenderer extends Renderer {

    @Override
    public void setCamera(Camera camera) {
    }

    @Override
    public Camera createCamera(int width, int height) {
        return null;
    }

    @Override
    public BlendState createBlendState() {
        return new BlendState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }

        };
    }

    @Override
    public void flush() {
    }

    @Override
    public void finish() {
    }

    @Override
    public CullState createCullState() {
        return new CullState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public FogState createFogState() {
        return new FogState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public LightState createLightState() {
        return new LightState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public MaterialState createMaterialState() {
        return new MaterialState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public ShadeState createShadeState() {
        return new ShadeState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    class TextureStateN extends TextureState {

        private static final long serialVersionUID = 1L;

        TextureStateN() {
            int numTexUnits = 32;
            numTotalTexUnits = numTexUnits;
            texture = new ArrayList<Texture>(numTexUnits);
        }

        public void load(int unit) {
        }

        public void delete(int unit) {
        }

        public void deleteAll() {
        }

        public void deleteAll(boolean removeFromCache) {
        }

        public void apply() {
        }

        public StateRecord createStateRecord() {
            return null;
        }
    }

    @Override
    public TextureState createTextureState() {
        return new TextureStateN();
    }

    @Override
    public WireframeState createWireframeState() {
        return new WireframeState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public ZBufferState createZBufferState() {
        return new ZBufferState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public VertexProgramState createVertexProgramState() {
        return new VertexProgramState() {

            private static final long serialVersionUID = 1L;

            public boolean isSupported() {
                return false;
            }

            public void load(URL file) {
            }

            public void load(String contents) {
            }

            public void apply() {
            }

            public String getProgram() {
                return null;
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public FragmentProgramState createFragmentProgramState() {
        return new FragmentProgramState() {

            private static final long serialVersionUID = 1L;

            public boolean isSupported() {
                return false;
            }

            public void load(URL file) {
            }

            public void load(String contents) {
            }

            public void apply() {
            }

            public String getProgram() {
                return null;
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public GLSLShaderObjectsState createGLSLShaderObjectsState() {
        return new GLSLShaderObjectsState() {

            private static final long serialVersionUID = 1L;

            public void load(URL vert, URL frag) {
            }

            public void load(String vert, String frag) {

            }

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }

			@Override
			public void load(InputStream vert, InputStream frag) {
			}

			@Override
			protected void sendToGL(ByteBuffer vertexByteBuffer, ByteBuffer fragmentByteBuffer) {
			}

            @Override
            public void cleanup() {
            }
        };
    }

    @Override
    public StencilState createStencilState() {
        return new StencilState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public ClipState createClipState() {
        return new ClipState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    @Override
    public ColorMaskState createColorMaskState() {
        return new ColorMaskState() {

            private static final long serialVersionUID = 1L;

            public void apply() {
            }

            public StateRecord createStateRecord() {
                return null;
            }
        };
    }

    
    @Override
    public StippleState createStippleState() {
    	return new StippleState() {
    		private static final long serialVersionUID = 1L;
    		@Override
    		public void apply() {
    		}
    		@Override
    		public StateRecord createStateRecord() {
    			return null;
    		}
    	};
    }
    
    @Override
    public void setBackgroundColor(ColorRGBA c) {
    }

    @Override
    public ColorRGBA getBackgroundColor() {
        return null;
    }

    @Override
    public void clearZBuffer() {
    }

    @Override
    public void clearColorBuffer() {
    }

    @Override
    public void clearStencilBuffer() {
    }

    @Override
    public void clearBuffers() {
    }

    @Override
    public void clearStrictBuffers() {
    }

    @Override
    public void displayBackBuffer() {
    }

    @Override
    public void setOrtho() {
    }

    @Override
    public void setOrthoCenter() {
    }

    @Override
    public void unsetOrtho() {
    }

    @Override
    public void takeScreenShot(String filename) {
    }

    @Override
    public void grabScreenContents(ByteBuffer buff, Format format, int x,
            int y, int w, int h) {
    }

    @Override
    public void draw(Spatial s) {
    }

    @Override
    public void draw(Point point) {
    }

    @Override
    public void draw(Line line) {
    }

    @Override
    public void draw(Curve c) {
    }

    @Override
    public void draw(Text t) {
    }

    @Override
    public RenderQueue getQueue() {
        return null;
    }

    @Override
    public boolean isProcessingQueue() {
        return false;
    }

    @Override
    public boolean checkAndAdd(Spatial s) {
        return false;
    }

    @Override
    public boolean supportsVBO() {
        return false;
    }

    @Override
    public boolean isHeadless() {
        return false;
    }

    @Override
    public void setHeadless(boolean headless) {
    }

    @Override
    public int getWidth() {
        return -1;
    }

    @Override
    public int getHeight() {
        return -1;
    }

    @Override
    public void reinit(int width, int height) {
    }

    @Override
    public int createDisplayList(Geometry g) {
        return -1;
    }

    @Override
    public void releaseDisplayList(int listId) {
    }

    @Override
    public void setPolygonOffset(float factor, float offset) {
    }

    @Override
    public void clearPolygonOffset() {
    }

    @Override
    public void deleteVBO(Buffer buffer) {

    }

    @Override
    public void deleteVBO(int vboid) {

    }

    @Override
    public void clearVBOCache() {

    }

    @Override
    public Integer removeFromVBOCache(Buffer buffer) {
        return null;
    }

    @Override
    public void draw(TriMesh tMesh) {
    }

    @Override
    public void draw(QuadMesh qMesh) {
    }

    @Override
    public StateRecord createLineRecord() {
        return null;
    }

    @Override
    public StateRecord createRendererRecord() {
        return null;
    }

    @Override
    public void checkCardError() throws JmeException {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public boolean isInOrthoMode() {
        return false;
    }

    @Override
    public void updateTextureSubImage(Texture dstTexture, int dstX, int dstY,
            Image srcImage, int srcX, int srcY, int width, int height)
            throws JmeException, UnsupportedOperationException {
    }
}
