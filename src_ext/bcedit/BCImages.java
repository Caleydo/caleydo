// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCImages.java

package bcedit;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.PrintStream;

public class BCImages extends Component
    implements ImageObserver
{

    public BCImages()
    {
        bUseJAR = true;
        if(data != null)
        {
            return;
        } else
        {
            startUp();
            return;
        }
    }

    private synchronized void startUp()
    {
        String FileSep = System.getProperty("file.separator");
        if(bUseJAR)
        {
            ClassLoader cl = getClass().getClassLoader();
            int i = 0;
            data = new Image[ImageFilename.length];
            for(i = 0; i < ImageFilename.length; i++)
            {
                String fname = "grafik/" + ImageFilename[i];
                java.net.URL urlx = cl.getResource(fname);
                if(urlx == null)
                {
                    data[i] = null;
                    System.out.println("File: " + fname + " not found!");
                } else
                {
                    try
                    {
                        data[i] = getToolkit().createImage(urlx);
                        if(!prepareImage(data[i], this))
                            while((checkImage(data[i], this) & 3) != 3) 
                                try
                                {
                                    wait(10L);
                                }
                                catch(IllegalArgumentException e1)
                                {
                                    System.out.println(e1.getMessage());
                                }
                                catch(IllegalMonitorStateException e2)
                                {
                                    System.out.println(e2.getMessage());
                                }
                                catch(InterruptedException e3)
                                {
                                    System.out.println(e3.getMessage());
                                }
                    }
                    catch(Exception e)
                    {
                        data[i] = null;
                        System.out.println(e.getMessage());
                    }
                }
            }

        } else
        {
            int i = 0;
            data = new Image[ImageFilename.length];
label0:
            for(i = 0; i < ImageFilename.length; i++)
            {
                String fname = "grafik" + FileSep + ImageFilename[i];
                data[i] = getToolkit().createImage(fname);
                if(prepareImage(data[i], this))
                    continue;
                do
                {
                    if((checkImage(data[i], this) & 3) == 3)
                        continue label0;
                    try
                    {
                        wait(10L);
                    }
                    catch(IllegalArgumentException e1)
                    {
                        System.out.println(e1.getMessage());
                    }
                    catch(IllegalMonitorStateException e2)
                    {
                        System.out.println(e2.getMessage());
                    }
                    catch(InterruptedException e3)
                    {
                        System.out.println(e3.getMessage());
                    }
                } while(true);
            }

        }
    }

    public static Image getImage(int id)
    {
        if(id >= 0 && id < ImageFilename.length)
            return data[id];
        else
            return null;
    }

    public static Image getImage(String name)
    {
        int i = 0;
        for(i = 0; i < ImageFilename.length; i++)
            if(ImageFilename[i].equalsIgnoreCase(name))
                return data[i];

        return null;
    }

    public boolean imageUpdate(Image image, int infoflags, int x, int y, int width, int height)
    {
        int checkflag = 3;
        if(infoflags < 64)
            if((infoflags & checkflag) != checkflag);
        return true;
    }

    private boolean bUseJAR;
    static final String ImageSubDir = "grafik";
    static String ImageFilename[] = {
        "bcedit_icon.png", "bg1.png", "empty8x16.png", "Text_COMPUTER_unit.png", "Text_EDITOR_unit.png", "Text_HARDWARE_unit.png", "EMPTY_Mini.png", "BCR2000_Mini.png", "BCF2000_Mini.png", "empty_Main.png", 
        "BCF2000_Main.png", "BCR2000_Main.png", "Welcome_Main.png", "Edit_Knob_01.png", "Pfeilrechts.png", "Pfeilrechts.png", "load.gif", "Button_Led_red_dark.png", "Button_Led_red_light.png", "Button_Led_red_dark_highlight.png", 
        "Button_Led_red_light_highlight.png", "switch_off_36x25.png", "switch_on_36x25.png", "switch_sel_36x25.png", "button_72x25.png", "button_pressed_72x25.png", "button_selected_72x25.png", "Panel_Layout_01.png", "Panel_Layout_02.png", "Panel_Layout_03.png", 
        "Panel_Layout_04.png", "Panel_Layout_04_top.png", "Panel_Layout_04_bottom.png", "Panel_Layout_04_center.png", "Panel_Layout_06.png", "Panel_Layout_06_top.png", "Panel_Layout_06_bottom.png", "Panel_Layout_06_center.png", "Panel_Layout_07.png", "Panel_Layout_08.png", 
        "Panel_Layout_09.png", "CTRL-anim-1.png", "CTRL-anim-2.png", "CTRL-anim-3.png", "CTRL-anim-4.png", "CTRL-anim-5.png", "CTRL-anim-6.png", "CTRL-anim-7.png", "CTRL-anim-8.png", "CTRL-anim-9.png", 
        "CTRL-anim-10.png", "CTRL-anim-11.png", "CTRL-anim-12.png", "EDIT-anim-1.png", "EDIT-anim-2.png", "EDIT-anim-3.png", "EDIT-anim-4.png", "EDIT-anim-5.png", "EDIT-anim-6.png", "EDIT-anim-7.png", 
        "EDIT-anim-8.png", "EDIT-anim-9.png", "EDIT-anim-10.png", "EDIT-anim-11.png", "EDIT-anim-12.png", "logo-a01.png", "logo-a02.png", "logo-a03.png", "logo-a04.png", "logo-a05.png", 
        "logo-a06.png", "logo-a07.png", "logo-a08.png", "logo-a09.png", "logo-a10.png", "logo-a11.png", "logo-a12.png", "logo-a13.png", "logo-a14.png", "logo-a15.png", 
        "logo-a16.png", "logo-a17.png", "logo-a18.png", "logo-a19.png", "logo-a20.png", "GRP1.png", "GRP2.png", "GRP3.png", "GRP4.png", "BcButtonLedOff.png", 
        "BcButtonLedOn.png", "BcButtonLedHi.png", "BcButtonNoLed.png", "BcButtonNoLedHi.png", "BcButtonNoLedMark.png", "Ordner_blau_geschlossen.png", "Ordner_blau_offen.png", "bcr2000leaficon.png", "bcf2000leaficon.png", "FoldLeft.png", 
        "FoldRight.png", "Left-1.png", "Right-1.png", "LOGO Behringer.png", "WindowFrame_upperleft.png", "WindowFrame_uppercenter.png", "WindowFrame_upperright.png", "WindowFrame_centerleft.png", "WindowFrame_centerright.png", "WindowFrame_lowerleft.png", 
        "WindowFrame_lowercenter.png", "WindowFrame_lowerright.png", "curDefault.png", "curCopy.png", "curDrop.png", "curNodrop.png", "curResizeD1.png", "curResizeD2.png", "curResizeH.png", "curResizeV.png", 
        "curWaitAni01.png", "curWaitAni02.png", "curWaitAni03.png", "curWaitAni04.png", "curWaitAni05.png", "curWaitAni06.png", "curWaitAni07.png", "curWaitAni08.png", "curWaitAni09.png", "curWaitAni10.png", 
        "curWaitAni11.png"
    };
    public static Image data[] = null;
    public static int ready = 0;

}
