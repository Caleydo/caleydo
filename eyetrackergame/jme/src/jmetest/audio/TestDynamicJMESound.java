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

package jmetest.audio;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.jme.app.SimpleGame;
import com.jme.scene.Text;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.openal.OpenALStreamedAudioPlayer;
import com.jmex.audio.player.StreamedAudioPlayer;
import com.jmex.audio.stream.AudioInputStream;

/**
 * Demonstration of subclassing the audio system to provide your own data.
 * (Requires OpenAL)
 * 
 * @author toxcwav
 */
public class TestDynamicJMESound extends SimpleGame {

    private AudioSystem audio;
    private Text label;

    public static void main(String[] args) {
        TestDynamicJMESound app = new TestDynamicJMESound();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    @Override()
    protected void simpleInitGame() {

        audio = AudioSystem.getSystem();
        audio.getEar().trackOrientation(cam);
        audio.getEar().trackPosition(cam);

        AudioTrack music = getDynamic();

        audio.getEnvironmentalPool().addTrack(music);
        label = Text.createDefaultTextLabel("listen",
                "Static noise should be playing.  Hit esc to quit.");
        label.updateRenderState();
        label.setLocalTranslation((display.getWidth() - label.getWidth()) / 2f,
                (display.getHeight() - label.getHeight()) / 2f, 0);
    }

    private AudioTrack getDynamic() {
        return new DynamicAudioTrack();
    }

    @Override()
    protected void simpleUpdate() {
        audio.update();
    }

    @Override
    protected void simpleRender() {
        super.simpleRender();
        label.draw(display.getRenderer());
    }

    @Override()
    protected void cleanup() {
        audio.cleanup();
    }

    class DynamicAudioStream extends AudioInputStream {
        private int samplesPerSecond = 44100;

        public DynamicAudioStream() throws IOException {
            // supply a dummy URL for the superclass
            super(DynamicAudioStream.class.getClassLoader().getResource("."),
                    10.0f);
        }

        @Override
        public int available() {
            return samplesPerSecond;
        }

        @Override
        public int getBitRate() {
            return getChannelCount() * getDepth() / 8 * samplesPerSecond;
        }

        @Override
        public int getChannelCount() {
            return 1;
        }

        @Override
        public int getDepth() {
            return 16;
        }

        @Override
        public AudioInputStream makeNew() throws IOException {
            return this;
        }

        /** synthesizes the next audio buffer */
        @Override
        public int read(ByteBuffer b, int offset, int length)
                throws IOException {
            for (int i = offset; i < length; i++) {
                b.put(i, (byte) (Math.random() * 200));
            }
            return length;
        }
    }

    class DynamicAudioTrack extends AudioTrack {

        private StreamedAudioPlayer stream;

        public DynamicAudioTrack() {
            // supply dummy URL for superclass
            super(DynamicAudioStream.class.getClassLoader().getResource("."),
                    true);

            try {
                this.stream = new OpenALStreamedAudioPlayer(
                        new DynamicAudioStream(), this);
                stream.init();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            setPlayer(stream);

            setEnabled(true);

            setType(TrackType.ENVIRONMENT);
            setRelative(true);
            setTargetVolume(0.9F);
        }
    }
}
