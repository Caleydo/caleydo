package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.htcalculation;

class HTCoordE {

    float x = 0.0f; // x coord
    float y = 0.0f; // y coord
//    float x = 2.0; // x coord
//    float y = 2.0; // y coord


    HTCoordE() {}

    
    HTCoordE(HTCoordE z) {
        this.copy(z);
    }


    HTCoordE(float x, float y) {
        this.x = x;
        this.y = y;
    }


 
    void copy(HTCoordE z) {
        this.x = z.x;
        this.y = z.y;
    }


 
    boolean isValid() {
        return (this.d2() < 1.0);
    }
 

  
    void multiply(HTCoordE z) {
        float tx = x;
        float ty = y;
        x = (tx * z.x) - (ty * z.y);
        y = (tx * z.y) + (ty * z.x);
    }
    

    void divide(HTCoordE z) {
        float d = z.d2();
        float tx = x;
        float ty = y;
        x = ((tx * z.x) + (ty * z.y)) / d;
        y = ((ty * z.x) - (tx * z.y)) / d;
    }

 
    void sub(HTCoordE a, HTCoordE b) {
        x = a.x - b.x;
        y = a.y - b.y;
    }


    float arg() {
        float a = (float)Math.atan(y / x);
        if (x < 0) {
            a += (float)Math.PI;
        } else if (y < 0) {
            a += 2 * (float)Math.PI;
        }
        return a;
    }


    float d2() {
        return (x * x) + (y * y);
    }

 
    float d() {
        return (float)Math.sqrt(d2());
    }


    float d(HTCoordE p) {
        return (float)Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
    }

 
    void translate(HTCoordE t) {
        // z = (z + t) / (1 + z * conj(t))
        
        // first the denominator
        float denX = (x * t.x) + (y * t.y) + 1;
        float denY = (y * t.x) - (x * t.y) ;    
        float dd   = (denX * denX) + (denY * denY);

        // and the numerator
        float numX = x + t.x;
        float numY = y + t.y;

        // then the division (bell)
        x = ((numX * denX) + (numY * denY)) / dd;
        y = ((numY * denX) - (numX * denY)) / dd;
    }

  

}

