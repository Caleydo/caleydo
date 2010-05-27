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

package jmetest.effects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.model.converters.MilkToJme;

/**
 * <code>TestDynamicSmoker</code>
 * @author Joshua Slack
 */
public class TestDynamicSmoker extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestDynamicSmoker.class.getName());
    
  private Node smokeNode;
  private Vector3f offset = new Vector3f(0,3.75f,14.0f);
  private ParticleMesh mesh;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestDynamicSmoker app = new TestDynamicSmoker();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
      cam.setLocation( new Vector3f( 0.0f, 50.0f, 100.0f ) );
      cam.update();

      smokeNode = new Node( "Smoker Node" );
      smokeNode.setLocalTranslation( new Vector3f( 0, 50, -50 ) );

      // Setup the input controller and timer
      input = new NodeHandler( smokeNode, 10f, 1f );

      display.setTitle( "Dynamic Smoke box" );

      // hijack the camera model for our own purposes
      Spatial camBox;
      MilkToJme converter = new MilkToJme();
      URL MSFile = TestDynamicSmoker.class.getClassLoader().getResource(
              "jmetest/data/model/msascii/camera.ms3d" );
      ByteArrayOutputStream BO = new ByteArrayOutputStream();

      try {
          converter.convert( MSFile.openStream(), BO );
      } catch ( IOException e ) {
          logger.info( "IO problem writting the file!!!" );
          logger.info( e.getMessage() );
          System.exit( 0 );
      }
      camBox = null;
      try {
          camBox =(Spatial)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
          camBox.setModelBound(new BoundingBox());
          camBox.updateModelBound();
      } catch ( IOException e ) {
          logger.info( "darn exceptions:" + e.getMessage() );
      }

      camBox.setLocalScale( 5f );
      camBox.setRenderQueueMode( Renderer.QUEUE_OPAQUE );
      smokeNode.attachChild( camBox );
      Disk emitDisc = new Disk( "disc", 6, 6, 1.5f );
      emitDisc.setLocalTranslation( offset );
      emitDisc.setCullHint( Spatial.CullHint.Always );
      smokeNode.attachChild( emitDisc );
      rootNode.attachChild( smokeNode );

      BlendState as1 = display.getRenderer().createBlendState();
      as1.setBlendEnabled( true );
      as1.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
      as1.setDestinationFunction( BlendState.DestinationFunction.One );
      as1.setTestEnabled( true );
      as1.setTestFunction( BlendState.TestFunction.GreaterThan );
      as1.setEnabled( true );

      TextureState ts = display.getRenderer().createTextureState();
      ts.setTexture(
              TextureManager.loadTexture(
                      TestDynamicSmoker.class.getClassLoader().getResource(
                              "jmetest/data/texture/flaresmall.jpg" ),
                      Texture.MinificationFilter.Trilinear,
                      Texture.MagnificationFilter.Bilinear ) );
      ts.setEnabled( true );

      mesh = ParticleFactory.buildParticles("particles", 300);
      mesh.setEmissionDirection( new Vector3f( 0f, 0f, 1f ) );
      mesh.setMaximumAngle( 0.0f );
      mesh.setSpeed( 1.0f );
      mesh.setMinimumLifeTime( 750.0f );
      mesh.setMaximumLifeTime( 900.0f );
      mesh.setStartSize( 1.6f );
      mesh.setEndSize( 15.0f );
      mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
      mesh.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f ) );
      mesh.setInitialVelocity( 0.5f );
      mesh.setGeometry( emitDisc );
      mesh.setRotateWithScene(true);

      mesh.forceRespawn();
      mesh.warmUp( 60 );
      
      mesh.setModelBound(new BoundingBox());
      mesh.updateModelBound();
      
      ZBufferState zbuf = display.getRenderer().createZBufferState();
      zbuf.setWritable( false );
      zbuf.setEnabled( true );
      zbuf.setFunction( ZBufferState.TestFunction.LessThanOrEqualTo );

      mesh.setRenderState( ts );
      mesh.setRenderState( as1 );
      mesh.setRenderState( zbuf );
      rootNode.attachChild( mesh );
  }

}
