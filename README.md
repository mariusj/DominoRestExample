# Installation

- install Domino & Notes client
- on the Domino server create the `UpdateSite.nsf` database based on the advanced template `EclipseUpdateSite`
- in the `notes.ini` for the Domino server add following line:

      OSGI_HTTP_DYNAMIC_BUNDLES=UpdateSite.nsf

- install Eclipse
- in the Eclipse install this plugin: [XPages SDK for Eclipse RCP](https://www.openntf.org/main.nsf/project.xsp?r=project/XPages%20SDK%20for%20Eclipse%20RCP)
- configure this plugin in the Eclipse (Windows/Preferences/XPages SDK). Enable the option to automatically create the JRE.
- configure the Target Platform:
  - enter Window/Preferences/Plug-in Development/Target Platform
  - click `Add...` to add a new platform
  - select `Nothing. Start with empty...`
  - name it for example `Domino`
  - in the locations tab add directories where Domino folders are located:
	  - `osgi\rcp\Eclipse`
	  - `osgi\shared\Eclipse`
  - make the `Domino` Target Platform active by selecting a checkbox near it


# Creating new plugins in the Eclipse IDE

Steps bellow are for creating new plugins from the scratch. These are given for reference only and are not required for importing existing plugins.

- create a new project of type `Plug-in Project`
- change the JRE used by the project to the one used by Domino:
	- right click on the project, select `Properties`
	- enter `Java Build Path`, select `JRE System Library`
	- click `Edit`, select `Alternate JRE` and choose the Domino or Notes JRE
- add required dependencies in the `plugin.xml` on the `Dependencies` tab:
	- `com.ibm.wink` for REST api
	- `com.ibm.domino.osgi.core` for `ContextInfo` class
- code the REST API classes
- configure the REST application - add the extension point, sample  `plugin.xml` is provided bellow:

      <?xml version="1.0" encoding="UTF-8"?>
      <?eclipse version="3.4"?>
      <plugin>
        <extension point="com.ibm.domino.das.service">
          <serviceResources
            class="example.rest.app.RESTApp"
            name="example.rest"
            path="example"
            version="0.0.1">
          </serviceResources>
        </extension>
      </plugin>

	- `class` - name of the class with the application
	- `name` - name of the API as configured in Domino
	- `path` - a path to the API
- create a new project of type `Feature Project`
- add all required plugins to this feature
- create a new project of type `Update Site Project`
- create a new Category and add a feature created earlier
- build an update site by using `Build All` button

# Importing sample plugins into the Eclipse IDE

The steps here are for importing sample plugins provided by me.
- in the Eclipse select File/Import/General/Existing projects into Workspace
- make sure there are no compilation errors
- open the `example.rest.updatesite` project
- open `site.xml`
- click `Build All` to package the plugins and feature into Update Site

# Enabling the API on the Domino server
- open server configuration in Domino Administrator client
- on the `Basics` tab make sure that the option `Load Internet configuration from Server/Internet sites documents` is set to `enabled`
- select the site from the `Web/Internet sites` tree
- select the `Configuration` tab
- click on the `Enabled services`
- enter `example.rest`in the `New keyword` section and click OK. This name is taken from the `plugin.xml` in the `example.rest.plugin`.
- restart the Domino

# Installing a new version of plugins into Domino

- open the `UpdateSite.nsf` database in Notes client
- click `Import Local Update Site` and select the `site.xml` file that is located in the `example.rest.updatesite` folder
- invoke `'restart task http` in Domino console
- make sure the Domino server prints to the console message `NSF Based plugins are being installed in the OSGi runtime`. That means that the update site is installed correctly.
- to check if plugin is installed invoke: `tell http osgi ss example`, where `example` is the part of the plugin name
- invoke sample endpoints:
	- http://localhost/api/example/
	- http://localhost/api/example/endpoint_a
	- http://localhost/api/example/endpoint_b
	- http://localhost/names.nsf/api/example/endpoint_b


