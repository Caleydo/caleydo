/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998-2003 Kenneth B. Russell (kbrussel@alum.mit.edu)
 *
 * Copying, distribution and use of this software in source and binary
 * forms, with or without modification, is permitted provided that the
 * following conditions are met:
 *
 * Distributions of source code must reproduce the copyright notice,
 * this list of conditions and the following disclaimer in the source
 * code header files; and Distributions of binary code must reproduce
 * the copyright notice, this list of conditions and the following
 * disclaimer in the documentation, Read me file, license file and/or
 * other materials provided with the software distribution.
 *
 * The names of Sun Microsystems, Inc. ("Sun") and/or the copyright
 * holder may not be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," WITHOUT A WARRANTY OF ANY
 * KIND. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, NON-INTERFERENCE, ACCURACY OF
 * INFORMATIONAL CONTENT OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. THE
 * COPYRIGHT HOLDER, SUN AND SUN'S LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL THE
 * COPYRIGHT HOLDER, SUN OR SUN'S LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES. YOU ACKNOWLEDGE THAT THIS SOFTWARE IS NOT
 * DESIGNED, LICENSED OR INTENDED FOR USE IN THE DESIGN, CONSTRUCTION,
 * OPERATION OR MAINTENANCE OF ANY NUCLEAR FACILITY. THE COPYRIGHT
 * HOLDER, SUN AND SUN'S LICENSORS DISCLAIM ANY EXPRESS OR IMPLIED
 * WARRANTY OF FITNESS FOR SUCH USES.
 */

package gleem;

import java.util.*;

import gleem.linalg.*;

/** A cube of width, height, and depth 2, centered about the origin
    and aligned with the X, Y, and Z axes. */

public class ManipPartCube extends ManipPartTriBased {
  private static final Vec3f[] vertices = {
    // Front side
    new Vec3f(-1, 1, 1),
    new Vec3f(-1, -1, 1),
    new Vec3f(1, -1, 1),
    new Vec3f(1, 1, 1),
    // Back side
    new Vec3f(-1, 1, -1),
    new Vec3f(-1, -1, -1),
    new Vec3f(1, -1, -1),
    new Vec3f(1, 1, -1),
  };

  private static final int[] vertexIndices = {
    // Front face
    0, 1, 2,
    0, 2, 3,
    // Right face
    3, 2, 6,
    3, 6, 7,
    // Back face
    7, 6, 5,
    7, 5, 4,
    // Left face
    4, 5, 1,
    4, 1, 0,
    // Top face
    4, 0, 3,
    4, 3, 7,
    // Bottom face
    1, 5, 6,
    1, 6, 2
  };

  private static Vec3f[] normals  = null;
  private static int[] normalIndices = null;

  public ManipPartCube() {
    super();

    if (normals == null) {
      NormalCalc.NormalInfo normInfo =
        NormalCalc.computeFacetedNormals(vertices, vertexIndices, true);
      normals = normInfo.normals;
      normalIndices = normInfo.normalIndices;
    }

    setVertices(vertices);
    setVertexIndices(vertexIndices);
    setNormals(normals);
    setNormalIndices(normalIndices);
  }
}
