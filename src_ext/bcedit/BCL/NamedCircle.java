// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NamedCircle.java

package bcedit.BCL;

import java.awt.Point;

// Referenced classes of package bcedit.BCL:
//            Circle

public class NamedCircle extends Circle
{

    public NamedCircle(int x, int y, int r, String s)
    {
        super(x, y, r);
        name = null;
        name = s;
    }

    public NamedCircle(int x, int y, int r)
    {
        super(x, y, r);
        name = null;
        name = new String("");
    }

    public NamedCircle()
    {
        name = null;
        name = new String("");
    }

    public NamedCircle(NamedCircle c)
    {
        name = null;
        x = c.x;
        y = c.y;
        r = c.r;
        name = new String(c.name);
    }

    public NamedCircle(Circle c, String s)
    {
        name = null;
        x = c.x;
        y = c.y;
        r = c.r;
        name = s;
    }

    public NamedCircle(Circle c)
    {
        name = null;
        x = c.x;
        y = c.y;
        r = c.r;
        name = new String("");
    }

    public NamedCircle(Point p, int radius, String s)
    {
        super(p, radius);
        name = null;
        name = s;
    }

    public NamedCircle(Point p, int radius)
    {
        super(p, radius);
        name = null;
        name = new String("");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String s)
    {
        name = s;
    }

    private String name;
}
