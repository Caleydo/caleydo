package com.jmex.font2d;

import java.util.Hashtable;

import com.jme.image.Texture;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.font3d.TextFactory;

public class Font2D implements TextFactory {
    private static final String DEFAULT_FONT = "com/jme/app/defaultfont.tga";
    private static Hashtable<String, TextureState> cachedFontTextureStates = new Hashtable<String, TextureState>();
    private String fontBitmapFile = DEFAULT_FONT;
    private TextureState fontTextureState;

    /**
     * Creates the texture state if not created before.
     * 
     * @return texture state for the default font
     */
    public static TextureState getFontTextureState(String fontFile) {
        TextureState cached = cachedFontTextureStates.get(fontFile);
        if (cached == null) {
            cached = DisplaySystem.getDisplaySystem().getRenderer()
                    .createTextureState();
            cached.setTexture(TextureManager.loadTexture(Text.class
                    .getClassLoader().getResource(fontFile), Texture.MinificationFilter.BilinearNoMipMaps,
                    Texture.MagnificationFilter.Bilinear));
            cached.setEnabled(true);
            cachedFontTextureStates.put(fontFile, cached);
        }
        return cached;
    }

    /**
     * @return the texture state used by this font.
     */
    public TextureState getFontTextureState() {
        return fontTextureState;
    }

    /**
     * @return the bitmap used by this fonts texture state.
     */
    public String getFontBitmapFile() {
        return fontBitmapFile;
    }

    public Font2D() {
        this(DEFAULT_FONT);
    }

    public Font2D(String fontBitmapFile) {
        this.fontBitmapFile = fontBitmapFile;
        fontTextureState = getFontTextureState(this.fontBitmapFile);
    }

    public Text2D createText(String text, float size, int flags) {
        Text2D textObj = new Text2D(this, text, size, flags);
        textObj.setCullHint(Spatial.CullHint.Never);
        textObj.setRenderState(fontTextureState);
        textObj.setRenderState(getFontBlend());
        textObj.setTextureCombineMode(TextureCombineMode.Replace);
        textObj.setLightCombineMode(Spatial.LightCombineMode.Off);
        return textObj;
    }

    /*
     * @return a blend state for allowing 'black' to be transparent
     */
    private static BlendState getFontBlend() {
        BlendState as1 = DisplaySystem.getDisplaySystem().getRenderer()
                .createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        return as1;
    }
    
    public static void clearCachedFontTextureStates() {
        try {
            for (TextureState ts : cachedFontTextureStates.values()) {
                ts.deleteAll(true);
            }
        } catch (Exception e) { }
        cachedFontTextureStates.clear();
    }
}
