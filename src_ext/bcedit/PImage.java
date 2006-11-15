// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PImage.java

package bcedit;

import java.awt.Image;

public class PImage
{

    public PImage()
    {
        img = new Image[3];
        x = 0;
        y = 0;
        img[0] = null;
        img[1] = null;
        img[2] = null;
    }

    public PImage(int x, int y, Image i0, Image i1, Image i2)
    {
        img = new Image[3];
        this.x = x;
        this.y = y;
        img[0] = i0;
        img[1] = i1;
        img[2] = i2;
    }

    public PImage(int x, int y, Image i0, Image i1, Image i2, String name)
    {
        img = new Image[3];
        this.x = x;
        this.y = y;
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

    public int x;
    public int y;
    public Image img[];
    public String name;
}
