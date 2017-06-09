# TinyRCP
Small GUI Java framework to create simple visual interfaces

## Architecture
The framework is complelty modular and plugin based.

Each component is stored in a jar file with a specific manifest entry

The components are recursively loaded from file system in the default folders
during the app creation

    {cwd}/lib/ext

## Release
Work in progress, alpha quality for the moment...

## Features
TODO

# Components
The built in components are

- Containers (tabs, desktop)
- ... 


# Plugins
To add a plugin simply create a jar files in the /lib/ext folder with the manifest entry

    Tiny-Factory: {full qualified class name of the factory

The class must implement the interface below

    org.tinyrcp.TinyFactory