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
package jmetest.scalarfields;

import java.util.Random;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.Timer;



public class MetaBalls extends TriMesh {
    private static final long serialVersionUID = 1L;
    private MetaBallScalarField field;
    private final ScalarFieldPolygonisator polygonisator;

    public MetaBalls() {
        this( 6 );
    }

    public MetaBalls( final int numBalls ) {
        this( numBalls, 10f, 0.1f );
    }

    public MetaBalls( final int numBalls, final float maxWeight,
            final float maxSpeed ) {
        this( numBalls, maxWeight, maxSpeed, (ColorRGBA) null );
    }

    public MetaBalls( final int numBalls, final float maxWeight,
            final float maxSpeed, ColorRGBA... colors ) {
        this( numBalls, new Vector3f( 5, 5, 5 ), maxWeight, maxSpeed, (ColorRGBA) null );
    }

    public MetaBalls( final int numBalls, final Vector3f fieldBoxSize,
            final float maxWeight, final float maxSpeed, ColorRGBA... ballColors ) {

        field = new MetaBallScalarField( numBalls, fieldBoxSize, maxWeight, maxSpeed, ballColors );
        polygonisator = new ScalarFieldPolygonisator( fieldBoxSize.mult( 2 ), 1, field );
    }

    @Override
    public void updateGeometricState( final float tpf, final boolean initiator ) {
        field.updateBallLocations();
        polygonisator.calculate( this, 1f );
        super.updateGeometricState( 0, true );
    }


    // This does not have to be an internal class ;-)
    private class MetaBallScalarField implements ScalarField {

        private final Ball[] balls;
        private final Vector3f boxSize = new Vector3f();
        private final Vector3f calcVector = new Vector3f();

        private MetaBallScalarField( final int numBalls, final Vector3f fieldBoxSize,
                final float maxWeight, final float maxSpeed, ColorRGBA... ballColors ) {

            boxSize.set( fieldBoxSize );
            balls = new Ball[ numBalls ];

            final Random random = new Random( Timer.getTimer().getTime() );
            for( int i = 0; i < numBalls; ++i ){
                final Vector3f randomPosition = new Vector3f(
                        ( random.nextFloat() * fieldBoxSize.x * 2 ) - fieldBoxSize.x,
                        ( random.nextFloat() * fieldBoxSize.y * 2 ) - fieldBoxSize.y,
                        ( random.nextFloat() * fieldBoxSize.z * 2 ) - fieldBoxSize.z );

                final Vector3f randomSpeed = new Vector3f(
                        random.nextFloat() * maxSpeed,
                        random.nextFloat() * maxSpeed,
                        random.nextFloat() * maxSpeed );

                final float randomWeight = maxWeight * random.nextFloat() + 1f;

                final ColorRGBA ballColor = ballColors != null && ballColors.length > i &&
                        ballColors[i] != null ? ballColors[i] : ColorRGBA.randomColor();

                balls[i] = new Ball( randomPosition, randomWeight, randomSpeed, ballColor );
            }
        }

        public float calculate( final Vector3f point ) {
            float sum = 0;
            for( Ball ball : balls ){
                float part = ball.getWeight() / ( ball.getPosition().distanceSquared( point ) + 0.001f );
                sum += part;
            }
            return sum;
        }

        // VERY IMPORTANT! Do NOT create new Vector3f, method may be called 1000/sec
        public void normal( final Vector3f point, final Vector3f result ) {
            result.zero();
            for( Ball ball : balls ){
                calcVector.set( point ).subtractLocal( ball.getPosition() );
                float lengthSquared = calcVector.lengthSquared() + 0.001f;
                result.addLocal( calcVector.divideLocal( lengthSquared * lengthSquared ) );
            }
            result.normalizeLocal();
        }

        public void textureCoords( final Vector3f normal, final Vector2f result ) {
            result.zero();
            // little trick: we know that normals have been computed before
            result.x = /*point.x/20 +*/ normal.x;
            result.y = /*point.x/20 +*/ normal.y;
        }

        // VERY IMPORTANT! Do NOT create new ColorRGBA, method may be called 1000/sec
        public void color( final Vector3f point, final ColorRGBA color ) {
            color.set( 0, 0, 0, 0 );
            for( Ball ball : balls ){
                float part = ball.getWeight() / ( ball.getPosition().distanceSquared( point ) + 0.001f ) * 2;
                if( part > 1 ){
                    part = 1;
                }
                color.interpolate( ball.getColor(), part );
            }
        }

        public void updateBallLocations() {
            for( Ball ball : balls ){
                ball.getPosition().addLocal( ball.getSpeed() );
                if( ball.getPosition().x < -boxSize.x || ball.getPosition().x > boxSize.x ){
                    ball.getSpeed().x = -ball.getSpeed().x;
                }
                if( ball.getPosition().y < -boxSize.y || ball.getPosition().y > boxSize.y ){
                    ball.getSpeed().y = -ball.getSpeed().y;
                }
                if( ball.getPosition().z < -boxSize.z || ball.getPosition().z > boxSize.z ){
                    ball.getSpeed().z = -ball.getSpeed().z;
                }
            }
        }



        private class Ball {

            private final Vector3f position;
            private final Vector3f speed;
            private final ColorRGBA color;
            private float weight;

            public Ball( Vector3f position, float weight, Vector3f speed, ColorRGBA color ) {
                this.position = position;
                this.weight = weight;
                this.speed = speed;
                this.color = color;
            }

            public Vector3f getPosition() {
                return position;
            }

            public void setPosition( Vector3f position ) {
                this.position.set( position );
            }

            public float getWeight() {
                return weight;
            }

            public void setWeight( float weight ) {
                this.weight = weight;
            }

            public Vector3f getSpeed() {
                return speed;
            }

            public void setSpeed( Vector3f speed ) {
                this.speed.set( speed );
            }

            public ColorRGBA getColor() {
                return color;
            }
        }
    }
}