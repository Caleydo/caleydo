// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import bcedit.BCL.BCElement;
import bcedit.BCL.BCL;
import bcedit.BCL.BCPreset;
import bcedit.BCL.BCPresetHead;
import bcedit.BCL.Circle;
import bcedit.BCL.NamedCircle;
import bcedit.BCL.NamedRectangle;
import java.awt.*;
import java.awt.event.*;

// Referenced classes of package bcedit:
//            PImage, BCInternalNotify, ElementEditor, EditorPanel, 
//            GrPreset, GPanel, BCR2000MainGraphic, BCImages, 
//            BCDefaults

class BcGrafikMouseMotionListener
    implements MouseMotionListener, MouseListener
{

    public BcGrafikMouseMotionListener(Object oList[], GPanel gp, ElementEditor ee)
    {
        hotspot = null;
        hotspothit = -1;
        lasthighlighted = -1;
        dragStart = -1;
        dragEnd = -1;
        dragElement = null;
        dragActive = false;
        dragStartPoint = new Point(0, 0);
        MainGrafik = null;
        eEdit = null;
        eGROUP = null;
        eGROUP2 = null;
        hotspot = oList;
        MainGrafik = gp;
        eEdit = ee;
    }

    public void changePreset(int addMe)
    {
        int idx = eEdit.getEditorPanel().getPresetIndex() + addMe;
        if(idx < 0)
            idx += 32;
        else
        if(idx >= 32)
            idx -= 32;
        eEdit.getEditorPanel().getPresetPanel().setPresetListIndex(idx);
        MainGrafik.setHighlightObject(hotspot[lasthighlighted]);
        eEdit.eClicked(lasthighlighted);
    }

    public void changeGroup(int group, boolean callUp)
    {
        eEdit.getActivePreset().activeGroup = group;
        if(eGROUP != null)
            MainGrafik.removePersistentOverlay(eGROUP);
        if(eGROUP2 != null)
            MainGrafik.removePersistentOverlay(eGROUP2);
        PImage p = new PImage();
        p.x = BCR2000MainGraphic.DisplayRect.x;
        p.y = BCR2000MainGraphic.DisplayRect.y + 1;
        p.img[0] = BCImages.getImage("GRP" + Integer.toString(group) + ".png");
        eGROUP = p;
        MainGrafik.addPersistentOverlay(eGROUP);
        MainGrafik.repaint();
        if(callUp)
            eEdit.eGroupChanged(group - 1);
    }

    public String getHotspotHitName()
    {
        if(hotspothit < 0)
            return null;
        if(hotspot[hotspothit] instanceof NamedCircle)
            return ((NamedCircle)hotspot[hotspothit]).getName();
        if(hotspot[hotspothit] instanceof NamedRectangle)
            return ((NamedRectangle)hotspot[hotspothit]).getName();
        if(hotspot[hotspothit] instanceof PImage)
            return ((PImage)hotspot[hotspothit]).getName();
        else
            return null;
    }

    public void mouseMoved(MouseEvent e)
    {
        double rescale = 1.0D;
        if(MainGrafik == null)
            return;
        if(MainGrafik.bAllowResize && MainGrafik.bKeepAspectRatio && MainGrafik.lastRescale != 1.0D)
            rescale = MainGrafik.lastRescale;
        int lastHit = hotspothit;
        Point hp = e.getPoint();
        Point tp = MainGrafik.getLocation(null);
        Rectangle r = MainGrafik.getPaintRect();
        tp.x = hp.x;
        tp.y = hp.y;
        if(hotspot != null)
        {
            hotspothit = -1;
            for(int cnt = 0; cnt < hotspot.length; cnt++)
            {
                if(hotspot[cnt] instanceof Circle)
                {
                    Circle c = new Circle();
                    c.x = (int)((double)((Circle)hotspot[cnt]).x * rescale);
                    c.y = (int)((double)((Circle)hotspot[cnt]).y * rescale);
                    c.r = (int)((double)((Circle)hotspot[cnt]).r * rescale);
                    double px = tp.x - c.x - r.x;
                    double py = tp.y - c.y - r.y;
                    double pr = c.r;
                    px *= px;
                    py *= py;
                    pr *= pr;
                    if(px + py > pr)
                        continue;
                    hotspothit = cnt;
                    break;
                }
                if(hotspot[cnt] instanceof Rectangle)
                {
                    Rectangle c = new Rectangle((Rectangle)hotspot[cnt]);
                    c.width = (int)((double)c.width * rescale);
                    c.height = (int)((double)c.height * rescale);
                    c.x = r.x + (int)((double)c.x * rescale);
                    c.y = r.y + (int)((double)c.y * rescale);
                    if(!c.contains(tp))
                        continue;
                    hotspothit = cnt;
                    break;
                }
                if(!(hotspot[cnt] instanceof PImage))
                    continue;
                PImage p = (PImage)hotspot[cnt];
                Rectangle c = new Rectangle(p.x, p.y, p.img[0].getWidth(null), p.img[0].getHeight(null));
                c.width = (int)((double)c.width * rescale);
                c.height = (int)((double)c.height * rescale);
                c.x = r.x + (int)((double)c.x * rescale);
                c.y = r.y + (int)((double)c.y * rescale);
                if(!c.contains(tp))
                    continue;
                hotspothit = cnt;
                break;
            }

            if(hotspothit >= 0 && hotspothit < 8 && eEdit != null)
            {
                hotspothit += eEdit.getFunctionSelect();
                hotspothit += (eEdit.getActivePreset().activeGroup - 1 & 3) * 8;
            }
            if(lastHit != hotspothit)
            {
                if(dragActive && hotspothit != -1)
                {
                    BCPreset bcp = eEdit.getActivePreset();
                    String name = getHotspotHitName();
                    int num = eEdit.getElementInt(name);
                    BCElement bce = eEdit.getActivePreset().getElement(num & 0xff, num >> 8 & 0xff);
                    if(dragElement != null && dragElement.ctrlType != bce.ctrlType && ((bce.ctrlType ^ dragElement.ctrlType) & 1) != 0)
                        MainGrafik.setCursor(BCDefaults.curNodrop);
                    else
                    if(dragStart != hotspothit)
                        MainGrafik.setCursor(BCDefaults.curDrop);
                    else
                        MainGrafik.setCursor(BCDefaults.curDefault);
                }
                if(hotspothit == -1)
                {
                    if(dragActive)
                    {
                        if(MainGrafik.getCursor() != BCDefaults.curCopy)
                            MainGrafik.setCursor(BCDefaults.curCopy);
                    } else
                    if(MainGrafik.getCursor() != BCDefaults.curDefault)
                        MainGrafik.setCursor(BCDefaults.curDefault);
                    MainGrafik.setMarkObject(null);
                } else
                {
                    MainGrafik.setMarkObject(hotspot[hotspothit]);
                }
                if(BCDefaults.showElementHint)
                {
                    String name = getHotspotHitName();
                    if(name != null)
                    {
                        BCPreset bcp = eEdit != null ? eEdit.getActivePreset() : null;
                        if(bcp != null)
                        {
                            int num = eEdit.getElementInt(name);
                            BCElement bce = bcp.getElement(num & 0xff, num >> 8 & 0xff);
                            if(bce != null && bce.getEasy() != null)
                                name = name + "\n" + bce.getEasy();
                        }
                    }
                    MainGrafik.setToolTipText(name);
                } else
                if(MainGrafik.getToolTipText() != null)
                    MainGrafik.setToolTipText(null);
            }
        }
    }

    public void mouseDragged(MouseEvent e)
    {
        mouseMoved(e);
    }

    public void mouseClicked(MouseEvent e)
    {
        if(e.getButton() == 1 && e.getClickCount() == 1 && hotspothit != -1)
        {
            boolean lock = (eEdit.getActivePreset().getPreset().flags & 0x800) != 2048;
            int grpcnt = eEdit.getActivePreset().getPreset().flags + 1 & 3;
            grpcnt = grpcnt != 0 ? grpcnt : 4;
            String name = null;
            name = getHotspotHitName();
            if(name != null && name.startsWith("Button"))
            {
                int idx = 0;
                try
                {
                    idx = Integer.parseInt(name.substring(7));
                }
                catch(Exception ne) { }
                switch(idx)
                {
                case 57: // '9'
                    if(grpcnt > 1)
                        idx = 1;
                    break;

                case 58: // ':'
                    if(grpcnt > 1)
                        idx = 2;
                    break;

                case 59: // ';'
                    if(grpcnt > 2)
                        idx = 3;
                    break;

                case 60: // '<'
                    if(grpcnt > 3)
                        idx = 4;
                    break;

                case 63: // '?'
                    if(lock)
                    {
                        changePreset(-1);
                        return;
                    }
                    // fall through

                case 64: // '@'
                    if(lock)
                    {
                        changePreset(1);
                        return;
                    }
                    // fall through

                case 61: // '='
                case 62: // '>'
                default:
                    idx = 0;
                    break;
                }
                if(idx > 0 && idx <= 4)
                {
                    changeGroup(idx, true);
                    return;
                }
            }
            lasthighlighted = hotspothit;
            MainGrafik.setHighlightObject(hotspot[hotspothit]);
            eEdit.eClicked(hotspothit);
        }
        if(e.getButton() == 1 && e.getClickCount() == 2 && !eEdit.isAutoSendActive())
            (new BCInternalNotify(this, "SendElementToHardware")).start();
    }

    public void mouseEntered(MouseEvent e)
    {
        if(dragActive && MainGrafik.getCursor() != BCDefaults.curCopy)
            MainGrafik.setCursor(BCDefaults.curCopy);
    }

    public void mouseExited(MouseEvent e)
    {
        if(dragActive && MainGrafik.getCursor() != BCDefaults.curNodrop)
            MainGrafik.setCursor(BCDefaults.curNodrop);
    }

    public void mousePressed(MouseEvent e)
    {
        if(e.getButton() == 1 && hotspothit != -1)
        {
            boolean lock = (eEdit.getActivePreset().getPreset().flags & 0x800) != 2048;
            int grpcnt = eEdit.getActivePreset().getPreset().flags + 1 & 3;
            grpcnt = grpcnt != 0 ? grpcnt : 4;
            String name = getHotspotHitName();
            if(name != null && name.startsWith("Button"))
            {
                int idx = 0;
                try
                {
                    idx = Integer.parseInt(name.substring(7));
                }
                catch(Exception ne) { }
                switch(idx)
                {
                case 57: // '9'
                    if(grpcnt > 1)
                        idx = 1;
                    break;

                case 58: // ':'
                    if(grpcnt > 1)
                        idx = 2;
                    break;

                case 59: // ';'
                    if(grpcnt > 2)
                        idx = 3;
                    break;

                case 60: // '<'
                    if(grpcnt > 3)
                        idx = 4;
                    break;

                case 63: // '?'
                    if(lock)
                        idx = 8;
                    break;

                case 64: // '@'
                    if(lock)
                        idx = 9;
                    break;

                case 61: // '='
                case 62: // '>'
                default:
                    idx = 0;
                    break;
                }
                if(idx > 0)
                    return;
            }
            dragStart = hotspothit;
            dragActive = true;
            dragStartPoint = e.getPoint();
            BCPreset bcp = eEdit.getActivePreset();
            int num = eEdit.getElementInt(name);
            String elementScript = bcp.getElement(num & 0xff, num >> 8 & 0xff).getScript(true);
            if(elementScript != null)
                dragElement = BCL.initElementFromScript(elementScript);
            else
                dragElement = null;
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        if(e.getButton() == 1)
        {
            if(hotspothit != -1 && dragStart != -1 && hotspothit != dragStart && dragElement != null)
            {
                dragEnd = hotspothit;
                String name = getHotspotHitName();
                BCPreset bcp = eEdit.getActivePreset();
                int num = eEdit.getElementInt(name);
                BCElement bce = bcp.getElement(num & 0xff, num >> 8 & 0xff);
                if(bce.ctrlType != dragElement.ctrlType && ((bce.ctrlType ^ dragElement.ctrlType) & 1) == 0)
                {
                    dragElement.ctrlType = bce.ctrlType;
                    dragElement.flags &= 0xfffff800;
                }
                if(dragElement.ctrlType == bce.ctrlType)
                {
                    dragElement.id = (num & 0xff) + 1;
                    bcp.setElement(dragElement);
                    mouseClicked(e);
                }
            }
            if(dragStart != -1)
            {
                dragActive = false;
                MainGrafik.setCursor(BCDefaults.curDefault);
                dragStart = -1;
            }
        }
    }

    public Object hotspot[];
    public int hotspothit;
    public int lasthighlighted;
    public int dragStart;
    public int dragEnd;
    public BCElement dragElement;
    public boolean dragActive;
    private Point dragStartPoint;
    public GPanel MainGrafik;
    public ElementEditor eEdit;
    public Object eGROUP;
    public Object eGROUP2;
}
