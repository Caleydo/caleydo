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

package com.jmex.model.ogrexml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.BlendState.BlendEquation;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.scene.state.BlendState.TestFunction;
import com.jme.util.TextureManager;
import com.jme.util.resource.RelativeResourceLocator;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;

/**
 * Reads OGRE3D material files<br/>
 *
 * MaterialLoader.getMaterials() to get a map of materials.
 * call Material.apply(Spatial) to apply material to a model.
 */
public class MaterialLoader {
    private static final Logger logger =
            Logger.getLogger(MaterialLoader.class.getName());

    private StreamTokenizer reader;
    private Map<String, Material> materialMap;

    public MaterialLoader() {
    }

    public Map<String, Material> getMaterials(){
        return materialMap;
    }

    public String tokenName(){
        switch (reader.ttype){
            case StreamTokenizer.TT_EOF:
                return "EOF";
            case StreamTokenizer.TT_EOL:
                return "EOL";
            case StreamTokenizer.TT_NUMBER:
                return reader.nval+"";
            case StreamTokenizer.TT_WORD:
                return reader.sval;
            default:
                return Character.toString((char)reader.ttype);
        }
    }

    public String nextStatementRightCurlyNull() throws IOException{
        while (true) {
            switch (reader.ttype) {
                case StreamTokenizer.TT_WORD:
                    return reader.sval;
                case '}':
                case StreamTokenizer.TT_EOF:
                    return null;
            }
            reader.nextToken();
        }
    }

    public String nextStatement() throws IOException{
        while (reader.ttype != StreamTokenizer.TT_WORD){
            if (reader.ttype == StreamTokenizer.TT_EOF){
                return null;
            }
            reader.nextToken();
        }
        return reader.sval;
    }

    public double nextNumber() throws IOException {
        while (reader.ttype != StreamTokenizer.TT_NUMBER){
            if (reader.ttype == StreamTokenizer.TT_EOF){
                return -1;
            }
            reader.nextToken();
        }
        return reader.nval;
    }

    public ColorRGBA readColor() throws IOException{
        ColorRGBA color = new ColorRGBA();
        color.r = (float) nextNumber();
        reader.nextToken();
        color.g = (float) nextNumber();
        reader.nextToken();
        color.b = (float) nextNumber();
        reader.nextToken();
        color.a = (float) nextNumber();
        if (color.a < 0.000001f) {
            logger.warning(
                    "Reverting alpha value from " + color.a + " to 1.0f");
            color.a = 1f;
        }
        color.clamp();
        return color;
    }

    /**
     * This method just returns null.
     *
     * @deprecated  Haven't checked the history, but this method is now a
     * no-op.
     * @returns null
     */
    @Deprecated
    public float[] readFloatArray(){
        return null;
    }

    public void readMaterialStatement(Material material) throws IOException{
        String stat_name = nextStatement();
        if (stat_name.equals("receive_shadows")){
            reader.nextToken();
            material.recieveShadows = reader.sval.equalsIgnoreCase("true");
        }
        while (reader.ttype != StreamTokenizer.TT_EOL)
            reader.nextToken();
    }

    public void readTechniqueStatement(Material material) throws IOException{
        nextStatement();
        while (reader.ttype != StreamTokenizer.TT_EOL)
            reader.nextToken();
    }

    public void readUnitStatement(TextureState tex, int unit) throws IOException{
        String stat_name = nextStatement();

        if (stat_name.equals("texture")){
            reader.nextToken();
            String texName = nextStatement();

            URL texURL = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, texName);

            if (texURL != null){
                Texture t = TextureManager.loadTexture(texURL,
                                                       MinificationFilter.Trilinear,
                                                       MagnificationFilter.Bilinear, 0.0f, false);
                t.setWrap(WrapMode.Repeat);
                tex.setTexture(t, unit);
            }

            logger.fine("TEXTURE: "+texURL);
        }else if (stat_name.equals("tex_address_mode")){
            reader.nextToken();
            String mode = nextStatement();
            Texture t = tex.getTexture(unit);
            if (t == null)
                return;

            if (mode.equals("wrap")){
                t.setWrap(WrapMode.Repeat);
            }else if (mode.equals("mirror")){
                t.setWrap(WrapMode.MirroredRepeat);
            }else{
                t.setWrap(WrapMode.Clamp);
            }
            logger.fine("ADDRESS MODE: "+mode);
        }else if (stat_name.equals("filtering")){
            reader.nextToken();
            String mode = nextStatement();
            Texture t = tex.getTexture(unit);
            if (t == null)
                return;

            if (mode.equals("trilinear")){
                t.setMinificationFilter(MinificationFilter.Trilinear);
                t.setMagnificationFilter(MagnificationFilter.Bilinear);
            }else{
                // ??
            }
            logger.fine("FILTERING: "+mode);
        }else{
            logger.fine("UNIT STAT: "+stat_name);
        }

        while (reader.ttype != StreamTokenizer.TT_EOL)
            reader.nextToken();
    }

    public void readTextureUnit(Material material) throws IOException{
        // skip 'texture_unit'
        nextStatement();

        TextureState ts = (TextureState) material.getState(RenderState.RS_TEXTURE);
        int unit = ts.getNumberOfSetTextures();

        logger.fine("TEXTURE UNIT START: "+unit);

        while (reader.ttype != '{')
            reader.nextToken();

        while (reader.ttype != '}'){
            readUnitStatement(ts, unit);
            while (reader.ttype == StreamTokenizer.TT_EOL)
                reader.nextToken();
        }

        logger.fine("TEXTURE UNIT END");
    }

    public void readPassStatement(Material material) throws IOException{
        String stat_name = nextStatementRightCurlyNull();
        if (stat_name == null) {
            return;
        }

        if (stat_name.equals("ambient")){
            MaterialState ms = (MaterialState) material.getState(RenderState.RS_MATERIAL);
            ms.setAmbient(readColor());
            logger.fine("AMBIENT: "+ms.getAmbient());
        }else if (stat_name.equals("diffuse")){
            MaterialState ms = (MaterialState) material.getState(RenderState.RS_MATERIAL);
            ms.setDiffuse(readColor());
            logger.fine("DIFFUSE: "+ms.getDiffuse());
        }else if (stat_name.equals("specular")){
            MaterialState ms = (MaterialState) material.getState(RenderState.RS_MATERIAL);
            ms.setSpecular(readColor());
            reader.nextToken();
            ms.setShininess((float)nextNumber());
            logger.fine("SPECULAR: "+ms.getSpecular());
            logger.fine("SHININESS: "+ms.getShininess());
        }else if (stat_name.equals("emissive")){
            MaterialState ms = (MaterialState) material.getState(RenderState.RS_MATERIAL);
            ms.setEmissive(readColor());
            logger.fine("EMISSIVE: "+ms.getEmissive());
        }else if (stat_name.equals("scene_blend")){
            reader.nextToken();
            String mode = nextStatement();
            BlendState as = (BlendState) material.getState(RenderState.RS_BLEND);
            as.setBlendEnabled(true);
            if (mode.equals("alpha_blend")){
                material.transparent = true;
                as.setSourceFunction(SourceFunction.SourceAlpha);
                as.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
                //as.setBlendEquation(BlendEquation.Add);
                as.setTestEnabled(true);
                as.setTestFunction(TestFunction.GreaterThan);
                as.setReference(0.1f);

                CullState cs = (CullState) material.getState(RenderState.RS_CULL);
                cs.setCullFace(CullState.Face.None);
            }else if (mode.equals("modulate")){
                as.setSourceFunction(SourceFunction.DestinationColor);
                as.setDestinationFunction(DestinationFunction.SourceColor);
            }else if (mode.equals("add")){
                as.setBlendEquation(BlendEquation.Add);
            }else{
                throw new IOException("Unknown scene_blend mode: "+mode);
            }
        }else if (stat_name.equals("depth_write")){
            reader.nextToken();
            String enabled = nextStatement();
            ZBufferState zbuf = (ZBufferState) material.getState(RenderState.RS_ZBUFFER);
            if (enabled.equals("on")){
                zbuf.setWritable(true);
            }else{
//                zbuf.setWritable(false);
            }
        }else if (stat_name.equals("lighting")){
            reader.nextToken();
            String enabled = nextStatement();
            if (enabled.equals("on")){
                material.lightingOff = false;
            }else{
                material.lightingOff = true;
            }
        }else if (stat_name.equals("shader")){
            reader.nextToken();
            File f = new File(nextStatement());
            BufferedReader fr = new BufferedReader(new FileReader(f));
            StringBuffer sb = new StringBuffer((int)f.length());
            while (fr.ready()){
                sb.append(fr.readLine()).append("\n");
            }
            fr.close();
            final String vertShader = sb.toString();

            reader.nextToken();
            f = new File(nextStatement());
            fr = new BufferedReader(new FileReader(f));
            sb = new StringBuffer((int)f.length());
            while (fr.ready()){
                sb.append(fr.readLine()).append("\n");
            }
            fr.close();
            final String fragShader = sb.toString();

            GLSLShaderObjectsState glsl = (GLSLShaderObjectsState) material.getState(RenderState.RS_GLSL_SHADER_OBJECTS);
            glsl.load(vertShader, fragShader);
        }else if (stat_name.equals("uniform_int")){
            reader.nextToken();
            String name = nextStatement();

            reader.nextToken();
            int value = (int) nextNumber();

            final GLSLShaderObjectsState glsl = (GLSLShaderObjectsState) material.getState(RenderState.RS_GLSL_SHADER_OBJECTS);
            glsl.setUniform(name, value);
        }else if (stat_name.equals("texture_unit")){
            readTextureUnit(material);
        }else{
            logger.fine("PASS STAT: "+stat_name);
        }

        while (reader.ttype != StreamTokenizer.TT_EOL)
            reader.nextToken();
    }

    public void readPass(Material material) throws IOException{
        // skip "pass"
        reader.nextToken();

        logger.fine("PASS START");

        while (reader.ttype != '{')
            reader.nextToken();

        while (reader.ttype != '}'){
            readPassStatement(material);
            while (reader.ttype == StreamTokenizer.TT_EOL)
                reader.nextToken();
        }

        logger.fine("PASS END");
    }

    public void readTechnique(Material material) throws IOException{
        // skip "technique"
        String stat_name = nextStatement();

        if (!stat_name.equals("technique"))
            throw new IOException("Expected token 'technique' missing");

        logger.fine("TECHNIQUE START");

        while (reader.ttype != '{')
            reader.nextToken();

        while (reader.ttype != '}'){
            stat_name = nextStatement();

            if (stat_name.equals("pass")){
                reader.pushBack();
                readPass(material);
            }else{
                reader.pushBack();
                readTechniqueStatement(material);
            }
        }

        logger.fine("TECHNIQUE END");
    }

    private boolean skip = false;

    public Material readMaterial() throws IOException{
        String stat_name = nextStatement();
        if (stat_name == null){
            skip = false;
            return null;
        }
        if (stat_name.equals("fragment_program")){
            skip = true;
            return null;
        }
        if (!stat_name.equals("material")){
            throw new IOException("Expected 'material', got: "+stat_name);
        }

        reader.resetSyntax();
        reader.ordinaryChar('{');
        reader.ordinaryChar('}');
        reader.wordChars('_', '_');
        reader.wordChars('-', '-');
        reader.wordChars(' ', ' ');
        reader.wordChars('.', '.');
        reader.wordChars('\t', '\t');
        reader.wordChars('0', '9');
        reader.wordChars('A', 'Z');
        reader.wordChars('a', 'z');
        reader.wordChars('/', '/');
        reader.eolIsSignificant(true);

        reader.nextToken();
        String matName = reader.sval.trim();

        Material mat = new Material(matName);

        logger.fine("MATERIAL START: "+matName);
        materialMap.put(matName, mat);

        reader.resetSyntax();
        setupReader(reader);

        while (reader.ttype != '{'){
            reader.nextToken();

            //if (reader.ttype != '{' && reader.ttype != StreamTokenizer.TT_EOL)
            //    matName = matName + " " + reader.toString();
        }

        while (reader.ttype != '}'){
            if (reader.ttype == StreamTokenizer.TT_WORD){
                stat_name = reader.sval;

                if (stat_name.equals("technique")){
                    reader.pushBack();
                    readTechnique(mat);
                }else{
                    reader.pushBack();
                    readMaterialStatement(mat);
                }
            }

            reader.nextToken();
        }
        mat.assignTransparency();

        logger.fine("MATERIAL END for material '" + matName + "'");

        return mat;
    }

    public void setupReader(StreamTokenizer reader){
        reader.slashSlashComments(true);
        reader.parseNumbers();
        reader.ordinaryChar('{');
        reader.ordinaryChar('}');
        reader.wordChars('_', '_');
        reader.wordChars('A', 'Z');
        reader.wordChars('a', 'z');
        reader.wordChars('+', '/');
        reader.whitespaceChars('\t', '\t');
        reader.whitespaceChars(' ', ' ');
        //reader.wordChars('-', '-');
        reader.eolIsSignificant(true);
    }

    /**
     * REPLACE the materialsMap of this MaterialLoader instance.
     */
    public void load(InputStream in) throws IOException {
        reader = new StreamTokenizer(new InputStreamReader(in));
        setupReader(reader);

        materialMap = new HashMap<String, Material>();

        while (true){
            Material mat = readMaterial();
            if (mat == null && !skip)
                return;
        }
    }

    /**
     * Print a debugging message to standard output.
     *
     * @deprecated  If you want to see debugging messages, you should use the
     *              logging infrastructure which is there for this purpose.
     *              This allows you to specify when to see messages
     *              declaratively, without changing any source code.
     */
    @Deprecated
    public void println(String str){
        logger.fine(str);
    }

    /**
     * Convenience wrapper.
     *
     * @see #load(URL)
     */
    public void load(URI uri) throws IOException{
        load(uri.toURL());
    }

    /**
     * REPLACE the materialsMap of this MaterialLoader instance,
     * automatically adding the containing directory to the resource locator
     * paths for the duration of the load.
     * The materials may then be retrieved with getMaterials().
     * <P>
     * An example of invoking this method for a filesystem file:<CODE><PRE>
     *  materialLoader.load(file.toURI());
     *  </PRE></CODE>
     * </P>
     *
     * @see #getMaterials()
     * @see RelativeResourceLocator
     */
    public void load(URL url) throws IOException{
        ResourceLocator locator = null;
        try {
            locator = new RelativeResourceLocator(url);
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException("Bad URL: " + use);
        }
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE, locator);
        InputStream stream = null;
        try {
            stream = url.openStream();
            if (stream == null) {
                throw new IOException("Failed to load materials file '"
                        + url + "'");
            }
            logger.fine("Loading materials from '" + url + "'...");
            load(stream);
        } finally {
            if (stream != null) stream.close();
            ResourceLocatorTool.removeResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE, locator);
            locator = null;  // Just to encourage GC
        }
    }
}
