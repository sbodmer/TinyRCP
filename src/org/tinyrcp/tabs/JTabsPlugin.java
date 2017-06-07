/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp.tabs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javafx.scene.control.Tab;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.tinyrcp.App;
import org.tinyrcp.TinyFactory;
import org.tinyrcp.TinyPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author sbodmer
 */
public class JTabsPlugin extends javax.swing.JPanel implements TinyPlugin, MouseListener, ActionListener {

    TinyFactory factory = null;
    App app = null;

    /**
     * Creates new form JTabsPlugin
     */
    public JTabsPlugin(TinyFactory factory) {
        this.factory = factory;

        initComponents();

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
    public Object doAction(String message, Object argument) {
        return null;
    }

    @Override
    public void setup(App app, Object argument) {
        this.app = app;

        MN_Delete.addActionListener(this);

        PU_Factories.add(app.createFactoryMenus("Panels", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_PANEL,this),0);
        PU_Factories.add(app.createFactoryMenus("Containers", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_CONTAINER,this),1);
        TAB_Tabs.addMouseListener(this);
    }

    @Override
    public void configure(Element config) {
        if (config == null) return;

        NodeList nl = config.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals("Tab")) {
                Element e = (Element) nl.item(i);
                TinyFactory fac = app.getFactory(e.getAttribute("factory"));
                if (fac != null) {
                    TinyPlugin p = fac.newPlugin(null);
                    p.setup(app, null);
                    p.configure(e);
                    JComponent jcomp = p.getVisualComponent();
                    jcomp.putClientProperty("plugin", p);
                    TAB_Tabs.addTab(p.getPluginName(), jcomp);
                }
            }
        }
    }

    @Override
    public void cleanup() {
        TAB_Tabs.removeMouseListener(this);
        while(TAB_Tabs.getTabCount()>0) {
            JComponent jcomp = (JComponent) TAB_Tabs.getComponentAt(0);
            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            TAB_Tabs.remove(jcomp);
            p.cleanup();
        }
    }

    @Override
    public void saveConfig(Element config) {
        if (config == null) return;
        
        for (int i=0;i<TAB_Tabs.getTabCount();i++) {
            JComponent jcomp = (JComponent) TAB_Tabs.getComponentAt(i);
            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            Element e = config.getOwnerDocument().createElement("Tab");
            e.setAttribute("factory", p.getPluginFactory().getClass().getName());
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
        //---
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) PU_Factories.show(this, e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) PU_Factories.show(this, e.getX(), e.getY());
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
            TAB_Tabs.addTab(p.getPluginName(), jcomp);
            TAB_Tabs.setSelectedComponent(p.getVisualComponent());
            
        } else if (e.getActionCommand().equals("delete")) {
            JComponent jcomp = (JComponent) TAB_Tabs.getSelectedComponent();
            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            TAB_Tabs.remove(jcomp);
            p.cleanup();

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
        MN_Delete = new javax.swing.JMenuItem();
        TAB_Tabs = new javax.swing.JTabbedPane();

        PU_Factories.add(jSeparator1);

        MN_Delete.setText("Delete");
        MN_Delete.setActionCommand("delete");
        PU_Factories.add(MN_Delete);

        setLayout(new java.awt.BorderLayout());
        add(TAB_Tabs, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JMenuItem MN_Delete;
    protected javax.swing.JPopupMenu PU_Factories;
    protected javax.swing.JTabbedPane TAB_Tabs;
    protected javax.swing.JPopupMenu.Separator jSeparator1;
    // End of variables declaration//GEN-END:variables

}
