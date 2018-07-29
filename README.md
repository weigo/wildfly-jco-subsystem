# Wildfly JCo destination subsystem

The subsystem provides integration for the SAP JCo runtime into Wildfly.

## Prerequisites

The Widlfy JCo destination subsystem requires
 
* at minimum a Wildfly _12.0.0.Final_ installation.
* the SAP JCo connector JAR and runtime libraries must be obtained separately (you will need an S-User account at the download site).

## Building
Install the `sapjco3.jar` from your JCo connector download into your local Maven repository.

### Maven command line

See the [Maven reference](https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html) for further explanations:

```
mvn deploy:deploy-file -DgroupId=sap.jco.conn \
  -DartifactId=jco3 \
  -Dversion=3.0.17 \
  -Dpackaging=jar \
  -Dfile=sapjco3.jar \
  -DrepositoryId=<id-to-map-on-server-section-of-settings.xml> \
  -Durl=<url-of-the-repository-to-deploy>
```

The version must map to the dependency defined in `pom.xml` and should be updated appropriately (in pom.xml) to match your download.
This is the artifact specification you'll use later when developing your application.

Sonatype Nexus sports an upload ui. So you can upload the JAR file there.
 
Execute `maven` and copy the result to your wildfly installation:

```
export WILDFLY_HOME=/opt/wildfly-12.0.0.Final
mvn clean package && cp -av target/module/* $WILDFLY_HOME/modules/system/layers/base/
```

In the folder `$WILDFLY_HOME/modules/system/layers/base/org/arachna/jco-destinations/main` create a subfolder `lib`.
Add subfolders wrt. to your server architecture:

| Architecture | Subfolder |
| --- | --- |
| Windows | win-x86_64 |
| Linux | linux-x86_64 |

The reference document wrt. to supporting native libraries in Wildfly modules can be found in the
[JBoss modules manual, subsection native libraries](https://jboss-modules.github.io/jboss-modules/manual/#native-libraries).

Copy the `sapjco3.jar` from your JCo connector download to `$WILDFLY_HOME/modules/system/layers/base/org/arachna/jco-destinations/main`.

## Installation/Configuration
* In `standalone.xml` add the `org.arachna.jco-destinations` module to the exstensions list to register the extension with your wildfly installation:

```
<server xmlns="urn:jboss:domain:6.0">
    <extensions>
    ...
       <extension module="org.arachna.jco-destinations"/>
    </extensions>
```

* Add the subsystem configuration and required JCo destination descriptors to `standalone.xml`:

```
<subsystem xmlns="urn:org.arachna.jboss:destination-service:1.0">
    <jco-destinations>
        <jco-destination destination="ExampleDestination"
                         jndi-name="java:jboss/jco-destinations/ExampleDestination"
                         sys-nr="00"
                         client="100">
            <security>
                <user-name>user</user-name>
                <password>secret</password>
             </security>
             <as-host>your.app-host.intern</as-host>
             <language>de</language>
        </jco-destination>
     </jco-destinations>
</subsystem>
```

## Example application

According to [Class loading in Wildfly]()https://docs.jboss.org/author/display/WFLY10/Class+Loading+in+WildFly) you can
make the modules classes available to application via a deployment descriptor, so:

* Create an example test application, f.e. using the `com.airhacks` maven archetype:

```
mvn archetype:generate -Dfilter=com.airhacks:javaee7-essentials-archetype
```
  
* Add a `jboss-deployment-structure.xml` to your web applications `WEB-INF`-folder:

```$xml
<?xml version="1.0"?>

<jboss-deployment-structure>
	<deployment>
		<dependencies>
			<module name="org.arachna.jco-destinations"/>
		</dependencies>
	</deployment>
</jboss-deployment-structure>
``` 

### Spoiler alert

The above should be fixed to make the JCo3 library separatly as a module in Wildfly available which should be referenced
by both the JCo destination subsystem and Web applications.

* Add a REST ressource to the project:

```
@Path("/jcotest")
public class JCoTestResrource {

    @Resource(name = "java:jboss/jco-destinations/ExampleDestination")
    private JCoDestination destination;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getDestinationProperties() throws NamingException {
        return String.format("{destination: '%s'}", this.destination.getDestinationName());
    }
}
```

* In `JAXRSConfiguration` register the JAX-RS ressource handler:

```
@ApplicationPath("resources")
public class JAXRSConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JCoTestResource.class);
        return classes;
    }
}
```

* Build and deploy:

```
mvn clean package && cp target/jaxrs-jco-test.war $WILDFLY_HOME/standalone/deployments
```

* and call the RESt service from the browser: `http://localhost:8080/jaxrs-jco-test/resources/jcotest`

The result should list your JCo destination name as you configured it in `standalone.xml`

```
{destination: 'ExampleDestination'}
```