// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NamedRectangle.java

package bcedit.BCL;

import java.awt.*;

public class NamedRectangle extends Rectangle
{

    public NamedRectangle()
    {
        name = null;
        name = new String("");
    }

    public NamedRectangle(Dimension d)
    {
        super(d);
        name = null;
        name = new String("");
    }

    public NamedRectangle(int width, int height)
    {
        super(width, height);
        name = null;
        name = new String("");
    }

    public NamedRectangle(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        name = null;
        name = new String("");
    }

    public NamedRectangle(Point p)
    {
        super(p);
        name = null;
        name = new String("");
    }

    public NamedRectangle(Point p, Dimension d)
    {
        super(p, d);
        name = null;
        name = new String("");
    }

    public NamedRectangle(Rectangle r)
    {
        super(r);
        name = null;
        name = new String("");
    }

    public NamedRectangle(String name)
    {
        this.name = null;
        this.name = name;
    }

    public NamedRectangle(Dimension d, String name)
    {
        super(d);
        this.name = null;
        this.name = name;
    }

    public NamedRectangle(int width, int height, String name)
    {
        super(width, height);
        this.name = null;
        this.name = name;
    }

    public NamedRectangle(int x, int y, int width, int height, String name)
    {
        super(x, y, width, height);
        this.name = null;
        this.name = name;
    }

    public NamedRectangle(Point p, String name)
    {
        super(p);
        this.name = null;
        this.name = name;
    }

    public NamedRectangle(Point p, Dimension d, String name)
    {
        super(p, d);
        this.name = null;
        this.name = name;
    }

    public NamedRectangle(Rectangle r, String name)
    {
        super(r);
        this.name = null;
        this.name = name;
    }

    public NamedRectangle(NamedRectangle r)
    {
        super(r);
        name = null;
        name = r.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    private String name;
}
