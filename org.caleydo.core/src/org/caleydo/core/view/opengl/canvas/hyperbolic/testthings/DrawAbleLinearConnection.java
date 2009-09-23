// package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;
//
// import gleem.linalg.Vec3f;
//
// import javax.media.opengl.GL;
//
// import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
// import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
// import
// org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleHyperbolicLayoutConnector;
//
// public final class DrawAbleLinearConnection
// extends DrawAbleHyperbolicLayoutConnector {
//
// public DrawAbleLinearConnection(IDrawAbleNode iNodeA, IDrawAbleNode iNodeB) {
// super(iNodeA, iNodeB);
// }
//
// @Override
// public void draw(GL gl, boolean bHighlight) {
// if (bHighlight) {
// gl.glColor4fv(HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME_HL, 0);
// gl.glLineWidth(HyperbolicRenderStyle.DA_LINEAR_CONNECTION_THICKNESS_HL);
// }
// else {
// gl.glColor4fv(HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME, 0);
// gl.glLineWidth(HyperbolicRenderStyle.DA_LINEAR_CONNECTION_THICKNESS);
// }
//
// gl.glBegin(gl.GL_LINE);
// for (Vec3f point : findClosestCorrespondendingPoints())
// gl.glVertex3f(point.x(), point.y(), point.z());
// gl.glEnd();
// }
// }
//
// // this.fRed = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[0];
// // this.fGreen = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[1];
// // this.fBlue = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[2];
// // this.fAlpha = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_ALPHA;
//
// // TODO: maybe define thickness in renderstyle
// // @Override
// // public void drawConnectionFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness) {
// // gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
// // gl.glLineWidth(fThickness);
// //
// // for (int i = 0; i < lPoints.size()-1; i++){
// // gl.glBegin(GL.GL_LINE);
// // gl.glVertex3f(lPoints.get(i).x(), lPoints.get(i).y(), lPoints.get(i).z());
// // gl.glVertex3f(lPoints.get(i+1).x(), lPoints.get(i+1).y(), lPoints.get(i+1).z());
// // gl.glEnd();
// // }
// // }
//
// // @Override
// // public void setHighlight(boolean b) {
// // this.bHighlight = b;
// // if (b) {
// // this.fRed = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME_HL[0];
// // this.fGreen = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME_HL[1];
// // this.fBlue = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME_HL[2];
// // this.fAlpha = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_ALPHA_HL;
// // this.fThickness = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_THICKNESS_HL;
// // }
// // else {
// // this.fRed = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[0];
// // this.fGreen = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[1];
// // this.fBlue = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[2];
// // this.fAlpha = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_ALPHA;
// // this.fThickness = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_THICKNESS;
// // }
// // }

