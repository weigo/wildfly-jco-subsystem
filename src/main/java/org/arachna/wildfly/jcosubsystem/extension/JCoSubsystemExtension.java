package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.*;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.Arrays;
import java.util.List;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.*;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoContent;


/**
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class JCoSubsystemExtension implements Extension {

    /**
     * The name of our subsystem within the model.
     */
    public static final String SUBSYSTEM_NAME = "jco-destinations";
    protected static final PathElement SUBSYSTEM_PATH = PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME);
    private static final String RESOURCE_NAME = JCoSubsystemExtension.class.getPackage().getName() + ".LocalDescriptions";
    /**
     * The parser used for parsing our subsystem
     */
    private final JCoSubsystemParser parser = new JCoSubsystemParser();

    static StandardResourceDescriptionResolver getResourceDescriptionResolver(final String... keyPrefix) {
        StringBuilder prefix = new StringBuilder(SUBSYSTEM_NAME);
        for (String kp : keyPrefix) {
            prefix.append('.').append(kp);
        }

        return new StandardResourceDescriptionResolver(prefix.toString(), RESOURCE_NAME, JCoSubsystemExtension.class.getClassLoader(), true, false);
    }

    @Override
    public void initializeParsers(ExtensionParsingContext context) {
        context.setSubsystemXmlMapping(SUBSYSTEM_NAME, Namespace.CURRENT.getUriString(), parser);
    }


    @Override
    public void initialize(ExtensionContext context) {
        final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME, ModelVersion.create(1, 0, 0));
        final ManagementResourceRegistration registration = subsystem.registerSubsystemModel(JCoSubsystemDefinition.INSTANCE);
        registration.registerOperationHandler(GenericSubsystemDescribeHandler.DEFINITION, GenericSubsystemDescribeHandler.INSTANCE, false);

        subsystem.registerXMLElementWriter(parser);
    }

    /**
     * The subsystem parser, which uses stax to read and write to and from xml
     */
    private static class JCoSubsystemParser implements XMLStreamConstants, XMLElementReader<List<ModelNode>>, XMLElementWriter<SubsystemMarshallingContext> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeContent(XMLExtendedStreamWriter writer, SubsystemMarshallingContext context) throws XMLStreamException {
            context.startSubsystemElement(Namespace.CURRENT.getUriString(), false);
            writer.writeStartElement(Element.JCO_DESTINATIONS.getLocalName());

            ModelNode node = context.getModelNode();

            if (node.hasDefined(JCoDestinationDescriptor.Tag.JCO_DESTINATION.getLocalName())) {
                writeJCoDestinations(writer, node.get(JCoDestinationDescriptor.Tag.JCO_DESTINATION.getLocalName()));
            }
            // end jco-destinations
            writer.writeEndElement();

            // end subsystem
            writer.writeEndElement();
        }

        private void writeJCoDestinations(XMLExtendedStreamWriter writer, ModelNode jcoDestinations) throws XMLStreamException {
            for (String destinationName : jcoDestinations.keys()) {
                ModelNode model = jcoDestinations.get(destinationName);
                writer.writeStartElement(JCoDestinationDescriptor.Tag.JCO_DESTINATION.getLocalName());

                Constants.DESTINATION.marshallAsAttribute(model, writer);
                Constants.JNDI_NAME.marshallAsAttribute(model, writer);
                Constants.SYSNR.marshallAsAttribute(model, writer);
                Constants.CLIENT.marshallAsAttribute(model, writer);

                // user name, password is optional using SNC
                if (model.has(Constants.USER_NAME.getName()) && model.has(Constants.PASSWORD.getName())) {
                    writer.writeStartElement(JCoDestinationDescriptor.Tag.SECURITY.getLocalName());
                    Constants.USER_NAME.marshallAsElement(model, writer);
                    Constants.PASSWORD.marshallAsElement(model, writer);
                    // end security
                    writer.writeEndElement();
                }

                Constants.ASHOST.marshallAsElement(model, writer);

                for (SimpleAttributeDefinition definition : Arrays.asList(Constants.GROUP, Constants.LANG)) {
                    if (model.has(definition.getName())) {
                        definition.marshallAsElement(model, writer);
                    }
                }

                // end jco-destination
                writer.writeEndElement();

            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
            final ModelNode address = new ModelNode();
            address.add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
            address.protect();

            final ModelNode subsystem = new ModelNode();
            subsystem.get(OP).set(ADD);
            subsystem.get(OP_ADDR).set(address);

            list.add(subsystem);

            try {
                Element element = Element.forName(reader.getLocalName());

                switch (element) {
                    case SUBSYSTEM: {
                        final JCoDestinationParser parser = new JCoDestinationParser();
                        parser.parse(reader, list, address);
                        requireNoContent(reader);
                        break;
                    }
                }
            } catch (Exception e) {
                throw new XMLStreamException(e);
            }
        }
    }
}
