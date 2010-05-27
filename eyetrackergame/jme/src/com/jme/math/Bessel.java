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

package com.jme.math;

/**
 * This class provides static Bessel functions.
 */
public final class Bessel {

  /**
   * @param x a double value
   * @return the Bessel function of order 0 of the argument.
   */
  public static double j0(double x) {
    double ax;

    if ( (ax = Math.abs(x)) < 8.0) {
      double y = x * x;
      double ans1 = 57568490574.0 + y * ( -13362590354.0 + y * (651619640.7
          + y * ( -11214424.18 + y * (77392.33017 + y * ( -184.9052456)))));
      double ans2 = 57568490411.0 + y * (1029532985.0 + y * (9494680.718
          + y * (59272.64853 + y * (267.8532712 + y * 1.0))));

      return ans1 / ans2;

    }
    
    double z = 8.0 / ax;
    double y = z * z;
    double xx = ax - 0.785398164;
    double ans1 = 1.0 + y * ( -0.1098628627e-2 + y * (0.2734510407e-4
        + y * ( -0.2073370639e-5 + y * 0.2093887211e-6)));
    double ans2 = -0.1562499995e-1 + y * (0.1430488765e-3
                                          +
                                          y * ( -0.6911147651e-5 + y * (0.7621095161e-6
        - y * 0.934935152e-7)));

    return Math.sqrt(0.636619772 / ax) *
        (Math.cos(xx) * ans1 - z * Math.sin(xx) * ans2);    
  }

  /**
   * @param x a double value
   * @return the Bessel function of order 1 of the argument.
   */
  static public double j1(double x) {

    double ax;
    double y;
    double ans1, ans2;

    if ( (ax = Math.abs(x)) < 8.0) {
      y = x * x;
      ans1 = x * (72362614232.0 + y * ( -7895059235.0 + y * (242396853.1
          + y * ( -2972611.439 + y * (15704.48260 + y * ( -30.16036606))))));
      ans2 = 144725228442.0 + y * (2300535178.0 + y * (18583304.74
          + y * (99447.43394 + y * (376.9991397 + y * 1.0))));
      return ans1 / ans2;
    }
    
    double z = 8.0 / ax;
    double xx = ax - 2.356194491;
    y = z * z;

    ans1 = 1.0 + y * (0.183105e-2 + y * ( -0.3516396496e-4
                                         +
                                         y * (0.2457520174e-5 + y * ( -0.240337019e-6))));
    ans2 = 0.04687499995 + y * ( -0.2002690873e-3
                                + y * (0.8449199096e-5 + y * ( -0.88228987e-6
        + y * 0.105787412e-6)));
    double ans = Math.sqrt(0.636619772 / ax) *
        (Math.cos(xx) * ans1 - z * Math.sin(xx) * ans2);
    if (x < 0.0) ans = -ans;
    return ans;    
  }

  /**
   * @param n integer order
   * @param x a double value
   * @return the Bessel function of order n of the argument.
   */
  static public double jn(int n, double x) {
    int j, m;
    double ax, bj, bjm, bjp, sum, tox, ans;
    boolean jsum;

    double ACC = 40.0;
    double BIGNO = 1.0e+10;
    double BIGNI = 1.0e-10;

    if (n == 0)return j0(x);
    if (n == 1)return j1(x);

    ax = Math.abs(x);
    if (ax == 0.0)return 0.0;
    else
    if (ax > n) {
      tox = 2.0 / ax;
      bjm = j0(ax);
      bj = j1(ax);
      for (j = 1; j < n; j++) {
        bjp = j * tox * bj - bjm;
        bjm = bj;
        bj = bjp;
      }
      ans = bj;
    } else {
      tox = 2.0 / ax;
      m = 2 * ( (n + (int) Math.sqrt(ACC * n)) / 2);
      jsum = false;
      bjp = ans = sum = 0.0;
      bj = 1.0;
      for (j = m; j > 0; j--) {
        bjm = j * tox * bj - bjp;
        bjp = bj;
        bj = bjm;
        if (Math.abs(bj) > BIGNO) {
          bj *= BIGNI;
          bjp *= BIGNI;
          ans *= BIGNI;
          sum *= BIGNI;
        }
        if (jsum) sum += bj;
        jsum = !jsum;
        if (j == n) ans = bjp;
      }
      sum = 2.0 * sum - bj;
      ans /= sum;
    }
    return x < 0.0 && n % 2 == 1 ? -ans : ans;
  }

  /**
   * @param x a double value
   * @return the Bessel function of the second kind,
   *          of order 0 of the argument.
   */
  static public double y0(double x) {

    if (x < 8.0) {
      double y = x * x;

      double ans1 = -2957821389.0 + y * (7062834065.0 + y * ( -512359803.6
          + y * (10879881.29 + y * ( -86327.92757 + y * 228.4622733))));
      double ans2 = 40076544269.0 + y * (745249964.8 + y * (7189466.438
          + y * (47447.26470 + y * (226.1030244 + y * 1.0))));

      return (ans1 / ans2) + 0.636619772 * j0(x) * Math.log(x);
    } 
    
    double z = 8.0 / x;
    double y = z * z;
    double xx = x - 0.785398164;

    double ans1 = 1.0 + y * ( -0.1098628627e-2 + y * (0.2734510407e-4
        + y * ( -0.2073370639e-5 + y * 0.2093887211e-6)));
    double ans2 = -0.1562499995e-1 + y * (0.1430488765e-3
                                          +
                                          y * ( -0.6911147651e-5 + y * (0.7621095161e-6
        + y * ( -0.934945152e-7))));
    return Math.sqrt(0.636619772 / x) *
        (Math.sin(xx) * ans1 + z * Math.cos(xx) * ans2);   
  }

  /**
   * @param x a double value
   * @return the Bessel function of the second kind,
   *  of order 1 of the argument.
   */
  static public double y1(double x) {

    if (x < 8.0) {
      double y = x * x;
      double ans1 = x * ( -0.4900604943e13 + y * (0.1275274390e13
                                                  +
                                                  y * ( -0.5153438139e11 + y * (0.7349264551e9
          + y * ( -0.4237922726e7 + y * 0.8511937935e4)))));
      double ans2 = 0.2499580570e14 + y * (0.4244419664e12
                                           +
                                           y * (0.3733650367e10 + y * (0.2245904002e8
          + y * (0.1020426050e6 + y * (0.3549632885e3 + y)))));
      return (ans1 / ans2) + 0.636619772 * (j1(x) * Math.log(x) - 1.0 / x);
    } 
    
    double z = 8.0 / x;
    double y = z * z;
    double xx = x - 2.356194491;
    double ans1 = 1.0 + y * (0.183105e-2 + y * ( -0.3516396496e-4
                                                +
                                                y * (0.2457520174e-5 + y * ( -0.240337019e-6))));
    double ans2 = 0.04687499995 + y * ( -0.2002690873e-3
                                       +
                                       y * (0.8449199096e-5 + y * ( -0.88228987e-6
        + y * 0.105787412e-6)));
    return Math.sqrt(0.636619772 / x) *
        (Math.sin(xx) * ans1 + z * Math.cos(xx) * ans2);    
  }

  /**
   * @param n integer order
   * @param x a double value
   * @return the Bessel function of the second kind,
   *    of order n of the argument.
   */
  static public double yn(int n, double x) {
    double by, bym, byp, tox;

    if (n == 0)return y0(x);
    if (n == 1)return y1(x);

    tox = 2.0 / x;
    by = y1(x);
    bym = y0(x);
    for (int j = 1; j < n; j++) {
      byp = j * tox * by - bym;
      bym = by;
      by = byp;
    }
    return by;
  }
}
