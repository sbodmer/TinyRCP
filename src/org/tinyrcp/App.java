/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.Manifest;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.tinyrcp.desk.JDeskFactory;
import org.tinyrcp.tabs.JTabsFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Application resources singleton<p>
 *
 * Store all shared resources here<p>
 *
 * @author sbodmer
 */
public class App {

    /**
     * Main class loader for all plugins
     */
    protected JarClassLoader loader = null;

    /**
     * The application folder to store the configuration and load plugins
     */
    protected File appFolder = null;
    protected String appName = "application";

    /**
     * List of all found factories grouped by category (extracted from manifest)
     */
    protected HashMap<String, ArrayList<TinyFactory>> factories = new HashMap<>();

    /**
     * XML builder
     */
    protected DocumentBuilder builder = null;

    /**
     * Store the factory origin file, the key is the factory, the value
     * the jar containing the factory class
     */
    protected HashMap<TinyFactory, String> origin = new HashMap<>();
    
    /**
     * Instantiate all the factories here, the configuration of the factories is
     * done late<p>
     *
     * If the manualFactories param is filled, then the passed classes are also
     * created and added to the factories list<p>
     *
     * @param loader
     * @param appName
     * @param manualFactories The list of manual factories to add (or null if
     * none), the array contains the full qualified class names
     */
    public App(JarClassLoader loader, String appName, ArrayList<String> manualFactories) {
        this.loader = loader;
        this.appName = appName;
        appFolder = new File(System.getProperty("user.home"), "." + appName);
        appFolder.getParentFile().mkdirs();

        //--- Create the xml builder
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();

        } catch (Exception ex) {
            ex.printStackTrace();

        }

        //--- Insert built in factories
        ArrayList<TinyFactory> list = new ArrayList<>();
        list.add(new JTabsFactory());
        list.add(new JDeskFactory());
        factories.put(TinyFactory.PLUGIN_CATEGORY_PANEL, list);

        //----------------------------------------------------------------------
        //--- Prepare the manual factories
        //----------------------------------------------------------------------
        if (manualFactories != null) {
            for (int i = 0; i < manualFactories.size(); i++) {
                String cla = manualFactories.get(i);
                try {
                    Class cl = Class.forName(cla, true, loader);
                    //--- Always empty constructor
                    TinyFactory factory = (TinyFactory) cl.newInstance();
                    list = factories.get(factory.getFactoryCategory());
                    if (list == null) {
                        list = new ArrayList<>();
                        factories.put(factory.getFactoryCategory(), list);
                    }
                    list.add(factory);
                    System.out.println("(I) Manual Tiny Factory instantiated [" + factory.getFactoryCategory() + "] " + factory.getFactoryName());
                    System.out.flush();
                    
                } catch (Exception ex) {
                    System.err.println("(E) Manual Tiny Factory " + cla + " :" + ex.getMessage());
                    ex.printStackTrace();


                }
            }
        }
        
        //----------------------------------------------------------------------
        //--- Prepare dynamic loaded factories
        //----------------------------------------------------------------------
        Iterator<String> paths = loader.getManifests().keySet().iterator();
        //--- Prepare the classes to instantiate
        while (paths.hasNext()) {
            String path = paths.next();
            
            //--- Do until the attribut was not found
            Manifest manifest = loader.getManifest(path);
            java.util.jar.Attributes attr = manifest.getMainAttributes();
            String classnames = attr.getValue("Tiny-Factory");

            if (classnames != null) {
                //--- Split classes
                String classname[] = classnames.split(" ");
                for (int i = 0; i < classname.length; i++) {
                    String cla = classname[i].trim();
                    try {
                        Class cl = Class.forName(cla.trim(), true, loader);
                        //--- Always empty constructor
                        TinyFactory factory = (TinyFactory) cl.newInstance();
                        list = factories.get(factory.getFactoryCategory());
                        if (list == null) {
                            list = new ArrayList<>();
                            factories.put(factory.getFactoryCategory(), list);
                        }
                        list.add(factory);
                        System.out.println("(I) Dynamic Tiny Factory instantiated [" + factory.getFactoryCategory() + "] " + factory.getFactoryName());
                        System.out.flush();
                        origin.put(factory, path);
                    
                    } catch (Exception ex) {
                        System.err.println("(E) Dynamic Tiny Factory " + cla + " :" + ex.getMessage());
                        ex.printStackTrace();

                    }
                }
            }
        }

    }

    //**************************************************************************
    //*** API
    //**************************************************************************
    public JarClassLoader getLoader() {
        return loader;
    }

    public String getAppName() {
        return appName;
    }

    public File getAppFolder() {
        return appFolder;
    }

    /**
     * Returns the shared document builder instance<p>
     *
     * @param in
     * @return
     */
    public DocumentBuilder getDocumentBuilder() {
        return builder;
    }

    /**
     * Create the menu which lists the plugin for the family<p>
     *
     * The passed listener will received an event when item is selected
     * <PRE>
     * ID            : ACTION_PERFORMED
     * ActionCommand : "newPlugin"
     * ClientProperty: "factory" (TinyFactory)
     * </PRE>
     *
     * @param listener
     * @return
     */
    public JMenu createFactoryMenus(String title, String category, String family, ActionListener listener) {
        JMenu jmenu = new JMenu(title);
        // jmenu.setFont(new Font("Arial", 0, 11));
        jmenu.setActionCommand("menu");
        ArrayList<TinyFactory> facs = getFactories(category);
        for (int i = 0; i < facs.size(); i++) {
            TinyFactory factory = facs.get(i);
            if ((family != null) && !factory.getFactoryFamily().equals(family)) continue;

            //--- create menu
            JMenuItem jitem = new JMenuItem(factory.getFactoryName());
            jitem.setIcon(factory.getFactoryIcon(TinyFactory.ICON_SIZE_NORMAL));
            // jitem.setFont(new java.awt.Font("Arial", 0, 11));
            jitem.setActionCommand("newPlugin");
            jitem.putClientProperty("factory", factory);
            jitem.addActionListener(listener);
            jmenu.add(jitem);

        }
        return jmenu;

    }

    /**
     * Initilaize the factories
     *
     */
    public void initialize() {
        ArrayList<TinyFactory> facs = getFactories(null);
        for (int i = 0; i < facs.size(); i++) facs.get(i).initialize(this);
    }

    /**
     * Configure all factories with the pass config (if config is null, the
     * factory are configured with a null element)
     * <p>
     *
     * This method can be called multiple times<p>
     *
     * @param config
     */
    public void configure(Element config) {
        //--- Store each factory element for later use
        HashMap<String, Element> configs = new HashMap<>();
        if (config != null) {
            NodeList nl = config.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeName().equals("TinyFactory")) {
                    Element e = (Element) n;
                    configs.put(e.getAttribute("class"), e);
                }
            }
        }

        //--- Find all factories
        ArrayList<TinyFactory> facs = getFactories(null);

        //--- Configure factories
        for (int i = 0; i < facs.size(); i++) {
            TinyFactory fac = facs.get(i);
            fac.configure(configs.get(fac.getClass().getName()));

        }

    }

    /**
     * Store the config for each factory
     *
     * @param config
     */
    public void store(Element config) {
        if (config == null) return;

        ArrayList<TinyFactory> facs = getFactories(null);
        for (int i = 0; i < facs.size(); i++) {
            TinyFactory fac = facs.get(i);
            Element e = config.getOwnerDocument().createElement("TinyFactory");
            e.setAttribute("class", fac.getClass().getName());
            fac.store(e);
            config.appendChild(e);
        }

    }

    public void destroy() {
        ArrayList<TinyFactory> facs = getFactories(null);
        for (int i = 0; i < facs.size(); i++) facs.get(i).destroy();
    }

    /**
     * Add a new factory to the correct category<p>
     * 
     * The passed factory should be fully realized (inited and confiugured)<p>
     *
     * @param factory
     */
    public void addFactory(TinyFactory factory) {
        ArrayList<TinyFactory> facs = factories.get(factory.getFactoryCategory());
        if (facs == null) {
            facs = new ArrayList<>();
            factories.put(factory.getFactoryCategory(), facs);
        }
        if (!facs.contains(factory)) facs.add(factory);
    }

    /**
     * Return the factory instance for the given factory class name (if present)
     *
     * If the factory is not found, null is returned<p>
     *
     * @param classname
     * @param category
     * @return
     */
    public TinyFactory getFactory(String classname) {
        ArrayList<TinyFactory> fac = getFactories(null);
        for (int i = 0; i < fac.size(); i++) {
            TinyFactory f = fac.get(i);
            if (f.getClass().getName().equals(classname)) return f;
        }
        return null;
    }

    /**
     * Return the origin jar file path or null if not loaded from an external
     * jar<p>
     * 
     * @param f
     * @return 
     */
    public String getFactoryOrigin(TinyFactory f) {
        return origin.get(f);
    }
    
    /**
     * Return the list of the factories or en empty vector if none were
     * present<p>
     *
     * If null is passed, then all factories are returned<p>
     *
     * @param category The factories category
     * @return
     */
    public ArrayList<TinyFactory> getFactories(String category) {
        ArrayList<TinyFactory> fac = new ArrayList<>();
        if (category != null) {
            ArrayList<TinyFactory> tmp = factories.get(category);
            if (tmp != null) for (int i = 0; i < tmp.size(); i++) fac.add(tmp.get(i));

        } else {
            Iterator<ArrayList<TinyFactory>> it = factories.values().iterator();
            while (it.hasNext()) {
                ArrayList<TinyFactory> f = it.next();
                for (int i = 0; i < f.size(); i++) fac.add(f.get(i));
            }
        }
        return fac;
    }

    static public void showScreenIdentifiers() {
        //--- Try to find the selected graphics device
        final ArrayList<JFrame> frames = new ArrayList<JFrame>();

        //--- Find available screens
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        for (int i = 0; i < gds.length; i++) {
            GraphicsDevice gd = gds[i];

            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            JFrame jt = new JFrame("", gc);
            jt.setUndecorated(true);
            JLabel jlabel = new JLabel(gd.getIDstring());
            jt.add(jlabel);
            jlabel.setOpaque(true);
            jlabel.setBackground(Color.BLUE);
            jlabel.setForeground(Color.WHITE);
            // jt.setSize(200,200);
            jt.pack();
            jt.setVisible(true);

            frames.add(jt);
        }

        Thread t = new Thread() {
            public void run() {
                setName("ScreenIdentifier closing thread");
                try {
                    sleep(5000);
                    for (int i = 0; i < frames.size(); i++) {
                        JFrame jt = frames.get(i);
                        jt.setVisible(false);
                        jt.dispose();
                    }

                } catch (InterruptedException ex) {
                    //---
                }
            }
        };
        t.start();

    }
}
