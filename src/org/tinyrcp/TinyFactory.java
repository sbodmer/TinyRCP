/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp;

import javax.swing.Icon;
import javax.swing.JComponent;
import org.w3c.dom.Element;

/**
 *
 * @author sbodmer
 */
public interface TinyFactory {
    static final String PLUGIN_CATEGORY_PANEL       = "panel";
    static final String PLUGIN_CATEGORY_COMPONENT   = "component";
    
    static final String PLUGIN_FAMILY_CONTAINER = "container";
    static final String PLUGIN_FAMILY_PANEL     = "panel";
    static final String PLUGIN_FAMILY_COMPONENT = "component";
    
    /**
     * The wanted icon size (22x22)
     */
    public static final int ICON_SIZE_NORMAL = 22;
    
    /**
     * The small version (16x16)
     */
    public static final int ICON_SIZE_SMALL = 16;
    
    /**
     * The big version (32x32)
     */
    public static final int ICON_SIZE_BIG = 32;
    
    /**
     * To get some licence text<p>
     *
     * String<p>
     *
     * Read only<p>
     *
     * Return null if none
     */
    public static final String PROPERTY_LICENCE_TEXT = "factoryLicenceText";
    
    /**
     * Some small copyright text<p>
     * 
     * String<p>
     * 
     * Read only<p>
     * 
     * Returns null if none
     */
    public static final String PROPERTY_COPYRIGHT_TEXT = "factoryCopyright";
    
    /**
     * The author<p>
     * 
     * String<p>
     * 
     * Read only<p>
     * 
     * return null if none
     */
    public static final String PROPERTY_AUTHOR = "factoryAuthor";
    
    /**
     * The factory version<p>
     * 
     * String<p>
     * 
     * Read only<p>
     * 
     * return null if none
     */
    public static final String PROPERTY_VERSION = "factoryVersion";
    
    /**
     * Returns the factory icon as, if the wanted
     * size is not available return the normal one which
     * is 22x22<p>
     * 
     * @return 
     */
    public Icon getFactoryIcon(int size);
    
    /**
     * The plugin factory name
     *
     * @return 
     */
    public String getFactoryName();
    
    /**
     * Returns some description
     * @return 
     */
    public String getFactoryDescription();
    
    /**
     * Returns the category of the plugin factory (used to determine in which context
     * expose this factory)<p>
     * 
     * @return 
     */
    public String getFactoryCategory();
    
    /**
     * Return the additional family in the category
     * 
     * @return 
     */
    public String getFactoryFamily();
    
    /**
     * Additional check of the factory capabilities
     * 
     * The passed object could be anything, return true if supported
     * 
     * @param obj
     * @return 
     */
    public boolean doesFactorySupport(Object obj);
    
    /**
     * Return the global factory component or null if none<p>
     * 
     * @return 
     */
    public JComponent getFactoryConfigComponent();
    
    /**
     * This method is called once after the factory was instantiated<p>
     * 
     * @param app 
     */
    public void initialize(App app);
    
    /**
     * Is called after the factory was instantiated ans initilized<p>
     * 
     * This method can be called multiple times, but it's called at least
     * once<p>
     * 
     * 
     * @param app
     * @param config 
     */
    public void configure(Element config);
    
    /**
     * Save the configuration
     * @param config 
     */
    public void store(Element config);
    
    /**
     * Release all resources
     */
    public void destroy();
    
    /**
     * Return a new instance of the plugin
     * 
     * @param argument
     * @return 
     */
    public TinyPlugin newPlugin(Object argument);
    
    /**
     * Returns some property
     * 
     * @param property
     * @return 
     */
    public Object getProperty(String property);
    
}
