package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.datagraph.IDataGraphNode;

public class ConnectionBandCreatorFactory {

	private ConnectionBandCreatorFactory() {

	}

	public static AEdgeBandRenderer getConnectionBandCreator(
			IDataGraphNode node1, IDataGraphNode node2,
			PixelGLConverter pixelGLConverter, ViewFrustum viewFrustum) {
		Point2D position1 = node1.getPosition();
		Point2D position2 = node2.getPosition();

		float deltaX = (float) (position1.getX() - position2.getX());
		float deltaY = (float) (position1.getY() - position2.getY());

		// Pair<Point2D, Point2D> anchorPoints1;
		// Pair<Point2D, Point2D> anchorPoints2;
		//
		// float offset1 = 0;
		// boolean isOffsetHorizontal = false;

		if (deltaX < 0) {
			if (deltaY < 0) {
				// -2
				// 1-

				return new LeftDownRightUpNodeRelationBandCreator(node1, node2,
						pixelGLConverter, viewFrustum);
				//
				// float spacingX = (float) ((position2.getX() -
				// node2.getWidth() / 2.0f) - (position1
				// .getX() + node1.getWidth() / 2.0f));
				// float spacingY = (float) ((position2.getY() -
				// node2.getHeight() / 2.0f) - (position1
				// .getY() + node1.getHeight() / 2.0f));
				// if (spacingX > spacingY) {
				// anchorPoints1 = node1.getRightAnchorPoints();
				// anchorPoints2 = node2.getLeftAnchorPoints();
				// offset1 = 0.3f * spacingX;
				// isOffsetHorizontal = true;
				// } else {
				// anchorPoints1 = node1.getTopAnchorPoints();
				// anchorPoints2 = node2.getBottomAnchorPoints();
				// offset1 = 0.3f * spacingY;
				// isOffsetHorizontal = false;
				// }
			} else {
				// 1-
				// -2
				return new LeftUpRightDownNodeRelationBandCreator(node1, node2,
						pixelGLConverter, viewFrustum);
				// float spacingX = (float) ((position2.getX() -
				// node2.getWidth() / 2.0f) - (position1
				// .getX() + node1.getWidth() / 2.0f));
				// float spacingY = (float) ((position1.getY() -
				// node1.getHeight() / 2.0f) - (position2
				// .getY() + node2.getHeight() / 2.0f));
				// if (spacingX > spacingY) {
				// anchorPoints1 = node1.getRightAnchorPoints();
				// anchorPoints2 = node2.getLeftAnchorPoints();
				// offset1 = 0.3f * (spacingX);
				// isOffsetHorizontal = true;
				// } else {
				// anchorPoints1 = node1.getBottomAnchorPoints();
				// anchorPoints2 = node2.getTopAnchorPoints();
				// offset1 = -0.3f * spacingY;
				// isOffsetHorizontal = false;
				// }
			}
		} else {
			if (deltaY < 0) {
				// 2-
				// -1
				return new LeftUpRightDownNodeRelationBandCreator(node2, node1,
						pixelGLConverter, viewFrustum);
				// float spacingX = (float) ((position1.getX() -
				// node1.getWidth() / 2.0f) - (position2
				// .getX() + node2.getWidth() / 2.0f));
				// float spacingY = (float) ((position2.getY() -
				// node2.getHeight() / 2.0f) - (position1
				// .getY() + node1.getHeight() / 2.0f));
				// if (spacingX > spacingY) {
				// anchorPoints1 = node1.getLeftAnchorPoints();
				// anchorPoints2 = node2.getRightAnchorPoints();
				// offset1 = -0.3f * (spacingX);
				// isOffsetHorizontal = true;
				// } else {
				// anchorPoints1 = node1.getTopAnchorPoints();
				// anchorPoints2 = node2.getBottomAnchorPoints();
				// offset1 = 0.3f * spacingY;
				// isOffsetHorizontal = false;
				// }
			} else {
				// -1
				// 2-
				return new LeftDownRightUpNodeRelationBandCreator(node2, node1,
						pixelGLConverter, viewFrustum);
				// float spacingX = (float) ((position1.getX() -
				// node1.getWidth() / 2.0f) - (position2
				// .getX() + node2.getWidth() / 2.0f));
				// float spacingY = (float) ((position1.getY() -
				// node1.getHeight() / 2.0f) - (position2
				// .getY() + node2.getHeight() / 2.0f));
				// if (spacingX > spacingY) {
				// anchorPoints1 = node1.getLeftAnchorPoints();
				// anchorPoints2 = node2.getRightAnchorPoints();
				// offset1 = -0.3f * (spacingX);
				// isOffsetHorizontal = true;
				// } else {
				// anchorPoints1 = node1.getBottomAnchorPoints();
				// anchorPoints2 = node2.getTopAnchorPoints();
				// offset1 = -0.3f * spacingY;
				// isOffsetHorizontal = false;
				// }
			}
		}
	}

}
