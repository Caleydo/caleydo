// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PCircle.java

package bcedit;

import bcedit.BCL.Circle;
import java.awt.Image;

public class PCircle extends Circle
{

    public PCircle()
    {
        img = new Image[3];
        name = null;
        img[0] = null;
        img[1] = null;
        img[2] = null;
    }

    public PCircle(int x, int y, int r, Image i0, Image i1, Image i2)
    {
        super(x, y, r);
        img = new Image[3];
        name = null;
        img[0] = i0;
        img[1] = i1;
        img[2] = i2;
    }

    public PCircle(int x, int y, int r, Image i0, Image i1, Image i2, String name)
    {
        super(x, y, r);
        img = new Image[3];
        this.name = null;
        img[0] = i0;
        img[1] = i1;
        img[2] = i2;
        setName(name);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String _name)
    {
        name = _name;
    }

    public Image img[];
    public String name;
}
