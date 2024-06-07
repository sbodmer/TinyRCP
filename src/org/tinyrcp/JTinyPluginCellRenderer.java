/*
 * JWorldWindLayerCellRenderer.java
 *
 * Created on June 7, 2007, 11:03 AM
 */
package org.tinyrcp;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author sbodmer
 */
public class JTinyPluginCellRenderer extends javax.swing.JPanel implements ListCellRenderer, TableCellRenderer {

    App app = null;

    /**
     *
     */
    public JTinyPluginCellRenderer(App app) {
        this.app = app;

        initComponents();
       
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Color fg = isSelected?list.getSelectionForeground():list.getForeground();
        // setForeground(fg);
        
        TinyPlugin f = (TinyPlugin) value;
        LB_Name.setText(f.getPluginName());
        LB_Icon.setIcon(f.getPluginFactory().getFactoryIcon(22));
        if (isSelected) {
            setBackground(list.getSelectionBackground());

        } else {
            setBackground(list.getBackground());
        }
        return this;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TinyPlugin f = (TinyPlugin) value;
        LB_Name.setText(f.getPluginName());
        LB_Icon.setIcon(f.getPluginFactory().getFactoryIcon(22));
        if (isSelected) {
            setBackground(table.getSelectionBackground());

        } else {
            setBackground(table.getBackground());
        }
        return this;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LB_Icon = new javax.swing.JLabel();
        LB_Name = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        LB_Icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_Icon.setIconTextGap(0);
        LB_Icon.setMaximumSize(new java.awt.Dimension(30, 30));
        LB_Icon.setMinimumSize(new java.awt.Dimension(30, 30));
        LB_Icon.setPreferredSize(new java.awt.Dimension(30, 30));
        add(LB_Icon, java.awt.BorderLayout.WEST);

        LB_Name.setText("Name");
        LB_Name.setMinimumSize(new java.awt.Dimension(0, 10));
        add(LB_Name, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LB_Icon;
    private javax.swing.JLabel LB_Name;
    // End of variables declaration//GEN-END:variables

    

    

}