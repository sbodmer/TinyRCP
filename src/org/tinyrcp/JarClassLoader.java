package org.tinyrcp;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

/**
 * Simple utility loader for some custom class loading<p>
 * 
 * Load classes from jar files in some specified directories
 * <p>
 *
 * If a jar file has a manifest, it will be stored for later use<p>
 *
 * @author sbodmer
 */
public class JarClassLoader extends URLClassLoader {

    /**
     * Holds the jar manifest (if exists)
     * <p>
     *
     * The key is the jar file name, the value the list of manifes entries<p>
     */
    public HashMap<String, Manifest> manifests = new HashMap<String, Manifest>();

    public JarClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }

    public JarClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        prepare();
    }

    public JarClassLoader(URL urls[]) {
        super(urls);
        prepare();
    }

    public JarClassLoader(URL urls[], ClassLoader parent) {
        super(urls, parent);
        prepare();
    }

    public JarClassLoader(URL urls[], ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        prepare();
    }

    private void prepare() {
        //--- Do some stuff here
        
    }
    //***************************************************************************
    //*** API
    //***************************************************************************
    public static void stackJarFiles(ArrayList<File> v, File f) {
        if (f.isDirectory()) {
            File files[] = f.listFiles();
            for (int i = 0; i < files.length; i++) stackJarFiles(v, files[i]);

        } else {
            if (f.getName().endsWith(".jar")) v.add(f);
        }

    }
    
    /**
     * Manual adding a local jar file or directory.<p>
     *
     * If it's a directory, it will be scanned recusively to retrieve all
     * .jar<p>
     *
     * @param path The path to the jar file or directory
     */
    public void addJar(String path) {
        //--- Stacking of .jar
        ArrayList<File> v = new ArrayList<>();
        File f = new File(path);
        if (f.exists()) stackJarFiles(v, f);
        
        for (int i = 0; i < v.size(); i++) {
            f = v.get(i);
            try {
                if (f.exists() == false) continue;
                if (f.isDirectory()) continue;
                System.out.println("(I) Jar found : " + f.getPath());

                JarFile jfile = new JarFile(f, true);
                Manifest manifest = jfile.getManifest();
                if (manifest != null) manifests.put(f.getPath(), manifest);

                // Enumeration<JarEntry> entries = jfile.entries();
                // while (entries.hasMoreElements()) System.out.println("Entry:"+entries.nextElement());
                //--- Pass to parent to do the real work
                addURL(f.toURI().toURL());

            } catch (MalformedURLException ex) {
                ex.printStackTrace();

            } catch (IOException ex) {
                ex.printStackTrace();

            } catch (SecurityException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void addJar(URL url) {
        addURL(url);
    }
    /**
     * Return the manifest of the jar if present<p>
     *
     * @return The manifest object
     * @param jarname The jarname
     */
    public Manifest getManifest(String jarname) {
        return manifests.get(jarname);
    }

    /**
     * Return the full hashtable of manifest<p>
     *
     * @return The manifest hashtable
     */
    public HashMap<String, Manifest> getManifests() {
        return manifests;
    }

    @Override
    public String toString() {
        return "JarClassLoader : (" + manifests.size() + " manifests)";
    }

    /*
     @Override
     public URL getResource(String name) {
     System.out.println("Searching:"+name);
     return super.getResource(name);
     }
     */
 /*
    public InputStream getResourceAsStream(String name) {
        URL urls[] = getURLs();
        for (int i=0;i<urls.length;i++) System.out.println("URL["+i+"]="+urls[i]);
        return super.getResourceAsStream(name);
        
    }
     */
    @Override
    public Class findClass(String classname) throws ClassNotFoundException {
        Class cl = super.findClass(classname);
        System.out.println(">>>> Looking for "+classname+" FOUND:"+cl);
        return cl;
    }
    
    
    

    //***************************************************************************
    //*** Private
    //***************************************************************************
    
    //**************************************************************************
    //*** TEST
    //**************************************************************************
    public static void main(String args[]) {
        // System.out.println("(M) Default java.protocol.handler.pkgs:"+System.getProperty("java.protocol.handler.pkgs"));
        // System.out.println("(M) New java.protocol.handler.pkgs:"+System.getProperty("java.protocol.handler.pkgs"));
        System.setProperty("java.security.debug", "all");
        System.setProperty("java.net.debug", "all");

        File cwd = new File(System.getProperty("user.dir"));
        System.out.println("Searching in current dir :" + cwd.getPath());
        try {
            File test = new File("lib/ext/panels/Earth.jar");
            
            System.out.println("PARENT:"+Class.forName("app.JarClassLoader").getClassLoader());
            JarClassLoader loader = new JarClassLoader(new URL[0]);
            loader.addJar("lib/ext");
            // System.out.println("(M) New java.protocol.handler.pkgs:" + System.getProperty("java.protocol.handler.pkgs"));
            // loader.addJar("dist");
            Class cl = loader.loadClass("org.wwe.earth.JEarthWWEFactory", true);
            System.out.println("Class:"+cl);
            System.out.println("Loader:"+cl.getClassLoader());
            
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

}
