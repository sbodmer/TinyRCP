/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp.example.dummy;

import org.tinyrcp.tabs.*;
import javax.swing.Icon;
import org.tinyrcp.App;
import org.tinyrcp.TinyFactory;
import org.tinyrcp.TinyPlugin;
import org.w3c.dom.Element;

/**
 *
 * @author sbodmer
 */
public class JDummyFactory extends javax.swing.JPanel implements TinyFactory {

    /**
     * Creates new form OSMBuildingsWWELayerPluginFactory
     */
    public JDummyFactory() {
        initComponents();
    }

    //**************************************************************************
    //*** WWEPluginFactory
    //**************************************************************************
    @Override
    public TinyPlugin newPlugin(Object arg) {
        return new JDummyPlugin(this);
    }

    @Override
    public Icon getFactoryIcon(int size) {
        return LB_Name.getIcon();
    }

    @Override
    public String getFactoryName() {
        return LB_Name.getText();
    }
    
    @Override
    public String getFactoryDescription() {
        return LB_Description.getText();
    }
    
    @Override
    public String getFactoryCategory() {
        return PLUGIN_CATEGORY_PANEL;
    }
    
    @Override
    public String getFactoryFamily() {
        return PLUGIN_FAMILY_PANEL;
    }
    
    @Override
    public void initialize(App app) {
        //---
    }
    
    @Override
    public void configure(Element config) {
        //---
    }
    
    @Override
    public void store(Element config) {
        //---
    }
    
    @Override
    public void destroy() {
        //---
    }
    
    @Override
    public Object getProperty(String property) {
        return null;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LB_Name = new javax.swing.JLabel();
        LB_Description = new javax.swing.JLabel();

        LB_Name.setText("Dummy example panel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JLabel LB_Description;
    protected javax.swing.JLabel LB_Name;
    // End of variables declaration//GEN-END:variables

    

    
}
