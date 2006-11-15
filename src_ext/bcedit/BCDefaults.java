// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCDefaults.java

package bcedit;

import java.awt.*;
import javax.swing.JButton;

// Referenced classes of package bcedit:
//            BCSettings, BCImages

public class BCDefaults
{

    public BCDefaults(Toolkit toolkit)
    {
        curDefault = toolkit.createCustomCursor(BCImages.getImage("curDefault.png"), new Point(0, 0), "curDefault");
        curCopy = toolkit.createCustomCursor(BCImages.getImage("curCopy.png"), new Point(0, 0), "curCopy");
        curDrop = toolkit.createCustomCursor(BCImages.getImage("curDrop.png"), new Point(0, 0), "curDrop");
        curNodrop = toolkit.createCustomCursor(BCImages.getImage("curNodrop.png"), new Point(0, 0), "curNodrop");
        curResizeD2 = toolkit.createCustomCursor(BCImages.getImage("curResizeD2.png"), new Point(16, 16), "curResizeD2");
        curResizeD1 = toolkit.createCustomCursor(BCImages.getImage("curResizeD1.png"), new Point(16, 16), "curResizeD1");
        curResizeV = toolkit.createCustomCursor(BCImages.getImage("curResizeV.png"), new Point(16, 16), "curResizeV");
        curResizeH = toolkit.createCustomCursor(BCImages.getImage("curResizeH.png"), new Point(16, 16), "curResizeH");
        curWaitAni = new Cursor[11];
        String s;
        for(int i = 1; i < 12; i++)
        {
            s = "curWaitAni";
            if(i < 10)
                s = s + "0";
            s = s + Integer.toString(i);
            s = s + ".png";
            curWaitAni[i - 1] = toolkit.createCustomCursor(BCImages.getImage(s), new Point(0, 0), s);
        }

        BCSettings settings = new BCSettings();
        s = "Element hint";
        showElementHint = settings.getBooleanValue(s, true);
        settings.store(s, Boolean.toString(showElementHint));
    }

    public static void reColor(Object o)
    {
        if((o instanceof Component) && !(o instanceof JButton))
        {
            Component c = (Component)o;
            c.setBackground(bgColor);
            c.setForeground(fgColor);
        }
        if(o instanceof Container)
        {
            Container c = (Container)o;
            for(int i = 0; i < c.getComponentCount(); i++)
                reColor(c.getComponent(i));

        }
    }

    public static Cursor curDefault = null;
    public static Cursor curCopy = null;
    public static Cursor curDrop = null;
    public static Cursor curNodrop = null;
    public static Cursor curResizeH = null;
    public static Cursor curResizeV = null;
    public static Cursor curResizeD1 = null;
    public static Cursor curResizeD2 = null;
    public static Cursor curWaitAni[] = null;
    public static boolean showElementHint = false;
    public static Color bgPanelColor = new Color(0x2f4260);
    public static Color bgColor = new Color(0x4a7597);
    public static Color bgColor2 = new Color(0x5a7eb8);
    public static Color fgColor = new Color(0xffd800);
    public static Color cgColor = new Color(0xff0000);

}
