/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp.split;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import org.tinyrcp.App;
import org.tinyrcp.TinyFactory;
import org.tinyrcp.TinyPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author sbodmer
 */
public class JSplitPlugin extends javax.swing.JPanel implements TinyPlugin, MouseListener, ActionListener {

    TinyFactory factory = null;
    App app = null;

    /**
     * Which side the new plugin panel must be created
     */
    boolean left = true;

    /**
     * Creates new form JTabsPlugin
     */
    public JSplitPlugin(TinyFactory factory) {
        this.factory = factory;

        initComponents();

        //-- For nimbus L&F
        JButton jb = (JButton) SP_Main.getLeftComponent();
        jb.addMouseListener(this);

        jb = (JButton) SP_Main.getRightComponent();
        jb.addMouseListener(this);

    }

    //**************************************************************************
    //*** API
    //**************************************************************************
    //**************************************************************************
    //*** TinyPlugin
    //**************************************************************************
    @Override
    public TinyFactory getPluginFactory() {
        return factory;
    }

    @Override
    public String getPluginName() {
        return factory.getFactoryName();
    }

    @Override
    public void setPluginName(String name) {
        //---
    }

    @Override
    public JComponent getVisualComponent() {
        return this;
    }

    @Override
    public JComponent getConfigComponent() {
        return null;
    }

    @Override
    public Object doAction(String message, Object argument, Object subject) {
        return null;
    }

    @Override
    public void setup(App app, Object argument) {
        this.app = app;

        MN_Horizontal.addActionListener(this);
        MN_Vertical.addActionListener(this);

        PU_Factories.add(app.createFactoryMenus("Panels", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_PANEL, this), 0);
        PU_Factories.add(app.createFactoryMenus("Containers", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_CONTAINER, this), 1);
        SP_Main.addMouseListener(this);
    }

    @Override
    public void configure(Element config) {
        if (config == null) return;

        SP_Main.setOrientation(config.getAttribute("orientation").equals("") ? JSplitPane.HORIZONTAL_SPLIT : Integer.parseInt(config.getAttribute("orientation")));
        try {
            SP_Main.setDividerLocation(Integer.parseInt(config.getAttribute("divider")));

        } catch (NumberFormatException ex) {

        }
        NodeList nl = config.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals("First")) {
                Element e = (Element) nl.item(i);
                TinyFactory fac = app.getFactory(e.getAttribute("factory"));
                if (fac != null) {
                    TinyPlugin p = fac.newPlugin(null);
                    p.setup(app, null);
                    p.configure(e);
                    p.setPluginName(e.getAttribute("name"));
                    JComponent jcomp = p.getVisualComponent();
                    jcomp.putClientProperty("plugin", p);
                    SP_Main.setLeftComponent(jcomp);
                }

            } else if (nl.item(i).getNodeName().equals("Second")) {
                Element e = (Element) nl.item(i);
                TinyFactory fac = app.getFactory(e.getAttribute("factory"));
                if (fac != null) {
                    TinyPlugin p = fac.newPlugin(null);
                    p.setup(app, null);
                    p.configure(e);
                    p.setPluginName(e.getAttribute("name"));
                    JComponent jcomp = p.getVisualComponent();
                    jcomp.putClientProperty("plugin", p);
                    SP_Main.setRightComponent(jcomp);
                }
            }
        }
    }

    @Override
    public void cleanup() {
        SP_Main.removeMouseListener(this);
        JComponent jcomp = (JComponent) SP_Main.getLeftComponent();
        if (jcomp != null) {
            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            SP_Main.remove(jcomp);
            if (p != null) p.cleanup();
        }
        jcomp = (JComponent) SP_Main.getRightComponent();
        if (jcomp != null) {
            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            SP_Main.remove(jcomp);
            if (p != null) p.cleanup();
        }
    }

    @Override
    public void saveConfig(Element config) {
        if (config == null) return;

        config.setAttribute("orientation", "" + SP_Main.getOrientation());
        config.setAttribute("divider", ""+SP_Main.getDividerLocation());
        
        JComponent jcomp = (JComponent) SP_Main.getLeftComponent();
        if (jcomp != null) {
            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            Element e = config.getOwnerDocument().createElement("First");
            e.setAttribute("factory", p.getPluginFactory().getClass().getName());
            e.setAttribute("name", p.getPluginName().replace('&', ' '));
            p.saveConfig(e);
            config.appendChild(e);
        }
        jcomp = (JComponent) SP_Main.getRightComponent();
        if (jcomp != null) {
            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            Element e = config.getOwnerDocument().createElement("Second");
            e.setAttribute("factory", p.getPluginFactory().getClass().getName());
            e.setAttribute("name", p.getPluginName().replace('&', ' '));
            p.saveConfig(e);
            config.appendChild(e);
        }

    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public void setProperty(String name, Object value) {
        //---
    }

    //**************************************************************************
    //*** MouseListener
    //**************************************************************************
    @Override
    public void mouseClicked(MouseEvent e) {
        PU_Factories.show((Component) e.getSource(), e.getX(), e.getY());
        left = e.getSource() == SP_Main.getLeftComponent();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {

        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //---
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //---
    }

    //**************************************************************************
    //*** ActionListener
    //**************************************************************************
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("newPlugin")) {
            JMenuItem ji = (JMenuItem) e.getSource();
            TinyFactory factory = (TinyFactory) ji.getClientProperty("factory");
            TinyPlugin p = factory.newPlugin(null);
            p.setup(app, null);
            p.configure(null);
            JComponent jcomp = p.getVisualComponent();
            jcomp.putClientProperty("plugin", p);

            //--- Find where to add
            if (left) {
                SP_Main.setLeftComponent(jcomp);

            } else {
                SP_Main.setRightComponent(jcomp);
            }

        } else if (e.getActionCommand().equals("horizontal")) {
            SP_Main.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        } else if (e.getActionCommand().equals("vertical")) {
            SP_Main.setOrientation(JSplitPane.VERTICAL_SPLIT);

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PU_Factories = new javax.swing.JPopupMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        MN_Orientation = new javax.swing.JMenu();
        MN_Horizontal = new javax.swing.JRadioButtonMenuItem();
        MN_Vertical = new javax.swing.JRadioButtonMenuItem();
        BTG_TabOrientation = new javax.swing.ButtonGroup();
        SP_Main = new javax.swing.JSplitPane();

        PU_Factories.add(jSeparator1);
        PU_Factories.add(jSeparator2);

        MN_Orientation.setText("Orientation");

        BTG_TabOrientation.add(MN_Horizontal);
        MN_Horizontal.setSelected(true);
        MN_Horizontal.setText("Horizontal");
        MN_Horizontal.setActionCommand("horizontal");
        MN_Orientation.add(MN_Horizontal);

        BTG_TabOrientation.add(MN_Vertical);
        MN_Vertical.setText("Vertical");
        MN_Vertical.setActionCommand("vertical");
        MN_Orientation.add(MN_Vertical);

        PU_Factories.add(MN_Orientation);

        setLayout(new java.awt.BorderLayout());

        SP_Main.setDividerLocation(300);
        add(SP_Main, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.ButtonGroup BTG_TabOrientation;
    protected javax.swing.JRadioButtonMenuItem MN_Horizontal;
    protected javax.swing.JMenu MN_Orientation;
    protected javax.swing.JRadioButtonMenuItem MN_Vertical;
    protected javax.swing.JPopupMenu PU_Factories;
    protected javax.swing.JSplitPane SP_Main;
    protected javax.swing.JPopupMenu.Separator jSeparator1;
    protected javax.swing.JPopupMenu.Separator jSeparator2;
    // End of variables declaration//GEN-END:variables

}
