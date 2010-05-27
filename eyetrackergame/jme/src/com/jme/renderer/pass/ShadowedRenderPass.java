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

package com.jme.renderer.pass;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import com.jme.light.Light;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shadow.MeshShadows;
import com.jme.scene.shadow.ShadowVolume;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.StencilState.StencilFunction;
import com.jme.scene.state.StencilState.StencilOperation;
import com.jme.system.DisplaySystem;

/**
 * <code>ShadowedRenderPass</code> is a render pass that renders the added
 * spatials along with shadows cast by givens occluders and lights flagged as
 * casting shadows.
 *
 * @author Mike Talbot (some code for MODULATIVE method written Jan 2005)
 * @author Joshua Slack
 * @version $Id: ShadowedRenderPass.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class ShadowedRenderPass extends Pass {

   private static final long serialVersionUID = 1L;

   public enum LightingMethod {
       /**
        * value for lightingMethod indicating that a scene should be rendered first
        * with ambient lighting and then multiple passes per light done to
        * illuminate unshadowed areas (resulting in shadows.) More costly but more
        * accurate than Modulative.
        */
       Additive,
    
       /**
        * value for lightingMethod indicating that a scene should be rendered first
        * with full lighting and then multiple screens applied per light to darken
        * shadowed areas. More prone to artifacts than Additive, but faster.
        */
       Modulative;
   }

   /** list of occluders registered with this pass. */
   protected ArrayList<Spatial> occluders = new ArrayList<Spatial>();

   /** node used to gather and hold shadow volumes for rendering. */
   protected Node volumeNode = new Node("Volumes");

   /** whether or not the renderstates for this pass have been init'd yet. */
   protected boolean initialised = false;

   /**
    * A quad to use with MODULATIVE lightMethod for full screen darkening
    * against the shadow stencil.
    */
   protected Quad shadowQuad = new Quad("RenderForeground", 10, 10);

   /**
    * Used with MODULATIVE lightMethod. Defines the base color of the shadow -
    * the alpha value is replaced with 1 - the alpha of the light's alpha.
    */
   protected ColorRGBA shadowColor = new ColorRGBA(.2f,.2f,.2f,.1f);

   /** Whether shadow volumes are visible */
   protected boolean renderVolume = false;

   /** Whether to render shadows (true) or act like a normal RenderPass (false) */
   protected boolean renderShadows = true;

   /** Sets the type of pass to do to show shadows - ADDITIVE or MODULATIVE */
   protected LightingMethod lightingMethod = LightingMethod.Additive;

   /** collection of TriMesh to MeshShadows mappings */
   protected IdentityHashMap<TriMesh, MeshShadows> meshes = new IdentityHashMap<TriMesh, MeshShadows>();

   /**
    * list of occluders that will be casting shadows in this pass. If no
    * occluders set, pass acts like normal RenderPass.
    */
   protected ArrayList<TriMesh> occluderMeshes = new ArrayList<TriMesh>();

   /**
    * list of lights that will be used to calculate shadows in this pass.
    * Constructed dynamically by searching through the scene for lights with
    * shadowCaster set to true.
    */
   protected ArrayList<Light> shadowLights = new ArrayList<Light>();

   /**
    * a place to internally save previous enforced states setup before
    * rendering this pass
    */
   protected RenderState[] preStates = new RenderState[RenderState.StateType.values().length];

   protected int quadWidth = -1, quadHeight = -1;

   private ShadowGate shadowGate = new DefaultShadowGate();    

   public static boolean rTexture = true;


   /**
    * <code>addOccluder</code> adds an occluder to this pass.
    *
    * @param toAdd
    *            Occluder Spatial to add to this pass.
    */
   public void addOccluder(Spatial toAdd) {
       occluders.add(toAdd);
   }

   /**
    * <code>clearOccluders</code> removes all occluders from this pass.
    */
   public void clearOccluders() {
       occluders.clear();
   }

   /**
    * <code>containsOccluder</code>
    *
    * @param s
    * @return
    */
   public boolean containsOccluder(Spatial s) {
       return occluders.contains(s);
   }

   /**
    * <code>removeOccluder</code>
    *
    * @param toRemove the Occluder Spatial to remove from this pass.
    * @return true if the Spatial was found and removed.
    */
   public boolean removeOccluder(Spatial toRemove) {
       return occluders.remove(toRemove);
   }

   /**
    * @return the number of occluders registered with this pass
    */
   public int occludersSize() {
       return occluders.size();
   }

   /**
    * @return Returns whether shadow volumes will be rendered to the display.
    */
   public boolean getRenderVolume() {
       return renderVolume;
   }

   /**
    * @param renderVolume
    *            sets whether shadow volumes will be rendered to the display
    */
   public void setRenderVolume(boolean renderVolume) {
       this.renderVolume = renderVolume;
   }

   /**
    * @return whether shadow volumes will be rendered to the display.
    */
   public boolean getRenderShadows() {
       return renderShadows;
   }

   /**
    * @param renderShadows
    *            whether shadows will be rendered by this pass.
    */
   public void setRenderShadows(boolean renderShadows) {
       this.renderShadows = renderShadows;
   }


   /**
    * @return the shadowColor used by MODULATIVE lightMethod.
    */
   public ColorRGBA getShadowColor() {
       return shadowColor;
   }


   /**
    * @param shadowColor
    *            the shadowColor used by MODULATIVE lightMethod.
    */
   public void setShadowColor(ColorRGBA shadowColor) {
       if (shadowColor == null)
           throw new IllegalArgumentException("shadowColor must not be null!");
       this.shadowColor = shadowColor;
   }


   /**
    * @return the lightingMethod currently in use.
    */
   public LightingMethod getLightingMethod() {
       return lightingMethod;
   }


   /**
     * Sets which method to use with the shadow volume stencils in order to
     * generate shadows in the scene. See javadoc descriptions in the enum
     * LightingMethod for more info.
     * 
     * @param method
     *            method to use
     * @throws IllegalArgumentException
     *             if method is null
     */
   public void setLightingMethod(LightingMethod method) {
       if (method == null) {
           throw new IllegalArgumentException("method can not be null.");
       }
       this.lightingMethod = method;
   }


   /**
    * <code>doRender</code> renders this pass to the framebuffer
    *
    * @param r
    *            Renderer to use for drawing.
    * @see com.jme.renderer.pass.Pass#doRender(com.jme.renderer.Renderer)
    */
   public void doRender(Renderer r) {
       // init states
       init();
       
       if (!renderShadows) {
           renderScene(r);
           if (renderVolume) {
               getShadowLights();
               setupOccluderMeshes();
               generateVolumes();
               drawVolumes(r);
           }
           return;
       }

       // grab the shadowcasting lights
       getShadowLights();

       // grab the occluders
       setupOccluderMeshes();

       // if no occluders or no shadow casting lights, just render the scene normally and return.
       if (occluderMeshes.size() == 0 || shadowLights.size() == 0) {
           //render normal
           renderScene(r);
           cleanup();
           return;
       } 
           
       // otherwise render an ambient pass by masking the diffuse and specular of shadowcasting lights.
       if (lightingMethod == LightingMethod.Additive) {
           maskShadowLights(LightState.MASK_DIFFUSE | LightState.MASK_SPECULAR);
           saveEnforcedStates();
           context.enforceState(noTexture);
           renderScene(r);
           replaceEnforcedStates();
           unmaskShadowLights();
           r.setPolygonOffset(0.0f, -5.0f);
       } else {
           renderScene(r);
       }       

       generateVolumes();

       for (int l = shadowLights.size(); --l >= 0;) {
           Light light = shadowLights.get(l);
           light.setEnabled(false);
       }
       for (int l = shadowLights.size(); --l >= 0;) {
           Light light = shadowLights.get(l);
           // Clear out the stencil buffer
           r.clearStencilBuffer();
           light.setEnabled(true);

           saveEnforcedStates();
           context.enforceState(noTexture);
           context.enforceState(forTesting);
           context.enforceState(colorDisabled);

           if (StencilState.supportsTwoSided()) {
               context.enforceState(noCull);
               context.enforceState(stencilBothFaces);
               volumeNode.getChildren().clear();
               addShadowVolumes(light);
               volumeNode.updateWorldVectors();
               volumeNode.onDraw(r);
           } else {
               context.enforceState(stencilFrontFaces);
               context.enforceState(cullBackFace);
    
               volumeNode.getChildren().clear();
               addShadowVolumes(light);
               volumeNode.updateWorldVectors();
               volumeNode.onDraw(r);
    
               context.enforceState(stencilBackFaces);
               context.enforceState(cullFrontFace);
               volumeNode.onDraw(r);
           }

           context.enforceState(colorEnabled);
           context.enforceState(forColorPassTesting);
           context.enforceState(cullBackFace);
           if (lightingMethod == LightingMethod.Additive) {
               context.enforceState(lights);
               context.enforceState(blended);
               lights.detachAll();
               lights.attach(light);
               context.enforceState(stencilDrawWhenNotSet);
               renderScene(r);
           } else {
               if (rTexture) {
                   context.enforceState(modblended);
                   context.enforceState(zbufferAlways);
                   context.enforceState(cullBackFace);
                   context.enforceState(noLights);
                   context.enforceState(stencilDrawOnlyWhenSet);
    
                   shadowColor.a = 1 - light.getAmbient().a;
                   shadowQuad.setDefaultColor(shadowColor);
                   r.setOrtho();
                   resetShadowQuad(r);
                   shadowQuad.draw(r);
                   r.unsetOrtho();
               }
           }
           light.setEnabled(false);
           replaceEnforcedStates();
       }

       for (int l = shadowLights.size(); --l >= 0;) {
           Light light = shadowLights.get(l);
           light.setEnabled(true);
       }

       if (lightingMethod == LightingMethod.Additive && rTexture ) {
           saveEnforcedStates();
           context.enforceState(noStencil);
           context.enforceState(colorEnabled);
           context.enforceState(cullBackFace);
           context.enforceState(blendTex);
           renderScene(r);
           replaceEnforcedStates();
       }

       if (renderVolume) {
           drawVolumes(r);
       }

       cleanup();
   }

   protected void cleanup() {
       occluderMeshes.clear();
       shadowLights.clear();
   }


   protected void maskShadowLights(int mask) {
       for (int x = shadowLights.size(); --x >= 0;) {
           Light l = shadowLights.get(x);
           l.pushLightMask();
           l.setLightMask(mask);
       }
   }

   protected void unmaskShadowLights() {
       for (int x = shadowLights.size(); --x >= 0;) {
           Light l = shadowLights.get(x);
           l.popLightMask();
       }
   }


   protected void renderScene(Renderer r) {
       for (int i = 0, sSize = spatials.size(); i < sSize; i++) {
           Spatial s = spatials.get(i);
           s.onDraw(r);
       }
       r.renderQueue();
   }


   protected void getShadowLights() {
       if (shadowLights == null) shadowLights = new ArrayList<Light>();
       for (int x = occluders.size(); --x >= 0;)
           getShadowLights(occluders.get(x));
   }

   protected void getShadowLights(Spatial s) {
       if (s instanceof Geometry) {
           Geometry g = (Geometry)s;
           LightState ls = (LightState)g.states[RenderState.StateType.Light.ordinal()];
           if (ls != null) {
               for (int q = ls.getQuantity(); --q >= 0;) {
                   Light l = ls.get(q);
                   if (l.isShadowCaster()
                           && (l.getType() == Light.Type.Directional ||
                                   l.getType() == Light.Type.Point)
                           && !shadowLights.contains(l)) {
                       shadowLights.add(l);
                   }
               }
           }
       }
       if (s instanceof Node) {
           Node n = (Node)s;
           if (n.getChildren() != null) {
               List<Spatial> children = n.getChildren();
               for (int i = children.size(); --i >= 0;) {
                   Spatial child = children.get(i);
                   getShadowLights(child);
               }
           }
       }

   }

   protected void setupOccluderMeshes() {
       if (occluderMeshes == null) occluderMeshes = new ArrayList<TriMesh>();
       occluderMeshes.clear();
       for (int x = occluders.size(); --x >= 0;)
           setupOccluderMeshes(occluders.get(x));
       
       meshes.keySet().retainAll(occluderMeshes);
   }

   protected void setupOccluderMeshes(Spatial spat) {
       if (spat instanceof TriMesh)
           addOccluderMeshes((TriMesh)spat);
       else if (spat instanceof Node) {
           Node node = (Node)spat;
           for (int c = 0, nQ = node.getQuantity(); c < nQ; c++) {
               Spatial child = node.getChild(c);
               setupOccluderMeshes(child);
           }
       }
   }

   private void addOccluderMeshes(TriMesh mesh) {
       if (mesh.isCastsShadows()) {
           occluderMeshes.add(mesh);
       }
   }

/**
    * saves any states enforced by the user for replacement at the end of the
    * pass.
    */
   protected void saveEnforcedStates() {
       for (int x = RenderState.StateType.values().length; --x >= 0;) {
           preStates[x] = context.enforcedStateList[x];
       }
   }

   /**
    * replaces any states enforced by the user at the end of the pass.
    */
   protected void replaceEnforcedStates() {
       for (int x = RenderState.StateType.values().length; --x >= 0;) {
           context.enforcedStateList[x] = preStates[x];
       }
   }

   protected void generateVolumes() {

        for (int c = 0; c < occluderMeshes.size(); c++) {
            TriMesh mesh = occluderMeshes.get(c);
            if (!shadowGate.shouldUpdateShadows(mesh)) continue;
            if (!meshes.containsKey(mesh)) {
                meshes.put(mesh, new MeshShadows(mesh));
            } else if ((mesh.getLocks() & Spatial.LOCKED_SHADOWS) != 0) {
                continue;
            }

            MeshShadows sv = meshes.get(mesh);

            // Create the geometry for the shadow volume
            sv.createGeometry((LightState) mesh.states[RenderState.StateType.Light.ordinal()]);
        }
    }

   /**
     * <code>addShadowVolumes</code> adds the shadow volumes for a given light
     * to volumeNode
     * 
     * @param light
     *            the light whose volumes should be added
     */
   protected void addShadowVolumes(Light light) {
       if (enabled) {
           for (int i = occluderMeshes.size(); --i >= 0;) {
               TriMesh key = occluderMeshes.get(i);
               if (!shadowGate.shouldDrawShadows(key)) continue;
               MeshShadows ms = meshes.get(key);
               ShadowVolume lv = ms.getShadowVolume(light);
               if (lv != null)
                   volumeNode.getChildren().add(lv);
           }
       }

   }


   /**
    * <code>drawVolumes</code> is a debug method used to draw the shadow
    * volumes currently in use in the pass.
    *
    * @param r
    *            Renderer to draw with.
    */
   protected void drawVolumes(Renderer r) {

       Node renderNode = new Node("renderVolume");
       renderNode.setRenderState(cullBackFace);
       renderNode.setRenderState(forTesting);
       renderNode.setRenderState(colorEnabled);
       renderNode.setRenderState(noStencil);
       renderNode.setRenderState(alphaBlended);

       for (int i = occluderMeshes.size(); --i >= 0;) {
           Object key = occluderMeshes.get(i);
           MeshShadows ms = meshes.get(key);
           if(ms != null) {
               ArrayList<ShadowVolume> volumes = ms.getVolumes();
               for (int v = 0, vSize = volumes.size(); v < vSize; v++) {
                   ShadowVolume vol = volumes.get(v);
                   renderNode.attachChild(vol);
                   vol.setDefaultColor(new ColorRGBA(0,1,0,.075f));
               }
           }
       }

       renderNode.updateRenderState();
       renderNode.updateGeometricState(0, true);
       renderNode.onDraw(r);
   }

   protected static ZBufferState zbufferWriteLE;
   protected static ZBufferState zbufferAlways;
   protected static ZBufferState forTesting;
   protected static ZBufferState forColorPassTesting;

   protected static StencilState noStencil;
   protected static StencilState stencilFrontFaces;
   protected static StencilState stencilBothFaces;
   protected static StencilState stencilBackFaces;
   protected static StencilState stencilDrawOnlyWhenSet;
   protected static StencilState stencilDrawWhenNotSet;

   protected static CullState cullFrontFace;
   protected static CullState cullBackFace;
   protected static CullState noCull;

   protected static TextureState noTexture;

   protected static LightState lights;
   protected static LightState noLights;

   public static BlendState blended;
   public static BlendState alphaBlended;
   public static BlendState modblended;
   public static BlendState blendTex;
   
   protected static ColorMaskState colorEnabled;
   protected static ColorMaskState colorDisabled;

   protected void init() {
       if (initialised)
           return;

       initialised = true;

       Renderer r = DisplaySystem.getDisplaySystem().getRenderer();

       zbufferWriteLE = r.createZBufferState();
       zbufferWriteLE.setWritable(true);
       zbufferWriteLE.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
       zbufferWriteLE.setEnabled(true);

       zbufferAlways = r.createZBufferState();
       zbufferAlways.setEnabled(false);
       zbufferAlways.setWritable(false);

       forTesting = r.createZBufferState();
       forTesting.setWritable(false);
       forTesting.setFunction(ZBufferState.TestFunction.LessThan);
       forTesting.setEnabled(true);

       forColorPassTesting = r.createZBufferState();
       forColorPassTesting.setWritable(false);
       forColorPassTesting.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
       forColorPassTesting.setEnabled(true);

       noStencil = r.createStencilState();
       noStencil.setEnabled(false);

       stencilBothFaces = r.createStencilState();
       stencilBothFaces.setEnabled(true);
       stencilBothFaces.setUseTwoSided(true);
       stencilBothFaces.setStencilMaskFront(~0);
       stencilBothFaces.setStencilFunctionFront(StencilFunction.Always);
       stencilBothFaces.setStencilOpFailFront(StencilOperation.Keep);
       stencilBothFaces.setStencilOpZFailFront(StencilOperation.Keep);
       stencilBothFaces.setStencilOpZPassFront(StencilOperation.IncrementWrap);
       stencilBothFaces.setStencilMaskBack(~0);
       stencilBothFaces.setStencilFunctionBack(StencilFunction.Always);
       stencilBothFaces.setStencilOpFailBack(StencilOperation.Keep);
       stencilBothFaces.setStencilOpZFailBack(StencilOperation.Keep);
       stencilBothFaces.setStencilOpZPassBack(StencilOperation.DecrementWrap);

       stencilFrontFaces = r.createStencilState();
       stencilFrontFaces.setEnabled(true);
       stencilFrontFaces.setStencilMask(~0);
       stencilFrontFaces.setStencilFunction(StencilFunction.Always);
       stencilFrontFaces.setStencilOpFail(StencilOperation.Keep);
       stencilFrontFaces.setStencilOpZFail(StencilOperation.Keep);
       stencilFrontFaces.setStencilOpZPass(StencilOperation.IncrementWrap);

       stencilBackFaces = r.createStencilState();
       stencilBackFaces.setEnabled(true);
       stencilBackFaces.setStencilMask(~0);
       stencilBackFaces.setStencilFunction(StencilFunction.Always);
       stencilBackFaces.setStencilOpFail(StencilOperation.Keep);
       stencilBackFaces.setStencilOpZFail(StencilOperation.Keep);
       stencilBackFaces.setStencilOpZPass(StencilOperation.DecrementWrap);

       stencilDrawOnlyWhenSet = r.createStencilState();
       stencilDrawOnlyWhenSet.setEnabled(true);
       stencilDrawOnlyWhenSet.setStencilMask(~0);
       stencilDrawOnlyWhenSet.setStencilFunction(StencilFunction.NotEqualTo);
       stencilDrawOnlyWhenSet.setStencilOpFail(StencilOperation.Keep);
       stencilDrawOnlyWhenSet.setStencilOpZFail(StencilOperation.Keep);
       stencilDrawOnlyWhenSet.setStencilOpZPass(StencilOperation.Keep);
       stencilDrawOnlyWhenSet.setStencilReference(0);

       stencilDrawWhenNotSet = r.createStencilState();
       stencilDrawWhenNotSet.setEnabled(true);
       stencilDrawWhenNotSet.setStencilMask(~0);
       stencilDrawWhenNotSet.setStencilFunction(StencilFunction.EqualTo);
       stencilDrawWhenNotSet.setStencilOpFail(StencilOperation.Keep);
       stencilDrawWhenNotSet.setStencilOpZFail(StencilOperation.Keep);
       stencilDrawWhenNotSet.setStencilOpZPass(StencilOperation.Keep);
       stencilDrawWhenNotSet.setStencilReference(0);

       cullFrontFace = r.createCullState();
       cullFrontFace.setEnabled(true);
       cullFrontFace.setCullFace(CullState.Face.Front);

       noCull = r.createCullState();
       noCull.setEnabled(false);

       noLights = r.createLightState();
       noLights.setEnabled(false);

       cullBackFace = r.createCullState();
       cullBackFace.setEnabled(true);
       cullBackFace.setCullFace(CullState.Face.Back);

       blended = r.createBlendState();
       blended.setEnabled(true);
       blended.setBlendEnabled(true);
       blended.setDestinationFunction(BlendState.DestinationFunction.One);
       blended.setSourceFunction(BlendState.SourceFunction.DestinationColor);

       alphaBlended = r.createBlendState();
       alphaBlended.setEnabled(true);
       alphaBlended.setBlendEnabled(true);
       alphaBlended.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
       alphaBlended.setSourceFunction(BlendState.SourceFunction.One);

       modblended = r.createBlendState();
       modblended.setEnabled(true);
       modblended.setBlendEnabled(true);
       modblended.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
       modblended.setSourceFunction(BlendState.SourceFunction.DestinationColor);

       blendTex = r.createBlendState();
       blendTex.setEnabled(true);
       blendTex.setBlendEnabled(true);
       blendTex.setDestinationFunction(BlendState.DestinationFunction.Zero);
       blendTex.setSourceFunction(BlendState.SourceFunction.DestinationColor);

       colorEnabled = r.createColorMaskState();
       colorEnabled.setAll(true);

       colorDisabled = r.createColorMaskState();
       colorDisabled.setAll(false);

       volumeNode.setRenderQueueMode(Renderer.QUEUE_SKIP);
       volumeNode.updateRenderState();
       volumeNode.attachChild(new Node());

       noTexture = r.createTextureState();
       noTexture.setEnabled(false);

       resetShadowQuad(r);
       
       lights = r.createLightState();
       lights.setEnabled(true);
       lights.setLightMask(LightState.MASK_AMBIENT | LightState.MASK_GLOBALAMBIENT);
   }
   
   public void resetShadowQuad(Renderer r) {
       if (r.getWidth() == quadWidth && r.getHeight() == quadHeight) return;
       quadWidth = r.getWidth();
       quadHeight = r.getHeight();
       shadowQuad.resize(quadWidth, r.getHeight());
       shadowQuad.setLocalTranslation(new Vector3f(quadWidth >> 1, quadHeight >> 1, 0));
       shadowQuad.setRenderQueueMode(Renderer.QUEUE_SKIP);
       shadowQuad.updateGeometricState(0, true);
       shadowQuad.updateRenderState();
       
   }

    public ShadowGate getShadowGate() {
        return shadowGate;
    }
    
    public void setShadowGate(ShadowGate shadowCheck) {
        this.shadowGate = shadowCheck;
    }
}
