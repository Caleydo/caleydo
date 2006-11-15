// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Circle.java

package bcedit.BCL;

import java.awt.Point;

public class Circle
{

    public Circle(int x, int y, int r)
    {
        this.x = 0;
        this.y = 0;
        this.r = 0;
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public Circle()
    {
        x = 0;
        y = 0;
        r = 0;
        x = y = r = 0;
    }

    public Circle(Circle c)
    {
        x = 0;
        y = 0;
        r = 0;
        x = c.x;
        y = c.y;
        r = c.r;
    }

    public Circle(Point p, int radius)
    {
        x = 0;
        y = 0;
        r = 0;
        x = p.x;
        y = p.y;
        r = radius;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getRadius()
    {
        return r;
    }

    public void setRadius(int r)
    {
        this.r = r;
    }

    public void setPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setPoint(Point c)
    {
        x = c.x;
        y = c.y;
    }

    public boolean contains(Point c)
    {
        int x1 = (x - c.x) * (x - c.x);
        int y1 = (y - c.y) * (y - c.y);
        int r1 = r * r;
        return r1 >= x1 + y1;
    }

    public boolean contains(int x, int y)
    {
        int x1 = (this.x - x) * (this.x - x);
        int y1 = (this.y - y) * (this.y - y);
        int r1 = r * r;
        return r1 >= x1 + y1;
    }

    public boolean isTouching(Circle c)
    {
        double r1 = r + c.r;
        int x1 = x - c.x;
        int y1 = y - c.y;
        r1 *= r1;
        double d1 = x1 * x1 + y1 * y1;
        return d1 <= r1;
    }

    public static final boolean contains(Circle c, Point p)
    {
        int x1 = (c.x - p.x) * (c.x - p.x);
        int y1 = (c.y - p.y) * (c.y - p.y);
        int r1 = c.r * c.r;
        return r1 >= x1 + y1;
    }

    public static final boolean contains(Circle c, int x, int y)
    {
        int x1 = (c.x - x) * (c.x - x);
        int y1 = (c.y - y) * (c.y - y);
        int r1 = c.r * c.r;
        return r1 >= x1 + y1;
    }

    public static final boolean contains(int cx, int cy, int cr, int x, int y)
    {
        int x1 = (cx - x) * (cx - x);
        int y1 = (cy - y) * (cy - y);
        int r1 = cr * cr;
        return r1 >= x1 + y1;
    }

    public static final boolean contains(Point c, int cr, int x, int y)
    {
        int x1 = (c.x - x) * (c.x - x);
        int y1 = (c.y - y) * (c.y - y);
        int r1 = cr * cr;
        return r1 >= x1 + y1;
    }

    public static final boolean contains(Point c, int cr, Point test)
    {
        int x1 = (c.x - test.x) * (c.x - test.x);
        int y1 = (c.y - test.y) * (c.y - test.y);
        int r1 = cr * cr;
        return r1 >= x1 + y1;
    }

    public static final boolean contains(int cx, int cy, int cr, Point test)
    {
        int x1 = (cx - test.x) * (cx - test.x);
        int y1 = (cy - test.y) * (cy - test.y);
        int r1 = cr * cr;
        return r1 >= x1 + y1;
    }

    public boolean isEqual(Circle c)
    {
        return x == c.x && y == c.y && r == c.r;
    }

    public boolean isEqual(int x, int y, int r)
    {
        return this.x == x && this.y == y && this.r == r;
    }

    public boolean isEqual(Point p, int r)
    {
        return x == p.x && y == p.y && this.r == r;
    }

    public int x;
    public int y;
    public int r;
}
