package org.tinyrcp.grid;

import java.awt.GridLayout;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.tinyrcp.App;
import org.tinyrcp.TinyFactory;
import org.tinyrcp.TinyPlugin;
import org.w3c.dom.*;

/**
 *
 * @author sbodmer
 */
public class JGridPlugin extends JPanel implements TinyPlugin, ActionListener, ChangeListener {

    ResourceBundle bundle = null;
    App app = null;
    TinyFactory factory = null;
    String name = "Grid";

    /**
     *
     * @param factory
     */
    public JGridPlugin(TinyFactory factory) {
        this.factory = factory;
        bundle = ResourceBundle.getBundle("org.tinyrcp.grid.Grid");

        initComponents();

    }
    //***************************************************************************
    //*** API
    //***************************************************************************
    //***************************************************************************
    //*** GUI
    //***************************************************************************

    public void addActionListener(ActionListener listener) {
        //---
    }

    public void removeActionListener(ActionListener listener) {
        //---
    }

    //***************************************************************************
    //*** Plugin
    //***************************************************************************
    @Override
    public void cleanup() {
        for (int i = 0; i < PN_Cells.getComponentCount(); i++) {
            JGridCell jgc = (JGridCell) PN_Cells.getComponent(i);

            TinyPlugin p = jgc.getPlugin();
            p.cleanup();

        }
        PN_Cells.removeAll();

    }

    @Override
    public Object doAction(String action, Object arguments, Object subject) {
        return null;
    }

    @Override
    public JComponent getConfigComponent() {
        return null;
    }

    @Override
    public void saveConfig(Element config) {
        if (config == null) return;

        config.setAttribute("columns", SP_Columns.getValue().toString());

        for (int i = 0; i < PN_Cells.getComponentCount(); i++) {
            JGridCell jgc = (JGridCell) PN_Cells.getComponent(i);

            TinyPlugin p = jgc.getPlugin();
            Element e = config.getOwnerDocument().createElement("Cell");
            e.setAttribute("factory", p.getPluginFactory().getClass().getName());
            e.setAttribute("title", jgc.getTitle());
            p.saveConfig(e);
            config.appendChild(e);

        }

    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public void setup(App app, Object param) {
        this.app = app;

        //------------------------------------------------------------------------
        //--- Create Windows menu with the panels to add
        //------------------------------------------------------------------------
        JMenu jmenu = app.createFactoryMenus("Panels", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_PANEL, this);
        PU_Panels.add(jmenu, 0);
        jmenu = app.createFactoryMenus("Container", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_CONTAINER, this);
        PU_Panels.add(jmenu, 0);

        BT_More.addActionListener(this);
        SP_Columns.addChangeListener(this);
    }

    @Override
    public void setProperty(String name, Object obj) {
        //---
    }

    @Override
    public String getPluginName() {
        return name;
    }

    @Override
    public void setPluginName(String name) {
        this.name = name;
    }

    @Override
    public TinyFactory getPluginFactory() {
        return factory;
    }

    @Override
    public JComponent getVisualComponent() {
        return this;
    }

    @Override
    public void configure(Element config) {
        if (config == null) return;

        try {
            int columns = Integer.parseInt(config.getAttribute("columns"));
            SP_Columns.setValue(columns);

        } catch (NumberFormatException ex) {

        }
        //--- Init Frames
        NodeList nl = config.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals("Cell")) {
                Element e = (Element) nl.item(i);

                TinyFactory factory = app.getFactory(e.getAttribute("factory"));
                if (factory != null) {
                    TinyPlugin p = factory.newPlugin(null);
                    p.setup(app, null);
                    p.configure(e);

                    JGridCell jgc = new JGridCell(p, e.getAttribute("title"), this);
                    PN_Cells.add(jgc);
                }

            }
        }

        int columns = (int) SP_Columns.getValue();
        GridLayout layout = (GridLayout) PN_Cells.getLayout();
        layout.setColumns(columns);
        PN_Cells.revalidate();
    }

    //***************************************************************************
    //*** ActionListener
    //***************************************************************************
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getActionCommand().equals("newPlugin")) {
            String name = JOptionPane.showInputDialog(this, bundle.getString("opt_cellname"));
            if (name != null) {
                JMenuItem ji = (JMenuItem) e.getSource();
                TinyFactory fac = (TinyFactory) ji.getClientProperty("factory");
                TinyPlugin p = fac.newPlugin(null);
                p.setup(app, null);
                p.configure(null);

                JGridCell jgc = new JGridCell(p, name, this);
                PN_Cells.add(jgc);
                PN_Cells.revalidate();
            }

        } else if (e.getActionCommand().equals("more")) {
            PU_Panels.show(BT_More, 10, 10);

        } else if (e.getSource() instanceof JGridCell) {
            JGridCell jgc = (JGridCell) e.getSource();
            if (e.getActionCommand().equals("close")) {
                jgc.getPlugin().cleanup();
                PN_Cells.remove(jgc);
                PN_Cells.revalidate();
            }
            repaint();
        }

    }

    //**************************************************************************
    //*** ChangeListener
    //**************************************************************************
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == SP_Columns) {
            int columns = (int) SP_Columns.getValue();
            GridLayout layout = (GridLayout) PN_Cells.getLayout();
            layout.setColumns(columns);
            PN_Cells.revalidate();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PU_Panels = new javax.swing.JPopupMenu();
        PN_Top = new javax.swing.JPanel();
        BT_More = new javax.swing.JButton();
        SP_Columns = new javax.swing.JSpinner();
        PN_Cells = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        PN_Top.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        BT_More.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        BT_More.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tinyrcp/Resources/Icons/16x16/down.png"))); // NOI18N
        BT_More.setActionCommand("more");
        BT_More.setPreferredSize(new java.awt.Dimension(32, 32));
        PN_Top.add(BT_More);

        SP_Columns.setModel(new javax.swing.SpinnerNumberModel(1, 1, 32, 1));
        PN_Top.add(SP_Columns);

        add(PN_Top, java.awt.BorderLayout.NORTH);

        PN_Cells.setBackground(java.awt.Color.lightGray);
        PN_Cells.setLayout(new java.awt.GridLayout(0, 1));
        add(PN_Cells, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton BT_More;
    public javax.swing.JPanel PN_Cells;
    public javax.swing.JPanel PN_Top;
    public javax.swing.JPopupMenu PU_Panels;
    public javax.swing.JSpinner SP_Columns;
    // End of variables declaration//GEN-END:variables

}
