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
package com.jme.scene.state.lwjgl.records;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.opengl.ARBDepthTexture;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShadow;
import org.lwjgl.opengl.ARBTextureBorderClamp;
import org.lwjgl.opengl.ARBTextureEnvCombine;
import org.lwjgl.opengl.ARBTextureEnvDot3;
import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.ARBTextureMirroredRepeat;
import org.lwjgl.opengl.EXTTextureCompressionS3TC;
import org.lwjgl.opengl.EXTTextureMirrorClamp;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import com.jme.image.Texture;
import com.jme.image.Image.Format;
import com.jme.image.Texture.ApplyMode;
import com.jme.image.Texture.CombinerFunctionAlpha;
import com.jme.image.Texture.CombinerFunctionRGB;
import com.jme.image.Texture.CombinerOperandAlpha;
import com.jme.image.Texture.CombinerOperandRGB;
import com.jme.image.Texture.CombinerSource;
import com.jme.image.Texture.DepthTextureCompareFunc;
import com.jme.image.Texture.DepthTextureCompareMode;
import com.jme.image.Texture.DepthTextureMode;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.math.Vector3f;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.TextureState.CorrectionType;
import com.jme.util.geom.BufferUtils;

public class TextureStateRecord extends StateRecord {
    
    public FloatBuffer eyePlaneS = BufferUtils.createFloatBuffer(4);
    public FloatBuffer eyePlaneT = BufferUtils.createFloatBuffer(4);
    public FloatBuffer eyePlaneR = BufferUtils.createFloatBuffer(4);
    public FloatBuffer eyePlaneQ = BufferUtils.createFloatBuffer(4);

    public HashMap<Integer, TextureRecord> textures;
    public TextureUnitRecord[] units;
    public int hint = -1;
    public int currentUnit = -1;
    
    private boolean supportsMirroredRepeat;
    private boolean supportsMirrorClamp;
    private boolean supportsBorderClamp;
    private boolean supportsEdgeClamp;

    /**
     * temporary rotation axis vector to flatline memory usage.
     */
    public final Vector3f tmp_rotation1 = new Vector3f();

    /**
     * temporary matrix buffer to flatline memory usage.
     */
    public final FloatBuffer tmp_matrixBuffer = BufferUtils.createFloatBuffer(16);

    public TextureStateRecord(int maxUnits) {
        textures = new HashMap<Integer, TextureRecord>();
        units = new TextureUnitRecord[maxUnits];
        for (int i = 0; i < maxUnits; i++) {
            units[i] = new TextureUnitRecord();
        }

        eyePlaneS.put(1.0f).put(0.0f).put(0.0f).put(0.0f);
        eyePlaneT.put(0.0f).put(1.0f).put(0.0f).put(0.0f);
        eyePlaneR.put(0.0f).put(0.0f).put(1.0f).put(0.0f);
        eyePlaneQ.put(0.0f).put(0.0f).put(0.0f).put(1.0f);
        
        supportsMirroredRepeat = GLContext.getCapabilities().GL_ARB_texture_mirrored_repeat;
        supportsMirrorClamp = GLContext.getCapabilities().GL_EXT_texture_mirror_clamp;
        supportsBorderClamp = GLContext.getCapabilities().GL_ARB_texture_border_clamp;
        supportsEdgeClamp = GLContext.getCapabilities().OpenGL12;
    }

    public TextureRecord getTextureRecord(int textureId, Texture.Type type) {
        TextureRecord tr = textures.get(textureId);
        if (tr == null) {
            tr = new TextureRecord();
            textures.put(textureId, tr);
        }
        return tr;
    }
    
    public void removeTextureRecord(int textureId) {
        textures.remove(textureId);
        for (int i = 0; i < units.length; i++) {
            if (units[i].boundTexture == textureId)
                units[i].boundTexture = -1;
        }
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        currentUnit = -1;
        hint = -1;
        Collection<TextureRecord> texs = textures.values();
        for (TextureRecord tr : texs) {
            tr.invalidate();
        }
        for (int i = 0; i < units.length; i++) {
            units[i].invalidate();
        }
    }
    
    @Override
    public void validate() {
        super.validate();
        Collection<TextureRecord> texs = textures.values();
        for (TextureRecord tr : texs) {
            tr.validate();
        }
        for (int i = 0; i < units.length; i++) {
            units[i].validate();
        }
    }

    // None static because of the support booleans
    public int getGLWrap(WrapMode wrap) {
        switch (wrap) {
            case Repeat:
                return GL11.GL_REPEAT;
            case MirroredRepeat:
                if (supportsMirroredRepeat)
                    return ARBTextureMirroredRepeat.GL_MIRRORED_REPEAT_ARB;
                else
                    return GL11.GL_REPEAT;
            case MirrorClamp:
                if (supportsMirrorClamp)
                    return EXTTextureMirrorClamp.GL_MIRROR_CLAMP_EXT;
                // FALLS THROUGH
            case Clamp:
                return GL11.GL_CLAMP;
            case MirrorBorderClamp:
                if (supportsMirrorClamp)
                    return ARBTextureMirroredRepeat.GL_MIRRORED_REPEAT_ARB;
                // FALLS THROUGH
            case BorderClamp:
                if (supportsBorderClamp)
                    return ARBTextureBorderClamp.GL_CLAMP_TO_BORDER_ARB;
                else
                    return GL11.GL_CLAMP;
            case MirrorEdgeClamp:
                if (supportsMirrorClamp)
                    return ARBTextureMirroredRepeat.GL_MIRRORED_REPEAT_ARB;
                // FALLS THROUGH
            case EdgeClamp:
                if (supportsEdgeClamp)
                    return GL12.GL_CLAMP_TO_EDGE;
                else 
                    return GL11.GL_CLAMP;
        }
        throw new IllegalArgumentException("invalid WrapMode type: "+wrap);
    }

    public static boolean isCompressedType(Format format) {
        switch (format) {
            case NativeDXT1:
            case NativeDXT1A:
            case NativeDXT3:
            case NativeDXT5:
                return true;
            default:
                return false;
        }
    }

    public static int getGLDataFormat(Format format) {
        switch (format) {
            // first some frequently used formats
            case RGBA8:
                return GL11.GL_RGBA8;
            case RGB8:
                return GL11.GL_RGB8;
            case Alpha8:
                return GL11.GL_ALPHA8;
            case RGB_TO_DXT1:
            case NativeDXT1:
                return EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
            case RGBA_TO_DXT1:
            case NativeDXT1A:
                return EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
            case RGBA_TO_DXT3:
            case NativeDXT3:
                return EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
            case RGBA_TO_DXT5:
            case NativeDXT5:
                return EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;

            // The rest...
            case Alpha4:
                return GL11.GL_ALPHA4;
            case Alpha12:
                return GL11.GL_ALPHA12;
            case Alpha16:
                return GL11.GL_ALPHA16;
            case Luminance4:
                return GL11.GL_LUMINANCE4;
            case Luminance8:
                return GL11.GL_LUMINANCE8;
            case Luminance12:
                return GL11.GL_LUMINANCE12;
            case Luminance16:
                return GL11.GL_LUMINANCE16;
            case Intensity4:
                return GL11.GL_INTENSITY4;
            case Intensity8:
                return GL11.GL_INTENSITY8;
            case Intensity12:
                return GL11.GL_INTENSITY12;
            case Intensity16:
                return GL11.GL_INTENSITY16;
            case Luminance4Alpha4:
                return GL11.GL_LUMINANCE4_ALPHA4;
            case Luminance6Alpha2:
                return GL11.GL_LUMINANCE6_ALPHA2;
            case Luminance8Alpha8:
                return GL11.GL_LUMINANCE8_ALPHA8;
            case Luminance12Alpha4:
                return GL11.GL_LUMINANCE12_ALPHA4;
            case Luminance12Alpha12:
                return GL11.GL_LUMINANCE12_ALPHA12;
            case Luminance16Alpha16:
                return GL11.GL_LUMINANCE16_ALPHA16;
            case R3G3B2:
                return GL11.GL_R3_G3_B2;
            case RGB4:
                return GL11.GL_RGB4;
            case RGB5:
                return GL11.GL_RGB5;
            case RGB10:
                return GL11.GL_RGB10;
            case RGB12:
                return GL11.GL_RGB12;
            case RGB16:
                return GL11.GL_RGB16;
            case RGBA2:
                return GL11.GL_RGBA2;
            case RGBA4:
                return GL11.GL_RGBA4;
            case RGB5A1:
                return GL11.GL_RGB5_A1;
            case RGB10A2:
                return GL11.GL_RGB10_A2;
            case RGBA12:
                return GL11.GL_RGBA12;
            case RGBA16:
                return GL11.GL_RGBA16;
            case Depth16:
                return ARBDepthTexture.GL_DEPTH_COMPONENT16_ARB;
            case Depth24:
                return ARBDepthTexture.GL_DEPTH_COMPONENT24_ARB;
            case Depth32:
                return ARBDepthTexture.GL_DEPTH_COMPONENT32_ARB;
            case RGB16F:
                return ARBTextureFloat.GL_RGB16F_ARB;
            case RGB32F:
                return ARBTextureFloat.GL_RGB32F_ARB;
            case RGBA16F:
                return ARBTextureFloat.GL_RGBA16F_ARB;
            case RGBA32F:
                return ARBTextureFloat.GL_RGBA32F_ARB;
            case Alpha16F:
                return ARBTextureFloat.GL_ALPHA16F_ARB;
            case Alpha32F:
                return ARBTextureFloat.GL_ALPHA32F_ARB;
            case Luminance16F:
                return ARBTextureFloat.GL_LUMINANCE16F_ARB;
            case Luminance32F:
                return ARBTextureFloat.GL_LUMINANCE32F_ARB;
            case LuminanceAlpha16F:
                return ARBTextureFloat.GL_LUMINANCE_ALPHA16F_ARB;
            case LuminanceAlpha32F:
                return ARBTextureFloat.GL_LUMINANCE_ALPHA32F_ARB;
            case Intensity16F:
                return ARBTextureFloat.GL_INTENSITY16F_ARB;
            case Intensity32F:
                return ARBTextureFloat.GL_INTENSITY32F_ARB;
        }
        throw new IllegalArgumentException("Incorrect format set: "+format);
    }

    public static int getGLPixelFormat(Format format) {
        switch (format) {
            case RGBA2:
            case RGBA4:
            case RGBA8:
            case RGB5A1:
            case RGB10A2:
            case RGBA12:
            case RGBA16:
            case RGBA_TO_DXT1:
            case NativeDXT1A:
            case RGBA_TO_DXT3:
            case NativeDXT3:
            case RGBA_TO_DXT5:
            case NativeDXT5:
            case RGBA16F:
            case RGBA32F:
                return GL11.GL_RGBA;
            case R3G3B2:
            case RGB4:
            case RGB5:
            case RGB8:
            case RGB10:
            case RGB12:
            case RGB16:
            case RGB_TO_DXT1:
            case NativeDXT1:
            case RGB16F:
            case RGB32F:
                return GL11.GL_RGB;
            case Alpha4:
            case Alpha8:
            case Alpha12:
            case Alpha16:
            case Alpha16F:
            case Alpha32F:
                return GL11.GL_ALPHA;
            case Luminance4:
            case Luminance8:
            case Luminance12:
            case Luminance16:
            case Luminance16F:
            case Luminance32F:
                return GL11.GL_LUMINANCE;
            case Intensity4:
            case Intensity8:
            case Intensity12:
            case Intensity16:
            case Intensity16F:
            case Intensity32F:
                return GL11.GL_INTENSITY;
            case Luminance4Alpha4:
            case Luminance6Alpha2:
            case Luminance8Alpha8:
            case Luminance12Alpha4:
            case Luminance12Alpha12:
            case Luminance16Alpha16:
            case LuminanceAlpha16F:
            case LuminanceAlpha32F:
                return GL11.GL_LUMINANCE_ALPHA;
            case Depth16:
            case Depth24:
            case Depth32:
                return GL11.GL_DEPTH_COMPONENT;
        }
        throw new IllegalArgumentException("Incorrect format set: "+format);
    }

    public static int getGLDepthTextureMode(DepthTextureMode mode) {
    	switch (mode) {
    	case Alpha:
    		return GL11.GL_ALPHA;
    	case Luminance:
    		return GL11.GL_LUMINANCE;
    	case Intensity:
    	default:
    		return GL11.GL_INTENSITY;
    	}
    }
    
    public static int getGLDepthTextureCompareMode(DepthTextureCompareMode mode) {
    	switch (mode) {
    	case RtoTexture:
    		return ARBShadow.GL_COMPARE_R_TO_TEXTURE_ARB;
    	case None:
    	default:
    		return GL11.GL_NONE;
    	}
    }
    
    public static int getGLDepthTextureCompareFunc(DepthTextureCompareFunc func) {
    	switch (func) {
    	case GreaterThanEqual:
    		return GL11.GL_GEQUAL;
    	case LessThanEqual:
    	default:
    		return GL11.GL_LEQUAL;
    	}
    }
    
    public static int getGLMagFilter(MagnificationFilter magFilter) {
        switch (magFilter) {
            case Bilinear:
                return GL11.GL_LINEAR;
            case NearestNeighbor:
            default:
                return GL11.GL_NEAREST;

        }
    }

    public static int getGLMinFilter(MinificationFilter filter) {
        switch (filter) {
            case BilinearNoMipMaps:
                return GL11.GL_LINEAR;
            case Trilinear:
                return GL11.GL_LINEAR_MIPMAP_LINEAR;
            case BilinearNearestMipMap:
                return GL11.GL_LINEAR_MIPMAP_NEAREST;
            case NearestNeighborNoMipMaps:
                return GL11.GL_NEAREST;
            case NearestNeighborNearestMipMap:
                return GL11.GL_NEAREST_MIPMAP_NEAREST;
            case NearestNeighborLinearMipMap:
                return GL11.GL_NEAREST_MIPMAP_LINEAR;
        }
        throw new IllegalArgumentException("invalid MinificationFilter type: "+filter);
    }

    public static int getGLEnvMode(ApplyMode apply) {
        switch (apply) {
            case Replace:
                return GL11.GL_REPLACE;
            case Blend:
                return GL11.GL_BLEND;
            case Combine:
                return ARBTextureEnvCombine.GL_COMBINE_ARB;
            case Decal:
                return GL11.GL_DECAL;
            case Add:
                return GL11.GL_ADD;
            case Modulate:
                return GL11.GL_MODULATE;
        }
        throw new IllegalArgumentException("invalid ApplyMode type: "+apply);
    }

    public static int getPerspHint(CorrectionType type) {
        switch (type) {
            case Perspective:
                return GL11.GL_NICEST;
            case Affine:
                return GL11.GL_FASTEST;
        }
        throw new IllegalArgumentException("unknown correction type: "+type);
    }

    public static int getGLCombineOpRGB(CombinerOperandRGB operand) {
        switch (operand) {
            case SourceColor:
                return GL11.GL_SRC_COLOR;
            case OneMinusSourceColor:
                return GL11.GL_ONE_MINUS_SRC_COLOR;
            case SourceAlpha:
                return GL11.GL_SRC_ALPHA;
            case OneMinusSourceAlpha:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
        }
        throw new IllegalArgumentException("invalid CombinerOperandRGB type: "+operand);
    }

    public static int getGLCombineOpAlpha(CombinerOperandAlpha operand) {
        switch (operand) {
            case SourceAlpha:
                return GL11.GL_SRC_ALPHA;
            case OneMinusSourceAlpha:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
        }
        throw new IllegalArgumentException("invalid CombinerOperandAlpha type: "+operand);
    }

    public static int getGLCombineSrc(CombinerSource combineSrc) {
        switch (combineSrc) {
            case CurrentTexture:
                return GL11.GL_TEXTURE;
            case PrimaryColor:
                return ARBTextureEnvCombine.GL_PRIMARY_COLOR_ARB;
            case Constant:
                return ARBTextureEnvCombine.GL_CONSTANT_ARB;
            case Previous:
                return ARBTextureEnvCombine.GL_PREVIOUS_ARB;
            case TextureUnit0:
                return ARBMultitexture.GL_TEXTURE0_ARB;
            case TextureUnit1:
                return ARBMultitexture.GL_TEXTURE1_ARB;
            case TextureUnit2:
                return ARBMultitexture.GL_TEXTURE2_ARB;
            case TextureUnit3:
                return ARBMultitexture.GL_TEXTURE3_ARB;
            case TextureUnit4:
                return ARBMultitexture.GL_TEXTURE4_ARB;
            case TextureUnit5:
                return ARBMultitexture.GL_TEXTURE5_ARB;
            case TextureUnit6:
                return ARBMultitexture.GL_TEXTURE6_ARB;
            case TextureUnit7:
                return ARBMultitexture.GL_TEXTURE7_ARB;
            case TextureUnit8:
                return ARBMultitexture.GL_TEXTURE8_ARB;
            case TextureUnit9:
                return ARBMultitexture.GL_TEXTURE9_ARB;
            case TextureUnit10:
                return ARBMultitexture.GL_TEXTURE10_ARB;
            case TextureUnit11:
                return ARBMultitexture.GL_TEXTURE11_ARB;
            case TextureUnit12:
                return ARBMultitexture.GL_TEXTURE12_ARB;
            case TextureUnit13:
                return ARBMultitexture.GL_TEXTURE13_ARB;
            case TextureUnit14:
                return ARBMultitexture.GL_TEXTURE14_ARB;
            case TextureUnit15:
                return ARBMultitexture.GL_TEXTURE15_ARB;
            case TextureUnit16:
                return ARBMultitexture.GL_TEXTURE16_ARB;
            case TextureUnit17:
                return ARBMultitexture.GL_TEXTURE17_ARB;
            case TextureUnit18:
                return ARBMultitexture.GL_TEXTURE18_ARB;
            case TextureUnit19:
                return ARBMultitexture.GL_TEXTURE19_ARB;
            case TextureUnit20:
                return ARBMultitexture.GL_TEXTURE20_ARB;
            case TextureUnit21:
                return ARBMultitexture.GL_TEXTURE21_ARB;
            case TextureUnit22:
                return ARBMultitexture.GL_TEXTURE22_ARB;
            case TextureUnit23:
                return ARBMultitexture.GL_TEXTURE23_ARB;
            case TextureUnit24:
                return ARBMultitexture.GL_TEXTURE24_ARB;
            case TextureUnit25:
                return ARBMultitexture.GL_TEXTURE25_ARB;
            case TextureUnit26:
                return ARBMultitexture.GL_TEXTURE26_ARB;
            case TextureUnit27:
                return ARBMultitexture.GL_TEXTURE27_ARB;
            case TextureUnit28:
                return ARBMultitexture.GL_TEXTURE28_ARB;
            case TextureUnit29:
                return ARBMultitexture.GL_TEXTURE29_ARB;
            case TextureUnit30:
                return ARBMultitexture.GL_TEXTURE30_ARB;
            case TextureUnit31:
                return ARBMultitexture.GL_TEXTURE31_ARB;
        }
        throw new IllegalArgumentException("invalid CombinerSource type: "+combineSrc);
    }

    public static int getGLCombineFuncAlpha(CombinerFunctionAlpha combineFunc) {
        switch (combineFunc) {
            case Modulate:
                return GL11.GL_MODULATE;
            case Replace:
                return GL11.GL_REPLACE;
            case Add:
                return GL11.GL_ADD;
            case AddSigned:
                return ARBTextureEnvCombine.GL_ADD_SIGNED_ARB;
            case Subtract:
                return ARBTextureEnvCombine.GL_SUBTRACT_ARB;
            case Interpolate:
                return ARBTextureEnvCombine.GL_INTERPOLATE_ARB;
        }
        throw new IllegalArgumentException("invalid CombinerFunctionAlpha type: "+combineFunc);
    }

    public static int getGLCombineFuncRGB(CombinerFunctionRGB combineFunc) {
        switch (combineFunc) {
            case Modulate:
                return GL11.GL_MODULATE;
            case Replace:
                return GL11.GL_REPLACE;
            case Add:
                return GL11.GL_ADD;
            case AddSigned:
                return ARBTextureEnvCombine.GL_ADD_SIGNED_ARB;
            case Subtract:
                return ARBTextureEnvCombine.GL_SUBTRACT_ARB;
            case Interpolate:
                return ARBTextureEnvCombine.GL_INTERPOLATE_ARB;
            case Dot3RGB:
                return ARBTextureEnvDot3.GL_DOT3_RGB_ARB;
            case Dot3RGBA:
                return ARBTextureEnvDot3.GL_DOT3_RGBA_ARB;
        }
        throw new IllegalArgumentException("invalid CombinerFunctionRGB type: "+combineFunc);
    }
}
