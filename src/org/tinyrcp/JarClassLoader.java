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
     * The instantated classed, the key is the class full qualified name<p>
     *
     */
    protected HashMap<String, Class> classes = new HashMap<String, Class>();

    /**
     * The temp raw class as byte (the key is the full qualified name), the byte
     * will be removed when found and converted to class<p>
     */
    protected HashMap<String, byte[]> memory = new HashMap<String, byte[]>();

    /**
     * The resources from the jars in memory are stored tmp folder
     *
     * The key is the full resources path
     */
    protected HashMap<String, File> resources = new HashMap<>();

    /**
     * The resource temp folder
     */
    protected File tmpFolder = null;

    /**
     * Holds the jar manifest (if exists)
     * <p>
     *
     * The key is the jar file name, the value the list of manifes entries<p>
     */
    protected HashMap<String, Manifest> manifests = new HashMap<String, Manifest>();

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
        //--- Create the temp dir for resources dumps
        tmpFolder = new File(System.getProperty("java.io.tmpdir"), "jarResources" + File.separator + "" + System.getProperty("user.name"));
        tmpFolder.mkdirs();
        // System.out.println("(M) Temp folder for resource is " + tmpFolder.getPath());

    }

    //***************************************************************************
    //*** API
    //***************************************************************************
    public static void stackJarFiles(ArrayList<File> v, File f) {
        if (f.isDirectory()) {
            File files[] = f.listFiles();
            for (int i = 0;i < files.length;i++) stackJarFiles(v, files[i]);

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

        for (int i = 0;i < v.size();i++) {
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
     * In memory jar file
     * 
     * @param path The file path for the passed image
     * @param image The jar image
     */
    public void addJar(String path, byte image[]) {
        try {
            //--- Analyse jar and find the manifest
            ByteArrayInputStream bin = new ByteArrayInputStream(image);
            JarInputStream jin = new JarInputStream(bin);
            Manifest manifest = jin.getManifest();
            if (manifest != null) manifests.put(path, manifest);
            while (true) {
                JarEntry jarentry = jin.getNextJarEntry();
                if (jarentry == null) break;
                // System.out.println("JarEntry:"+jarentry.getName());

                if (jarentry.getName().equals("META-INF/MANIFEST.MF")) {
                    //--- If found, do not handle

                } else {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    byte buffer[] = new byte[1024];
                    while (true) {
                        int readed = jin.read(buffer);
                        if (readed == -1) break;
                        bout.write(buffer, 0, readed);
                    }
                    buffer = null;

                    //--- Store
                    // if (jarentry.getName().startsWith("/lsimdia/knop/clients/Resources/Icons")) System.out.println("Resource:"+jarentry.getName());
                    if (jarentry.getName().endsWith(".class")) {
                        // System.out.println(">>> class   :" + jarentry.getName());
                        memory.put(jarentry.getName(), bout.toByteArray());

                    } else if (jarentry.getName().endsWith("/")) {
                        //--- It's a folder, do nothing

                    } else {
                        // System.out.println(">>> resource:" + jarentry.getName());
                        //--- Dump to file system
                        File f = File.createTempFile("res", ".bin", tmpFolder);
                        FileOutputStream fout = new FileOutputStream(f);
                        fout.write(bout.toByteArray());
                        fout.close();
                        f.deleteOnExit();

                        //--- Store the reference
                        resources.put(jarentry.getName(), f);
                    }
                    bout.close();

                }
            }
            bin.close();
            jin.close();

        } catch (MalformedURLException ex) {
            System.err.println("(E) jarClassLoader.addJar:" + ex.getMessage());

        } catch (IOException ex) {
            System.err.println("(E) jarClassLoader.addJar:" + ex.getMessage());

        }

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

    //**************************************************************************
    //*** Class loader
    //**************************************************************************
    @Override
    public URL getResource(String name) {
        System.out.println("Searching:" + name);
        try {
            File f = resources.get(name);
            if (f != null) return f.toURI().toURL();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        }
        return super.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        try {
            File f = resources.get(name);
            if (f != null) return new FileInputStream(f);

        } catch (Exception ex) {
            ex.printStackTrace();

        }

        // URL urls[] = getURLs();
        // for (int i = 0;i < urls.length;i++) System.out.println("URL[" + i + "]=" + urls[i]);
        return super.getResourceAsStream(name);

    }

    @Override
    public Class findClass(String classname) throws ClassNotFoundException {
        //--- Find in manual creation
        Class cl = classes.get(classname);
        if (cl != null) return cl;

        //--- Find in memory
        byte buffer[] = memory.remove(classname.replace('.', '/') + ".class");
        if (buffer != null) {
            cl = defineClass(null, buffer, 0, buffer.length);
            classes.put(cl.getName(), cl);
            return cl;
        }

        //--- Call super class
        cl = super.findClass(classname);
        // System.out.println(">>>> Looking for "+classname+" FOUND:"+cl);
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
            File test = new File("lib/ext/dummy.jar");
            byte b[] = new byte[(int) test.length()];
            FileInputStream fin = new FileInputStream(test);
            int read = fin.read(b);
            System.out.println("Loaded " + read + " bytes");

            System.out.println("PARENT:" + Class.forName("org.tinyrcp.JarClassLoader").getClassLoader());
            JarClassLoader loader = new JarClassLoader(new URL[0]);
            // loader.addJar("lib/ext");
            loader.addJar(test.getPath(), b);

            // System.out.println("(M) New java.protocol.handler.pkgs:" + System.getProperty("java.protocol.handler.pkgs"));
            // loader.addJar("dist");
            Class cl = loader.loadClass("org.tinyrcp.example.dummy.JDummyFactory", true);
            System.out.println("Class:" + cl);
            System.out.println("Loader:" + cl.getClassLoader());

            URL url = loader.getResource("org/tinyrcp/example/dummy/Resources/Icons/22x22/dummy.png");
            System.out.println("Icon :" + url);
            InputStream st = loader.getResourceAsStream("org/tinyrcp/example/dummy/Resources/Icons/22x22/dummy.png");
            System.out.println("Stream:" + st);
            st.close();
            Thread.sleep(30000);

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

}
