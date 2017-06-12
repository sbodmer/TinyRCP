/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.jar.Manifest;
import javax.swing.JOptionPane;

/**
 * Launch the boot process which is
 *
 * <PRE>
 * 1. Load the libraries jars in defined folder
 * 2. Start main class (defined as "Tiny-Main-Class" manifest attribute)
 * </PRE>
 *
 * Will boot the application in it's own thread<p>
 *
 * Each step will be notified to the passed listener (not in the UID thread)
 * <p>
 *
 * The libraries will be loaded and downloaded if url is a remote address<p>
 *
 * At the end, all jars will be search to find the manifes entry called
 * <PRE>
 * Tiny-Main-Class: {main class)
 * </PRE>
 *
 * If the manifest entry was found, the class in instantiated and the main
 * method is called with the passed arguments<p>
 *
 * @author sbodmer
 */
public class AppLauncher {

    /**
     * Will boot the application in it's own thread<p>
     *
     * Each step will be notified to the passed listener (not in the UID thread)
     * <p>
     *
     * The libraries will be loaded and downloaded if url is a remote address<p>
     *
     * At the end, all jars will be search to find the manifes entry called
     * <PRE>
     * Tiny-Main-Class: {main class)
     * </PRE>
     *
     * If the manifest entry was found, the class in instantiated and the main
     * method is called with the passed arguments<p>
     *
     * For manual factory creation, pass the following arguments
     * <PRE>
     * -factory {full.qualified.factory.class}
     * etc.
     * -main {full.qualified.main.class}
     * </PRE>
     *
     * @param listener The application boot listener for user feed back
     * @param libs The folder to use for dynamic plugin loading
     * @param args The main args to pass to application main frame
     * @param appName The application name
     */
    public static void boot(final AppLauncher.AppBootListener listener, final String libs[], final String args[], final String appName) {

        Thread t = new Thread() {
            @Override
            public void run() {

                //--- Prepare the main resource singleton
                JarClassLoader loader = new JarClassLoader(getClass().getClassLoader());
                listener.appBootStarted();

                //--- Start to load the libraries
                for (int i = 0; i < libs.length; i++) {
                    listener.appBootProgress("Resolving libraries", i, libs.length);
                    try {
                        URL url = new URL(libs[i]);
                        if (url.getPath().endsWith(".xml")) {
                            //TODO: Resolve                  xml
                            System.out.println("TODO: Resolve the xml file with the paths");

                        } else if (url.getPath().endsWith(".jar")) {
                            loader.addJar(url);
                        }

                    } catch (MalformedURLException ex) {
                        //--- Try local files
                        File f = new File(libs[i]);
                        if (f.exists()) {
                            if (f.isDirectory()) {
                                loader.addJar(libs[i]);

                            } else if (f.getName().endsWith(".xml")) {
                                //--- Try to resolve the list
                                //TODO: Resolve the xml file
                                System.out.println("TODO: Resolve the xml file with the paths");

                            } else if (f.getName().endsWith(".jar")) {
                                loader.addJar(libs[i]);
                            }

                        } else {
                            System.err.println("(E) Wrong libraries URL : " + libs[i]);
                        }

                    }
                }

                //--- Start the application
                //--- Find all the main class
                Iterator<Manifest> it = loader.getManifests().values().iterator();

                //--- The key is the name, the value the fq class
                Object mc = null;    //--- Main classfound 
                while (it.hasNext()) {
                    //--- Find the main entry point
                    java.util.jar.Attributes attributes = it.next().getMainAttributes();
                    String mainClass = attributes.getValue("Tiny-Main-Class");
                    if (mainClass == null) continue;

                    try {
                        //--- Create the instance of the main class
                        System.setSecurityManager(null);    //--- Disable custom classloader security (avoid the JavaAppletWindow text)
                        // Thread.currentThread().setContextClassLoader(loader);
                        Class cl = Class.forName(mainClass, true, loader);
                        Class cls[] = {String[].class};
                        Object params[] = {args};
                        Method method = cl.getDeclaredMethod("main", cls);
                        //--- Start application
                        mc = method.invoke(null, params);
                        break;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Main class error : " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);

                    } catch (NoClassDefFoundError er) {
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Main class error : " + er.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }

                }

                //--------------------------------------------------------------
                //--- If not found, try args one
                //--------------------------------------------------------------
                if (mc == null) {
                    for (int i = 0; i < args.length; i++) {
                        String arg = args[i];
                        if (arg.equals("-main")) {
                            try {
                                System.setSecurityManager(null);    //--- Disable custom classloader security (avoid the JavaAppletWindow text)
                                // Thread.currentThread().setContextClassLoader(loader);
                                Class cl = Class.forName(args[i+1], true, loader);
                                Class cls[] = {String[].class};
                                Object params[] = {args};
                                Method method = cl.getDeclaredMethod("main", cls);
                                //--- Start application
                                mc = method.invoke(null, params);
                                break;
                                
                            } catch (Exception ex) {
                                ex.printStackTrace();

                            }
                        }
                    }
                }

                listener.appBootFinished(mc);

            }
        };
        t.start();
    }

    static public interface AppBootListener {

        public void appBootStarted();

        public void appBootProgress(String message, int current, int total);

        /**
         * The called main class instance is passed as arguments
         *
         * @param mc
         */
        public void appBootFinished(Object mc);

    }
}
