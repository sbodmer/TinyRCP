/*
 * Copyright (C) 2017 sbodmer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tinyrcp;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The panel for the factory settings will be called each time the factory is
 * selected, so it's possible to call TinyFactory.getConfigComponent() from
 * other component without corrupting this one<p>
 *
 * The list is settings are created once in the initialize method, no dynamic
 * factory adding is supported<p>
 * 
 * @author sbodmer
 */
public class JSettingsFrame extends javax.swing.JFrame implements ListSelectionListener {

    App app = null;

    DefaultListModel<TinyFactory> model = new DefaultListModel<>();

    /**
     * Creates new form JPlugins
     */
    public JSettingsFrame() {
        initComponents();

        LI_Factories.setModel(model);

    }

    //**************************************************************************
    //*** API
    //**************************************************************************
    public void initialize(App app) {
        this.app = app;

        LI_Factories.setCellRenderer(new JTinyFactoryCellRenderer(app, true));
        ArrayList<TinyFactory> facs = app.getFactories(null);
        for (int i = 0; i < facs.size(); i++) {
            TinyFactory f = facs.get(i);
            JComponent jcomp = f.getFactoryConfigComponent();
            if (jcomp != null) model.addElement(f);

        }
        LI_Factories.addListSelectionListener(this);
        // if (facs.size() > 0) LI_Factories.setSelectedIndex(0);

    }

    /**
     * Select the factory
     *
     * @param tiny
     */
    public void select(TinyFactory tiny) {
        for (int i = 0; i < model.size(); i++) {
            TinyFactory f = model.get(i);
            if (f == tiny) {
                LI_Factories.setSelectedValue(tiny, true);
                break;
            }
        }
    }

    //**************************************************************************
    //*** ListSelectionListener
    //**************************************************************************
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == LI_Factories) {
            if (e.getValueIsAdjusting() == false) {
                TinyFactory f = LI_Factories.getSelectedValue();

                PN_Settings.removeAll();
                if (f == null) {
                    LB_Name.setText("...");
                    LB_Name.setIcon(null);
                    
                } else {
                    JComponent jcomp = f.getFactoryConfigComponent();
                    PN_Settings.add(jcomp);
                    PN_Settings.revalidate();
                    LB_Name.setText(f.getFactoryName());
                    LB_Name.setIcon(f.getFactoryIcon(22));
                }
                PN_Settings.repaint();
            }
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

        jPanel1 = new javax.swing.JPanel();
        LB_Name = new javax.swing.JLabel();
        PN_Settings = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        LI_Factories = new javax.swing.JList<>();

        LB_Name.setText("...");

        PN_Settings.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_Settings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(LB_Name, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 24, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(LB_Name, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PN_Settings, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(250, 480));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 134));

        jScrollPane1.setViewportView(LI_Factories);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.WEST);

        setBounds(0, 0, 810, 630);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JLabel LB_Name;
    protected javax.swing.JList<TinyFactory> LI_Factories;
    protected javax.swing.JPanel PN_Settings;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel3;
    protected javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
