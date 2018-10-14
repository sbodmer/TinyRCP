package org.tinyrcp.desk;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.tinyrcp.App;
import org.tinyrcp.TinyFactory;
import org.tinyrcp.TinyPlugin;
import org.w3c.dom.*;

/**
 *
 * @author sbodmer
 */
public class JDeskPlugin extends JPanel implements TinyPlugin, ActionListener, InternalFrameListener {

    ResourceBundle bundle = null;
    App app = null;
    TinyFactory factory = null;
    String name = "Desktop";

    /**
     *
     * @param factory
     */
    public JDeskPlugin(TinyFactory factory) {
        this.factory = factory;
        bundle = ResourceBundle.getBundle("org.tinyrcp.desk.Desk");

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
        JInternalFrame iframes[] = DT_Desktop.getAllFrames();
        for (int i=0;i<iframes.length;i++) {
            JInternalFrame iframe = iframes[i];
            TinyPlugin p = (TinyPlugin) iframe.getClientProperty("plugin");
            iframe.remove(p.getVisualComponent());
            p.cleanup();
            
        }
        DT_Desktop.removeAll();
        
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

        JInternalFrame components[] = DT_Desktop.getAllFrames();
        for (int i = 0; i < components.length; i++) {
            JInternalFrame jiframe = components[i];
            TinyPlugin p = (TinyPlugin) jiframe.getClientProperty("plugin");
            Element e = config.getOwnerDocument().createElement("InternalFrame");
            e.setAttribute("x", "" + jiframe.getLocation().x);
            e.setAttribute("y", "" + jiframe.getLocation().y);
            e.setAttribute("width", "" + jiframe.getWidth());
            e.setAttribute("height", "" + jiframe.getHeight());
            e.setAttribute("name", jiframe.getTitle());
            e.setAttribute("iconified", "" + jiframe.isIcon());
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
    public void setup(App app, Object param) {
        this.app = app;

        //------------------------------------------------------------------------
        //--- Create Windows menu with the panels to add
        //------------------------------------------------------------------------
        JMenu jmenu = app.createFactoryMenus("Panels", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_PANEL, this);
        PU_Panels.add(jmenu, 0);
        jmenu = app.createFactoryMenus("Container", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_CONTAINER, this);
        PU_Panels.add(jmenu, 0);   

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
        
        //--- Init Frames
        NodeList nl = config.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals("InternalFrame")) {
                //--- Found a internalframe
                Element inf = (Element) nl.item(i);

                TinyFactory factory = app.getFactory(inf.getAttribute("factory"));
                if (factory != null) {
                    TinyPlugin p = factory.newPlugin(null);
                    p.setup(app, null);
                    p.configure(inf);
                    
                    JInternalFrame jiframe = new JInternalFrame(inf.getAttribute("name"));
                    jiframe.setFont(new Font("Arial", 0, 11));
                    jiframe.setTitle(inf.getAttribute("name"));
                    jiframe.setClosable(true);
                    jiframe.setIconifiable(true);
                    jiframe.setResizable(true);
                    jiframe.addInternalFrameListener(this);

                    try {
                        jiframe.setLocation(Integer.valueOf(inf.getAttribute("x")).intValue(), Integer.valueOf(inf.getAttribute("y")).intValue());
                        jiframe.setSize(Integer.valueOf(inf.getAttribute("width")).intValue(), Integer.valueOf(inf.getAttribute("height")).intValue());

                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                    DT_Desktop.add(jiframe);
                    DT_Desktop.revalidate();
                    DT_Desktop.repaint();
                    DT_Desktop.setSelectedFrame(jiframe);

                    Component compo = p.getVisualComponent();
                    compo.setSize(jiframe.getSize());
                    jiframe.getContentPane().add(compo);
                    jiframe.revalidate();
                    jiframe.setVisible(true);
                    jiframe.repaint();
                    jiframe.putClientProperty("plugin", p);
                    
                }

            }
        }
        
    }
    //***************************************************************************
    //*** InternalFrameListener
    //***************************************************************************

    @Override
    public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
        //---
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent evt) {
        try {
            JInternalFrame jiframe = evt.getInternalFrame();
            jiframe.removeInternalFrameListener(this);
            jiframe.removeAll();
            DT_Desktop.remove(jiframe);
            TinyPlugin p = (TinyPlugin) jiframe.getClientProperty("plugin");
            p.cleanup();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
        //---
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {
        //---
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {
        //---
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
        //---
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {
        //---
    }
    //***************************************************************************
    //*** ActionListener
    //***************************************************************************

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getActionCommand().equals("newPlugin")) {
            String name = JOptionPane.showInputDialog(this, bundle.getString("opt_framename"));
            if (name != null) {
                JMenuItem ji = (JMenuItem) e.getSource();
                TinyFactory fac = (TinyFactory) ji.getClientProperty("factory");
                TinyPlugin p = fac.newPlugin(null);
                p.setup(app, null);
                p.configure(null);
                JComponent jcomp = p.getVisualComponent();
                jcomp.putClientProperty("plugin", p);

                JInternalFrame jiframe = new JInternalFrame(name);
                jiframe.getContentPane().add(p.getVisualComponent());
                jiframe.setSize(320, 240);
                jiframe.setClosable(true);
                jiframe.setIconifiable(true);
                jiframe.setResizable(true);
                jiframe.addInternalFrameListener(this);
                DT_Desktop.add(jiframe);
                jiframe.setVisible(true);
                jiframe.putClientProperty("plugin", p);
                DT_Desktop.repaint();

            }

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
        DT_Desktop = new javax.swing.JDesktopPane();

        setLayout(new java.awt.BorderLayout());

        DT_Desktop.setBackground(new java.awt.Color(204, 204, 255));
        DT_Desktop.setDoubleBuffered(true);
        DT_Desktop.setPreferredSize(new java.awt.Dimension(640, 480));
        DT_Desktop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mouseEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mouseEvent(evt);
            }
        });
        add(DT_Desktop, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

   private void mouseEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseEvent
       if (evt.isPopupTrigger()) {
           PU_Panels.show(evt.getComponent(), evt.getX(), evt.getY());
       }
   }//GEN-LAST:event_mouseEvent
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JDesktopPane DT_Desktop;
    public javax.swing.JPopupMenu PU_Panels;
    // End of variables declaration//GEN-END:variables

}
