package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;

import java.io.IOException;

/**
 * This is the barebone test example that tests subsystem
 * It does same things that {@link JCoSubsystemParsingTestCase} does but most of internals are already done in AbstractSubsystemBaseTest
 * If you need more control over what happens in tests look at  {@link JCoSubsystemParsingTestCase}
 *
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a>
 */
public class SubsystemBaseParsingTestCase extends AbstractSubsystemBaseTest {

    public SubsystemBaseParsingTestCase() {
        super(JCoSubsystemExtension.SUBSYSTEM_NAME, new JCoSubsystemExtension());
    }


    @Override
    protected String getSubsystemXml() throws IOException {
        return "<subsystem xmlns=\"" + Namespace.CURRENT.getUriString() + "\">" +
                "<jco-destinations>" +
                JCoSubsystemXmlFactory.createJCoDestination("java:jboss/jco-destinations/ExampleDestination", "100", "00",
                        "ExampleDestination", "user", "geheim", "apphost.your-organisation.intern") +
                "</jco-destinations>" +
                "</subsystem>";
    }

    protected String getSubsystemXsdPath() throws Exception {
        return "schema/jco-destinations.xsd";
    }
}
