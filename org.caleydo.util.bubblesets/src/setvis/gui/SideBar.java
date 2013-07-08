/**
 *
 */
package setvis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import setvis.SetOutline;
import setvis.shape.AbstractShapeGenerator;

/**
 * The side bar for controlling the input to the {@link CanvasComponent}.
 *
 * @author Joschi <josua.krause@googlemail.com>
 *
 */
public class SideBar extends JPanel {

    // serial version uid
    private static final long serialVersionUID = 6967000092103706967L;

    /**
     * The color to indicate an invalid number.
     */
	private static final Color ERROR = new Color(Color.HSBtoRGB(0f, 0.6f, 1f));

    /**
     * The normal input background.
     */
    private static final Color NORMAL = Color.WHITE;

    /**
     * The minimal rectangle size.
     */
    private static final int MIN_SIZE = 10;

    /**
     * The underlying canvas.
     */
    private final Canvas canvas;

    /**
     * A simple list model for maintaining groups of the {@link CanvasComponent}
     * .
     *
     * @author Joschi <josua.krause@googlemail.com>
     *
     */
    private class CanvasListModel extends AbstractListModel {

        // serial version uid
        private static final long serialVersionUID = 3431270899264849193L;

        @Override
        public Object getElementAt(final int index) {
            return "Group " + index;
        }

        @Override
        public int getSize() {
            return canvas.getGroupCount();
        }

        /**
         * Propagates that something in the list has changed.
         */
        public void invalidate() {
            fireContentsChanged(this, 0, canvas.getGroupCount());
        }

    }

    /** The canvas list model. */
    private final CanvasListModel listModel;

    /** The groups list. */
    private final JList list;

    /** The text field for the rectangle width. */
    private final JTextField width;

    /** The text field for the rectangle height. */
    private final JTextField height;

    /** The box for choosing the outline generator. */
    private final JComboBox outlineBox;

    /** The box for choosing the shape generator. */
    private final JComboBox shapeBox;

    /** The slider for the border size. */
    private final JSlider borderSlider;

    /** The label for the slider for the border size. */
    private final JLabel borderSliderLabel;

    /** The outline dependent panel. */
    private final JPanel outlinePanel;

    /** The draw points check-box. */
    private final JCheckBox drawPoints;

    /** The simplify check-box. */
    private final JCheckBox simplifyShape;

    /** The tolerance slider. */
    private final JSlider simplifyTolerance;

    /** The tolerance slider label. */
    private final JLabel simplifyLabel;

    /** The information label. */
    private final JLabel infoLabel;

    /** The Java creation text. */
    private final JTextField javaText;

    /** The content of the outline panel. */
    private AbstractOutlineConfiguration outlineContent;

    /** The constraints for the layout. */
    private GridBagConstraints constraint;

    /**
     * Creates a side bar for the given {@link Canvas}.
     *
     * @param cc
     *            The {@link Canvas}.
     */
    public SideBar(final Canvas cc) {
        canvas = cc;
        setLayout(new GridBagLayout());
        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.fill = GridBagConstraints.BOTH;
        // the combo-box for outlines
        outlineBox = new JComboBox(OutlineType.values());
        addHor(new JLabel("Outline:"), outlineBox);
        // the combo-box for shape creators
        shapeBox = new JComboBox(ShapeType.values());
        addHor(new JLabel("Shape:"), shapeBox);
        // interaction for the shape and outline combo-boxes
        final ActionListener shapeOutlineListener = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                canvas.setShapeAndOutline(
                        (OutlineType) outlineBox.getSelectedItem(),
                        (ShapeType) shapeBox.getSelectedItem());
            }

        };
        outlineBox.addActionListener(shapeOutlineListener);
        shapeBox.addActionListener(shapeOutlineListener);
        // the groups list
        listModel = new CanvasListModel();
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                // selection of groups
                canvas.setCurrentGroup(list.getSelectedIndex());
            }

        });
        add(new JScrollPane(list), constraint);
        // adding and removing groups
        final JButton addGroup = new JButton(new AbstractAction("+") {

            // serial version uid
            private static final long serialVersionUID = -7674517069323059813L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                canvas.addGroup();
                // select the newly created group
                list.setSelectedIndex(canvas.getGroupCount() - 1);
            }

        });
        final JButton remGroup = new JButton(new AbstractAction("-") {

            // serial version uid
            private static final long serialVersionUID = 45084574338099392L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                canvas.removeSelectedGroup();
            }

        });
        final JButton center = new JButton(new AbstractAction("Center View") {

            private static final long serialVersionUID = -996182815730540464L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                canvas.defaultView();
            }

        });
        constraint.fill = GridBagConstraints.VERTICAL;
        addHor(addGroup, remGroup, center);
        constraint.fill = GridBagConstraints.BOTH;
        // add empty filling space
        final JPanel empty = new JPanel();
        constraint.weighty = 1.0;
        // empty.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(empty, constraint);
        constraint.weighty = 0.0;
        // outline dependent content
        outlinePanel = new JPanel();
        add(outlinePanel, constraint);
        // draw points and simplify tolerance
        drawPoints = new JCheckBox("Show points");
        drawPoints.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                canvas.setDrawPoints(drawPoints.isSelected());
            }

        });
        addHor(drawPoints);
        simplifyShape = new JCheckBox("Simplify shapes");
        simplifyTolerance = new JSlider(0, 1000);
        simplifyLabel = new JLabel();
        final ChangeListener simplifyListener = new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                canvas.setTolerance(simplifyShape.isSelected() ? calcTolerance(simplifyTolerance
                        .getValue()) : -1.0);
            }
        };
        simplifyShape.addChangeListener(simplifyListener);
        simplifyTolerance.addChangeListener(simplifyListener);
        addHor(simplifyShape, simplifyTolerance, simplifyLabel, null);
        // border slider
        borderSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 20, 10);
        final ChangeListener borderListener = new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                canvas.setShapeBorder(borderSlider.getValue());
            }

        };
        borderSlider.addChangeListener(borderListener);
        borderSliderLabel = new JLabel();
        addHor(new JLabel("Border:"), borderSlider, borderSliderLabel);
        // the rectangle width and height input fields
        width = new JTextField(4);
        height = new JTextField(4);
        width.setMaximumSize(width.getPreferredSize());
        height.setMaximumSize(height.getPreferredSize());
        final ActionListener bounds = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent ae) {
                int w = 0;
                try {
                    width.setBackground(NORMAL);
                    w = Integer.parseInt(width.getText());
                } catch (final NumberFormatException e) {
                    // nothing to do
                }
                int h = 0;
                try {
                    height.setBackground(NORMAL);
                    h = Integer.parseInt(height.getText());
                } catch (final NumberFormatException e) {
                    // nothing to do
                }
                if (w < MIN_SIZE) {
                    width.setBackground(ERROR);
                } else {
                    canvas.setCurrentItemWidth(w);
                }
                if (h < MIN_SIZE) {
                    height.setBackground(ERROR);
                } else {
                    canvas.setCurrentItemHeight(h);
                }
            }
        };
        width.addActionListener(bounds);
        height.addActionListener(bounds);
        constraint.fill = GridBagConstraints.VERTICAL;
        addHor(new JLabel("Width:"), width, new JLabel("Height:"), height);
        // texts
        infoLabel = new JLabel();
        javaText = new JTextField(20);
        javaText.setEditable(false);
        addHor(new JLabel("Java-Code:"), javaText);
        addHor(infoLabel);
        constraint.fill = GridBagConstraints.BOTH;
        constraint = null;
    }

    // the maximal tolerance reachable with the slider
    private final double MAX_TOLERANCE = 10.0;

    /**
     * @param from
     *            The slider position.
     * @return The tolerance for the slider position, i.e. a value between 0 and
     *         1000.
     */
    private double calcTolerance(final int from) {
        return from / 1000.0 * MAX_TOLERANCE;
    }

    /**
     * @param fromTolerance
     *            The tolerance.
     * @return The slider position for the given tolerance.
     */
    private int calcFromTolerance(final double fromTolerance) {
        return Math
                .min(
                        Math.max((int) (fromTolerance / MAX_TOLERANCE * 1000.0), 0),
                        1000);
    }

    /**
     * Adds a series of components in a horizontal manner. This method may not
     * be called outside the constructor.
     *
     * @param comps
     *            The components.
     */
    private void addHor(final JComponent... comps) {
        if (constraint == null) {
            throw new IllegalStateException("layouting already done");
        }
        final JPanel hor = new JPanel();
        hor.setLayout(new BoxLayout(hor, BoxLayout.X_AXIS));
        boolean first = true;
        for (final JComponent c : comps) {
            if (first) {
                first = false;
            } else {
                hor.add(Box.createRigidArea(new Dimension(5, 5)));
            }
            if (c != null) {
                hor.add(c);
            }
        }
        add(hor, constraint);
    }

    /**
     * Is called when something on the outside has changed.
     *
     * @param changes
     *            The type of changes as defined in
     *            {@link CanvasListener#canvasChanged(int)}.
     */
    public void somethingChanged(final int changes) {
        if ((changes & CanvasListener.GROUPS) != 0) {
            list.setSelectedIndex(canvas.getCurrentGroup());
            listModel.invalidate();
        }
        if ((changes & CanvasListener.GENERATORS) != 0) {
            final AbstractShapeGenerator asc = canvas.getShapeCreator();
            final SetOutline so = asc.getSetOutline();
            final OutlineType outlineType = OutlineType.getFor(so);
            outlineBox.setSelectedItem(outlineType);
            shapeBox.setSelectedItem(ShapeType.getFor(asc));
            final int oldValue = borderSlider.getValue();
            final int newValue = (int) canvas.getShapeBorder();
            borderSliderLabel.setText(newValue + "  ");
            if (oldValue != newValue) {
                borderSlider.setValue(newValue);
            }
            final double tolerance = canvas.getTolerance();
            final boolean hasTolerance = tolerance >= 0.0;
            simplifyShape.setSelected(hasTolerance);
            simplifyTolerance.setEnabled(hasTolerance);
            if (hasTolerance) {
                simplifyTolerance.setValue(calcFromTolerance(tolerance));
            }
            final String text = (calcTolerance(simplifyTolerance.getValue()) + "0000")
                    .substring(0, 4);
            simplifyLabel.setText(text);
            refreshOutlineContent(outlineType, so);
        }
        if ((changes & CanvasListener.SCREEN) != 0) {
            drawPoints.setSelected(canvas.isDrawingPoints());
        }
        if ((changes & CanvasListener.RECT_SIZE) != 0) {
            final int cw = canvas.getCurrentItemWidth();
            final String tw = "" + cw;
            try {
                final int w = Integer.parseInt(width.getText());
                if (cw != w) {
                    width.setText(tw);
                }
            } catch (final NumberFormatException e) {
                width.setText(tw);
            }
            final int ch = canvas.getCurrentItemHeight();
            final String th = "" + ch;
            try {
                final int h = Integer.parseInt(height.getText());
                if (ch != h) {
                    height.setText(th);
                }
            } catch (final NumberFormatException e) {
                height.setText(th);
            }
        }
        if ((changes & CanvasListener.TEXT) != 0) {
            infoLabel.setText(canvas.getInfoText());
            javaText.setText(canvas.getCreationText());
        }
    }

    /**
     * Refreshes the outline specific pane.
     *
     * @param outlineType
     *            The current outline type.
     * @param so
     *            The current set outline.
     */
    private void refreshOutlineContent(final OutlineType outlineType,
            final SetOutline so) {
        if (outlineContent == null || outlineContent.getType() != outlineType) {
            if (outlineContent != null) {
                outlinePanel.remove(outlineContent);
            }
            outlineContent = outlineType.createOutlineConfiguration(canvas);
            if (outlineContent != null) {
                outlineContent.fillContent();
                outlinePanel.add(outlineContent);
                outlinePanel.setBorder(BorderFactory.createTitledBorder(""
                        + outlineType));
            } else {
                outlinePanel.setBorder(BorderFactory.createEmptyBorder());
            }
        }
        if (outlineContent != null) {
            if (so != outlineContent.getOutline()) {
                outlineContent.setOutline(so);
            }
            outlineContent.somethingChanged();
        }
    }

}
