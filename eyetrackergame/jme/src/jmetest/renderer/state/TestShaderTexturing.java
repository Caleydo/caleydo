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

package jmetest.renderer.state;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class TestShaderTexturing extends SimpleGame {

    public static void main(String[] args) {
        TestShaderTexturing app = new TestShaderTexturing();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    
    @Override
    protected void simpleInitGame() {
        String shader = "uniform sampler2D tex0, tex1, tex2, tex3, tex4;"
                + " void main(){"
                + "vec2 tc0 = vec2(gl_TexCoord[1].x, gl_TexCoord[1].y);"
                + "vec2 tc1 = vec2(gl_TexCoord[0].x, gl_TexCoord[0].y);"
                + "vec4 result = texture2D(tex0,tc0);"
                + "vec4 col0 = texture2D(tex1, tc0);"
                + "vec4 alp0= texture2D(tex2, tc1);"
                + "result = mix(result, col0, alp0.r);"
                + "vec4 col1 = texture2D(tex3, tc0);"
                + "vec4 alp1= texture2D(tex4, tc1);"
                + "result = mix(result, col1, alp1.r);"
                + "gl_FragColor = result * gl_Color;" + "}";

        GLSLShaderObjectsState so = display.getRenderer()
                .createGLSLShaderObjectsState();
        so.load(null, shader);
        so.setUniform("tex0", 0);
        so.setUniform("tex1", 1);
        so.setUniform("tex2", 2);
        so.setUniform("tex3", 3);
        so.setUniform("tex4", 4);
        so.setEnabled(true);

        Quad mesh = new Quad("mesh", 10, 10);
        mesh.copyTextureCoordinates(0, 1, 1.0f);
        mesh.setRenderState(so);

        TextureState ts = display.getRenderer().createTextureState();
        Texture t0 = TextureManager.loadTexture(ClassLoader
                .getSystemResource("jmetest/data/texture/decalimage.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.NearestNeighbor);
        ts.setTexture(t0, 0);
        Texture t1 = TextureManager.loadTexture(ClassLoader
                .getSystemResource("jmetest/data/texture/highest.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t1, 1);
        Texture t2 = TextureManager.loadTexture(ClassLoader
                .getSystemResource("jmetest/data/cursor/testcursor.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.NearestNeighbor, 0, true);
        t2.setWrap(Texture.WrapMode.EdgeClamp);
        ts.setTexture(t2, 2);
        Texture t3 = TextureManager.loadTexture(ClassLoader
                .getSystemResource("jmetest/data/texture/highest.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t3, 3);
        Texture t4 = TextureManager.loadTexture(ClassLoader
                .getSystemResource("jmetest/data/cursor/testcursor.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.NearestNeighbor, 0, false);
        t4.setWrap(Texture.WrapMode.EdgeClamp);
        ts.setTexture(t4, 4);

        mesh.setRenderState(ts);
        rootNode.attachChild(mesh);

        lightState.setEnabled(false);
    }

}
