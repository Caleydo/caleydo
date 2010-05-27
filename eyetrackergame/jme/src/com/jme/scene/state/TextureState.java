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

package com.jme.scene.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>TextureState</code> maintains a texture state for a given node and
 * it's children. The number of states that a TextureState can maintain at one
 * time is equal to the number of texture units available on the GPU. It is not
 * within the scope of this class to generate the texture, and is recommended
 * that <code>TextureManager</code> be used to create the Texture objects.
 * 
 * @see com.jme.util.TextureManager
 * @author Mark Powell
 * @author Joshua Slack
 * @author Tijl Houtbeckers - TextureID cache / Shader texture units
 * @author Vekas Arpad - Shader Texture units
 * @version $Id: TextureState.java 4752 2009-11-09 19:08:29Z blaine.dev $
 */
public abstract class TextureState extends RenderState {
    private static final Logger logger = Logger.getLogger(TextureState.class
            .getName());

    protected static Texture defaultTexture = null;

    public enum CorrectionType {
        /**
         * Correction modifier makes no color corrections, and is the fastest.
         */
        Affine,

        /**
         * Correction modifier makes color corrections based on perspective and
         * is slower than CM_AFFINE. (Default)
         */
        Perspective;
    }
    
    /** The texture(s). */
    protected transient ArrayList<Texture> texture;

    /** The total number of supported texture units. */
    protected static int numTotalTexUnits = -1;

    /** The number of texture units availible for fixed functionality */
    protected static int numFixedTexUnits = -1;

    /** The number of texture units availible to vertex shader */
    protected static int numVertexTexUnits = -1;

    /** The number of texture units availible to fragment shader */
    protected static int numFragmentTexUnits = -1;

    /** The number of texture coordinate sets available */
    protected static int numFragmentTexCoordUnits = -1;

    protected static float maxAnisotropic = -1.0f;

    /** True if multitexturing is supported. */
    protected static boolean supportsMultiTexture = false;
    protected static boolean supportsMultiTextureDetected = false;

    /** True if combine dot3 is supported. */
    protected static boolean supportsEnvDot3 = false;
    protected static boolean supportsEnvDot3Detected = false;

    /** True if combine dot3 is supported. */
    protected static boolean supportsEnvCombine = false;
    protected static boolean supportsEnvCombineDetected = false;

    /** True if anisofiltering is supported. */
    protected static boolean supportsAniso = false;
    protected static boolean supportsAnisoDetected = false;

    /** True if non pow 2 texture sizes are supported. */
    protected static boolean supportsNonPowerTwo = false;
    protected static boolean supportsNonPowerTwoDetected = false;

    /** True if rectangular textures are supported (vs. only square textures) */
    protected static boolean supportsRectangular = false;
    protected static boolean supportsRectangularDetected = false;

    /** True if S3TC compression is supported. */
    protected static boolean supportsS3TCCompression = false;
    protected static boolean supportsS3TCCompressionDetected = false;

    /** True if Texture3D is supported. */
    protected static boolean supportsTexture3D = false;
    protected static boolean supportsTexture3DDetected = false;

    /** True if TextureCubeMap is supported. */
    protected static boolean supportsTextureCubeMap = false;
    protected static boolean supportsTextureCubeMapDetected = false;

    /** True if non-GLU mipmap generation (part of FBO) is supported. */
    protected static boolean automaticMipMaps = false;
    protected static boolean automaticMipMapsDetected = false;

    /** True if depth textures are supported */
    protected static boolean supportsDepthTexture = false;
    /** True if shadow mapping supported */
    protected static boolean supportsShadow = false;
    
    protected transient int firstTexture = 0;
    protected transient int lastTexture = 0;

    /**
     * Perspective correction to use for the object rendered with this texture
     * state. Default is CorrectionType.Perspective.
     */
    private CorrectionType correctionType = CorrectionType.Perspective;

    /**
     * offset is used to denote where to begin access of texture coordinates. 0
     * default
     */
    protected int offset = 0;

    protected transient int[] idCache = new int[0];

    /**
     * Constructor instantiates a new <code>TextureState</code> object.
     *
     * If this method fails to set up a default texture, it will not throw but
     * will just log that fact, creating the TextureState instance without a
     * default texture set.
     */
    public TextureState() {
        if (defaultTexture == null)
            try {
                URL dfltUrl = TextureState.class.getResource("notloaded.png");
                if (dfltUrl == null) {
                    logger.warning("Setting no default texture, since default "
                            + "texture image 'notloaded.png' is not available");
                    return;
                }
                defaultTexture = TextureManager.loadTexture(dfltUrl,
                        Texture.MinificationFilter.Trilinear,
                        Texture.MagnificationFilter.Bilinear, 0.0f, true);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to load default texture: notloaded.png", e);
            }
    }

    /**
     * <code>getType</code> returns this type of render state. (RS_TEXTURE).
     * 
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_TEXTURE;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#Texture}
     * 
     * @return {@link RenderState.StateType#Texture}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.Texture;
    }

    /**
     * <code>setTexture</code> sets a single texture to the first texture
     * unit.
     * 
     * @param texture
     *            the texture to set.
     */
    public void setTexture(Texture texture) {
        if (this.texture.size() == 0) {
            this.texture.add(texture);
        } else {
            this.texture.set(0, texture);
        }
        setNeedsRefresh(true);

        resetFirstLast();
    }

    /**
     * <code>getTexture</code> gets the texture that is assigned to the first
     * texture unit.
     * 
     * @return the texture in the first texture unit.
     */
    public Texture getTexture() {
        if (texture.size() > 0)
            return texture.get(0);
        else return null;
    }

    /**
     * <code>setTexture</code> sets the texture object to be used by the
     * state. The texture unit that this texture uses is set, if the unit is not
     * valid, i.e. less than zero or greater than the number of texture units
     * supported by the graphics card, it is ignored.
     * 
     * @param texture
     *            the texture to be used by the state.
     * @param textureUnit
     *            the texture unit this texture will fill.
     */
    public void setTexture(Texture texture, int textureUnit) {
        if (textureUnit < 0 || textureUnit >= numTotalTexUnits)
            throw new IllegalStateException(
                    "Attempted to setTexture for textureunit "
                    + textureUnit + " < " + numTotalTexUnits);
        while (textureUnit >= this.texture.size()) this.texture.add(null);
        this.texture.set(textureUnit, texture);
        resetFirstLast();
        setNeedsRefresh(true);
    }

    /**
     * <code>getTexture</code> retrieves the texture being used by the state
     * in a particular texture unit.
     * 
     * @param textureUnit
     *            the texture unit to retrieve the texture from.
     * @return the texture being used by the state. If the texture unit is
     *         invalid, null is returned.
     */
    public Texture getTexture(int textureUnit) {
        if (textureUnit < texture.size() && textureUnit >= 0) {
            return texture.get(textureUnit);
        }

        return null;
    }

    public boolean removeTexture(Texture tex) {

        int index = texture.indexOf(tex);
        if (index == -1)
            return false;

        texture.set(index, null);
        idCache[index] = 0;
        resetFirstLast();
        setNeedsRefresh(true);
        return true;
    }

    public boolean removeTexture(int textureUnit) {
        if (textureUnit < 0 || textureUnit >= numTotalTexUnits
                || textureUnit >= texture.size())
            return false;

        Texture t = texture.get(textureUnit);
        if (t == null)
            return false;

        texture.set(textureUnit, null);
        idCache[textureUnit] = 0;
        resetFirstLast();
        setNeedsRefresh(true);
        return true;

    }

    /**
     * Removes all textures in this texture state. Does not delete them from the
     * graphics card.
     */
    public void clearTextures() {
        for (int i = texture.size(); --i >= 0;) {
            removeTexture(i);
        }
    }

    /**
     * <code>setCorrectionType</code> sets the image correction type for this
     * texture state.
     * 
     * @param type
     *            the correction type for this texture.
     * @throws IllegalArgumentException
     *             if type is null
     */
    public void setCorrectionType(CorrectionType type) {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null.");
        }
        this.correctionType = type;
        setNeedsRefresh(true);
    }

    /**
     * <code>getCorrectionType</code> returns the correction mode for the texture state.
     * 
     * @return the correction type for the texture state.
     */
    public CorrectionType getCorrectionType() {
        return correctionType;
    }

    /**
     * <code>getTotalNumberOfUnits</code> returns the total number of texture
     * units the computer's graphics card supports.
     * 
     * @return the total number of texture units supported by the graphics card.
     */
    public static int getTotalNumberOfUnits() {
        return numTotalTexUnits;
    }

    /**
     * <code>getNumberOfFixedUnits</code> returns the number of texture units
     * the computer's graphics card supports, for use in the fixed pipeline.
     * 
     * @return the number units.
     */
    public static int getNumberOfFixedUnits() {
        return numFixedTexUnits;
    }

    /**
     * <code>getNumberOfVertexUnits</code> returns the number of texture units
     * available to a vertex shader that this graphics card supports.
     * 
     * @return the number of units.
     */
    public static int getNumberOfVertexUnits() {
        return numVertexTexUnits;
    }

    /**
     * <code>getNumberOfFragmentUnits</code> returns the number of texture units
     * available to a fragment shader that this graphics card supports.
     * 
     * @return the number of units.
     */
    public static int getNumberOfFragmentUnits() {
        return numFragmentTexUnits;
    }

    /**
     * <code>getNumberOfFragmentTexCoordUnits</code> returns the number of
     * texture coordinate sets available that this graphics card supports.
     * 
     * @return the number of units.
     */
    public static int getNumberOfFragmentTexCoordUnits() {
        return numFragmentTexCoordUnits;
    }

    /**
     * <code>getNumberOfTotalUnits</code> returns the number texture units the
     * computer's graphics card supports.
     * 
     * @return the number of units.
     */
    public static int getNumberOfTotalUnits() {
        return numTotalTexUnits;
    }

    /**
     * Returns the number of textures this texture manager is maintaining.
     * 
     * @return the number of textures.
     */
    public int getNumberOfSetTextures() {
        return texture.size();
    }

    /**
     * Fast access for retrieving a Texture ID. A return is guaranteed when
     * <code>textureUnit</code> is any number under or equal to the highest
     * textureunit currently in use. This value can be retrieved with
     * <code>getNumberOfSetTextures</code>. A higher value might result in
     * unexpected behaviour such as an exception being thrown.
     * 
     * @param textureUnit
     *            The texture unit from which to retrieve the ID.
     * @return the textureID, or 0 if there is none.
     */
    public final int getTextureID(int textureUnit) {
        if (textureUnit < idCache.length && textureUnit >= 0) {
            return idCache[textureUnit];
        }

        return 0;
    }

    /**
     * <code>setTextureCoordinateOffset</code> sets the offset value used to
     * determine which coordinates to use for texturing Geometry.
     * 
     * @param offset
     *            the offset (default 0).
     */
    public void setTextureCoordinateOffset(int offset) {
        this.offset = offset;
        setNeedsRefresh(true);
    }

    /**
     * <code>setTextureCoordinateOffset</code> gets the offset value used to
     * determine which coordinates to use for texturing Geometry.
     * 
     * @return the offset (default 0).
     */
    public int getTextureCoordinateOffset() {
        return this.offset;
    }

    /**
     * Loads our textures into the underlying rendering system, generating mip
     * maps if appropriate.
     */
    public void load() {
        for (int unit = 0; unit < numTotalTexUnits; unit++) {
            if (getTexture(unit) != null) {
                load(unit);
            }
        }
    }

    /**
     * Loads the texture for the given unit into the underlying rendering
     * system, generating mip maps if appropriate.
     */
    public abstract void load(int unit);

    /**
     * Removes the texture of the given unit.
     * 
     * @param unit
     *            The unit of the Texture to remove.
     */
    public abstract void delete(int unit);

    /**
     * Removes all Texture set in this TextureState. Does not also remove from
     * TextureManager's cache.
     */
    public abstract void deleteAll();

    /**
     * Removes all Texture set in this TextureState. Also removes the textures
     * from the TextureManager cache if passed boolean is true.
     */
    public abstract void deleteAll(boolean removeFromCache);

    /**
     * Returns the maximum anisotropic filter.
     * 
     * @return The maximum anisotropic filter.
     */
    public float getMaxAnisotropic() {
        return maxAnisotropic;
    }

    /**
     * Updates firstTexture to be the first non-null Texture, and lastTexture to
     * be the last non-null texture.
     */
    protected void resetFirstLast() {
        boolean foundFirst = false;
        for (int x = 0; x < texture.size(); x++) {
            if (texture.get(x) != null) {
                if (!foundFirst) {
                    firstTexture = x;
                    foundFirst = true;
                }
                lastTexture = x;
            }
        }
        if (idCache == null || idCache.length <= lastTexture) {
            if (idCache == null || idCache.length == 0) {
                idCache = new int[lastTexture + 2];
            } else {
                int[] tempCache = new int[lastTexture + 2];
                System.arraycopy(idCache, 0, tempCache, 0, idCache.length);
                idCache = tempCache;
            }
        }
    }

    /**
     * @return true if multi-texturing is supported in fixed function
     */
    public static boolean isMultiTextureSupported() {
        return supportsMultiTexture;
    }

    /**
     * Overide setting of fixed function multi-texturing support.
     * 
     * @param use
     */
    public static void overrideMultiTextureSupport(boolean use) {
        supportsMultiTexture = use;
    }

    /**
     * Reset fixed function multi-texturing support to driver-detected setting.
     */
    public static void resetMultiTextureSupport() {
        supportsMultiTexture = supportsMultiTextureDetected;
    }

    
    /**
     * @return true we support dot3 environment texture settings
     */
    public static boolean isEnvDot3Supported() {
        return supportsEnvDot3;
    }

    /**
     * Overide support for dot3 environment texture settings
     * 
     * @param use
     */
    public static void overrideEnvDot3Support(boolean use) {
        supportsEnvDot3 = use;
    }

    /**
     * Reset dot3 environment texture support to driver-detected setting.
     */
    public static void resetEnvDot3Support() {
        supportsEnvDot3 = supportsEnvDot3Detected;
    }

    
    /**
     * @return true we support combine environment texture settings
     */
    public static boolean isEnvCombineSupported() {
        return supportsEnvCombine;
    }

    /**
     * Overide support for combine environment texture settings
     * 
     * @param use
     */
    public static void overrideEnvCombineSupport(boolean use) {
        supportsEnvCombine = use;
    }

    /**
     * Reset combine environment texture support to driver-detected setting.
     */
    public static void resetEnvCombineSupport() {
        supportsEnvCombine = supportsEnvCombineDetected;
    }
    
    
    /**
     * Returns if S3TC compression is available for textures.
     * 
     * @return true if S3TC is available.
     */
    public boolean isS3TCSupported() {
        return supportsS3TCCompression;
    }

    /**
     * Overide setting of S3TC compression support.
     * 
     * @param use
     */
    public static void overrideS3TCSupport(boolean use) {
        supportsS3TCCompression = use;
    }

    /**
     * Reset dot3 environment texture support to driver-detected setting.
     */
    public static void resetS3TCSupport() {
        supportsS3TCCompression = supportsS3TCCompressionDetected;
    }
    
    /**
     * Returns if Texture3D is available for textures.
     * 
     * @return true if Texture3D is available.
     */
    public boolean isTexture3DSupported() {
        return supportsTexture3D;
    }

    /**
     * Overide setting of Texture3D support.
     * 
     * @param use
     */
    public static void overrideTexture3DSupport(boolean use) {
        supportsTexture3D = use;
    }

    /**
     * Reset Texture3D support to driver-detected setting.
     */
    public static void resetTexture3DSupport() {
        supportsTexture3D = supportsTexture3DDetected;
    }

    /**
     * Returns if TextureCubeMap is available for textures.
     * 
     * @return true if TextureCubeMap is available.
     */
    public boolean isTextureCubeMapSupported() {
        return supportsTextureCubeMap;
    }

    /**
     * Overide setting of TextureCubeMap support.
     * 
     * @param use
     */
    public static void overrideTextureCubeMapSupport(boolean use) {
        supportsTextureCubeMap = use;
    }

    /**
     * Reset TextureCubeMap support to driver-detected setting.
     */
    public static void resetTextureCubeMapSupport() {
        supportsTextureCubeMap = supportsTextureCubeMapDetected;
    }

    /**
     * Returns if AutomaticMipmap generation is available for textures.
     * 
     * @return true if AutomaticMipmap generation is available.
     */
    public boolean isAutomaticMipmapsSupported() {
        return automaticMipMaps;
    }

    /**
     * Overide setting of AutomaticMipmap generation support.
     * 
     * @param use
     */
    public static void overrideAutomaticMipmapsSupport(boolean use) {
        automaticMipMaps = use;
    }

    /**
     * Reset AutomaticMipmap generation support to driver-detected setting.
     */
    public static void resetAutomaticMipmapsSupport() {
        automaticMipMaps = automaticMipMapsDetected;
    }
    

    /**
     * @return if Anisotropic texture filtering is supported
     */
    public static boolean isAnisoSupported() {
        return supportsAniso;
    }

    /**
     * Overide setting of support for Anisotropic texture filtering.
     * 
     * @param use
     */
    public static void overrideAnisoSupport(boolean use) {
        supportsAniso = use;
    }

    /**
     * Reset dot3 environment texture support to driver-detected setting.
     */
    public static void resetAnisoSupport() {
        supportsAniso = supportsAnisoDetected;
    }

    
    /**
     * @return true if non pow 2 texture sizes are supported
     */
    public static boolean isNonPowerOfTwoTextureSupported() {
        return supportsNonPowerTwo;
    }

    /**
     * Overide setting of support for non-pow2 texture sizes.
     * 
     * @param use
     */
    public static void overrideNonPowerOfTwoTextureSupport(boolean use) {
        supportsNonPowerTwo = use;
    }

    /**
     * Reset support for non-pow2 texture sizes to driver-detected setting.
     */
    public static void resetNonPowerOfTwoTextureSupport() {
        supportsNonPowerTwo = supportsNonPowerTwoDetected;
    }

    
    /**
     * @return if rectangular texture sizes are supported (width != height)
     */
    public static boolean isRectangularTextureSupported() {
        return supportsRectangular;
    }

    /**
     * Overide auto-detected setting of support for rectangular texture sizes (width != height).
     * 
     * @param use
     */
    public static void overrideRectangularTextureSupport(boolean use) {
        supportsRectangular = use;
    }

    /**
     * Reset support for rectangular texture sizes to driver-detected setting.
     */
    public static void resetRectangularTextureSupport() {
        supportsRectangular = supportsRectangularDetected;
    }

    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(texture, "texture",
                new ArrayList<Texture>(1));
        capsule.write(offset, "offset", 0);
        capsule.write(correctionType, "correctionType", CorrectionType.Perspective);

    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        texture = capsule.readSavableArrayList("texture",
                new ArrayList<Texture>(1));
        offset = capsule.readInt("offset", 0);
        correctionType = capsule.readEnum("correctionType", CorrectionType.class, CorrectionType.Perspective);
        resetFirstLast();
    }

    public Class<? extends TextureState> getClassTag() {
        return TextureState.class;
    }

    public void deleteTextureId(int textureId) {
    }

    public static Image getDefaultTextureImage() {
        return defaultTexture != null ? defaultTexture.getImage() : null;
    }

    public static Texture getDefaultTexture() {
        return defaultTexture;
    }
}
