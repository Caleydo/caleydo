package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;

/**
 * 
 * @author Oliver Pimas
 * @version 0.1 (09-07-20)
 * 
 * NURBSCurve implements a simple NURBS (Non-uniform rational B-Spline) curve without weights.
 * This class can be used to calculate a curve by a given set of control points.
 *
 */

public class NURBSCurve {
	
	protected Vec3f[] controlPoints;
	protected int numberOfSegments;
	protected int numberOfCurvePoints; // there are numberOfSegments + 1 curve points (vertices)
	protected Vec3f[] curvePoints;
	protected float[] knots; // knot vector, there are (n + d + 1) knot values
	protected int n; // parameter n, there are (n+1) control points [n = # CPs - 1]
	protected int d; // degree parameter [2 <= d <= (n+1)]
	protected float u_min;
	protected float u_max;
	protected float u;
	protected float step_length;
	
	
	/**
	 * Constructor.
	 * 
	 * @param sourcePoint 
	 * 					Defines the first vertex of the spline (control point 1).
	 * @param bundlingPoint
	 * 					Defines the middle of the spine (control point 2).
	 * @param destPoint
	 * 					Defines the last vertex of the spline (control point 3).
	 * @param numberOfSegments
	 * 					Defines the subintervals of the spline.
	 * 					Note that for n subintervals there are n+3 curve points.
	 * 					The begin of the curve, the end of the curve and n+1 vertices
	 * 					connecting the n segments. 
	 */
	public NURBSCurve(Vec3f sourcePoint, Vec3f bundlingPoint, Vec3f destPoint, int numberOfSegments) {
		
		Vec3f[] controlPoints = {sourcePoint, bundlingPoint, destPoint};
		this.controlPoints = controlPoints;
		
		this.n = 2; // 3 control points
		this.d = 3;
		this.numberOfCurvePoints = numberOfSegments + 3; // n segments -> n+1 vertices + src & dest
		this.numberOfSegments = numberOfSegments;
		
		float[] knots = {0.0f, 0.0f, 1.0f, 2.0f, 3.0f, 3.0f};
		this.knots = knots;
		
		 this.u_min = (float) (this.knots[this.d-1]);
		 this.u_max = (float) (this.knots[this.n+1]);
		 this.u = 0.0f;
		 this.step_length = (float) (this.knots[this.n+1] - this.knots[this.d-1]) / (float) (this.numberOfSegments);
		 
		
		Vec3f[] curvePoints = new Vec3f[this.numberOfCurvePoints];
		curvePoints[0] = sourcePoint;
		curvePoints[this.numberOfCurvePoints - 1] = destPoint;
		this.curvePoints = curvePoints.clone();
		
		this.evaluateCurve();
	}

	
	/**
	 * Constructor.
	 * 
	 * @param controlPoints
	 * 					A set of control points of which the spline is generated.
	 * @param numberOfSegments
	 * 					Defines the subintervals of the spline.
	 * 					Note that for n subintervals there are n+3 curve points.
	 * 					The begin of the curve, the end of the curve and n+1 vertices
	 * 					connecting the n segments. 
	 */
	
	public NURBSCurve(Vec3f[] controlPoints, int numberOfSegments) {
		
		this.controlPoints = controlPoints;
		
		this.n = (controlPoints.length - 1);
		this.d = 3;
		this.numberOfCurvePoints = numberOfSegments + 3; // n segments -> n+1 vertices + src & dest
		this.numberOfSegments = numberOfSegments;
		
		float[] knots = {0.0f, 0.0f, 1.0f, 2.0f, 3.0f, 3.0f};
		this.knots = knots;
		
		 this.u_min = (float) (this.knots[this.d-1]);
		 this.u_max = (float) (this.knots[this.n+1]);
		 this.u = 0.0f;
		 this.step_length = (float) (this.knots[this.n+1] - this.knots[this.d-1]) / (float) (this.numberOfSegments);
		 
		
		Vec3f[] curvePoints = new Vec3f[this.numberOfCurvePoints];
		curvePoints[0] = controlPoints[0];
		curvePoints[this.numberOfCurvePoints - 1] = controlPoints[controlPoints.length - 1];
		this.curvePoints = curvePoints.clone();
		
		this.evaluateCurve();
	}
	
	
	/**
	 * Evaluates the curve and calculates the curve points.
	 * After this function has been called, the points are hold
	 * in the variable curvePoints and can be obtained by
	 * the method getCurvePoints().
	 */
	public void evaluateCurve() {
        
        for(int step = 0; step <= this.numberOfSegments; step++)
        {
          Vec3f point = new Vec3f();
          for(int k = 0; k <= n; k++)
          {
            this.u = this.u_min + this.step_length * step;
            if(step == this.numberOfSegments) // because of rounding errors we call the last blending function with exact u_max to avoid u outside the definition
              this.u = this.u_max;
            point = point.addScaled( this.coxDeBoor(k, this.d), this.controlPoints[k] );
          }
          this.curvePoints[step+1] = point;
          //System.out.println("added point " + this.curvePoints[step].x() + ", " + this.curvePoints[step].y() + ", " + this.curvePoints[step].z());
        }
        //System.out.println("----------------");
	}
	
	
	/**
	 * The Cox-deBoor recursive formula is used as blending function.
	 * @param k
	 * 			Defines the control variable representing the current control point.
	 * @param u
	 * 			The B-Spline curve equation parameter.
	 * @return
	 * 			The result of the Cox-de Boor recursive formula as float value.
	 */
	protected float coxDeBoor(int k, int d) {
	  int k_max = this.n;
	  float result = 0.0f;

	  if(d == 1)
	    if(this.knots[k] <= this.u && this.u < this.knots[k+1] && k <= k_max)
	      return 1.0f;
	    else if(k == k_max && this.u == this.u_max)
	      return 1.0f;
	    else
	      return 0.0f;

	  float factor1 = this.blendingDivision( (float) (this.u - this.knots[k]), (float) (this.knots[k+d-1] - this.knots[k]) );
	  result = factor1 * this.coxDeBoor(k, (d-1));
	  float factor2 = this.blendingDivision( (float) (this.knots[k+d] - this.u), (float) (this.knots[k+d] - this.knots[k+1]) );
	  result += factor2 * this.coxDeBoor((k+1), (d-1));

	  return result;	
	}
	
	
	/**
	 * Division for the blending function (coxDeBoor).
	 * Needed because it is possible to choose the elements of the knot vector so that some
	 * denominators in the Cox-deBoor calculations evaluate to 0. These terms are by
	 * definition evaluated to 0.
	 * 
	 * @param dividend
	 * @param divisor
	 * 
	 */
	float blendingDivision(float dividend, float divisor) {
	  if(divisor == 0.0f)
	    return 0.0f;
	  return (dividend / divisor);
	}
	
	
	/**
	 * Returns an array with the curvePoints.
	 */
	public Vec3f[] getCurvePoints() {
		return this.curvePoints;
	}
	
	
	/**
	 * Returns the number of curve points.
	 */
	public int getNumberOfCurvePoints() {
		return this.numberOfCurvePoints;
	}
	
	
	/**
	 * Returns the given curve point at index i.
	 * @param i
	 * 			Index of the curve point that should be returned
	 * @return
	 * 			A single curve point at index i [Vec3f] or null
	 * 			if the index is out of bounds.
	 */
	public Vec3f getCurvePoint(int i) {
		if (i >= this.numberOfCurvePoints || i < 0) {
			System.err.println("Index out of bounds (requested #" + i + ", but there are only " + this.numberOfCurvePoints + " points).");
			return null;
		}
		return this.curvePoints[i];
	}

}
