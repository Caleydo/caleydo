// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import bcedit.BCL.NamedCircle;
import bcedit.BCL.NamedRectangle;
import java.awt.*;

// Referenced classes of package bcedit:
//            PImage, BCImages

class BCR2000MainGraphic
{

    public BCR2000MainGraphic()
    {
    }

    public void createElements()
    {
        int idx = 0;
        int element = 0;
        int startx = 30;
        int starty[] = {
            49, 162, 220, 278, 70, 94
        };
        int radius = 15;
        int diameter = radius * 2;
        int j;
        for(j = 0; j < 4; j++)
        {
            for(int i = 0; i < 8; i++)
                nelement[element++] = new NamedCircle(startx + radius + i * 33, starty[0], 15, "Encoder " + Integer.toString(++idx));

        }

        for(j = 1; j < 4; j++)
        {
            for(int i = 0; i < 8; i++)
                nelement[element++] = new NamedCircle(startx + i * 33, starty[j], 15, "Encoder " + Integer.toString(++idx));

        }

        idx = 0;
        startx = 36;
        for(j = 0; j < 4; j++)
        {
            for(int i = 0; i < 8; i++)
                nelement[element++] = new PImage(startx + i * 33, starty[0], BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button " + Integer.toString(++idx));

        }

        for(; j < 6; j++)
        {
            for(int i = 0; i < 8; i++)
                nelement[element++] = new PImage(startx + i * 33, starty[j], BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button " + Integer.toString(++idx));

        }

    }

    static int x_correction;
    static int y_correction;
    public static Object nelement[];
    public static Point ButtonHighlightPosition = new Point(-3, 2);
    public static Dimension ButtonHighlightDimension = new Dimension(6, 2);
    public static Rectangle DisplayRect = new Rectangle(308, 41, 46, 16);

    static 
    {
        x_correction = 6;
        y_correction = 14;
        nelement = (new Object[] {
            new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Encoder 01"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Encoder 02"), new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Encoder 03"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Encoder 04"), new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Encoder 05"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Encoder 06"), new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Encoder 07"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Encoder 08"), new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Encoder 09"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Encoder 10"), 
            new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Encoder 11"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Encoder 12"), new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Encoder 13"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Encoder 14"), new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Encoder 15"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Encoder 16"), new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Encoder 17"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Encoder 18"), new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Encoder 19"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Encoder 20"), 
            new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Encoder 21"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Encoder 22"), new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Encoder 23"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Encoder 24"), new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Encoder 25"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Encoder 26"), new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Encoder 27"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Encoder 28"), new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Encoder 29"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Encoder 30"), 
            new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Encoder 31"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Encoder 32"), new NamedCircle(39 + x_correction, 148 + y_correction, 15, "Encoder 33"), new NamedCircle(72 + x_correction, 148 + y_correction, 15, "Encoder 34"), new NamedCircle(105 + x_correction, 148 + y_correction, 15, "Encoder 35"), new NamedCircle(138 + x_correction, 148 + y_correction, 15, "Encoder 36"), new NamedCircle(171 + x_correction, 148 + y_correction, 15, "Encoder 37"), new NamedCircle(204 + x_correction, 148 + y_correction, 15, "Encoder 38"), new NamedCircle(237 + x_correction, 148 + y_correction, 15, "Encoder 39"), new NamedCircle(270 + x_correction, 148 + y_correction, 15, "Encoder 40"), 
            new NamedCircle(39 + x_correction, 206 + y_correction, 15, "Encoder 41"), new NamedCircle(72 + x_correction, 206 + y_correction, 15, "Encoder 42"), new NamedCircle(105 + x_correction, 206 + y_correction, 15, "Encoder 43"), new NamedCircle(138 + x_correction, 206 + y_correction, 15, "Encoder 44"), new NamedCircle(171 + x_correction, 206 + y_correction, 15, "Encoder 45"), new NamedCircle(204 + x_correction, 206 + y_correction, 15, "Encoder 46"), new NamedCircle(237 + x_correction, 206 + y_correction, 15, "Encoder 47"), new NamedCircle(270 + x_correction, 206 + y_correction, 15, "Encoder 48"), new NamedCircle(39 + x_correction, 264 + y_correction, 15, "Encoder 49"), new NamedCircle(72 + x_correction, 264 + y_correction, 15, "Encoder 50"), 
            new NamedCircle(105 + x_correction, 264 + y_correction, 15, "Encoder 51"), new NamedCircle(138 + x_correction, 264 + y_correction, 15, "Encoder 52"), new NamedCircle(171 + x_correction, 264 + y_correction, 15, "Encoder 53"), new NamedCircle(204 + x_correction, 264 + y_correction, 15, "Encoder 54"), new NamedCircle(237 + x_correction, 264 + y_correction, 15, "Encoder 55"), new NamedCircle(270 + x_correction, 264 + y_correction, 15, "Encoder 56"), new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Button 01"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Button 02"), new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Button 03"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Button 04"), 
            new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Button 05"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Button 06"), new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Button 07"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Button 08"), new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Button 09"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Button 10"), new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Button 11"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Button 12"), new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Button 13"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Button 14"), 
            new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Button 15"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Button 16"), new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Button 17"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Button 18"), new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Button 19"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Button 20"), new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Button 21"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Button 22"), new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Button 23"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Button 24"), 
            new NamedCircle(39 + x_correction, 35 + y_correction, 15, "Button 25"), new NamedCircle(72 + x_correction, 35 + y_correction, 15, "Button 26"), new NamedCircle(105 + x_correction, 35 + y_correction, 15, "Button 27"), new NamedCircle(138 + x_correction, 35 + y_correction, 15, "Button 28"), new NamedCircle(171 + x_correction, 35 + y_correction, 15, "Button 29"), new NamedCircle(204 + x_correction, 35 + y_correction, 15, "Button 30"), new NamedCircle(237 + x_correction, 35 + y_correction, 15, "Button 31"), new NamedCircle(270 + x_correction, 35 + y_correction, 15, "Button 32"), new PImage(36, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 33"), new PImage(69, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 34"), 
            new PImage(102, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 35"), new PImage(135, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 36"), new PImage(168, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 37"), new PImage(201, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 38"), new PImage(234, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 39"), new PImage(267, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 40"), new PImage(36, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 41"), new PImage(69, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 42"), new PImage(102, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 43"), new PImage(135, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 44"), 
            new PImage(168, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 45"), new PImage(201, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 46"), new PImage(234, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 47"), new PImage(267, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 48"), new PImage(305, 252, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 49"), new PImage(338, 252, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 50"), new PImage(305, 276, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 51"), new PImage(338, 276, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 52"), new PImage(305, 166, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 53"), new PImage(338, 166, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 54"), 
            new PImage(305, 189, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 55"), new PImage(338, 189, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 56"), new PImage(305, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 57"), new PImage(338, 70, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 58"), new PImage(305, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 59"), new PImage(338, 94, BCImages.getImage("BcButtonLedOff.png"), BCImages.getImage("BcButtonLedOn.png"), BCImages.getImage("BcButtonLedHi.png"), "Button 60"), new NamedRectangle(44 + x_correction, -4 + y_correction, 23, 5, "Footsw 01"), new NamedRectangle(77 + x_correction, -4 + y_correction, 23, 5, "Footsw 02"), new PImage(305, 221, BCImages.getImage("BcButtonNoLed.png"), BCImages.getImage("BcButtonNoLedMark.png"), BCImages.getImage("BcButtonNoLedHi.png"), "Button 63"), new PImage(338, 221, BCImages.getImage("BcButtonNoLed.png"), BCImages.getImage("BcButtonNoLedMark.png"), BCImages.getImage("BcButtonNoLedHi.png"), "Button 64")
        });
    }
}
