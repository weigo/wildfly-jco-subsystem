# Wildfly JCo destination subsystem

The subsystem provides integration for the SAP JCo runtime into Wildfly.

## Prerequisites

The Widlfy JCo destination subsystem requires
 
* at minimum a Wildfly _12.0.0.Final_ installation.
* the SAP JCo connector JAR and runtime libraries must be obtained separately (you will need an S-User account at the download site).

## Building
Install the `sapjco3.jar` from your [JCo connector download](http://service.sap.com/connectors) into your local Maven repository.

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

## SAP JCo module

Install the SAP JCo3 module into your Wildfly installation using the shell script in the `installation` folder: 

* Use the switch `-j` to denote the download folder of your SAP JCo3 connector, e.g. ~/Downloads/JCo3.
* Use the switch `-t` to denote the folder where your Wildfly installation resides, e.g. the $WILDFLY_HOME environment variable in the section above.

```
weigo@eileanne:~/workspace/destination-service-subsystem/install$ ./install-sapjco-module.sh -j ~/tmp/sap-jco3 -t ~/tmp/wildfly-12.0.0.Final
Installing module base...
'modules/system/layers/base/com/sap' -> '/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap'
'modules/system/layers/base/com/sap/conn' -> '/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn'
'modules/system/layers/base/com/sap/conn/main' -> '/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main'
'modules/system/layers/base/com/sap/conn/main/module.xml' -> '/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main/module.xml'
Installing sapjco3.jar into module...
'/home/weigo/tmp/sap-jco3/sapjco3.jar' -> '/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main/sapjco3.jar'
Installing libsapjco3.so into module...
'/home/weigo/tmp/sap-jco3/libsapjco3.so' -> '/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main/lib/linux-x86_64/libsapjco3.so'
Installed SAP JCo3 module into /home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main:
/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main:
insgesamt 1452
drwxr-xr-x 1 weigo weigo      24 Aug  2 23:29 lib
-rw-r--r-- 1 weigo weigo     220 Aug  2 22:34 module.xml
-rwxr-xr-x 1 weigo weigo 1479890 Jul 17  2017 sapjco3.jar

/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main/lib:
insgesamt 0
drwxr-xr-x 1 weigo weigo 26 Aug  2 23:29 linux-x86_64

/home/weigo/tmp/wildfly-12.0.0.Final/modules/system/layers/base/com/sap/conn/main/lib/linux-x86_64:
insgesamt 5260
-rwxr-xr-x 1 weigo weigo 5383939 Jul 17  2017 libsapjco3.so
```

## Installation/Configuration

* In `standalone.xml` add the `org.arachna.jco-destinations` module to the extensions list to register the extension with your wildfly installation:

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

According to [Class loading in Wildfly](https://docs.jboss.org/author/display/WFLY10/Class+Loading+in+WildFly) you can
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
			<module name="com.sap.conn"/>
		</dependencies>
	</deployment>
</jboss-deployment-structure>
``` 

This will make the classes from the SAP JCo connector available to your application.
 
* Add a REST ressource to the project:

```
@Path("/jcotest")
public class JCoTestRessource {

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

* and call the REST service from the browser: `http://localhost:8080/jaxrs-jco-test/resources/jcotest`

The result should list your JCo destination name as you configured it in `standalone.xml`

```
{destination: 'ExampleDestination'}
```
