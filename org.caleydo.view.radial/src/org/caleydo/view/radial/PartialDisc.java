/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.radial;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.Tree;

/**
 * This class represents a partial disc that corresponds to a cluster node in a
 * hierarchy and provides methods for rendering this hierarchy (as radial
 * hierarchy visualization).
 * 
 * @author Christian Partl
 */
public class PartialDisc extends AHierarchyElement<PartialDisc> {

	private float fSize;
	private APDDrawingStrategy drawingStrategy;
	private AHierarchyElement<?> hierarchyElement;
	private PartialDisc pdLargestChild;

	private float fCurrentAngle;
	private float fCurrentStartAngle;
	private int iCurrentDepth;
	private float fCurrentWidth;
	private float fCurrentInnerRadius;
	private int iDrawingStrategyDepth;

	/**
	 * Constructor.
	 * 
	 * @param partialDiscTree
	 *            Tree that represents the hierarchy of partial discs. Instances
	 *            of partial disc have to be inserted externally into this tree.
	 * @param clusterNode
	 *            Cluster node that the partial disc shall correspond to.
	 */
	public PartialDisc(Tree<PartialDisc> partialDiscTree,
			AHierarchyElement<?> hierarchyElement, APDDrawingStrategy drawingStrategy) {

		super(partialDiscTree);
		// setNode(this);
		this.fSize = hierarchyElement.getSize();
		this.hierarchyElement = hierarchyElement;
		this.drawingStrategy = drawingStrategy;
		fCurrentStartAngle = 0;
		hierarchyDepth = -1;
	}

	/**
	 * Recursively calculates the largest child of each node in the sub-tree
	 * with the current element as root node.
	 */
	public void calculateLargestChildren() {

		if (!hasChildren()) {
			pdLargestChild = null;
		} else {
			ArrayList<PartialDisc> alChildren = tree.getChildren(this);

			float fMaxChildSize = 0.0f;
			if (alChildren != null) {
				for (PartialDisc pdChild : alChildren) {
					if (fMaxChildSize < pdChild.getSize()) {
						fMaxChildSize = pdChild.getSize();
						pdLargestChild = pdChild;
					}

					pdChild.calculateLargestChildren();
				}
			}
		}
	}

	/**
	 * Draws the radial hierarchy with the specified depth using the current
	 * element as root node. The root node will be drawn as circle.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fWidth
	 *            The width of each hierarchy level, i.e. the width of a partial
	 *            disc.
	 * @param iDepth
	 *            Specifies the number of hierarchy levels that should be drawn.
	 */
	public void drawHierarchyFull(GL2 gl, GLU glu, float fWidth, int iDepth) {

		setCurrentDisplayParameters(fWidth, fCurrentStartAngle, 360, 0, iDepth);

		if (iDepth <= 0)
			return;

		drawingStrategy.drawFullCircle(gl, glu, this);
		iDepth--;

		float fAnglePerSizeUnit = 360 / fSize;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fCurrentStartAngle, fWidth,
					fAnglePerSizeUnit, iDepth, false);
		}
	}

	/**
	 * Simulates the drawing of the radial hierarchy with the specified depth
	 * using the current element as root node, i.e. nothing is really drawn,
	 * just the properties of the partial discs (angle etc.) are set as if they
	 * were drawn. The root node is considered to be a circle.
	 * 
	 * @param fWidth
	 *            The width of each hierarchy level, i.e. the width of a partial
	 *            disc.
	 * @param iDepth
	 *            Specifies the number of hierarchy levels where drawing should
	 *            be simulated.
	 */
	public void simulateDrawHierarchyFull(float fWidth, int iDepth) {

		setCurrentDisplayParameters(fWidth, fCurrentStartAngle, 360, 0, iDepth);

		if (iDepth <= 0)
			return;
		iDepth--;

		float fAnglePerSizeUnit = 360 / fSize;

		if (iDepth > 0) {
			drawAllChildren(null, null, fWidth, fCurrentStartAngle, fWidth,
					fAnglePerSizeUnit, iDepth, true);
		}
	}

	/**
	 * Draws the radial hierarchy with the specified parameters using the
	 * current element as root node.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fWidth
	 *            Specifies the width of each hierarchy level, i.e. the width of
	 *            a partial disc.
	 * @param iDepth
	 *            Specifies the number of hierarchy levels that should be drawn.
	 * @param fStartAngle
	 *            Specifies the angle where to start drawing the root partial
	 *            disc.
	 * @param fAngle
	 *            Specifies the angle of the root partial disc.
	 * @param fInnerRadius
	 *            Specifies the inner radius of the root partial disc.
	 */
	public void drawHierarchyAngular(GL2 gl, GLU glu, float fWidth, int iDepth,
			float fStartAngle, float fAngle, float fInnerRadius) {

		fStartAngle = getValidAngle(fStartAngle);
		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);

		if (fAngle >= RadialHierarchyRenderStyle.PARTIAL_DISC_MIN_DISPLAYED_ANGLE) {
			drawingStrategy.drawPartialDisc(gl, glu, this);
		}
		iDepth--;

		float fAnglePerSizeUnit = fAngle / fSize;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fStartAngle, fInnerRadius + fWidth,
					fAnglePerSizeUnit, iDepth, false);
		}
	}

	/**
	 * Simulates the drawing of the radial hierarchy with the specified
	 * parameters using the current element as root node, i.e. nothing is really
	 * drawn, just the properties of the partial discs (angle etc.) are set as
	 * if they were drawn.
	 * 
	 * @param fWidth
	 *            Specifies the width of each hierarchy level, i.e. the width of
	 *            a partial disc.
	 * @param iDepth
	 *            Specifies the number of hierarchy levels where drawing should
	 *            be simulated.
	 * @param fStartAngle
	 *            Specifies the angle where to start the root partial disc.
	 * @param fAngle
	 *            Specifies the angle of the root partial disc.
	 * @param fInnerRadius
	 *            Specifies the inner radius of the root partial disc.
	 */
	public void simulateDrawHierarchyAngular(float fWidth, int iDepth, float fStartAngle,
			float fAngle, float fInnerRadius) {

		fStartAngle = getValidAngle(fStartAngle);
		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);
		iDepth--;

		float fAnglePerSizeUnit = fAngle / fSize;

		if (iDepth > 0) {
			drawAllChildren(null, null, fWidth, fStartAngle, fInnerRadius + fWidth,
					fAnglePerSizeUnit, iDepth, true);
		}
	}

	/**
	 * Draws the current partial disc according to the parameters and calls the
	 * method for drawing its children if the specified depth for drawing the
	 * hierarchy is not reached yet. * @param gl GL2 object that shall be used
	 * for drawing.
	 * 
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fWidth
	 *            Specifies the width of each hierarchy level, i.e. the width of
	 *            a partial disc.
	 * @param fStartAngle
	 *            Specifies the angle where to start drawing the root partial
	 *            disc.
	 * @param fInnerRadius
	 *            Specifies the inner radius of the root partial disc.
	 * @param fAnglePerSizeUnit
	 *            Specifies the angle of an element that has the size 1.
	 * @param iDepth
	 *            Specifies the number of hierarchy levels that should be drawn.
	 * @param bSimulation
	 *            Specifies whether the partial disc should be really drwan or
	 *            drawing should just be simulated.
	 * @return The angle with which the current partial disc was drawn.
	 */
	private float drawHierarchy(GL2 gl, GLU glu, float fWidth, float fStartAngle,
			float fInnerRadius, float fAnglePerSizeUnit, int iDepth, boolean bSimulation) {

		float fAngle = fSize * fAnglePerSizeUnit;
		fStartAngle = getValidAngle(fStartAngle);
		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);

		if (!bSimulation
				&& fAngle >= RadialHierarchyRenderStyle.PARTIAL_DISC_MIN_DISPLAYED_ANGLE) {
			drawingStrategy.drawPartialDisc(gl, glu, this);
		}

		iDepth--;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fStartAngle, fInnerRadius + fWidth,
					fAnglePerSizeUnit, iDepth, bSimulation);
		}
		return fAngle;
	}

	/**
	 * Tells all children of the current element (if any) to draw themselves.
	 * 
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fWidth
	 *            Specifies the width of each hierarchy level, i.e. the width of
	 *            a partial disc.
	 * @param fStartAngle
	 *            Specifies the angle where to start drawing the root partial
	 *            disc.
	 * @param fInnerRadius
	 *            Specifies the inner radius of the root partial disc.
	 * @param fAnglePerSizeUnit
	 *            Specifies the angle of an element that has the size 1.
	 * @param iDepth
	 *            Specifies the number of hierarchy levels that should be drawn.
	 * @param bSimulation
	 *            Specifies whether the partial disc should be really drwan or
	 *            drawing should just be simulated.
	 */
	private void drawAllChildren(GL2 gl, GLU glu, float fWidth, float fStartAngle,
			float fInnerRadius, float fAnglePerSizeUnit, int iDepth, boolean bSimulation) {

		float fChildStartAngle = fStartAngle;
		ArrayList<PartialDisc> alChildren = tree.getChildren(this);

		if (alChildren != null) {
			for (int i = 0; i < alChildren.size(); i++) {
				PartialDisc pdCurrentChild = alChildren.get(i);
				fChildStartAngle += pdCurrentChild.drawHierarchy(gl, glu, fWidth,
						fChildStartAngle, fInnerRadius, fAnglePerSizeUnit, iDepth,
						bSimulation);
			}
		}
	}

	/**
	 * Determines whether a child of the current element would be drawn or not.
	 * No child of the element is drawn if there is no child, the maximum depth
	 * for drawing the hierarchy is reached or all children are too small to be
	 * drawn.
	 * 
	 * @return True if a child would be drawn, false otherwise.
	 */
	public boolean isAChildDrawn() {

		if ((iCurrentDepth == 1) || (!hasChildren()) || (pdLargestChild == null)) {
			return false;
		}

		float fAnglePerSizeUnit = fCurrentAngle / fSize;

		float fChildAngle = pdLargestChild.getSize() * fAnglePerSizeUnit;
		if (fChildAngle >= RadialHierarchyRenderStyle.PARTIAL_DISC_MIN_DISPLAYED_ANGLE) {
			return true;
		}
		return false;
	}

	/**
	 * Returns an angle that lies between 0 and 360 degrees corresponding to the
	 * specified angle.
	 * 
	 * @param fAngle
	 *            Angle where the corresponding angle between 0 and 360 shall be
	 *            found.
	 * @return Angle between 0 and 360.
	 */
	private float getValidAngle(float fAngle) {
		while (fAngle > 360) {
			fAngle -= 360;
		}
		while (fAngle < 0) {
			fAngle += 360;
		}
		return fAngle;
	}

	/**
	 * Sets the properties of the current partial disc to the specified
	 * parameters.
	 * 
	 * @param fWidth
	 *            Width of the partial disc.
	 * @param fStartAngle
	 *            Angle where the partial disc starts.
	 * @param fAngle
	 *            Angle of the partial disc.
	 * @param fInnerRadius
	 *            Inner radius of the partial disc.
	 * @param iDepth
	 *            Depth of the hierarchy that is (theoretically) drawn from the
	 *            current element.
	 */
	private void setCurrentDisplayParameters(float fWidth, float fStartAngle,
			float fAngle, float fInnerRadius, int iDepth) {
		fCurrentAngle = fAngle;
		iCurrentDepth = Math.min(iDepth, getDepth());
		fCurrentInnerRadius = fInnerRadius;
		fCurrentStartAngle = fStartAngle;
		fCurrentWidth = fWidth;
	}

	/**
	 * @return ID of partial disc which is used for picking. This ID equals the
	 *         ID of the hierarchy data object the partial disc represents.
	 */
	public int getElementID() {
		return hierarchyElement.getID();
	}

	/**
	 * @return Size of the partial disc.
	 */
	@Override
	public float getSize() {
		return fSize;
	}

	/**
	 * Sets the size of the partial disc.
	 * 
	 * @param fSize
	 *            Value the size of the partial disc should be set to.
	 */
	public void setSize(float fSize) {
		this.fSize = fSize;
	}

	/**
	 * Sets the drawing strategy which shall be used for drawing the partial
	 * disc.
	 * 
	 * @param drawingStrategy
	 *            Drawing strategy which shall be used for drawing the partial
	 *            disc.
	 */
	public void setPDDrawingStrategy(APDDrawingStrategy drawingStrategy) {
		this.drawingStrategy = drawingStrategy;
		iDrawingStrategyDepth = 1;
	}

	/**
	 * Recursively sets the drawing strategy which shall be used for drawing the
	 * current partial disc and the elements of its sub-tree.
	 * 
	 * @param drawingStrategy
	 *            Drawing strategy which shall be used for drawing the partial
	 *            discs.
	 * @param iDepth
	 *            Depth of the sub-tree.
	 */
	public void setPDDrawingStrategyChildren(APDDrawingStrategy drawingStrategy,
			int iDepth) {
		this.drawingStrategy = drawingStrategy;
		iDrawingStrategyDepth = iDepth;
		iDepth--;
		ArrayList<PartialDisc> alChildren = tree.getChildren(this);

		if (iDepth > 0 && alChildren != null) {
			for (int i = 0; i < alChildren.size(); i++) {
				PartialDisc pdCurrentChild = alChildren.get(i);
				pdCurrentChild.setPDDrawingStrategyChildren(drawingStrategy, iDepth);
			}
		}
	}

	/**
	 * Recursively decorates the drawing strategy of the current partial disc
	 * and the elements of its sub-tree with copies of the specified decorator.
	 * 
	 * @param drawingStrategy
	 *            Drawing strategy which shall be used for drawing the partial
	 *            discs.
	 * @param iDepth
	 *            Depth of the sub-tree.
	 */
	public void decoratePDDrawingStrategyChildren(APDDrawingStrategyDecorator decorator,
			int iDepth) {
		APDDrawingStrategyDecorator myDecorator = decorator.clone();
		myDecorator.setDrawingStrategy(drawingStrategy);
		drawingStrategy = myDecorator;
		iDrawingStrategyDepth = iDepth;
		iDepth--;
		ArrayList<PartialDisc> alChildren = tree.getChildren(this);

		if (iDepth > 0 && alChildren != null) {
			for (int i = 0; i < alChildren.size(); i++) {
				PartialDisc pdCurrentChild = alChildren.get(i);
				pdCurrentChild.decoratePDDrawingStrategyChildren(decorator, iDepth);
			}
		}
	}

	/**
	 * @return Angle that has been used for drawing the partial disc.
	 */
	public float getCurrentAngle() {
		return fCurrentAngle;
	}

	/**
	 * @return Angle where the partial disc has been started drawing.
	 */
	public float getCurrentStartAngle() {
		return fCurrentStartAngle;
	}

	/**
	 * Sets the angle where the drawing of the partial disc starts.
	 * 
	 * @param fCurrentStartAngle
	 *            Angle where the partial disc starts
	 */
	public void setCurrentStartAngle(float fCurrentStartAngle) {
		this.fCurrentStartAngle = getValidAngle(fCurrentStartAngle);
	}

	/**
	 * @return The number of hierarchy levels that have been drawn starting from
	 *         the current element.
	 */
	public int getCurrentDepth() {
		return iCurrentDepth;
	}

	/**
	 * @return The width that has been used for drawing the partial disc.
	 */
	public float getCurrentWidth() {
		return fCurrentWidth;
	}

	/**
	 * @return The inner radius that has been used for drawing the partial disc.
	 */
	public float getCurrentInnerRadius() {
		return fCurrentInnerRadius;
	}

	public AHierarchyElement<?> getHierarchyData() {
		return hierarchyElement;
	}

	/**
	 * @return The number of hierarchy levels the method
	 *         setDrawingStragtegyChildren used to set the drawing strategy.
	 *         (This value is set to 1 by the method setDrawingStrategy.)
	 */
	public int getDrawingStrategyDepth() {
		return iDrawingStrategyDepth;
	}

	/**
	 * @return The largest child of the current element, or null if there is no
	 *         child. Note, that calculateLargestChildren must have been called
	 *         before.
	 */
	public PartialDisc getLargestChild() {
		return pdLargestChild;
	}

	public APDDrawingStrategy getDrawingStrategy() {
		return drawingStrategy;
	}

	@Override
	public int compareTo(PartialDisc disc) {

		return hierarchyElement.getComparableValue()
				- disc.hierarchyElement.getComparableValue();
	}

	/**
	 * Determines whether the partial disc is displayed given the specified
	 * parameters.
	 * 
	 * @param pdCurrentRootElement
	 *            Currently displayed root element.
	 * @param iDisplayedHierarchyDepth
	 *            Currently displayed hierarchy depth.
	 * @return True, if the partial disc is displayed, false otherwise.
	 */
	public boolean isCurrentlyDisplayed(PartialDisc pdCurrentRootElement,
			int iDisplayedHierarchyDepth) {
		if (pdCurrentRootElement == this)
			return true;
		int iParentPathLength = getParentPathLength(pdCurrentRootElement);
		if ((iParentPathLength >= iDisplayedHierarchyDepth) || (iParentPathLength == -1)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the first element that is visible on the path from the current
	 * partial disc along its parents to the specified root element.
	 * 
	 * @param pdCurrentRootElement
	 *            Current root element and destination of the path.
	 * @param iDisplayedHierarchyDepth
	 *            Currently displayed hierarchy depth.
	 * @return First visible element on the path from the current partial disc
	 *         to the root element, null if no such path exists.
	 */
	public PartialDisc getFirstVisibleElementOnParentPathToRoot(
			PartialDisc pdCurrentRootElement, int iDisplayedHierarchyDepth) {

		if (pdCurrentRootElement == this)
			return this;

		ArrayList<PartialDisc> alParentPath = getParentPath(pdCurrentRootElement);
		if (alParentPath == null)
			return null;
		if (alParentPath.size() >= iDisplayedHierarchyDepth)
			return alParentPath.get(alParentPath.size() - iDisplayedHierarchyDepth);
		return this;
	}

}
