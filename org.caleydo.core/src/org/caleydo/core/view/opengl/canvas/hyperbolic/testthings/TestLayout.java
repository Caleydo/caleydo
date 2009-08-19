package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.DefaultNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.Layouter;

public class TestLayout
	extends Layouter {

	Tree<DefaultNode> testTree;
	float fYBorderSpace = 7.0f;
	float fXBorderSpace = 7.0f;
	int iDebug = 0;

	public TestLayout(GL gl, IViewFrustum frustum, Tree tree) {
		super(gl, frustum);

		testTree = tree;
	}

	//TODO: DELETE AS SOON AS POSSIBLE!!!
	
	public void drawGraph(GL gl) {
		int iDeph = 1;//testTree.getDeph();
		DefaultNode root = testTree.getRoot();
		// float xRootPixel = this.fWidth/2.0f;
		// float yRootPixel = this.fHight - (this.fHight/100*fBorderSpace);
		// root.setXCoord(this.fWidth/2.0f);

		root.setYCoord(this.fHight - (this.fHight / 100 * fYBorderSpace));
		root.setLeftBorderOfXCoord(fLeftBorder + this.fWidth / 100 * fXBorderSpace);
		root.setRightBorderOfXCoord(fRightBorder - this.fWidth / 100 * fXBorderSpace);
		root.setXCoord(this.fWidth / 2);

		drawVertex(gl, root);

		// increment for building layers of the tree
		float fStep = (this.fHight - 2 * (this.fHight / 100 * fYBorderSpace)) / iDeph;

		recursiveTreeBuilder(gl, root, 0, fStep, iDeph);

		
		// ArrayList<DefaultNode> childs = testTree.getChildren(root);
		// //int numberOfRootChilds = testTree.getNumberOfChildren(root);
		//		
		//		
		// for(DefaultNode tempNode : childs)
		// {
		// recursiveTreeBuilder(gl, tempNode, 0, fStep, iDeph);
		//			
		// }

	}

	// private int getNumberOfChildren(DefaultNode node, Tree tree)
	// {
	// return tree.getNumberOfChildren(node);
	// }

	private void drawVertex(GL gl, DefaultNode node) {
		gl.glPointSize(10.0f);
		gl.glColor4f(0, 0, 1, 1);
		gl.glBegin(GL.GL_POINTS);

		System.out.println("XCoord " + node.getXCoord());
		gl.glVertex3f(node.getXCoord(), node.getYCoord(), 0.0f);

		gl.glEnd();
	}

	private void drawLine(GL gl, DefaultNode node, DefaultNode parentNode) {

		// gl.glPointSize(10.0f);
		// gl.glColor4f(0,0,1,1);
		gl.glBegin(GL.GL_LINE);

		gl.glVertex3f(node.getXCoord(), node.getYCoord(), 0.0f);
		gl.glVertex3f(parentNode.getXCoord(), parentNode.getYCoord(), 0.0f);

		gl.glEnd();
	}

	private void recursiveTreeBuilder(GL gl, DefaultNode node, int iLayer, float fStep, int iDeph) {
		// for(int i = 0; i <= iDeph; i++)
		// if(testTree.hasChildren(node))
		if (iLayer != iDeph) {

			ArrayList<DefaultNode> childs = testTree.getChildren(node);
			// int iNumberOfNodeChilds = testTree.getNumberOfChildren(node);

			// float fYCoord = this.fHight -(this.fHight/100*fYBorderSpace)*fStep;
			float fYCoord = node.getYCoord() - fStep;

			float iSpaceCount = 1.0f;

			for (DefaultNode tempNode : childs) {

				recursiveTreeBuilder(gl, tempNode, iLayer + 1, fStep * (iLayer + 1), iDeph);

				tempNode.setYCoord(fYCoord);

				calulateXCoordValues(tempNode, iSpaceCount);
				iDebug++;

				gl.glLineWidth(2);
				drawVertex(gl, tempNode);
				drawLine(gl, tempNode, node);
				// System.out.println("iDebug" +iDebug);
				iSpaceCount++;
			}
		}
		else {
			System.out.println("Deph reached, do nothing!");
		}

	}

	public void calulateXCoordValues(DefaultNode node, float fCount) {
		DefaultNode parent = testTree.getParent(node);
		if (parent != null) {
			ArrayList<DefaultNode> siblings = testTree.getChildren(parent);
			float fNumberOfSiblings = siblings.size();
			float fSpaceForNodes = parent.getRightBorderOfXCoord() - parent.getLeftBorderOfXCoord();

			float fNewNodeSpace = fSpaceForNodes / fNumberOfSiblings;

			node.setRightBorderOfXCoord((fNewNodeSpace * fCount) + parent.getLeftBorderOfXCoord());

			System.out.println("LeftBorder " + fCount + " " + parent.getLeftBorderOfXCoord());
			System.out.println("RightBorder " + fCount + " " + parent.getRightBorderOfXCoord());

			node.setLeftBorderOfXCoord(node.getRightBorderOfXCoord() - fNewNodeSpace);

			node.setXCoord(node.getLeftBorderOfXCoord()
				+ ((node.getRightBorderOfXCoord() - node.getLeftBorderOfXCoord()) / 2));
		}
		else {
			// i am root
			// node.setLeftBorderOfXCoord(fLeftBorder + fXBorderSpace);
			// node.setRightBorderOfXCoord(fRightBorder - fXBorderSpace);
		}
	}
}
