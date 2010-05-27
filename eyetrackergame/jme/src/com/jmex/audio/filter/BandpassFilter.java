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

package com.jmex.audio.filter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * @author Arman Ozcelik
 * @version $Id: BandpassFilter.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class BandpassFilter extends Filter {

    private float qParam = 1.6f;

    private double alpha[];
    private double beta[];
    private double gamma[];

    public BandpassFilter(int[] frequencies) {
        super(frequencies);
    }

    public void init(int rate) {
        super.initalize(rate);
        alpha = new double[frequencies.length];
        beta = new double[frequencies.length];
        gamma = new double[frequencies.length];
        resetABC(qParam);
    }

    public void resetABC(double q) {
        for (int a = 0; a < theta.length; a++) {
            double tan = Math.tan(theta[a] / (2.0 * q));
            beta[a] = 0.5 * ((1.0 - tan) / (1.0 + tan));
            alpha[a] = (0.5 - beta[a]) / 2.0;
            gamma[a] = (0.5 + beta[a]) * Math.cos(theta[a]);
        }
    }

    public byte[] filter(byte[] input) {

        ByteOrder order = ByteOrder.nativeOrder();
        ShortBuffer sbuf = ByteBuffer.wrap(input).order(order).asShortBuffer();
        short[] sinput = new short[input.length / 2];
        for (int i = 0; i < sinput.length; i++) {
            sinput[i] = (sbuf.get(i));
        }
        if (output == null) {
            output = new double[sinput.length];
        }
        for (int a = 0; a < output.length; a++) {
            output[a] = sinput[a] * gainFactor;
        }

        for (int a = 0; a < frequencies.length; a++) {
            passBand(a, sinput);
        }
        for (int a = 0; a < output.length; a++) {
            sinput[a] = (short) Math.min(Short.MAX_VALUE, Math.max(output[a],
                    Short.MIN_VALUE));
        }
        return toByte(sinput, true);

    }

    /**
     * @param a
     * @param buffer
     */
    private void passBand(int passNumber, short[] sinput) {
        double[] inputArray = new double[3];
        double[] outputArray = new double[3];
        int i = 0, j = 0, k = 0;
        for (int a = 0; a < sinput.length; a++) {
            inputArray[i] = sinput[a];
            j = i - 2;
            if (j < 0)
                j += 3;
            k = i - 1;
            if (k < 0)
                k += 3;
            outputArray[i] = 2 * (alpha[passNumber]
                    * (inputArray[i] - inputArray[j]) + gamma[passNumber]
                    * outputArray[k] - beta[passNumber] * outputArray[j]);

            output[a] += adjust[passNumber] * outputArray[i];
            i = (i + 1) % 3;
        }

    }

    public byte[] toByte(short[] array, boolean flag) {
        byte[] outBuf = new byte[array.length * 2];
        for (int a = 0, b = 0; a < array.length; a++, b += 2) {
            byte[] ret = toByte(array[a], flag);
            outBuf[b] = ret[0];
            outBuf[b + 1] = ret[1];
        }
        return outBuf;
    }

    public static final byte[] toByte(short value, boolean flag) {
        byte temp[] = new byte[2];
        for (byte b = 0; b <= 1; b++)
            temp[b] = (byte) (value >>> (1 - b) * 8);

        if (flag)
            temp = reverse_order(temp, 2);

        return temp;

    }

    public static final byte[] toByte(short s) {
        return toByte(s, false);
    }

    private static final byte[] reverse_order(byte array[], int i) {
        byte temp[] = new byte[i];
        for (byte b = 0; b <= i - 1; b++)
            temp[b] = array[i - 1 - b];

        return temp;
    }

}