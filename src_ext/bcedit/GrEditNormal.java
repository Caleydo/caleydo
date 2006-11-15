// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            JPanelMy, GPanel, ElementEditor, BCF2000MainGraphic, 
//            BCR2000MainGraphic, BcGrafikMouseMotionListener, ListLabel, BCImages, 
//            EditorPanel

class GrEditNormal extends JPanelMy
{

    public GrEditNormal(EditorPanel ep)
    {
        super(null);
        disp1 = null;
        editorPanel = null;
        bottomEdit = null;
        activeVisual = 0;
        editorPanel = ep;
        bottomEdit = new ElementEditor(editorPanel);
        reinit(0);
    }

    public GrEditNormal(EditorPanel ep, int devId)
    {
        super(null);
        disp1 = null;
        editorPanel = null;
        bottomEdit = null;
        activeVisual = 0;
        editorPanel = ep;
        bottomEdit = new ElementEditor(editorPanel);
        reinit(devId);
    }

    public void reinit(int devId)
    {
        BCF2000MainGraphic ___a___ = new BCF2000MainGraphic();
        BCR2000MainGraphic ___b___ = new BCR2000MainGraphic();
        for(int i = 0; i < 3; i++)
        {
            centerEdit[i].bAllowResize = true;
            centerEdit[i].setFocusable(true);
            java.awt.event.MouseListener m1listener[] = centerEdit[i].getMouseListeners();
            for(int j = 0; j < m1listener.length; j++)
                centerEdit[i].removeMouseListener(m1listener[j]);

            java.awt.event.MouseMotionListener mlistener[] = centerEdit[i].getMouseMotionListeners();
            for(int j = 0; j < mlistener.length; j++)
            {
                centerEdit[i].removeMouseMotionListener(mlistener[j]);
                centerEdit[i].removeAllPersistentOverlay();
            }

            switch(i)
            {
            case 1: // '\001'
            {
                BcGrafikMouseMotionListener xxx = new BcGrafikMouseMotionListener(BCF2000MainGraphic.nelement, centerEdit[i], bottomEdit);
                centerEdit[i].addMouseListener(xxx);
                centerEdit[i].addMouseMotionListener(xxx);
                break;
            }

            case 2: // '\002'
            {
                BcGrafikMouseMotionListener xxx = new BcGrafikMouseMotionListener(BCR2000MainGraphic.nelement, centerEdit[i], bottomEdit);
                centerEdit[i].addMouseListener(xxx);
                centerEdit[i].addMouseMotionListener(xxx);
                break;
            }
            }
        }

        for(int i = 0; i < 3; i++)
        {
            centerEdit[i].xAlignment = 1;
            centerEdit[i].yAlignment = 1;
        }

        java.awt.Component cc[] = getComponents();
        for(int i = 0; i < cc.length; i++)
            remove(cc[i]);

        BorderLayout layout = new BorderLayout();
        JPanel n = new JPanel();
        n.setOpaque(false);
        FlowLayout f = new FlowLayout(1, 8, 8);
        n.setLayout(f);
        n.add(bottomEdit);
        setLayout(layout);
        add("South", n);
        switch(devId)
        {
        case 21: // '\025'
            activeVisual = 2;
            break;

        case 20: // '\024'
            activeVisual = 1;
            break;

        default:
            activeVisual = 0;
            break;
        }
        add("Center", centerEdit[activeVisual]);
        bottomEdit.setVisualEditor(centerEdit[activeVisual]);
        if(disp1 == null)
        {
            disp1 = new ListLabel("---");
            disp1.setName("disp1");
            disp1.setBControlDefault();
            disp1.setLayout(new BorderLayout(0, 0));
            ListLabel ll1 = new ListLabel("Auto");
            ll1.setOpaque(false);
            ll1.setBControlButtonDefault();
            ll1.setAutoRepaint(true);
            ll1.deselectItem();
            ll1.setName("ElementAutoSend");
            disp1.add("West", ll1);
            ll1.addMouseListener(bottomEdit);
        }
        add("North", disp1);
    }

    ElementEditor getElementEditor()
    {
        return bottomEdit;
    }

    GPanel getVisualEditor()
    {
        return centerEdit[activeVisual];
    }

    public ListLabel disp1;
    EditorPanel editorPanel;
    ElementEditor bottomEdit;
    GPanel centerEdit[] = {
        new GPanel(BCImages.getImage("empty_Main.png")), new GPanel(BCImages.getImage("BCF2000_Main.png")), new GPanel(BCImages.getImage("BCR2000_Main.png"))
    };
    int activeVisual;
}
