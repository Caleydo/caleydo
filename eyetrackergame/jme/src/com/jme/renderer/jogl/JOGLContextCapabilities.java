/*
 * Copyright (c) 2008 SRA International, Inc.
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

package com.jme.renderer.jogl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.jme.util.geom.BufferUtils;

/**
 * This class collects all of the settings for a specific
 * {@link javax.media.opengl.GLContext}, avoiding unnecessary communications
 * with the graphics hardware for settings which won't change. The class is
 * patterned after the LWJGL {@link org.lwjgl.opengl.ContextCapabilities}
 * implementation, but goes the additional step of accessing common
 * <code>integer</code> and <code>float</code> values. This instance is not
 * immutable, in order to allow the values to be updated whenever the device
 * associated with the {@link javax.media.opengl.GLDrawable} has changed.
 * 
 * @author Steve Vaughan
 * @see org.lwjgl.opengl.ContextCapabilities
 */
public final class JOGLContextCapabilities {

    private final IntBuffer intBuf = BufferUtils.createIntBuffer(16);

    // TODO Due to JOGL buffer check, you can't use smaller sized
    // buffers (min_size = 16) for glGetFloat().
    private final FloatBuffer floatBuf = BufferUtils.createFloatBuffer(16);

    public boolean GL_VERSION_1_1;

    public boolean GL_VERSION_1_2;

    public boolean GL_VERSION_1_3;
    
    public boolean GL_VERSION_1_4;
    
    public boolean GL_VERSION_1_5;
    
    public boolean GL_VERSION_2_0;
    
    public boolean GL_VERSION_2_1;
    
    public boolean GL_VERSION_3_0;

    public boolean GL_ARB_imaging;

    public boolean GL_EXT_blend_func_separate;

    public boolean GL_EXT_blend_equation_separate;

    public boolean GL_EXT_blend_minmax;

    public boolean GL_EXT_blend_subtract;

    public boolean GL_ARB_depth_texture;

    public boolean GL_EXT_fog_coord;
    
    public boolean GL_EXT_compiled_vertex_array;

    public boolean GL_ARB_fragment_program;

    public boolean GL_ARB_shader_objects;

    public boolean GL_ARB_fragment_shader;

    public boolean GL_ARB_vertex_shader;

    public boolean GL_ARB_shading_language_100;

    public boolean GL_EXT_stencil_two_side;

    public boolean GL_EXT_stencil_wrap;

    public boolean GL_ARB_multitexture;

    public boolean GL_ARB_texture_env_dot3;

    public boolean GL_ARB_texture_env_combine;

    public boolean GL_SGIS_generate_mipmap;

    public boolean GL_ARB_vertex_program;

    public boolean GL_ARB_texture_mirrored_repeat;

    public boolean GL_EXT_texture_mirror_clamp;

    public boolean GL_ARB_texture_border_clamp;

    public boolean GL_EXT_texture_compression_s3tc;

    public boolean GL_EXT_texture_3d;

    public boolean GL_ARB_texture_cube_map;

    public boolean GL_EXT_texture_filter_anisotropic;

    public boolean GL_ARB_texture_non_power_of_two;

    public boolean GL_ARB_texture_rectangle;

    public int GL_MAX_TEXTURE_UNITS;

    public int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB;

    public int GL_MAX_TEXTURE_IMAGE_UNITS_ARB;

    public int GL_MAX_TEXTURE_COORDS_ARB;

    public float GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;

    public int GL_MAX_VERTEX_ATTRIBS_ARB;

    public int GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB;

    public int GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB;

    public int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB;

    public int GL_MAX_VARYING_FLOATS_ARB;

    public String GL_SHADING_LANGUAGE_VERSION_ARB;

    public boolean GL_ARB_vertex_buffer_object;

    public boolean GL_ARB_shadow;

    public JOGLContextCapabilities(GLAutoDrawable autodrawable) {
        init(autodrawable.getGL());
    }

    public JOGLContextCapabilities(final GL gl) {
        init(gl);
    }

    public void init(final GL gl) {
        // See Renderer
        GL_ARB_vertex_buffer_object = gl
                .isExtensionAvailable("GL_ARB_vertex_buffer_object");
        GL_VERSION_1_1 = gl.isExtensionAvailable("GL_VERSION_1_1");
        GL_VERSION_1_2 = gl.isExtensionAvailable("GL_VERSION_1_2");
        GL_VERSION_1_3 = gl.isExtensionAvailable("GL_VERSION_1_3");
        GL_VERSION_1_4 = gl.isExtensionAvailable("GL_VERSION_1_4");
        GL_VERSION_1_5 = gl.isExtensionAvailable("GL_VERSION_1_5");
        GL_VERSION_2_0 = gl.isExtensionAvailable("GL_VERSION_2_0");
        GL_VERSION_2_1 = gl.isExtensionAvailable("GL_VERSION_2_1");
        GL_VERSION_3_0 = gl.isExtensionAvailable("GL_VERSION_3_0");

        // See BlendState
        GL_ARB_imaging = gl.isExtensionAvailable("GL_ARB_imaging");
        GL_EXT_blend_func_separate = gl
                .isExtensionAvailable("GL_EXT_blend_func_separate");
        GL_EXT_blend_equation_separate = gl
                .isExtensionAvailable("GL_EXT_blend_equation_separate");
        GL_EXT_blend_minmax = gl.isExtensionAvailable("GL_EXT_blend_minmax");
        GL_EXT_blend_subtract = gl
                .isExtensionAvailable("GL_EXT_blend_subtract");

        // See FogState
        GL_EXT_fog_coord = gl.isExtensionAvailable("GL_EXT_fog_coord");

        // See FragmentProgramState
        GL_ARB_fragment_program = gl
                .isExtensionAvailable("GL_ARB_fragment_program");

        // See ShaderObjectsState
        GL_ARB_shader_objects = gl
                .isExtensionAvailable("GL_ARB_shader_objects");
        GL_ARB_fragment_shader = gl
                .isExtensionAvailable("GL_ARB_fragment_shader");
        GL_ARB_vertex_shader = gl.isExtensionAvailable("GL_ARB_vertex_shader");
        GL_ARB_shading_language_100 = gl
                .isExtensionAvailable("GL_ARB_shading_language_100");
        if(GL_ARB_shading_language_100){
            GL_SHADING_LANGUAGE_VERSION_ARB = gl
            .glGetString(GL.GL_SHADING_LANGUAGE_VERSION_ARB);
        } else {
            GL_SHADING_LANGUAGE_VERSION_ARB = "";
        }
        
        // See TextureState
        GL_ARB_depth_texture = gl.isExtensionAvailable("GL_ARB_depth_texture");
        GL_ARB_shadow = gl.isExtensionAvailable("GL_ARB_shadow");
           
        if(gl.isExtensionAvailable( "GL_ARB_vertex_shader" )) {
            gl.glGetIntegerv(GL.GL_MAX_VERTEX_ATTRIBS_ARB, intBuf);
            GL_MAX_VERTEX_ATTRIBS_ARB = intBuf.get(0);
            gl.glGetIntegerv(GL.GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB, intBuf);
            GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB = intBuf.get(0);
            gl.glGetIntegerv(GL.GL_MAX_VARYING_FLOATS_ARB, intBuf);
            GL_MAX_VARYING_FLOATS_ARB = intBuf.get(0);
            gl.glGetIntegerv(GL.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB, intBuf);
            GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB = intBuf.get(0);
            gl.glGetIntegerv(GL.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB, intBuf);
            GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB = intBuf.get(0);
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_IMAGE_UNITS_ARB, intBuf);
            GL_MAX_TEXTURE_IMAGE_UNITS_ARB = intBuf.get(0);
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_COORDS_ARB, intBuf);
            GL_MAX_TEXTURE_COORDS_ARB = intBuf.get(0);
        } else {
            GL_MAX_VERTEX_ATTRIBS_ARB = 0;
            GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB = 0;
            GL_MAX_VARYING_FLOATS_ARB = 0;
            GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB = 0;
            GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB = 0;
            GL_MAX_TEXTURE_IMAGE_UNITS_ARB = 0;
            GL_MAX_TEXTURE_COORDS_ARB = 0;
        }
        if( gl.isExtensionAvailable( "GL_ARB_fragment_shader" )) {
            gl.glGetIntegerv(GL.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB, intBuf);
            GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB = intBuf.get(0);
        } else {
            GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB = 0;
        }
        
        if( gl.isExtensionAvailable( "GL_EXT_texture_filter_anisotropic" )) {
            // FIXME I don't think this was necessary: floatBuf.rewind();
            gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, floatBuf);
            GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = floatBuf.get(0);
        } else {
            GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0;
        }
        // See StencilState
        GL_EXT_stencil_two_side = gl
                .isExtensionAvailable("GL_EXT_stencil_two_side");
        GL_EXT_stencil_wrap = gl.isExtensionAvailable("GL_EXT_stencil_wrap");
        
        // See TextureState
        GL_ARB_multitexture = gl.isExtensionAvailable("GL_ARB_multitexture");
        if(GL_ARB_multitexture) {
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS, intBuf);
            GL_MAX_TEXTURE_UNITS = intBuf.get(0);
        } else {
            GL_MAX_TEXTURE_UNITS = 1;
        }    
        GL_ARB_texture_env_dot3 = gl
                .isExtensionAvailable("GL_ARB_texture_env_dot3");
        GL_ARB_texture_env_combine = gl
                .isExtensionAvailable("GL_ARB_texture_env_combine");
        GL_SGIS_generate_mipmap = gl
                .isExtensionAvailable("GL_SGIS_generate_mipmap");
        GL_EXT_texture_compression_s3tc = gl
                .isExtensionAvailable("GL_EXT_texture_compression_s3tc");
        GL_EXT_texture_3d = gl.isExtensionAvailable("GL_EXT_texture_3d");
        GL_ARB_texture_cube_map = gl
                .isExtensionAvailable("GL_ARB_texture_cube_map");
        GL_EXT_texture_filter_anisotropic = gl
                .isExtensionAvailable("GL_EXT_texture_filter_anisotropic");
        GL_ARB_texture_non_power_of_two = gl
                .isExtensionAvailable("GL_ARB_texture_non_power_of_two");
        GL_ARB_texture_rectangle = gl
                .isExtensionAvailable("GL_ARB_texture_rectangle");
        // See VertexProgram
        GL_ARB_vertex_program = gl
                .isExtensionAvailable("GL_ARB_vertex_program");

        // See TextureStateRecord
        GL_ARB_texture_mirrored_repeat = gl
                .isExtensionAvailable("GL_ARB_texture_mirrored_repeat");
        GL_EXT_texture_mirror_clamp = gl
                .isExtensionAvailable("GL_EXT_texture_mirror_clamp");
        GL_ARB_texture_border_clamp = gl
                .isExtensionAvailable("GL_ARB_texture_border_clamp");
        
        GL_EXT_compiled_vertex_array = gl.isExtensionAvailable("GL_EXT_compiled_vertex_array");
    }

}
 