// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MyButton.java

package bcedit;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class MyButton extends JButton
{

    public MyButton(String name)
    {
        super(name);
        setBackground(new Color(0x4a7597));
        setForeground(new Color(0xffd800));
        setBorder(BorderFactory.createRaisedBevelBorder());
        setRolloverEnabled(true);
    }

    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        d.width += 4;
        d.height += 4;
        return d;
    }
}
