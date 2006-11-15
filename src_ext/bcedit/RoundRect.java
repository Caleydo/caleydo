// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RoundRect.java

package bcedit;

import java.awt.Dimension;
import java.awt.Rectangle;

public class RoundRect
{

    public RoundRect()
    {
        x = y = width = height = arcWidth = arcHeight = 0;
    }

    public RoundRect(RoundRect r)
    {
        x = r.x;
        y = r.y;
        width = r.width;
        height = r.height;
        arcWidth = r.arcWidth;
        arcHeight = r.arcHeight;
    }

    public RoundRect(Rectangle r, Dimension arc)
    {
        x = r.x;
        y = r.y;
        width = r.width;
        height = r.height;
        arcWidth = arc.width;
        arcHeight = arc.height;
    }

    public RoundRect(Rectangle r, int arcWidth, int arcHeight)
    {
        x = r.x;
        y = r.y;
        width = r.width;
        height = r.height;
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
    }

    public int x;
    public int y;
    public int width;
    public int height;
    public int arcWidth;
    public int arcHeight;
}
