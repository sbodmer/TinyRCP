# TinyRCP
Small GUI Java framework to create simple Swing visual interfaces.

## Architecture
The framework is modular and plugin based.

Each component is stored in a jar file with a specific manifest entry.

The components are recursively loaded from file system in the default folders
during the app creation

    {project}/lib/ext

## Features
The built in components are

* Containers (tabs, desktop, split, grid)

## Release
Work in progress...

## Building
The project is a Netbeans project, for manual compiling, use the ant build.xml

    cd {project}
    ant jar

To run the example application

    cd dist
    java -jar ExampleApp.jar

# Developers guide
The components and main application frame are all stored in jar archives as plugins
in a defined folder on your file system.

The framework will load dynamically the plugins at the boot process

Check the example application files for more informations

    org/tinyrcp/example
    app/

## Components
Each component must be composed of

- A factory
- A plugin
- A jar containing the classes, libraries and resources of the component

Each component is attached to a 

- Category
- Family

### Factories
The factory is the entry point of you component/plugin.
Each factory will be instantiated when the application is started
 
The factory must implement the interface

    org.tinyrcp.TinyFactory

Here are some description of the TinyFactory interface

#### public String getFactoryCategory();
Determine the category of the plugin (gui, ...)
    
    PLUGIN_CATEGORY_PANEL
        For visual components


#### public String getFactoryFamily();
Determine the family within the category

    PLUGIN_FAMILY_CONTAINER
        Visual panel containers (contains other component)

    PLUGIN_FAMILY_PANEL
        A basic visual panel

#### public TinyPlugin newPlugin(Object argument);
Return the new plugin instance
    
    The passed argument can be specific for each factory

### Plugin
Each factory will produce a plugin instance when needed

## Jar
To add a plugin simply create a jar files in the /lib/ext folder with the manifest entry

    Tiny-Factory: {full qualified class name of the factory}

The class must implement the interface below

    org.tinyrcp.TinyFactory


## Application boot
The entry point of your app should be loaded by the default class loader (for
example the Java Web Start class loader) and stored in a separated jar which
defines the common manifest entry

    Main-Class: app.ExampleApp
    Class-Path: TinyRCP.jar

The framework will start the main entry point and do some initialization and
finally will start the main application frame

The boot process consist of

* Create a custom classloader
* Load all the custom plugins stored in a defined folder
* Create a splash screen for user feedback
* The framework boot method is called
* The boot process will call the main() method of the app frame

Here is an example of your main entry point (which is loaded by the system
class loader)

<code>
    public class ExampleApp {

        /**
         * Main entry point for the boot process
         *
         * @param args the command line arguments
         */
        public static void main(final String args[]) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    //--- Prepare the loader plugin path to use
                    String libs[] = {
                        "lib"+File.separator+"ext",
                        System.getProperty("user.home")+File.separator+".ExampleApp"

                    };

                    //--- Prepare default boot splash frame for user feedback
                    JAppLauncher japp = new JAppLauncher(args);
                    japp.setVisible(true);

                    //--- Start boot process
                    //--- When the boot has finished, the splash screen should
                    //--- close itself and the main app frame will be opened
                    AppLauncher.boot(japp, libs, args, "ExampleApp");
                }
            });

        }
    }
</code>

## Main application frame
The main application frame to start is defined in the manifest entry

    Tiny-Main-Class

The stored jar will be loaded by the tinyrcp custom class loader and then the
main method of the class will be called with all the passed arguments from the
boot process