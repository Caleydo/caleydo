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

package com.jme.image;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.TextureKey;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Texture</code> defines a texture object to be used to display an
 * image on a piece of geometry. The image to be displayed is defined by the
 * <code>Image</code> class. All attributes required for texture mapping are
 * contained within this class. This includes mipmapping if desired,
 * magnificationFilter options, apply options and correction options. Default
 * values are as follows: minificationFilter - NearestNeighborNoMipMaps,
 * magnificationFilter - NearestNeighbor, wrap - EdgeClamp on S,T and R, apply -
 * Modulate, enivoronment - None.
 * 
 * @see com.jme.image.Image
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Texture.java 4490 2009-07-10 10:14:25Z mulova $
 */
public abstract class Texture implements Savable {
    private static final long serialVersionUID = -3642148179543729674L;

    public static boolean DEFAULT_STORE_TEXTURE = false;

    public enum Type {
        /**
         * One dimensional texture. (basically a line)
         */
        OneDimensional,
        /**
         * Two dimensional texture (default). A rectangle.
         */
        TwoDimensional,
        /**
         * Three dimensional texture. (A cube)
         */
        ThreeDimensional,
        /**
         * A set of 6 TwoDimensional textures arranged as faces of a cube facing
         * inwards.
         */
        CubeMap;
    }
    
    public enum MinificationFilter {

        /**
         * Nearest neighbor interpolation is the fastest and crudest filtering
         * method - it simply uses the color of the texel closest to the pixel
         * center for the pixel color. While fast, this results in aliasing and
         * shimmering during minification. (GL equivalent: GL_NEAREST)
         */
        NearestNeighborNoMipMaps(false),

        /**
         * In this method the four nearest texels to the pixel center are
         * sampled (at texture level 0), and their colors are combined by
         * weighted averages. Though smoother, without mipmaps it suffers the
         * same aliasing and shimmering problems as nearest
         * NearestNeighborNoMipMaps. (GL equivalent: GL_LINEAR)
         */
        BilinearNoMipMaps(false),

        /**
         * Same as NearestNeighborNoMipMaps except that instead of using samples
         * from texture level 0, the closest mipmap level is chosen based on
         * distance. This reduces the aliasing and shimmering significantly, but
         * does not help with blockiness. (GL equivalent:
         * GL_NEAREST_MIPMAP_NEAREST)
         */
        NearestNeighborNearestMipMap(true),

        /**
         * Same as BilinearNoMipMaps except that instead of using samples from
         * texture level 0, the closest mipmap level is chosen based on
         * distance. By using mipmapping we avoid the aliasing and shimmering
         * problems of BilinearNoMipMaps. (GL equivalent:
         * GL_LINEAR_MIPMAP_NEAREST)
         */
        BilinearNearestMipMap(true),

        /**
         * Similar to NearestNeighborNoMipMaps except that instead of using
         * samples from texture level 0, a sample is chosen from each of the
         * closest (by distance) two mipmap levels. A weighted average of these
         * two samples is returned. (GL equivalent: GL_NEAREST_MIPMAP_LINEAR)
         */
        NearestNeighborLinearMipMap(true),

        /**
         * Trilinear filtering is a remedy to a common artifact seen in
         * mipmapped bilinearly filtered images: an abrupt and very noticeable
         * change in quality at boundaries where the renderer switches from one
         * mipmap level to the next. Trilinear filtering solves this by doing a
         * texture lookup and bilinear filtering on the two closest mipmap
         * levels (one higher and one lower quality), and then linearly
         * interpolating the results. This results in a smooth degradation of
         * texture quality as distance from the viewer increases, rather than a
         * series of sudden drops. Of course, closer than Level 0 there is only
         * one mipmap level available, and the algorithm reverts to bilinear
         * filtering (GL equivalent: GL_LINEAR_MIPMAP_LINEAR)
         */
        Trilinear(true);

        private boolean usesMipMapLevels;

        private MinificationFilter(boolean usesMipMapLevels) {
            this.usesMipMapLevels = usesMipMapLevels;
        }

        public boolean usesMipMapLevels() {
            return usesMipMapLevels;
        }
    }

    public enum MagnificationFilter {

        /**
         * Nearest neighbor interpolation is the fastest and crudest filtering
         * mode - it simply uses the color of the texel closest to the pixel
         * center for the pixel color. While fast, this results in texture
         * 'blockiness' during magnification. (GL equivalent: GL_NEAREST)
         */
        NearestNeighbor,

        /**
         * In this mode the four nearest texels to the pixel center are sampled
         * (at the closest mipmap level), and their colors are combined by
         * weighted average according to distance. This removes the 'blockiness'
         * seen during magnification, as there is now a smooth gradient of color
         * change from one texel to the next, instead of an abrupt jump as the
         * pixel center crosses the texel boundary. (GL equivalent: GL_LINEAR)
         */
        Bilinear;

    }

    public enum WrapMode {
        /**
         * Only the fractional portion of the coordinate is considered.
         */
        Repeat,
        /**
         * Only the fractional portion of the coordinate is considered, but if
         * the integer portion is odd, we'll use 1 - the fractional portion.
         * (Introduced around OpenGL1.4) Falls back on Repeat if not supported.
         */
        MirroredRepeat,
        /**
         * coordinate will be clamped to [0,1]
         */
        Clamp,
        /**
         * mirrors and clamps the texture coordinate, where mirroring and
         * clamping a value f computes:
         * <code>mirrorClamp(f) = min(1, max(1/(2*N),
         * abs(f)))</code> where N
         * is the size of the one-, two-, or three-dimensional texture image in
         * the direction of wrapping. (Introduced after OpenGL1.4) Falls back on
         * Clamp if not supported.
         */
        MirrorClamp,
        /**
         * coordinate will be clamped to the range [-1/(2N), 1 + 1/(2N)] where N
         * is the size of the texture in the direction of clamping. Falls back
         * on Clamp if not supported.
         */
        BorderClamp,
        /**
         * Wrap mode MIRROR_CLAMP_TO_BORDER_EXT mirrors and clamps to border the
         * texture coordinate, where mirroring and clamping to border a value f
         * computes:
         * <code>mirrorClampToBorder(f) = min(1+1/(2*N), max(1/(2*N), abs(f)))</code>
         * where N is the size of the one-, two-, or three-dimensional texture
         * image in the direction of wrapping." (Introduced after OpenGL1.4)
         * Falls back on BorderClamp if not supported.
         */
        MirrorBorderClamp,
        /**
         * coordinate will be clamped to the range [1/(2N), 1 - 1/(2N)] where N
         * is the size of the texture in the direction of clamping. Falls back
         * on Clamp if not supported.
         */
        EdgeClamp,
        /**
         * mirrors and clamps to edge the texture coordinate, where mirroring
         * and clamping to edge a value f computes:
         * <code>mirrorClampToEdge(f) = min(1-1/(2*N), max(1/(2*N), abs(f)))</code>
         * where N is the size of the one-, two-, or three-dimensional texture
         * image in the direction of wrapping. (Introduced after OpenGL1.4)
         * Falls back on EdgeClamp if not supported.
         */
        MirrorEdgeClamp;
    }

    public enum WrapAxis {
        /**
         * S wrapping (u or "horizontal" wrap)
         */
        S,
        /**
         * T wrapping (v or "vertical" wrap)
         */
        T,
        /**
         * R wrapping (w or "depth" wrap)
         */
        R;
    }

    public enum ApplyMode {
        /**
         * Apply modifier that replaces the previous pixel color with the
         * texture color.
         */
        Replace,
        /**
         * Apply modifier that replaces the color values of the pixel but makes
         * use of the alpha values.
         */
        Decal,
        /**
         * Apply modifier multiples the color of the pixel with the texture
         * color.
         */
        Modulate,
        /**
         * Apply modifier that interpolates the color of the pixel with a blend
         * color using the texture color, such that the final color value is Cv =
         * (1 - Ct) * Cf + BlendColor * Ct Where Ct is the color of the texture
         * and Cf is the initial pixel color.
         */
        Blend,
        /**
         * Apply modifier combines two textures based on the combine parameters
         * set on this texture.
         */
        Combine,
        /**
         * Apply modifier adds two textures.
         */
        Add;
    }

    public enum EnvironmentalMapMode {
        /**
         * Use texture coordinates as they are. (Do not do texture coordinate
         * generation.)
         */
        None,
        /**
         * TODO: add documentation
         */
        EyeLinear,
        /**
         * TODO: add documentation
         */
        ObjectLinear,
        /**
         * TODO: add documentation
         */
        SphereMap,
        /**
         * TODO: add documentation
         */
        NormalMap,
        /**
         * TODO: add documentation
         */
        ReflectionMap;
    }

    public enum CombinerFunctionRGB {
        /** Arg0 */
        Replace,
        /** Arg0 * Arg1 */
        Modulate,
        /** Arg0 + Arg1 */
        Add,
        /** Arg0 + Arg1 - 0.5 */
        AddSigned,
        /** Arg0 * Arg2 + Arg1 * (1 - Arg2) */
        Interpolate,
        /** Arg0 - Arg1 */
        Subtract,
        /**
         * 4 * ((Arg0r - 0.5) * (Arg1r - 0.5) + (Arg0g - 0.5) * (Arg1g - 0.5) +
         * (Arg0b - 0.5) * (Arg1b - 0.5)) [ result placed in R,G,B ]
         */
        Dot3RGB,
        /**
         * 4 * ((Arg0r - 0.5) * (Arg1r - 0.5) + (Arg0g - 0.5) * (Arg1g - 0.5) +
         * (Arg0b - 0.5) * (Arg1b - 0.5)) [ result placed in R,G,B,A ]
         */
        Dot3RGBA;
    }

    public enum CombinerFunctionAlpha {
        /** Arg0 */
        Replace,
        /** Arg0 * Arg1 */
        Modulate,
        /** Arg0 + Arg1 */
        Add,
        /** Arg0 + Arg1 - 0.5 */
        AddSigned,
        /** Arg0 * Arg2 + Arg1 * (1 - Arg2) */
        Interpolate,
        /** Arg0 - Arg1 */
        Subtract;
    }

    public enum CombinerSource {
        /**
         * The incoming fragment color from the previous texture unit. When used
         * on texture unit 0, this is the same as using PrimaryColor.
         */
        Previous,
        /** The blend color set on this texture. */
        Constant,
        /** The incoming fragment color before any texturing is applied. */
        PrimaryColor,
        /** The current texture unit's bound texture. */
        CurrentTexture,
        /** The texture bound on texture unit 0. */
        TextureUnit0,
        /** The texture bound on texture unit 1. */
        TextureUnit1,
        /** The texture bound on texture unit 2. */
        TextureUnit2,
        /** The texture bound on texture unit 3. */
        TextureUnit3,
        /** The texture bound on texture unit 4. */
        TextureUnit4,
        /** The texture bound on texture unit 5. */
        TextureUnit5,
        /** The texture bound on texture unit 6. */
        TextureUnit6,
        /** The texture bound on texture unit 7. */
        TextureUnit7,
        /** The texture bound on texture unit 8. */
        TextureUnit8,
        /** The texture bound on texture unit 9. */
        TextureUnit9,
        /** The texture bound on texture unit 10. */
        TextureUnit10,
        /** The texture bound on texture unit 11. */
        TextureUnit11,
        /** The texture bound on texture unit 12. */
        TextureUnit12,
        /** The texture bound on texture unit 13. */
        TextureUnit13,
        /** The texture bound on texture unit 14. */
        TextureUnit14,
        /** The texture bound on texture unit 15. */
        TextureUnit15,
        /** The texture bound on texture unit 16. */
        TextureUnit16,
        /** The texture bound on texture unit 17. */
        TextureUnit17,
        /** The texture bound on texture unit 18. */
        TextureUnit18,
        /** The texture bound on texture unit 19. */
        TextureUnit19,
        /** The texture bound on texture unit 20. */
        TextureUnit20,
        /** The texture bound on texture unit 21. */
        TextureUnit21,
        /** The texture bound on texture unit 22. */
        TextureUnit22,
        /** The texture bound on texture unit 23. */
        TextureUnit23,
        /** The texture bound on texture unit 24. */
        TextureUnit24,
        /** The texture bound on texture unit 25. */
        TextureUnit25,
        /** The texture bound on texture unit 26. */
        TextureUnit26,
        /** The texture bound on texture unit 27. */
        TextureUnit27,
        /** The texture bound on texture unit 28. */
        TextureUnit28,
        /** The texture bound on texture unit 29. */
        TextureUnit29,
        /** The texture bound on texture unit 30. */
        TextureUnit30,
        /** The texture bound on texture unit 31. */
        TextureUnit31;
    }

    public enum CombinerOperandRGB {
        SourceColor, OneMinusSourceColor, SourceAlpha, OneMinusSourceAlpha;
    }

    public enum CombinerOperandAlpha {
        SourceAlpha, OneMinusSourceAlpha;
    }

    public enum CombinerScale {
        /** No scale (1.0x) */
        One(1.0f),
        /** 2.0x */
        Two(2.0f),
        /** 4.0x */
        Four(4.0f);

        private float scale;

        private CombinerScale(float scale) {
            this.scale = scale;
        }

        public float floatValue() {
            return scale;
        }
    }

    /**
     * When doing RenderToTexture operations with this texture, this value
     * indicates what content to render into this texture.
     */
    public enum RenderToTextureType {
        /**
         *Each element is an RGB triple. OpenGL converts it to fixed-point or floating-point and assembles it into an RGBA element by attaching 1 for alpha. 
         *Each component is then clamped to the range [0,1].
         */
    	RGB, 
    	/**
    	 * Each element contains all four components. OpenGL converts it to fixed-point or floating-point. 
    	 * Each component is then clamped to the range [0,1].
    	 */
    	RGBA, 
    	/**
    	 * Each element is a single depth component clamped to the range [0, 1].
    	 * Each component is then clamped to the range [0,1].
    	 */
    	Depth, 
    	/**
    	 * Each element is a luminance/alpha pair. OpenGL converts it to fixed-point or floating point, then assembles it into an RGBA element by replicating the luminance value three times for red, green, and blue.
    	 * Each component is then clamped to the range [0,1].
    	 */
    	Alpha, 
    	/**
    	 * Each element is a single luminance value. OpenGL converts it to fixed-point or floating-point, then assembles it into an RGBA element by replicating the luminance value three times for red, green, and blue and attaching 1 for alpha.
    	 * Each component is then clamped to the range [0,1].
    	 */
    	Luminance, 
    	/**
    	 * Each element is a luminance/alpha pair. OpenGL converts it to fixed-point or floating point, then assembles it into an RGBA element by replicating the luminance value three times for red, green, and blue.
    	 * Each component is then clamped to the range [0,1].
    	 */
    	LuminanceAlpha, 
    	/**
    	 * Each element has both luminance (grayness) and alpha (transparency) information, but the luminance and alpha values at every texel are the same.
    	 * Each component is then clamped to the range [0,1].
    	 */
    	Intensity,
    	Alpha4, Alpha8, Alpha12, Alpha16, 
    	Depth16, Depth24, Depth32,
    	Luminance4, Luminance8, Luminance12, Luminance16, 
		  Luminance4Alpha4,Luminance6Alpha2, Luminance8Alpha8,Luminance12Alpha4,
		  Luminance12Alpha12, Luminance16Alpha16, 
		  Intensity4, Intensity8, Intensity12, Intensity16, 
		  R3_G3_B2, RGB4, RGB5, RGB8, RGB10, RGB12, RGB16, 
		  RGBA2, RGBA4, RGB5_A1, RGBA8, RGB10_A2, RGBA12, RGBA16,
		  //floats
		  RGBA32F, RGB32F, Alpha32F, Intensity32F, Luminance32F, LuminanceAlpha32F,
		  RGBA16F, RGB16F, Alpha16F, Intensity16F, Luminance16F, LuminanceAlpha16F;
    	
    }
    
    /**
     * The shadowing texture compare mode
     */
    public enum DepthTextureCompareMode {
    	/** Perform no shadow based comparsion */
    	None,
    	/** Perform a comparison between source depth and texture depth */
    	RtoTexture,
    }

    /**
     * The shadowing texture compare function
     */
    public enum DepthTextureCompareFunc {
    	/** Outputs if the source depth is less than the texture depth */
    	LessThanEqual,
    	/** Outputs if the source depth is greater than the texture depth */
    	GreaterThanEqual
    }
    
    /**
     * The type of depth texture translation to output
     */
    public enum DepthTextureMode {
    	/** Output luminance values based on the depth comparison */
    	Luminance,
    	/** Output alpha values based on the depth comparison */
    	Alpha,
    	/** Output intensity values based on the depth comparison */
    	Intensity
    }
    
    

    // Optional String to point to where this texture is located
    private String imageLocation = null;

    // texture attributes.
    private Image image = null;
    private ColorRGBA blendColor = null; // If null, black (gl's default)
    // will be used
    private ColorRGBA borderColor = null; // If null, black (gl's default)
    // will be used

    private Vector3f translation = null;
    private Vector3f scale = null;
    private Quaternion rotation = null;
    private Matrix4f matrix = null;

    private float anisotropicFilterPercent = 0.0f;

    private transient int textureId;
    private ApplyMode apply = ApplyMode.Modulate;
    private MinificationFilter minificationFilter = MinificationFilter.NearestNeighborNoMipMaps;
    private MagnificationFilter magnificationFilter = MagnificationFilter.NearestNeighbor;
    private EnvironmentalMapMode envMapMode = EnvironmentalMapMode.None;
    private RenderToTextureType rttSource = RenderToTextureType.RGBA;
    
    private int memReq = 0;
    private boolean hasBorder = false;

    // The following will only used if apply is set to ApplyMode.Combine
    private CombinerFunctionRGB combineFuncRGB = CombinerFunctionRGB.Modulate;
    private CombinerSource combineSrc0RGB = CombinerSource.CurrentTexture;
    private CombinerSource combineSrc1RGB = CombinerSource.Previous;
    private CombinerSource combineSrc2RGB = CombinerSource.Constant;
    private CombinerOperandRGB combineOp0RGB = CombinerOperandRGB.SourceColor;
    private CombinerOperandRGB combineOp1RGB = CombinerOperandRGB.SourceColor;
    private CombinerOperandRGB combineOp2RGB = CombinerOperandRGB.SourceAlpha;
    private CombinerScale combineScaleRGB = CombinerScale.One;

    private CombinerFunctionAlpha combineFuncAlpha = CombinerFunctionAlpha.Modulate;
    private CombinerSource combineSrc0Alpha = CombinerSource.CurrentTexture;
    private CombinerSource combineSrc1Alpha = CombinerSource.Previous;
    private CombinerSource combineSrc2Alpha = CombinerSource.Constant;
    private CombinerOperandAlpha combineOp0Alpha = CombinerOperandAlpha.SourceAlpha;
    private CombinerOperandAlpha combineOp1Alpha = CombinerOperandAlpha.SourceAlpha;
    private CombinerOperandAlpha combineOp2Alpha = CombinerOperandAlpha.SourceAlpha;
    private CombinerScale combineScaleAlpha = CombinerScale.One;

    private TextureKey key = null;
    private transient boolean storeTexture = DEFAULT_STORE_TEXTURE;

    private DepthTextureCompareMode depthCompareMode = DepthTextureCompareMode.None;
    private DepthTextureCompareFunc depthCompareFunc = DepthTextureCompareFunc.GreaterThanEqual;
    private DepthTextureMode depthMode = DepthTextureMode.Intensity;
    
    /**
     * Constructor instantiates a new <code>Texture</code> object with default
     * attributes.
     */
    public Texture() {
        memReq = 0;
    }
    
    /**
     * <code>setBlendColor</code> sets a color that is used with
     * CombinerSource.Constant
     * 
     * @param color
     *            the new blend color - or null for the default (black)
     */
    public void setBlendColor(ColorRGBA color) {
        this.blendColor = color != null ? color.clone() : null;
    }

    /**
     * <code>setBorderColor</code> sets the color used when texture operations
     * encounter the border of a texture.
     * 
     * @param color
     *            the new border color - or null for the default (black)
     */
    public void setBorderColor(ColorRGBA color) {
        this.borderColor = color != null ? color.clone() : null;
    }

    /**
     * @return the MinificationFilterMode of this texture.
     */
    public MinificationFilter getMinificationFilter() {
        return minificationFilter;
    }

    /**
     * @param minificationFilter
     *            the new MinificationFilterMode for this texture.
     * @throws IllegalArgumentException
     *             if minificationFilter is null
     */
    public void setMinificationFilter(MinificationFilter minificationFilter) {
        if (minificationFilter == null) {
            throw new IllegalArgumentException(
                    "minificationFilter can not be null.");
        }
        this.minificationFilter = minificationFilter;
    }

    /**
     * @return the MagnificationFilterMode of this texture.
     */
    public MagnificationFilter getMagnificationFilter() {
        return magnificationFilter;
    }

    /**
     * @param magnificationFilter
     *            the new MagnificationFilter for this texture.
     * @throws IllegalArgumentException
     *             if magnificationFilter is null
     */
    public void setMagnificationFilter(MagnificationFilter magnificationFilter) {
        if (magnificationFilter == null) {
            throw new IllegalArgumentException(
                    "magnificationFilter can not be null.");
        }
        this.magnificationFilter = magnificationFilter;
    }

    /**
     * <code>setApply</code> sets the apply mode for this texture.
     * 
     * @param apply
     *            the apply mode for this texture.
     * @throws IllegalArgumentException
     *             if apply is null
     */
    public void setApply(ApplyMode apply) {
        if (apply == null) {
            throw new IllegalArgumentException("apply can not be null.");
        }
        this.apply = apply;
    }

    /**
     * <code>setImage</code> sets the image object that defines the texture.
     * 
     * @param image
     *            the image that defines the texture.
     */
    public void setImage(Image image) {
        this.image = image;
        updateMemoryReq();
    }

    /**
     * <code>getTextureId</code> returns the texture id of this texture. This
     * id is required to be unique to any other texture objects running in the
     * same JVM. However, no guarantees are made that it will be unique, and as
     * such, the user is responsible for this.
     * 
     * @return the id of the texture.
     */
    public int getTextureId() {
        return textureId;
    }

    /**
     * <code>setTextureId</code> sets the texture id for this texture. Zero
     * means no id is set.
     * 
     * @param textureId
     *            the texture id of this texture.
     */
    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    /**
     * <code>getImage</code> returns the image data that makes up this
     * texture. If no image data has been set, this will return null.
     * 
     * @return the image data that makes up the texture.
     */
    public Image getImage() {
        return image;
    }

    /**
     * <code>getApply</code> returns the apply mode for the texture.
     * 
     * @return the apply mode of the texture.
     */
    public ApplyMode getApply() {
        return apply;
    }

    /**
     * <code>getBlendColor</code> returns the color set to be used with
     * CombinerSource.Constant for this texture (as applicable) If null, black
     * is assumed.
     * 
     * @return the blend color.
     */
    public ColorRGBA getBlendColor() {
        return blendColor;
    }

    /**
     * <code>getBorderColor</code> returns the color to be used for border
     * operations. If null, black is assumed.
     * 
     * @return the border color.
     */
    public ColorRGBA getBorderColor() {
        return borderColor;
    }

    /**
     * <code>setWrap</code> sets the wrap mode of this texture for a
     * particular axis.
     * 
     * @param axis
     *            the texture axis to define a wrapmode on.
     * @param mode
     *            the wrap mode for the given axis of the texture.
     * @throws IllegalArgumentException
     *             if axis or mode are null or invalid for this type of texture
     */
    public abstract void setWrap(WrapAxis axis, WrapMode mode);

    /**
     * <code>setWrap</code> sets the wrap mode of this texture for all axis.
     * 
     * @param mode
     *            the wrap mode for the given axis of the texture.
     * @throws IllegalArgumentException
     *             if mode is null or invalid for this type of texture
     */
    public abstract void setWrap(WrapMode mode);

    /**
     * <code>getWrap</code> returns the wrap mode for a given coordinate axis
     * on this texture.
     * 
     * @param axis
     *            the axis to return for
     * @return the wrap mode of the texture.
     * @throws IllegalArgumentException
     *             if axis is null or invalid for this type of texture
     */
    public abstract WrapMode getWrap(WrapAxis axis);
    
    public abstract Type getType();
    
    /**
     * @return Returns the combineFuncRGB.
     */
    public CombinerFunctionRGB getCombineFuncRGB() {
        return combineFuncRGB;
    }

    /**
     * @param combineFuncRGB
     *            The combineFuncRGB to set.
     * @throws IllegalArgumentException
     *             if combineFuncRGB is null
     */
    public void setCombineFuncRGB(CombinerFunctionRGB combineFuncRGB) {
        if (combineFuncRGB == null) {
            throw new IllegalArgumentException("invalid CombinerFunctionRGB: null");
        }
        this.combineFuncRGB = combineFuncRGB;
    }

    /**
     * @return Returns the combineOp0Alpha.
     */
    public CombinerOperandAlpha getCombineOp0Alpha() {
        return combineOp0Alpha;
    }

    /**
     * @param combineOp0Alpha
     *            The combineOp0Alpha to set.
     * @throws IllegalArgumentException
     *             if combineOp0Alpha is null
     */
    public void setCombineOp0Alpha(CombinerOperandAlpha combineOp0Alpha) {
        if (combineOp0Alpha == null) {
            throw new IllegalArgumentException("invalid CombinerOperandAlpha: null");
        }

        this.combineOp0Alpha = combineOp0Alpha;
    }

    /**
     * @return Returns the combineOp0RGB.
     */
    public CombinerOperandRGB getCombineOp0RGB() {
        return combineOp0RGB;
    }

    /**
     * @param combineOp0RGB
     *            The combineOp0RGB to set.
     * @throws IllegalArgumentException
     *             if combineOp0RGB is null
     */
    public void setCombineOp0RGB(CombinerOperandRGB combineOp0RGB) {
        if (combineOp0RGB == null) {
            throw new IllegalArgumentException("invalid CombinerOperandRGB: null");
        }
        this.combineOp0RGB = combineOp0RGB;
    }

    /**
     * @return Returns the combineOp1Alpha.
     */
    public CombinerOperandAlpha getCombineOp1Alpha() {
        return combineOp1Alpha;
    }

    /**
     * @param combineOp1Alpha
     *            The combineOp1Alpha to set.
     * @throws IllegalArgumentException
     *             if combineOp1Alpha is null
     */
    public void setCombineOp1Alpha(CombinerOperandAlpha combineOp1Alpha) {
        if (combineOp1Alpha == null) {
            throw new IllegalArgumentException("invalid CombinerOperandAlpha: null");
        }
        this.combineOp1Alpha = combineOp1Alpha;
    }

    /**
     * @return Returns the combineOp1RGB.
     */
    public CombinerOperandRGB getCombineOp1RGB() {
        return combineOp1RGB;
    }

    /**
     * @param combineOp1RGB
     *            The combineOp1RGB to set.
     * @throws IllegalArgumentException
     *             if combineOp1RGB is null
     */
    public void setCombineOp1RGB(CombinerOperandRGB combineOp1RGB) {
        if (combineOp1RGB == null) {
            throw new IllegalArgumentException("invalid CombinerOperandRGB: null");
        }
        this.combineOp1RGB = combineOp1RGB;
    }

    /**
     * @return Returns the combineOp2Alpha.
     */
    public CombinerOperandAlpha getCombineOp2Alpha() {
        return combineOp2Alpha;
    }

    /**
     * @param combineOp2Alpha
     *            The combineOp2Alpha to set.
     * @throws IllegalArgumentException
     *             if combineOp2Alpha is null
     */
    public void setCombineOp2Alpha(CombinerOperandAlpha combineOp2Alpha) {
        if (combineOp2Alpha == null) {
            throw new IllegalArgumentException("invalid CombinerOperandAlpha: null");
        }
        this.combineOp2Alpha = combineOp2Alpha;
    }

    /**
     * @return Returns the combineOp2RGB.
     */
    public CombinerOperandRGB getCombineOp2RGB() {
        return combineOp2RGB;
    }

    /**
     * @param combineOp2RGB
     *            The combineOp2RGB to set.
     * @throws IllegalArgumentException
     *             if combineOp2RGB is null
     */
    public void setCombineOp2RGB(CombinerOperandRGB combineOp2RGB) {
        if (combineOp2RGB == null) {
            throw new IllegalArgumentException("invalid CombinerOperandRGB: null");
        }
        this.combineOp2RGB = combineOp2RGB;
    }

    /**
     * @return Returns the combineScaleAlpha.
     */
    public CombinerScale getCombineScaleAlpha() {
        return combineScaleAlpha;
    }

    /**
     * @param combineScaleAlpha
     *            The combineScaleAlpha to set.
     * @throws IllegalArgumentException
     *             if combineScaleAlpha is null
     */
    public void setCombineScaleAlpha(CombinerScale combineScaleAlpha) {
        if (combineScaleAlpha == null) {
            throw new IllegalArgumentException("invalid CombinerScale: null");
        }
        this.combineScaleAlpha = combineScaleAlpha;
    }

    /**
     * @return Returns the combineScaleRGB.
     */
    public CombinerScale getCombineScaleRGB() {
        return combineScaleRGB;
    }

    /**
     * @param combineScaleRGB
     *            The combineScaleRGB to set.
     * @throws IllegalArgumentException
     *             if combineScaleRGB is null
     */
    public void setCombineScaleRGB(CombinerScale combineScaleRGB) {
        if (combineScaleRGB == null) {
            throw new IllegalArgumentException("invalid CombinerScale: null");
        }
        this.combineScaleRGB = combineScaleRGB;
    }

    /**
     * @return Returns the combineSrc0Alpha.
     */
    public CombinerSource getCombineSrc0Alpha() {
        return combineSrc0Alpha;
    }

    /**
     * @param combineSrc0Alpha
     *            The combineSrc0Alpha to set.
     * @throws IllegalArgumentException
     *             if combineSrc0Alpha is null
     */
    public void setCombineSrc0Alpha(CombinerSource combineSrc0Alpha) {
        if (combineSrc0Alpha == null) {
            throw new IllegalArgumentException("invalid CombinerSource: null");
        }
        this.combineSrc0Alpha = combineSrc0Alpha;
    }

    /**
     * @return Returns the combineSrc0RGB.
     */
    public CombinerSource getCombineSrc0RGB() {
        return combineSrc0RGB;
    }

    /**
     * @param combineSrc0RGB
     *            The combineSrc0RGB to set.
     * @throws IllegalArgumentException
     *             if combineSrc0RGB is null
     */
    public void setCombineSrc0RGB(CombinerSource combineSrc0RGB) {
        if (combineSrc0RGB == null) {
            throw new IllegalArgumentException("invalid CombinerSource: null");
        }
        this.combineSrc0RGB = combineSrc0RGB;
    }

    /**
     * @return Returns the combineSrc1Alpha.
     */
    public CombinerSource getCombineSrc1Alpha() {
        return combineSrc1Alpha;
    }

    /**
     * @param combineSrc1Alpha
     *            The combineSrc1Alpha to set.
     * @throws IllegalArgumentException
     *             if combineSrc1Alpha is null
     */
    public void setCombineSrc1Alpha(CombinerSource combineSrc1Alpha) {
        if (combineSrc1Alpha == null) {
            throw new IllegalArgumentException("invalid CombinerSource: null");
        }
        this.combineSrc1Alpha = combineSrc1Alpha;
    }

    /**
     * @return Returns the combineSrc1RGB.
     */
    public CombinerSource getCombineSrc1RGB() {
        return combineSrc1RGB;
    }

    /**
     * @param combineSrc1RGB
     *            The combineSrc1RGB to set.
     * @throws IllegalArgumentException
     *             if combineSrc1RGB is null
     */
    public void setCombineSrc1RGB(CombinerSource combineSrc1RGB) {
        if (combineSrc1RGB == null) {
            throw new IllegalArgumentException("invalid CombinerSource: null");
        }
        this.combineSrc1RGB = combineSrc1RGB;
    }

    /**
     * @return Returns the combineSrc2Alpha.
     */
    public CombinerSource getCombineSrc2Alpha() {
        return combineSrc2Alpha;
    }

    /**
     * @param combineSrc2Alpha
     *            The combineSrc2Alpha to set.
     * @throws IllegalArgumentException
     *             if combineSrc2Alpha is null
     */
    public void setCombineSrc2Alpha(CombinerSource combineSrc2Alpha) {
        if (combineSrc2Alpha == null) {
            throw new IllegalArgumentException("invalid CombinerSource: null");
        }
        this.combineSrc2Alpha = combineSrc2Alpha;
    }

    /**
     * @return Returns the combineSrc2RGB.
     */
    public CombinerSource getCombineSrc2RGB() {
        return combineSrc2RGB;
    }

    /**
     * @param combineSrc2RGB
     *            The combineSrc2RGB to set.
     * @throws IllegalArgumentException
     *             if combineSrc2RGB is null
     */
    public void setCombineSrc2RGB(CombinerSource combineSrc2RGB) {
        if (combineSrc2RGB == null) {
            throw new IllegalArgumentException("invalid CombinerSource: null");
        }
        this.combineSrc2RGB = combineSrc2RGB;
    }

    /**
     * @return Returns the combineFuncAlpha.
     */
    public CombinerFunctionAlpha getCombineFuncAlpha() {
        return combineFuncAlpha;
    }

    /**
     * @param combineFuncAlpha
     *            The combineFuncAlpha to set.
     * @throws IllegalArgumentException
     *             if combineFuncAlpha is null
     */
    public void setCombineFuncAlpha(CombinerFunctionAlpha combineFuncAlpha) {
        if (combineFuncAlpha == null) {
            throw new IllegalArgumentException("invalid CombinerFunctionAlpha: null");
        }
        this.combineFuncAlpha = combineFuncAlpha;
    }

    /**
     * @param envMapMode
     * @throws IllegalArgumentException
     *             if envMapMode is null
     */
    public void setEnvironmentalMapMode(EnvironmentalMapMode envMapMode) {
        if (envMapMode == null) {
            throw new IllegalArgumentException("invalid EnvironmentalMapMode: null");
        }
        this.envMapMode = envMapMode;
    }

    public EnvironmentalMapMode getEnvironmentalMapMode() {
        return envMapMode;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    /**
     * @return the anisotropic filtering level for this texture as a percentage
     *         (0.0 - 1.0)
     */
    public float getAnisotropicFilterPercent() {
        return anisotropicFilterPercent;
    }

    /**
     * @param percent
     *            the anisotropic filtering level for this texture as a
     *            percentage (0.0 - 1.0)
     */
    public void setAnisotropicFilterPercent(float percent) {
        if (percent > 1.0f)
            percent = 1.0f;
        else if (percent < 0.0f)
            percent = 0.0f;
        this.anisotropicFilterPercent = percent;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Texture)) {
            return false;
        }
        
        Texture that = (Texture) other;
        if (this.textureId != that.textureId)
            return false;
        if (this.textureId == 0) {
            if (this.key != null && !this.key.equals(that.key))
                return false;
            if (this.getImage() != null
                    && !this.getImage().equals(that.getImage()))
                return false;
            if (this.getImage() == null && that.getImage() != null)
                return false;
            if (this.getAnisotropicFilterPercent() != that
                    .getAnisotropicFilterPercent())
                return false;
            if (this.getApply() != that.getApply())
                return false;
            if (this.getCombineFuncAlpha() != that.getCombineFuncAlpha())
                return false;
            if (this.getCombineFuncRGB() != that.getCombineFuncRGB())
                return false;
            if (this.getCombineOp0Alpha() != that.getCombineOp0Alpha())
                return false;
            if (this.getCombineOp1RGB() != that.getCombineOp1RGB())
                return false;
            if (this.getCombineOp2Alpha() != that.getCombineOp2Alpha())
                return false;
            if (this.getCombineOp2RGB() != that.getCombineOp2RGB())
                return false;
            if (this.getCombineScaleAlpha() != that.getCombineScaleAlpha())
                return false;
            if (this.getCombineScaleRGB() != that.getCombineScaleRGB())
                return false;
            if (this.getCombineSrc0Alpha() != that.getCombineSrc0Alpha())
                return false;
            if (this.getCombineSrc0RGB() != that.getCombineSrc0RGB())
                return false;
            if (this.getCombineSrc1Alpha() != that.getCombineSrc1Alpha())
                return false;
            if (this.getCombineSrc1RGB() != that.getCombineSrc1RGB())
                return false;
            if (this.getCombineSrc2Alpha() != that.getCombineSrc2Alpha())
                return false;
            if (this.getCombineSrc2RGB() != that.getCombineSrc2RGB())
                return false;
            if (this.getEnvironmentalMapMode() != that
                    .getEnvironmentalMapMode())
                return false;
            if (this.getMagnificationFilter() != that.getMagnificationFilter())
                return false;
            if (this.getMinificationFilter() != that.getMinificationFilter())
                return false;
            if (this.getBlendColor() != null
                    && !this.getBlendColor().equals(that.getBlendColor()))
                return false;
            if (this.getBlendColor() == null && that.getBlendColor() != null)
                return false;
        }
        return true;
    }

    public abstract Texture createSimpleClone();

    /**
     * Retreive a basic clone of this Texture (ie, clone everything but the
     * image data, which is shared)
     * 
     * @return Texture
     */
    public Texture createSimpleClone(Texture rVal) {
        rVal.setApply(apply);
        rVal.setCombineFuncAlpha(combineFuncAlpha);
        rVal.setCombineFuncRGB(combineFuncRGB);
        rVal.setCombineOp0Alpha(combineOp0Alpha);
        rVal.setCombineOp0RGB(combineOp0RGB);
        rVal.setCombineOp1Alpha(combineOp1Alpha);
        rVal.setCombineOp1RGB(combineOp1RGB);
        rVal.setCombineOp2Alpha(combineOp2Alpha);
        rVal.setCombineOp2RGB(combineOp2RGB);
        rVal.setCombineScaleAlpha(combineScaleAlpha);
        rVal.setCombineScaleRGB(combineScaleRGB);
        rVal.setCombineSrc0Alpha(combineSrc0Alpha);
        rVal.setCombineSrc0RGB(combineSrc0RGB);
        rVal.setCombineSrc1Alpha(combineSrc1Alpha);
        rVal.setCombineSrc1RGB(combineSrc1RGB);
        rVal.setCombineSrc2Alpha(combineSrc2Alpha);
        rVal.setCombineSrc2RGB(combineSrc2RGB);
        rVal.setEnvironmentalMapMode(envMapMode);
        rVal.setMinificationFilter(minificationFilter);
        rVal.setMagnificationFilter(magnificationFilter);
        rVal.setHasBorder(hasBorder);
        rVal.setAnisotropicFilterPercent(anisotropicFilterPercent);
        rVal.setImage(image); // NOT CLONED.
        rVal.memReq = memReq;
        rVal.setImageLocation(imageLocation);
        rVal.setTextureId(textureId);
        rVal.setBlendColor(blendColor != null ? blendColor.clone() : null);
        if (scale != null)
            rVal.setScale(scale);
        if (translation != null)
            rVal.setTranslation(translation);
        if (rotation != null)
            rVal.setRotation(rotation);
        if (matrix != null)
            rVal.setMatrix(matrix);
        if (getTextureKey() != null) {
            rVal.setTextureKey(getTextureKey());
        }
        return rVal;
    }

    /**
     * @return Returns the rotation.
     */
    public Quaternion getRotation() {
        return rotation;
    }

    /**
     * @param rotation
     *            The rotation to set.
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the texture matrix set on this texture or null if none is set.
     */
    public Matrix4f getMatrix() {
        return matrix;
    }

    /**
     * @param matrix
     *            The matrix to set on this Texture. If null, rotation, scale
     *            and/or translation will be used.
     */
    public void setMatrix(Matrix4f matrix) {
        this.matrix = matrix;
    }

    /**
     * @return Returns the scale.
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * @param scale
     *            The scale to set.
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    /**
     * @return Returns the translation.
     */
    public Vector3f getTranslation() {
        return translation;
    }

    /**
     * @param translation
     *            The translation to set.
     */
    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    /**
     * @return Returns the rttSource.
     */
    public RenderToTextureType getRTTSource() {
        return rttSource;
    }

    /**
     * @param rttSource
     *            The rttSource to set.
     * @throws IllegalArgumentException
     *             if rttSource is null
     */
    public void setRenderToTextureType(RenderToTextureType rttSource) {
        if (rttSource == null) {
            throw new IllegalArgumentException("invalid RenderToTextureType: null");
        }
        this.rttSource = rttSource;
    }

    /**
     * @return the estimated footprint of this texture in bytes
     */
    public int getMemoryReq() {
        return memReq;
    }

    public void updateMemoryReq() {
        if (image != null) {
            int width = image.getWidth(), height = image.getHeight();
            memReq = width * height;
            int bpp = Image.getEstimatedByteSize(image.getFormat());
            memReq *= bpp;
            if (this.getMinificationFilter().usesMipMapLevels()
                    || image.hasMipmaps()) {
                if (FastMath.isPowerOfTwo(image.getWidth())
                        && FastMath.isPowerOfTwo(image.getHeight()))
                    memReq *= 1.33333f;
                else
                    memReq *= 2.0f; // XXX: Is this right?
            }
        }
    }

    public void write(JMEExporter e) throws IOException {

        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(imageLocation, "imageLocation", null);
        if (storeTexture) {
            capsule.write(image, "image", null);
        }
        capsule.write(blendColor, "blendColor", null);
        capsule.write(borderColor, "borderColor", null);
        capsule.write(translation, "translation", null);
        capsule.write(scale, "scale", null);
        capsule.write(rotation, "rotation", null);
        capsule.write(matrix, "matrix", null);
        capsule.write(hasBorder, "hasBorder", false);
        capsule.write(anisotropicFilterPercent, "anisotropicFilterPercent",
                0.0f);
        capsule.write(minificationFilter, "minificationFilter",
                MinificationFilter.NearestNeighborNoMipMaps);
        capsule.write(magnificationFilter, "magnificationFilter",
                MagnificationFilter.NearestNeighbor);
        capsule.write(apply, "apply", ApplyMode.Modulate);
        capsule.write(envMapMode, "envMapMode", EnvironmentalMapMode.None);
        capsule.write(rttSource, "rttSource", RenderToTextureType.RGBA);
        capsule.write(memReq, "memReq", 0);
        capsule.write(combineFuncRGB, "combineFuncRGB",
                CombinerFunctionRGB.Replace);
        capsule.write(combineFuncAlpha, "combineFuncAlpha",
                CombinerFunctionAlpha.Replace);
        capsule.write(combineSrc0RGB, "combineSrc0RGB",
                CombinerSource.CurrentTexture);
        capsule
                .write(combineSrc1RGB, "combineSrc1RGB",
                        CombinerSource.Previous);
        capsule
                .write(combineSrc2RGB, "combineSrc2RGB",
                        CombinerSource.Constant);
        capsule.write(combineSrc0Alpha, "combineSrc0Alpha",
                CombinerSource.CurrentTexture);
        capsule.write(combineSrc1Alpha, "combineSrc1Alpha",
                CombinerSource.Previous);
        capsule.write(combineSrc2Alpha, "combineSrc2Alpha",
                CombinerSource.Constant);
        capsule.write(combineOp0RGB, "combineOp0RGB",
                CombinerOperandRGB.SourceColor);
        capsule.write(combineOp1RGB, "combineOp1RGB",
                CombinerOperandRGB.SourceColor);
        capsule.write(combineOp2RGB, "combineOp2RGB",
                CombinerOperandRGB.SourceAlpha);
        capsule.write(combineOp0Alpha, "combineOp0Alpha",
                CombinerOperandAlpha.SourceAlpha);
        capsule.write(combineOp1Alpha, "combineOp1Alpha",
                CombinerOperandAlpha.SourceAlpha);
        capsule.write(combineOp2Alpha, "combineOp2Alpha",
                CombinerOperandAlpha.SourceAlpha);
        capsule.write(combineScaleRGB, "combineScaleRGB", CombinerScale.One);
        capsule
                .write(combineScaleAlpha, "combineScaleAlpha",
                        CombinerScale.One);
        if (!storeTexture) {
            capsule.write(key, "textureKey", null);
        }
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        imageLocation = capsule.readString("imageLocation", null);
        image = (Image) capsule.readSavable("image", null);
        if (image == null) {
            key = (TextureKey) capsule.readSavable("textureKey", null);
            if (key != null && key.getLocation() != null) {
                TextureManager.loadTexture(this, key);
            }
        }
        blendColor = (ColorRGBA) capsule.readSavable("blendColor", null);
        borderColor = (ColorRGBA) capsule.readSavable("borderColor", null);
        translation = (Vector3f) capsule.readSavable("translation", null);
        scale = (Vector3f) capsule.readSavable("scale", null);
        rotation = (Quaternion) capsule.readSavable("rotation", null);
        matrix = (Matrix4f) capsule.readSavable("matrix", null);
        hasBorder = capsule.readBoolean("hasBorder", false);
        anisotropicFilterPercent = capsule.readFloat(
                "anisotropicFilterPercent", 0.0f);
        minificationFilter = capsule.readEnum("minificationFilter",
                MinificationFilter.class,
                MinificationFilter.NearestNeighborNoMipMaps);
        magnificationFilter = capsule.readEnum("magnificationFilter",
                MagnificationFilter.class, MagnificationFilter.NearestNeighbor);
        apply = capsule.readEnum("apply", ApplyMode.class, ApplyMode.Modulate);
        envMapMode = capsule.readEnum("envMapMode", EnvironmentalMapMode.class,
                EnvironmentalMapMode.None);
        rttSource = capsule.readEnum("rttSource", RenderToTextureType.class,
                RenderToTextureType.RGBA);
        memReq = capsule.readInt("memReq", 0);
        combineFuncRGB = capsule.readEnum("combineFuncRGB",
                CombinerFunctionRGB.class, CombinerFunctionRGB.Replace);
        combineFuncAlpha = capsule.readEnum("combineFuncAlpha",
                CombinerFunctionAlpha.class, CombinerFunctionAlpha.Replace);
        combineSrc0RGB = capsule.readEnum("combineSrc0RGB",
                CombinerSource.class, CombinerSource.CurrentTexture);
        combineSrc1RGB = capsule.readEnum("combineSrc1RGB",
                CombinerSource.class, CombinerSource.Previous);
        combineSrc2RGB = capsule.readEnum("combineSrc2RGB",
                CombinerSource.class, CombinerSource.Constant);
        combineSrc0Alpha = capsule.readEnum("combineSrc0Alpha",
                CombinerSource.class, CombinerSource.CurrentTexture);
        combineSrc1Alpha = capsule.readEnum("combineSrc1Alpha",
                CombinerSource.class, CombinerSource.Previous);
        combineSrc2Alpha = capsule.readEnum("combineSrc2Alpha",
                CombinerSource.class, CombinerSource.Constant);
        combineOp0RGB = capsule.readEnum("combineOp0RGB",
                CombinerOperandRGB.class, CombinerOperandRGB.SourceColor);
        combineOp1RGB = capsule.readEnum("combineOp1RGB",
                CombinerOperandRGB.class, CombinerOperandRGB.SourceColor);
        combineOp2RGB = capsule.readEnum("combineOp2RGB",
                CombinerOperandRGB.class, CombinerOperandRGB.SourceAlpha);
        combineOp0Alpha = capsule.readEnum("combineOp0Alpha",
                CombinerOperandAlpha.class, CombinerOperandAlpha.SourceAlpha);
        combineOp1Alpha = capsule.readEnum("combineOp1Alpha",
                CombinerOperandAlpha.class, CombinerOperandAlpha.SourceAlpha);
        combineOp2Alpha = capsule.readEnum("combineOp2Alpha",
                CombinerOperandAlpha.class, CombinerOperandAlpha.SourceAlpha);
        combineScaleRGB = capsule.readEnum("combineScaleRGB",
                CombinerScale.class, CombinerScale.One);
        combineScaleAlpha = capsule.readEnum("combineScaleAlpha",
                CombinerScale.class, CombinerScale.One);
    }

    public Class<? extends Texture> getClassTag() {
        return this.getClass();
    }

    public void setTextureKey(TextureKey tkey) {
        this.key = tkey;
    }

    public TextureKey getTextureKey() {
        return key;
    }

    public boolean isStoreTexture() {
        return storeTexture;
    }

    public void setStoreTexture(boolean storeTexture) {
        this.storeTexture = storeTexture;
    }

    public boolean hasBorder() {
        return hasBorder;
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    /**
     * Get the depth texture compare function 
     * 
     * @return The depth texture compare function
     */
	public DepthTextureCompareFunc getDepthCompareFunc() {
		return depthCompareFunc;
	}

    /**
     * Set the depth texture compare function 
     * 
     * param depthCompareFunc The depth texture compare function
     */
	public void setDepthCompareFunc(DepthTextureCompareFunc depthCompareFunc) {
		this.depthCompareFunc = depthCompareFunc;
	}

	/**
	 * Get the depth texture apply mode
	 * 
	 * @return The depth texture apply mode
	 */
	public DepthTextureMode getDepthMode() {
		return depthMode;
	}

	/**
	 * Set the depth texture apply mode
	 * 
	 * param depthMode The depth texture apply mode
	 */
	public void setDepthMode(DepthTextureMode depthMode) {
		this.depthMode = depthMode;
	}

	/**
	 * Get the depth texture compare mode
	 * 
	 * @return The depth texture compare mode
	 */
	public DepthTextureCompareMode getDepthCompareMode() {
		return depthCompareMode;
	}

	/**
	 * Set the depth texture compare mode
	 * 
	 * @param depthCompareMode The depth texture compare mode
	 */
	public void setDepthCompareMode(DepthTextureCompareMode depthCompareMode) {
		this.depthCompareMode = depthCompareMode;
	}
}

