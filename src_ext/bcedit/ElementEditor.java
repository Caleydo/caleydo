// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import bcedit.BCL.BCElement;
import bcedit.BCL.BCL;
import bcedit.BCL.BCPreset;
import bcedit.BCL.NamedCircle;
import bcedit.BCL.NamedRectangle;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

// Referenced classes of package bcedit:
//            BCActionListener, ListLabel, ComboBoxNumberEdit, CbListCellRenderer, 
//            PImage, StrObj, BCInternalNotify, CBNumberEditor, 
//            JNumberField, BcGrafikMouseMotionListener, BCDefaults, EditorPanel, 
//            GPanel, BCR2000MainGraphic, BCF2000MainGraphic, bcedit

class ElementEditor extends JPanel
    implements ActionListener, MouseListener, FocusListener
{

    public ElementEditor(EditorPanel ep)
    {
        bcActionListener = new BCActionListener();
        editorPanel = null;
        bcPreset = null;
        VisualEditor = null;
        staticLabel = new ListLabel[12];
        aElement = 0;
        aElementType = 0;
        cbEncNumber = null;
        EncName = null;
        bUse3D = true;
        bg = BCDefaults.bgColor;
        fg = BCDefaults.fgColor;
        cbMidiDataType = null;
        cbMidiDataChannel = null;
        cbMidiParameter = null;
        cbDisplayValue = null;
        cbMidiValue1 = null;
        cbMidiValue2 = null;
        cbCtrlMode = null;
        cbCtrlOption = null;
        cbEncAcceleration = null;
        ftfMmc = null;
        activeControl = null;
        addme = null;
        gbc = null;
        layout = null;
        jpFunction = null;
        llEncoder = null;
        llButton = null;
        llFader = null;
        modelID = 0;
        functionSelect = 0;
        int cnt = 0;
        editorPanel = ep;
        layout = new GridBagLayout();
        gbc = makeGridBagConstraints();
        setOpaque(false);
        cbEncAcceleration = new JComboBox();
        cbMidiDataType = new JComboBox();
        cbMidiDataChannel = new JComboBox();
        cbMidiParameter = new ComboBoxNumberEdit();
        cbDisplayValue = new JComboBox();
        cbMidiValue1 = new ComboBoxNumberEdit();
        cbMidiValue2 = new ComboBoxNumberEdit();
        cbCtrlMode = new JComboBox();
        cbCtrlOption = new JComboBox();
        cbEncNumber = new JComboBox();
        cbEncAcceleration.setName("cbEncAcceleration");
        cbMidiDataType.setName("cbMidiDataType");
        cbMidiDataChannel.setName("cbMidiDataChannel");
        cbMidiParameter.setName("cbMidiParameter");
        cbDisplayValue.setName("cbDisplayValue");
        cbMidiValue1.setName("cbMidiValue1");
        cbMidiValue2.setName("cbMidiValue2");
        cbCtrlMode.setName("cbCtrlMode");
        cbCtrlOption.setName("cbCtrlOption");
        cbEncNumber.setName("cbEncNumber");
        MaskFormatter fmt = null;
        try
        {
            fmt = new MaskFormatter("##':##':##'.##");
        }
        catch(ParseException e) { }
        fmt.setPlaceholderCharacter('0');
        ftfMmc = new JFormattedTextField(fmt);
        ftfMmc.setBackground(bg);
        ftfMmc.setForeground(fg);
        ftfMmc.setName("ftfMmc");
        ftfMmc.addActionListener(this);
        ftfMmc.addFocusListener(this);
        EncName = new JTextField(16);
        jpFunction = new JPanel(new FlowLayout(0, 2, 2));
        llEncoder = new ListLabel("Enc");
        llButton = new ListLabel("Button");
        llFader = new ListLabel("Fader");
        llEncoder.setBControlButtonDefault();
        llButton.setBControlButtonDefault();
        llFader.setBControlButtonDefault();
        llEncoder.addMouseListener(this);
        llButton.addMouseListener(this);
        llFader.addMouseListener(this);
        jpFunction.setOpaque(false);
        jpFunction.add(llEncoder);
        jpFunction.add(llButton);
        EncName.addActionListener(this);
        EncName.addFocusListener(this);
        EncName.setEditable(true);
        for(int i = 0; i < 6; i++)
        {
            staticLabel[i] = new ListLabel(staticTextLeft[i]);
            staticLabel[i].setName(staticTextLeft[i]);
            staticLabel[i + 6] = new ListLabel(staticTextRight[i]);
            staticLabel[i].setBControlDefault();
            staticLabel[i + 6].setBControlDefault();
        }

        JComponent xaddme[] = {
            staticLabel[0], cbEncNumber, staticLabel[6], EncName, staticLabel[1], cbEncAcceleration, staticLabel[7], jpFunction, staticLabel[2], cbMidiDataType, 
            staticLabel[8], cbMidiValue1, staticLabel[3], cbMidiDataChannel, staticLabel[9], cbMidiValue2, staticLabel[4], cbMidiParameter, staticLabel[10], cbCtrlMode, 
            staticLabel[5], cbDisplayValue, staticLabel[11], cbCtrlOption
        };
        addme = xaddme;
        addme[0].addMouseListener(this);
        for(int i = 0; i < addme.length; i++)
        {
            if(addme[i] == null || !(addme[i] instanceof JComboBox))
                continue;
            JComboBox _c = (JComboBox)addme[i];
            _c.setBackground(BCDefaults.bgColor);
            _c.setForeground(BCDefaults.fgColor);
            _c.setRenderer(new CbListCellRenderer());
            if(_c.getEditor() != null && _c.getEditor().getEditorComponent() != null)
            {
                Component _e = _c.getEditor().getEditorComponent();
                _e.setBackground(BCDefaults.bgColor);
                _e.setForeground(BCDefaults.fgColor);
            }
        }

        setLayout(layout);
        for(int i = 0; i < 24; i++)
            if(addme[i] != null)
            {
                layout.setConstraints(addme[i], gbc[i]);
                add(addme[i]);
            }

        float size = getFont().getSize2D() - 1.0F;
        Font fnt = getFont().deriveFont(size);
        for(int cnt1 = 0; cnt1 < getComponentCount(); cnt1++)
            getComponent(cnt1).setFont(fnt);

        setBackground(BCDefaults.bgColor);
        reinit(0);
        BCActionListener _tmp = bcActionListener;
        BCActionListener.reColor(this);
    }

    public boolean isAutoSendActive()
    {
        if(editorPanel != null)
            return editorPanel.isAutoSendActive();
        else
            return false;
    }

    void setVisualEditor(GPanel v)
    {
        VisualEditor = v;
    }

    private void changeActiveControl()
    {
    }

    private void genMIDIParameterList(int i)
    {
    }

    private void removeItemListeners()
    {
label0:
        for(int cnt1 = 0; cnt1 < addme.length; cnt1++)
        {
            if(!(addme[cnt1] instanceof JComboBox) && !(addme[cnt1] instanceof ComboBoxNumberEdit))
                continue;
            ActionListener al[] = ((JComboBox)addme[cnt1]).getActionListeners();
            int cnt = 0;
            do
            {
                if(cnt >= al.length)
                    continue label0;
                if(al[cnt] == this)
                {
                    ((JComboBox)addme[cnt1]).removeActionListener(this);
                    continue label0;
                }
                cnt++;
            } while(true);
        }

    }

    private void installItemListeners()
    {
        for(int cnt1 = 0; cnt1 < addme.length; cnt1++)
            if((addme[cnt1] instanceof JComboBox) || (addme[cnt1] instanceof ComboBoxNumberEdit))
                ((JComboBox)addme[cnt1]).addActionListener(this);

    }

    private void removeAnyComboboxItems()
    {
        for(int cnt1 = 0; cnt1 < addme.length; cnt1++)
            if(((addme[cnt1] instanceof JComboBox) || (addme[cnt1] instanceof ComboBoxNumberEdit)) && ((JComboBox)addme[cnt1]).getItemCount() != 0)
                ((JComboBox)addme[cnt1]).removeAllItems();

    }

    public void reinit(int type)
    {
        removeItemListeners();
        removeAnyComboboxItems();
        functionSelect = 0;
        aElementType = 0;
        aElement = 0;
        llEncoder.selectItem();
        llButton.deselectItem();
        if(VisualEditor != null)
            VisualEditor.setHighlightObject(null);
        if(editorPanel != null)
        {
            bcPreset = editorPanel.getactivePreset();
            if(bcPreset != null)
                type = bcPreset.getModelID();
        }
label0:
        switch(type)
        {
        case 21: // '\025'
            encNameCount[0] = 56;
            encNameCount[1] = 64;
            encNameCount[2] = 0;
            for(int cnt = 0; cnt < BCR2000MainGraphic.nelement.length; cnt++)
                if(BCR2000MainGraphic.nelement[cnt] instanceof NamedCircle)
                    cbEncNumber.addItem(((NamedCircle)BCR2000MainGraphic.nelement[cnt]).getName());
                else
                if(BCR2000MainGraphic.nelement[cnt] instanceof NamedRectangle)
                    cbEncNumber.addItem(((NamedRectangle)BCR2000MainGraphic.nelement[cnt]).getName());
                else
                if(BCR2000MainGraphic.nelement[cnt] instanceof PImage)
                    cbEncNumber.addItem(((PImage)BCR2000MainGraphic.nelement[cnt]).getName());

            break;

        case 20: // '\024'
            encNameCount[0] = 32;
            encNameCount[1] = 64;
            encNameCount[2] = 9;
            int cnt = 0;
            do
            {
                if(cnt >= BCF2000MainGraphic.nelement.length)
                    break label0;
                if(BCF2000MainGraphic.nelement[cnt] instanceof NamedCircle)
                    cbEncNumber.addItem(((NamedCircle)BCF2000MainGraphic.nelement[cnt]).getName());
                else
                if(BCF2000MainGraphic.nelement[cnt] instanceof NamedRectangle)
                    cbEncNumber.addItem(((NamedRectangle)BCF2000MainGraphic.nelement[cnt]).getName());
                else
                if(BCF2000MainGraphic.nelement[cnt] instanceof PImage)
                    cbEncNumber.addItem(((PImage)BCF2000MainGraphic.nelement[cnt]).getName());
                cnt++;
            } while(true);

        default:
            type = 0;
            break;
        }
        modelID = type;
        for(int cnt = 0; cnt < MidiDataTypeStrObj.length; cnt++)
            cbMidiDataType.addItem(MidiDataTypeStrObj[cnt]);

        for(int cnt = 0; cnt < addme.length; cnt++)
            if(addme[cnt] != null)
                addme[cnt].setEnabled(true);

        installItemListeners();
        if(type == 0)
        {
            for(int cnt = 0; cnt < addme.length; cnt++)
                if(addme[cnt] != null)
                    addme[cnt].setEnabled(false);

        } else
        {
            evaluate1(false);
            if(VisualEditor != null && cbEncNumber.getSelectedIndex() >= 0)
                switch(modelID)
                {
                case 21: // '\025'
                    VisualEditor.setHighlightObject(BCR2000MainGraphic.nelement[cbEncNumber.getSelectedIndex()]);
                    break;

                case 20: // '\024'
                    VisualEditor.setHighlightObject(BCF2000MainGraphic.nelement[cbEncNumber.getSelectedIndex()]);
                    break;
                }
        }
    }

    private GridBagConstraints[] makeGridBagConstraints()
    {
        GridBagConstraints gbConstraints[] = new GridBagConstraints[24];
        int x = 0;
        int y = 0;
        int w = 1;
        int h = 1;
        double wx = 0.0D;
        double wy = 0.0D;
        for(int i = 0; i < 24; i++)
        {
            switch(i)
            {
            case 3: // '\003'
                w = 4;
                x = i % 4;
                break;

            case 6: // '\006'
            case 10: // '\n'
            case 14: // '\016'
            case 18: // '\022'
            case 22: // '\026'
                w = 2;
                x = i % 4;
                break;

            case 7: // '\007'
            case 11: // '\013'
            case 15: // '\017'
            case 19: // '\023'
            case 23: // '\027'
                w = 2;
                x = i % 4 + 2;
                break;

            case 4: // '\004'
            case 5: // '\005'
            case 8: // '\b'
            case 9: // '\t'
            case 12: // '\f'
            case 13: // '\r'
            case 16: // '\020'
            case 17: // '\021'
            case 20: // '\024'
            case 21: // '\025'
            default:
                w = 1;
                x = i % 4;
                break;
            }
            y = i / 4;
            gbConstraints[i] = new GridBagConstraints(x, y, w, h, wx, wy, 10, 1, new Insets(2, 2, 2, 2), 0, 0);
        }

        return gbConstraints;
    }

    private void setFunctionDisplay(int elementType, int num)
    {
        int cc = jpFunction.getComponentCount();
        if(cc != 0)
            jpFunction.removeAll();
        llEncoder.deselectItem();
        llButton.deselectItem();
        llFader.deselectItem();
        if((elementType == 1 || elementType == 0) && num < 32)
        {
            jpFunction.add(llEncoder);
            jpFunction.add(llButton);
        } else
        {
            switch(elementType)
            {
            case 0: // '\0'
                jpFunction.add(llEncoder);
                break;

            case 1: // '\001'
                jpFunction.add(llButton);
                break;

            case 2: // '\002'
                jpFunction.add(llFader);
                break;
            }
        }
        switch(elementType)
        {
        case 0: // '\0'
            llEncoder.selectItem();
            break;

        case 1: // '\001'
            llButton.selectItem();
            break;

        case 2: // '\002'
            llFader.selectItem();
            break;
        }
        validate();
        jpFunction.repaint();
    }

    public int getFunctionSelect()
    {
        return functionSelect;
    }

    public Dimension getPreferredSize()
    {
        Dimension d1 = super.getPreferredSize();
        if(d1.height < 160)
            d1.height = 160;
        return d1;
    }

    public void paintComponent(Graphics g)
    {
        if(isOpaque())
        {
            Dimension d = getSize();
            Insets i = getInsets();
            int nx = i.left;
            int ny = i.top;
            int nw = d.width - i.left - i.right;
            int nh = d.height - i.top - i.bottom;
            d = getPreferredSize();
            nx = i.left + (nw - d.width) / 2;
            ny = i.top + (nh - d.height) / 2;
            i = addme[20].getInsets();
            nw = d.width - i.left - i.right;
            nh = d.height - i.top - i.bottom;
            Rectangle iB = new Rectangle(nx, ny, nw, nh);
            if(isOpaque())
            {
                Rectangle oB = new Rectangle(0, 0, d.width, d.height);
                g.setColor(new Color(0x666699));
                g.fillRect(oB.x, oB.y, oB.width, oB.height);
            }
            g.setColor(getBackground());
            g.fillRect(iB.x, iB.y, iB.width, iB.height);
            if(bUse3D)
                g.draw3DRect(iB.x, iB.y, iB.width - 1, iB.height - 1, true);
        }
        paintChildren(g);
    }

    private int _initCBRange(JComboBox box, String aItem, int min, int max, boolean bGenList, int actualValue, boolean bEnable, 
            boolean bSetEditable)
    {
        int correctionValue;
        if(aItem != null)
        {
            box.addItem(aItem);
            correctionValue = 1;
        } else
        {
            correctionValue = 0;
        }
        if(box instanceof ComboBoxNumberEdit)
        {
            ((ComboBoxNumberEdit)box).setMinValue(min);
            ((ComboBoxNumberEdit)box).setMaxValue(max);
        }
        if(bGenList)
        {
            for(int i = min; i <= max; i++)
                box.addItem(Integer.toString(i));

            box.setMaximumRowCount(8);
        } else
        {
            box.setMaximumRowCount(4);
        }
        if(actualValue < min || actualValue > max + correctionValue)
            actualValue = 0;
        correctionValue = actualValue - correctionValue;
        if(box.getEditor() != null)
        {
            if(bSetEditable && !box.isEditable())
                box.setEditable(true);
            if(correctionValue < 0)
            {
                box.getEditor().setItem(new String(aItem));
            } else
            {
                if(actualValue >= box.getItemCount())
                    box.setSelectedIndex(-1);
                else
                    box.setSelectedIndex(actualValue);
                box.getEditor().setItem(Integer.toString(correctionValue));
            }
        } else
        {
            box.setSelectedIndex(actualValue);
        }
        if(!box.isEnabled() && bEnable)
            box.setEnabled(true);
        return actualValue;
    }

    private void handleMidiDataType(BCElement bce)
    {
        switch(aElementType)
        {
        case 0: // '\0'
        case 2: // '\002'
            if(cbMidiDataType.getItemAt(7).toString().equals(MidiDataTypeStrObj[7].toString()))
            {
                cbMidiDataType.removeItemAt(7);
                cbMidiDataType.insertItemAt(new StrObj("-"), 7);
            }
            if(cbMidiDataType.getItemAt(4).toString().equals(MidiDataTypeStrObj[4].toString()))
            {
                cbMidiDataType.removeItemAt(4);
                cbMidiDataType.insertItemAt(new StrObj("-"), 4);
            }
            if(!cbMidiDataType.getItemAt(5).toString().equals(MidiDataTypeStrObj[5].toString()))
            {
                cbMidiDataType.removeItemAt(5);
                cbMidiDataType.insertItemAt(MidiDataTypeStrObj[5].toString(), 5);
            }
            break;

        case 1: // '\001'
            if(cbMidiDataType.getItemAt(5).toString().equals(MidiDataTypeStrObj[5].toString()))
            {
                cbMidiDataType.removeItemAt(5);
                cbMidiDataType.insertItemAt(new StrObj("-"), 5);
            }
            if(!cbMidiDataType.getItemAt(7).toString().equals(MidiDataTypeStrObj[7].toString()))
            {
                cbMidiDataType.removeItemAt(7);
                cbMidiDataType.insertItemAt(MidiDataTypeStrObj[7].toString(), 7);
            }
            if(!cbMidiDataType.getItemAt(4).toString().equals(MidiDataTypeStrObj[4].toString()))
            {
                cbMidiDataType.removeItemAt(4);
                cbMidiDataType.insertItemAt(MidiDataTypeStrObj[4].toString(), 4);
            }
            break;
        }
        if(bce.easypar[0] >= cbMidiDataType.getItemCount())
            bce.easypar[0] = 0;
        cbMidiDataType.setSelectedIndex(bce.easypar[0]);
        if(cbMidiDataType.getSelectedItem().toString().equals("-"))
        {
            bce.easypar[0]++;
            cbMidiDataType.setSelectedIndex(bce.easypar[0]);
        }
        if(bce.easypar[0] == 7)
        {
            Component comp[] = getComponents();
            for(int i = 0; i < comp.length; i++)
                if(comp[i] == cbCtrlOption)
                {
                    remove(cbCtrlOption);
                    add(ftfMmc, gbc[23], i);
                    ftfMmc.setVisible(false);
                    revalidate();
                }

        } else
        {
            Component comp[] = getComponents();
            for(int i = 0; i < comp.length; i++)
                if(comp[i] == ftfMmc)
                {
                    remove(ftfMmc);
                    add(cbCtrlOption, gbc[23], i);
                    cbCtrlOption.setVisible(false);
                    revalidate();
                }

        }
        staticLabel[2].setText(staticTextLeft[2]);
    }

    private void handleControlMode(BCElement bce)
    {
        boolean bUpdate = cbCtrlMode.getItemCount() == 0;
        switch(aElementType)
        {
        case 0: // '\0'
        case 2: // '\002'
            switch(bce.easypar[0])
            {
            case 2: // '\002'
                if(bUpdate)
                {
                    for(int i = 1312; i < 1327; i++)
                    {
                        if(i == 1316)
                            continue;
                        String s = BCL.GetToken(32768 + i);
                        if(s == null)
                            break;
                        cbCtrlMode.addItem(s);
                    }

                    if(bce.easypar[5] == 4)
                        bce.easypar[5] = 0;
                }
                staticLabel[10].setText(staticTextRight[4]);
                if(!cbCtrlMode.isEnabled())
                    cbCtrlMode.setEnabled(true);
                if(!cbCtrlMode.isVisible())
                    cbCtrlMode.setVisible(true);
                break;

            case 3: // '\003'
                if(bUpdate)
                {
                    int i = 1312;
                    do
                    {
                        if(i >= 1327)
                            break;
                        String s = BCL.GetToken(32768 + i);
                        if(s == null)
                            break;
                        cbCtrlMode.addItem(s);
                        i++;
                    } while(true);
                }
                staticLabel[10].setText(staticTextRight[4]);
                if(!cbCtrlMode.isEnabled())
                    cbCtrlMode.setEnabled(true);
                if(!cbCtrlMode.isVisible())
                    cbCtrlMode.setVisible(true);
                break;

            default:
                staticLabel[10].setText("");
                if(cbCtrlMode.isEnabled())
                    cbCtrlMode.setEnabled(false);
                if(cbCtrlMode.isVisible())
                    cbCtrlMode.setVisible(false);
                break;
            }
            break;

        case 1: // '\001'
            switch(bce.easypar[0])
            {
            case 7: // '\007'
                if(bUpdate)
                {
                    cbCtrlMode.addItem("Off");
                    cbCtrlMode.addItem("24 fps");
                    cbCtrlMode.addItem("25 fps");
                    cbCtrlMode.addItem("29.97 (30d)");
                    cbCtrlMode.addItem("30 fps");
                }
                if(!cbCtrlMode.isEnabled())
                    cbCtrlMode.setEnabled(true);
                if(!cbCtrlMode.isVisible())
                    cbCtrlMode.setVisible(true);
                staticLabel[10].setText(staticTextRight[4]);
                break;

            case 2: // '\002'
            case 3: // '\003'
            case 6: // '\006'
                if(bUpdate)
                {
                    cbCtrlMode.addItem("Toggle off");
                    cbCtrlMode.addItem("Toggle on");
                    cbCtrlMode.addItem("Increment");
                }
                staticLabel[10].setText(staticTextRight[4]);
                if(!cbCtrlMode.isEnabled())
                    cbCtrlMode.setEnabled(true);
                if(!cbCtrlMode.isVisible())
                    cbCtrlMode.setVisible(true);
                break;

            case 4: // '\004'
            case 8: // '\b'
                if(bUpdate)
                {
                    cbCtrlMode.addItem("Toggle off");
                    cbCtrlMode.addItem("Toggle on");
                }
                staticLabel[10].setText(staticTextRight[4]);
                if(!cbCtrlMode.isEnabled())
                    cbCtrlMode.setEnabled(true);
                if(!cbCtrlMode.isVisible())
                    cbCtrlMode.setVisible(true);
                break;

            case 5: // '\005'
            default:
                staticLabel[10].setText("");
                if(cbCtrlMode.isEnabled())
                    cbCtrlMode.setEnabled(false);
                if(cbCtrlMode.isVisible())
                    cbCtrlMode.setVisible(false);
                break;
            }
            break;
        }
        if(cbCtrlMode.getItemCount() != 0)
        {
            if(cbCtrlMode.getItemCount() == 3 && cbCtrlMode.getItemAt(2).toString().equals("Increment") && bce.easypar[5] == 2)
                bce.easypar[5] = 6;
            if(bce.easypar[5] >= cbCtrlMode.getItemCount())
            {
                if(cbCtrlMode.getItemCount() == 3 && cbCtrlMode.getItemAt(2).toString().equals("Increment"))
                    cbCtrlMode.setSelectedIndex(2);
                else
                    bce.easypar[5] = 0;
            } else
            {
                cbCtrlMode.setSelectedIndex(bce.easypar[5]);
            }
        }
    }

    private void handleControlOption(BCElement bce)
    {
        boolean bEnabled = cbCtrlOption.isEnabled();
        boolean bVisible = cbCtrlOption.isVisible();
        boolean bUpdate = cbCtrlOption.getItemCount() == 0;
        if(bce.easypar[0] == 0)
        {
            staticLabel[11].setText("");
            if(cbCtrlOption.isEnabled())
                cbCtrlOption.setEnabled(false);
            if(cbCtrlOption.isVisible())
                cbCtrlOption.setVisible(false);
            return;
        }
label0:
        switch(aElementType)
        {
        default:
            break;

        case 2: // '\002'
        {
            if(bUpdate)
            {
                cbCtrlOption.addItem("move");
                cbCtrlOption.addItem("pick-Up");
                if(aElement < 8)
                    cbCtrlOption.addItem("motor");
            }
            int i;
            if(aElement < 8 && (bce.flags & 0x200) != 0)
                i = 2;
            else
            if((bce.flags & 0x400) != 0)
                i = 1;
            else
                i = 0;
            cbCtrlOption.setSelectedIndex(i);
            if(!bEnabled)
                cbCtrlOption.setEnabled(true);
            if(!bVisible)
                cbCtrlOption.setVisible(true);
            staticLabel[11].setText(staticTextRight[5]);
            break;
        }

        case 0: // '\0'
        {
            int i;
            if(bUpdate)
            {
                cbCtrlOption.addItem("Off");
                i = 0;
                do
                {
                    if(i >= 15)
                        break;
                    String s = cbCtrlOptionStrings[i];
                    if(s == null)
                        break;
                    cbCtrlOption.addItem(s);
                    if(aElement > 31 && i == 1)
                        break;
                    i++;
                } while(true);
            }
            i = bce.flags & 0xf;
            if(i >= cbCtrlOption.getItemCount())
            {
                i = cbCtrlOption.getItemCount() - 1;
                bce.flags &= 0xfffffff0;
                bce.flags |= i & 0xf;
            }
            cbCtrlOption.setSelectedIndex(bce.flags & 0xf);
            if(!bEnabled)
                cbCtrlOption.setEnabled(true);
            if(!bVisible)
                cbCtrlOption.setVisible(true);
            staticLabel[11].setText(staticTextRight[5]);
            break;
        }

        case 1: // '\001'
        {
            switch(bce.easypar[0])
            {
            case 7: // '\007'
                if(bce.easypar[5] != 0)
                {
                    staticLabel[11].setText("Time");
                    ftfMmc.setVisible(true);
                } else
                {
                    staticLabel[11].setText("");
                    ftfMmc.setVisible(false);
                }
                break label0;

            case 2: // '\002'
            case 3: // '\003'
            case 6: // '\006'
                if(cbCtrlMode.getSelectedIndex() == 2)
                {
                    int i;
                    if(bUpdate)
                        for(i = -127; i < 128; i++)
                            if(i == 0)
                                cbCtrlOption.addItem(new StrObj("-"));
                            else
                                cbCtrlOption.addItem(Integer.toString(i));

                    i = bce.easypar[5];
                    if((i & 2) == 2)
                    {
                        i >>= 2;
                        if(i >= 128)
                            i = -(256 - i);
                        i += 127;
                    } else
                    {
                        i &= 1;
                    }
                    cbCtrlOption.setSelectedIndex(i);
                    if(!bEnabled)
                        cbCtrlOption.setEnabled(true);
                    if(!bVisible)
                        cbCtrlOption.setVisible(true);
                    staticLabel[11].setText(staticTextRight[5]);
                    break label0;
                }
                // fall through

            case 4: // '\004'
            case 5: // '\005'
            default:
                staticLabel[11].setText("");
                break;
            }
            if(bVisible)
                cbCtrlOption.setVisible(false);
            if(bEnabled)
                cbCtrlOption.setEnabled(false);
            break;
        }
        }
    }

    private void handleMidiDataChannel(BCElement bce)
    {
        boolean bUpdate = cbMidiDataChannel.getItemCount() == 0;
        if(bce.easypar[0] == 0)
        {
            staticLabel[3].setText("");
            if(cbMidiDataChannel.isEnabled())
                cbMidiDataChannel.setEnabled(false);
            if(cbMidiDataChannel.isVisible())
                cbMidiDataChannel.setVisible(false);
            return;
        }
        if(bce.easypar[0] == 7)
        {
            if(bUpdate)
            {
                staticLabel[3].setText("MIDI Device Number");
                cbMidiDataChannel.addItem("Off");
                for(int i = 0; i < 127; i++)
                    cbMidiDataChannel.addItem(Integer.toString(i));

            }
        } else
        if(bUpdate)
        {
            staticLabel[3].setText("Send Channel");
            for(int i = 1; i <= 16; i++)
                cbMidiDataChannel.addItem(Integer.toString(i));

        }
        if(bce.easypar[1] >= cbMidiDataChannel.getItemCount())
            bce.easypar[1] = 0;
        cbMidiDataChannel.setSelectedIndex(bce.easypar[1]);
        if(!cbMidiDataChannel.isEnabled())
            cbMidiDataChannel.setEnabled(true);
        if(!cbMidiDataChannel.isVisible())
            cbMidiDataChannel.setVisible(true);
        staticLabel[3].repaint();
    }

    private void handleDisplayValue(BCElement bce)
    {
        boolean bUpdate = cbDisplayValue.getItemCount() == 0;
        if(bce.easypar[0] == 0)
        {
            staticLabel[5].setText("");
            if(cbDisplayValue.isEnabled())
                cbDisplayValue.setEnabled(false);
            if(cbDisplayValue.isVisible())
                cbDisplayValue.setVisible(false);
            return;
        }
        if(bUpdate)
        {
            cbDisplayValue.addItem("Off");
            cbDisplayValue.addItem("On");
        }
        if(!cbDisplayValue.isEnabled())
            cbDisplayValue.setEnabled(true);
        if(!cbDisplayValue.isVisible())
            cbDisplayValue.setVisible(true);
        if((bce.flags & 0x2000) != 0)
        {
            if(cbDisplayValue.getSelectedIndex() != 1)
                cbDisplayValue.setSelectedIndex(1);
        } else
        if(cbDisplayValue.getSelectedIndex() != 0)
            cbDisplayValue.setSelectedIndex(0);
        staticLabel[5].setText(staticTextLeft[5]);
    }

    private void handleParameter(BCElement bce)
    {
        boolean bVisible = cbMidiParameter.isVisible();
        boolean bEnabled = cbMidiParameter.isEnabled();
        boolean bUpdate = cbMidiParameter.getItemCount() == 0;
label0:
        switch(aElementType)
        {
        default:
            break;

        case 0: // '\0'
        case 2: // '\002'
            switch(bce.easypar[0])
            {
            case 1: // '\001'
                if(bUpdate)
                {
                    staticLabel[4].setText("Bank Sel. MSB");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, "Off", 0, 127, true, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break;

            case 2: // '\002'
                if(bUpdate)
                {
                    staticLabel[4].setText("CC no.");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, null, 0, 127, true, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break;

            case 3: // '\003'
                if(bUpdate)
                {
                    staticLabel[4].setText("NRPN no.");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, null, 0, 16383, false, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break;

            case 5: // '\005'
                staticLabel[4].setText("");
                if(bVisible)
                    cbMidiParameter.setVisible(false);
                if(bEnabled)
                    cbMidiParameter.setEnabled(false);
                break;

            case 6: // '\006'
                if(bUpdate)
                {
                    staticLabel[4].setText("Key Number");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, "All", 0, 127, true, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break;

            case 8: // '\b'
                int i;
                if(bUpdate)
                {
                    staticLabel[4].setText("MainCtrl");
                    i = 0;
                    do
                    {
                        if(i >= 15)
                            break;
                        String s = BCL.GetToken(34112 + i);
                        if(s == null)
                            break;
                        cbMidiParameter.addItem(s);
                        i++;
                    } while(true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                cbMidiParameter.setBackground(BCDefaults.bgColor);
                cbMidiParameter.setForeground(BCDefaults.fgColor);
                i = bce.easypar[2] & 0xf;
                if(i != bce.easypar[2])
                    bce.easypar[2] = i;
                cbMidiParameter.setSelectedIndex(i);
                break;

            case 4: // '\004'
            case 7: // '\007'
            default:
                staticLabel[4].setText("");
                if(bVisible)
                    cbMidiParameter.setVisible(false);
                if(bEnabled)
                    cbMidiParameter.setEnabled(false);
                break;
            }
            break;

        case 1: // '\001'
            switch(bce.easypar[0])
            {
            case 1: // '\001'
            {
                if(bUpdate)
                {
                    staticLabel[4].setText("Bank Sel. MSB");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, "Off", 0, 127, true, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break label0;
            }

            case 2: // '\002'
            {
                if(bUpdate)
                {
                    staticLabel[4].setText("CC no.");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, null, 0, 127, true, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break label0;
            }

            case 3: // '\003'
            {
                if(bUpdate)
                {
                    staticLabel[4].setText("NRPN no.");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, null, 0, 16383, false, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break label0;
            }

            case 4: // '\004'
            {
                if(bUpdate)
                {
                    staticLabel[4].setText("NOTE no.");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, null, 0, 127, true, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break label0;
            }

            case 6: // '\006'
            {
                if(bUpdate)
                {
                    staticLabel[4].setText("Key Number");
                    bce.easypar[2] = _initCBRange(cbMidiParameter, "All", 0, 127, true, bce.easypar[2], true, true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                break label0;
            }

            case 7: // '\007'
            {
                if(bUpdate)
                {
                    staticLabel[4].setText("Operation");
                    cbMidiParameter.setMinValue(0);
                    cbMidiParameter.setMaxValue(0);
                    cbMidiParameter.addItem("play");
                    cbMidiParameter.addItem("pause");
                    cbMidiParameter.addItem("stop");
                    cbMidiParameter.addItem("fwd");
                    cbMidiParameter.addItem("rew");
                    cbMidiParameter.addItem("locate");
                    cbMidiParameter.addItem("punch-in");
                    cbMidiParameter.addItem("punch-out");
                }
                int i = bce.easypar[2];
                if(i >= 8)
                    bce.easypar[2] = i = 0;
                cbMidiParameter.setSelectedIndex(i);
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                cbMidiParameter.setBackground(BCDefaults.bgColor);
                cbMidiParameter.setForeground(BCDefaults.fgColor);
                break label0;
            }

            case 8: // '\b'
            {
                int i;
                if(bUpdate)
                {
                    staticLabel[4].setText("MainCtrl");
                    i = 0;
                    do
                    {
                        if(i >= 15)
                            break;
                        String s = BCL.GetToken(34112 + i);
                        if(s == null)
                            break;
                        cbMidiParameter.addItem(s);
                        i++;
                    } while(true);
                }
                if(!bVisible)
                    cbMidiParameter.setVisible(true);
                if(!bEnabled)
                    cbMidiParameter.setEnabled(true);
                cbMidiParameter.setBackground(BCDefaults.bgColor);
                cbMidiParameter.setForeground(BCDefaults.fgColor);
                i = bce.easypar[2] & 0xf;
                bce.easypar[2] = i;
                cbMidiParameter.setSelectedIndex(i);
                break label0;
            }

            case 5: // '\005'
            default:
            {
                staticLabel[4].setText("");
                break;
            }
            }
            if(bVisible)
                cbMidiParameter.setVisible(false);
            if(bEnabled)
                cbMidiParameter.setEnabled(false);
            break;
        }
        staticLabel[4].repaint();
    }

    private void handleValue1(BCElement bce)
    {
        boolean bVisible = cbMidiValue1.isVisible();
        boolean bEnabled = cbMidiValue1.isEnabled();
        boolean bUpdate = cbMidiValue1.getItemCount() == 0;
label0:
        switch(aElementType)
        {
        default:
            break;

        case 0: // '\0'
        case 2: // '\002'
            switch(bce.easypar[0])
            {
            case 1: // '\001'
                if(bUpdate)
                {
                    staticLabel[8].setText("Bank Sel. LSB");
                    bce.easypar[3] = _initCBRange(cbMidiValue1, "Off", 0, 127, true, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break;

            case 2: // '\002'
            case 3: // '\003'
                if(bUpdate)
                {
                    staticLabel[8].setText("Minimum");
                    int max;
                    if(cbCtrlMode.getSelectedIndex() > 4)
                        max = 16383;
                    else
                        max = 127;
                    bce.easypar[3] = _initCBRange(cbMidiValue1, null, 0, max, max <= 127, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break;

            case 5: // '\005'
                if(bUpdate)
                {
                    staticLabel[8].setText("Range");
                    bce.easypar[3] = _initCBRange(cbMidiValue1, null, 0, 127, true, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break;

            case 6: // '\006'
            case 8: // '\b'
                if(bUpdate)
                {
                    staticLabel[8].setText("Minimum");
                    bce.easypar[3] = _initCBRange(cbMidiValue1, null, 0, 127, true, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break;

            case 4: // '\004'
            case 7: // '\007'
            default:
                staticLabel[8].setText("");
                if(bVisible)
                    cbMidiValue1.setVisible(false);
                if(bEnabled)
                    cbMidiValue1.setEnabled(false);
                break;
            }
            break;

        case 1: // '\001'
            switch(bce.easypar[0])
            {
            case 1: // '\001'
                if(bUpdate)
                {
                    staticLabel[8].setText("Bank Sel. LSB");
                    bce.easypar[3] = _initCBRange(cbMidiValue1, "Off", 0, 127, true, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break label0;

            case 2: // '\002'
            case 3: // '\003'
                if(bUpdate)
                {
                    if((bce.easypar[5] & 2) == 2)
                        staticLabel[8].setText("Maximum");
                    else
                        staticLabel[8].setText("On-Value");
                    int max = 127;
                    bce.easypar[3] = _initCBRange(cbMidiValue1, null, 0, max, true, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break label0;

            case 7: // '\007'
                staticLabel[8].setText("");
                if(cbCtrlMode.getSelectedIndex() == 0);
                if(bVisible)
                    cbMidiValue1.setVisible(false);
                if(bEnabled)
                    cbMidiValue1.setEnabled(false);
                break label0;

            case 4: // '\004'
                if(bUpdate)
                {
                    staticLabel[8].setText("Velocity");
                    bce.easypar[3] = _initCBRange(cbMidiValue1, null, 0, 127, true, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break label0;

            case 6: // '\006'
            case 8: // '\b'
                if(bUpdate)
                {
                    if((bce.easypar[5] & 2) == 2)
                        staticLabel[8].setText("Maximum");
                    else
                        staticLabel[8].setText("On-Value");
                    bce.easypar[3] = _initCBRange(cbMidiValue1, null, 0, 127, true, bce.easypar[3], true, true);
                }
                if(!bVisible)
                    cbMidiValue1.setVisible(true);
                if(!bEnabled)
                    cbMidiValue1.setEnabled(true);
                break label0;

            case 5: // '\005'
            default:
                staticLabel[8].setText("");
                break;
            }
            if(bVisible)
                cbMidiValue1.setVisible(false);
            if(bEnabled)
                cbMidiValue1.setEnabled(false);
            break;
        }
        staticLabel[8].repaint();
    }

    private void handleValue2(BCElement bce)
    {
        boolean bVisible = cbMidiValue2.isVisible();
        boolean bEnabled = cbMidiValue2.isEnabled();
        boolean bUpdate = cbMidiValue2.getItemCount() == 0;
label0:
        switch(aElementType)
        {
        default:
            break;

        case 0: // '\0'
        case 2: // '\002'
            switch(bce.easypar[0])
            {
            case 2: // '\002'
            case 3: // '\003'
                if(bUpdate)
                {
                    staticLabel[9].setText("Maximum");
                    int max = cbCtrlMode.getSelectedIndex() <= 4 ? 127 : 16383;
                    bce.easypar[4] = _initCBRange(cbMidiValue2, null, 0, max, max <= 256, bce.easypar[4], true, true);
                }
                if(!bVisible)
                    cbMidiValue2.setVisible(true);
                if(!bEnabled)
                    cbMidiValue2.setEnabled(true);
                break;

            case 6: // '\006'
            case 8: // '\b'
                if(bUpdate)
                {
                    staticLabel[9].setText("Maximum");
                    bce.easypar[4] = _initCBRange(cbMidiValue2, null, 0, 127, true, bce.easypar[4], true, true);
                }
                if(!bVisible)
                    cbMidiValue2.setVisible(true);
                if(!bEnabled)
                    cbMidiValue2.setEnabled(true);
                break;

            case 4: // '\004'
            case 5: // '\005'
            case 7: // '\007'
            default:
                staticLabel[9].setText("");
                bce.easypar[4] = _initCBRange(cbMidiValue2, null, 0, 0, false, bce.easypar[4], false, false);
                if(bVisible)
                    cbMidiValue2.setVisible(false);
                if(bEnabled)
                    cbMidiValue2.setEnabled(false);
                break;
            }
            break;

        case 1: // '\001'
            switch(bce.easypar[0])
            {
            case 1: // '\001'
                if(bUpdate)
                {
                    staticLabel[9].setText("Prg. Change");
                    bce.easypar[4] = _initCBRange(cbMidiValue2, "Off", 0, 127, true, bce.easypar[4], true, true);
                }
                if(!bVisible)
                    cbMidiValue2.setVisible(true);
                if(!bEnabled)
                    cbMidiValue2.setEnabled(true);
                break label0;

            case 6: // '\006'
                if(bUpdate)
                {
                    if((bce.easypar[5] & 2) == 2)
                        staticLabel[9].setText("Minimum");
                    else
                        staticLabel[9].setText("Off-Value");
                    int max = 127;
                    bce.easypar[4] = _initCBRange(cbMidiValue2, (bce.easypar[5] & 2) != 0 ? null : "Off", 0, max, true, bce.easypar[4], true, true);
                }
                if(!bVisible)
                    cbMidiValue2.setVisible(true);
                if(!bEnabled)
                    cbMidiValue2.setEnabled(true);
                break label0;

            case 8: // '\b'
                if(bUpdate)
                {
                    staticLabel[9].setText("Off-Value");
                    bce.easypar[4] = _initCBRange(cbMidiValue2, (bce.easypar[5] & 2) != 0 ? null : "Off", 0, 127, true, bce.easypar[4], true, true);
                }
                if(!bVisible)
                    cbMidiValue2.setVisible(true);
                if(!bEnabled)
                    cbMidiValue2.setEnabled(true);
                break label0;

            case 2: // '\002'
            case 3: // '\003'
                if(bUpdate)
                {
                    if((bce.easypar[5] & 2) == 2)
                        staticLabel[9].setText("Minimum");
                    else
                        staticLabel[9].setText("Off-Value");
                    int max = 127;
                    bce.easypar[4] = _initCBRange(cbMidiValue2, (bce.easypar[5] & 2) != 0 ? null : "Off", 0, max, true, bce.easypar[4], true, true);
                }
                if(!bVisible)
                    cbMidiValue2.setVisible(true);
                if(!bEnabled)
                    cbMidiValue2.setEnabled(true);
                break label0;

            case 7: // '\007'
                staticLabel[9].setText("");
                if(cbCtrlMode.getSelectedIndex() != 0 && bce.easypar[5] != 0)
                {
                    int frames[] = {
                        0, 24, 25, 30, 30
                    };
                    String timecode = "";
                    int mmc = bce.easypar[3];
                    int h = mmc / 60;
                    int m = mmc % 60;
                    mmc = bce.easypar[4];
                    int s = mmc / frames[bce.easypar[5]];
                    int f = mmc % frames[bce.easypar[5]];
                    if(h < 10)
                        timecode = timecode + "0";
                    timecode = timecode + Integer.toString(h) + ":";
                    if(m < 10)
                        timecode = timecode + "0";
                    timecode = timecode + Integer.toString(m) + ":";
                    if(s < 10)
                        timecode = timecode + "0";
                    timecode = timecode + Integer.toString(s) + ".";
                    if(f < 10)
                        timecode = timecode + "0";
                    timecode = timecode + Integer.toString(f);
                    ftfMmc.setValue(timecode);
                }
                if(bVisible)
                    cbMidiValue2.setVisible(false);
                if(bEnabled)
                    cbMidiValue2.setEnabled(false);
                break label0;

            case 4: // '\004'
            case 5: // '\005'
            default:
                staticLabel[9].setText("");
                break;
            }
            if(bVisible)
                cbMidiValue2.setVisible(false);
            if(bEnabled)
                cbMidiValue2.setEnabled(false);
            break;
        }
        staticLabel[9].repaint();
    }

    public synchronized void evaluate1(boolean sendFlag)
    {
        if(editorPanel == null)
        {
            System.out.println("evaluate1::editorPanel is null!");
            return;
        }
        BCPreset bcp = editorPanel.getactivePreset();
        if(bcp == null)
        {
            System.out.println("evaluate1::BCPreset is null!");
            return;
        }
        BCElement bce = bcp.getElement(aElement, aElementType);
        if(bce == null)
        {
            System.out.println("evaluate1::BCElement (" + Integer.toString(aElement) + ", " + Integer.toString(aElementType) + ") is null! ");
            return;
        }
        removeItemListeners();
        EncName.setText(bce.name);
        cbMidiDataType.setSelectedIndex(bce.easypar[0]);
        if((bce.easy = bce.easypar[0]) == 0)
            bce.flags &= 0xffff7fff;
        else
            bce.flags |= 0x8000;
        if(bce.easy > 10)
            bce.easy = 0;
        handleMidiDataType(bce);
        if(cbMidiDataType.getSelectedItem().toString().equals("-"))
        {
            bce.easy = 0;
            installItemListeners();
            return;
        }
        handleControlMode(bce);
        handleControlOption(bce);
        handleMidiDataChannel(bce);
        handleDisplayValue(bce);
        handleParameter(bce);
        handleValue1(bce);
        handleValue2(bce);
        for(int cnt = 0; cnt < addme.length; cnt++)
            if(addme[cnt] != null && (addme[cnt] instanceof ListLabel))
            {
                boolean bflag = ((ListLabel)addme[cnt]).getText().length() != 0;
                addme[cnt].setVisible(bflag);
            }

        if(sendFlag)
            (new BCInternalNotify(this, "SendElementToHardware")).start();
        installItemListeners();
    }

    public void eGroupChanged(int group)
    {
        if((aElementType == 0 || aElementType == 1) && aElement < 32)
        {
            int idx = aElement % 8 + group * 8 + functionSelect;
            cbEncNumber.setSelectedIndex(idx);
        }
    }

    public void eClicked(int idx)
    {
        cbEncNumber.setSelectedIndex(idx);
    }

    public int getElementInt(String stest)
    {
        int aType = 0;
        int num = 0;
        try
        {
            num = Integer.parseInt(stest.substring(1 + stest.indexOf(' ')));
        }
        catch(NumberFormatException e2)
        {
            return -1;
        }
        if(stest.startsWith("Encoder "))
            aType = 0;
        else
        if(stest.startsWith("Button "))
            aType = 1;
        else
        if(stest.startsWith("Footsw "))
        {
            aType = 1;
            if(num != 0)
                num += 60;
        } else
        if(stest.startsWith("Fader "))
            aType = 2;
        else
        if(stest.startsWith("Footctrl "))
        {
            aType = 2;
            if(num != 0)
                num += 8;
        }
        if(num > 0)
            return (aType << 8) + (num - 1);
        else
            return -1;
    }

    public BCPreset getActivePreset()
    {
        return bcPreset;
    }

    private boolean checkMMCtimecode()
    {
        boolean retval;
        BCElement bce;
        int frames[];
        int h;
        int m;
        int s;
        int f;
        int fps;
        String input;
        retval = false;
        if(editorPanel == null)
            return retval;
        BCPreset bcp = editorPanel.getactivePreset();
        if(bcp == null)
            return retval;
        bce = bcp.getElement(aElement, aElementType);
        if(bce == null)
            return retval;
        frames = (new int[] {
            0, 24, 25, 30, 30
        });
        h = 0;
        m = 0;
        s = 0;
        f = 0;
        fps = 0;
        input = ftfMmc.getValue().toString();
        h = Integer.parseInt(input.substring(0, 2));
        m = Integer.parseInt(input.substring(3, 5));
        s = Integer.parseInt(input.substring(6, 8));
        f = Integer.parseInt(input.substring(9));
        fps = frames[bce.easypar[5]];
        
        if(f < fps) {
	//            break MISSING_BLOCK_LABEL_191;
	        
	        ftfMmc.requestFocus();
	        ftfMmc.select(9, input.length());
	        return retval;
        }
        
        if(h <= 23) {
           // break MISSING_BLOCK_LABEL_216;
        
	        ftfMmc.requestFocus();
	        ftfMmc.select(0, 2);
	        return retval;
	    }
    
        if(m <= 59) {
           // break MISSING_BLOCK_LABEL_241;
            
	        ftfMmc.requestFocus();
	        ftfMmc.select(3, 5);
	        return retval;
        }
        
        if(s <= 59) {
            //break MISSING_BLOCK_LABEL_268;
	        ftfMmc.requestFocus();
	        ftfMmc.select(6, 8);
	        return retval;
        }
        
        try
        {
            bce.easypar[3] = h * 60 + m;
            bce.easypar[4] = s * fps + f;
            retval = true;
        }
        catch(Exception e)
        {
            bce.easypar[3] = 0;
            bce.easypar[4] = 0;
            ftfMmc.setValue("00:00:00.00");
            return retval;
        }
        ftfMmc.selectAll();
        return retval;
    }

    private void checkEncName()
    {
        String invalidString[] = {
            ":", "/", "\\", ";", "?", "*", "'"
        };
        boolean bInvalidCharacter = false;
        if(editorPanel == null)
            return;
        BCPreset bcp = editorPanel.getactivePreset();
        if(bcp == null)
            return;
        BCElement bce = bcp.getElement(aElement, aElementType);
        if(bce == null)
            return;
        int i = 0;
        do
        {
            if(i >= invalidString.length)
                break;
            if(EncName.getText().indexOf(invalidString[i]) >= 0)
            {
                bInvalidCharacter = true;
                break;
            }
            i++;
        } while(true);
        if(bInvalidCharacter)
        {
            JOptionPane.showMessageDialog(bcedit.app, "Your Name contains invalid characters:\nDo not use one of the following characters:\n':' '/' '\\' ';' '?' '*'", "Name Error", 0);
            return;
        }
        bce.name = EncName.getText();
        int len = bce.name.length();
        if(len > 0)
        {
            EncName.setCaretPosition(len);
            EncName.select(0, len);
        }
    }

    public EditorPanel getEditorPanel()
    {
        return editorPanel;
    }

    public void focusGained(FocusEvent e)
    {
        if(e.getSource() == EncName)
        {
            int len = EncName.getText().length();
            if(len > 0)
            {
                EncName.setCaretPosition(len);
                EncName.select(0, len);
            }
        } else
        if(e.getSource() == ftfMmc)
            ftfMmc.selectAll();
    }

    public void focusLost(FocusEvent e)
    {
        if(e.getSource() == EncName)
            checkEncName();
        if(e.getSource() == ftfMmc && checkMMCtimecode())
            evaluate1(editorPanel.isAutoSendActive());
    }

    public void actionPerformed(ActionEvent e)
    {
        if((e.getSource() instanceof JComboBox) && e.getActionCommand().equals("comboBoxChanged"))
        {
            if(editorPanel == null)
                return;
            BCPreset bcp = editorPanel.getactivePreset();
            if(bcp == null)
                return;
            BCElement bce = bcp.getElement(aElement, aElementType);
            if(bce == null)
                return;
            int lastValue = -1;
            JComboBox box = (JComboBox)e.getSource();
            if(box.isEditable() && box.getEditor() != null && (box.getEditor() instanceof CBNumberEditor) && !((JNumberField)((CBNumberEditor)box.getEditor()).getEditorComponent()).checkValue())
                return;
            if(e.getSource() == cbMidiDataType)
            {
                if(cbMidiDataType.getItemCount() > 2)
                {
                    bce.easypar[0] = cbMidiDataType.getSelectedIndex();
                    if((bce.easy = bce.easypar[0]) == 0)
                        bce.flags &= 0xffff7fff;
                    else
                        bce.flags |= 0x8000;
                    if(bce.easy > 10)
                        bce.easy = 0;
                    removeItemListeners();
                    cbMidiDataChannel.removeAllItems();
                    cbMidiParameter.removeAllItems();
                    if(cbMidiParameter.isEditable())
                        cbMidiParameter.setEditable(false);
                    cbMidiValue1.removeAllItems();
                    cbMidiValue2.removeAllItems();
                    cbCtrlMode.removeAllItems();
                    cbCtrlOption.removeAllItems();
                    cbDisplayValue.removeAllItems();
                    installItemListeners();
                    evaluate1(editorPanel.isAutoSendActive());
                }
            } else
            if(e.getSource() == cbEncAcceleration)
                System.out.println("cbEncAcceleration not yet handled");
            else
            if(e.getSource() == cbMidiDataChannel)
            {
                lastValue = bce.easypar[1];
                bce.easypar[1] = cbMidiDataChannel.getSelectedIndex();
                if(lastValue != bce.easypar[1])
                    evaluate1(editorPanel.isAutoSendActive());
            } else
            if(e.getSource() == cbMidiParameter)
            {
                if(cbMidiParameter.getSelectedIndex() < 0)
                {
                    try
                    {
                        int x = Integer.parseInt(cbMidiParameter.getText());
                        if(bce.easypar[2] != x)
                        {
                            bce.easypar[2] = x;
                            evaluate1(editorPanel.isAutoSendActive());
                        }
                    }
                    catch(Exception e_parameter)
                    {
                        bce.easypar[2] = 0;
                        System.out.println("EASY_PARAMETER is NaN");
                    }
                } else
                {
                    int x = cbMidiParameter.getSelectedIndex();
                    if(bce.easypar[2] != x)
                    {
                        bce.easypar[2] = x;
                        evaluate1(editorPanel.isAutoSendActive());
                    }
                }
            } else
            if(e.getSource() == cbDisplayValue)
            {
                if(cbDisplayValue.getSelectedIndex() == 1)
                    bce.flags |= 0x2000;
                else
                    bce.flags &= 0xffffdfff;
                evaluate1(editorPanel.isAutoSendActive());
            } else
            if(e.getSource() == cbMidiValue1)
            {
                lastValue = bce.easypar[3];
                if(cbMidiValue1.getSelectedIndex() < 0)
                    try
                    {
                        bce.easypar[3] = Integer.parseInt(cbMidiValue1.getText());
                    }
                    catch(Exception e_parameter)
                    {
                        bce.easypar[3] = -1;
                    }
                else
                    bce.easypar[3] = cbMidiValue1.getSelectedIndex();
                if(lastValue != bce.easypar[3])
                {
                    evaluate1(editorPanel.isAutoSendActive());
                    cbMidiValue1.getEditor().selectAll();
                }
            } else
            if(e.getSource() == cbMidiValue2)
            {
                lastValue = bce.easypar[4];
                if(cbMidiValue2.getSelectedIndex() < 0)
                    try
                    {
                        bce.easypar[4] = Integer.parseInt(cbMidiValue2.getText());
                    }
                    catch(Exception e_parameter)
                    {
                        bce.easypar[4] = -1;
                    }
                else
                    bce.easypar[4] = cbMidiValue2.getSelectedIndex();
                if(lastValue != bce.easypar[4])
                {
                    evaluate1(editorPanel.isAutoSendActive());
                    cbMidiValue2.getEditor().selectAll();
                }
            } else
            if(e.getSource() == cbCtrlMode)
            {
                int i = cbCtrlMode.getSelectedIndex();
                lastValue = bce.easypar[5];
                if(cbCtrlMode.getSelectedItem().toString().equals("Increment") && lastValue >> 2 == 0)
                    i = 6;
                if(bce.easypar[0] == 2 && cbCtrlMode.getModel().getSize() == 8)
                {
                    if(i > 3)
                        i++;
                } else
                if(cbCtrlMode.getModel().getSize() == 5 && lastValue < cbCtrlMode.getModel().getSize())
                {
                    int frames[] = {
                        0, 24, 25, 30, 30
                    };
                    int s = 0;
                    int f = 0;
                    int fps = frames[lastValue];
                    if(fps != 0 && frames[i] != 0)
                    {
                        s = bce.easypar[4] / fps;
                        f = bce.easypar[4] % fps;
                        fps = frames[i];
                        f = f < fps ? f : fps - 1;
                        bce.easypar[4] = s * fps + f;
                    }
                }
                bce.easypar[5] = i;
                if(lastValue != bce.easypar[5])
                {
                    removeItemListeners();
                    cbMidiValue1.removeAllItems();
                    cbMidiValue2.removeAllItems();
                    installItemListeners();
                    evaluate1(editorPanel.isAutoSendActive());
                }
            } else
            if(e.getSource() == cbCtrlOption)
            {
                lastValue = bce.flags;
                switch(aElementType)
                {
                case 2: // '\002'
                    bce.flags &= 0xfffff9ff;
                    if(cbCtrlOption.getSelectedIndex() == 2)
                        bce.flags |= 0x200;
                    else
                    if(cbCtrlOption.getSelectedIndex() == 1)
                        bce.flags |= 0x400;
                    break;

                case 0: // '\0'
                    bce.flags &= 0xfffffff0;
                    bce.flags |= cbCtrlOption.getSelectedIndex() & 0xf;
                    break;

                case 1: // '\001'
                    if(cbCtrlMode.getSelectedItem().toString().equals("Increment"))
                    {
                        int i = cbCtrlOption.getSelectedIndex();
                        if(cbCtrlOption.getSelectedItem().toString().equals("-"))
                            cbCtrlOption.setSelectedIndex(++i);
                        i = -127 + cbCtrlOption.getSelectedIndex();
                        if(i < 0)
                            i = 256 + i;
                        else
                        if(i == 0)
                            i++;
                        i <<= 2;
                        i |= 2;
                        bce.easypar[5] = i;
                        lastValue = -1;
                    } else
                    {
                        bce.flags &= 0xfffff9ff;
                        switch(cbCtrlOption.getSelectedIndex())
                        {
                        case 1: // '\001'
                            bce.flags |= 0x600;
                            break;

                        case 2: // '\002'
                            bce.flags |= 0x200;
                            break;
                        }
                    }
                    break;
                }
                if(lastValue != bce.flags)
                    evaluate1(editorPanel.isAutoSendActive());
            } else
            if(e.getSource() == cbEncNumber)
            {
                int num = getElementInt((String)cbEncNumber.getSelectedItem());
                if(num != -1)
                {
                    aElementType = num >> 8 & 0xff;
                    aElement = num & 0xff;
                    setFunctionDisplay(aElementType, aElement);
                    editorPanel.setActiveElementNumber(num);
                    if(VisualEditor != null && (aElementType == 0 || aElementType == 1) && aElement < 32)
                    {
                        MouseListener x[] = VisualEditor.getMouseListeners();
                        int i = 0;
                        do
                        {
                            if(i >= x.length)
                                break;
                            if(x[i] instanceof BcGrafikMouseMotionListener)
                            {
                                ((BcGrafikMouseMotionListener)x[i]).changeGroup((aElement >> 3) + 1, false);
                                break;
                            }
                            i++;
                        } while(true);
                    }
                }
                removeItemListeners();
                cbMidiDataChannel.removeAllItems();
                cbMidiParameter.removeAllItems();
                if(cbMidiParameter.isEditable())
                    cbMidiParameter.setEditable(false);
                cbMidiValue1.removeAllItems();
                cbMidiValue2.removeAllItems();
                cbCtrlMode.removeAllItems();
                cbCtrlOption.removeAllItems();
                cbDisplayValue.removeAllItems();
                installItemListeners();
                evaluate1(editorPanel.isAutoSendActive());
                if(VisualEditor != null && cbEncNumber.getSelectedIndex() >= 0)
                    switch(modelID)
                    {
                    case 21: // '\025'
                        VisualEditor.setHighlightObject(BCR2000MainGraphic.nelement[cbEncNumber.getSelectedIndex()]);
                        break;

                    case 20: // '\024'
                        VisualEditor.setHighlightObject(BCF2000MainGraphic.nelement[cbEncNumber.getSelectedIndex()]);
                        break;
                    }
            }
        } else
        if((e.getSource() instanceof JFormattedTextField) && e.getSource() == ftfMmc && checkMMCtimecode())
            evaluate1(editorPanel.isAutoSendActive());
        if(e.getSource() == EncName)
            checkEncName();
    }

    public void mouseClicked(MouseEvent e)
    {
        if(e.getSource() == llEncoder)
        {
            if(!llEncoder.isHighlighted())
            {
                llEncoder.selectItem();
                llButton.deselectItem();
                aElementType = 0;
                editorPanel.setActiveElementNumber(aElementType << 8 | aElement);
                functionSelect = 0;
                cbEncNumber.setSelectedIndex(functionSelect + aElement);
            }
        } else
        if(e.getSource() == llButton)
        {
            if(!llButton.isHighlighted())
            {
                llButton.selectItem();
                llEncoder.deselectItem();
                aElementType = 1;
                editorPanel.setActiveElementNumber(aElementType << 8 | aElement);
                functionSelect = encNameCount[0];
                cbEncNumber.setSelectedIndex(functionSelect + aElement);
            }
        } else
        if(e.getSource() != llFader)
            if(((ListLabel)e.getSource()).getName().equals("ElementAutoSend"))
            {
                ListLabel ll1 = (ListLabel)e.getSource();
                if(!ll1.isEnabled())
                    return;
                boolean flag = !ll1.isHighlighted();
                (new BCInternalNotify(ll1, ll1.getName() + ":" + Boolean.toString(flag))).start();
            } else
            if(((ListLabel)e.getSource()).getName().equals("Control Element"))
            {
                ListLabel ll1 = (ListLabel)e.getSource();
                int omask = 704;
                if((e.getModifiersEx() & omask) == omask)
                {
                    BCPreset bcp = editorPanel.getactivePreset();
                    if(bcp == null)
                        return;
                    BCElement bce = bcp.getElement(aElement, aElementType);
                    if(bce == null)
                        return;
                    String s = bce.getScript(true);
                    if(s == null)
                        return;
                    JOptionPane.showMessageDialog(new JFrame(), s.replaceAll(";.", "\n  .").replace(';', '\n'), "Info", 1);
                }
            }
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    BCActionListener bcActionListener;
    EditorPanel editorPanel;
    BCPreset bcPreset;
    GPanel VisualEditor;
    ListLabel staticLabel[];
    int aElement;
    int aElementType;
    JComboBox cbEncNumber;
    JTextField EncName;
    boolean bUse3D;
    Color bg;
    Color fg;
    static StrObj MidiDataTypeStrObj[] = {
        new StrObj("Off"), new StrObj("Program Change"), new StrObj("Control Change"), new StrObj("NRPN"), new StrObj("Note"), new StrObj("Pitch Bend"), new StrObj("After Touch"), new StrObj("MMC"), new StrObj("GS/XG")
    };
    JComboBox cbMidiDataType;
    JComboBox cbMidiDataChannel;
    ComboBoxNumberEdit cbMidiParameter;
    JComboBox cbDisplayValue;
    ComboBoxNumberEdit cbMidiValue1;
    ComboBoxNumberEdit cbMidiValue2;
    JComboBox cbCtrlMode;
    String cbCtrlOptionStrings[] = {
        "1 Dot", "1 Dot/Off", "1-2 Dot", "1-2 Dot/Off", "Bar", "Bar/Off", "Spread", "Pan", "Qual", "Cut", 
        "Damp", null
    };
    JComboBox cbCtrlOption;
    JComboBox cbEncAcceleration;
    JFormattedTextField ftfMmc;
    Object activeControl;
    JComponent addme[];
    GridBagConstraints gbc[];
    GridBagLayout layout;
    JPanel jpFunction;
    ListLabel llEncoder;
    ListLabel llButton;
    ListLabel llFader;
    int modelID;
    int functionSelect;
    int encNameCount[] = {
        0, 0, 0
    };
    String staticTextLeft[] = {
        "Control Element", "Enc Accel.", "MIDI Data Type", "MIDI Send Ch", "MIDI Parameter", "Display Value"
    };
    String staticTextRight[] = {
        "Name", "Function", "Midi Value 1", "Midi Value 2", "Ctrl Mode", "Ctrl Option"
    };

}
