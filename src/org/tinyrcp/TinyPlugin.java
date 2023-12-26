/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp;

import java.awt.Component;
import javax.swing.JComponent;
import org.w3c.dom.Element;

/**
 * The plugin for the TinyRCP<p>
 * 
 * @author sbodmer
 */
public interface TinyPlugin {
    /**
     * New size for the layer<p>
     *
     * Dimension<p>
     */
    public static final String DO_ACTION_NEWSIZE = "org.tinyrcp.newSize";
    
    /**
     * Return the plugin factory instance
     * 
     * @return 
     */
    public TinyFactory getPluginFactory();
    
    /**
     * Return the plugin custom name
     * 
     * @return 
     */
    public String getPluginName();
    
    public void setPluginName(String name);
    
    /**
     * Returns the main visual component (or null if none)
     * 
     * @return 
     */
    public JComponent getVisualComponent();
    
    /**
     * Return the component to configure this plugin instance
     * 
     * @return 
     */
    public JComponent getConfigComponent();
    
    /**
     * Do some action<p>
     * 
     * @param message
     * @param argument
     * @param subject
     * @return 
     */
    public Object doAction(String message, Object argument, Object subject);
    
    /**
     * Allocate/create resource for the passed configuration
     * 
     * @param app
     * @param argument
     */
    public void setup(App app, Object argument);
     
    /**
     * Configure the plugin, this method can be called multiple times<p>
     * 
     * @param config 
     */
    public void configure(Element config);
    
    /**
     * Free the resources, after that, the component is no more configured
     * 
     */
    public void cleanup();
    
    /**
     * Fill the passed xml node with the plugin configuration
     * 
     * @param config 
     */
    public void saveConfig(Element config);
    
    /**
     * Returns some property
     * 
     * @param name
     * @return 
     */
    public Object getProperty(String name);
    
    /**
     * Set a property
     * 
     * @param name
     * @param value 
     */
    public void setProperty(String name, Object value);
    
}
